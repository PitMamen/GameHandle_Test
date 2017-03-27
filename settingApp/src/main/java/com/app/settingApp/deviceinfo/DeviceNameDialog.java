package com.app.settingApp.deviceinfo;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
* @Description: 修改设备名字dialog
* @author chengkai  
* @date 2016年6月1日 上午10:24:46 
*
 */
public class DeviceNameDialog extends ActivityReceive {
	
	private static final String TAG = "DeviceNameDialog";

	public static final int OK_CODE = 2;
	
	Context mContext;
	
	Button cancel;
	
	Button ok;
	
	EditText etName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_device_name);
		
		cancel = (Button) findViewById(R.id.cancel);
		ok = (Button) findViewById(R.id.ok);
		etName = (EditText) findViewById(R.id.edit_name);
		
		String name = getIntent().getStringExtra(DeviceInfoActivity.TYPE_NAME);
		etName.setText(name);
		
		etName.setSelection(etName.getText().length());
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = etName.getText().toString();
				Intent data = new Intent();
				data.putExtra(DeviceInfoActivity.TYPE_NAME, name);
				setResult(OK_CODE, data);
				finish();
			}
		});
		
	}
	
	void isNeedNext() {
		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "code:"+event.getKeyCode());
		if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_CENTER);
		}else if(etName.isFocused() && etName.getSelectionStart() == etName.getSelectionEnd() 
				&& etName.getSelectionEnd()==etName.getText().length() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_DOWN);
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && cancel.isFocused()) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_UP);
		}
		return super.dispatchKeyEvent(event);
		
	}

}
