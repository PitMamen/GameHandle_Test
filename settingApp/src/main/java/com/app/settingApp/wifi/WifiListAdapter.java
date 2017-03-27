package com.app.settingApp.wifi;

import java.util.ArrayList;
import java.util.List;

import com.app.settingApp.R;
import com.app.settingApp.wifi.WifiItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private RelativeLayout mWifiLayout;
	private List<WifiItem> mDataList;
	private String TAG = "WifiListAdapter";
	
	public WifiListAdapter(Context context, ListView listview, List<WifiItem> dataList) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDataList = dataList;
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;		
		WifiItem info = mDataList.get(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_wifi_detail, null);
			holder = new ViewHolder();
			mWifiLayout = (RelativeLayout)convertView.findViewById(R.id.wifi_layout);
            holder.name = (TextView) convertView.findViewById(R.id.wifi_name);
            holder.secured = (TextView) convertView.findViewById(R.id.wifi_secured);
            holder.lock = (ImageView) convertView.findViewById(R.id.wifi_lock);
            holder.signal = (ImageView) convertView.findViewById(R.id.wifi_signal);
            
            convertView.setTag(holder);
		}
		else {
            holder = (ViewHolder) convertView.getTag();
		}
		
		/*if(position%2 == 0){
			mWifiLayout.setBackgroundResource(R.drawable.list_white_selector);
		}
		else{
			mWifiLayout.setBackgroundResource(R.drawable.list_gray_selector);
		}*/
		holder.name.setText(info.getWifiName());
		if(info.getConnectState() == WifiItem.STATE_CONNECTING){
			holder.secured.setText(mContext.getResources().getString(R.string.connecting));
			holder.secured.setVisibility(View.VISIBLE);
		}else if(info.getConnectState() == WifiItem.STATE_CONNECTED){
			holder.secured.setText(mContext.getResources().getString(R.string.connected));
			holder.secured.setVisibility(View.VISIBLE);
		}else if(info.getConnectState() == WifiItem.STATE_DISCONNECTING){
			holder.secured.setText(mContext.getResources().getString(R.string.disconnecting));
			holder.secured.setVisibility(View.VISIBLE);
		}else if(info.getWifiSecured().equals(WifiItem.TYPE_SECURITY)){
			holder.secured.setText(mContext.getResources().getString(R.string.secured));
			holder.secured.setVisibility(View.VISIBLE);	
		}else if(info.getWifiSecured().equals(WifiItem.TYPE_NONE)){
			//holder.secured.setText(mContext.getResources().getString(R.string.none));
			holder.secured.setVisibility(View.INVISIBLE);
		}
		else{
			holder.secured.setVisibility(View.INVISIBLE);	
		}
		
		if(info.getWifiLock().equals("LOCK")){
			holder.lock.setVisibility(View.VISIBLE);
		}
		else{
			holder.lock.setVisibility(View.GONE);
		}
		
		if (Math.abs(info.getWifiSignal()) > 100) {  

			holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi4));  

        } else if (Math.abs(info.getWifiSignal()) > 80) {  

        	holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi3));  

        } else if (Math.abs(info.getWifiSignal()) > 70) {  

        	holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi2));  

        } else if (Math.abs(info.getWifiSignal()) > 60) {  

        	holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi1));  

        } else if (Math.abs(info.getWifiSignal()) > 50) {  

        	holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi0));  

        } else {  

        	holder.signal.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setting_wifi_ic_wifi0));  

        }  
		
		return convertView;
	}

	public void notifySetChanged() { 
	    super.notifyDataSetChanged(); 
	}

	static class ViewHolder {
		//ImageView bg;
		TextView name; 
		TextView secured;
		ImageView lock;
		ImageView signal;
    }
	
}
