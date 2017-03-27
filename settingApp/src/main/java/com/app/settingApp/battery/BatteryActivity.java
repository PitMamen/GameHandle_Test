package com.app.settingApp.battery;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.view.ControlView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * 
* @Description: 电池界面
* @author chengkai  
* @date 2016年6月4日 上午9:37:28 
*
 */
public class BatteryActivity extends ActivityReceive {

	private View selectMode;
	
	private CheckBox checkBox;
	
	private RoundProgressBar progressBar1;
	
	private RoundProgressBar progressBar2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.battery), false);
		
		progressBar1 = (RoundProgressBar) findViewById(R.id.roundProgressBar1);
		progressBar2 = (RoundProgressBar) findViewById(R.id.roundProgressBar2);
		selectMode = findViewById(R.id.chose_battery_mode);
		checkBox = (CheckBox) findViewById(R.id.battery_select);
		
		progressBar1.setProgress(50);
		progressBar2.setProgress(50);
		
		selectMode.setFocusable(true);
		selectMode.setFocusableInTouchMode(true);
		selectMode.requestFocus();
		
		selectMode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkBox.setChecked(checkBox.isChecked() == false);
			}
		});
	}
}
