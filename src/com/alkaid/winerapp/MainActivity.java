package com.alkaid.winerapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends Activity implements OnClickListener {

	public static final String TAG = "MainActivity";

	private Status status;
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

	private static final int REQUEST_ENABLE_BT = 1;

//	private static final int MSG_WHAT_INIT_VIEW_UNLOAD = 1;
//	private static final int MSG_WHAT_INIT_VIEW_LOAD = 2;
	private static final int MSG_WHAT_ERROR = 3;
	private static final int MSG_WHAT_UPDATE_STATUS = 4;
	private static final int MSG_WHAT_VERIFY_ERROR = 5;

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
		status = new Status();
		LEBCentralManager mLEBCentralManager = LEBCentralManager
				.getInstance(this);
		mLEBCentralManager.operate(mLEBCentralCallback,Constants.UUID_CH_WRITE);
		if (!mLEBCentralManager.isLEBEnable()) {
			// 开启蓝牙
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			LEBCentralManager.getInstance(this).scan();
		}
	}

	// 蓝牙的相关回调
	LEBCentralCallback mLEBCentralCallback = new LEBCentralCallback() {

		@Override
		public void startScanCallback() {
			showProgressDialog(getString(R.string.tip_find_device));
		}

		@Override
		public void scanSuccessCallback(ArrayList<BluetoothDevice> BLEDevices) {
			dismissPdg();
			Intent intent = new Intent(MainActivity.this,
					DevicesListActivity.class);
			ArrayList<String> deviceInfos = new ArrayList<String>();
			for (BluetoothDevice device : BLEDevices) {
				String isPair = device.getBondState() == BluetoothDevice.BOND_BONDED ? "Bonded"
						: "Unbond";
				String deviceName = device.getName();
				if (device.getName() == null) {
					deviceName = "UnkownDevice";
				}
				String str = isPair + "|" + deviceName + "|"
						+ device.getAddress();
				deviceInfos.add(str);
			}
			intent.putStringArrayListExtra(Constants.LIST_DEVICES, deviceInfos);
			startActivityForResult(intent, 0);
		}

		@Override
		public void scanNoDeviceCallback() {
			handleErrorOnUIThread(getString(R.string.notDiscoveryDevices));
		}

		@Override
		public void startDeviceConnectCallback(BluetoothDevice device) {
			showProgressDialog(
					getString(R.string.tip_connect_device));
		}

		@Override
		public void deviceConnectedCallback(BluetoothGatt gatt) {
		}

		@Override
		public void deviceDisConnectedCallback(BluetoothDevice device) {
			handleErrorOnUIThread(getString(R.string.connectionFailed));
		}

		@Override
		public void requestReadOrWrite() {
		}

		@Override
		public void responseReadOrWrite() {
		}
		//发现目标Characteristic 开始验证连接
		@Override
		public void onTargetCharacteristicDiscovered(BluetoothGatt gatt) {
			showProgressDialog(getString(R.string.beginVerify));
//				dismissPdg();
			MainActivity.this. status = new Status();
			// // startReader();
			 // 开始验证链接
			byte randNo = (byte) (Math.random()*0xff );
			if(Constants.D){
				Log.i(TAG, "rand 0x"+Utils.byte2HexStr(randNo));
			}
			toastDebug("The Auth Code is 0x"+Utils.byte2HexStr(randNo));
			MainActivity.this.status.setAuthCode(randNo);
			//验证
//				sendData(new byte[]{(byte) 0xcc,Utils.encode(randNo)});
			//TODO 注意 此处改为分两次发送 每次一字节
			sendCmd(0xcc,Status.CMD_AUTH);
			sendCmd(Utils.encode(randNo),Status.CMD_AUTH);
			runOnUiThread(new Runnable() {
				public void run() {
					initView();
				}
			});
		};

		@Override
		public void onCharacteristicChanged(byte[] info) {
			final String hexStr = Utils.byteArrayToHex(info);
			toastDebug("receive data:0x"+hexStr);
			//test 忽略目前外设返回的0xFF...
			if(info[0]==(byte)0xff)
				return;
			if(!status.isAuthed()&&status.getCurCmd()==Status.CMD_AUTH){
				/*if(!status.isAuthHeadRight()){
					//验证0xcc
					if(info.length==1&&info[0]==(byte)0xcc){
						status.setAuthHeadRight(true);
					}else{
						handleErrorOnUIThread(getString(R.string.verifyFailed));
					}
				}else{*/
					//Auth头已经验证过是0xcc，则
					if(info.length==1&&info[0]==status.getAuthCode()){
						showProgressDialog(getString(R.string.beginInit));
						//验证成功
						status.setAuthed(true);
						//请求初始化马达
						sendCmd(Status.CMD_INIT_MOTO);
					}else{
						handleErrorOnUIThread(getString(R.string.verifyFailed));
					}
//				}
				return;
			}
			//初始化moto数量反馈
			if(status.isAuthed() && !status.isLogined() && status.getCurCmd()==Status.CMD_INIT_MOTO && info.length==1){
				status.setLogined(true);
				switch (info[0]) {
				case (byte)0x30:
					status.setMotoNums(1);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x31:
					status.setMotoNums(2);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x32:
					status.setMotoNums(4);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x33:
					status.setMotoNums(6);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x34:
					status.setMotoNums(8);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x35:
					status.setMotoNums(9);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x36:
					status.setMotoNums(18);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x37:
					status.setMotoNums(20);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
				case (byte)0x38:
					status.setMotoNums(24);
					status.setMotoType(Status.MOTO_TYPE_NORMAL);
					break;
					//特殊马达
				case (byte)0x3a:
					status.setMotoNums(1);
					status.setMotoType(Status.MOTO_TYPE_ZERO);
					break;
				default:
					status.setLogined(false);
					break;
				}
				//马达初始化成功代表登录成功
				if(status.isLogined()){
					runOnUiThread(new Runnable() {
						public void run() {
							dismissPdg();
							Toast.makeText(getApplicationContext(),
									R.string.matchedConnected, Toast.LENGTH_SHORT)
									.show();
						}
					});
				}else{
					handleErrorOnUIThread(""+getString(R.string.initFailed));
				}
				return;
			}
			//指令反馈
			if(status.isLogined() && info.length == 1&&info[0] == (byte)0xaa){
				switch (status.getCurCmd()) {
				// 根据之前的命令更新状态
				case Status.CMD_LIGHT_OFF:
					status.setLightOn(false);
					break;
				case Status.CMD_LIGHT_ON:
					status.setLightOn(true);
					break;
				case Status.CMD_MOTO:
					status.changeMoto();
					break;
				case Status.CMD_SWITCH_OFF:
					status.setSwitchOn(false);
					break;
				case Status.CMD_SWITCH_ON:
					status.setSwitchOn(true);
					break;
//				case Status.CMD_TPD:
//					status.changeTpd();
//					break;
				case Status.CMD_TURN_ALL:
					status.setTurnStatus(Status.TURN_STATUS_ALL);
					break;
				case Status.CMD_TURN_BACK:
					status.setTurnStatus(Status.TURN_STATUS_BACK);
					break;
				case Status.CMD_TURN_FOWARD:
					status.setTurnStatus(Status.TURN_STATUS_FORWARD);
					break;
				default:
					if(status.isTpdCmd()){
						status.changeTpd();
					}
					break;
				}
				mHandler.sendEmptyMessage(MSG_WHAT_UPDATE_STATUS);
				return;
			}
			
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if(status==BluetoothGatt.GATT_SUCCESS){
			}else{
				handleErrorOnUIThread(getString(R.string.discoverServiceFailed));
			}
		}

	};

	@Override
	public void onClick(View v) {
		int cmd = -1;
        if(!status.isLogined()){
            handleError(getString(R.string.notConnectedWhenSendCmd));
            return;
        }
		switch (v.getId()) {
		case R.id.imgLight:
			cmd = status.isLightOn() ? Status.CMD_LIGHT_OFF
					: Status.CMD_LIGHT_ON;
			break;
		case R.id.imgMoto:
			if(status.getMotoNums()<=1)
				return;
			cmd = Status.CMD_MOTO;
			break;
		case R.id.imgSwitch:
			cmd = status.isSwitchOn() ? Status.CMD_SWITCH_OFF
					: Status.CMD_SWITCH_ON;
			break;
		case R.id.imgTpd:
			cmd = status.getCurTpdCmd();
			break;
		case R.id.imgTurn:
			switch (status.getTurnStatus()) {
			case Status.TURN_STATUS_FORWARD:
				cmd = Status.CMD_TURN_BACK;
				break;
			case Status.TURN_STATUS_BACK:
				cmd = Status.CMD_TURN_ALL;
				break;
			case Status.TURN_STATUS_ALL:
				cmd = Status.CMD_TURN_FOWARD;
				break;
			}
			break;
		default:
			return;
		}
		
		sendCmd(cmd);
	}

	private void updateStatusView() {
		if(status == null){
			status = new Status();
		}
		if (status.isLogined()) {
			imgTurnBack.setImageDrawable(animTurnBack);
			animTurnBack.stop();
			animTurnBack.start();
			imgTurnForward.setImageDrawable(animTurnForward);
			animTurnForward.stop();
			animTurnForward.start();

			if (status.isLightOn()) {
				imgLightStatus.setImageDrawable(animLightOn);
				animLightOn.stop();
				animLightOn.start();
			} else {
				imgLightStatus.setImageResource(R.drawable.ico_light_00);
			}
			if (status.isSwitchOn()) {
				imgSwitchStatus.setImageDrawable(animSwitchOn);
				animSwitchOn.stop();
				animSwitchOn.start();
			} else {
				imgSwitchStatus.setImageResource(R.drawable.ico_turn_00);
			}

		} else {
			animTurnBack.stop();
			animTurnForward.stop();
			animSwitchOn.stop();
			animLightOn.stop();
			imgTurnForward.setImageResource(R.drawable.r01);
			imgTurnBack.setImageResource(R.drawable.l01);
			if (status.isLightOn()) {
				imgLightStatus.setImageResource(R.drawable.ico_light_05);
			} else {
				imgLightStatus.setImageResource(R.drawable.ico_light_00);
			}
			if (status.isSwitchOn()) {
				imgSwitchStatus.setImageResource(R.drawable.ico_turn_01);
			} else {
				imgSwitchStatus.setImageResource(R.drawable.ico_turn_00);
			}
		}
		switch (status.getTurnStatus()) {
		case Status.TURN_STATUS_ALL:
			imgTurnBack.setVisibility(View.VISIBLE);
			imgTurnForward.setVisibility(View.VISIBLE);
			break;
		case Status.TURN_STATUS_BACK:
			imgTurnBack.setVisibility(View.VISIBLE);
			imgTurnForward.setVisibility(View.INVISIBLE);
			break;
		case Status.TURN_STATUS_FORWARD:
			imgTurnBack.setVisibility(View.INVISIBLE);
			imgTurnForward.setVisibility(View.VISIBLE);
			break;
		}
		imgCurMoto.setImageBitmap(drawNumImg(status.getCurMoto()+1));
		imgCurTpd.setImageBitmap(drawNumImg(status.getTpd()));
	}

	private Bitmap drawNumImg(int num) {
		// 原图 83*138px 42*69dp
		float w = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42,
				getResources().getDisplayMetrics());
		float h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 69,
				getResources().getDisplayMetrics());
		float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, getResources().getDisplayMetrics());
		Bitmap result = null;
		List<Integer> nums = new ArrayList<Integer>();
		if (num == 0) {
			nums.add(0);
		}
		while (num != 0) {
			nums.add(num % 10);
			num /= 10;
		}
		float maxWidth = w * nums.size() + spacing * (nums.size() - 1);
		result = Bitmap.createBitmap((int) maxWidth, (int) h,
				Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(result);
		Paint mPaint = new Paint();
		for (int i = nums.size() - 1; i >= 0; i--) {
			int id = getResources().getIdentifier("number" + nums.get(i),
					"drawable", getPackageName());
			Bitmap imgNo = BitmapFactory
					.decodeResource(this.getResources(), id);
			float left = (nums.size() - 1 - i) * (w + spacing);
			canvas.drawBitmap(imgNo, left, 0, mPaint);
		}
		return result;
	}
	
	private void toastDebug(final String msg){
		if(Constants.D){
			Log.i(TAG,msg);
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this, msg,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	/** 发送指令 */
	public void sendCmd(final int cmd,int curCmd) {
		status.setCurCmd(curCmd);
		byte cmdbyte=(byte)cmd;
		toastDebug ("send data：0x" + String.format("%02X", cmdbyte));
		LEBCentralManager.getInstance(this).writeCharacteristic(
				new byte[] { cmdbyte });
	}

	/** 发送指令 */
	public void sendCmd(final int cmd) {
		status.setCurCmd(cmd);
		byte cmdbyte=(byte)cmd;
		toastDebug ("send data：0x" + String.format("%02X", cmdbyte));
		LEBCentralManager.getInstance(this).writeCharacteristic(
				new byte[] { cmdbyte });
	}
	/** 发送指令 */
	public void sendData(final byte[] data) {
		String hexStr = Utils.byteArrayToHex(data);
		toastDebug("send data：0x" + hexStr);
		LEBCentralManager.getInstance(this).writeCharacteristic(data);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED
				&& requestCode == REQUEST_ENABLE_BT) {
			// 蓝牙不开启
			finish();
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_ENABLE_BT) {
				// 启动蓝牙成功，并且搜索蓝牙外设
				LEBCentralManager.getInstance(this).scan();
				return;
			}
			int selectedIndex = data.getIntExtra(Constants.SELECTED_INDEX, -1);
			if (selectedIndex < 0) {
				// 没有选择的设备，要么没有设备可选，或者没有选按了返回键
				handleError(getString(R.string.cancelChooseDevice));
				return;
			}
			LEBCentralManager.getInstance().stopScan();
			ArrayList<BluetoothDevice> BLEDevices = LEBCentralManager
					.getInstance(this).BLEDevices;
			if (BLEDevices != null && BLEDevices.size() > 0) {
				// 连接选择上的外设
				LEBCentralManager.getInstance(this).connect(
						BLEDevices.get(selectedIndex));
			}
		}else{
			handleError(getString(R.string.cancelChooseDevice));
		}

	}

	private void initView() {
//		if (loading) {
//			layMain.setBackgroundResource(R.drawable.loading_bg);
//			showProgressDialog(getString(R.string.tip_find_device));
//			shutdown();
//			if (Constants.D)
//				Log.d(TAG, "开始蓝牙操作");
//			Utils.toast(getApplicationContext(), "开始蓝牙操作");
//			LEBCentralManager.getInstance(this).scan();
//		} else {
			layBar.setVisibility(View.VISIBLE);
			layContent.setVisibility(View.VISIBLE);
			layMain.setBackgroundResource(R.drawable.main_bg);
			updateStatusView();
//			dismissPdg();
//		}
	}

	private void shutdown() {
		LEBCentralManager.getInstance().stopScan();
	}

	private void dismissPdg() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (pdg != null && pdg.isShowing()) {
					pdg.dismiss();
					pdg = null;
				}
			}
		});
	}
	/**
	 * 显示加载框
	 * 
	 * @param mContext
	 * @param msg
	 */
	private void showProgressDialog(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (null != pdg && pdg.isShowing()) {
					pdg.setMessage(msg);
				} else {
					// pdg = ProgressDialog.show(this, null, msg, true, true);
					pdg = ProgressDialog.show(MainActivity.this, null, msg, true, true,
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									shutdown();
									dismissPdg();
									handleError(getString(R.string.cancelOperation));
								}
							});
				}
			}
		});
	}
	
	private void handleErrorOnUIThread(String error){
		Message msg=mHandler.obtainMessage(MSG_WHAT_ERROR);
		msg.obj=error;
		mHandler.sendMessage(msg);
	}

	private void handleError(String msg) {
		dismissPdg();
		shutdown();
		initView();
		msg += getString(R.string.tip_error_append);
		if (null != errorDialog && errorDialog.isShowing()) {
			errorDialog.setMessage(msg);
		} else {
			errorDialog = new AlertDialog.Builder(this)
					.setMessage(msg)
					.setCancelable(false)
					.setPositiveButton(R.string.btn_retry,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									LEBCentralManager.getInstance(
											MainActivity.this).scan();
								}
							})
					.setNegativeButton(R.string.btn_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
			;
			errorDialog.show();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//			case MSG_WHAT_INIT_VIEW_LOAD:
//				initView(true);
//				break;
//			case MSG_WHAT_INIT_VIEW_UNLOAD:
//				initView(false);
//				break;
			case MSG_WHAT_ERROR:
				String errMsg = (String) msg.obj;
				handleError(errMsg);
				break;
			case MSG_WHAT_VERIFY_ERROR:
				String emsg = (String) msg.obj;
				handleError(emsg);
				break;
			case MSG_WHAT_UPDATE_STATUS:
				// dismissPdg();
				enableOrDisableUI(true);
				updateStatusView();
				break;
			default:
				break;
			}
		}
	};

	private void enableOrDisableUI(boolean enable) {
		imgLight.setEnabled(enable);
		imgMoto.setEnabled(enable);
		imgSwitch.setEnabled(enable);
		imgTpd.setEnabled(enable);
		imgTurn.setEnabled(enable);
	}


}
