package unitlauncher;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;

import java.lang.reflect.Field;

public class UnitLauncher {

    public static void main(String[] args) throws Exception {
        args = new String[]{"-console","-dev","-debug"};
        String configdir = "D:\\soft\\hora\\unitor3.0_linux\\bin\\configuration";
        configdir = "F:\\soft\\hora\\unitboot-1.0\\bin\\configuration";

        System.setProperty("osgi.configuration.area",configdir);
        // System.setProperty("osgi.instance.area",configdir);

        Debug.DEBUG_BUNDLE_TIME = true;
        Debug.DEBUG_GENERAL = true;
        Debug.DEBUG_BUNDLE_TIME=true;
        Debug.DEBUG_STARTLEVEL = true;
        // Debug.DEBUG_LOADER = true;

        //Field[] fds = Debug.class.getDeclaredFields();
        //Arrays.stream(fds).filter(p->p.getType() == boolean.class).forEach(OSGILauncher::accept);

        String defaultExeecuteEnv = "OSGi/Minimum-1.0,OSGi/Minimum-1.1,OSGi/Minimum-1.2,J2SE-1.0,J2SE-1.1,J2SE-1.2,J2SE-1.3,J2SE-1.4,J2SE-1.5,JavaSE-1.5,JavaSE-1.6";

        FrameworkProperties.setProperty("org.osgi.framework.executionenvironment", defaultExeecuteEnv);
        FrameworkProperties.setProperty("osgi.clean", "true");
        FrameworkProperties.setProperty("osgi.adaptor", UnionAdaptor.class.getName());
        EclipseStarter.main(args);
    }
}
