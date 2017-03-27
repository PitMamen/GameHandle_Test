package com.app.settingApp.adapter;

import com.app.settingApp.R;
import com.app.settingApp.view.GGridView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeListAdapter extends BaseAdapter {

	private Context mContext;
	private GridView mGridView;
	private LayoutInflater mInflater;
	private String TAG = "HomeListAdapter";
	public static int ROW_NUMBER = 3;

	
	public HomeListAdapter(Context context, GridView gridview) {
		mContext = context;
		mGridView = gridview;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.item_home, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
          
            switch(position){
    		case 0:
    			holder.name.setText(mContext.getResources().getString(R.string.wifi));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_wifi_selector));
    			break;
    		case 1:
    			holder.name.setText(mContext.getResources().getString(R.string.bluetooth));
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_bluetooth_selector));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			break;
    		case 2:
    			holder.name.setText(mContext.getResources().getString(R.string.brightness));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_brightness_selector));
    			break;
    		case 3:
    			holder.name.setText(mContext.getResources().getString(R.string.calibration));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_adjust_selector));
    			break;
    		case 4:
    			holder.name.setText(mContext.getResources().getString(R.string.color_temperature));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_color_selector));
    			break;
    		case 5:
    			holder.name.setText(mContext.getResources().getString(R.string.battery));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_battery_selector));
    			break;
    		case 6:
    			holder.name.setText(mContext.getResources().getString(R.string.sdcard));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_sdcard_selector));
    			break;
    		case 7:
    			holder.name.setText(mContext.getResources().getString(R.string.volume));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_sound_selector));
    			break;
    		case 8:
    			holder.name.setText(mContext.getResources().getString(R.string.language));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_language_selector));
    			break;
    		case 9:
    			holder.name.setText(mContext.getResources().getString(R.string.firmware));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_upgrade_selector));
    			break;
    		case 10:
    			holder.name.setText(mContext.getResources().getString(R.string.deviceinfo));
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_devinfo_selector));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			break;	
    		case 11:
    			holder.name.setText(mContext.getResources().getString(R.string.initialization));
    			//holder.icon.setBackgroundResource(R.drawable.setting);
    			holder.icon.setBackground(mContext.getResources().getDrawable(R.drawable.home_reset_selector));
    			break;	
    			
    		default:
    			break;
    		}
           /* int height = mGridView.getHeight();  
            int width = mGridView.getWidth(); 
            GridView.LayoutParams params = new GridView.LayoutParams(width / 4,  
                    height /3);  
            //设置每一行的高度和宽度
            convertView.setLayoutParams(params);*/
            //高度计算
            /*Log.i(TAG,"mGridView"+mGridView.getHeight());
            GridView.LayoutParams param = new GridView.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                mGridView.getHeight()/ROW_NUMBER);
            convertView.setLayoutParams(param);
            Log.i(TAG,"param"+param.height);*/
            convertView.setTag(holder);
		}
		else {
            holder = (ViewHolder) convertView.getTag();
		}
		
		int height = mGridView.getHeight() -4*2;  
        int width = mGridView.getWidth() - 4*5; 
        Log.d(TAG, "height:"+height+",width:"+width);
        GridView.LayoutParams params = new GridView.LayoutParams(width / 4,  
        		height /3);  
        //设置每一行的高度和宽度
        convertView.setLayoutParams(params);
		
		return convertView;
	}

	public static void setListViewHeightBasedOnChildren(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
		return;
		}
		// 固定列宽，有多少列
		int col = 4;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
		// listAdapter.getCount()小于等于8时计算两次高度相加
		for (int i = 0; i < listAdapter.getCount(); i += col) {
		// 获取listview的每一个item
		View listItem = listAdapter.getView(i, null, listView);
		listItem.measure(0, 0);
		// 获取item的高度和
		totalHeight += listItem.getMeasuredHeight();
		}
		
		// 获取listview的布局参数
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight;
		// 设置margin
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
		}
	
	public void notifySetChanged() { 
	    super.notifyDataSetChanged(); 
	}

	static class ViewHolder {
		ImageView icon;
		TextView name; 
		void update(){
			
		}
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 12;
	}
	
}
