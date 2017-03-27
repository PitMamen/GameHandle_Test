package com.app.settingApp.view;

import com.app.settingApp.R;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeItemView extends RelativeLayout {
	private Context   mContext;   
	private ImageView mIcon;         
	private TextView  mName; 
	
	public HomeItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		inflate(mContext, R.layout.item_home, this);
		mIcon = (ImageView) findViewById(R.id.item_icon);
		mName = (TextView) findViewById(R.id.item_name);
		
	}
	
	public TextView getNameText(){
		return mName;
	}
	
	public void setName(String name) {
		mName.setText(name);
	}
	
	/**
	 * 
	 * @Title: setName
	 * @Description: 设置名称(关键字高亮显示)
	 * @param name
	 * @return: void
	 */
	public void setName(SpannableString name) {
		mName.setText(name);
	}

	
	public void setIndex(int index) {
		int i = index%2;
		mIcon.setImageResource(R.drawable.setting);
		/*if(0 == index/4%2)
		{
			switch (i) {
				case 0: {
					mIcon.setImageResource(R.drawable.home_item_g);
					break;
				}
				
				case 1: {
					mIcon.setImageResource(R.drawable.home_item_w);
					break;
				}
	
				default:
					break;
			}
		}
		else
		{
			switch (i) {
				case 0: {
					mIcon.setImageResource(R.drawable.home_item_w);
					break;
				}
				
				case 1: {
					mIcon.setImageResource(R.drawable.home_item_g);
					break;
				}
	
				default:
					break;
			}
		}*/
	}

}
