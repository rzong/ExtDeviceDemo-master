package com.coolshow.extdevicedemo.utils;

/**
 * 存放指定的pid和vid
 */
public class Constants {
    public static final int[] DEVICE_PIDS = {53};
    public static final int[] DEVICE_VIDS = {65535};

    //USB输出转对应的键盘码
    public static String usbcodetokeycode(int xx){
        String str = "";
        if (xx == 30){
            str = "1";
        }else if (xx == 31){
            str = "2";
        }else if (xx == 32){
            str = "3";
        }else if (xx == 33){
            str = "4";
        }else if (xx == 34){
            str = "5";
        }else if (xx == 35){
            str = "6";
        }else if (xx == 36){
            str = "7";
        }else if (xx == 37){
            str = "8";
        }else if (xx == 38){
            str = "9";
        }else if (xx == 39){
            str = "0";
        }else if (xx == 40){
            str = "e";
        }
        return  str;
    }

}
