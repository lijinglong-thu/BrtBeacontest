package com.arlong.brtbeacontest;

import android.app.Application;

import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.IBle;
import com.brtbeacon.sdk.utils.L;

public class BleApplication extends Application {

	private BRTBeaconManager beaconManager;

	@Override
	public void onCreate() {
		super.onCreate();
		L.enableDebugLogging(true);// 开启log打印
		beaconManager = BRTBeaconManager.getInstance(this);
		// 注册应用 APPKEY申请地址brtbeacon.com/main/index.shtml
		beaconManager.registerApp("00000000000000000000000000000000");
		// 开启Beacon扫描服务
		beaconManager.startService();

	}
	/**
	 * 创建Beacon连接需要传递此参数
	 * @return IBle
	 */
	public IBle getIBle() {
		return beaconManager.getIBle();
	}

	/**
	 * 获取Beacon管理对象
	 * 
	 * @return BRTBeaconManager
	 */
	public BRTBeaconManager getBRTBeaconManager() {
		return beaconManager;
	}

}
