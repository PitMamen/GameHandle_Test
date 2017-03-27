package com.app.settingApp.bluetooth;

import java.util.ArrayList;
import java.util.List;

import com.app.settingApp.R;
import com.app.settingApp.wifi.WifiItem;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
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
import android.widget.Toast;

public class BluetoothListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private RelativeLayout mBluetoothLayout;
	private ArrayList<BluetoothItem> mDataList;
	private String TAG = "BluetoothAdapter";
	
	public BluetoothListAdapter(Context context, ListView listview, ArrayList<BluetoothItem> dataList) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDataList = dataList;
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
		BluetoothItem info = mDataList.get(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_bluetooth_detail, null);
			holder = new ViewHolder();
			mBluetoothLayout = (RelativeLayout)convertView.findViewById(R.id.bluetooth_layout);
            holder.icon = (ImageView) convertView.findViewById(R.id.bluetooth_icon);
            holder.name = (TextView) convertView.findViewById(R.id.bluetooth_name);
            holder.paired = (TextView) convertView.findViewById(R.id.bluetooth_paired);
            
            convertView.setTag(holder);
		}
		else {
            holder = (ViewHolder) convertView.getTag();
		}
		
		/*if(position%2 == 0){
			mBluetoothLayout.setBackgroundResource(R.drawable.list_white_selector);
		}
		else{
			mBluetoothLayout.setBackgroundResource(R.drawable.list_gray_selector);
		}*/
		
		holder.name.setText(info.getBluetoothName()); 

		if(BluetoothItem.STATE_PAIRED == info.getBluetoothPaired()){
			holder.paired.setText(mContext.getResources().getString(R.string.paired)); 
		}else if(BluetoothItem.STATE_CONNECTED == info.getBluetoothPaired()){
			holder.paired.setText(mContext.getResources().getString(R.string.connected)); 
		}else if(BluetoothItem.STATE_PARING == info.getBluetoothPaired()){
			holder.paired.setText(mContext.getResources().getString(R.string.pairing)); 
		}else if(BluetoothItem.STATE_CONNECTING == info.getBluetoothPaired()){//
			holder.paired.setText(mContext.getResources().getString(R.string.connecting)); 
		}else{
			holder.paired.setText(""); 
		}
		if(0 == info.getBluetoothType()){
			//holder.icon.setBackgroundResource(R.drawable.setting_bluetooth_ic_media); 
			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.bluetooth_media_selector));
		}
		else if(1 == info.getBluetoothType()){
			//holder.icon.setBackgroundResource(R.drawable.setting_bluetooth_ic_pc); 
			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.bluetooth_device_selector));
		}
		else{
			//holder.icon.setBackgroundResource(R.drawable.setting_bluetooth_ic_device);
			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.bluetooth_pc_selector));
		}
		return convertView;
	}

	private Context getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public void notifySetChanged() { 
	    super.notifyDataSetChanged(); 
	}

	static class ViewHolder {
		ImageView icon;
		TextView name; 
		TextView paired;
    }
	
}
