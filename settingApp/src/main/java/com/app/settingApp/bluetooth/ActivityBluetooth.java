package com.app.settingApp.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;

import com.app.settingApp.ui.interfaces.onClickCustomListener;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;
import com.app.settingApp.view.LoadingView;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ClipData.Item;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class ActivityBluetooth<BluetoothReceiver> extends ActivityReceive implements OnClickListener, OnItemClickListener, OnItemSelectedListener {
	
	private static final String		TAG = "ActivityBluetooth";
	private static final String 	SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";  
	private ListView				mListView;
	private TextView				mNullTxView;
	private CheckBox				mCBoxView;
	private LoadingView 			mLoadingView;
	private ControlView 			mControlView;
	private RelativeLayout			mSwitchLayoutView;
	private BluetoothListAdapter 	mBluetoothListAdapter;
	private LinearLayout			mBluetoothNullView;
	private BluetoothAdapter 		mBluetoothAdapter = null;
//	private BluetoothReceiver 		mBluetoothReceiver = null; 
	private BluetoothHeadset 		mBluetoothHeadset=null;
	private	BluetoothA2dp			mBluetoothA2dp=null;
	private DialogBluetoothConnect 	mConnectDialog;

	//private InputStream mInputSream;
	private Context mContext ;
	private int mPosition;
	private Boolean mProfile = true;
	private BluetoothDevice mConnectedDevice;
	private ArrayList<BluetoothItem>	mBluetoothList = new ArrayList<BluetoothItem>();
	private ArrayList<BluetoothDevice>	mDeviceList = new ArrayList<BluetoothDevice>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_bluetooth); 
		initView();	
		getBluetooth();
		if(mBluetoothAdapter.isEnabled()){
			registerBleutoothStateReceiver();
			mHandler.sendEmptyMessageDelayed(MSG_BLUETOOTH_INITED, 100);
		}
		//mListView.requestFocus();
	}
	
	@Override
	protected void onResume() {		
		super.onResume();	
	}
	
	@Override  
	protected void onDestroy() {  
		this.unregisterReceiver(mBluetoothReceiver);  
		//do NOT forget close proxy when activity exit.
		if(mBluetoothA2dp != null)mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2dp);
		if(mBluetoothHeadset != null)mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
		super.onDestroy();  
	}  

	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub	
		super.onPostCreate(savedInstanceState);
	}
	
	private void initView() {	
		mControlView = (ControlView) findViewById(R.id.control_view);
		mCBoxView = (CheckBox)findViewById(R.id.bluetooth_switch);
		mLoadingView = (LoadingView) findViewById(R.id.bluetooth_search_icon);
		mListView = (ListView) findViewById(R.id.bluetooth_listview);
		mNullTxView = (TextView)findViewById(R.id.no_bluetooth_msg);
		mBluetoothNullView = (LinearLayout)findViewById(R.id.bluetooth_null_layout);
		mSwitchLayoutView = (RelativeLayout)findViewById(R.id.bluetooth_control_layout);
		
		mSwitchLayoutView.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		//mListView.setOnItemLongClickListener(bluetoothItemLongClickListener);
		//mListView.setOnItemLongClickListener(this);
		mListView.setOnItemSelectedListener(this);
		mBluetoothNullView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.GONE);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mSwitchLayoutView.setFocusable(true);
		mSwitchLayoutView.setFocusableInTouchMode(true);
		mSwitchLayoutView.requestFocus();
		
		mControlView.setTitleText(getResources().getString(R.string.bluetooth),false);
	}
	
	
	final int MSG_BLUETOOTH_DISCOVERY_START = 0x04;
	final int MSG_BLUETOOTH_DISCOVERY_FINISH = 0x05;
	final int MSG_BLUETOOTH_INITED = 0x03;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (0x01 == msg.what) {//NO use

				int index = msg.arg1;
				mBluetoothList.get(index).setBluetoothPaired(
						BluetoothItem.STATE_PAIRED);
				BluetoothItem item = mBluetoothList.get(index);
				mBluetoothList.remove(index);
				mDeviceList.remove(index);
				mBluetoothList.add(0, item);
				freshCurBluetoothList(0);
			} else if (0x02 == msg.what) {//NO use
				getDevice();
			} else if (MSG_BLUETOOTH_INITED == msg.what) {
				if (mBluetoothAdapter.isDiscovering()) {
					Log.i(TAG, "init bluetooth cancelDiscovery");
					mBluetoothAdapter.cancelDiscovery();
				}
				// 获得已配对的远程蓝牙设备的集合
				Set<BluetoothDevice> devices = mBluetoothAdapter
						.getBondedDevices();
				Log.i(TAG, "BondedDevice size = " + devices.size());
				if (devices.size() > 0) {
					for (Iterator<BluetoothDevice> it = devices.iterator(); it
							.hasNext();) {
						BluetoothDevice device = (BluetoothDevice) it.next();
						// 打印出远程蓝牙设备的物理地址
						/*BluetoothItem item = BluetoothItem
								.getItemFromDevice(device);
						
						 * new BluetoothItem();
						 * if(TextUtils.isEmpty(device.getName())){
						 * item.setBluetoothName(device.getAddress()); } else{
						 * item.setBluetoothName(device.getName()); }
						 * item.setBluetoothAddress(device.getAddress());
						 * item.setBluetoothType(device.getType());
						 * item.setBluetoothPaired(device.getBondState());
						 * item.setBluetoothClass(device.getClass());
						 */
						//mBluetoothList.add(item);
						//mDeviceList.add(device);
						// freshCurBluetoothList(0);
						checkIfNeedAddDeviceToList(device);
						updateBluetoothItemState(device, device.getBondState());
					}
				} else {
					Log.i(TAG, "还没有已配对的远程蓝牙设备！");
				}
				
				//get profile after bluetooth was enabled
				mBluetoothAdapter.getProfileProxy(ActivityBluetooth.this, mProfileServiceListener, BluetoothProfile.A2DP);
				mBluetoothAdapter.getProfileProxy(ActivityBluetooth.this, mHeadsetProfileServiceListener, BluetoothProfile.HEADSET);

				// /startScanThread();
				startDiscovery(false);
				//
			} else if (MSG_BLUETOOTH_DISCOVERY_START == msg.what) {
				startDiscovery(true);
			} else if (MSG_BLUETOOTH_DISCOVERY_FINISH == msg.what) {
				Log.i(TAG, "discovery finished, should restart ??");
			}
		}
	};

	private void getBluetooth() {
		// 得到本机蓝牙设备
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 创建一个IntentFilter对象,将其action指定为BluetoothDevice.ACTION_FOUND
		// IntentFilter它是一个过滤器,只有符合过滤器的Intent才会被我们的BluetoothReceiver所接收
		// Log.i(TAG,"getBluetooth()"+mBluetoothAdapter);
		if (mBluetoothAdapter != null) {
			Log.i(TAG, "getBluetooth()" + mBluetoothAdapter.isEnabled());

			if (mBluetoothAdapter.isEnabled()) {
				mCBoxView.setChecked(true);
			} else {
				mLoadingView.setVisibility(View.GONE);
				mNullTxView.setText(getResources().getString(R.string.bluetooth_need_open));
				mCBoxView.setChecked(false);
			}

		} else {
			Log.i(TAG, "could NOT find BluetoothAdapter");
		}
	}

	private BluetoothProfile.ServiceListener mHeadsetProfileServiceListener = new BluetoothProfile.ServiceListener() {
		
		@Override
		public void onServiceDisconnected(int arg0) {
			// TODO Auto-generated method stub
			mBluetoothHeadset = null;
		}
		
		@Override
		public void onServiceConnected(int arg0, BluetoothProfile profile) {
			// TODO Auto-generated method stub
			mBluetoothHeadset = (BluetoothHeadset)profile;
		}
	};
	private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {

		@Override
		public void onServiceConnected(int arg0, BluetoothProfile arg1) {
			Log.i(TAG, "onServiceConnected()");
			mBluetoothA2dp = (BluetoothA2dp) arg1;

			Log.i(TAG, "onServiceConnected()arg0" + arg0);
			Log.i(TAG, "onServiceConnected()arg0" + mProfile);
			Log.i(TAG,
					"onServiceConnected()mBluetoothList"
							+ mBluetoothList.size());
			if (mProfile) {
				List<BluetoothDevice> list = arg1.getConnectedDevices();
				Log.i(TAG, "getConnectedDevices list size = " + list.size());
				if (list.size() > 0) {
					for (Iterator<BluetoothDevice> it = list.iterator(); it.hasNext();) {
						BluetoothDevice device = (BluetoothDevice) it.next();
						// 打印出远程蓝牙设备的物理地址
						Log.i(TAG, "onServiceConnected()device" + device.getName());
						/*BluetoothItem item = new BluetoothItem();
						if (TextUtils.isEmpty(device.getName())) {
							item.setBluetoothName(device.getAddress());
						} else {
							item.setBluetoothName(device.getName());
						}
						item.setBluetoothAddress(device.getAddress());
						item.setBluetoothType(1);
						item.setBluetoothPaired(BluetoothItem.STATE_CONNECTED);
						item.setBluetoothClass(device.getClass());
						mBluetoothList.add(item);
						mDeviceList.add(device);*/
						updateBluetoothItemState(device, BluetoothItem.STATE_CONNECTED);
					}
					return;
				} else {
					Log.i(TAG, "还没有已配对的远程蓝牙设备！");
				}

			}
		}

		@Override
		public void onServiceDisconnected(int arg0) {
			// TODO Auto-generated method stub
			mBluetoothA2dp = null;
		}
	};

	private void getDevice(){
		if(!mBluetoothAdapter.isEnabled()){
       	 	//Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
            //请求开启蓝牙设备  
       	 	//startActivityForResult(intent,0); 
			mBluetoothAdapter.enable();
		}
		//mHandler.sendEmptyMessageDelayed(0x03, 3000);
	}
	
	void registerBleutoothStateReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        //创建一个BluetoothReceiver对象

        //for discovery .
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND); 
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        
        //for connection state
        /*intentFilter.addAction(InputDevice_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(Pan_ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(Map_ACTION_CONNECTION_STATE_CHANGED);*/
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        //for pair
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_CANCEL");
        //for state
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST"); 
        //mBluetoothReceiver = new BluetoothReceiver();

        //注册广播接收器 注册完后每次发送广播后，BluetoothReceiver就可以接收到这个广播了
        registerReceiver(mBluetoothReceiver, intentFilter);
	}
	
	//接收广播
	BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			Log.i(TAG, "onReceive " + action);
			//Log.i(TAG, "BluetoothReceiverSDSDSDSDSDSDSDSDS action "+action); 
	        if(BluetoothDevice.ACTION_FOUND.equals(action)){    
	        	//只要BluetoothReceiver接收到来自于系统的广播,这个广播是什么呢,是我找到了一个远程蓝牙设备
			    //Intent代表刚刚发现远程蓝牙设备适配器的对象,可以从收到的Intent对象取出一些信息
			    BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    Log.i(TAG,"fonud device : " + device.getName() + "(" + device.getAddress() + ")"); 
			    Log.i(TAG,"device.getType "+device.getType());
			    checkIfNeedAddDeviceToList(device);
			    freshCurBluetoothList(0);
	        }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mLoadingView.setVisibility(View.GONE);
                if(mBluetoothList.size() == 0){
                	mNullTxView.setText(getResources().getString(R.string.bluetooth_scan_null));
                	mBluetoothNullView.setVisibility(View.VISIBLE);
                }
                
            }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                mLoadingView.setVisibility(View.VISIBLE);
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){  
	        	BluetoothDevice  device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
	        	//maybe bounding device during activiy was starting ....
	        	//TODO check should insert device to mBluetoothList..
	        	checkIfNeedAddDeviceToList(device);
	        	switch (device.getBondState()) {  
	        	case BluetoothDevice.BOND_BONDING://11
	        		mBluetoothList.get(mPosition).setBluetoothPaired(BluetoothItem.STATE_PARING);
		        	freshCurBluetoothList(0);
	        		Log.i("BlueToothTestActivity", "正在配对......");  
	        		break;  
	        	case BluetoothDevice.BOND_BONDED://12
	        		updateBluetoothItemState(device, BluetoothItem.STATE_PAIRED);
	        		Log.i("BlueToothTestActivity", "完成配对"); 
	        		/** device paired and then connect ... **/
	        		connectDevice(device);
	        		break;  
	        	case BluetoothDevice.BOND_NONE://10
	        		Log.i(TAG, "取消配对mPosition"+mPosition); 
	        		updateBluetoothItemState(device, BluetoothItem.STATE_NONE);
	        		Log.i(TAG, "取消配对");  
	        	default:  
	        		break;  
	        	}  
	        }else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
	        	Log.i(TAG,"android.bluetooth.device.action.PAIRING_REQUEST22222222222222222222222222");
	        	mBluetoothList.get(mPosition).setBluetoothPaired(BluetoothItem.STATE_PARING);
	        	freshCurBluetoothList(0);
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED == action){
	        	String stateExtra = BluetoothAdapter.EXTRA_STATE;
	            int state = arg1.getIntExtra(stateExtra, -1);
	            Log.i(TAG,"BluetoothAdapter.ACTION_STATE_CHANGED " +  state);
	            switch(state) {
	            	case BluetoothAdapter.STATE_TURNING_ON://11
	            		break;
	            	case BluetoothAdapter.STATE_ON://12
	            		mHandler.sendEmptyMessage(MSG_BLUETOOTH_INITED);
	            		break;
	            	case BluetoothAdapter.STATE_TURNING_OFF://13
	            		if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
	            			mBluetoothAdapter.cancelDiscovery();
	            		}
	            		break;
	            	case BluetoothAdapter.STATE_OFF://10
	            		break;
	            	default:  
		        		break;  
	            }

	        }else if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)){
	        	BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	int state = arg1.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                        BluetoothAdapter.STATE_DISCONNECTED);
	        	Log.i(TAG, "CONNECTION_STATE = " + state);
	        	if(state == BluetoothAdapter.STATE_CONNECTED){
	        		state = BluetoothItem.STATE_CONNECTED;
	        	}else if(state == BluetoothAdapter.STATE_CONNECTING){
	        		state = BluetoothItem.STATE_CONNECTING;
	        	}else{
	        		state = device.getBondState();
	        	}
	        	//if item not found, added.
	        	checkIfNeedAddDeviceToList(device);
                updateBluetoothItemState(device, state);
	        }
		}
	};
	
	/**
	 * update device state
	 * @param device BluetoothDevice
	 * @param state new state of BluetoothItem
	 */
	void updateBluetoothItemState(BluetoothDevice device, int state){
		if(device == null || state == -1){
			Log.i(TAG, "updateBluetoothItemState _device == null || state = " + state + "!!!");
			return;
		}
		//find bluetooth item
		BluetoothItem item = checkIfNeedAddDeviceToList(device);
		
		if(item != null){
			Log.i(TAG, "BluetoothItem found and update state to " + BluetoothItem.getStateString(state));
			if(DeviceType(device)){
				item.setBluetoothType(0);
			}
			item.setBluetoothPaired(state);
			freshCurBluetoothList(0);
		}else{
			Log.i(TAG, "could NOT find Bluetooth for device " + device.getName());
		}
	}
	
	/**
	 * find bluetooth item form list.
	 * if NOT found, add it.
	 * @param device BluetoothDevice
	 * @return BluetoothItem
	 */
	BluetoothItem checkIfNeedAddDeviceToList(BluetoothDevice device){
		BluetoothItem item = isExists(device);
		if(item != null){
			Log.i(TAG, "checkIfNeedAddDeviceToList item already EXISTS");
			return item;
		}
		item = getItemFromDevice(device);		
		//not found, MUST add to list.
		mDeviceList.add(device);
		mBluetoothList.add(item);
		return item;
	}

	/**
	 * covert BleutoothDevice to BluetoothItem.
	 * @param device bluetooth device
	 * @return BluetoothItem.
	 */
	public BluetoothItem getItemFromDevice(BluetoothDevice device){
		if(device != null){
        //打印出远程蓝牙设备的物理地址  
        BluetoothItem item = new BluetoothItem();
	        if(TextUtils.isEmpty(device.getName())){
	        	item.setBluetoothName(device.getAddress());
			}
			else{
				item.setBluetoothName(device.getName());
			}
	        if(DeviceType(device)){
	        	item.setBluetoothType(0);
	        }
	        else{
	        	item.setBluetoothType(device.getType());
	        }
	        item.setBluetoothAddress(device.getAddress());
	        item.setBluetoothPaired(device.getBondState());
	        item.setBluetoothClass(device.getClass());
	        return item;
		}
        return null;
	}
	
	private void  freshCurBluetoothList(int selectIndex) {//selectIndex for what ??
		if (null == mBluetoothListAdapter) {
			mBluetoothListAdapter = new BluetoothListAdapter(this, mListView, mBluetoothList);	
			mListView.setAdapter(mBluetoothListAdapter);
		}
		mBluetoothListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {	
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		Log.i(TAG,"onItemClick arg2 "+pos);
		Log.i(TAG,"onItemClick "+mBluetoothList.get(pos).getBluetoothPaired());
		Log.i(TAG,"onItemClickgetBluetoothClass "+mDeviceList.get(pos).getBluetoothClass().getDeviceClass());
		Log.i(TAG,"onItemClickgetgetType "+mDeviceList.get(pos).getType());
		mPosition = pos;
		if (mBluetoothAdapter.isDiscovering()){  
			Log.i(TAG, "befor pair cancelDiscovery");
			mBluetoothAdapter.cancelDiscovery();
		}
		
		//real handle item click
		//if device paired already, just go to show option dialog
		if(BluetoothItem.STATE_NONE != mDeviceList.get(pos).getBondState()){//已配对
			BluetoothItem item = mBluetoothList.get(pos);
			createConnectDialog(mDeviceList.get(pos).getName(),item.getBluetoothPaired());
		}else{
			//device is unpair and disconnected, try pair and then connect it.
			BluetoothDevice btDevice = mDeviceList.get(pos);
			Log.i(TAG, "try pair to device " + btDevice.getName());
			try {
				btDevice.createBond();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void createConnectDialog(String name,int type){
		mConnectDialog = new DialogBluetoothConnect(mContext,name,type, new onClickCustomListener(){

			@Override
			public void OnClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
					case R.id.unpair_btn:{
						mConnectDialog.dismiss();
						unpairDevice();	
						break;
					}
					case R.id.connect_btn:{	
						mConnectDialog.dismiss();
						if(v.getTag() != null && ((Integer)v.getTag()) == BluetoothItem.STATE_CONNECTED){
							disconnectDevice();
						}else{
							//device disconnected, go connect.
							connectDevice(mDeviceList.get(mPosition));
						}
						break;
					}
					default:
						break;
				}
			}

		});
		mConnectDialog.show();
	}
	
	/**
	 * for delete:
	 * BluetoothDevicePreference.java
	 * |-- onClick()
	 * |-- askDisconnect()
	 * CachedBluetoothDevice.java
	 * |-- disconnect.
	 * LocalBluetoothProfile.java
	 * BluetoothA2dp.disconnect.
	 */
	boolean debugDisconnect = true;
	/** DISCONNECT device **/
	void disconnectDevice(){
		BluetoothDevice device = mDeviceList.get(mPosition);
		if(mBluetoothHeadset != null){
			disconnectHeadset(device);
		}
		if(mBluetoothA2dp != null){
			disconnectA2dp(device);
		}
	}
	boolean disconnectHeadset(BluetoothDevice device){
		if(debugDisconnect)Log.i(TAG, "start reflect class of BluetoothHeadset");
		Class CHeadset = BluetoothHeadset.class;
		try {
			Method Mdisconnect = CHeadset.getDeclaredMethod("disconnect", BluetoothDevice.class);
			if(Mdisconnect != null){
				if(debugDisconnect)Log.i(TAG, "reflect disconnect method success");
				Boolean result = (Boolean)Mdisconnect.invoke(mBluetoothHeadset, device);
				if(debugDisconnect)Log.i(TAG, "call disconnect method " + (result ? "success":"failed"));
				return result;
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	boolean disconnectA2dp(BluetoothDevice device){
		if(debugDisconnect)Log.i(TAG, "start reflect class of BluetoothA2dp");
		Class CA2dp = BluetoothA2dp.class;
		try {
			Method Mdisconnect = CA2dp.getDeclaredMethod("disconnect", BluetoothDevice.class);
			if(Mdisconnect != null){
				if(debugDisconnect)Log.i(TAG, "reflect disconnect method success");
				Boolean result = (Boolean)Mdisconnect.invoke(mBluetoothA2dp, device);
				if(debugDisconnect)Log.i(TAG, "call disconnect method " + (result ? "success":"failed"));
				return result;
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	boolean debugConnect = true;
	/** connect Bluetooth device **/
	boolean connectDevice(BluetoothDevice device){
		if(device == null){
			Log.i(TAG,"connectDevice device is null");
			return false;
		}
		
		if(debugConnect)Log.i(TAG, "try connect to " + device.getName());
		boolean result = false;
		try {
			result = (Boolean)BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class)
					.invoke(mBluetoothA2dp, device);
			Log.i(TAG, "connect to " + device.getName() + (result ? " success": " failed"));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	boolean debugUnpair = true;
	/** UNPAIR device **/
	void unpairDevice(){
		BluetoothDevice device = mDeviceList.get(mPosition);
		if(device == null){
			Log.i(TAG, "unpair devcie failed, could NOT find device");
		}else{
			try {
				if(debugUnpair)Log.i(TAG, "try to unbond for " + device.getName());
				boolean result = false;
				if(device.getBondState() == BluetoothDevice.BOND_BONDING){
					if(debugUnpair)Log.i(TAG, "unpair bonding device");
					cancelBondProcess(BluetoothDevice.class, device);
				}else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
					if(debugUnpair)Log.i(TAG, "unpair bonded device");
					result = removeBond(BluetoothDevice.class, device);
				}
				if(debugUnpair)Log.i(TAG, "unpair device " + device.getName() + (result ? " success" : " failed"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		//Log.i(TAG,"mBluetoothAdapter.isEnabled()"+mBluetoothAdapter.isEnabled());
		if(mBluetoothAdapter.isEnabled()){
			Log.i(TAG, "disable bluetooth");
			mBluetoothAdapter.disable();
			mProfile = false;
			mCBoxView.setChecked(false);
			mListView.setVisibility(View.GONE);
			mNullTxView.setText(getResources().getString(R.string.bluetooth_need_open));
			mBluetoothNullView.setVisibility(View.VISIBLE);
		}else{
			Log.i(TAG, "enable bluetooth");
			//mHandler.sendEmptyMessageDelayed(0x02, 100);
			mBluetoothAdapter.enable();
			registerBleutoothStateReceiver();
			mHandler.sendEmptyMessageDelayed(MSG_BLUETOOTH_INITED, 100);
			mBluetoothList.clear();
			mDeviceList.clear();
			mProfile = true;
			mListView.setVisibility(View.VISIBLE);
			getDevice();
			
			mBluetoothNullView.setVisibility(View.GONE);
			mCBoxView.setChecked(true);
		}
	}
	

	private boolean createBond(Class bclass, BluetoothDevice device) 
            throws Exception { 
		Boolean flag = true;
		
		if (device.getBondState() == BluetoothDevice.BOND_NONE) {  
			Method createBondMethod = bclass.getMethod("createBond"); 
			Boolean returnValue = (Boolean) createBondMethod.invoke(device); 
			flag =  returnValue.booleanValue();
			/*if(flag){
				connect(device);
			}*/
		}
		else{
			//connect(device);
		}
		return flag;
    } 

	private boolean removeBond(Class blass, BluetoothDevice device) 
			throws Exception { 
    	Method removeBondMethod = blass.getDeclaredMethod("removeBond"); 
    	Boolean returnValue = (Boolean) removeBondMethod.invoke(device); 
    	return returnValue.booleanValue(); 
    } 
    
    // 取消配对  
    private boolean cancelBondProcess(Class bClass,  
            BluetoothDevice device)  throws Exception  
    {  
        Method createBondMethod = bClass.getMethod("cancelBondProcess");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);  
        return returnValue.booleanValue();  
    }  
    
    /**
     * start discovery bluetooth devices
     * @param force
     */
    void startDiscovery(boolean force){
    	Log.i(TAG, "startDiscovery force=" + force);
    	if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
    		Log.i(TAG, "bluetooth already discovering");
    		if(!force){
    			return;
    		}else{
    			Log.i(TAG, "startDiscovery restart cancelDiscovery");
    			mBluetoothAdapter.cancelDiscovery();
    		}
    	}
    	mBluetoothAdapter.startDiscovery();  	
    }
 
    private Boolean DeviceType(BluetoothDevice device){
    	Boolean dRet = false;
    	if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE	||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX  ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VCR ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER  ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING ||
    			device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY){
    		dRet = true;
    	}
    	return dRet;
    }
  
    private BluetoothItem isExists(BluetoothDevice device){
    	for(int i = 0;i<mBluetoothList.size();i++){
    		if(device.getAddress().equals(mBluetoothList.get(i).getBluetoothAddress())){
    			return mBluetoothList.get(i); 
    		}
    	}
    	return null;
    }
}


