package com.alkaid.winerapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	// ui
	private ProgressDialog pdg;
	private AlertDialog errorDialog;
	private View layBar, layMain, layContent;
	private ImageView imgTurnForward, imgTurnBack, imgLightStatus,
			imgSwitchStatus, imgCurMoto, imgCurTpd;
	private ImageView imgLight, imgMoto, imgSwitch, imgTpd, imgTurn;
	// animation
	private AnimationDrawable animLightOn, animSwitchOn, animTurnBack,
			animTurnForward;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// find view
		layBar = findViewById(R.id.layBar);
		layMain = findViewById(R.id.layMain);
		layContent = findViewById(R.id.layContent);
		imgTurnForward = (ImageView) findViewById(R.id.imgTurnForward);
		imgTurnBack = (ImageView) findViewById(R.id.imgTurnBack);
		imgLightStatus = (ImageView) findViewById(R.id.imgLightStatus);
		imgSwitchStatus = (ImageView) findViewById(R.id.imgSwitchStatus);
		imgCurMoto = (ImageView) findViewById(R.id.imgCurMoto);
		imgCurTpd = (ImageView) findViewById(R.id.imgCurTpd);

		imgLight = (ImageView) findViewById(R.id.imgLight);
		imgMoto = (ImageView) findViewById(R.id.imgMoto);
		imgSwitch = (ImageView) findViewById(R.id.imgSwitch);
		imgTpd = (ImageView) findViewById(R.id.imgTpd);
		imgTurn = (ImageView) findViewById(R.id.imgTurn);
		// init anim
		animLightOn = (AnimationDrawable) getResources().getDrawable(
				R.drawable.anim_light_on);
		animSwitchOn = (AnimationDrawable) getResources().getDrawable(
				R.drawable.anim_switch_on);
		animTurnBack = (AnimationDrawable) getResources().getDrawable(
				R.drawable.anim_turn_back);
		animTurnForward = (AnimationDrawable) getResources().getDrawable(
				R.drawable.anim_turn_forward);
	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
            int selectedIndex=data.getIntExtra(Constants.SELECTED_INDEX, -1);
            if(selectedIndex<0) {
            	//没有选择的设备，要么没有设备可选，或者没有选按了返回键
                handleError(getString(R.string.cancelChooseDevice));
                return;
            }
            ArrayList<BluetoothDevice> BLEDevices = LEBCentralManager.getInstance(this).BLEDevices; 
    		if(BLEDevices!=null&&BLEDevices.size()>0){
    			//连接选择上的外设
    			LEBCentralManager.getInstance(this).connect(BLEDevices.get(selectedIndex));
    		}
		}
		
	}
	
    private void handleError(String msg){}

}
