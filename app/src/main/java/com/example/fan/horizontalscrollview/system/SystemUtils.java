package com.example.fan.horizontalscrollview.system;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by LiZhenxing on 14-11-25.
 */
public class SystemUtils {
    public static String getAndroidID(Context context) {
        try {
            ContentResolver cr = context.getContentResolver();
            return Settings.System.getString(cr, Settings.System.ANDROID_ID);
        } catch (Exception e) {
            return "";
        }
    }
}
