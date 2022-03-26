package me.victor.ast.auto.log.logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import me.victor.ast.auto.log.Arg;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */
@UtilityClass
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

    public static void logArgs(String methodTag, Arg... args) {
        String log = methodTag + Arrays.stream(args)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        logger.info(log);
    }

    public static void logReturn(String methodTag, Object retVal) {
        logger.info(methodTag + retVal);
    }

    public static void logTime(String methodTag, long millisecond) {
        logger.info(methodTag+ millisecond);
    }
}
