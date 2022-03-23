package me.victor.ast.auto.log;

import java.util.Arrays;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */
public class Arg {

    private final String name;
    private final Object value;

    public Arg(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        String valueStr = value != null && value.getClass().isArray()
                ? Arrays.toString((Object[]) value)
                : String.valueOf(value);
        return name + "=" + valueStr;
    }
}
