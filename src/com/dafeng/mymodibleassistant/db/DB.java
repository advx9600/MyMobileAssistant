package com.dafeng.mymodibleassistant.db;

import com.dafeng.mymodibleassistant.dao.DaoMaster;
import com.dafeng.mymodibleassistant.dao.DaoMaster.DevOpenHelper;
import com.dafeng.mymodibleassistant.dao.DaoSession;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	public static SQLiteDatabase getWritableDb(Context act) {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(act,
				"my_mobile_assistant.db", null);
		return helper.getWritableDatabase();
	}

	public static DaoSession getDaoSession(Context con) {
		return (new DaoMaster(getWritableDb(con))).newSession();
		// noteDao = daoSession.getNoteDao();
	}

	public static DaoSession getDaoSession(SQLiteDatabase db) {
		return (new DaoMaster(db)).newSession();
	}
}
