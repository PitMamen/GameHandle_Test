package com.app.settingApp.ScreenAdjust;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.view.ControlView;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 
* @Description: 梯形校正
* @author chengkai  
* @date 2016年6月4日 下午3:21:31 
*
 */
public class ScreenAdjustActivity extends ActivityReceive implements OnClickListener {

	private static final String TAG = "ScreenAdjustActivity";
	
	private int max = 10;
	
	private int mCurrent = 3;
	
	private View selectMode, mirroring, rotation;
	
	private CheckBox checkBox;
	
	private Button down, up;
	
	private SeekBar seekbar;
	
	private View seekbg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_screen_adjust);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.calibration), false);
		
		selectMode = findViewById(R.id.chose_mode);
		mirroring = findViewById(R.id.mirroring);
		rotation = findViewById(R.id.rotation);
		checkBox = (CheckBox) findViewById(R.id.select);
		down = (Button) findViewById(R.id.btn_down);
		up = (Button) findViewById(R.id.btn_up);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbg = findViewById(R.id.seek);
		
		selectMode.setFocusable(true);
		selectMode.setFocusableInTouchMode(true);
		selectMode.requestFocus();
		
		selectMode.setOnClickListener(this);
		mirroring.setOnClickListener(this);
		rotation.setOnClickListener(this);
		down.setOnClickListener(this);
		up.setOnClickListener(this);
		
		seekbar.setMax(max);
		seekbar.setProgress(mCurrent);
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				seekbg.setFocusable(!isChecked);
				
			}
		});
		
		seekbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				seekbar.setFocusable(true);
				seekbar.setFocusableInTouchMode(true);
				seekbar.requestFocus();
				seekbg.setSelected(true);
				seekbar.setSelected(true);
			}
		});
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		if(seekbar.isSelected()) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				seekbar.setFocusable(false);
				seekbar.setSelected(false);
				seekbg.requestFocus();
				seekbg.setSelected(false);
				return true;
			}
		}else {
			KeyEventUtil.changeKeyCode(event);
		}
		
		/*if(mirroring.isFocused() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction()==KeyEvent.ACTION_DOWN) {
			up.requestFocus();
			return true;
		}else if(up.isFocused() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction()==KeyEvent.ACTION_DOWN) {
			mirroring.requestFocus();
			return true;
		}else if((down.isFocused() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) || 
				(up.isFocused() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT)) {
			
		}else {
			KeyEventUtil.changeKeyCode(event);
		}*/
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chose_mode:
			checkBox.setChecked(checkBox.isChecked() == false);
			break;
		case R.id.mirroring:
			
			break;
		case R.id.rotation:
			
			break;
		case R.id.btn_down:
			if(mCurrent > 0) {
				mCurrent--;
				seekbar.setProgress(mCurrent);
			}
			break;
		case R.id.btn_up:
			if(mCurrent < max) {
				mCurrent++;
				seekbar.setProgress(mCurrent);
			}
			break;
		}
	}
}
