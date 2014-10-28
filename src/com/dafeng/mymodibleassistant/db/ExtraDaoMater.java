package com.dafeng.mymodibleassistant.db;

import com.dafeng.mymodibleassistant.dao.DaoMaster;
import com.dafeng.mymodibleassistant.dao.DaoSession;
import com.dafeng.mymodibleassistant.dao.TbAppDis;
import com.dafeng.mymodibleassistant.dao.TbAppDisDao;
import com.dafeng.mymodibleassistant.util.Util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;

public class ExtraDaoMater {
	public static void onCreate(SQLiteDatabase db, Context con) {
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		TbAppDisDao dao = daoSession.getTbAppDisDao();

		addSelf(con, dao);
		addLaucher(con, dao);
	}

	private static void addSelf(Context con, TbAppDisDao dao) {
		TbAppDis tb = new TbAppDis();
		ActivityInfo info = Util.getSelfActivityInfo(con);
		tb.setPkg(info.packageName);
		dao.insert(tb);
	}

	private static void addLaucher(Context con, TbAppDisDao dao) {
		TbAppDis tb = new TbAppDis();
		ActivityInfo info = Util.getLauncherActivityInfo(con);
		tb.setPkg(info.packageName);
		dao.insert(tb);
	}
	/*
	 * add in DaoMaster.java public static abstract class OpenHelper extends
	 * SQLiteOpenHelper {
	 * 
	 * public OpenHelper(Context context, String name, CursorFactory factory) {
	 * super(context, name, factory, SCHEMA_VERSION); }
	 * 
	 * @Override public void onCreate(SQLiteDatabase db) { Log.i("greenDAO",
	 * "Creating tables for schema version " + SCHEMA_VERSION);
	 * createAllTables(db, false); ExtraDaoMater.onCreate(db); // this is added
	 * } }
	 */
}
