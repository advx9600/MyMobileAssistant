package com.dafeng.mymodibleassistant.present;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;

import com.dafeng.mymodibleassistant.dao.TbApp;
import com.dafeng.mymodibleassistant.dao.TbAppDao;
import com.dafeng.mymodibleassistant.dao.TbJump;
import com.dafeng.mymodibleassistant.dao.TbJumpDao;
import com.dafeng.mymodibleassistant.dao.TbAppDao.Properties;
import com.dafeng.mymodibleassistant.dao.TbShortcut;
import com.dafeng.mymodibleassistant.dao.TbShortcutDao;
import com.dafeng.mymodibleassistant.util.Util;

public class AppPresent {
	private static TbAppDao mTbAppDao;
	private static TbJumpDao mJumpToAppDao;
	private static TbShortcutDao mShortcutDao;

	public static void setDao(TbAppDao TbAppDao, TbJumpDao JumpToAppDao,
			TbShortcutDao shortcutDao) {
		mTbAppDao = TbAppDao;
		mJumpToAppDao = JumpToAppDao;
		mShortcutDao = shortcutDao;
	}

	private static TbAppDao a() {
		return mTbAppDao;
	}

	private static TbJumpDao b() {
		return mJumpToAppDao;
	}

	private static TbShortcutDao c() {
		return mShortcutDao;
	}

	public static long addShowPage(String pkg, String name) {
		long id = -1;
		List<TbApp> list = a().queryBuilder().where(Properties.Pkg.eq(pkg))
				.list();
		if (list.size() == 0) {
			TbApp app = new TbApp();
			app.setPkg(pkg);
			app.setName(name);
			id = a().insert(app);
		} else {
			TbApp app = list.get(0);
			if (!app.getIsShow()) {
				app.setIsShow(true);
				a().update(app);
			}
			id = app.getId();
		}
		return id;
	}

	public static void addJumpApp(long storeId, String pkg, String name) {
		long jumpId = addShowPage(pkg, name);
		TbJump jump = new TbJump();
		jump.setAppId(storeId);
		jump.setJumpId(jumpId);
		b().insert(jump);
	}

	public static boolean setHomePageDisplay(Context con, TbAppDao dao) {
		ActivityInfo info = Util.getLauncherActivityInfo(con);
		String pkg = info.packageName;
		if (a() != null) {
			dao = a();
		}
		List<TbApp> list = dao.queryBuilder()
				.where(Properties.Pkg.eq(pkg), Properties.Name.eq(info.name))
				.list();

		TbApp app = list.get(0);
		if (!app.getIsShow()) {
			app.setIsShow(true);
			dao.update(app);
		}
		return true;
	}

	public static TbApp getTbApp(String pkg, String name) {
		List<TbApp> list = mTbAppDao.queryBuilder()
				.where(Properties.Pkg.eq(pkg)).list();
		if (list.size() < 1) {
			return null;
		}
		return list.get(0);
	}

	public static List<TbApp> getJumpApps(long curAppId) {
		List<TbJump> list = b().queryBuilder()
				.where(TbJumpDao.Properties.AppId.eq(curAppId)).list();
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			ids.add(list.get(i).getJumpId());
		}
		return a().queryBuilder().where(Properties.Id.in(ids)).list();
	}

	public static long getTbJumpId(long appId, long jumpToAppId) {
		List<TbJump> list = b()
				.queryBuilder()
				.where(TbJumpDao.Properties.AppId.eq(appId),
						TbJumpDao.Properties.JumpId.eq(jumpToAppId)).list();
		if (list.size() > 0) {
			return list.get(0).getId();
		}
		return -1;
	}

	public static void modJumpApp(long id, String pkg, String name) {
		long jumpId = addShowPage(pkg, name);
		TbJump jump = b().findById(id);
		jump.setJumpId(jumpId);
		b().update(jump);
	}

	public static void addInputMethod(long id, String inputId) {
		TbApp app = a().findById(id);
		app.setIsShowInputPicker(true);
		app.setInputMethod(inputId);
		a().update(app);
	}

	public static long getTbShortcutIdByAppId(long appId) {
		long id = -1;
		List<TbShortcut> list = c().queryBuilder()
				.where(TbShortcutDao.Properties.AppId.eq(appId)).list();
		if (list.size() > 0) {
			return list.get(0).getId();
		}
		return id;
	}

	public static void delShortcut(long id) {
		c().deleteByKey(id);
	}

	public static void addShortcut(String pkg, String name) {
		long id = addShowPage(pkg, name);
		TbShortcut shortcut = new TbShortcut();
		shortcut.setAppId(id);
		c().insert(shortcut);
	}

	public static void modShortcut(long id, String pkg, String name) {
		long appId = addShowPage(pkg, name);
		TbShortcut shortcut = c().findById(id);
		shortcut.setAppId(appId);
		c().update(shortcut);
	}

	/*
	 * 
	 */

	public static List<TbApp> getShortcuts() {
		List<TbShortcut> list = c().queryBuilder().list();
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			ids.add(list.get(i).getAppId());
		}
		return a().queryBuilder().where(Properties.Id.in(ids)).list();
	}

	public static void delJumpApp(long id) {
		b().deleteByKey(id);
	}

	public static void cancelJumpApp() {

	}
}
