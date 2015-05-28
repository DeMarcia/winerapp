package com.alkaid.winerapp;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

/**
 * 蓝牙4.0相关回调
 * @author jiang
 *
 */
public interface LEBCentralCallback {
	
	/**开始搜索外设的回调*/
	public void startScanCallback();
	
	/**搜索外设后的回调*/
	public void scanSuccessCallback(ArrayList<BluetoothDevice> BLEDevices);
	
	/**没有搜索到任何外设*/
	public void scanNoDeviceCallback();
	
	/**开始链接外设的回调*/
	public void startDeviceConnectCallback(BluetoothDevice device);
	
	/**连接上外设的回调*/
	public void deviceConnectedCallback(BluetoothGatt gatt);
	
	/**发现服务后的回调*/
	public void onServicesDiscovered(BluetoothGatt gatt, int status);
	
	/**发现目标Characteristic*/
	public void onTargetCharacteristicDiscovered(BluetoothGatt gatt);
	
	/**未连接上外设,有包含重试连接的情况*/
	public void deviceDisConnectedCallback(BluetoothDevice device);
	
	/**开始发送读写操作*/
	public void requestReadOrWrite();
	
	/**返回读写操作*/
	public void responseReadOrWrite();
	
	/**监听返回的参数信息*/
	public void onCharacteristicChanged(byte[] param);

}
