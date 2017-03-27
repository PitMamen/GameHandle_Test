package com.app.settingApp.sdcard;

import java.io.File;

import com.app.settingApp.R;
import com.app.settingApp.activity.ActivityReceive;
import com.app.settingApp.util.KeyEventUtil;
import com.app.settingApp.util.MemoryUtil;
import com.app.settingApp.view.ControlView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * 
 * @Description: TODO(这里用一句话描述这个类的作用) 内置sd卡和外置sd卡bug
 * @author chengkai
 * @date 2016年5月25日 下午4:16:52
 *
 */
public class SDCardActivity extends ActivityReceive {

	public static final String TAG = "SDCardActivity";
	
	public static final String MOUNTED = "mounted";
	public static final String NOT_MOUNTED = "unmounted";
	
	public static final String DIALOG_TYPE = "dialog_type";

	private TextView sdTatleMemory;
	private TextView sdAvailableMemory;
	private TextView sdVideosMemory;
	private TextView sdAPPMemory;
	private TextView sdETCMemory;
	
	private TextView SDMount;
//	private TextView SDFormat;
	
	private View memoryView;
	private View appsView;
	private View videosView;
	private View etcView;
	private View availableView;

	private SDCardDialog dialog = null;
	
	private String mountStr	= null;
	
	private int mountType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sdcard);

		sdTatleMemory = (TextView) findViewById(R.id.sd_total_memory);
		sdAvailableMemory = (TextView) findViewById(R.id.sd_available_memory);
		sdVideosMemory = (TextView) findViewById(R.id.sd_videos_memory);
		sdAPPMemory = (TextView) findViewById(R.id.sd_apps_memory);
		sdETCMemory = (TextView) findViewById(R.id.sd_etc_memory);
		
		SDMount = (TextView) findViewById(R.id.sd_mount);
		
		memoryView = findViewById(R.id.memory_size);
		appsView = findViewById(R.id.apps_size);
		videosView = findViewById(R.id.videos_size);
		etcView = findViewById(R.id.etc_size);
		availableView = findViewById(R.id.available_size);
		
		((ControlView) findViewById(R.id.control_view)).setTitleText(getString(R.string.sdcard), false);

		MemoryUtil memoryUtil = new MemoryUtil(this);

		long appTotalSize = memoryUtil.getAppsSize();
		long videosTotalSize = memoryUtil.getMediaSize(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.SIZE);
		videosTotalSize += memoryUtil.getMediaSize(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.SIZE);
		
		long SDTatalSize = memoryUtil.getSDTotalSize();
		long SDAvailableSize = memoryUtil.getSDAvailableSize();
		
		long ETCSize = SDTatalSize - SDAvailableSize - appTotalSize - videosTotalSize;
		
		sdAPPMemory.setText("APPS \n" + Formatter.formatFileSize(this, appTotalSize));
		sdVideosMemory.setText("Videos \n" + Formatter.formatFileSize(this, videosTotalSize));
		sdAvailableMemory.setText("Available \n" + Formatter.formatFileSize(this, SDAvailableSize));
		sdETCMemory.setText("ETC \n" + Formatter.formatFileSize(this, ETCSize));
		sdTatleMemory.setText(Formatter.formatFileSize(this, SDTatalSize));
		
		float appsScale = 1.0f * appTotalSize / SDTatalSize;
		float videosScale = 1.0f * videosTotalSize / SDTatalSize;
		float etcScale = 1.0f * ETCSize / SDTatalSize;
		float availableScale = 1.0f * SDAvailableSize / SDTatalSize;
		
		setMemoryView(appsScale, videosScale, etcScale, availableScale);
		
		initListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			File file = Environment.getExternalStorageDirectory();
			Log.d(TAG, file.toString());
			mountStr = getMountService().getVolumeState(file.toString());
			Log.d(TAG, mountStr);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if(MOUNTED.equals(mountStr)) {
			SDMount.setText(R.string.sd_pull_out_title);
			mountType = SDCardDialog.PULL_OUT;
		}else if(NOT_MOUNTED.equals(mountStr)) {
			mountType = SDCardDialog.INSERT;
			SDMount.setText(R.string.sd_insert_title);
		}
	};
	
	IMountService mMountService;
	
	private synchronized IMountService getMountService() {
       if (mMountService == null) {
           IBinder service = ServiceManager.getService("mount");
           if (service != null) {
               mMountService = IMountService.Stub.asInterface(service);
           } else {
               Log.e("getMountService", "Can't get mount service");
           }
       }
       return mMountService;
    }
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		KeyEventUtil.changeKeyCode(event);
		return super.dispatchKeyEvent(event);
	}
	
	private void initListener() {
		SDMount.setFocusable(true);
		SDMount.setFocusableInTouchMode(true);
		SDMount.requestFocus();
		
		SDMount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mountType != 0) {
					showDialogByType(mountType);
				}
			}
		});
	
		/*findViewById(R.id.sd_format).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialogByType(SDCardDialog.FORMAT);
			}
		});*/
		
	}
	
	private void showDialogByType(int type) {
		Intent intent = new Intent(this,SDCardDialog.class);
		intent.putExtra(DIALOG_TYPE, type);
		startActivity(intent);
	}
	
	private void setMemoryView (final float appsScale,final float videosScale, final float etcScale, final float availableScale) {
		memoryView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				int width = memoryView.getMeasuredWidth();
				appsView.getLayoutParams().width = (int) (width * appsScale);
				videosView.getLayoutParams().width = (int) (width * videosScale);
				etcView.getLayoutParams().width = (int) (width * etcScale);
//				availableView.getLayoutParams().width = (int) (width * availableScale);
			}
		});
	}
	
}
