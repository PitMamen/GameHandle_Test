package com.app.settingApp.deviceinfo;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 
* @Description: 机器信息
* @author chengkai  
* @date 2016年5月30日 上午9:56:58 
*
 */
public class DeviceInfoActivity extends ActivityReceive {

	public static final String TYPE_NAME = "name";
	
	private ListView listView;
	private View nameItem;
	private TextView deviceName;
	
	private DeviceAdapter adapter;
	
	class MyHandler extends Handler {
		
		public void handleMessage(android.os.Message msg) {
			adapter.setTime(DeviceUtil.getTimeFormat(SystemClock.elapsedRealtime()));
			sendEmptyMessageDelayed(1, 1000);
		};
	};
	
	SoftReference<Handler> reference = new SoftReference<Handler>(new MyHandler());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deviceinfo);
		
		listView = (ListView) findViewById(R.id.list);
		nameItem = findViewById(R.id.name_item);
		deviceName = (TextView) findViewById(R.id.device_name);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.deviceinfo), false);
		deviceName.setText("Smart Beam");
		
		setListView();
		
//		nameItem.setFocusable(true);
//		nameItem.setFocusableInTouchMode(true);
//		nameItem.requestFocus();
		
		nameItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeviceInfoActivity.this,DeviceNameDialog.class);
				intent.putExtra(TYPE_NAME, deviceName.getText().toString());
				startActivityForResult(intent, 1);
			}
		});
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == DeviceNameDialog.OK_CODE) {
			String name = data.getStringExtra(TYPE_NAME);
			deviceName.setText(name);
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Handler handler = reference.get();
		if(handler != null) {
			handler.removeMessages(1);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Handler handler = reference.get();
		if(handler != null) {
			handler.sendEmptyMessageDelayed(1, 1000);
		}
	}
	
	private void setListView() { 
		
		adapter = new DeviceAdapter();
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}
	
	class DeviceAdapter extends BaseAdapter {

		private ArrayList<DeviceInfoBean> list = new ArrayList<DeviceInfoBean>();
		
		private String[] names; 
		
		private List<String> listInfo;
		
		int colorGray = getResources().getColor(R.color.item_gray);
		int colorWhiteGray = getResources().getColor(R.color.item_gray_white);
		
		DeviceAdapter() {
			names = getResources().getStringArray(R.array.device_name_list);
			
			listInfo = DeviceUtil.getDeviceInfo(DeviceInfoActivity.this);
		    
		    for(int i = 0;i<names.length;i++) {
		    	DeviceInfoBean bean = new DeviceInfoBean();
		    	bean.setName(names[i]);
		    	if(listInfo.size() >  i) {
		    		bean.setInfo(listInfo.get(i));
		    	}
		    	list.add(bean);
		    }
		}
		
		public void setTime(String time) {
			getItem(2).setInfo(time);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public DeviceInfoBean getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(DeviceInfoActivity.this).inflate(R.layout.item_device, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
				
			}
			holder = (ViewHolder) convertView.getTag();
			DeviceInfoBean bean = getItem(position);
			holder.info.setText(bean.getInfo());
			holder.name.setText(bean.getName());
			
			return convertView;
		}
		
		class ViewHolder {
			
			TextView name;
			TextView info;
			ViewHolder(View view) {
				info = (TextView) view.findViewById(R.id.info);
				name = (TextView) view.findViewById(R.id.name);
			}
		}
		
	}
}
