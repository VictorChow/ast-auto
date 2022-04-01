package me.victor.ast.auto.stringfog;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created by victor on 2022/3/31. (ง •̀_•́)ง
 */

public class XOR {
    private static final byte[] KEYS = new byte[]{84, 24, 1, 57, 8, 49, 51, 25, 54, 55, 89, 34, 13, 10, 5, 39, 6, 98, 46, 66};

    public static String coding(String message) {
        return coding(message, KEYS);
    }

    public static byte[] randomKeys(){
        return UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
    }

    public static String coding(String message, byte[] keys) {
        byte[] origin = message.getBytes();
        byte[] master = new byte[origin.length];
        for (int i = 0, len = origin.length; i < len; i++) {
            master[i] = (byte) (origin[i] ^ keys[i % keys.length]);
        }
        return new String(master);
    }

}
