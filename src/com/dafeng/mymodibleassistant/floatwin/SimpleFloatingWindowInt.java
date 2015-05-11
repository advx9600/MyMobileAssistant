package com.dafeng.mymodibleassistant.floatwin;

import wei.mark.standout.ui.Window;

public interface SimpleFloatingWindowInt {
	public static final String PREF_FILE_NAME = "SimpleFloatingWindow";
	public static final String PREF_floatwin_width = "float_width";
	public static final String PREF_floatwin_height = "float_height";
	public static final String PREF_floatwin_entry_width="float_entry_width";
	public static final String PREF_floatwin_entry_height="float_entry_height";
	public static final String PREF_floatwin_alpha="float_alpha";

	public void onClick();

	public void onLongClick();

	public void onRightFlip();

	public void onLeftFlip();

	public void onTopFlip();

	public void onBottomFlip();

	public void onDoubleClick();

	public void onThreeClick();
	// public void on45DegreenFlip(); // ��׼ȷ

	public void setStatus(long status);

	public boolean isStatus(long status);

	public void setOpsId(long id);
	
	// base ����ʵ�ֵķ���
	public wei.mark.standout.ui.Window getFloatWindow();
	
	public void saveLocationData(Window window);
}
