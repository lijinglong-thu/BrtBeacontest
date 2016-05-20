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
    private TextView bleData; //Beacon����
    private TextView LocData; // λ������
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
    //��������
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
    // ���������ݶ�ȡ��ʼ��ֹͣ����
    //******************************************/
    private void doStartScan() throws Exception{
        Log.d(TAG, "Create File");
        //Creat txt files for saving data
        String dateTime = android.text.format.DateFormat.format("yyyy_MM_dd_hhmm", new java.util.Date()).toString();
        BleFileName = new File(getDirectory(),String.format("ble%s.txt",dateTime));
        Log.d(TAG, "Start Listener");
        // ����BLE Listener
        beaconManager.startRanging();
        beaconManager.setBRTBeaconManagerListener(new BRTBeaconManagerListener() {

            @Override
            public void onUpdateBeacon(final ArrayList<BRTBeacon> beacons) {
                // �ص����ڷ����߳�ִ�еģ����Խ������ui�̵߳Ĵ������runOnUiThread����Handler��ִ�У�
                runOnUiThread(new Runnable() {
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();// �洢��������

                        for (BRTBeacon beacon : beacons) {  //�����beacon ��������������ǩ�����ڸ�ѭ���ж�ÿһ�������źŽ�������������
                            if (beacon.name.equals("L001")){ //��L001���� Ҳ���Ի���switch���
                                //float distance1 = (float)((beacon.rssi - (-50))^2); //��L001���о�������
                                //doNotify("gotL001");
                            }
                            else if(beacon.name.equals("L002")){

                            }
                            else if(beacon.name.equals("L003")){

                            }
                            Locx = 0f; Locy = 0f;

                            stringBuilder.append(String.format("%s: %s ", beacon.name,beacon.rssi));//��¼ble��Ϣ
                            stringBuilder.append("\n");
                        }
                        // д���ļ� SD����BrtBeaconScan�ļ�����
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
    // ����д��SD���ĺ���
    //******************************************/
    public String getDirectory() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        //�õ�һ��·����������sdcard���ļ���·��������
        String path=sdcardDir.getPath()+"/BrtBeaconScan";
        File path1 = new File(path);
        if (!path1.exists()) {
            //�������ڣ�����Ŀ¼��������Ӧ��������ʱ�򴴽�
            path1.mkdirs();
        }
        return String.format("%s", path1.toString());
    }
    //��ȡSD��Ŀ¼���ڸ�Ŀ¼���½�һ��sensorRec����Ŀ¼

    //д���ļ�//����ֱ��׷������ĩ
    private void doWriteToFile(File file, String string) throws Exception {
        FileWriter fstream = new FileWriter(file, true);//�˴�true��ʾ����д��ʱ�������ļ���β��д�����Ḳ��
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(string);
        out.close();
    }
    //****************************************/
    // ��Ļ��ʾ֪ͨ�����ļ�
    //******************************************/
    public void doNotify(String message) {
        doNotify(message, false);
    }
    public void doNotify(String message, boolean longMessage) {
        (Toast.makeText(this, message, longMessage ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
    }
}
