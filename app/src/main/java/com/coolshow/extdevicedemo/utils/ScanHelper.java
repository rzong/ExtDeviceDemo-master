package com.coolshow.extdevicedemo.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 扫码枪扫码的帮助类, 使用时需要把广播绑定在activity的生命周期中, 用于监听插入USB设备和请求权限的广播, 提供了两个方法
 * <p>
 * registerReceiver()和unregisterReceiver();
 * <p>
 * 如果要想对监听扫码的结果进行监听需要为其设置监听器
 * setScanListener(ScanListener scanListener)
 * 请在启动扫码方法startScan之前进行设置,否则可能会丢失掉信息
 * <p>
 * 请注意在manifest文件的manifest节点下设置权限:
 * <p>
 * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
 * <uses-feature android:name="android.hardware.usb.host"/>
 */

public class ScanHelper {
    private Context mContext;
    private boolean isScanConn;
    private UsbManager mManager;
    private UsbDeviceConnection connection;
    private ScanListener mScanListener;

    private List<String> strlist = new ArrayList<>();
    private int i = 0;

    private static final int HANDLER_SCAN_INPUT = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN_INPUT:
                    if (mScanListener != null) {
                        mScanListener.scan(msg.obj.toString());
                        Log.e("handleMessage",msg.obj.toString());
                    }else {
                        Log.e("handleMessage","mScanListener==null");
                    }
                    break;
                default:
                    Log.e("handleMessage","default");
                    break;
            }
        }
    };
    /**
     * 设备权限的广播
     */
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    /**
     * 插入USB的广播
     */
    private static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    private static final String ACTION_USB_UNPIN = "android.hardware.usb.action.USB_STATE";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("输入的键盘值", "进入onReceive方法");
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //在这里增加通信的代码
                            if (mManager.hasPermission(device)) {
                                Log.e("输入的键盘值", "开始通信");
//                                ToastUtils.showToast(mContext, "开始通信");
                                startScan(device);
                            } else {
                                Log.e("输入的键盘值", "没有权限");
//                                ToastUtils.showToast(mContext, "没有权限");
                            }
                        }else {
                            Log.e("输入的键盘值", "device == null1");
                        }
                    }
                }
            } else if (ACTION_USB_ATTACHED.equals(action)) {
                Log.e("输入的键盘值", "ACTION_USB_ATTACHED");
                UsbDevice device = checkScanDevice();
                if (device != null) {
                    startScan(device);
                }else {
                    Log.e("输入的键盘值", "device == null2");
                }
            }
        }
    };

    public ScanHelper(Context context) {
        mContext = context;
        isScanConn = true;
    }


    public ScanListener getScanListener() {
        return mScanListener;
    }

    public void setScanListener(ScanListener scanListener) {
        mScanListener = scanListener;
    }


    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_ATTACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_UNPIN);
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(mUsbReceiver);
    }

    /**
     * 检测是否有我们所需要的设备
     */
    public UsbDevice checkScanDevice() {

        mManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = mManager.getDeviceList();
        int count = devices.size();
        if (count > 0) {
            for (UsbDevice dev : devices.values()) {
                int pid = dev.getProductId();
                int vid = dev.getVendorId();
                if (hasDevice(vid, pid)) {
                    if (!mManager.hasPermission(dev)) {
                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        mManager.requestPermission(dev, mPermissionIntent);
                        return null;
                    }
                    ToastUtils.showToast(mContext, "获取到设备");
                    return dev;
                }
            }
        }
        return null;
    }

    private boolean hasDevice(int vid, int pid) {
        int[] vids = Constants.DEVICE_VIDS;
        int[] pids = Constants.DEVICE_PIDS;
        for (int i = 0; i < vids.length; i++) {
            if (vids[i] == vid && pids[i] == pid) {
                return true;
            }
        }
        return false;
    }

    /**
     * 启动扫码
     */
    public void startScan(final UsbDevice device) {
        Log.e("输入的键盘值", "startScan");
        if (device == null) {
            Log.e("输入的键盘值", "找不到硬件");
            return;
        }
        isScanConn = true;
        new Thread() {
            @Override
            public void run() {
                while (isScanConn) {
                    UsbInterface usbInterface = device.getInterface(0);
                    UsbEndpoint endpoint = usbInterface.getEndpoint(0);
                    connection = mManager.openDevice(device);
                    if (connection == null) {
                        ToastUtils.showToast(mContext, "不能打开连接!");
                        return;
                    }
                    connection.claimInterface(usbInterface, true);

                    byte[] bytes = new byte[176];
                    connection.bulkTransfer(endpoint, bytes, bytes.length, 0);
//                    Log.e("rfidstr数组", Arrays.toString(bytes));
                    String rfidstr = "";
                    for(int i=0;i<176;i++){
                        if (bytes[i]!=0){
                            rfidstr = rfidstr+Constants.usbcodetokeycode(bytes[i]);
                            if (rfidstr.length()==10){
                                break;
                            }
                        }
                    }
                    Log.e("rfidstr数组", rfidstr);
                    Message message = Message.obtain();
                    message.what = HANDLER_SCAN_INPUT;
                    message.obj = rfidstr;
                    mHandler.sendMessage(message);

                    if (connection != null) {
                        connection.close();
                    }

                }
            }
        }.start();
    }

    public void stopScan() {
        if (connection != null) {
            connection.close();
        }
        isScanConn = false;
        if (strlist.size()>0){
            strlist.clear();
        }
        i = 0 ;
    }

    public interface ScanListener {
        void scan(String data);
    }
}
