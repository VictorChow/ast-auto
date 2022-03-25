package me;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.victor.ast.auto.log.annotation.AutoLog;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */

public class Main2 {

    public static void main(String[] args) {
        //        test("1111", "2222");
        //        test("aaa", "bbb");
        //        test("aaa", "bbb");
        //        test("aaa", "bbb");

//        test("张三", 20);
        test("张三", 20);
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

    @AutoLog
    private static Student test(String name, int age) {
        if (name == null) return new Student("无名", 0);
        if (age > 100) {
            throw new IllegalArgumentException("太老了");
        }
        return new Student(name, age);
    }

    @Data
    @AllArgsConstructor
    private static class Student {
        private String stuName;
        private int stuAge;
    }
}
