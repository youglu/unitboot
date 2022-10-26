package org.union.sbp.springbase.adaptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.union.sbp.springbase.utils.SpringSwaggerUtil;
import org.union.sbp.springbase.utils.SpringContollerUtil;
import org.union.sbp.springbase.utils.UnitLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * spring子单元controller适配器.
 * @author youg
 * @since jdk1.8
 */
public class ControllerAdaptor {
    private final static Logger log = LoggerFactory.getLogger(ControllerAdaptor.class);
    /**
     * swagger controller开头的包前缀，子单元的swagger的controller不需要加到主context中。
     */
    private final static String SWAGGER_CONTROLLER_PACKAGE_PREFIX = "springfox.documentation.swagger";
    /**
     * 注册子context的conoller bean到主context。
     * @param unitApplicationContext
     * @throws Exception
     */
    public static void addUnitController(final AnnotationConfigApplicationContext unitApplicationContext){
        long startTime = System.currentTimeMillis();
        final Map<String, Object> controllerMap = unitApplicationContext.getBeansWithAnnotation(RestController.class);
        if (null == controllerMap || controllerMap.isEmpty()) {
            return;
        }
        for(Map.Entry<String,Object> entry:controllerMap.entrySet()){
            //排除swagger相关controller
            if(entry.getValue().getClass().getName().startsWith(SWAGGER_CONTROLLER_PACKAGE_PREFIX)){
                continue;
            }
            SpringContollerUtil.registerControllerWithBean(entry.getValue());
        }
        UnitLogger.logUserTimes(log,startTime,"完成controller适配");
        startTime = System.currentTimeMillis();
        // 添加controller到主context后，重新扫描下swagger
        try {
            SwaggerAdaptor.addUnitSwagger(unitApplicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UnitLogger.logUserTimes(log,startTime,"完成swagger适配");
    }
    /**
     * 从主context解除子context注册的所有controller.
     * @param unitApplicationContext
     * @throws Exception
     */
    public static void removeUnitController(final AnnotationConfigApplicationContext unitApplicationContext){

    }
}
