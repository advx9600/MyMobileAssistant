package com.dafeng.mymodibleassistant.floatwin;

import java.util.ArrayList;
import java.util.List;

import com.dafeng.mymodibleassistant.R;
import com.dafeng.mymodibleassistant.a;
import com.dafeng.mymodibleassistant.b.c;
import com.dafeng.mymodibleassistant.dao.DaoSession;
import com.dafeng.mymodibleassistant.dao.TbApp;
import com.dafeng.mymodibleassistant.dao.TbAppDao;
import com.dafeng.mymodibleassistant.dao.TbJumpDao;
import com.dafeng.mymodibleassistant.dao.TbShortcutDao;
import com.dafeng.mymodibleassistant.db.DB;
import com.dafeng.mymodibleassistant.present.AppPresent;
import com.dafeng.mymodibleassistant.util.Util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.ui.Window;

public class SimpleFloatingWindowBase extends StandOutWindow {
	public static final int MY_DEFAULT_ID = 0x1000;
	protected static final int POP_WIN_ID = 0x1001;

	public final static long STATUS_NORMAL = 1;
	public final static long STATUS_POS_INDEPENDENT = (0x1 << 1);
	public final static long STATUS_ADD_SHOW_PAGE = (0x1 << 2);
	public final static long STATUS_NOT_SHOW_THIS_PAGE = (0x1 << 3);
	public final static long STATUS_ADD_JUMP_APP_SHORTCUT = (0x1 << 4);
	public final static long STATUS_DEL_JUMP_APP_SHORTCUT = (0x1 << 5);
	public final static long STATUS_MOD_JUMP_APP_SHORTCUT = (0x1 << 6);
	public final static long STATUS_ADD_APP_INPUTMETHOD = (0x1 << 7);
	public final static long STATUS_CANCEL_APP_INPUTMETHOD = (0x1 << 8);
	public final static long STATUS_ADD_APP_SHORTCUT = (0x1 << 9);
	public final static long STATUS_JUMP_TO_APP_SHORTCUT = (0x1 << 10);
	public final static long STATUS_DEL_APP_SHORTCUT = (0x1 << 11);
	public final static long STATUS_MOD_APP_SHORTCUT = (0x1 << 12);
	public final static long STATUS_ADD_APP_SHORTCUT_INPUTMETHOD = (0x1 << 13);
	public final static long STATUS_CANCEL_APP_SHORTCUT_INPUTMETHOD = (0x1 << 14);
	public final static long STATUS_THIS_PAGE_TEMP_HIDE = (0x1 << 15);
	public final static long STATUS_SYSTEM_PKG_CHANGE = (0x1 << 16);

	private static List<Long> LIST_ACTION_ALWAY_SHOW;

