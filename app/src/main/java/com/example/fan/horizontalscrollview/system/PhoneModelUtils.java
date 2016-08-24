
package com.example.fan.horizontalscrollview.system;

import android.text.TextUtils;
public class PhoneModelUtils {



    public static int getFlymeVer(String ver) {
        if (TextUtils.isEmpty(ver) || !ver.contains("flyme")) {
            return 0;
        }

        String[] verGroup = ver.split("\\D+");
        int verDigit = 0;
        int verMulti = 1;
        for (int i = verGroup.length - 1; i > 0; --i) {
            try {
                verDigit += Integer.parseInt(verGroup[i]) * verMulti;
                verMulti *= 10;
            } catch (Exception e) {
                verDigit = 0;
            }
        }
        return verDigit;
    }
 }