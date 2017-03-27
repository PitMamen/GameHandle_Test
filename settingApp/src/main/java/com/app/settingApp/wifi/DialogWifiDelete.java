
package com.app.settingApp.wifi;

import com.app.settingApp.R;
import com.app.settingApp.ui.interfaces.onClickCustomListener;
import com.app.settingApp.util.KeyEventUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;

public class DialogWifiDelete extends Dialog implements OnClickListener
{
	private onClickCustomListener mListener;   //点击监听
	private TextView              mWifiName;   //wifi名称
	private Button                mCancel;      //取消
	private Button                mDelete;     //连接
	private String                mWifiInfo;  //信息String


	public DialogWifiDelete(Context mContext, String msg,
			onClickCustomListener onCustomDialogListener) {
		// TODO Auto-generated constructor stub
		super(mContext);
		mWifiInfo = msg;
		mListener = onCustomDialogListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_deletewifi);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  //设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		
		init();
	}
	
	private void init(){
		mWifiName = (TextView) findViewById(R.id.wifi_name);
		mCancel = (Button) findViewById(R.id.cancel_btn);
		mDelete = (Button) findViewById(R.id.delete_btn);
		mWifiName.setText(mWifiInfo);
		mCancel.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mCancel.requestFocus();
	}

	@Override
	public void onClick(View v) 
	{
		mListener.OnClick(v);
	}
	
	public void setOnClickCustomListener(onClickCustomListener listener) 
	{  
		mListener = listener;  
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && mCancel.isFocused()) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_UP);
		}
		return super.dispatchKeyEvent(event);
	}

}