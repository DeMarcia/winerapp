package com.alkaid.winerapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class Utils {
	
	private static ProgressDialog pd;

	public static void toast(Context context,String text){
		if(Constants.T){
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 显示加载框
	 * @param mContext
	 * @param msg
	 */
	public static void showProgressDialog(Context mContext,String msg){
		dismissProgressDialog();
		pd = ProgressDialog.show(mContext, null, msg, true, false);
	}
	
	/**
	 * 关闭加载框
	 */
	public static void dismissProgressDialog(){
		if(pd!=null&&pd.isShowing()){
			pd.dismiss();
			pd = null;
		}
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	 public static String byteArrayToHex(byte[] a) {
	        StringBuilder sb = new StringBuilder(a.length * 2);
	        for(byte b: a)
	            sb.append(String.format("%02x", b & 0xff));
	        return sb.toString();
	    }
}
