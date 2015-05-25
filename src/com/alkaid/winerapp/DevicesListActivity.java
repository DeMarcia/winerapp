package com.alkaid.winerapp;

import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DevicesListActivity extends ListActivity{

	List<String> devices;
	ArrayAdapter<String>  adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices);
		devices = getIntent().getStringArrayListExtra(Constants.LIST_DEVICES);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devices);
		this.setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent data=new Intent();
		data.putExtra(Constants.SELECTED_INDEX, position);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LEBCentralManager.ACTION_ADD_DEVICE);
		registerReceiver(mGattUpdateReceiver, intentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (LEBCentralManager.ACTION_ADD_DEVICE.equals(action)) {
            	String device=intent.getStringExtra(LEBCentralManager.BUNDLE_KEY_DEVICE);
            	devices.add(device);
            	adapter.notifyDataSetChanged();
            } 
        }
    };
}
