package unitlauncher;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;

import java.io.File;
import java.net.URL;
import java.util.Properties;

public class UnitLauncher {

    public static void main(String[] args) throws Exception {
        args = new String[]{"-console","-dev","-debug"};
        Properties properties = new Properties();
        properties.load(UnitLauncher.class.getResourceAsStream("/config.properties"));
        String osName = System.getProperty("os.name").toLowerCase();
        osName = osName.split(" ")[0];
        String configdir = properties.getProperty("runtime.path."+osName);

        System.setProperty("osgi.configuration.area",configdir);
        // System.setProperty("osgi.instance.area",configdir);

        Debug.DEBUG_BUNDLE_TIME = true;
        Debug.DEBUG_GENERAL = true;
        Debug.DEBUG_BUNDLE_TIME=true;
        Debug.DEBUG_STARTLEVEL = true;
        // Debug.DEBUG_LOADER = true;

        String defaultExeecuteEnv = "OSGi/Minimum-1.0,OSGi/Minimum-1.1,OSGi/Minimum-1.2,J2SE-1.0,J2SE-1.1,J2SE-1.2,J2SE-1.3,J2SE-1.4,J2SE-1.5,JavaSE-1.5,JavaSE-1.6";
        FrameworkProperties.setProperty("org.osgi.framework.executionenvironment", defaultExeecuteEnv);
        FrameworkProperties.setProperty("osgi.clean", "true");
        FrameworkProperties.setProperty("osgi.adaptor", UnionAdaptor.class.getName());
        EclipseStarter.main(args);
    }
}
