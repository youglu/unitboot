package unitlauncher.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * 单元日志打印类.
 *
 * @author youglu
 * @date
 */
public class UnitLog extends PrintStream {

    private static final Logger log = LoggerFactory.getLogger(UnitLog.class);

    public UnitLog(File file) throws FileNotFoundException {
        super(file);
    }

    public UnitLog(PrintStream out) {
        super(out);
    }

    public static void info(final String msg,Object...params){
        if(log.isInfoEnabled()){
            log.info(msg,params);
        }
    }
    public  void print(final Object msg){
        info(msg != null?String.valueOf(msg):"");
    }

}
