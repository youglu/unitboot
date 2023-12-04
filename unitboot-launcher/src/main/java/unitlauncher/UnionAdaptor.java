package unitlauncher;

import org.eclipse.osgi.internal.module.ResolverImpl;
import org.eclipse.osgi.internal.resolver.StateImpl;
import org.eclipse.osgi.internal.resolver.SystemState;
import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unitlauncher.adaptor.AllAccessHook; 
import unitlauncher.adaptor.UnitClasspathLoaderClassHook;

import java.lang.reflect.Field;

/**
 * 自定义OSGI框架适配器，用于提供自定义类加载器的HOOK。
 * @author youg
 * @since JDK1.8
 */
public class UnionAdaptor extends org.eclipse.osgi.baseadaptor.BaseAdaptor implements FrameworkListener {
    /**
     * 日志.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());


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
            getHookRegistry().addClassLoadingStatsHook(new UnitClasspathLoaderClassHook());

            readonlyField.setBoolean(getHookRegistry(), true);
            readonlyField.setAccessible(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 框架事件处理
     * @param frameworkEvent
     */
    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        if(null != frameworkEvent.getThrowable()){
            frameworkEvent.getThrowable().printStackTrace();
        }
    }
    /**
     * @Description: 获得状态实现处理器,方法返回状态实现器的包装类，用于重写一些
     * 方法进行额外的处理.
     *
     * @author youg
     * @since jdk1.8
     */
    public synchronized State getState() {
       State state = super.getState();
       //if(null == uniStateImplDecorator){
       // UniStateImplDecorator uniStateImplDecorator = new UniStateImplDecorator((SystemState)  state);
      // }
       return state ;
    }

}
