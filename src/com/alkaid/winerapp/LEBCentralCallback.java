package com.alkaid.winerapp;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙4.0相关回调
 * @author jiang
 *
 */
public interface LEBCentralCallback {
	
	/**搜索外设后的回调*/
	public void scanCallback(ArrayList<BluetoothDevice> BLEDevices);
	
	/**连接上外设的回调*/
	public void deviceConnectedCallback();
	
	/**未连接上外设*/
	public void deviceDisConnectedCallback();

}
