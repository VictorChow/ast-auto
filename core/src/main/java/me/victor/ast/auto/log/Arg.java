package me.victor.ast.auto.log;

import com.google.auto.service.AutoService;
import me.victor.ast.auto.log.annotation.FormatType;
import me.victor.ast.auto.log.logger.AutoLogAdapter;

import java.util.Arrays;

/**
 * Created by victor on 2022/3/23. (ง •̀_•́)ง
 */
public class Arg {

    private final String name;
    private final Object value;
    private final FormatType formatType;

    public Arg(String name, Object value, FormatType formatType) {
        this.name = name;
        this.value = value;
        this.formatType = formatType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public FormatType getFormatType() {
        return formatType;
    }

    @Override
    public String toString() {
        return name + "=" + (formatType == FormatType.JSON ? AutoLogAdapter.toJson(value) : plainString());
    }

    private String plainString() {
        return value != null && value.getClass().isArray()
                ? Arrays.toString((Object[]) value)
                : String.valueOf(value);
    }
}
