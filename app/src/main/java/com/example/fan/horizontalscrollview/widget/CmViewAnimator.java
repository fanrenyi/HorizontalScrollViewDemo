package com.example.fan.horizontalscrollview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewAnimator;

/**
 * @author blue
 * 为了避开2.1，2.2手机上当横竖屏转换时android的一个bug
 */
public class CmViewAnimator extends ViewAnimator {

	public CmViewAnimator(Context context) {
		this(context, null);
	}

	public CmViewAnimator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		try {
			super.onDetachedFromWindow();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * fix bug : android.app.IntentReceiverLeaked
	 */
	public void onDetachInFragment() {
		onDetachedFromWindow();
	}

}
