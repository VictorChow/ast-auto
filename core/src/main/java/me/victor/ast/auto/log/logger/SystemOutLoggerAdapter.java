package me.victor.ast.auto.log.logger;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public class SystemOutLoggerAdapter implements IAutoLogger {

    @Override
    public void info(String log) {
        System.err.println(log);
    }
}
