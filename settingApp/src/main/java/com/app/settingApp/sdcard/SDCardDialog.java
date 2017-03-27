package com.app.settingApp.sdcard;

import java.lang.ref.SoftReference;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.IMountServiceListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SDCardDialog extends ActivityReceive {

	private static final String TAG = "SDCardDialog"; 
	
	public static final int PULL_OUT = 1;
	public static final int INSERT = 2;
	public static final int FORMAT = 3;
	
	public static final int MOUNT_MSG = 1;
	public static final int UNMOUNT_MSG = 2;
	public static final int FORMAT_MSG = 3;
	
	private Button cancel;
	private Button btnRight;
	private TextView tvInfo;
	
	private String extStoragePath;
	
	private IMountService mMountService;
	
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sdcard);
		
		cancel = (Button) findViewById(R.id.cancel);
		btnRight = (Button) findViewById(R.id.btn_right);
		tvInfo = (TextView) findViewById(R.id.info);
		
		mType = getIntent().getIntExtra(SDCardActivity.DIALOG_TYPE, 0);
		showInfo();
		
		getMountService();
		try {
			mMountService.registerListener(mountServiceListener);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		Log.d(TAG, mMountService.getClass().getName());
		extStoragePath = Environment.getExternalStorageDirectory().toString();
		Log.d(TAG, extStoragePath);
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (mType) {
				case PULL_OUT:
					try {
						showProgressDialog();
						mMountService.unmountVolume(extStoragePath, true, true);
						
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
					break;
				case INSERT:
					showProgressDialog();
					new Thread(new Runnable() {
						public void run() {
							try {
								mMountService.mountVolume(extStoragePath);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					break;
				case FORMAT:
					
					formatSD();
					break;

				default:
					break;
				}
				
			}
		});
		
	}
	
	boolean isFormat = false;
	
	public void formatSD() {
        isFormat = true;
        try{
        	showProgressDialog();
        	mMountService.unmountVolume(extStoragePath, true,true);
        }catch(Exception e){
            System.out.println("Exception format: "+e.getMessage());
        }
    }
	
	private void showProgressDialog() {
		if(dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setTitle(R.string.progress_title);
		}
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}
	
	private void dismissProgressDialog() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	IMountServiceListener.Stub mountServiceListener = new IMountServiceListener.Stub() {
		
		@Override
		public void onUsbMassStorageConnectionChanged(boolean connected) throws RemoteException {
//			Toast.makeText(mContext, "connected"+connected,Toast.LENGTH_SHORT).show();;
			Log.d(TAG, "connected"+connected);
		}
		
		@Override
		public void onStorageStateChanged(String path, String oldState, String newState) throws RemoteException {
			Log.d(TAG, "path:"+path+",oldState:"+oldState+",newState:"+newState);
			if("unmounted".equals(newState)) {
				Handler handler = reference.get();
				if(handler != null) {
					Message message = handler.obtainMessage();
					Bundle data = new Bundle();
					data.putString("path", path);
					data.putString("oldState", oldState);
					data.putString("newState", newState);
					message.setData(data);
					message.what = UNMOUNT_MSG;
					if(!handler.hasMessages(UNMOUNT_MSG)) {
						handler.sendMessage(message);
					}
					
				}
				
				if(isFormat) {
					showProgressDialog();
					int result_format = mMountService.formatVolume(extStoragePath);        //格式化成功
					Log.d(TAG, "result_format:"+ result_format);
					Message message1 = handler.obtainMessage();
					Bundle data1 = new Bundle();
					data1.putInt("result_format", result_format);
					message1.setData(data1);
					message1.what = FORMAT_MSG;
					handler.sendMessage(message1);
		            if(result_format==0){
		            	showProgressDialog();
		                //问题出现在此，mountVolume函数将SD CARD 挂载回去失败?? 数据无法写入，其状态处于UNMOUNTED
		                int result_mount = mMountService.mountVolume(extStoragePath);
		                System.out.println("result_mount:  "+ result_mount);
		            }
				}
			}else if("mounted".equals(newState)) {
				Handler handler = reference.get();
				if(handler != null) {
					Message message = handler.obtainMessage();
					Bundle data = new Bundle();
					data.putString("path", path);
					data.putString("oldState", oldState);
					data.putString("newState", newState);
					message.setData(data);
					message.what = MOUNT_MSG;
					if(!handler.hasMessages(MOUNT_MSG)) {
						handler.sendMessage(message);
					}
				}
			}
		}
	};
	
	class MYHandler extends Handler{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MOUNT_MSG: {
				Bundle data = msg.getData();
				String path = data.getString("path");
				String oldState = data.getString("oldState");
				String newState = data.getString("newState");
				Toast.makeText(SDCardDialog.this, "path:"+path+",oldState:"+oldState+",newState:"+newState,Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				finish();
				break;
			}
			case UNMOUNT_MSG: {
				Bundle data = msg.getData();
				String path = data.getString("path");
				String oldState = data.getString("oldState");
				String newState = data.getString("newState");
				Toast.makeText(SDCardDialog.this, "path:"+path+",oldState:"+oldState+",newState:"+newState,Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				if(!isFormat) {
					finish();
				}
				break;
			}
			case FORMAT_MSG: {
				dismissProgressDialog();
				Bundle data = msg.getData();
				int result_format = data.getInt("result_format");
				Toast.makeText(SDCardDialog.this, "result_format: "+result_format, Toast.LENGTH_SHORT).show();
				break;
			}

			default:
				break;
			}
			
		};
	};
	
	private SoftReference<MYHandler> reference = new SoftReference<SDCardDialog.MYHandler>(new MYHandler());
	
	private synchronized void getMountService() {
       if (mMountService == null) {
           IBinder service = ServiceManager.getService("mount");
           if (service != null) {
               mMountService = IMountService.Stub.asInterface(service);
           } else {
               Log.e("getMountService", "Can't get mount service");
           }
       }
    }
	
	int mType = 0;
	
	public void showInfo() {
		cancel.setSelected(true);
		String strRight = null;
		String info = null;
		switch (mType) {
		case PULL_OUT:
			strRight = getString(R.string.sd_remove);
			info = getString(R.string.sd_pull_out_info);
			break;
		case INSERT:
			strRight = getString(R.string.sd_insert);
			info = getString(R.string.sd_insert_info);
			break;
		case FORMAT:
			strRight = getString(R.string.sd_format);
			info = getString(R.string.sd_format_info);
			break;

		default:
			break;
		}
		
		btnRight.setText(strRight);
		tvInfo.setText(info);
		
	}

}
