package com.dafeng.mymodibleassistant.util;

import java.util.List;

import com.dafeng.mymodibleassistant.a;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

public class Util {

	public static ActivityInfo getSelfActivityInfo(Context con) {
		PackageManager packageManager = con.getPackageManager();
		ActivityInfo info = null;
		try {
			info = packageManager.getActivityInfo(
					((Activity) con).getComponentName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return info;
	}

	/*
	 * return AndroidManifest.xml register activities
	 */
	public static ActivityInfo[] getSelfActivityInfos(Context con) {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm = con.getPackageManager();

		PackageInfo info = null;
		try {

			info = pm.getPackageInfo(con.getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// ApplicationInfo test = info.applicationInfo;
		ActivityInfo[] list = info.activities;
		return list;
	}

	/*
	 * get Home Activity info
	 */
	public static ActivityInfo getLauncherActivityInfo(Context con) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = con.getPackageManager().resolveActivity(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		return resolveInfo.activityInfo;
	}

	/*
	 * 根据包名得到图标
	 */
	public static Drawable getIconByAppPkg(Context con, String pkg) {
		try {
			PackageManager pkgMan = con.getPackageManager();
			ApplicationInfo app = pkgMan.getApplicationInfo(pkg, 0);

			Drawable icon = pkgMan.getApplicationIcon(app);
			// String name = pkgMan.getApplicationLabel(app).toString();
			return icon;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrenInputMethod(Context con) {
		return Settings.Secure.getString(con.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
	}

	/*
	 * 根据包名得到MainActivity名称
	 */
	public static String getMainClsNameByPkgName(Context con, String pkg) {
		try {
			PackageManager pm = con.getPackageManager();
			PackageInfo pi = con.getPackageManager().getPackageInfo(pkg, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = null;
			if (apps.iterator().hasNext())
				ri = apps.iterator().next();
			if (ri != null) {
				return ri.activityInfo.name;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 是否已经安装此包名
	 */
	public static boolean isPkgInstalled(Context con, String pkg) {
		PackageManager pm = con.getPackageManager();
		try {
			pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {

		}
		return false;
	}

	private static long mStatus = 0;

	public static void setStatusExample(long status) {
		// TODO Auto-generated method stub
		int bit1Count = 0;
		for (int i = 0; i < 3; i++) {
			if ((status & (0x1 << i)) > 0) {
				bit1Count++;
			}
		}
		if (bit1Count > 1) {
			mStatus &= status;
		} else {
			mStatus |= status;
		}
	}
}
