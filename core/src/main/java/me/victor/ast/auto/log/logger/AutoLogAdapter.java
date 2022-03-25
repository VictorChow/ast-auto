package me.victor.ast.auto.log.logger;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import me.victor.ast.auto.log.Arg;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public class AutoLogAdapter {

    private static final IAutoLogger logger;

    static {
        Iterator<IAutoLogger> iterator = ServiceLoader.load(IAutoLogger.class).iterator();
        if (iterator.hasNext()) {
            logger = iterator.next();
        } else {
            System.err.println("No IAutoLogger implementation class found, use the default SystemOutLogger!");
            logger = new SystemOutLoggerAdapter();
        }
    }

    private AutoLogAdapter() {
        throw new UnsupportedOperationException();
    }

    public static void logArgs(String methodTag, List<Arg> args) {
        logger.logArgs(methodTag, args);
    }

    public static void logReturn(String methodTag, Object retVal) {
        logger.logReturn(methodTag, retVal);
    }

    public static void logTime(String methodTag, long millisecond) {
        logger.logTime(methodTag, millisecond);
    }
    public static String toJson(Object o) {
        return logger.toJson(o);
    }

}
