package com.cebbank.poin.core.log;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

public class CSPSLogger {

    public CSPSLogger(Class<?> clazz) {
    }

    public void info(Object o) {
        System.err.println("打印: " + o);
    }
}
