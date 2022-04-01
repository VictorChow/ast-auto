package me;

import me.victor.ast.auto.stringfog.annotation.StringFog;

/**
 * Created by victor on 2022/3/30. (ง •̀_•́)ง
 */
@StringFog
public class Fog {
    private static final String NAME = "111";
    private static final String name2 = "111";
    private final String name3 = "111";
    private final String name4 = "111";

    public static void main(String[] args) {


        byte[] arr = {1, 2, 3};
        byte[] arr2 = new byte[]{1, 2, 3};
        //        Arrays.toString();


        int a = 1;
        String s = "123";
        s = "11111";
        String s1 = System.currentTimeMillis() + "";

        System.out.println("6666");
        System.out.println((String) null);

        String aa = s1.replaceAll(".+", "-");
        System.out.println(aa);
        s1.replaceAll("A+", "----");

        String s2 = new StringBuilder().append("1")
                .append(System.nanoTime())
                .append("2")
                .append("3")
                .append(4)
                .toString();
        System.out.println(s2);
        System.out.println("1" + 2 + "2");

        String name = "Victor Chow";
        System.out.println(name);
        name = name + " 1993";
        System.out.println(name);
        System.out.println(name.replaceAll(" +\\d+", "!!!"));
    }
}
