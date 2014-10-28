package com.dafeng.mymodibleassistant.floatwin;

public interface SimpleFloatingWindowInt {
	public static final String PREF_FILE_NAME = "SimpleFloatingWindow";
	public static final String PREF_floatwin_width = "float_width";
	public static final String PREF_floatwin_height = "float_height";

	public void onClick();

	public void onLongClick();

	public void onRightFlip();

	public void onLeftFlip();

	public void onTopFlip();

	public void onBottomFlip();

	// public void on45DegreenFlip(); // ²»×¼È·

	public void setStatus(long status);

	public boolean isStatus(long status);

	public void setOpsId(long id);
}
