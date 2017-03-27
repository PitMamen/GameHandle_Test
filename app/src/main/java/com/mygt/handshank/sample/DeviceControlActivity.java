/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mygt.handshank.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对于一个BLE设备，该activity向用户提供设备连接，显示数据，显示GATT服务和设备的字符串支持等界面，
 * 另外这个activity还与BluetoothLeService通讯，反过来与Bluetooth LE API进行通讯
 */
public class DeviceControlActivity extends Activity implements View.OnClickListener, BluetoothLeService.Callback{
	private final static String TAG = DeviceControlActivity.class.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	// 连接状态
	private EditText mDataField;
	private String mDeviceName;
	
	private TextView addressView;
//	private String mDeviceAddress;
	
	private List<String> addressList = new ArrayList<String>();
	
	private Set<String> rigAddressList = new HashSet<String>();

	private BluetoothLeService mBluetoothLeService;

	private boolean mConnected = false;

	private BluetoothAdapter mBluetoothAdapter;

//	private String mac = "66:66:66:66:66:66";//"18:7A:E9:34:89:4A"; 

	// 写数据
//	private BluetoothGattCharacteristic writeCharacteristic;
//	private BluetoothGattService mnotyGattService;
	// 读数据
//	private BluetoothGattCharacteristic readCharacteristic;

	// 管理服务的生命周期
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			connectService();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	// Handles various events fired by the Service.处理服务所激发的各种事件
	// ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.从服务中接受数据

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String address = bundle.getString(Constants.EXTRA_ADDRESS);
			switch (msg.what) {
				case BluetoothLeService.ACTION_GATT_CONNECTED:
					mConnected = true;
					addressView.setText(address);
					rigAddressList.add(address);
				case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
					rigAddressList.add(address);
					break;
				case BluetoothLeService.ACTION_GATT_DISCONNECTED:
					mConnected = false;
					rigAddressList.remove(address);
					// updateConnectionState(R.string.disconnected);
					clearUI();
					break;
				case BluetoothLeService.ACTION_DATA_AVAILABLE:
					// 将数据显示在mDataField上
					byte[] data = bundle.getByteArray(Constants.EXTRA_DATA);
					String dataStr = Byte2six.Bytes2HexString(data);
					Log.d(TAG, "data----" + dataStr+",address---"+address);
					displayData(dataStr);
					break;
			}
		}
	};

	@Override
	public void onDispatchData(int type, byte[] data, String address) {
		Log.d("测试次数DeviceControl", "onDispatchData.........");
		Message message = handler.obtainMessage(type);
		Bundle bundle = new Bundle();
		bundle.putByteArray(Constants.EXTRA_DATA, data);
		bundle.putString(Constants.EXTRA_ADDRESS, address);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.d(TAG, "action:" + action);
			String address;
			if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				address = device.getAddress();
				rigAddressList.add(address);
				Log.d(TAG, "devices:"+device.getName()+"address:"+address);
			}
		}
	};

	private void clearUI() {
		// mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
		// mDataField.setText(R.string.no_data);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //获取已经保存过的设备信息  
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice bluetoothDevice : devices) {
			  Log.d(TAG, "bond:"+bluetoothDevice.getBondState());

			  /*if(mac.equalsIgnoreCase(bluetoothDevice.getAddress())) {
				  mDeviceName = bluetoothDevice.getName();
				  mDeviceAddress = bluetoothDevice.getAddress();
			  }*/
			  addressList.add(bluetoothDevice.getAddress());
			  Log.d(TAG, "devices："+bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress());
		}

        // Sets up UI references.
        addressView = (TextView) findViewById(R.id.device_address);
        mDataField =  (EditText) findViewById(R.id.data_value);
        
        findViewById(R.id.change_mode).setOnClickListener(this);
		findViewById(R.id.mada).setOnClickListener(this);
       
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

	@Override
	protected void onResume() {
		super.onResume();
		register();
		connectService();
	}
	
	private void connectService() {
		if (mBluetoothLeService != null) {
			mBluetoothLeService.registerCallback(this);
			for (String address : addressList) {
				final boolean result = mBluetoothLeService.connect(address);
				Log.d(TAG, "Connect request result=" + result+",address:"+address);
				if(result) {
					rigAddressList.add(address);
//					addressView.setText(address);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBluetoothLeService.unRegisterCallback(this);
		unRegister();
	}

	boolean isRegister = false;
	private void register() {
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		isRegister = true;
	}

	private void unRegister() {
		if (isRegister) {
			unregisterReceiver(mGattUpdateReceiver);
			isRegister = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegister();
		if (mBluetoothLeService != null) {
			mBluetoothLeService.unRegisterCallback(this);
		}
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}

	/**
	 * private void updateConnectionState(final int resourceId) {
	 * runOnUiThread(new Runnable() {
	 * 
	 * public void run() { mConnectionState.setText(resourceId); } });
	 * }
	 */
	private void displayData(String data) {
		if (data != null) {
			mDataField.setText(data);
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		//system
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		return intentFilter;
	}

	@Override
	public void onClick(View v) {
		byte[] writeBytes = new byte[20];
		switch (v.getId()) {
			case R.id.change_mode:
				writeBytes[0] = 0x20;
				writeBytes[1] = 0x04;
				writeBytes[2] = 0x08;
				writeBytes[3] = 0x01;
				mBluetoothLeService.writeCharacteristic(writeBytes);
				break;
			case R.id.mada:
//				writeBytes[0] = 0x20;
//				writeBytes[1] = 0x05;
//				writeBytes[2] = 0x02;
//				writeBytes[3] = (byte) 0xff;
				writeBytes[0] = 0x20;
				writeBytes[1] = 0x03;
				writeBytes[2] = 0x09;
				mBluetoothLeService.writeCharacteristic(writeBytes);
				break;
		}
	}
}
