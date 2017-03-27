package com.app.settingApp.settingReset;

import com.app.settingApp.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingResetDialog extends Dialog {

	public static final int TYPE_INIT = 1;
	public static final int TYPE_RETURN = 2;
	
	private Context mContext;
	private Button cancel;
	private Button ok;
	private TextView tvInfo;
	private TextView tvTitle;
	
	private int mType = 0;
	
	String title = null;
	String info = null;
	
	public SettingResetDialog(Context context) {
		this(context,0);
		this.mContext = context;
	}
	
	public SettingResetDialog(Context context, int theme) {
		super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
		this.mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = getLayoutInflater().inflate(R.layout.dialog_setting_reset, null);
		setContentView(view);
		
		cancel = (Button) findViewById(R.id.cancel);
		ok = (Button) findViewById(R.id.ok);
		tvTitle = (TextView) findViewById(R.id.title);
		tvInfo = (TextView) findViewById(R.id.info);
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
	}
	
	public void show(int type) {
		
		switch (type) {
		case TYPE_INIT:
			title = mContext.getString(R.string.setting_init);
			info = mContext.getString(R.string.setting_init_info);
			break;
//		case TYPE_RETURN:
//			break;

		default:
			title = mContext.getString(R.string.setting_return);
			info = mContext.getString(R.string.setting_return_info);
			break;
		}
		
		show();
	}
	
	@Override
	public void show() {
		super.show();
		cancel.setSelected(true);
		tvTitle.setText(title);
		tvInfo.setText(info);
	}

}
