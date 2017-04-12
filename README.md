# ExtDeviceDemo-master
android Usb Reader 
使用场景：定制的android设备带RFID功能，可以刷IC卡之类的，通过该demo可以读取到刷卡卡片中的信息(不加密的卡片和读卡器)，
将获取到的信息转换为键盘上的编码，便是正确的卡片信息，用来做身份验证使用，我已使用项目验证，没有问题。
步骤：
1. AndroidManifest中添加权限 <uses-feature android:name="android.hardware.usb.host"/>
 <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
           <intent-filter>
                <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"/>
            </intent-filter>
        </activity>
        
2. 知道读卡器的PID和VID，这两个参数是匹配硬件的唯一码，android系统可以很多外接usb设备，我demo里面放到了Constants文件里面
    public static final int[] DEVICE_PIDS = {53};
    public static final int[] DEVICE_VIDS = {65535};
    
    //我demo的卡片数据是由0-9的数字组成的，所以我只替换这些，如果有其他的，可以google一下usb-hid协议，里面有对应的输出转换
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
        }
        return  str;
    }
 3. 创建一个工具类，注册一个动态广播，来监听刷卡的输入，包含了查找对应的usb设备，是否有权限可以读取，如果有的话就可以执行读取数据的工作，
 这里面有一个我任务很重要的参数
 byte[] bytes = new byte[176];
 connection.bulkTransfer(endpoint, bytes, bytes.length, 0);
 代码中的176是一个很重要的参数，它其实是指一个读卡器一次性可以需要读取多少数据，如果这个值太大或者太小，那你每次都取出来的值都不一样，如果大于卡片的数据位数，那么你第一次一定读不到数据，后面每次值都不一样；如果小于卡片信息的位数值，那么读取不全，每次也会不应，第二行代码的意思就是讲usb读取到的原数据放到bytes中去
 4. 既然使用用了广播，就要记得注册和反注册，UsbDeviceConnection用完就要close，否则这些很容易app崩溃掉
