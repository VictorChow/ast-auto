package com.log;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

public class LogFactory {

   public static Logger get(Class<?> clazz) {
      return new Logger(clazz);
   }
}
