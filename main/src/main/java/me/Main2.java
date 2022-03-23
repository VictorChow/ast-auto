package me;

import me.victor.ast.auto.log.annotation.AutoLog;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public class Main2 {

    public static void main(String[] args) {
        test("1111", "2222");
        test("aaa", "bbb");
        test("aaa", "bbb");
        test("aaa", "bbb");
    }

    @AutoLog
    private static String test(String a, String b) {
        System.out.println("业务逻辑:" + a + " " + b);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Victor" + System.currentTimeMillis();
    }
}
