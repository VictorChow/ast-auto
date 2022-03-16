package com.cebbank.poin.core.log;

/**
 * Created by victor on 2022/3/15. (ง •̀_•́)ง
 */

public class CSPSLogFactory {

   public static CSPSLogger get(Class<?> clazz) {
      return new CSPSLogger(clazz);
   }
}
