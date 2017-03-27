package com.app.settingApp.languageKeyboard;

import java.util.ArrayList;
import java.util.Locale;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;

import android.annotation.SuppressLint;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
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
* @Description: 语言和键盘
* @author chengkai  
* @date 2016年5月28日 下午1:56:51 
*
 */
public class LanguageKeyboardActivity extends ActivityReceive {

	private static final String TAG = "LanguageKeyboardActivity";
	public static final String LANGUAGE_TYPE = "language";
	
	private static final int REQUEST_LANGUAGE = 1;
	
	private static final int KOREAN = 0;
	private static final int ENGLISH = 1;
	private static final int CHINESE = 2;
	private static final int JAPANESE= 3;
	
	private ListView listView;
	private TextView language;
	private View choseLanguage;
	
	private LanguageAdapter adapter ;
	
	private int currentLanguage = 0;
	
	private String [] names =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_languagekeyboard);
		
		choseLanguage = findViewById(R.id.chose_language);
		language = (TextView) findViewById(R.id.language);
		listView = (ListView) findViewById(R.id.list);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.language), false);
		
		names = getResources().getStringArray(R.array.languages);
		
		setListView();
		
		currentLanguage = getLanguageEnv();
		setLanguage(currentLanguage);
		
		choseLanguage.setFocusable(true);
		choseLanguage.setFocusableInTouchMode(true);
		choseLanguage.requestFocus();
		
		choseLanguage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LanguageKeyboardActivity.this,LanguageDialog.class);
				intent.putExtra(LANGUAGE_TYPE, currentLanguage);
				startActivityForResult(intent, REQUEST_LANGUAGE);
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
		if(requestCode == REQUEST_LANGUAGE && resultCode == LanguageDialog.RESULT_LANGUAGE) {
			adapter.setCheck(currentLanguage, false);
			currentLanguage = data.getIntExtra(LANGUAGE_TYPE, 1);
			setLanguage(currentLanguage);
			Locale locale = null;
			switch (currentLanguage) {
				case KOREAN:
					locale = Locale.KOREAN;
					break;
				case ENGLISH:
					locale = Locale.ENGLISH;
					break;
				case CHINESE:
					locale = Locale.CHINESE;
					break;
				case JAPANESE:
					locale = Locale.JAPANESE;
					break;
			}
			
			changeLanguage(locale); 
		}
	}
	
	private void changeLanguage (Locale locale) {
		IActivityManager iActMag = ActivityManagerNative.getDefault();  
		try {  
		    Configuration config = iActMag.getConfiguration();  
		    config.locale = locale;  
		    // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION  
		    // 会重新调用 onCreate();  
		    iActMag.updateConfiguration(config);  
		} catch (RemoteException e) {  
		    e.printStackTrace();  
		} 
	}
	
	private void setLanguage(int index) {
		
		language.setText(names[index]);
		setListCheck(index);
	}
	
	private void setListCheck(int index) {
		if(index != 1) {
			adapter.setCheck(1, true);
		}
		adapter.setCheck(index,true);
	}
	
	private void setListView() {
		
		adapter = new LanguageAdapter();
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.makeCheck(position);
			}
		});
	}
	
	private int getLanguageEnv() {  
	       Locale l = Locale.getDefault();  
	       String language = l.getLanguage();  
//	       String country = l.getCountry().toLowerCase();  
	       int n = 1; //如果没找到，默认显示english
	       if("ko".equals(language)) {
	    	   n = 0;
	       }else if ("en".equals(language)) {  
	           n = 1;
	       }else if ("zh".equals(language)) {  
	    	   n = 2; 
	       }else if ("ja".equals(language)) {
	    	   n = 3;
	       }   
	       return n;
	   }  
	
	class LanguageAdapter extends BaseAdapter {

		private ArrayList<KeyboardBean> list = new ArrayList<KeyboardBean>();
		
		int colorGray = getResources().getColor(R.color.item_gray);
		int colorWhiteGray = getResources().getColor(R.color.item_gray_white);
		
		LanguageAdapter() {
			
			for(int i=0;i<4;i++) {
				KeyboardBean bean = new KeyboardBean(false,names[i]);
				list.add(bean);
			}
		}
		
		public void setCheck(int position, boolean isCheck) {
			getItem(position).setCheck(isCheck);
			notifyDataSetChanged();
		}
		 
		public void makeCheck(int position) {
			boolean isCheck = getItem(position).isCheck() == false;
			getItem(position).setCheck(isCheck);
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
				convertView = LayoutInflater.from(LanguageKeyboardActivity.this).inflate(R.layout.item_keyboard, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
				
			}
			holder = (ViewHolder) convertView.getTag();
			final KeyboardBean bean = getItem(position);
			holder.checkBox.setChecked(bean.isCheck());
			holder.textView.setText(bean.getName());
			
			holder.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) { 
					bean.setCheck((bean.isCheck() == false));
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
