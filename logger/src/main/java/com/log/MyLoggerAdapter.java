package com.log;


import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;

import java.util.List;
import java.util.stream.Collectors;

import me.victor.ast.auto.log.Arg;
import me.victor.ast.auto.log.logger.IAutoLogger;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

@AutoService(IAutoLogger.class)
public class MyLoggerAdapter implements IAutoLogger {

    private static final Logger logger = LogFactory.get(MyLoggerAdapter.class);

    @Override
    public void logArgs(String methodTag, List<Arg> args) {
        logger.info(methodTag + args.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }

    @Override
    public void logReturn(String methodTag, Object retVal) {
        logger.info(methodTag + retVal);
    }

    @Override
    public void logTime(String methodTag, long millisecond) {
        logger.info(methodTag + millisecond);
    }

    @Override
    public String toJson(Object object) {
        return JSON.toJSONString(object);
    }
}
