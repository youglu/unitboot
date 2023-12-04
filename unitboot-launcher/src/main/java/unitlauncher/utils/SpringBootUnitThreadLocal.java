package unitlauncher.utils;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * 在springboot环境保存当前正在处理的springboot单元id处理类.
 *
 * @author youglu
 * @date
 */
public class SpringBootUnitThreadLocal{

    /**
     * 保存当前正在处理的springboot单元Id本地线程对象。
     */
    private final static ThreadLocal<Long> activeSpringbootUnitId = new ThreadLocal<Long>();
    /**
     * 日志。
     */
    private static final Logger log = LoggerFactory.getLogger(SpringBootUnitThreadLocal.class);

    public synchronized static void set(final long unitId){
        activeSpringbootUnitId.set(unitId);
    }
    public synchronized static long get(){
        return activeSpringbootUnitId.get();
    }
    public synchronized static void remove(){ activeSpringbootUnitId.remove();
    }
}
