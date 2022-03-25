package me.victor.ast.auto.log;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */
@Getter
@RequiredArgsConstructor
public class Arg {

    private final String name;
    private final Object value;

    @Override
    public String toString() {
        String valueStr = value != null && value.getClass().isArray()
                ? Arrays.toString((Object[]) value)
                : String.valueOf(value);
        return name + "=" + valueStr;
    }
}
