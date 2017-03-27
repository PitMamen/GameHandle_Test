package com.app.settingApp.colortemperature;

import java.util.ArrayList;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.languageKeyboard.KeyboardBean;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityColorTemperature extends ActivityReceive implements OnClickListener, OnItemClickListener, OnItemSelectedListener {
	
	private static final String		TAG = "ActivityColorTemperature";
	private ListView				mListView;
	private ControlView 			mControlView;
	
	private ColorTemAdapter adapter;

	private Boolean mProfile = true;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_colortemperature); 
		initView();	
		
	}
	
	@Override
	protected void onResume() {		
		super.onResume();	
	}
	
	@Override  
	protected void onDestroy() {  
		super.onDestroy();  
	}  
	
	private void initView() {	
		mControlView = (ControlView) findViewById(R.id.control_view);
		mListView = (ListView) findViewById(R.id.list);
		
		mControlView.setTitleText(getString(R.string.color_temperature), false);
		
		setListView();
	}
	
	private void setListView() {
		
		adapter = new ColorTemAdapter();
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(this);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		adapter.setCheck(position);

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public void onClick(View arg0) {
		
	}
	
	class ColorTemAdapter extends BaseAdapter {

		private ArrayList<KeyboardBean> list = new ArrayList<KeyboardBean>();
		
		private String [] names = null;
		
		ColorTemAdapter() {
			names = getResources().getStringArray(R.array.color_temperatures);
			
			for(int i=0;i<4;i++) {
				KeyboardBean bean = new KeyboardBean(false,names[i]);
				list.add(bean);
			}
		}
		
		public void setCheck(int position) {
			int length = getCount();
			for(int i=0;i<length;i++) {
				getItem(i).setCheck(false);
			}
			getItem(position).setCheck(true);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public KeyboardBean getItem(int position) {
			
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(ActivityColorTemperature.this).inflate(R.layout.item_keyboard, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			holder.checkBox.setChecked(getItem(position).isCheck());
			holder.textView.setText(getItem(position).getName());
			
			holder.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setCheck(position);
				}
			});
			
			return convertView;
		}
		
		class ViewHolder {
			CheckBox checkBox;
			TextView textView;
			ViewHolder(View view) {
				checkBox = (CheckBox) view.findViewById(R.id.check);
				textView = (TextView) view.findViewById(R.id.name);
			}
		}
		
	}
	
}


