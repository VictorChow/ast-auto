package me;

import me.victor.ast.auto.log.annotation.AutoLog;
import me.victor.ast.auto.log.annotation.LogJSON;
import me.victor.ast.auto.log.logger.AutoLogAdapter;

import java.util.HashMap;
import java.util.Map;

public class Main3 {
    public static void main(String[] args) {


        String test = "xiaoming";
        HashMap map = new HashMap();
        map.put("name","小明");
        map.put("age","18");
        test(test,map);
        test(test,new A());
    }

    private static class A {
        private int age = 10;
        private String name = "aaa";

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "A{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @AutoLog
    public static void test(String test, @LogJSON Object map) {
        System.out.println("~~~~~~~~~~~~");
//        System.err.println("test:" + test);
//        System.err.println("map:" + map);
    }
}
