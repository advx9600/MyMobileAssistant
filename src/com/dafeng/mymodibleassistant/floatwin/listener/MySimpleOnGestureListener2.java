package com.dafeng.mymodibleassistant.floatwin.listener;

import wei.mark.standout.StandOutWindow.StandOutLayoutParams;
import wei.mark.standout.ui.Window;
import com.dafeng.mymodibleassistant.a;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindowInt;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

public class MySimpleOnGestureListener2 {

	private final int DOUBLE_CLICK_TIME = 300;
	private final int MIN_TOUCH_EVENT_DURATION = 500;
	private final int MAX_CLICK_TIME = 1000;

	private static SimpleFloatingWindowInt mMain;

	private boolean mIsJustHadLongPressEvent;
	private long mLastDownTime = 0;
	private int mLastDownX;
	private int mLastDownY;
	private int mMaxDurDis;

	private final static int EVENT_CLICK = 1;
	private final static int EVENT_LONG_PRESS = 2;
	private final static int EVENT_RIGHT_SLIP = 3;
	private final static int EVENT_LEFT_SLIP = 4;
	private final static int EVENT_BOTTOM_SLIP = 5;
	private final static int EVENT_TOP_SLIP = 6;

	private int mClickTimes = 0;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		private long lastDoTime = 0;

		public void handleMessage(Message msg) {
			if (System.currentTimeMillis() - lastDoTime < MIN_TOUCH_EVENT_DURATION) {
				lastDoTime = System.currentTimeMillis();
				return;
			}
			lastDoTime = System.currentTimeMillis();

			switch (msg.what) {
			case EVENT_CLICK:
				if (mClickTimes == 1) {
					a.c("onClick");
					mMain.onClick();
				} else if (mClickTimes == 2) {
					a.c("onDoubleClick");
					mMain.onDoubleClick();
				} else {
					a.c("onThreeClick");
					mMain.onThreeClick();
				}
				break;
			case EVENT_LONG_PRESS:
				a.b("longClickEvent");
				mMain.onLongClick();
				break;
			case EVENT_RIGHT_SLIP:
				a.c("onRightFlip");
				mMain.onRightFlip();
				break;
			case EVENT_LEFT_SLIP:
				a.c("onLeftFlip");
				mMain.onLeftFlip();
				break;
			case EVENT_BOTTOM_SLIP:
				a.c("onBottomFlip");
				mMain.onBottomFlip();
				break;
			case EVENT_TOP_SLIP:
				a.c("onTopFlip");
				mMain.onTopFlip();
				break;
			}
		}
	};

	private boolean mIsAtWait = false;;

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
				handler.sendEmptyMessage(EVENT_CLICK);
				mIsAtWait = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	private boolean mIsLongClickJudgeStarted = false;
	private boolean mIsHasUpEvent;

	private class LongClickJudgeThread extends Thread {
		@Override
		public void run() {
			mIsLongClickJudgeStarted = true;
			mIsHasUpEvent = false;
			mIsJustHadLongPressEvent = false;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!mIsHasUpEvent && mMaxDurDis < 10) {
				mIsJustHadLongPressEvent = true;
				handler.sendEmptyMessage(EVENT_LONG_PRESS);
			}
			mIsLongClickJudgeStarted = false;
		}
	};

	public MySimpleOnGestureListener2(SimpleFloatingWindowInt main) {
		mMain = main;
	}

	public void onTouch(MotionEvent event) {
		Window win = mMain.getFloatWindow();
		int x = win.getLayoutParams().x;
		int y = win.getLayoutParams().y;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMaxDurDis = 0;
			if (System.currentTimeMillis() - mLastDownTime > MAX_CLICK_TIME) {
				mClickTimes = 0;
			}
			mLastDownTime = System.currentTimeMillis();
			mLastDownX = x;
			mLastDownY = y;
			if (!mIsLongClickJudgeStarted) {
				new LongClickJudgeThread().start();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMaxDurDis < Math.sqrt((x - mLastDownX) * (x - mLastDownX)
					+ (y - mLastDownY) * (y - mLastDownY))) {
				mMaxDurDis = (int) Math.sqrt((x - mLastDownX)
						* (x - mLastDownX) + (y - mLastDownY)
						* (y - mLastDownY));
			}
			break;
		case MotionEvent.ACTION_UP:
			mIsHasUpEvent = true;
			mClickTimes++;
			long durTime = Math.abs(System.currentTimeMillis() - mLastDownTime);
			if (!mIsJustHadLongPressEvent) {
				if (mClickTimes == 1 && mMaxDurDis > 10 && durTime < 350) {
					onFlip(win);
				} else if (mClickTimes == 1 && mMaxDurDis < 5) { // click event					
					if (!mIsAtWait) {
						new ClickJudgeThread().start();
					}
				}
			}
			if (durTime < 350 ){
				restoreWindowParam(win);
			}
			mMain.saveLocationData(win);
			mIsJustHadLongPressEvent = false;
			break;
		}
	}

	private void restoreWindowParam(Window win) {
		StandOutLayoutParams params = win.getLayoutParams();
		if (params != null) {
			params.x = mLastDownX;
			params.y = mLastDownY;
			win.setLayoutParams(params);
			win.edit().commit();
		}
	}

	private void onFlip(Window win) {
		int xDis = mLastDownX - win.getLayoutParams().x;
		int yDis = mLastDownY - win.getLayoutParams().y;
		a.b("xDis:" + xDis + ",yDis:" + yDis);
		int rate = xDis / (yDis == 0 ? 1 : yDis);
		// a.b("xDis:" + xDis + ",yDis:" + yDis + ",rate:" + rate);
		if (Math.abs(rate) > 1) {
			if (xDis > 0) {
				handler.sendEmptyMessage(EVENT_LEFT_SLIP);
			} else {
				handler.sendEmptyMessage(EVENT_RIGHT_SLIP);
			}
		} else {
			if (yDis > 0) {
				handler.sendEmptyMessage(EVENT_TOP_SLIP);
			} else {
				handler.sendEmptyMessage(EVENT_BOTTOM_SLIP);
			}
		}
	}
}
