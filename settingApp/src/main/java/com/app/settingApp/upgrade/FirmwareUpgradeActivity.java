package com.app.settingApp.upgrade;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.view.ControlView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FirmwareUpgradeActivity extends ActivityReceive {

	private TextView upgrade;
	private TextView isNew;
	
	private UpgradeDialog dialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_firmware_upgrade);
		upgrade = (TextView) findViewById(R.id.upgrade);
		isNew = (TextView) findViewById(R.id.is_new);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.firmware), false);
		
		upgrade.setFocusable(true);
		upgrade.setFocusableInTouchMode(true);
		upgrade.requestFocus();
		
		upgrade.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showUpgradeDialog();
			}
		});
		
	}
	
	private void showUpgradeDialog() {
		if(dialog == null) {
			dialog = new UpgradeDialog(this);
		}
		dialog.show();
	}
	
}
