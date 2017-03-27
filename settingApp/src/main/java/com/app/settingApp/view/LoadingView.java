package com.app.settingApp.view;

/**
 * Copyright © 2013. All rights reserved.
 *
 * @Title: LoadingView.java
 * @Prject: 
 * @Package: com.app.settingApp.view
 * @Description: 加载旋转
 * @author: czq
 * @date: 2016年05月25日
 * @version: v1.0
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class LoadingView extends ImageView implements Runnable {

	// 旋转角度
	int mDegree = 0;
	// 定时器
	private Handler mHandler = null;
	private final int mPeriod = 200;
	
	
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		int nWidth = getWidth()/2;
		int nHeight = getHeight()/2;
		
		canvas.rotate(mDegree, nWidth, nHeight);
		Drawable drawable = getDrawable();
		if (drawable != null) {
			drawable.draw(canvas);
		}	
	}
	
	@Override
	public void run() {
		mDegree += 30;
		mDegree %= 360;
		postInvalidate();
		if (mHandler != null) {
			mHandler.postDelayed(this, mPeriod);
		}
		
	}
	

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		// 重置
		//mCurIndex = 0;
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}
		
		if (View.VISIBLE == visibility) {
			// 显示
			mHandler = new Handler();
			mHandler.postDelayed(this, mPeriod);
		}
		
		// TODO Auto-generated method stub
		super.onWindowVisibilityChanged(visibility);
	}
}

