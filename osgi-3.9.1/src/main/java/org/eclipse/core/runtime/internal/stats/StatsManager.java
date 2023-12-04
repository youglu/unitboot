/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.internal.stats;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.hooks.ClassLoadingStatsHook;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.eclipse.osgi.baseadaptor.loader.ClasspathManager;
import org.eclipse.osgi.framework.adaptor.BundleWatcher;
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.FrameworkDebugOptions;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;

public class StatsManager implements BundleWatcher, HookConfigurator, ClassLoadingStatsHook {
	// This connect bundles and their info, and so allows to access the info without running through
	// the bundle registry. This map only contains activated bundles. The key is the bundle Id
	private Hashtable<Long, BundleStats> bundles = new Hashtable<Long, BundleStats>(20);
	private Map<Thread, Stack<BundleStats>> activationStacks = new HashMap<Thread, Stack<BundleStats>>(5);
	private static boolean booting = true; // the state of the platform. This value is changed by the InternalPlatform itself.

	private static StatsManager defaultInstance;

	public static boolean MONITOR_ACTIVATION = false;
	public static boolean MONITOR_CLASSES = false;
	public static boolean MONITOR_RESOURCES = false;
	public static String TRACE_FILENAME = "runtime.traces"; //$NON-NLS-1$
	public static String TRACE_FILTERS = "trace.properties"; //$NON-NLS-1$
	public static boolean TRACE_CLASSES = false;
	public static boolean TRACE_BUNDLES = false;
	public static final String FRAMEWORK_SYMBOLICNAME = "org.eclipse.osgi"; //$NON-NLS-1$

	//Option names for spies
	private static final String OPTION_MONITOR_ACTIVATION = FRAMEWORK_SYMBOLICNAME + "/monitor/activation"; //$NON-NLS-1$
	private static final String OPTION_MONITOR_CLASSES = FRAMEWORK_SYMBOLICNAME + "/monitor/classes"; //$NON-NLS-1$
	private static final String OPTION_MONITOR_RESOURCES = FRAMEWORK_SYMBOLICNAME + "/monitor/resources"; //$NON-NLS-1$
	private static final String OPTION_TRACE_BUNDLES = FRAMEWORK_SYMBOLICNAME + "/trace/activation"; //$NON-NLS-1$
	private static final String OPTION_TRACE_CLASSES = FRAMEWORK_SYMBOLICNAME + "/trace/classLoading"; //$NON-NLS-1$
	private static final String OPTION_TRACE_FILENAME = FRAMEWORK_SYMBOLICNAME + "/trace/filename"; //$NON-NLS-1$
	private static final String OPTION_TRACE_FILTERS = FRAMEWORK_SYMBOLICNAME + "/trace/filters"; //$NON-NLS-1$

	static {
		setDebugOptions();
	}

	public static StatsManager getDefault() {
		if (defaultInstance == null) {
			defaultInstance = new StatsManager();
			defaultInstance.initialize();
		}
		return defaultInstance;
	}

	public static void setDebugOptions() {
		FrameworkDebugOptions options = FrameworkDebugOptions.getDefault();
		// may be null if debugging is not enabled
		if (options == null)
			return;
		MONITOR_ACTIVATION = options.getBooleanOption(OPTION_MONITOR_ACTIVATION, false);
		MONITOR_CLASSES = options.getBooleanOption(OPTION_MONITOR_CLASSES, false);
		MONITOR_RESOURCES = options.getBooleanOption(OPTION_MONITOR_RESOURCES, false);
		TRACE_CLASSES = options.getBooleanOption(OPTION_TRACE_CLASSES, false);
		TRACE_BUNDLES = options.getBooleanOption(OPTION_TRACE_BUNDLES, false);
		TRACE_FILENAME = options.getOption(OPTION_TRACE_FILENAME, TRACE_FILENAME);
		TRACE_FILTERS = options.getOption(OPTION_TRACE_FILTERS, TRACE_FILTERS);
	}

	public static void doneBooting() {
		booting = false;
	}

	public static boolean isBooting() {
		return booting;
	}

	/**
	 * Returns the result of converting a list of comma-separated tokens into an array
	 * 
	 * @return the array of string tokens
	 * @param prop the initial comma-separated string
	 */
	public static String[] getArrayFromList(String prop) {
		return ManifestElement.getArrayFromList(prop, ","); //$NON-NLS-1$
	}

	private void initialize() {
		// add the system bundle
		BundleStats bundle = findBundle(FrameworkAdaptor.FRAMEWORK_SYMBOLICNAME, 0);
		bundle.setTimestamp(System.currentTimeMillis());
		bundle.setActivationOrder(bundles.size());
		bundle.setDuringStartup(booting);
	}

