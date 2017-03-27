package com.app.settingApp.wifi;

import android.os.Parcel;
import android.os.Parcelable;

public class WifiItem implements Parcelable {
	private String mWifiName;		//wifi鍚嶇О
	private String mWifiSecured;	//wifi瀹夊叏
	private String mWifiLock;		//wifi閿�
	private int mWifiSignal;		//wifi淇″彿
	private int connectState = 0;
	
	public static final String TYPE_SECURITY = "security";
	public static final String TYPE_NONE = "none";
	
	public static final int STATE_DISCNNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_DISCONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	
	public String getWifiName() {
		return mWifiName;
	}
	
	public String getSSID(){
		return "\"" + mWifiName + "\"";
	}
	
	public boolean isConnected(){
		return connectState == STATE_CONNECTED;
	}
	
	public int getConnectState(){
		return connectState;
	}
	
	public void setConnectState(int state){
		this.connectState = state;
	}
	
	public void setWifiName(String wifiName) {
		this.mWifiName = wifiName;
	}
	
	public String getWifiSecured() {
		return mWifiSecured;
	}
	
	public void setWifiSecured(String wifiSecured) {
		this.mWifiSecured = wifiSecured;
	}
	
	public String getWifiLock() {
		return mWifiLock;
	}
	
	public void setWifiLock(String wifiLock) {
		this.mWifiLock = wifiLock;
	}
	
	public int getWifiSignal() {
		return mWifiSignal;
	}
	
	public void setWifiSignal(int wifiSignal) {
		this.mWifiSignal = wifiSignal;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		
		out.writeString(mWifiName);
		out.writeString(mWifiSecured);
		out.writeString(mWifiLock);
		out.writeInt(mWifiSignal);
		out.writeInt(connectState);
	}

	public static final Parcelable.Creator<WifiItem> CREATOR = new Parcelable.Creator<WifiItem>() {
		public WifiItem createFromParcel(Parcel in) {
			return new WifiItem(in);
		}

		public WifiItem[] newArray(int size) {
			return new WifiItem[size];
		}
	};

	private WifiItem(Parcel in) {
		mWifiName = in.readString();
		mWifiSecured = in.readString();
		mWifiLock  = in.readString();
		mWifiSignal = in.readInt();
		connectState = in.readInt();
	}
	public WifiItem() {
		// TODO Auto-generated constructor stub
	}	
}