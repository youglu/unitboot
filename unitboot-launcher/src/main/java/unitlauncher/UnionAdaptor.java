package unitlauncher;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import unitlauncher.adaptor.AllAccessHook;

import java.lang.reflect.Field;

/**
 * 自定义OSGI框架适配器，用于提供自定义类加载器的HOOK。
 * @author youg
 * @since JDK1.8
 */
public class UnionAdaptor extends org.eclipse.osgi.baseadaptor.BaseAdaptor implements FrameworkListener {
    /**
     * 默认构造器.
     * @param args
     */
    public UnionAdaptor(String[] args) {
        super(args);
        addAccessAllClassHook();
    }

    /**
     * 重写framework启动方法
     * @param fwContext
     * @throws BundleException
     */
    public void frameworkStart(BundleContext fwContext) throws BundleException {
        super.frameworkStart(fwContext);
        fwContext.addFrameworkListener(this);
    }

    /**
     * 强制更新HOOK注册器的只读状态为false,默认初始化完后会设置为true，导致在增加hook时报只读异常.
     */
    private void addAccessAllClassHook(){
        try {
            final Field readonlyField = getHookRegistry().getClass().getDeclaredField("readonly");
            readonlyField.setAccessible(true);
            readonlyField.setBoolean(getHookRegistry(), false);
            getHookRegistry().addClassLoaderDelegateHook(new AllAccessHook(this));
            readonlyField.setBoolean(getHookRegistry(), true);
            readonlyField.setAccessible(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        System.out.println(frameworkEvent.getBundle());
        System.out.println(frameworkEvent.getThrowable());
        if(null != frameworkEvent.getThrowable()){
            frameworkEvent.getThrowable().printStackTrace();
        }
        System.out.println(frameworkEvent);
    }
}
