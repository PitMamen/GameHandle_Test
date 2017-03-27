package com.app.settingApp.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;


/**
 * 
* @Description: 储存文件类
* @author chengkai  
* @date 2016年5月27日 上午10:21:05 
*
 */
public class MemoryUtil {

	private static final String TAG = "MemoryUtil";
	
	private static final String SIZE_GB = "GB";
	private static final String SIZE_MB = "MB";
	private static final String SIZE_KB = "KB";
	private static final String SIZE_BB = "BB";
	
	private static final float SIZE = 1024;
	
	Context mContext;

	public MemoryUtil(Context context) {
		mContext = context;
	}

	public static String memorySizeChange(long size) {
		float beforeResult = 0; 
		float result = size / SIZE;
		if(result < 0.1 ) {
			return String.format("%d" +SIZE_BB, size );
		}
		beforeResult = result;
		result = result / SIZE;
		if(result < 0.1 ) {
			return String.format("%.2f" +SIZE_KB, beforeResult);
		}
		beforeResult = result;
		result = result / SIZE;
		if(result < 0.1 ) {
			return String.format("%.2f" +SIZE_MB, beforeResult);
		}
		return String.format("%.2f" +SIZE_GB, result);
	}
	
	public long getAppsSize() {
		
		List<ApplicationInfo> installedAppList = mContext.getPackageManager()
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		if (installedAppList == null) {
			Log.d(TAG, "installedAppList is null");
			return 0;
		}
		/*List<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo appInfo : installedAppList) {
//			boolean flag = false;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				flag = true;
			}
			if (flag) {
				appList.add(appInfo);
//			}
		}*/
		long totalSize = 0l;
		for (int i = 0; i < installedAppList.size(); i++) {
			ApplicationInfo p = installedAppList.get(i);
			String dir = p.publicSourceDir;
			int size = Integer.valueOf((int) new File(dir).length());
			totalSize += size;
		}
		Log.d(TAG, "appsize" +totalSize);
		return totalSize;
	}
	
	void getSDCardpath () {
		StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);  
		try {  
		    Class<?>[] paramClasses = {};  
		    Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);  
		    getVolumePathsMethod.setAccessible(true);  
		    Object[] params = {};  
		    Object invoke = getVolumePathsMethod.invoke(storageManager, params);  
		    for (int i = 0; i < ((String[])invoke).length; i++) {  
//		        System.out.println(((String[])invoke)[i]);  
		        Log.d(TAG, ((String[])invoke)[i]);
		    }  
		} catch (NoSuchMethodException e1) {  
		    e1.printStackTrace();  
		} catch (IllegalArgumentException e) {  
		    e.printStackTrace();  
		} catch (IllegalAccessException e) {  
		    e.printStackTrace();  
		} catch (InvocationTargetException e) {  
		    e.printStackTrace();  
		}
	}
	
	/**
	 * 
	* @Description: 获取媒体文件大小
	* @param uri
	* @param size
	* @return    设定文件 
	* @return long    返回类型 
	* @throws
	 */
	public long getMediaSize(Uri uri, String size) {
		Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
		// IMediaScannerListener
		if (cursor == null) {
			Log.d(TAG, "cursor is null");
			return 0;
		}
		Log.d(TAG, "cursor is not null");
		Long musicTotalSize = 0l;
		if (cursor.moveToFirst()) {
			int sizeIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
			do {
				musicTotalSize += cursor.getLong(sizeIndex); // 获取文件名，不包含扩展名

			} while (cursor.moveToNext()); // 循环获取文件
		} else {
			Log.d(TAG, "cursor is imputy");
		}
		cursor.close();
		return musicTotalSize;
	}

	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public long getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	public long getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public long getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return blockSize * totalBlocks;
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/**
	 * 外部存储中所有音频文件所占内存
	 * 
	 * @return
	 */
	public long getAudioTotalSize() {
		ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		long size = 0L;
		for (MemoryInfo cInfo : resultList) {
			File file = new File(cInfo.getFilePath());
			if (null != file && file.exists()) {
				size += cInfo.getFileSize();
			}
		}
		return size;
	}

	/**
	 * 外部存储中除音频、视频、图片之前其他文件所占内存
	 * 
	 * @return
	 */
	public long getOtherTotalSize() {
		long size = getSDTotalSize() - getSDAvailableSize() - getPictureTotalSize() - getVideoTotalSize()
				- getAudioTotalSize();
		if (size < 0L) {
			size = 0L;
		}
		return size;
	}

	/**
	 * 外部存储中所有图片文件所占内存
	 * 
	 * @return
	 */
	public long getPictureTotalSize() {
		ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		long size = 0L;
		for (MemoryInfo cInfo : resultList) {
			File file = new File(cInfo.getFilePath());
			if (null != file && file.exists()) {
				size += cInfo.getFileSize();
			}
		}
		return size;
	}

	/**
	 * 外部存储中所有视频文件所占内存
	 * 
	 * @return
	 */
	public long getVideoTotalSize() {
		ArrayList<MemoryInfo> resultList = queryAllMediaList(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		long size = 0L;
		for (MemoryInfo cInfo : resultList) {
			File file = new File(cInfo.getFilePath());
			if (null != file && file.exists()) {
				size += cInfo.getFileSize();
			}
		}
		return size;
	}

	public ArrayList<MemoryInfo> queryAllMediaList(Uri uri) {
		// 我们只需要两个字段：大小、文件路径
		Cursor cursor = mContext.getContentResolver().query(uri,
				new String[] { MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA }, null, null, null);

		ArrayList<MemoryInfo> musicList = new ArrayList<MemoryInfo>();

		try {
			if (cursor.moveToFirst()) {
				do {
					MemoryInfo mInfo = new MemoryInfo();
					mInfo.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
					mInfo.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
				} while (cursor.moveToNext());
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return musicList;

	}

	class MemoryInfo {
		private long fileSize = 0L;
		private String filePath = "";

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
	}
}
