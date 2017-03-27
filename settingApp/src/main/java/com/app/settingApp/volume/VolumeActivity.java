package com.app.settingApp.volume;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.view.ControlView;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VolumeActivity extends ActivityReceive {

	private SeekBar volume;
	
	private Button volumeDown;
	private Button volumeUp;
	private TextView txtVol;
	
	private int mCurrent = 0;
	private int max = 0;
	private AudioManager mAudioManager = null;
	private String volFormat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏,全屏显示
        // 第一种：
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_volume);
		
		volume = (SeekBar) findViewById(R.id.seekbar_volume);
		volumeDown = (Button) findViewById(R.id.btn_volume_down);
		volumeUp = (Button) findViewById(R.id.btn_volume_up);
		txtVol = (TextView) findViewById(R.id.txt_vol);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.volume), false);
		
		volFormat = getString(R.string.volume_value);
		
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		
		//系统音量
		max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
		mCurrent = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
		
		volume.setMax(max);
		volume.setProgress(mCurrent);
		
		txtVol.setText(String.format(volFormat, mCurrent));
		
		volumeDown.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrent > 0) {
					mCurrent--;
					setVolume(mCurrent);
				}
			}
		});
		
		volumeUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCurrent < max) {
					mCurrent++;
					setVolume(mCurrent);
				}
				
			}
		});
		
		volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, AudioManager.FLAG_PLAY_SOUND);
				txtVol.setText(String.format(volFormat, progress));
				mCurrent = progress;
			}
		});
	}
	
	private void setVolume(int current) {
		volume.setProgress(current); 
//		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, current, AudioManager.FLAG_PLAY_SOUND);
//		txtVol.setText(String.format(getString(R.string.volume_value), current));
	}
}
