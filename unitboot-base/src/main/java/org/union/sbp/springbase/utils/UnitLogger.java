package org.union.sbp.springbase.utils;

import org.slf4j.Logger;

/**
 * 单元日志打印类，用于包装日志对象，在打印时判断是否开启当前级别打印.
 */
public class UnitLogger {
    /**
     * INFO打印.
     * @param log
     * @param msg
     * @param msgParams
     */
    public static void info(Logger log, String msg, Object...msgParams){
        if(log.isInfoEnabled()) {
            log.info(msg, msgParams);
        }
    }
    public static void logUserTimes(Logger log,long startTime,String msg){
        log.info(msg+" 耗时"+((System.currentTimeMillis() - startTime))+"毫秒");
    }
}
