package me.victor.ast.auto.log.logger;

import me.victor.ast.auto.log.Arg;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public interface IAutoLogger {

    void logArgs(String methodTag, Arg... args);

    void logReturn(String methodTag, Object retVal);

    void logTime(String methodTag, long millisecond);
}
