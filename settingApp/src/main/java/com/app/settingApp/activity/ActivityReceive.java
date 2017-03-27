package com.app.settingApp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

public class ActivityReceive extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

	    IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    registerReceiver(mBroadcastReceiver, intentFilter);
	 }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}

	 protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		 @Override
	     public void onReceive(Context context, Intent intent) 
	     {
			 ActivityReceive.this.onReceive(intent);
		 }
	 };
	 
	 protected void onReceive(Intent intent) {
	 }
}