	static {
		LIST_ACTION_ALWAY_SHOW = new ArrayList<Long>();
		LIST_ACTION_ALWAY_SHOW.add(STATUS_ADD_SHOW_PAGE);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_ADD_JUMP_APP_SHORTCUT);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_MOD_JUMP_APP_SHORTCUT);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_ADD_APP_INPUTMETHOD);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_ADD_APP_SHORTCUT);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_MOD_APP_SHORTCUT);
		LIST_ACTION_ALWAY_SHOW.add(STATUS_ADD_APP_SHORTCUT_INPUTMETHOD);
	}

	public static Handler handle;

	protected SharedPreferences mShare;

	protected static Window mLastWindow;
	protected static int mLastDownX;
	protected static int mLastDownY;

	protected boolean mIsQuit = false; // only for MY_DEFAULT_ID

	public static boolean IsShowInCenter = false;
	public static boolean IsPopShowDefault = false;

	protected String mTopActivePkg = "";
	protected String mTopActiveName = "";

	protected SQLiteDatabase mDb;
	protected DaoSession mDaoSession;
	protected TbAppDao mAppDao;
	protected TbShortcutDao mShortcutDao;
	protected TbJumpDao mJumpToAppDao;
	protected TbApp mApp;
	protected List<TbApp> mListTbJump;
	protected List<TbApp> mListAppshortcut;

	public long mStatus = STATUS_NORMAL;

	private long mLastDownTime = 0;

	protected boolean mIsTempIgnoreTouch = false;

	protected ImageView mFloatImg;

	protected List<TbApp> mListPreApp = new ArrayList<>();

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate() {
		super.onCreate();
		initDB();
		mShare = this.getSharedPreferences(
				SimpleFloatingWindowInt.PREF_FILE_NAME, MODE_PRIVATE);
		if (handle == null)
			handle = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						mApp = AppPresent.getTbApp(mTopActivePkg,
								mTopActiveName);
						mListTbJump = null;
						if (mApp != null) {
							mListTbJump = AppPresent
									.getJumpApps(SimpleFloatingWindowBase.this,
											mApp.getId());
						}
						mListAppshortcut = AppPresent
								.getShortcuts(SimpleFloatingWindowBase.this);
						if (mApp != null) {
							if (mApp.getIsShow()) {
								show2();
								addPreApp(mApp);
							} else {
								hide2();
							}

							Window window = getWindow(MY_DEFAULT_ID);
							if (mApp.getIsPosIndependent()) {
								setStatus(STATUS_POS_INDEPENDENT, false);
								c.a(window, mApp);
							} else {
								setStatus(~STATUS_POS_INDEPENDENT, false);
								c.b(window, mShare);
							}
							if (!isNeedAlwaysShow()) {
								if (mListTbJump.size() > 0) {
									mFloatImg
											.setImageDrawable(Util
													.getIconByAppPkg(
															SimpleFloatingWindowBase.this,
															mListTbJump.get(0)
																	.getPkg()));
									mFloatImg
											.setBackgroundColor(Color.TRANSPARENT);
								} else {
									mFloatImg.setImageDrawable(null);
									mFloatImg
											.setBackgroundResource(R.drawable.floating_icon);
								}
							}
						} else {
							hide2();
						}
						break;
					case 2:
						mIsTempIgnoreTouch = false;
						break;
					case 3: // uninstall package
						String pkg = (String) msg.obj;
						TbApp entry = AppPresent.getTbApp(pkg, null);
						if (entry != null) {
							((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
									.setOpsId(entry.getId());
							((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
									.setStatus(STATUS_SYSTEM_PKG_CHANGE);
						}
					}
				}
			};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDb != null)
			mDb.close();
		handle = null;
	}

	protected void initDB() {
		mDb = DB.getWritableDb(this);
		mDaoSession = DB.getDaoSession(mDb);
		mAppDao = mDaoSession.getTbAppDao();
		mJumpToAppDao = mDaoSession.getTbJumpDao();
		mShortcutDao = mDaoSession.getTbShortcutDao();
		AppPresent.setDao(mAppDao, mJumpToAppDao, mShortcutDao);
	}

	@Override
	public String getAppName() {
		return "SimpleWindow";
	}

	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_close_clear_cancel;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// TODO Auto-generated method stub

	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void toast(int id) {
		Toast.makeText(this.getApplicationContext(), id, Toast.LENGTH_LONG)
				.show();
	}

	private void savePopLocationData(Window window) {
		mShare.edit().putInt("pop_x", window.getLayoutParams().x).commit();
		mShare.edit().putInt("pop_y", window.getLayoutParams().y).commit();
	}

	protected void saveLocationData(Window window) {
		if (((SimpleFloatingWindowInt) (this)).isStatus(STATUS_POS_INDEPENDENT)) {
			mApp.setX(window.getLayoutParams().x);
			mApp.setY(window.getLayoutParams().y);
			mAppDao.update(mApp);
		} else {
			mShare.edit().putInt("x", window.getLayoutParams().x).commit();
			mShare.edit().putInt("y", window.getLayoutParams().y).commit();
		}
	}

	protected boolean isNeedAlwaysShow() {
		for (int i = 0; i < LIST_ACTION_ALWAY_SHOW.size(); i++) {
			if (((SimpleFloatingWindowInt) (this))
					.isStatus(LIST_ACTION_ALWAY_SHOW.get(i))) {
				return true;
			}
		}
		return false;
	}

	protected void setReverseStatus() {
		for (int i = 0; i < LIST_ACTION_ALWAY_SHOW.size(); i++) {
			if (((SimpleFloatingWindowInt) (this))
					.isStatus(LIST_ACTION_ALWAY_SHOW.get(i))) {
				((SimpleFloatingWindowInt) (this))
						.setStatus(~LIST_ACTION_ALWAY_SHOW.get(i));
			}
		}
	}

	@SuppressWarnings("deprecation")
	final GestureDetector gestureDetector = new GestureDetector(

	new GestureDetector.SimpleOnGestureListener() {
		// private int SWIPE_MIN_DISTANCE = 0;
		private int SWIPE_THRESHOLD_VELOCITY = 0;

		@Override
		public void onLongPress(MotionEvent e) {
			a.c("onLongClick");
			((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
					.onLongClick();
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			a.c("onDoubleTap");
			((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
					.onDoubleClick();
			return false;
		}

		public boolean onSingleTapConfirmed(MotionEvent e) {
			((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
					.onClick();
			a.c("onClick");
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (SWIPE_THRESHOLD_VELOCITY < 1) {
				final ViewConfiguration vc = ViewConfiguration
						.get(SimpleFloatingWindowBase.this);
				// SWIPE_MIN_DISTANCE = vc.getScaledPagingTouchSlop();
				SWIPE_THRESHOLD_VELOCITY = vc.getScaledMinimumFlingVelocity();
			}
			boolean isFlipOk = false;

			int durTime = (int) Math.abs(System.currentTimeMillis()
					- mLastDownTime);
			if (durTime > 350) {
				a.c("onSlowFlip");
				((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
						.onSlowFlip();
				isFlipOk = true;
			} else {

				int xDis = mLastDownX - mLastWindow.getLayoutParams().x;
				int yDis = mLastDownY - mLastWindow.getLayoutParams().y;
				int rate = xDis / (yDis == 0 ? 1 : yDis);
				// a.b("xDis:" + xDis + ",yDis:" + yDis + ",rate:" + rate);
				if (Math.abs(rate) > 1) {
					isFlipOk = true;
					if (xDis > 0) {
						a.c("onLeftFlip");
						((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
								.onLeftFlip();
					} else {
						a.c("onRightFlip");
						((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
								.onRightFlip();
					}
				} else {
					isFlipOk = true;
					if (yDis > 0) {
						a.c("onTopFlip");
						((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
								.onTopFlip();
					} else {
						a.c("onBottomFlip");
						((SimpleFloatingWindowInt) (SimpleFloatingWindowBase.this))
								.onBottomFlip();
					}
				}
			}
			if (isFlipOk) {
				StandOutLayoutParams params = mLastWindow.getLayoutParams();
				if (params != null) {
					params.x = mLastDownX;
					params.y = mLastDownY;
					mLastWindow.setLayoutParams(params);
					mLastWindow.edit().commit();

					saveLocationData(mLastWindow);
				}
			}

			return false;
		}
	});

	@Override
	public boolean onTouchBody(int id, Window window, View view,
			MotionEvent event) {
		if (id == POP_WIN_ID) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				savePopLocationData(window);
			}
		} else {
			if (!mIsTempIgnoreTouch) {
				gestureDetector.onTouchEvent(event);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLastDownTime = System.currentTimeMillis();
					mLastWindow = window;
					mLastDownX = window.getLayoutParams().x;
					mLastDownY = window.getLayoutParams().y;
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					mLastWindow = window;
					saveLocationData(window);
					break;
				}
			}
		}
		return super.onTouchBody(id, window, view, event);
	}

	private void addPreApp(TbApp app) {
		String pkg = app.getPkg();
		String name = app.getName();

		if (pkg == null || name == null || pkg.length() == 0
				|| name.length() == 0
				|| pkg.equals(Util.getLauncherActivityInfo(this).packageName)) {
			return;
		}
		final int count = mListPreApp.size();
		if (count == 0) {
			mListPreApp.add(app);
		} else {
			for (int i = 0; i < count; i++) {
				TbApp temp = mListPreApp.get(i);
				if (pkg.equals(temp.getPkg()) && name.equals(temp.getName())) {
					if (i != count - 1) {
						mListPreApp.remove(i);
						mListPreApp.add(temp);
					}
					break;
				} else if (i == count - 1) {
					mListPreApp.add(app);
				}
			}
		}
	}

	protected void startPreApp(int steps) {
		boolean isHave = false;
		int curStep = 0;
		for (int i = mListPreApp.size() - 1; i > -1; i--) {
			TbApp app = mListPreApp.get(i);
			if (!mTopActivePkg.equals(app.getPkg())
					&& Util.isPkgInstalled(this, app.getPkg())) {
				curStep--;
				if (curStep == steps) {
					startOutSideActivity(app);
					isHave = true;
					break;
				}
			}
		}
		if (!isHave) {
			toast(R.string.no_history);
		}
	}

	protected void startOutSideActivity(TbApp shortcut) {
		startOutSideActivity(shortcut.getPkg(), shortcut.getName(),
				shortcut.getIsShowInputPicker(), shortcut.getInputMethod());
	}

	protected void startOutSideActivity(String pkg, String name) {
		startOutSideActivity(pkg, name, false, null);
	}

	private long tStartOutSideActivity_LastTime = 0;

	private void startOutSideActivity(String pkg, String name,
			boolean isShowInputMethod, String curInputMethod) {

		boolean isHasStarted = false;
		try {
			final ActivityManager am = (ActivityManager) this
					.getSystemService(ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(100);
			for (int i = 0; i < taskInfo.size(); i++) {
				final ActivityManager.RunningTaskInfo info = taskInfo.get(i);
				if (pkg.equals(info.topActivity.getPackageName())) {
					/* android权限问题,切换时间要大于N秒 */
					if (Math.abs(System.currentTimeMillis()
							- tStartOutSideActivity_LastTime) > 5 * 1000) {
						tStartOutSideActivity_LastTime = System
								.currentTimeMillis();
						am.moveTaskToFront(info.id,
								ActivityManager.MOVE_TASK_WITH_HOME);
						isHasStarted = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!isHasStarted) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(new ComponentName(pkg, name));
			this.startActivity(intent);
		}

		if (isShowInputMethod) {
			if (curInputMethod != null
					&& !curInputMethod.equals(Util.getCurrenInputMethod(this))) {
				InputMethodManager imeManager = (InputMethodManager) getApplicationContext()
						.getSystemService(INPUT_METHOD_SERVICE);
				if (imeManager != null) {
					try {	
						if (android.os.Build.BRAND.startsWith("Xiaomi")) // 小米手机
							Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					imeManager.showInputMethodPicker();
				}
			}
		}
	}

	/*
	 * need to rewrite
	 */
	private void hide2() {
		if (!isNeedAlwaysShow()) {
			hide();
		}
	}

	private void show2() {
		show();
	}

	protected void hide() {

	}

	protected void show() {

	}

	public void setStatus(long status, boolean isHaveOthers) {
	}
}
