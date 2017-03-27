package com.app.settingApp.wifi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.app.settingApp.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

public class WifiUtil {
	
	private static final int		NUM_PER_WIFI = 10;
	private static final int 		SECURITY_NONE = 0;
	private static final int 		SECURITY_WEP = 1;
	private static final int 		SECURITY_WPA = 2;
	private static final int 		SECURITY_EAP = 3;
	
	public interface WifiListener {
		
		public void scanFinish(List<WifiItem> list);
		
		public void onConnectSucce(List<WifiItem> list, int succePosition);
	}

	private static final String TAG = "WifiUtil";
	
	private WifiManager 			mWifiManager;  
	private List<ScanResult> 		mWifiScanList;
	private List<WifiConfiguration> mWifiConfigList;  
	private String 					mWifiName;
	
	private List<WifiItem>		mWifiList = new ArrayList<WifiItem>();
	WifiListener mListener;
	
	private Context mContext;
	
	public WifiUtil(Context context, WifiListener listener) {
		this.mContext = context;
		this.mListener = listener;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	public int checkWifiState(){
		
		Log.i(TAG,"checkWifiState "+mWifiManager.getWifiState());
		return mWifiManager.getWifiState();
	}
	
	public WifiManager getWifiManager() {
		return mWifiManager;
	}
	
	public boolean startScan() {
		return mWifiManager.startScan();
	}
	
	public void scanWifi(){
		/*mWifiManager.startScan();
		while(mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
			
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		mWifiScanList = mWifiManager.getScanResults();
		 
		if (mWifiScanList == null) {  
			Log.i(TAG,"wifiScanList "+mWifiScanList);
//			mWifiNullView.setVisibility(View.VISIBLE);
		}else {  
			Log.i(TAG,"wifiScanList "+mWifiScanList.size());
			scanResultToString();
			mListener.scanFinish(mWifiList);
			getConfiguration();
//			if(mWifiManager.getConnectionInfo() == null) {
			autoConnect();
//			}
//			freshCurWifiList(0);
//			mWifiNullView.setVisibility(View.GONE);
			//mGridView.setAdapter(new WifiListAdapter(this,mGridView,list));  
		}  
	}
	
	public void notifyDataSetChanged() {
		mWifiScanList = mWifiManager.getScanResults();
        mWifiList = scanResultToString();
        mListener.scanFinish(mWifiList);
	}
	
	public int findPosition(String SSID) {
		for(int i = 0;i<mWifiScanList.size();i++) {
          	 
			if(mWifiScanList.get(i).SSID.equals(SSID.replace("\"", ""))) {
				return i;
			}
			
		}
		return -1;
	}
	
	public int findNetID (int position) {
		getConfiguration();
		String SSID = mWifiScanList.get(position).SSID;
		for(int i = 0; i<mWifiConfigList.size(); i++) {
			 
			if(SSID.equals(mWifiConfigList.get(i).SSID.replace("\"", ""))) {
				return mWifiConfigList.get(i).networkId;
			}
		}
		return -1;
	}
	
	public void checkConnect(int netId, int mPosition) {
		
		//Log.i(TAG,"ConnectWifi wifiId1："+netId+"__"+String.valueOf(mWifiConfigList.get(netId).status));//status:0--已经连接，1--不可连接，2--可以连接
		if(ConnectWifi(netId)){
			for(int i = 0; i < mWifiList.size();i++){
				if(mWifiList.get(i).getWifiSecured().equals("CON")){
					if(mWifiList.get(i).getWifiLock().equals("LOCK")){
						mWifiList.get(i).setWifiSecured("SEC");
					}else{
						mWifiList.get(i).setWifiSecured("");
					}
				}
			}
			
//			updateUIforSuccess();
		}
		mListener.onConnectSucce(mWifiList, mPosition);
	}
	
	private void autoConnect() {
		
		int netId = -1;
		Log.i(TAG,"wifiScanList "+mWifiList.size());
		Log.i(TAG,"wifiScanList "+mWifiConfigList.size());
		int mPosition = 0;
		for(int i = 0;i<mWifiConfigList.size();i++) {
 
			int ret = IsConfiguration(mWifiConfigList.get(i).SSID);
			if(-1 != ret) {
				Log.i(TAG,"i "+i);
				mPosition = ret;
				netId = mWifiConfigList.get(i).networkId;
				break;
			}
		}
		// int netId =  IsConfiguration();
		Log.i(TAG,"netId "+netId);
		if(netId != -1 ) {
			if(ConnectWifi(netId)){
				mListener.onConnectSucce(mWifiList, mPosition);
			}
		}
	}
	
	public List<WifiItem> exchangeConnect(int position) {
		mWifiList.get(position).setWifiSecured("CON");
		WifiItem item = mWifiList.get(position);
		mWifiList.remove(position);
		mWifiList.add(0,item);
		ScanResult sItem = mWifiScanList.get(position);
		mWifiScanList.remove(position);
		mWifiScanList.add(0,sItem);
		return mWifiList;
	}
	
	public void openWifi(){
		if (!mWifiManager.isWifiEnabled()) {  
			 mWifiManager.setWifiEnabled(true);  
		 }  
	}
	
	public void closeWifi(){
		if (mWifiManager.isWifiEnabled()) {  
			 mWifiManager.setWifiEnabled(false);  
		 }  
	}
	
	public boolean isWifiEnabled() {
		return mWifiManager.isWifiEnabled();
	}
	
	
	
	public int AddWifiConfig(String ssid){
		int wifiId = -1;
		Log.i("AddWifiConfig","ssid"+ssid);
		for(int i = 0;i < mWifiScanList.size(); i++){
			ScanResult wifi = mWifiScanList.get(i);
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
	
	public int getCapabilitiesType(int position) {
		int type;
		if(mWifiScanList.get(position).capabilities.contains("WPA")||mWifiScanList.get(position).capabilities.contains("wpa")){
			type = SECURITY_WPA;
		}else if(mWifiScanList.get(position).capabilities.contains("WEP")||mWifiScanList.get(position).capabilities.contains("wep")){
			type = SECURITY_WEP;
		}else if(mWifiScanList.get(position).capabilities.contains("EAP")||mWifiScanList.get(position).capabilities.contains("eap")){
			type = SECURITY_EAP;
		}else{
			type = SECURITY_NONE;
		}
		return type;
	}

	public int AddWifiConfig(String ssid,String pwd, int type){
		int wifiId = -1;
		for(int i = 0;i < mWifiScanList.size(); i++){
			ScanResult wifi = mWifiScanList.get(i);
			if(wifi.SSID.equals(ssid)){
				Log.i(TAG,"AddWifiConfig wifiManager.getWifiState()"
						+ mWifiManager.getWifiState());
				WifiConfiguration wifiCong = createWifiInfo(ssid,pwd,type);
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
	
	public void getConfiguration(){
		mWifiConfigList = mWifiManager.getConfiguredNetworks();
	}
	
	public boolean ConnectWifi(int wifiId){
		getConfiguration();
		for(int i = 0; i < mWifiConfigList.size(); i++){
			WifiConfiguration wifi = mWifiConfigList.get(i);
			if(wifi.networkId == wifiId){
				Log.i(TAG,"wifi.networkId == wifiId");
				/*Boolean flag = mWifiManager.enableNetwork(wifiId, true);
				//Log.i(TAG,"ConnectWifi "+String.valueOf(mWifiConfigList.get(wifiId).status));
				Log.i(TAG,"ConnectWifi flag"+flag);*/
//				mWifiManager.disconnect();
				Boolean flag = mWifiManager.enableNetwork(wifiId, true);
				Log.i(TAG,"ConnectWifi flag"+flag);
				/*while(!)){//激活该Id，建立连接
					
				}*/
				Log.i(TAG,"ConnectWifi wifiId1："+wifiId+"__"+String.valueOf(wifi.status));//status:0--已经连接，1--不可连接，2--可以连接
				
//				mWifiManager.reconnect();
				return true;
			}
		}
		return false;
	}
	
	public int IsConfiguration(String SSID) {
		int ret = -1;
		for(int i = 0; i < mWifiScanList.size(); i++){
			String ssid = "\"" + mWifiScanList.get(i).SSID + "\"";
			if(ssid.equals(SSID)){
				ret = i;
				break;
			}
		}
		return ret;
	}
	
	/*public int IsConfiguration(){
		int ret = -1;
		boolean flag = true;
		Log.i(TAG,"mWifiConfigList.size()  "+mWifiConfigList.size());
		for(int i = 0; i < mWifiConfigList.size(); i++){
			Log.i(TAG,"mWifiScanList.size()  "+mWifiScanList.size());
			for(int j = 0; j < mWifiScanList.size();j++){
				String ssid =  "\"" + mWifiScanList.get(j).SSID + "\""; 
				Log.i(TAG,"Scan  "+ssid);
				//Log.i(TAG,"IsConfiguration22  "+mWifiScanList.get(j).SSID);
				Log.i(TAG,"Config  "+mWifiConfigList.get(i).SSID);
				Log.i(TAG,"j  "+j);
				if(mWifiConfigList.get(i).SSID.equals(ssid)){
					mPosition = j;
					Log.i(TAG,"ssfgdgfgfhgjhgjhjkkjkljlk  ");
					
					ret =  mWifiConfigList.get(i).networkId;
					flag = false;
					break;
				}
			}
			if(!flag){
				break;
			}
		}
		return ret;
	}*/
	
	public List<WifiItem> scanResultToString(){
		mWifiList.clear();
		for(int i = 0; i <mWifiScanList.size(); i++){
			ScanResult strScan = mWifiScanList.get(i);
			Log.i(TAG,"scanResultToString ssid  "+strScan.SSID);
			WifiItem item = new WifiItem();
			item.setWifiName(strScan.SSID);
			if(strScan.capabilities.contains("WPA")||strScan.capabilities.contains("wpa")
					||strScan.capabilities.contains("WEP")||strScan.capabilities.contains("wep")
					||strScan.capabilities.contains("EAP")||strScan.capabilities.contains("eap")){
				item.setWifiSecured("SEC");
				item.setWifiLock("LOCK");
			}
			else{
				item.setWifiSecured("");
				item.setWifiLock("");
			}
			
			
			item.setWifiSignal(strScan.level);
			mWifiList.add(item);
		}
		for(int j = 0; j <mWifiList.size(); j++){
			Log.i(TAG,"scanResultToString mWifiScanList ssid  "+mWifiScanList.get(j).SSID);
		}
		return mWifiList;
	}
	
	// 查看以前是否也配置过这个网络
	public WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
	
	public boolean  RemoveWifi(int netId){
		return mWifiManager.removeNetwork(netId);
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
	
	public WifiConfiguration setWifiProxySettings(WifiConfiguration config)
	{
	    //get the current wifi configuration
//	    WifiManager mWifiManager = (WifiManager)mcgetSystemService(Context.WIFI_SERVICE);
	    WifiConfiguration retconfig = null;
	    if(null == config)
	        return null;

	    try
	    {
	        //get the link properties from the wifi configuration
	        Object linkProperties = getField(config, "linkProperties");
	        if(null == linkProperties)
	            return null ;

	        //get the setHttpProxy method for LinkProperties
	        Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
	        Class[] setHttpProxyParams = new Class[1];
	        setHttpProxyParams[0] = proxyPropertiesClass;
	        Class lpClass = Class.forName("android.net.LinkProperties");
	        Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
	        setHttpProxy.setAccessible(true);

	        //get ProxyProperties constructor
	        Class[] proxyPropertiesCtorParamTypes = new Class[3];
	        proxyPropertiesCtorParamTypes[0] = String.class;
	        proxyPropertiesCtorParamTypes[1] = int.class;
	        proxyPropertiesCtorParamTypes[2] = String.class;

	        Constructor proxyPropertiesCtor = proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);

	        //create the parameters for the constructor
	        Object[] proxyPropertiesCtorParams = new Object[3];
	        proxyPropertiesCtorParams[0] = "127.0.0.1";
	        proxyPropertiesCtorParams[1] = 8118;
	        proxyPropertiesCtorParams[2] = null;

	        //create a new object using the params
	        Object proxySettings = proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);

	        //pass the new object to setHttpProxy
	        Object[] params = new Object[1];
	        params[0] = proxySettings;
	        setHttpProxy.invoke(linkProperties, params);

	        setProxySettings("STATIC", config);

	        //save the settings
	        mWifiManager.updateNetwork(config);
	        mWifiManager.disconnect();
	        mWifiManager.reconnect();
	        retconfig = config ;
	        return retconfig;
	    }   
	    catch(Exception e)
	    {
	    	return null;
	    }
	}

	public static void setProxySettings(String assign , WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException{
			    setEnumField(wifiConf, assign, "proxySettings");     
			}

	public static void setEnumField(Object obj, String value, String name)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
			    Field f = obj.getClass().getField(name);
			    f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
			}

	public static Object getField(Object obj, String name)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
			    Field f = obj.getClass().getField(name);
			    Object out = f.get(obj);
			    return out;
			}
	
}
