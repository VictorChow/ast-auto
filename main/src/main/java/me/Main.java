package me;

import java.util.ArrayList;

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
        //        test(Collections.singletonList(123), Collections.singletonMap("k", "v"));
        //        test2(123);
        //        test3("1", "2", "3");
        System.err.println(testReturn("12"));
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


    //    @LogParam
    private static String test3(String... strings) {
        return " ===";
    }

    @LogParam
    private static String testReturn(String name) {
        //        if (System.currentTimeMillis() > 0) {
        //            return "1";
        //        } else return "2";

        //        synchronized (Main.class) {
        //            return "111";
        //        }

        //        for (char c : name.toCharArray()) {
        //            if (c == 'c') return "ccccc";
        //            else return "no ccccc";
        //        }

        if (name.length() > 3) return ">3";

        new ArrayList<>().get(0);
        //        switch (name.length()) {
        //            case 1: {
        //                System.err.println("~~~");
        //                return "111";
        //            }
        //            case 2:
        //                String time = String.valueOf(System.currentTimeMillis());
        //                time += " 123";
        //                return time;
        //            case 3:
        //            default:
        //                return "default !!";
        //        }


        return "def";
    }


}
