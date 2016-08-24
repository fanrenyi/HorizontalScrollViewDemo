package com.example.fan.horizontalscrollview.system;

import android.content.Context;
import android.os.Build;
import java.lang.reflect.Method;

/**
 * 机型工具类
 *
 * @author liuwei
 */
public class DeviceUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 魅族手机smartbar相关判断
     */
    private static boolean m_bDetected = false;
    private static boolean m_bHasSmartBar = false;

    public static boolean hasSmartBar() {
        if (m_bDetected)//已检测过了，直接返回保存的检测结果
        {
            return m_bHasSmartBar;
        } else //未检测过，先判断手机厂商是否为魅族，如果是,判断是否有smartbar，否则直接返回false。
        {
            if (Build.MANUFACTURER.equals("Meizu") && Build.VERSION.SDK_INT >= 14) {
                try {
                    // 新型号可用反射调用Build.hasSmartBar()
                    Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
                    m_bHasSmartBar = ((Boolean) method.invoke(null)).booleanValue();
                    m_bDetected = true;
                    return m_bHasSmartBar;
                } catch (Exception e) {
                }

                PhoneOSUtil.Data romData = PhoneOSUtil.getData();
                String ver = romData.getVer().toLowerCase();
                if (PhoneModelUtils.getFlymeVer(ver) >= 5130) {
                    m_bDetected = true;
                    m_bHasSmartBar = false;
                    return m_bHasSmartBar;
                }

                // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
                if (!Build.DEVICE.equals("mx") && Build.DEVICE.contains("mx")) {
                    m_bHasSmartBar = true;
                    m_bDetected = true;
                    return m_bHasSmartBar;
                } else //if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9"))
                {
                    m_bHasSmartBar = false;
                    m_bDetected = true;
                    return m_bHasSmartBar;
                }
            } else {
                m_bHasSmartBar = false;
                m_bDetected = true;
                return m_bHasSmartBar;
            }
        }
    }
}
