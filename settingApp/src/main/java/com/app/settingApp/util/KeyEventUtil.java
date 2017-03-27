package com.app.settingApp.util;

import java.lang.reflect.Field;

import android.util.Log;
import android.view.KeyEvent;

/**
 * 
* @Description: 使用放射修改keycode
* @author chengkai  
* @date 2016年6月1日 上午10:25:23 
*
 */
public class KeyEventUtil {
	
	private static final String TAG = "KeyEventUtil";

	public static void changeKeyCode(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
			setFieldCode(event,KeyEvent.KEYCODE_DPAD_DOWN);
		}else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
			setFieldCode(event,KeyEvent.KEYCODE_DPAD_UP);
		}
	}
	
	public static void setFieldCode(KeyEvent event, int keyCode) {
		Class clazz = event.getClass();
		try {
			Field name = clazz.getDeclaredField("mKeyCode");
			name.setAccessible(true);
	        Log.d(TAG, name.get(event)+","+event.getKeyCode());
	        name.set(event, keyCode);
	        name.setAccessible(false);
			Log.d(TAG, event.getKeyCode()+"");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
