package me;

import java.util.ArrayList;
import java.util.List;

import me.victor.ast.auto.log.annotation.AutoLog;

/**
 * Created by victor on 2022/3/25. (ง •̀_•́)ง
 */

public class Main3 {

    public static void main(String[] args) {
        //        System.out.println("java.util.List<Map<String, List<String>>".replaceAll("<.*>", ""));
//        test(new ArrayList<>());

        Class<Main2> main2Class = Main2.class;
        System.err.println(Main2.a);

    }

//    @AutoLog
    private static Main3 test() {
        return null;
    }

//    @AutoLog
    private static List<String> test(List<String> args) {

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        return list;
    }
}
