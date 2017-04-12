# ExtDeviceDemo-master
   android Usb Reader 
   使用场景：定制的android设备带RFID功能，可以刷IC卡之类的，通过该demo可以读取到刷卡卡片中的信息(不加密的卡片和读卡器)，
将获取到的信息转换为键盘上的编码，便是正确的卡片信息，用来做身份验证使用，我已使用项目验证，没有问题。步骤：

<p>1. AndroidManifest中添加权限  
<code><uses-feature android:name="android.hardware.usb.host"/></code>  

<code><activity android:name=".MainActivity">  
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
           <intent-filter>
                <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"/>
            </intent-filter>
        </activity></code>
</p><br />

<p>2. 知道读卡器的PID和VID，这两个参数是匹配硬件的唯一码，android系统可以很多外接usb设备，我demo里面放到了Constants文件里面<br />
<code>
public static final int[] DEVICE_PIDS = {53};
public static final int[] DEVICE_VIDS = {65535};
</code>
</p>
<p>我demo的卡片数据是由0-9的数字组成的，所以我只替换这些，如果有其他的，可以google一下usb-hid协议，里面有对应的输出转换<br />
  <code>public static String usbcodetokeycode(int xx){</code><br />
        <code>String str = "";</code><br />
        <code>if (xx == 30){</code><br />
            <code>str = "1";</code><br />
        <code>}else if (xx == 31){</code><br />
            <code>str = "2";</code><br />
        <code>}else if (xx == 32){</code><br />
            <code>str = "3";</code><br />
        <code>}else if (xx == 33){</code><br />
            <code>str = "4";</code><br />
        <code>}else if (xx == 34){</code><br />
            <code>str = "5";</code><br />
        <code>}else if (xx == 35){</code><br />
            <code>str = "6";</code><br />
        <code>}else if (xx == 36){</code><br />
            <code>str = "7";</code><br />
        <code>}else if (xx == 37){</code><br />
            <code>str = "8";</code><br />
        <code>}else if (xx == 38){</code><br />
            <code>str = "9";</code><br />
        <code>}else if (xx == 39){</code><br />
            <code>str = "0";</code><br />
        <code>}</code><br />
        <code>return  str;</code><br />
    <code>}</code><br />
</p>

<p>3. 创建一个工具类，注册一个动态广播，来监听刷卡的输入，包含了查找对应的usb设备，是否有权限可以读取，如果有的话就可以执行读取数据的工作，
 这里面有一个我任务很重要的参数<br />
 <code>
 byte[] bytes = new byte[176];
 connection.bulkTransfer(endpoint, bytes, bytes.length, 0);
 </code>
 <br />代码中的176是一个很重要的参数，它其实是指一个读卡器一次性可以需要读取多少数据，如果这个值太大或者太小，那你每次都取出来的值都不一样，如果大于卡片的数据位数，那么你第一次一定读不到数据，后面每次值都不一样；如果小于卡片信息的位数值，那么读取不全，每次也会不应，第二行代码的意思就是讲usb读取到的原数据放到bytes中去
</p> 

<p>4. 既然使用用了广播，就要记得注册和反注册，UsbDeviceConnection用完就要close，否则这些很容易app崩溃掉</p> 
