package me;

import java.util.Collections;
import java.util.Map;

import me.victor.ast.auto.log.annotation.AutoLog;

/**
 * Created by victor on 2022/3/25. (ง •̀_•́)ง
 */

//@AutoLog
public class Main4 {


    private String fieldName;

    @AutoLog("入口方法")
    public static void main(String[] args) {
        //        System.out.println("java.util.List<Map<String, List<String>>".replaceAll("<.*>", ""));
        //        test(new ArrayList<>());

        //        Class<Main2> main2Class = Main2.class;
        //        System.err.println(Main2.a);
        //

        //        test2("111");
        String name = System.currentTimeMillis() + "";

        int a = 0;

        System.err.println(name);
        System.err.println(a);
    }

    //    @AutoLog
    private static Main4 test() {
        return null;
    }

    //    @AutoLog("获取用户信息")
    private static <T> Map<String, T> test2(String methodName) {

        System.err.println(methodName);
        return Collections.singletonMap("methodName", null);
    }

    private void test2() {
        String testName = System.currentTimeMillis() + "";
        System.err.println(testName);
    }
}
