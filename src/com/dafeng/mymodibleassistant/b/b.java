package com.dafeng.mymodibleassistant.b;

import android.app.Service;

public class b {
	// private int mWidth;
	// private int mHeight;

	// @SuppressWarnings("deprecation")
	public b(Service s) {
		// Display display = ((WindowManager) s
		// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// mWidth = display.getWidth();
		// mHeight = display.getHeight();
	}

	private static int properNum() {
		// int max = mWidth > mHeight ? mWidth : mHeight;
		int setNum = 80;
		return setNum;
	}

	public static int getProperWidth() {
		return properNum();
	}

	public static int getProperHeight() {
		return properNum();
	}

}
