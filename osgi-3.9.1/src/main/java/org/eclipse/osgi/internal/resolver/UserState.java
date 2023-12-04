/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Rob Harrop - SpringSource Inc. (bug 247522)
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import java.util.*;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleException;

/**
 * This implementation of State does a bookkeeping of all added/removed 
 */
public class UserState extends StateImpl {
	// TODO this is not an accurate way to record updates
	private final Set<String> updated = Collections.synchronizedSet(new HashSet<String>());

	public boolean removeBundle(BundleDescription description) {
		if (description.getLocation() != null)
			updated.remove(description.getLocation());
		if (!super.removeBundle(description))
			return false;
		return true;
	}

	public boolean updateBundle(BundleDescription newDescription) {
		if (!super.updateBundle(newDescription))
			return false;
		updated.add(newDescription.getLocation());
		return true;
	}

	/**
	 * @throws BundleException  
	 */
	public StateDelta compare(State baseState) throws BundleException {
		BundleDescription[] current = this.getBundles();
		StateDeltaImpl delta = new StateDeltaImpl(this);
		// process additions and updates
		for (int i = 0; i < current.length; i++) {
			BundleDescription existing = baseState.getBundleByLocation(current[i].getLocation());
			if (existing == null)
				delta.recordBundleAdded((BundleDescriptionImpl) current[i]);
			else if (updated.contains(current[i].getLocation()))
				delta.recordBundleUpdated((BundleDescriptionImpl) current[i]);
		}
		// process removals
		BundleDescription[] existing = baseState.getBundles();
		for (int i = 0; i < existing.length; i++) {
			BundleDescription local = getBundleByLocation(existing[i].getLocation());
			if (local == null)
				delta.recordBundleRemoved((BundleDescriptionImpl) existing[i]);
		}
		return delta;
	}
}
