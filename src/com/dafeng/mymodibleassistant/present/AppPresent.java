package com.dafeng.mymodibleassistant.present;

import java.util.List;

import wei.mark.standout.StandOutWindow.StandOutLayoutParams;
import wei.mark.standout.ui.Window;
import android.content.Context;

import com.dafeng.mymodibleassistant.dao.TbAppDis;
import com.dafeng.mymodibleassistant.dao.TbAppDisDao;
import com.dafeng.mymodibleassistant.dao.TbAppDisDao.Properties;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindow;
import com.dafeng.mymodibleassistant.util.Util;

public class AppPresent {
	public static long insertOrReplace(TbAppDisDao dao, TbAppDis entry) {
		return dao.insertOrReplace(entry);
	}

	public static boolean setHomePageDisplay(Context con, TbAppDisDao dao) {
		TbAppDis entry = new TbAppDis();
		entry.setPkg(Util.getLauncherActivityInfo(con).packageName);
		dao.insertOrReplace(entry);
		return true;
	}

	public static TbAppDis getTbAppDisByPkg(String pkg, TbAppDisDao dao) {
		List<TbAppDis> list = dao.queryBuilder().where(Properties.Pkg.eq(pkg))
				.list();
		if (list.size() < 1) {
			return null;
		}
		return list.get(0);
	}

}
