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

	private static final int MSG_WHAT_INIT_VIEW_UNLOAD = 1;
	private static final int MSG_WHAT_INIT_VIEW_LOAD = 2;
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
			Utils.showProgressDialog(MainActivity.this,
					getString(R.string.tip_find_device));
		}

		@Override
		public void scanSuccessCallback(ArrayList<BluetoothDevice> BLEDevices) {
			Utils.dismissProgressDialog();
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
			Utils.dismissProgressDialog();
			handleError(getString(R.string.notDiscoveryDevices));
		}

		@Override
		public void startDeviceConnectCallback(BluetoothDevice device) {
			Utils.showProgressDialog(MainActivity.this,
					getString(R.string.tip_connect_device));
		}

		@Override
		public void deviceConnectedCallback(BluetoothGatt gatt) {
			Utils.dismissProgressDialog();
			// 连接外设成功操作
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(),
							R.string.matchedConnected, Toast.LENGTH_SHORT)
							.show();
				}
			});
			status = new Status();
			// // startReader();
			// // 开始验证链接
			// int randNo = (int) (Math.random() * 1000);
			// randNo = randNo < 100 ? randNo + 100 : randNo;
			// if (randNo > 127) {
			// int length = String.valueOf(randNo).length();
			// int high = randNo / (int) Math.pow(10, length - 1);
			// int low = randNo % 10;
			// randNo = high * 10 + low;
			// }
			// status.setAuthCode(randNo);
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			//验证
//			LEBCentralManager.getInstance().writeCharacteristic(
//					new byte[] { (byte) 0xcc,(byte) 0x49 });
			// TODO Test
			runOnUiThread(new Runnable() {
				public void run() {
					initView(false);
				}
			});

		}

		@Override
		public void deviceDisConnectedCallback(BluetoothDevice device) {
			runOnUiThread(new Runnable() {
				public void run() {
					handleError(getString(R.string.connectionFailed));
				}
			});
		}

		@Override
		public void requestReadOrWrite() {

		}

		@Override
		public void responseReadOrWrite() {

		}

		@Override
		public void onCharacteristicChanged(byte[] info) {
			final String hexStr = Utils.byteArrayToHex(info);
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(MainActivity.this, "response status:0x"+hexStr, Toast.LENGTH_SHORT).show();
				}
			});
			if(info.length == 1&&info[0] == (byte)0xaa){
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
				case Status.CMD_TPD:
					status.changeTpd();
					break;
				case Status.CMD_TURN_ALL:
					status.setTurnStatus(Status.TURN_STATUS_ALL);
					break;
				case Status.CMD_TURN_BACK:
					status.setTurnStatus(Status.TURN_STATUS_BACK);
					break;
				case Status.CMD_TURN_FOWARD:
					status.setTurnStatus(Status.TURN_STATUS_FORWARD);
					break;
				}
				mHandler.sendEmptyMessage(MSG_WHAT_UPDATE_STATUS);
			}
		}

	};

	@Override
	public void onClick(View v) {
		int cmd = -1;
        if(!isLogined()){
            handleError(getString(R.string.notConnectedWhenSendCmd));
            return;
        }
		switch (v.getId()) {
		case R.id.imgLight:
			cmd = status.isLightOn() ? Status.CMD_LIGHT_OFF
					: Status.CMD_LIGHT_ON;
			break;
		case R.id.imgMoto:
			cmd = Status.CMD_MOTO;
			break;
		case R.id.imgSwitch:
			cmd = status.isSwitchOn() ? Status.CMD_SWITCH_OFF
					: Status.CMD_SWITCH_ON;
			break;
		case R.id.imgTpd:
			cmd = Status.CMD_TPD;
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
		status.setCurCmd(cmd);
		sendCmd(cmd);
	}

	private void updateStatusView() {
		if(status == null){
			status = new Status();
		}
		if (isLogined()) {
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
		imgCurMoto.setImageBitmap(drawNumImg(status.getCurMoto()));
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

	/** 发送指令 */
	public void sendCmd(final int cmd) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(MainActivity.this, ("发送的数据为：0x" + String.format("%02X", cmd)),
						Toast.LENGTH_SHORT).show();
			}
		});
		LEBCentralManager.getInstance(this).writeCharacteristic(
				new byte[] { (byte) cmd });
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
			ArrayList<BluetoothDevice> BLEDevices = LEBCentralManager
					.getInstance(this).BLEDevices;
			if (BLEDevices != null && BLEDevices.size() > 0) {
				// 连接选择上的外设
				LEBCentralManager.getInstance(this).connect(
						BLEDevices.get(selectedIndex));
			}
		}

	}

	private void initView(boolean loading) {
		if (loading) {
			layMain.setBackgroundResource(R.drawable.loading_bg);
			pdg = ProgressDialog.show(this, null,
					getString(R.string.tip_find_device), true, true,
					new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							shutdown();
							dismissPdg();
							handleError(getString(R.string.cancelSearchDevice));
						}
					});
			shutdown();
			if (Constants.D)
				Log.d(TAG, "开始蓝牙操作");
			Utils.toast(getApplicationContext(), "开始蓝牙操作");
			LEBCentralManager.getInstance(this).scan();
		} else {
			layBar.setVisibility(View.VISIBLE);
			layContent.setVisibility(View.VISIBLE);
			layMain.setBackgroundResource(R.drawable.main_bg);
			updateStatusView();
			dismissPdg();
		}
	}

	private void shutdown() {
		// if(null!=reader) reader.shutdown();
		// btop.cancel();
	}

	private void dismissPdg() {
		if (pdg != null && pdg.isShowing()) {
			pdg.dismiss();
			pdg = null;
		}
	}

	private void handleError(String msg) {
		dismissPdg();
		shutdown();
		initView(false);
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
			case MSG_WHAT_INIT_VIEW_LOAD:
				initView(true);
				break;
			case MSG_WHAT_INIT_VIEW_UNLOAD:
				initView(false);
				break;
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

	private boolean isLogined() {
		return true;
	}

}
