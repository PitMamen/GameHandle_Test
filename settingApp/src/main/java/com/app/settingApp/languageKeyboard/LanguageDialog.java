package com.app.settingApp.languageKeyboard;

import java.util.ArrayList;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
* @Description: 语言选择
* @author chengkai  
* @date 2016年5月28日 下午2:26:43 
*
 */
public class LanguageDialog extends ActivityReceive {

	public static final int RESULT_LANGUAGE = 2;
	
	private ListView listView;
	
	private LanguageAdapter adapter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_language);
		
		listView = (ListView) findViewById(R.id.list);
		
		setListView();
		
		int currentLanguage = getIntent().getIntExtra(LanguageKeyboardActivity.LANGUAGE_TYPE, 1);
		adapter.setCheck(currentLanguage);
		
		listView.setSelection(0);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}
	
	private void setListView() {
		
		adapter = new LanguageAdapter();
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.setCheck(position);
				setLanguageResult(position);
			}
		});
	}
	
	private void setLanguageResult(int position) {
		Intent data = new Intent();
		data.putExtra(LanguageKeyboardActivity.LANGUAGE_TYPE, position);
		setResult(RESULT_LANGUAGE, data);
		finish();
	}
	
	class LanguageAdapter extends BaseAdapter {

		private ArrayList<KeyboardBean> list = new ArrayList<KeyboardBean>();
		
		private String [] names = null;
		
		LanguageAdapter() {
			names = getResources().getStringArray(R.array.languages);
			
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
				convertView = LayoutInflater.from(LanguageDialog.this).inflate(R.layout.item_language, null);
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
					setLanguageResult(position);
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
