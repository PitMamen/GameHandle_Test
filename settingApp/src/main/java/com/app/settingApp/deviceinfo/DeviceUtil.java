package com.app.settingApp.deviceinfo;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

public class DeviceUtil {

	public static List<String> getDeviceInfo(Context context) {
		
		ArrayList<String> listInfo = new ArrayList<String>();
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
		
		listInfo.add(Build.MODEL);   
		listInfo.add(tm.getDeviceId());
		listInfo.add(getTimeFormat(SystemClock.elapsedRealtime()));
		
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo info = wifi.getConnectionInfo();
		listInfo.add(info.getMacAddress());
		
	    String osVersion = Build.VERSION.RELEASE;
	    listInfo.add(osVersion);
	    listInfo.add(getVersionName(context));
	    return listInfo;
	}
	
	public static String getVersionName(Context context) {  
        // 获取packagemanager的实例  
        PackageManager packageManager = context.getPackageManager();  
        // getPackageName()是你当前类的包名，0代表是获取版本信息  
        PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			String version = packInfo.versionName;  
	        return version;  
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
        return null; 
	}
	
	@SuppressLint("DefaultLocale")
	public static String getTimeFormat (long time) {
		long s = time/1000;
		long min = s/60;
		if(min < 1) {
			return String.format("%ds", s);
		}
		long h = min/60;
		s = s%60;
		if(h < 1) {
			return String.format("%dmin%ds", min,s);
		}
		int d = (int) (h/24);
		if(d < 1) {
			return String.format("%dH%dmin%ds", h,min%60,s);
		}
		return String.format("%dD%dH%dmin%ds", d,h%24,min%60,s);
	}
}
