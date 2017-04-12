package com.coolshow.extdevicedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coolshow.extdevicedemo.utils.ScanHelper;
import com.coolshow.extdevicedemo.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private ScanHelper mScanHelper;
    private Button bt_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_start = (Button)findViewById(R.id.bt_start);
        init();
    }

    private void init() {
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mScanHelper = new ScanHelper(this);
        mScanHelper.setScanListener(new ScanHelper.ScanListener() {
            @Override
            public void scan(String data) {
                Log.e("监听到回调:",data);
                ToastUtils.showToast(MainActivity.this,"监听到回调:"+data);
                if (!TextUtils.isEmpty(data)){
                    ToastUtils.showToast(MainActivity.this,"监听到回调:"+data+"...");
                }else {
                    ToastUtils.showToast(MainActivity.this,"监听到回调:"+data+"为空");
                }
            }
        });
        mScanHelper.registerReceiver();
        mScanHelper.startScan(mScanHelper.checkScanDevice());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanHelper.unregisterReceiver();
        mScanHelper.stopScan();
    }
}
