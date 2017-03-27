package com.app.settingApp.colortemperature;

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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ColorTemperatureAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private Boolean mCheck;
	private int mPosition;
	private ArrayList<String> mDataList;
	private String TAG = "ColorTemperatureAdapter";
	
	public ColorTemperatureAdapter(Context context, ListView listview, ArrayList<String> dataList) {
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
	
	public void setCheck(Boolean check){
		mCheck = check;
	}
	
	public void setPosition(int pos){
		mPosition = pos;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;		
		//BluetoothItem info = mDataList.get(position);
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_colortemperature, null);
			holder = new ViewHolder();
            holder.cbox = (CheckBox) convertView.findViewById(R.id.color_check);
            Log.i(TAG,"getView mPosition"+mPosition);
            if(position == mPosition){
            	holder.cbox.setChecked(true);
            }
            else{
            	holder.cbox.setChecked(false);
            }
            holder.cbox.setText(mDataList.get(position));
            convertView.setTag(holder);
		}
		else {
            holder = (ViewHolder) convertView.getTag();
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
		CheckBox cbox;
    }
	
}
