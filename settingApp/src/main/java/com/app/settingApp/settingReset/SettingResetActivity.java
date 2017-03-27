package com.app.settingApp.settingReset;

import com.app.settingApp.R;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingResetActivity extends Activity {

	private TextView settingInit;
	private TextView settingReturn;
	
	private SettingResetDialog settingReset = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_reset);
		
		settingInit = (TextView) findViewById(R.id.setting_init);
		settingReturn = (TextView) findViewById(R.id.setting_return);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.initialization), false);
		
		settingInit.setFocusable(true);
		settingInit.setFocusableInTouchMode(true);
		settingInit.requestFocus();
		settingInit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSettingDialog(SettingResetDialog.TYPE_INIT);
			}
		});
		settingReturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSettingDialog(SettingResetDialog.TYPE_RETURN);
			}
		});
	}
	
	private void showSettingDialog(int type) {
		if(settingReset == null) {
			settingReset = new SettingResetDialog(this);
		}
		settingReset.show(type);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}
}
