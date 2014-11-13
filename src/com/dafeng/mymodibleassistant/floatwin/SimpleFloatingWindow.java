package com.dafeng.mymodibleassistant.floatwin;

import java.util.ArrayList;
import java.util.List;

import com.dafeng.mymodibleassistant.R;
import com.dafeng.mymodibleassistant.a;
import com.dafeng.mymodibleassistant.b.c;
import com.dafeng.mymodibleassistant.dao.TbApp;
import com.dafeng.mymodibleassistant.present.AppPresent;
import com.dafeng.mymodibleassistant.util.Util;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SimpleFloatingWindow extends SimpleFloatingWindowBase implements
		SimpleFloatingWindowInt {
	// private int curClickTimes = 0;

	// private RadialMenuRenderer mRenderer;
	private FrameLayout mHolderLayout;

	private long mStoreId;

	class ThreadCheck extends Thread {
		public void run() {
			while (!mIsQuit) {
				try {
					Thread.sleep(500);
					ActivityManager am = (ActivityManager) SimpleFloatingWindow.this
							.getSystemService(ACTIVITY_SERVICE);
					List<ActivityManager.RunningTaskInfo> taskInfo = am
							.getRunningTasks(1);
					if (taskInfo.size() == 0) {
						return;
					}

					String topPkg = taskInfo.get(0).topActivity
							.getPackageName();
					String topClsName = Util.getMainClsNameByPkgName(
							SimpleFloatingWindow.this, topPkg);

					if (mTopActivePkg == null || mTopActivePkg.length() == 0) {
						mTopActivePkg = topPkg;
						mTopActiveName = topClsName;
						handle.sendEmptyMessage(1);
					} else if (!mTopActivePkg.equals(topPkg)) {
						mTopActivePkg = topPkg;
						mTopActiveName = topClsName;
						handle.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void closePopWin() {
		final Window window = getWindow(POP_WIN_ID);
		if (window != null) {
			close(POP_WIN_ID);
		}
	}

	private boolean mIsClosePopWinOnly = false;

	protected void onlyClosePopWin() {
		mIsClosePopWinOnly = true;
		closePopWin();
		mIsClosePopWinOnly = false;
	}

	@Override
	public boolean onFocusChange(int id, Window window, boolean focus) {
		if (id == POP_WIN_ID) {
			if (!mIsClosePopWinOnly) {
				if (!focus) {
					closePopWin();
					show();
					return false;
				}
			}
		}

		return super.onFocusChange(id, window, focus);
	}

	@Override
	public boolean onShow(int id, Window window) {
		if (id == POP_WIN_ID) {
			hide();
		}
		return super.onShow(id, window);
	}

	@Override
	public boolean onClose(int id, Window window) {
		if (id == POP_WIN_ID) {
		} else if (id == MY_DEFAULT_ID) {
			a.c("exit mIsQuit=true");
			mIsQuit = true;
		}
		return super.onClose(id, window);
	}

	@Override
	public void createAndAttachView(int winId, FrameLayout frame) {
		// create a new layout from body.xml
		final LayoutInflater inflater = LayoutInflater.from(this);
		if (winId == POP_WIN_ID) {
			View v = inflater.inflate(R.layout.simple_pop, frame, true);
			{
				LinearLayout lay = (LinearLayout) v
						.findViewById(R.id.layout_jump_shortcut);
				c.d(mApp.getId(), mListTbJump, lay, this, inflater, v);
			}
			{
				LinearLayout layShortcut = (LinearLayout) v
						.findViewById(R.id.layout_app_shortcut);
				c.e(this, inflater, layShortcut, mListAppshortcut);
			}

		} else if (winId == MY_DEFAULT_ID) {
			mIsQuit = false;
			mHolderLayout = (FrameLayout) inflater.inflate(R.layout.simple,
					frame, true);
			mFloatImg = (ImageView) mHolderLayout.findViewById(R.id.float_img);
			new ThreadCheck().start();
			mListPreApp.clear();
		}
	}

	@Override
	public StandOutLayoutParams getParams(int id, Window window) {

		if (id == MY_DEFAULT_ID) {
			int x = StandOutLayoutParams.CENTER;
			int y = StandOutLayoutParams.CENTER;
			if (!IsShowInCenter) {
				x = mShare.getInt("x", 100);
				y = mShare.getInt("y", 100);
			} else {
				mShare.edit().putInt("x", x).commit();
				mShare.edit().putInt("y", y).commit();
				IsShowInCenter = false;
			}
			return new StandOutLayoutParams(
					id,
					mShare.getInt(PREF_floatwin_width,
							com.dafeng.mymodibleassistant.b.b.getProperWidth()),
					mShare.getInt(PREF_floatwin_height,
							com.dafeng.mymodibleassistant.b.b.getProperHeight()),
					x, y);
		} else if (id == POP_WIN_ID) {
			int x = StandOutLayoutParams.CENTER;
			int y = StandOutLayoutParams.TOP;
			if (!IsPopShowDefault) {
				x = mShare.getInt("pop_x", 100);
				y = mShare.getInt("pop_y", 100);
			} else {
				mShare.edit().putInt("pop_x", x).commit();
				mShare.edit().putInt("pop_y", y).commit();
				IsPopShowDefault = false;
			}
			return new StandOutLayoutParams(id, 400,
					StandOutLayoutParams.WRAP_CONTENT, x, y);
		}
		return null;
	}

	public Window getMainWindow(){
		return  getWindow(MY_DEFAULT_ID);
	}
	
	public void show() {
		Message msg = new Message();
		msg.what = 2;
		handle.sendMessageDelayed(msg, 200);
		getMainWindow().setVisibility(View.VISIBLE);
	}

	public void hide() {
		mIsTempIgnoreTouch = true;
		getMainWindow().setVisibility(View.GONE);
	}

	// move the window by dragging the view
	@Override
	public int getFlags(int id) {
		if (id == POP_WIN_ID) {
			return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
					| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
		}
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
				| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE;
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Click to close the SimpleWindow";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getCloseAllIntent(this,
				SimpleFloatingWindow.class);
	}

	@Override
	public List<DropDownListItem> getDropDownItems(final int id) {
		List<DropDownListItem> items = new ArrayList<DropDownListItem>();
		// FolderModel folder = mFolders.get(id);
		// add
		if (id == POP_WIN_ID) {
			items.add(new DropDownListItem(android.R.drawable.ic_menu_add,
					getString(R.string.add_floatwin_display_page),
					new Runnable() {

						@Override
						public void run() {
							setStatus(STATUS_ADD_SHOW_PAGE);
						}
					}));
			items.add(new DropDownListItem(
					android.R.drawable.ic_menu_close_clear_cancel,
					getString(R.string.this_page_not_show_floatwin),
					new Runnable() {
						@Override
						public void run() {
							setStatus(STATUS_NOT_SHOW_THIS_PAGE);
						}
					}));
			items.add(new DropDownListItem(android.R.drawable.ic_menu_edit,
					getString(R.string.this_page_temp_hide), new Runnable() {
						@Override
						public void run() {
							setStatus(STATUS_THIS_PAGE_TEMP_HIDE);
						}
					}));
			if (isStatus(STATUS_POS_INDEPENDENT))
				items.add(new DropDownListItem(android.R.drawable.ic_menu_edit,
						getString(R.string.cancel_page_pos_indepentent),
						new Runnable() {
							@Override
							public void run() {
								setStatus(~STATUS_POS_INDEPENDENT);
							}
						}));
			else
				items.add(new DropDownListItem(android.R.drawable.ic_menu_edit,
						getString(R.string.this_page_set_pos_indepentent),
						new Runnable() {
							@Override
							public void run() {
								setStatus(STATUS_POS_INDEPENDENT);
							}
						}));
		}

		return items;
	}

	/*
	 * button click actions
	 */

	public void addJumpShortcut(View v) {
		setStatus(STATUS_ADD_JUMP_APP_SHORTCUT);
	}

	public void addAppShortcut(View v) {
		setStatus(STATUS_ADD_APP_SHORTCUT);
	}

	private void reFreshPage() {
		closePopWin();
		handle.sendEmptyMessage(1);
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		// 直接切换对应界面或者显示界面
		if (isNeedAlwaysShow()) {
			setReverseStatus();
			return;
		}

		if (mApp != null) {
			if (mListTbJump != null && mListTbJump.size() == 0) {
				show(POP_WIN_ID);
			} else {
				startActivityByNum(0);
			}
		} else {
			show(POP_WIN_ID);
		}
	}

	@Override
	public void onDoubleClick() {
		startPreApp(-1);
	}
	@Override
	public void onSlowFlip(){
		startPreApp(-2);
	}
	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub
		if (isNeedAlwaysShow()) {
			mStatus = STATUS_NORMAL;
			reFreshPage();
			toast(R.string.already_cancel);
			return;
		}
		show(POP_WIN_ID);
	}

	private void startActivityByNum(int num) {
		if (mListTbJump != null && mListTbJump.size() > num) {
			startOutSideActivity(mListTbJump.get(num));
		}
	}

	@Override
	public void onRightFlip() {
		// TODO Auto-generated method stub
		startActivityByNum(1);
	}

	@Override
	public void onLeftFlip() {
		// TODO Auto-generated method stub
		startActivityByNum(1);
	}

	@Override
	public void onTopFlip() {
		// TODO Auto-generated method stub
		startActivityByNum(2);
	}

	@Override
	public void onBottomFlip() {
		// TODO Auto-generated method stub
		startActivityByNum(2);
	}

	public void setStatus(long status, boolean isHaveOthers) {
		int bit1Count = 0;
		if (status == STATUS_NOT_SHOW_THIS_PAGE) {
			mApp.setIsShow(false);
			mAppDao.update(mApp);
			reFreshPage();
			return;
		} else if (status == STATUS_DEL_JUMP_APP_SHORTCUT) {
			AppPresent.delJumpApp(mStoreId);
			reFreshPage();
			return;
		} else if (status == STATUS_CANCEL_APP_INPUTMETHOD) {
			TbApp jump = mAppDao.findById(mStoreId);
			jump.setIsShowInputPicker(false);
			mAppDao.update(jump);
			reFreshPage();
			return;
		} else if (status == STATUS_JUMP_TO_APP_SHORTCUT) {
			TbApp shortcut = mAppDao.findById(mStoreId);
			startOutSideActivity(shortcut);
			reFreshPage();
			return;
		} else if (status == STATUS_DEL_APP_SHORTCUT) {
			AppPresent.delShortcut(mStoreId);
			reFreshPage();
			return;
		} else if (status == STATUS_CANCEL_APP_SHORTCUT_INPUTMETHOD) {
			TbApp app = mAppDao.findById(mStoreId);
			app.setIsShowInputPicker(false);
			mAppDao.update(app);
			reFreshPage();
			return;
		} else if (status == STATUS_THIS_PAGE_TEMP_HIDE) {
			onlyClosePopWin();
			return;
		}

		for (int i = 0; i < 3; i++) {
			if ((status & (0x1 << i)) > 0) {
				bit1Count++;
			}
		}
		if (bit1Count > 1) {
			if (!isHaveOthers) {
				mStatus &= status;
				return;
			}
			if (isNeedAlwaysShow()) {
				if (mTopActiveName == null || mTopActiveName.length() == 0) {
					if (!isStatus(STATUS_ADD_SHOW_PAGE)
							&& !isStatus(STATUS_ADD_APP_INPUTMETHOD)) {
						toast(R.string.class_name_not_null);
						return;
					}
				}
			}

			long reverseStatus = ~status;
			if (reverseStatus == STATUS_ADD_SHOW_PAGE) {
				AppPresent.addShowPage(mTopActivePkg, mTopActiveName);
			} else if (reverseStatus == STATUS_POS_INDEPENDENT) {
				mApp.setIsPosIndependent(false);
				mAppDao.update(mApp);
			} else if (reverseStatus == STATUS_ADD_JUMP_APP_SHORTCUT) {
				AppPresent.addJumpApp(mStoreId, mTopActivePkg, mTopActiveName);
			} else if (reverseStatus == STATUS_MOD_JUMP_APP_SHORTCUT) {
				AppPresent.modJumpApp(mStoreId, mTopActivePkg, mTopActiveName);
			} else if (reverseStatus == STATUS_ADD_APP_INPUTMETHOD) {
				AppPresent.addInputMethod(mStoreId,
						Util.getCurrenInputMethod(this));
			} else if (reverseStatus == STATUS_ADD_APP_SHORTCUT) {
				AppPresent.addShortcut(mTopActivePkg, mTopActiveName);
			} else if (reverseStatus == STATUS_MOD_APP_SHORTCUT) {
				AppPresent.modShortcut(mStoreId, mTopActivePkg, mTopActiveName);
			}

			if (isNeedAlwaysShow()) {
				reFreshPage();
			}

			mStatus &= status;

		} else {
			mStatus |= status;
			if (!isHaveOthers)
				return;
			int okPng = R.drawable.ok_circle;
			if (isNeedAlwaysShow()) {
				mFloatImg.setImageDrawable(null);
				mFloatImg.setBackgroundResource(okPng);
				closePopWin();
			}

			if (status == STATUS_ADD_SHOW_PAGE) {
				toast(R.string.open_add_page_app_please);
			} else if (status == STATUS_POS_INDEPENDENT) {
				mApp.setIsPosIndependent(true);
				mAppDao.update(mApp);
				reFreshPage();
			} else if (status == STATUS_ADD_JUMP_APP_SHORTCUT) {
				mStoreId = mApp.getId();
				toast(R.string.open_add_page_app_please);
			}
		}
	}

	@Override
	public void setStatus(long status) {
		// TODO Auto-generated method stub
		setStatus(status, true);
	}

	@Override
	public boolean isStatus(long status) {
		if ((status & mStatus) > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void setOpsId(long id) {
		// TODO Auto-generated method stub
		this.mStoreId = id;
	}
}
