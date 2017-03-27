
package com.app.settingApp.bluetooth;

import com.app.settingApp.R;
import com.app.settingApp.ui.interfaces.onClickCustomListener;
import com.app.settingApp.util.KeyEventUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.TextView.OnEditorActionListener;

public class DialogBluetoothConnect extends Dialog implements OnClickListener
{
	private onClickCustomListener mListener;   //点击监听
	private TextView            mBluetoothName;   //蓝牙名称
	private Button              mUnpair;      //取消匹配
	private Button              mConnect;     //连接
	private String              mBluetoothInfo;  //信息String
	private int 				mType = 0;

	public DialogBluetoothConnect(Context mContext, String msg, int type,
			onClickCustomListener onCustomDialogListener) {
		// TODO Auto-generated constructor stub
		super(mContext);
		mBluetoothInfo = msg;
		mType = type;
		mListener = onCustomDialogListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_bluetooth_connect);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  //设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		
		init();
	}
	
	private void init(){
		mBluetoothName = (TextView) findViewById(R.id.bluetooth_name);
		mUnpair = (Button) findViewById(R.id.unpair_btn);
		mConnect = (Button) findViewById(R.id.connect_btn);
		mBluetoothName.setText(mBluetoothInfo);
		mUnpair.setOnClickListener(this);
		mConnect.setOnClickListener(this);
		mConnect.setTag(mType);
		mConnect.setText(mType == BluetoothItem.STATE_CONNECTED ?
				R.string.option_disconnect : R.string.option_connect);
		mUnpair.requestFocus();
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
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && mUnpair.isFocused()) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_UP);
		}
		//KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}
}