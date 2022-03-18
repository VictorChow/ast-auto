package com.log;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

public class Logger {

    public Logger(Class<?> clazz) {
    }

    public void info(Object o) {
        System.out.println(o);
    }

    public void test() {
        System.err.println("test");
    }
}
