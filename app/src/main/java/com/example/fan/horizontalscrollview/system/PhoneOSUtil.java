package com.example.fan.horizontalscrollview.system;

import android.os.Build;
import android.os.SystemProperties;

public class PhoneOSUtil {
    private PhoneOSUtil() {
    }

    public static class Data {
        private String os;
        private String ver;
        private Data(String os, String ver) {
            this.os = os;
            this.ver = ver;
        }

        public String getOs() {
            return os;
        }

        public String getVer() {
            return ver;
        }
    }

    public static Data getData() {
        String ver = "";

        //XiaoMi - MIUI
        ver = SystemProperties.get("ro.miui.ui.version.name", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("MIUI", ver);
        }

        //HuaWei - EMUI
        ver = SystemProperties.get("ro.build.version.emui", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("EMUI", ver);
        }

        //Oppo - ColorOS
        ver = SystemProperties.get("ro.build.version.opporom", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("OPPO", ver);
        }

        //Yun - YunOS
        ver = SystemProperties.get("ro.yunos.version", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("YunOS", ver);
        }

        //VIVO - FuntouchOS
        ver = SystemProperties.get("ro.vivo.os.build.display.id", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("VIVO", ver);
        }

        //letv -
        ver = SystemProperties.get("ro.letv.release.version", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("letv", ver);
        }

        //Coolpad - UGOLD
        ver = SystemProperties.get("ro.coolpad.ui.theme", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("Coolpad", ver);
        }

        //nubia - nubia
        ver = SystemProperties.get("ro.build.nubia.rom.code", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            return new Data("nubia", ver);
        }

        //GiONEE、Flyme - amigo、Flyme
        ver = SystemProperties.get("ro.build.display.id", "UNKNOWN");
        if(ver != null && !ver.equals("UNKNOWN")) {
            String str = ver.toLowerCase();
            if(str.contains("amigo")) {
                return new Data("GiONEE", ver);
            } else if(str.contains("flyme")){
                return new Data("Flyme", ver);
            }
        }

        String fingerPrint = "";
        //MeiZu - FlyMe
        try {
            fingerPrint = Build.FINGERPRINT.toLowerCase();
            if(fingerPrint.contains("flyme")) {
                return new Data("FLYME", ver);
            }
        } catch (Exception e) {
        }

       if (!fingerPrint.equals("")) {
           int index = fingerPrint.indexOf("/");
           if (index == -1){
               return new Data(ver, "");
           }
           String versionName = fingerPrint.substring(0,index);
           ver = SystemProperties.get("ro.build.version.incremental", "UNKNOWN");
           return new Data(versionName, ver);
       }

        return new Data(ver, "");
    }

}
