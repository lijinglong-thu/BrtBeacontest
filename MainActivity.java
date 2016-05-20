package com.arlong.brtbeacontest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTThrowable;
import com.brtbeacon.sdk.callback.BRTBeaconManagerListener;


public class MainActivity extends Activity {

    BRTBeaconManager beaconManager;
    //private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final String TAG = "BleScan";
    private TextView bleData; //Beacon数据
    private TextView LocData; // 位置数据
    private Button btnScanStart;
    private Button btnScanStop;
    private File BleFileName;
    private float Locx;
    private float Locy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bleData = (TextView)findViewById(R.id.bleData);
        LocData = (TextView)findViewById(R.id.Locdata);
        btnScanStart = (Button)findViewById(R.id.scanStart);
        btnScanStop = (Button)findViewById(R.id.scanStop);

        BleApplication app = (BleApplication) getApplication();
        beaconManager = app.getBRTBeaconManager();

        btnScanStart.setOnClickListener(btnListener);
        btnScanStop.setOnClickListener(btnListener);
    }
    //按键监听
    private Button.OnClickListener btnListener = new Button.OnClickListener()
    {
        public void onClick(View v){
            switch (v.getId())
            {
                case R.id.scanStart:
                    try {
                        doStartScan();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.scanStop:
                    try {
                        doStopScan();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    //****************************************/
    // 传感器数据读取开始与停止函数
    //******************************************/
    private void doStartScan() throws Exception{
        Log.d(TAG, "Create File");
        //Creat txt files for saving data
        String dateTime = android.text.format.DateFormat.format("yyyy_MM_dd_hhmm", new java.util.Date()).toString();
        BleFileName = new File(getDirectory(),String.format("ble%s.txt",dateTime));
        Log.d(TAG, "Start Listener");
        // 启动BLE Listener
        beaconManager.startRanging();
        beaconManager.setBRTBeaconManagerListener(new BRTBeaconManagerListener() {

            @Override
            public void onUpdateBeacon(final ArrayList<BRTBeacon> beacons) {
                // 回调是在非主线程执行的，所以建议操作ui线程的代码放在runOnUiThread或者Handler中执行；
                runOnUiThread(new Runnable() {
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();// 存储蓝牙数据

                        for (BRTBeacon beacon : beacons) {  //这里的beacon 代表单个的蓝牙标签，可在该循环中对每一个蓝牙信号进行求距离等运算
                            if (beacon.name.equals("L001")){ //对L001操作 也可以换用switch语句
                                //float distance1 = (float)((beacon.rssi - (-50))^2); //对L001进行距离计算等
                                //doNotify("gotL001");
                            }
                            else if(beacon.name.equals("L002")){

                            }
                            else if(beacon.name.equals("L003")){

                            }
                            Locx = 0f; Locy = 0f;

                            stringBuilder.append(String.format("%s: %s ", beacon.name,beacon.rssi));//记录ble信息
                            stringBuilder.append("\n");
                        }
                        // 写入文件 SD卡下BrtBeaconScan文件夹中
                        try {
                            doWriteToFile(BleFileName,stringBuilder.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        bleData.setText(stringBuilder.toString());
                        LocData.setText(String.format("x = %.2f , y = %.2f ",Locx,Locy));
                    }
                });

            }

            @Override
            public void onNewBeacon(final BRTBeacon beacon) {
                runOnUiThread(new Runnable() {
                    public void run() {;
                        // System.out.println("onNewBeacon>>>>>>" +
                        // beacon.getMacAddress());
                    }
                });
            }

            @Override
            public void onGoneBeacon(final BRTBeacon beacon) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // System.out.println("onGoneBeacon>>>>>>" +
                        // beacon.getMacAddress());
                    }
                });
            }

            @Override
            public void onError(BRTThrowable throwable) {

            }

        });
        doNotify("Scan Starting");
    }
    private void doStopScan() throws Exception{
        Log.d(TAG,"Stop Scan");
        Log.d(TAG, "Stop Listener");
        beaconManager.stopRanging();
        doNotify("Scan Stop!");
    }
    //****************************************/
    // 数据写入SD卡的函数
    //******************************************/
    public String getDirectory() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        //得到一个路径，内容是sdcard的文件夹路径和名字
        String path=sdcardDir.getPath()+"/BrtBeaconScan";
        File path1 = new File(path);
        if (!path1.exists()) {
            //若不存在，创建目录，可以在应用启动的时候创建
            path1.mkdirs();
        }
        return String.format("%s", path1.toString());
    }
    //获取SD卡目录，在该目录下新建一个sensorRec的子目录

    //写入文件//可以直接追加在文末
    private void doWriteToFile(File file, String string) throws Exception {
        FileWriter fstream = new FileWriter(file, true);//此处true表示后续写入时，会在文件结尾续写，不会覆盖
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(string);
        out.close();
    }
    //****************************************/
    // 屏幕显示通知函数的简化
    //******************************************/
    public void doNotify(String message) {
        doNotify(message, false);
    }
    public void doNotify(String message, boolean longMessage) {
        (Toast.makeText(this, message, longMessage ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
    }
}
