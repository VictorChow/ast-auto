package me;

import java.util.Collections;
import java.util.Map;

import me.victor.ast.auto.log.annotation.AutoLog;

/**
 * Created by victor on 2022/3/25. (ง •̀_•́)ง
 */

public class Main4 {

    public static void main(String[] args) {
        //        System.out.println("java.util.List<Map<String, List<String>>".replaceAll("<.*>", ""));
        //        test(new ArrayList<>());

        Class<Main2> main2Class = Main2.class;
        System.err.println(Main2.a);

    }

//    @AutoLog
    private static Main4 test() {
        return null;
    }

//    @AutoLog
    private <T> Map<String, T> test2(String name) {
        System.err.println(name);
        return Collections.singletonMap("name", null);
    }
}
