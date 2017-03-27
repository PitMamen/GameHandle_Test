package com.app.settingApp.wifi;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.bluetooth.BluetoothItem;
import com.app.settingApp.ui.interfaces.onClickCustomListener;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;
import com.app.settingApp.view.LoadingView;
import com.app.settingApp.wifi.DialogPasswordInput.OnCustomDialogListener;

public class ActivityWifi extends ActivityReceive implements OnClickListener, OnItemClickListener,OnItemLongClickListener, OnItemSelectedListener {
	
	private static final String		TAG = "ActivityWifi";
	private static final int		NUM_PER_WIFI = 10;
	private static final int 		SECURITY_NONE = 0;
	private static final int 		SECURITY_WEP = 1;
	private static final int 		SECURITY_WPA = 2;
	private static final int 		SECURITY_EAP = 3;
	private ListView				mListView;
	private TextView				mNullTxView;
	private CheckBox				mCBoxView;
	private LoadingView 			mLoadingView;
	private ControlView 			mControlView;
	private WifiListAdapter 		mWifiAdapter;
	private RelativeLayout			mSwitchLayoutView;
	private LinearLayout			mWifiNullView;
	private WifiManager 			mWifiManager;  
	private List<ScanResult> 		mWifiScanList;
	private List<WifiConfiguration> mWifiConfigList;  
	private String 					mWifiName;
	DialogPasswordInput				mDialog;
	DialogWifiDelete				mDelDialog;
	private int mPosition;
	private int mType;
	private Context mContext ;
	
	String connectingSSID = "";
	
	final boolean debugScan = false;
	
	BroadcastReceiver mWifiRecevier = null;
	ConnectivityManager connMgr;
	
	//private ArrayList<WifiItem>		mCurWifiList = new ArrayList<WifiItem>();
	private ArrayList<WifiItem>			mWifiList = new ArrayList<WifiItem>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_wifi); 
		connMgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		initView();	
		checkWifiState();
		Log.i(TAG, "mWifiManager.isWifiEnabled()"+mWifiManager.isWifiEnabled());
		if (mWifiManager.isWifiEnabled()) {
			mHandler.sendEmptyMessageDelayed(MSG_SCAN_START, 100);//JUST start scan, and scan result will received by Broadcast..
		}
	}
	
	@Override
	protected void onResume() {		
		super.onResume();	
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//MUST unregister receiver after finish Activity.
		unregisterReceiver(mWifiRecevier);
	}
	
	private void initView() {	
		mControlView = (ControlView) findViewById(R.id.control_view);
		mCBoxView = (CheckBox)findViewById(R.id.wifi_switch);
		mLoadingView = (LoadingView) findViewById(R.id.wifi_search_icon);
		mListView = (ListView) findViewById(R.id.wifi_listview);
		mWifiNullView = (LinearLayout)findViewById(R.id.wifi_null_layout);
		mNullTxView = (TextView)findViewById(R.id.no_wifi_msg);
		mSwitchLayoutView = (RelativeLayout)findViewById(R.id.wifi_control_layout);
		mSwitchLayoutView.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemSelectedListener(this);
		mListView.setOnItemLongClickListener(this);
		mWifiNullView.setVisibility(View.GONE);
		mSwitchLayoutView.setFocusable(true);
		mSwitchLayoutView.setFocusableInTouchMode(true);
		mSwitchLayoutView.requestFocus();
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mControlView.setTitleText(getResources().getString(R.string.wifi),false);
	
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mWifiRecevier = new WifiChangedReceiver();
		registerReceiver(mWifiRecevier, filter);
	}
	
	final int MSG_SCAN_START = 0x01;
	//final int MSG_SCAN_COMPLETE = 0x02;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (MSG_SCAN_START == msg.what) {
				if(debugScan)Log.i(TAG, "start scan wifi access point");
				mWifiManager.startScan();
	        	//we should start scan again to refresh wifi list state after every 10 sec
	        	mHandler.sendEmptyMessageDelayed(MSG_SCAN_START, 10 * 1000);
			}
