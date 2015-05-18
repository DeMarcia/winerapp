package com.alkaid.winerapp;

import java.util.ArrayList;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 蓝牙4.0的管理类，包括查找外设，连接外设，传递数据等方法
 * 
 * @author jiang
 * 
 */
@SuppressLint("NewApi")
public class LEBCentralManager {
	
	public static final String TAG = "LEBCentralManager";

	private static LEBCentralManager mInstance;
	private BluetoothManager BLEManager;
	private BluetoothAdapter BLEAdapter;
	private LEBCentralCallback mLEBCentralCallback;
	
	private BluetoothGatt BLEGatt;

	public ArrayList<BluetoothDevice> BLEDevices = null;

	private LEBCentralManager() {
	}

	private static Context mContext;

	private Handler mHandler = new Handler();

	private boolean mScanning = false;

	// 常量
	private static final long SCAN_PERIOD = 5000;// 查找外设期限
	private static final int RE_CONNECT_DEVICE_TIMES = 4;//连接设备失败重试次数

	// 管理类单例
	public static LEBCentralManager getInstance(Context mContext) {
		if (mInstance == null) {
			mInstance = new LEBCentralManager();
		}
		LEBCentralManager.mContext = mContext;
		return mInstance;
	}

	public void operate(LEBCentralCallback mLEBCentralCallback) {
		if (mContext != null) {
			this.BLEManager = (BluetoothManager) mContext
					.getSystemService(Context.BLUETOOTH_SERVICE);
			this.BLEAdapter = BLEManager.getAdapter();
			this.mLEBCentralCallback = mLEBCentralCallback;
			if (BLEDevices == null) {
				BLEDevices = new ArrayList<BluetoothDevice>();
			}
		}
	}

	/** 本机蓝牙是否打开 */
	public boolean isLEBEnable() {
		if (BLEAdapter != null) {
			return BLEAdapter.isEnabled();
		}
		return false;
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			BLEDevices.add(device);
		}

	};

	/**
	 * 查找所有外设
	 */
	public void scan() {
		if (BLEAdapter != null) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mScanning = false;
					BLEAdapter.stopLeScan(mLeScanCallback);
					if (mLEBCentralCallback != null) {
						mLEBCentralCallback.scanCallback(BLEDevices);
					}
				}
			}, SCAN_PERIOD);
			BLEAdapter.startLeScan(mLeScanCallback);
			mScanning = true;
		}
	}

	/**
	 * 根据uuid查找外设
	 * 
	 * @param uuids
	 */
	public void scan(UUID[] uuids) {
		if (BLEAdapter != null) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mScanning = false;
					BLEAdapter.stopLeScan(mLeScanCallback);
					if (mLEBCentralCallback != null) {
						mLEBCentralCallback.scanCallback(BLEDevices);
					}
				}
			}, SCAN_PERIOD);
			BLEAdapter.startLeScan(uuids, mLeScanCallback);
			mScanning = true;
		}
	}

	/**连接某个外设的回调*/
	BluetoothGattCallback BLEGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(
				android.bluetooth.BluetoothGatt gatt, int status, int newState) {
			if(newState == BluetoothProfile.STATE_CONNECTED){
				if(Constants.D)Log.d(TAG, "device connected");
				mLEBCentralCallback.deviceConnectedCallback();
			}else if(newState == BluetoothProfile.STATE_DISCONNECTED){
				if(Constants.D)Log.d(TAG, "device disconnected");
				mLEBCentralCallback.deviceDisConnectedCallback();
			}
		};
	};

	/**
	 * 连接某个外设
	 * 
	 * @param device
	 */
	public void connect(BluetoothDevice device) {
		BLEGatt = device.connectGatt(mContext, false, BLEGattCallback);
	}
	

}
