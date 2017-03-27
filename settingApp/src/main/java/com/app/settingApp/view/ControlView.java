package com.app.settingApp.view;

/**
 * Copyright © 2014. All rights reserved.
 *
 * @Title: PlayControlListView.java
 * @Prject: TvbApp
 * @Package: com.sz.ead.app.VBox.view
 * @Description: 直播控制
 * @author: 李英英
 * @date: 2014年2月8日 上午11:16:26
 * @version: V1.0
 */


import java.util.ArrayList;

import com.app.settingApp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ControlView extends RelativeLayout{

	private TextView				mTitleView;
	private ImageView				mTitleImg;

	private RelativeLayout 			mLayoutInflate;
	
	// 定时器
	private Context mContext;

	public ControlView(Context context) {
		this(context, null);
	}

	public ControlView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ControlView(Context context, AttributeSet attrs, int defaultstyle) {
		super(context, attrs, defaultstyle);
		mContext = context;
		
		initView();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	
	
	private void initView() {
		findViews();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		addView(mLayoutInflate, lp);
	
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	private void findViews() {
		mLayoutInflate = (RelativeLayout)View.inflate(mContext, R.layout.layout_control, null);
		mTitleView = (TextView)mLayoutInflate.findViewById(R.id.title_text);
		mTitleImg = (ImageView)mLayoutInflate.findViewById(R.id.title_img);
	}

	public void setTitleText(String text,boolean flag){
		mTitleView.setText(text);
		if(flag){
			mTitleImg.setVisibility(View.VISIBLE);
		}
		else{
			mTitleImg.setVisibility(View.GONE);
		}
	}
}
