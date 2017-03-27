package com.app.settingApp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class BluetoothItem implements Parcelable {
	private String mBluetoothName;		//bluetooth名称
	private String mBluetoothAddress;	//bluetooth地址
	private int mBluetoothPaired;	//bluetooth匹配 12:已匹配
	private int mBluetoothType;			//bluetooth类型
	private Object mBluetoothClass;
	
	public static final int TYPE_HEAD = 1;
	public static final int TYPE_COMPUTER = 2;
	public static final int TYPE_PHONE = 2;
	
	public static final int STATE_NONE = 10;
	public static final int STATE_PAIRED = 12;
	public static final int STATE_CONNECTING = 15;
	public static final int STATE_CONNECTED = 13;
	public static final int STATE_PARING = 14;
	
	public String getBluetoothName() {
		return mBluetoothName;
	}
	
	public void setBluetoothName(String bluetoothName) {
		this.mBluetoothName = bluetoothName;
	}
	
	public String getBluetoothAddress() {
		return mBluetoothAddress;
	}
	
	public void setBluetoothAddress(String bluetoothAddress) {
		this.mBluetoothAddress = bluetoothAddress;
	}
	
	public Object getBluetoothClass() {
		return mBluetoothClass;
	}
	
	public void setBluetoothClass(Class bclass) {
		this.mBluetoothClass = bclass;
	}
	
	public int getBluetoothPaired() {
		return mBluetoothPaired;
	}
	
	public void setBluetoothPaired(int bluetoothPaired) {
		this.mBluetoothPaired = bluetoothPaired;
	}
	
	public int getBluetoothType() {
		return mBluetoothType;
	}
	
	public void setBluetoothType(int bluetoothType) {
		this.mBluetoothType = bluetoothType;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(mBluetoothName);
		out.writeString(mBluetoothAddress);
		out.writeInt(mBluetoothPaired);
		out.writeInt(mBluetoothType);
		out.writeValue(mBluetoothClass);
	}

	public static final Parcelable.Creator<BluetoothItem> CREATOR = new Parcelable.Creator<BluetoothItem>() {
		public BluetoothItem createFromParcel(Parcel in) {
			return new BluetoothItem(in);
		}

		public BluetoothItem[] newArray(int size) {
			return new BluetoothItem[size];
		}
	};

	private BluetoothItem(Parcel in) {
		mBluetoothName = in.readString();
		mBluetoothAddress = in.readString();
		mBluetoothPaired = in.readInt();
		mBluetoothType = in.readInt();
		mBluetoothClass = in.readValue(null);
		
	}
	public BluetoothItem() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getStateString(int state){
		switch(state){
		case STATE_NONE: 
			return "STATE_NONE";
		case STATE_PAIRED:
			return "STATE_PAIRED";
		case STATE_CONNECTING:
			return "STATE_CONNECTING";
		case STATE_CONNECTED:
			return "STATE_CONNECTED";
		case STATE_PARING:
			return "STATE_PARING";
		}
		return "STATE_UNKNOWN";
	}
}

