package me.victor.ast.auto.log.logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.victor.ast.auto.log.Arg;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public class SystemOutLoggerAdapter implements IAutoLogger {

    @Override
    public void logArgs(String methodTag, Arg... args) {
        System.out.println(methodTag + Arrays.stream(args)
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public void logReturn(String methodTag, Object retVal) {
        System.out.println(methodTag + retVal);
    }

    @Override
    public void logTime(String methodTag, long millisecond) {
        System.out.println(methodTag + millisecond);
    }
}