/*			if (MSG_SCAN_COMPLETE == msg.what) {
				onWifiScanSuccess();
			}*/
		};
	};
	
	void forceScanWifiList(){
		mHandler.removeMessages(MSG_SCAN_START);
		mHandler.sendEmptyMessage(MSG_SCAN_START);
	}
	
	private void checkWifiState(){
		mWifiManager = (WifiManager) getSystemService(mContext.WIFI_SERVICE);
		Log.i(TAG,"checkWifiState "+mWifiManager.getWifiState());
		if(1 == mWifiManager.getWifiState()){
			mLoadingView.setVisibility(View.GONE);
			mListView.setVisibility(View.GONE);mNullTxView.setText(getResources().getString(R.string.wifi_need_open));
			mCBoxView.setChecked(false); 
			mWifiNullView.setVisibility(View.VISIBLE);
		}
		else{mLoadingView.setVisibility(View.VISIBLE);
			mCBoxView.setChecked(true);  
		}
	}
	
	private void onWifiScanSuccess(){	
		mWifiScanList = mWifiManager.getScanResults();
		getConfiguration();
		if (mWifiScanList == null) {  
			if(debugScan)Log.i(TAG,"wifiScanList "+mWifiScanList);
			mNullTxView.setText(getResources().getString(R.string.wifi_scan_null));
			mWifiNullView.setVisibility(View.VISIBLE);
		}else {  
			if(debugScan)Log.i(TAG,"wifiScanList "+mWifiScanList.size());
			scanResultToString();
			freshCurWifiList(0);
			mWifiNullView.setVisibility(View.GONE); 
		}  
	}
	
	public class WifiChangedReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.i(TAG, "onReceive " + intent.getAction());
	        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
	            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
	            Log.i(TAG, "wifiState" + wifiState);
	            switch (wifiState) {
		            case WifiManager.WIFI_STATE_DISABLED:
		                break;
		            case WifiManager.WIFI_STATE_DISABLING:
		                break;
		            case WifiManager.WIFI_STATE_ENABLED:{
		            	Log.i(TAG, "WIFI_STATE_ENABLED");
		            	mHandler.sendEmptyMessageDelayed(MSG_SCAN_START, 1000);
		            	break;
		            }
	            //
	            }
	        }else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
	        	//listener network state change action.
	        	NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
	                    WifiManager.EXTRA_NETWORK_INFO);
	        	if(getConnectedWifiName() != null){
	        		Log.i(TAG,"already connect to " + getConnectedWifiName());
	        	}else{
	        		Log.i(TAG,"no wifi connected !" );
	        	}
	        	connectingSSID = "";
	        	forceScanWifiList();
	        }else if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
	        	//wifi scan finsih...
	        	onWifiScanSuccess();
	        }
	        else if(intent.getAction().equals(WifiManager.NETWORK_IDS_CHANGED_ACTION)){
	        	//listener network state change action.
	        	Log.i(TAG,"NETWORK_IDS_CHANGED_ACTION ");
	        	NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
	                    WifiManager.EXTRA_NETWORK_INFO);
	        }
	        else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
	        	//listener network state change action.
	        	Log.i(TAG,"CONNECTIVITY_ACTION ");
	        	NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(
	                    WifiManager.EXTRA_NETWORK_INFO);
	        }
	  }
	}
	
	/***** open or close WIFI device ****/
	private void openWifi(){
		if (!mWifiManager.isWifiEnabled()) {  
			 mWifiManager.setWifiEnabled(true);  
		 }  
	}
	
	private void closeWifi(){
		if (mWifiManager.isWifiEnabled()) {  
			 mWifiManager.setWifiEnabled(false);  
		 }  
	}
	
	/** refresh WIFI list , all state and singals ... **/
	private void freshCurWifiList(int selectIndex) {// selectIndex for what ????
		Log.i(TAG,"mWifiList sizezzzz "+ mWifiList.size());
		if (null == mWifiAdapter) {
			mWifiAdapter = new WifiListAdapter(this, mListView, mWifiList);	
			mListView.setAdapter(mWifiAdapter);
		}
		//update configurations first
		getConfiguration();
		updateWifiItems();
		//mWifiAdapter.notifySetChanged();
		mWifiAdapter.notifyDataSetChanged();
		mLoadingView.setVisibility(View.GONE);
	}
	
	
	void updateWifiItems(){
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		Log.i(TAG,"updateWifiItems info " + info);
		
		if(info != null && info.getType() == ConnectivityManager.TYPE_WIFI){
			//wifi is connected
			WifiInfo wi = mWifiManager.getConnectionInfo();
			String ssid = wi.getSSID();
			Log.i(TAG,"updateWifiItems WIFI connected " + ssid);
			for(int i = 0; i < mWifiList.size(); i ++){
				WifiItem item = mWifiList.get(i);
				if(item.getSSID().equals(ssid)){
					updateUIforSuccess(i);
					break;
				}
			}
		}
		else{
			Log.i(TAG,"updateWifiItems info == null ");
			for(int j = 0; j<mWifiList.size(); j++){
				WifiConfiguration cDevice = isExsits(mWifiList.get(j).getSSID());
				if(cDevice != null){
					Log.i(TAG,"updateWifiItems cDevice.networkId "+cDevice.networkId);
					/*mWifiManager.removeNetwork(cDevice.networkId);
					//wifiCong = setWifiProxySettings(wifiCong);
					mWifiManager.addNetwork(cDevice);*/
					mWifiManager.enableNetwork(cDevice.networkId, true);
					break;
				}
			}
			/*int netId = IsConfiguration();
			Log.i(TAG,"updateWifiItems netId "+netId);
			if(netId != -1){
				//int netId = mWifiConfigList.get(index).networkId;
				mWifiManager.enableNetwork(netId, true);
			}*/
		}
	}
	
	void updateItemFromConfig(WifiItem item, WifiConfiguration config){
		
	}
		
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}

	/**** ListView Item Events **/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		WifiItem selected = mWifiList.get(arg2);
		mPosition = arg2;
		Log.i(TAG,"selected "+selected.getWifiName());
		Log.i(TAG,"selected "+selected.getSSID());
		int networkId = getNetworkIdFromConfigByName(selected.getSSID());
		if(selected.isConnected() && networkId != -1){
			//show delete dialog
			createDelDialog("Delete wifi " + selected.getWifiName(), networkId);
		}else{
			WifiConfiguration cDevice = isExsits(selected.getSSID());
			if(cDevice != null){
				/*mWifiManager.removeNetwork(cDevice.networkId);
				//wifiCong = setWifiProxySettings(wifiCong);
				int netId = mWifiManager.addNetwork(cDevice);
				Log.i(TAG,"cDevice != null "+netId);
				if(netId != -1){
					mWifiManager.enableNetwork(netId, true);
				}*/
				updateWifiItemState(mPosition, WifiItem.STATE_CONNECTING);
				Log.i(TAG,"cDevice != null "+cDevice.networkId);
				boolean flalg = mWifiManager.enableNetwork(cDevice.networkId, true);
			}else{
				Log.i(TAG,"onItemClick arg2 "+arg2);
				Log.i(TAG,"onItemClick mWifiScanList.size() "+mWifiScanList.size());
				Log.i(TAG,"onItemClick mWifiScanList.size() "+mWifiScanList.get(arg2).capabilities);
				mWifiName = mWifiList.get(arg2).getWifiName();
				if(mWifiScanList.get(arg2).capabilities.contains("WPA")||mWifiScanList.get(arg2).capabilities.contains("wpa")){
					mType = SECURITY_WPA;
					createPsdDialog(mWifiName);
				}
				else if(mWifiScanList.get(arg2).capabilities.contains("WEP")||mWifiScanList.get(arg2).capabilities.contains("wep")){
					mType = SECURITY_WEP;
					createPsdDialog(mWifiName);
				}
				else if(mWifiScanList.get(arg2).capabilities.contains("EAP")||mWifiScanList.get(arg2).capabilities.contains("eap")){
					mType = SECURITY_EAP;
					createPsdDialog(mWifiName);
				}
				else{
					mType = SECURITY_NONE;
					int netId = AddWifiConfig(mWifiScanList,mWifiName);
					if(netId != -1){
						updateWifiItemState(mPosition, WifiItem.STATE_CONNECTING);
						Log.i(TAG, "try to connect " + mWifiName + " for NO password");
						connectingSSID = mWifiName;
						mWifiManager.enableNetwork(netId, true);
					}
				}
			}
		}
	}
		
	public int getNetworkIdFromConfigByName(String name){
		int id = -1;
		for(WifiConfiguration conf : mWifiConfigList){
			if(conf.SSID.equals(name)){
				return conf.networkId;
			}
		}
		return id;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		mPosition = arg2;
		int ret = IsConfiguration();
		if(ret != -1){
			createDelDialog(mWifiName,ret);
		}
		return false;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		/*Log.i(TAG,"onItemSelected"+arg1.isFocused());
		if(arg1.isFocused()){
			arg1.setBackgroundResource(R.drawable.list_bg_b);
		}*/
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	/** Dialog to input password for Wifi AccessPoint  ***/
	private void createPsdDialog(String msg){
		mDialog = new DialogPasswordInput(mContext,msg,new OnCustomDialogListener(){
			@Override
			public void back(String str) {
				String password = str;
				Log.i(TAG, "try to connect to password"  + password);
				if(password != null){
		        	///freshCurWifiList(0);
					int netId = AddWifiConfig(mWifiScanList,mWifiName, password);
					if(netId != -1){
						Log.i(TAG, "try to connect to "  + mWifiName);
						connectingSSID = mWifiName;
						updateWifiItemState(mPosition, WifiItem.STATE_CONNECTING);
						if(mWifiManager.enableNetwork(netId, true)){
							Log.i(TAG, "enableNetwork true");
							int i =WifiConfiguration.Status.DISABLED;
						}
						else{
							Log.i(TAG, "enableNetwork false");
						}
					}
					else{
						Log.i(TAG, "try to connect to fail");
					}
				}else{
					//selectedItem.setBackgroundResource(R.color.burlywood);
				}
			}});
		mDialog.show();
	}
	
	/** update Wifi item in listView **/
	void updateWifiItemState(int pos, int state){
		for(int i = 0;i<mWifiList.size();i++){
			if(mWifiList.get(i).getConnectState() == WifiItem.STATE_CONNECTING){
				mWifiList.get(i).setConnectState(WifiItem.STATE_DISCNNECTED);
			}
		}
		
		mWifiList.get(pos).setConnectState(state);
		mWifiAdapter.notifyDataSetChanged();
	}
	
	/*** delete Wifi Dialog **/
	private void createDelDialog(String msg,final int netId){
		mDelDialog = new DialogWifiDelete(mContext, msg, new onClickCustomListener(){
			@Override
			public void OnClick(View v) {
				switch (v.getId()) {
				case R.id.cancel_btn:{
					mDelDialog.dismiss();
					break;
				}
				case R.id.delete_btn:{
					mDelDialog.dismiss();
					updateWifiItemState(mPosition, WifiItem.STATE_DISCONNECTING);
					if(RemoveWifi(netId)){
						
					}
					break;		
				}
				default:
					break;
			}
			}});
		mDelDialog.show();
	}
	
	/*** 
	* 配置要连接的WIFI热点信息     
	* @param SSID 
	* @param password 
	* @param type  加密类型 
	* @return 
	*/       
	public WifiConfiguration createWifiInfo(String SSID, String password, int type) {    
	     
		Log.e(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);    
          
		WifiConfiguration config = new WifiConfiguration();    
		config.allowedAuthAlgorithms.clear();    
		config.allowedGroupCiphers.clear();    
		config.allowedKeyManagement.clear();    
		config.allowedPairwiseCiphers.clear();    
		config.allowedProtocols.clear();    
		config.SSID = "\"" + SSID + "\"";    
  
		/*//增加热点时候 如果已经存在SSID 则将SSID先删除以防止重复SSID出现  
			WifiConfiguration tempConfig = wifiAdmin.IsExsits(SSID);    
			if (tempConfig != null) {    
			settingWifimin.AdwifiManager.removeNetwork(tempConfig.networkId);     
		} */   
	   
		// 分为三种情况：没有密码   用wep加密  用wpa加密    
		if (type == SECURITY_NONE) {   // WIFICIPHER_NOPASS    
			config.wepKeys[0] = "";    
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);    
			config.wepTxKeyIndex = 0;     
	      
		} else if (type == SECURITY_WEP) {  //  WIFICIPHER_WEP     
			config.hiddenSSID = true;    
			config.wepKeys[0] = "\"" + password + "\"";    
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);    
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);    
			config.wepTxKeyIndex = 0;    
		
		} else if (type == SECURITY_WPA) {   // WIFICIPHER_WPA    
			config.preSharedKey = "\"" + password + "\"";    
			/*config.hiddenSSID = true;    
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);    
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);    
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);     
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);    
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);    
			config.status = WifiConfiguration.Status.ENABLED; */   
			config.hiddenSSID = true;      
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);      
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                            
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                            
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                       
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); 
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;  

		}    
		/*config.proxySettings = ProxySettings.NONE;
		config.ipAssignment = m_ipAssignment;
		config.linkProperties = new LinkProperties(m_linkProperties);*/
		return config;    
	}   

	private int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
		int wifiId = -1;
		for(int i = 0;i < wifiList.size(); i++){
			ScanResult wifi = wifiList.get(i);
			if(wifi.SSID.equals(ssid)){
				Log.i(TAG,"AddWifiConfig wifiManager.getWifiState()"
						+ mWifiManager.getWifiState());
				WifiConfiguration wifiCong = createWifiInfo(ssid,pwd,mType);
				WifiConfiguration tempConfig = this.isExsits(ssid);

				if (tempConfig != null) {
					mWifiManager.removeNetwork(tempConfig.networkId);
				}
				//wifiCong = setWifiProxySettings(wifiCong);
				wifiId = mWifiManager.addNetwork(wifiCong);
				if(wifiId != -1){
					return wifiId;
				}
			}
		}
		return wifiId;
	}
	
	private int AddWifiConfig(List<ScanResult> wifiList,String ssid){
		int wifiId = -1;
		Log.i("AddWifiConfig","ssid"+ssid);
		for(int i = 0;i < wifiList.size(); i++){
			ScanResult wifi = wifiList.get(i);
			Log.i("AddWifiConfig","wifi.SSID "+wifi.SSID);
			
			if(wifi.SSID.equals(ssid)){
				Log.i("AddWifiConfig","equals");
				WifiConfiguration wifiCong = new WifiConfiguration();
				wifiCong.SSID = "\""+wifi.SSID+"\"";
				wifiCong.wepKeys[0] = "\"" + "\"";           
				wifiCong.wepTxKeyIndex = 0;   
				//wifiCong.wepKeys[0] = "";  
				//wifiCong.wepTxKeyIndex = 0;
				//wifiCong.hiddenSSID = false;
				wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				//wifiCong.status = WifiConfiguration.Status.ENABLED;
				wifiId = mWifiManager.addNetwork(wifiCong);
				if(wifiId != -1){
					return wifiId;
				}
			}
		}
		return wifiId;
	}

	private void getConfiguration(){
		mWifiConfigList = mWifiManager.getConfiguredNetworks();
		Log.i(TAG,"getConfiguration mWifiConfigList.size()  "+mWifiConfigList.size());
	}
	
	private int IsConfiguration(){
		int ret = -1;
		boolean flag = true;
		Log.i(TAG,"IsConfiguration  ");
		for(int i = 0; i < mWifiConfigList.size(); i++){
			if(!flag){
				break;
			}
			Log.i(TAG,"IsConfiguration2  "+mWifiConfigList.get(i).SSID);
			for(int j = 0; j < mWifiScanList.size();j++){
				String ssid =  "\"" + mWifiScanList.get(j).SSID + "\""; 
				Log.i(TAG,"IsConfiguration ssid  "+ssid);
				if(mWifiConfigList.get(i).SSID.equals(ssid)){
					mPosition = j;
					Log.i(TAG,"IsConfiguration j  "+j);
					ret =  mWifiConfigList.get(i).networkId;
					Log.i(TAG,"IsConfiguration ret  "+ret);
					flag = false;
					break;
				}
			}
			
		};
		return ret;
	}
	
	private void scanResultToString(){
		mWifiList.clear();
		for(int i = 0; i <mWifiScanList.size(); i++){
			ScanResult strScan = mWifiScanList.get(i);
			if(debugScan)Log.i(TAG,"scanResultToString ssid  "+strScan.SSID);
			WifiItem item = new WifiItem();
			item.setWifiName(strScan.SSID);
			if(strScan.capabilities.contains("WPA")||strScan.capabilities.contains("wpa")
					||strScan.capabilities.contains("WEP")||strScan.capabilities.contains("wep")
					||strScan.capabilities.contains("EAP")||strScan.capabilities.contains("eap")){
				item.setWifiSecured(WifiItem.TYPE_SECURITY);
				item.setWifiLock("LOCK");
			}
			else{
				//for NON security
				item.setWifiSecured(WifiItem.TYPE_NONE);
				item.setWifiLock("");
			}
			
			/** show connecting during update by scan wifi access point **/
			if(connectingSSID.equals(item.getWifiName())){
				item.setConnectState(WifiItem.STATE_CONNECTING);
			}
			
			item.setWifiSignal(strScan.level);
			mWifiList.add(item);
		}
		for(int j = 0; j <mWifiList.size(); j++){
			if(debugScan)Log.i(TAG,"scanResultToString mWifiScanList ssid  "+mWifiScanList.get(j).SSID);
		}
	}

	@Override
	public void onClick(View arg0) {
		Log.i("","mWifiManager.getWifiState()"+mWifiManager.getWifiState());
		if(3 == mWifiManager.getWifiState()){
			closeWifi();
			mLoadingView.setVisibility(View.GONE);
			mCBoxView.setChecked(false);
			mListView.setVisibility(View.GONE);mNullTxView.setText(getResources().getString(R.string.wifi_need_open));
			mWifiNullView.setVisibility(View.VISIBLE);
		}
		else{
			openWifi();
			mWifiList.clear();
			mLoadingView.setVisibility(View.VISIBLE);
			mWifiNullView.setVisibility(View.GONE);
			mCBoxView.setChecked(true);
			mListView.setVisibility(View.VISIBLE);
		}
	}
	
	public String getConnectedWifiName() {
		NetworkInfo mWifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(mWifi.isConnected()){
			return mWifiManager.getConnectionInfo().getSSID();
		}
		return null;
	}

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		Log.i(TAG,"isExsits SSID" + "\"" + SSID + "\"");
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			Log.i(TAG,"isExsits existingConfig.SSID" + existingConfig.SSID);
			if (existingConfig.SSID.equals(SSID)) {
				Log.i(TAG,"isExsits");
				return existingConfig;
			}
		}
		return null;
	}

	private boolean  RemoveWifi(int netId){
		return mWifiManager.removeNetwork(netId);
	}
	
	private void updateUIforSuccess(int pos){
		Log.i(TAG,"updateUIforSuccess " + pos);
		mWifiList.get(pos).setConnectState(WifiItem.STATE_CONNECTED);
		WifiItem item = mWifiList.get(pos);
		mWifiList.remove(pos);
		mWifiList.add(0,item);
		ScanResult sItem = mWifiScanList.get(pos);
		mWifiScanList.remove(pos);
		mWifiScanList.add(0,sItem);
		//freshCurWifiList(0);
	}
	
}