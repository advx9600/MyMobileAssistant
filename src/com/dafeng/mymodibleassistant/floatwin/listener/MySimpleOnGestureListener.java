package com.dafeng.mymodibleassistant.floatwin.listener;

import wei.mark.standout.StandOutWindow.StandOutLayoutParams;
import wei.mark.standout.ui.Window;
import com.dafeng.mymodibleassistant.a;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindowInt;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class MySimpleOnGestureListener extends SimpleOnGestureListener {
	private final int DOUBLE_CLICK_TIME = 250;

	private static SimpleFloatingWindowInt mMain;

	private long mLastDownTime = 0;
	private int mLastDownX;
	private int mLastDownY;

	private int mClickTimes = 0;
	private boolean mHasClickTimesEvent;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (mHasClickTimesEvent) {
					switch (mClickTimes) {
					case 1:
						a.c("onClick");
						mMain.onClick();
						break;
					case 2:
						a.c("onDoubleClick");
						mMain.onDoubleClick();
						break;
					case 3:
						a.c("onThreeClick");
						mMain.onThreeClick();
						break;
					case 4:
						a.c("onThreeClick");
						mMain.onThreeClick();
						break;
					}
				}
				mClickTimes = 0;
				break;
			}
		}
	};

	public MySimpleOnGestureListener(
			SimpleFloatingWindowInt simpleFloatingWindowInt) {
		mMain = simpleFloatingWindowInt;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		a.c("onLongClick");
		mMain.onLongClick();
	}

	private static boolean mIsAtWait = false;

	private class ClickJudgeThread extends Thread {
		@Override
		public void run() {
			try {
				mIsAtWait = true;
				int clicks = mClickTimes;
				while (true) {
					int i;
					for (i = 0; i < DOUBLE_CLICK_TIME; i += 10) {
						sleep(10);
						if (clicks != mClickTimes) {
							clicks = mClickTimes;
							break;
						}
					}
					if (i >= DOUBLE_CLICK_TIME) {
						break;
					}
				}
				handler.sendEmptyMessage(1);
				mIsAtWait = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private void doClickTimesPre(boolean isClick) {
		mHasClickTimesEvent = isClick;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		doClickTimesPre(true);
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		doClickTimesPre(true);
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (mClickTimes < 1) {
			mHasClickTimesEvent = false;
		}
		mClickTimes += 1;
		if (!mIsAtWait)
			new ClickJudgeThread().start();

		mLastDownTime = System.currentTimeMillis();
		mLastDownX = mMain.getFloatWindow().getLayoutParams().x;
		mLastDownY = mMain.getFloatWindow().getLayoutParams().y;
		return super.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		doClickTimesPre(false);

		boolean isFlipOk = false;

		int durTime = (int) Math
				.abs(System.currentTimeMillis() - mLastDownTime);
		Window win = mMain.getFloatWindow();

		if (durTime > 350) {
			// do nothing
		} else {
			int xDis = mLastDownX - win.getLayoutParams().x;
			int yDis = mLastDownY - win.getLayoutParams().y;
			a.b("xDis:"+xDis+",yDis:"+yDis);
			int rate = xDis / (yDis == 0 ? 1 : yDis);
			// a.b("xDis:" + xDis + ",yDis:" + yDis + ",rate:" + rate);
			isFlipOk = true;
			if (Math.abs(rate) > 1) {
				if (xDis > 0) {
					a.c("onLeftFlip");
					mMain.onLeftFlip();
				} else {
					a.c("onRightFlip");
					mMain.onRightFlip();
				}
			} else {
				if (yDis > 0) {
					a.c("onTopFlip");
					mMain.onTopFlip();
				} else {
					a.c("onBottomFlip");
					mMain.onBottomFlip();
				}
			}
		}

		if (isFlipOk) {
			StandOutLayoutParams params = win.getLayoutParams();
			if (params != null) {
				params.x = mLastDownX;
				params.y = mLastDownY;
				win.setLayoutParams(params);
				win.edit().commit();
			}
		}

		return false;
	}
}
