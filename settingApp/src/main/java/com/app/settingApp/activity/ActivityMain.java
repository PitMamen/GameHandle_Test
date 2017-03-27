package com.app.settingApp.activity;

import com.app.settingApp.R;
import com.app.settingApp.ScreenAdjust.ScreenAdjustActivity;
import com.app.settingApp.adapter.HomeListAdapter;
import com.app.settingApp.battery.BatteryActivity;
import com.app.settingApp.bluetooth.ActivityBluetooth;
import com.app.settingApp.brightness.ActivityBrightness;
import com.app.settingApp.colortemperature.ActivityColorTemperature;
import com.app.settingApp.deviceinfo.DeviceInfoActivity;
import com.app.settingApp.languageKeyboard.LanguageKeyboardActivity;
import com.app.settingApp.sdcard.SDCardActivity;
import com.app.settingApp.settingReset.SettingResetActivity;
import com.app.settingApp.upgrade.FirmwareUpgradeActivity;
import com.app.settingApp.view.ControlView;
import com.app.settingApp.view.GGridView;
import com.app.settingApp.view.HomeItemView;
import com.app.settingApp.volume.VolumeActivity;
import com.app.settingApp.wifi.ActivityWifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class ActivityMain extends ActivityReceive implements OnClickListener, OnItemClickListener, 
OnItemSelectedListener {
	private static final String		TAG = "ActivityMain";
	private static final int		NUM_PER_ITEM = 12;
	
	private	GridView		mGridView;
	private HomeListAdapter mAdapter;
	private ControlView 	mControlView;
	private Context 		mContext ;
	private HomeItemView    mItems [] = new HomeItemView[NUM_PER_ITEM]; 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContext = this;	
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() { 
		mGridView = (GridView) findViewById(R.id.main_gridview);
		mControlView = (ControlView) findViewById(R.id.control_view);
		/*mItems[0] = (HomeItemView) findViewById(R.id.item0);
		mItems[0].setName(getResources().getString(R.string.wifi));
		mItems[1] = (HomeItemView) findViewById(R.id.item1);
		mItems[1].setName(getResources().getString(R.string.bluetooth));
		mItems[2] = (HomeItemView) findViewById(R.id.item2);
		mItems[2].setName(getResources().getString(R.string.brightness));
		mItems[3] = (HomeItemView) findViewById(R.id.item3);
		mItems[3].setName(getResources().getString(R.string.calibration));
		mItems[4] = (HomeItemView) findViewById(R.id.item4);
		mItems[4].setName(getResources().getString(R.string.color));
		mItems[5] = (HomeItemView) findViewById(R.id.item5);
		mItems[5].setName(getResources().getString(R.string.battery));
		mItems[6] = (HomeItemView) findViewById(R.id.item6);
		mItems[6].setName(getResources().getString(R.string.sdcard));
		mItems[7] = (HomeItemView) findViewById(R.id.item7);
		mItems[7].setName(getResources().getString(R.string.volume));
		mItems[8] = (HomeItemView) findViewById(R.id.item8);
		mItems[8].setName(getResources().getString(R.string.language));
		mItems[9] = (HomeItemView) findViewById(R.id.item9);
		mItems[9].setName(getResources().getString(R.string.firmware));
		mItems[10] = (HomeItemView) findViewById(R.id.item10);
		mItems[10].setName(getResources().getString(R.string.deviceinfo));
		mItems[11] = (HomeItemView) findViewById(R.id.item11);
		mItems[11].setName(getResources().getString(R.string.initialization));
		
		for (int i=0; i<NUM_PER_ITEM; i++) {
			mItems[i].setIndex(i);
			mItems[i].setOnClickListener(this);
			mItems[i].setOnFocusChangeListener(this);	
			mItems[i].setFocusable(false);
			
		}*/
		mControlView.setTitleText(getResources().getString(R.string.settings),true);
		
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemSelectedListener(this);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		freshGridView();
		mGridView.setFocusable(true);
		mGridView.setFocusableInTouchMode(true);
		mGridView.requestFocus();
		//mHandler.sendEmptyMessageDelayed(0x01, 100);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int position = mGridView.getSelectedItemPosition();
		if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if((position+1)%4 == 0) {
				if(position == 11) {
					mGridView.setSelection(0);
				}else {
					mGridView.setSelection(position+1);
				}
			}
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			if((position)%4 == 0) {
				if(position == 0) {
					mGridView.setSelection(11);
				}else {
					mGridView.setSelection(position-1);
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		//setTextColor(UILApplication.RemainDayNum);
		super.onResume();	
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (0x01 == msg.what) {
				if (mGridView.getAdapter().getCount()>=12) {
					setListViewHeightBasedOnChildren(mGridView);
					mAdapter.notifySetChanged();
				}
				else {
					Log.i("","mHandler null");
					mHandler.sendEmptyMessage(0x01);
				}
			}
		};
	};
	
	private void freshGridView(){
		Log.i("","freshGridView");
			if (null == mAdapter) {
				Log.i("freshGridView",""+mAdapter);
				mAdapter = new HomeListAdapter(this, mGridView);	
				setListViewHeightBasedOnChildren(mGridView);
				mGridView.setAdapter(mAdapter);
			}
			
			//mAdapter.notifySetChanged();
	}
	
	public static void setListViewHeightBasedOnChildren(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
		return;
		}
		// 固定列宽，有多少列
		int col = 4;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
		// listAdapter.getCount()小于等于8时计算两次高度相加
		for (int i = 0; i < listAdapter.getCount(); i += col) {
		// 获取listview的每一个item
		View listItem = listAdapter.getView(i, null, listView);
		listItem.measure(0, 0);
		// 获取item的高度和
		totalHeight += listItem.getMeasuredHeight();
		}
		
		// 获取listview的布局参数
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight;
		// 设置margin
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
		}

	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ActivityWifi.class);	
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
//		arg1.setBackgroundResource(R.drawable.home_item_b);
		Log.i("onItemClick",""+mGridView.getSelectedItemPosition());
		Log.i("onItemClick","arg2 "+position );
		if(0 == position ){
			Intent intent = new Intent(this, ActivityWifi.class);	
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		else if(1 == position ){
			Intent intent = new Intent(this, ActivityBluetooth.class);	
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		else if(2 == position ){
			Intent intent = new Intent(this, ActivityBrightness.class);	
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else if(3 == position ){
			startActivity(new Intent(ActivityMain.this,ScreenAdjustActivity.class));
		}else if(4 == position ){
			startActivity(new Intent(ActivityMain.this,ActivityColorTemperature.class));
		}else if(5 == position ){
			startActivity(new Intent(ActivityMain.this,BatteryActivity.class));
		}else if(6 == position ){
			startActivity(new Intent(ActivityMain.this,SDCardActivity.class));
		}
		else if(7 == position ){
			startActivity(new Intent(ActivityMain.this,VolumeActivity.class));
		}
		else if(8 == position ){
			startActivity(new Intent(ActivityMain.this,LanguageKeyboardActivity.class));
		}
		else if(9 == position ){
			startActivity(new Intent(ActivityMain.this,FirmwareUpgradeActivity.class));
		}
		else if(10 == position ){
			startActivity(new Intent(ActivityMain.this,DeviceInfoActivity.class));
		}
		else if(11 == position ){
			startActivity(new Intent(ActivityMain.this,SettingResetActivity.class));
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Log.i("","onItemSelected"+arg1);
		//arg1.setBackgroundResource(R.drawable.home_item_b);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
