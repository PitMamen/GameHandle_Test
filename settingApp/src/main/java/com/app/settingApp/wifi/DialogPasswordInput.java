
package com.app.settingApp.wifi;

import com.app.settingApp.R;
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

public class DialogPasswordInput extends Dialog
{
	final String TAG = "DialogPasswordInput";
	private OnCustomDialogListener mListener;   //点击监听
	private TextView              mWifiName;   //wifi名称
	private EditText			  mWifiPassword;//wifi密码
	private Button                mCancel;      //取消
	private Button                mConnect;     //连接
	private String                mWifiInfo;  //信息String
	final int MIN_PWD_LENGTH = 8;
	
	public DialogPasswordInput(Context mContext,String WifiName,OnCustomDialogListener listener) {
		// TODO Auto-generated constructor stub
		super(mContext);
		mWifiInfo = WifiName;
		mListener = listener;
	}

	public interface OnCustomDialogListener{
		void back(String str);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//TODO add show password checkbox is better.
		setContentView(R.layout.dialog_password_input);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
//		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);  //设置成系统级别的Dialog，即全局性质的Dialog，在任何界面下都可以弹出来
		
		init();
	}
	
	private void init(){
		mWifiName = (TextView) findViewById(R.id.wifi_name);
		mWifiPassword = (EditText) findViewById(R.id.password_input);
		mCancel = (Button) findViewById(R.id.cancel_btn);
		mConnect = (Button) findViewById(R.id.connect_btn);
		
		
		mWifiPassword.setFocusableInTouchMode(true);
		mWifiName.setText(mWifiInfo);
		mCancel.setOnClickListener(dialogListener);
		mConnect.setOnClickListener(dialogListener);
		mConnect.setEnabled(false);// disable before input password.
		mConnect.setFocusable(false);
		mWifiPassword.setFocusable(true);
		mWifiPassword.requestFocus();
		
		mWifiPassword.addTextChangedListener(new TextWatcher() { // 监听编辑框内容修改
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) { // 修改编辑框的内容，隐藏提示字
			}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				//android.util.Log.d(TAG, String.format("start(%d), before(%d), count(%d), s.length(%d)",
				//		start, before, count, s.length()));
				int curTextLength = s.length();
				//check if char count >= min password char length.
				mConnect.setEnabled(curTextLength >= MIN_PWD_LENGTH);
				mConnect.setFocusable(curTextLength >= MIN_PWD_LENGTH);
			}
		});
		/*mWifiPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			   @Override
			   public void onFocusChange(View v, boolean hasFocus) {
			       if (hasFocus) {
			    	    Log.i("","hasFocus"+hasFocus);
			            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				    //pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			       }
			   }
			});
		mWifiPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					return true;
				}

				return false;
			}
		});
		mWifiPassword.setTypeface(Typeface.DEFAULT);  
		mWifiPassword.setTransformationMethod(new PasswordTransformationMethod());
		//inputManager.showSoftInput(mWifiPassword,  InputMethodManager.SHOW_FORCED);
	*/}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, event.getKeyCode()+"");
		if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_CENTER);
		}else if(mWifiPassword.isFocused() && mWifiPassword.getSelectionStart() == mWifiPassword.getSelectionEnd() 
				&& mWifiPassword.getSelectionEnd()==mWifiPassword.getText().length() 
				&& event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT ) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_DOWN);
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && mCancel.isFocused()) {
			KeyEventUtil.setFieldCode(event, KeyEvent.KEYCODE_DPAD_UP);
		}
		
		Log.d(TAG, event.getKeyCode()+"");
		
		return super.dispatchKeyEvent(event);
		
	}

	private View.OnClickListener dialogListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if(view.getId() == R.id.cancel_btn){
				mWifiPassword = null;
				mListener.back(null);
				cancel();
			}
			else{
				if(mWifiPassword.getText().toString().length()>=MIN_PWD_LENGTH){
					mListener.back(mWifiPassword.getText().toString());
					dismiss();
				}
				
			}
		}
	};

}