package com.log;


import com.google.auto.service.AutoService;

import me.victor.ast.auto.log.logger.IAutoLogger;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

@AutoService(IAutoLogger.class)
public class MyLoggerAdapter implements IAutoLogger {

    private static final Logger logger = LogFactory.get(MyLoggerAdapter.class);

    @Override
    public void info(String log) {
        logger.info(log);
    }
}
