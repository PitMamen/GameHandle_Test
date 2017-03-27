package com.app.settingApp.brightness;

import java.util.ArrayList;
import java.util.List;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class ActivityBrightness extends ActivityReceive implements OnClickListener {
	
	private static final String		TAG = "ActivityBrightness";
	private SeekBar					mSeekbarView;
	private TextView				mBrightView;
	private CheckBox				mCBoxView;
	private ControlView 			mControlView;
	private LinearLayout 			mLayoutView;
	private RelativeLayout 			mSwitchLayoutView;
	private Context mContext ;

	private int mCurBrightness = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_brightness); 
		initView();	
		mSwitchLayoutView.setFocusable(true);
		mSwitchLayoutView.setFocusableInTouchMode(true);
		mSwitchLayoutView.requestFocus();
		
		mLayoutView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSeekbarView.setFocusable(true);
				mSeekbarView.setFocusableInTouchMode(true);
				mSeekbarView.requestFocus();
				mLayoutView.setSelected(true);
				mSeekbarView.setSelected(true);
			}
		});
		
		mCBoxView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
	}
	
	@Override
	protected void onResume() {		
		super.onResume();	
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	private void initView() {	
		mControlView = (ControlView) findViewById(R.id.control_view);
		mLayoutView = (LinearLayout)findViewById(R.id.brightness_layout);
		mSeekbarView = (SeekBar)findViewById(R.id.seekbar_brightness);
		mCBoxView = (CheckBox)findViewById(R.id.brightness_switch);
		mBrightView = (TextView)findViewById(R.id.txt_brightness);
		mSwitchLayoutView = (RelativeLayout)findViewById(R.id.brightness_control_layout);
		mSwitchLayoutView.setOnClickListener(this);
		mCBoxView.setChecked(false);
		mControlView.setTitleText(getResources().getString(R.string.brightness),false);
		mCurBrightness = (int) (android.provider.Settings.System.getInt(
                getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS, 255) );
		Log.i(TAG,"mCurBrightness"+mCurBrightness);
		mSeekbarView.setProgress(mCurBrightness);
		mSeekbarView.setOnSeekBarChangeListener(seekListener);
		mBrightView.setText(String.valueOf(mCurBrightness));

	}
	
	private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
        	Log.i(TAG,"fromUser"+fromUser);
        	Integer value = seekBar.getProgress();
            System.out.println(value);
            android.provider.Settings.System.putInt(getContentResolver(),
                            android.provider.Settings.System.SCREEN_BRIGHTNESS,
                            value); // 0-255
            value = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, -1);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (0<= value && value <= 255) {
                    lp.screenBrightness = value;
            }
            Log.i(TAG,"value"+value);
            getWindow().setAttributes(lp);
            mBrightView.setText(String.valueOf(value));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
	};
	
	/*
	 * 鎸夐敭鎿嶄綔
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(mSeekbarView.isSelected()) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				mSeekbarView.setFocusable(false);
				mSeekbarView.setSelected(false);
				mLayoutView.requestFocus();
				mLayoutView.setSelected(false);
				return true;
			}
		}else {
			KeyEventUtil.changeKeyCode(event);
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void onClick(View arg0) {
		Log.i("","onClick");
		if(View.VISIBLE == mLayoutView.getVisibility()){
			mLayoutView.setVisibility(View.GONE);
			mCBoxView.setChecked(false);
		}
		else{
			mLayoutView.setVisibility(View.VISIBLE);
			mCBoxView.setChecked(true);
		}
	}
	
	private void setBrightness(int value){
		mSeekbarView.setProgress(value);
	}
}
