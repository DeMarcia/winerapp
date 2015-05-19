package com.alkaid.winerapp;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DevicesListActivity extends ListActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devices);
		List<String> list = getIntent().getStringArrayListExtra(Constants.LIST_DEVICES);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
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
}
