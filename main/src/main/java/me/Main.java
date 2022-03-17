package me;

import java.util.Collections;
import java.util.Objects;

import me.victor.lombok.core.annotation.LogParam;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

//@LogParam
public class Main {

    private String name;

    //    @LogParam
    public static void main(String[] args) {
        //        System.err.println(args.getClass().isArray());
        test(Collections.singletonList(123), Collections.singletonMap("k", "v"));
        test2(123);
        test3("1", "2", "3");
    }

    //    @LogParam
    public static String test(Object param1, Object param2) {
        if (param1 == null) {
            return String.valueOf(param2);
        }
        if (param2 == null) {
            return String.valueOf(param1);
        }
        return param1 + " + " + param2;
    }

    //    @LogParam
    private static void test2(int age) {
        System.err.println(age);
    }

    //    @MethodLogger
    public static String test2(Object param1, Object param2) {
        if (param1 == null) {
            return String.valueOf(param2);
        }
        if (param2 == null) {
            return String.valueOf(param1);
        }
        return param1 + " + " + param2;
    }


    @LogParam
    private static String test3(String... strings) {
        return " ===";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Main main = (Main) o;

        return Objects.equals(name, main.name);
    }

}
