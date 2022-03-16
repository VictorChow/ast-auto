package me;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;

import java.util.Collections;

import me.victor.lombok.core.annotation.LogParam;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

@LogParam
public class Main {

    public static void main(String[] args) {

        test(Collections.singletonList(123), Collections.singletonMap("k","v"));
        test3(123);
    }

    @LogParam
    public static String test(Object param1, Object param2) {
        return param1 + " + " + param2;
    }

    @LogParam
    private static String test3(int age) {
        return age + " ===";
    }
}