	public void watchBundle(Bundle bundle, int type) {
		switch (type) {
			case BundleWatcher.START_ACTIVATION :
				startActivation(bundle);
				break;
			case BundleWatcher.END_ACTIVATION :
				endActivation(bundle);
				break;
		}
	}

	public void startActivation(Bundle bundle) {
		// should be called from a synchronized location to protect against concurrent updates
		BundleStats info = findBundle(bundle.getSymbolicName(), bundle.getBundleId());
		info.setTimestamp(System.currentTimeMillis());
		info.setActivationOrder(bundles.size());
		info.setDuringStartup(booting);

		Stack<BundleStats> activationStack = activationStacks.get(Thread.currentThread());
		if (activationStack == null) {
			activationStack = new Stack<BundleStats>();
			activationStacks.put(Thread.currentThread(), activationStack);
		}

		// set the parentage of activation
		if (activationStack.size() != 0) {
			BundleStats activatedBy = activationStack.peek();
			activatedBy.activated(info);
			info.setActivatedBy(activatedBy);
		}
		activationStack.push(info);

		if (TRACE_BUNDLES == true) {
			traceActivate(bundle, info);
		}
	}

	public void endActivation(Bundle symbolicName) {
		Stack<BundleStats> activationStack = activationStacks.get(Thread.currentThread());
		// should be called from a synchronized location to protect against concurrent updates
		BundleStats info = activationStack.pop();
		info.endActivation();
	}

	private void traceActivate(Bundle bundle, BundleStats info) {
		try {
			PrintWriter output = new PrintWriter(new FileOutputStream(ClassloaderStats.traceFile.getAbsolutePath(), true));
			try {
				long startPosition = ClassloaderStats.traceFile.length();
				output.println("Activating bundle: " + bundle.getSymbolicName()); //$NON-NLS-1$
				output.println("Bundle activation stack:"); //$NON-NLS-1$
				Stack<BundleStats> activationStack = activationStacks.get(Thread.currentThread());
				for (int i = activationStack.size() - 1; i >= 0; i--)
					output.println("\t" + activationStack.get(i).getSymbolicName()); //$NON-NLS-1$
				output.println("Class loading stack:"); //$NON-NLS-1$
				Stack<ClassStats> classStack = ClassloaderStats.getClassStack();
				for (int i = classStack.size() - 1; i >= 0; i--)
					output.println("\t" + classStack.get(i).getClassName()); //$NON-NLS-1$
				output.println("Stack trace:"); //$NON-NLS-1$
				new Throwable().printStackTrace(output);
				info.setTraceStart(startPosition);
			} finally {
				output.close();
				info.setTraceEnd(ClassloaderStats.traceFile.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BundleStats findBundle(String symbolicName, long id) {
		BundleStats result = bundles.get(new Long(id));
		try {
			if (result == null) {
				result = new BundleStats(symbolicName, id);
				bundles.put(new Long(id), result);
			}
		} catch (IllegalAccessError e) {
			e.printStackTrace();
		}
		return result;
	}

	public BundleStats[] getBundles() {
		return bundles.values().toArray(new BundleStats[bundles.size()]);
	}

	public BundleStats getBundle(long id) {
		return bundles.get(new Long(id));
	}

	/**
	 * @throws ClassNotFoundException  
	 */
	public void preFindLocalClass(String name, ClasspathManager manager) throws ClassNotFoundException {
		if (StatsManager.MONITOR_CLASSES) //Support for performance analysis
			ClassloaderStats.startLoadingClass(getClassloaderId(manager), name);
	}

	public void postFindLocalClass(String name, Class<?> clazz, ClasspathManager manager) {
		if (StatsManager.MONITOR_CLASSES)
			ClassloaderStats.endLoadingClass(getClassloaderId(manager), name, clazz != null);
	}

	public void preFindLocalResource(String name, ClasspathManager manager) {
		// do nothing
	}

	public void postFindLocalResource(String name, URL resource, ClasspathManager manager) {
		if (StatsManager.MONITOR_RESOURCES)
			if (resource != null && name.endsWith(".properties")) //$NON-NLS-1$
				ClassloaderStats.loadedBundle(getClassloaderId(manager), new ResourceBundleStats(getClassloaderId(manager), name, resource));
		return;
	}

	public void recordClassDefine(String name, Class<?> clazz, byte[] classbytes, ClasspathEntry classpathEntry, BundleEntry entry, ClasspathManager manager) {
		// do nothing
	}

	private String getClassloaderId(ClasspathManager loader) {
		return loader.getBaseData().getSymbolicName();
	}

	public void addHooks(HookRegistry hookRegistry) {
		if (Debug.MONITOR_ACTIVATION)
			hookRegistry.addWatcher(StatsManager.getDefault());
		if (StatsManager.MONITOR_CLASSES || StatsManager.MONITOR_RESOURCES)
			hookRegistry.addClassLoadingStatsHook(StatsManager.getDefault());
	}
}
