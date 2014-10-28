package com.dafeng.mymodibleassistant;

import com.dafeng.mymodibleassistant.dao.DaoSession;
import com.dafeng.mymodibleassistant.dao.TbAppDisDao;
import com.dafeng.mymodibleassistant.db.DB;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class TestAcvitiy extends Activity {

	private SQLiteDatabase mDb;
	private DaoSession mDaoSession;
	private TbAppDisDao mAppDisDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initDB();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	private void initDB() {
		mDb = DB.getWritableDb(this);
		mDaoSession = DB.getDaoSession(mDb);
		mAppDisDao = mDaoSession.getTbAppDisDao();
	}

}
