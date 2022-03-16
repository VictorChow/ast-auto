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
        System.err.println(args.getClass().isArray());
        test(Collections.singletonList(123), Collections.singletonMap("k", "v"));
        test2(123);
        test3("1", "2", "3");
    }

    @LogParam
    public static String test(Object param1, Object param2) {
        return param1 + " + " + param2;
    }

    @LogParam
    private static String test2(int age) {
        return age + " ===";
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
