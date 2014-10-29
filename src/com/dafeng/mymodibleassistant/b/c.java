package com.dafeng.mymodibleassistant.b;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow.StandOutLayoutParams;
import wei.mark.standout.ui.Window;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dafeng.mymodibleassistant.R;
import com.dafeng.mymodibleassistant.dao.TbApp;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindow;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindowInt;
import com.dafeng.mymodibleassistant.present.AppPresent;
import com.dafeng.mymodibleassistant.util.Util;

public class c {
	public static class DropDownListItem {
		public int icon;
		public String description;
		public Runnable action;

		public DropDownListItem(int icon, String description, Runnable action) {
			super();
			this.icon = icon;
			this.description = description;
			this.action = action;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public static void a(Window window, TbApp mAppDis) {
		if (window != null) {
			window.edit().setPosition(mAppDis.getX(), mAppDis.getY());
			window.edit().commit();
		}
	}

	public static void b(Window window, SharedPreferences mShare) {
		window.edit().setPosition(mShare.getInt("x", 100),
				mShare.getInt("y", 100));
		window.edit().commit();
	}

	public static void d(final long curAppId, List<TbApp> mListTbJump,
			LinearLayout lay, final Context con, final LayoutInflater inflater,
			View v) {

		final SimpleFloatingWindowInt floatWinInt = ((SimpleFloatingWindowInt) con);
		if (mListTbJump != null && mListTbJump.size() > 0) {
			for (int i = 0; i < mListTbJump.size(); i++) {
				final TbApp app = mListTbJump.get(i);
				final long id = app.getId();
				final long tbJumpId = AppPresent.getTbJumpId(curAppId, id);
				ImageButton btnJump = new ImageButton(lay.getContext());
				// btnJump.setBackgroundDrawable(background);
				btnJump.setBackgroundColor(Color.TRANSPARENT);
				btnJump.setImageDrawable(Util.getIconByAppPkg(con, app.getPkg()));
				lay.addView(btnJump);
				btnJump.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LinearLayout list = new LinearLayout(v.getContext());
						list.setOrientation(LinearLayout.VERTICAL);

						final PopupWindow dropDown = new PopupWindow(list,
								StandOutLayoutParams.WRAP_CONTENT,
								StandOutLayoutParams.WRAP_CONTENT, true);
						List<DropDownListItem> items = new ArrayList<DropDownListItem>();
						items.add(new DropDownListItem(
								android.R.drawable.ic_menu_edit, con
										.getString(R.string.modify),
								new Runnable() {
									@Override
									public void run() {
										floatWinInt.setOpsId(tbJumpId);
										floatWinInt
												.setStatus(SimpleFloatingWindow.STATUS_MOD_JUMP_APP_SHORTCUT);
									}
								}));
						items.add(new DropDownListItem(
								android.R.drawable.ic_menu_close_clear_cancel,
								con.getString(R.string.del), new Runnable() {
									@Override
									public void run() {
										floatWinInt.setOpsId(tbJumpId);
										floatWinInt
												.setStatus(SimpleFloatingWindow.STATUS_DEL_JUMP_APP_SHORTCUT);
									}
								}));
						if (app.getIsShowInputPicker())
							items.add(new DropDownListItem(
									android.R.drawable.ic_menu_close_clear_cancel,
									con.getString(R.string.cancel_input_mehtod),
									new Runnable() {
										@Override
										public void run() {
											floatWinInt.setOpsId(id);
											floatWinInt
													.setStatus(SimpleFloatingWindow.STATUS_CANCEL_APP_INPUTMETHOD);
										}
									}));
						else
							items.add(new DropDownListItem(
									android.R.drawable.ic_menu_add,
									con.getString(R.string.add_input_method),
									new Runnable() {
										@Override
										public void run() {
											floatWinInt.setOpsId(id);
											floatWinInt
													.setStatus(SimpleFloatingWindow.STATUS_ADD_APP_INPUTMETHOD);
										}
									}));

						for (final DropDownListItem item : items) {
							ViewGroup listItem = (ViewGroup) inflater.inflate(
									R.layout.drop_down_list_item, null);
							list.addView(listItem);

							ImageView icon = (ImageView) listItem
									.findViewById(R.id.icon);
							icon.setImageResource(item.icon);

							TextView description = (TextView) listItem
									.findViewById(R.id.description);
							description.setText(item.description);

							listItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									item.action.run();
									dropDown.dismiss();
								}
							});
						}

						Drawable background = con.getResources().getDrawable(
								android.R.drawable.editbox_dropdown_dark_frame);
						dropDown.setBackgroundDrawable(background);
						dropDown.showAsDropDown(v);
					}

				});
			}
			if (mListTbJump.size() == 3) {
				v.findViewById(R.id.btn_jump_app).setVisibility(View.GONE);
			}
		}
	}

	public static void e(final Context con, final LayoutInflater inflater,
			LinearLayout layShortcut, List<TbApp> mListAppshortcut) {
		final SimpleFloatingWindowInt floatWinInt = ((SimpleFloatingWindowInt) con);
		if (mListAppshortcut != null && mListAppshortcut.size() > 0) {
			for (int i = 0; i < mListAppshortcut.size(); i++) {
				final TbApp shortcut = mListAppshortcut.get(i);
				final long id = shortcut.getId();
				final long tbShortcutId = AppPresent.getTbShortcutIdByAppId(id);
				ImageButton btnShortcut = new ImageButton(
						layShortcut.getContext());
				btnShortcut.setImageDrawable(Util.getIconByAppPkg(con,
						shortcut.getPkg()));
				btnShortcut.setBackgroundColor(Color.TRANSPARENT);
				layShortcut.addView(btnShortcut);
				btnShortcut.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						floatWinInt.setOpsId(id);
						floatWinInt
								.setStatus(SimpleFloatingWindow.STATUS_JUMP_TO_APP_SHORTCUT);
					}
				});
				btnShortcut.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						LinearLayout list = new LinearLayout(v.getContext());
						list.setOrientation(LinearLayout.VERTICAL);

						final PopupWindow dropDown = new PopupWindow(list,
								StandOutLayoutParams.WRAP_CONTENT,
								StandOutLayoutParams.WRAP_CONTENT, true);
						List<DropDownListItem> items = new ArrayList<DropDownListItem>();
						items.add(new DropDownListItem(
								android.R.drawable.ic_menu_edit, con
										.getString(R.string.modify),
								new Runnable() {
									@Override
									public void run() {
										floatWinInt.setOpsId(tbShortcutId);
										floatWinInt
												.setStatus(SimpleFloatingWindow.STATUS_MOD_APP_SHORTCUT);
									}
								}));
						items.add(new DropDownListItem(
								android.R.drawable.ic_menu_close_clear_cancel,
								con.getString(R.string.del), new Runnable() {
									@Override
									public void run() {
										floatWinInt.setOpsId(tbShortcutId);
										floatWinInt
												.setStatus(SimpleFloatingWindow.STATUS_DEL_APP_SHORTCUT);
									}
								}));
						if (shortcut.getIsShowInputPicker())
							items.add(new DropDownListItem(
									android.R.drawable.ic_menu_close_clear_cancel,
									con.getString(R.string.cancel_input_mehtod),
									new Runnable() {
										@Override
										public void run() {
											floatWinInt.setOpsId(id);
											floatWinInt
													.setStatus(SimpleFloatingWindow.STATUS_CANCEL_APP_INPUTMETHOD);
										}
									}));
						else
							items.add(new DropDownListItem(
									android.R.drawable.ic_menu_add,
									con.getString(R.string.add_input_method),
									new Runnable() {
										@Override
										public void run() {
											floatWinInt.setOpsId(id);
											floatWinInt
													.setStatus(SimpleFloatingWindow.STATUS_ADD_APP_INPUTMETHOD);
										}
									}));

						for (final DropDownListItem item : items) {
							ViewGroup listItem = (ViewGroup) inflater.inflate(
									R.layout.drop_down_list_item, null);
							list.addView(listItem);

							ImageView icon = (ImageView) listItem
									.findViewById(R.id.icon);
							icon.setImageResource(item.icon);

							TextView description = (TextView) listItem
									.findViewById(R.id.description);
							description.setText(item.description);

							listItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									item.action.run();
									dropDown.dismiss();
								}
							});
						}

						Drawable background = con.getResources().getDrawable(
								android.R.drawable.editbox_dropdown_dark_frame);
						dropDown.setBackgroundDrawable(background);
						dropDown.showAsDropDown(v);
						return true;
					}

				});
			}
		}
	}
}
