package com.alkaid.winerapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class Utils {

	public static void toast(Context context, String text) {
		if (Constants.T) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
	
	public static String byte2HexStr(byte num){
		return String.format("%02X", num);
	}

	public static byte encode(byte random) {
		// 高低位取反
		byte temp = (byte) ((random << 4) & 0xf0);
		random &= 0xf0;
		random = (byte) ((random >> 4) & 0x0f);
		random |= temp;
		// 和0xf0 Xor
		random ^= 0xf0;
		return random;
	}

	public static byte decode(byte num) {
		// 和0xf0 Xor
		num ^= 0xf0;
		// 高低位取反
		byte temp = (byte) ((num << 4) & 0xf0);
		num &= 0xf0;
		num = (byte) ((num >> 4) & 0x0f);
		num |= temp;
		return num;
	}
}
