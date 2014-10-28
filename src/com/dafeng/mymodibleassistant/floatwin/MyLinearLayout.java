package com.dafeng.mymodibleassistant.floatwin;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();
		for (int i = 0; i < getChildCount(); i++) {
			// int firstWidth = getChildAt(0).getMeasuredWidth();
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				// a.b("childw:" + childw + ",width:" + width);
				if (xpos + childw >= width) {
					break;
				}
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				xpos += childw;
			}
		}
	}
}
