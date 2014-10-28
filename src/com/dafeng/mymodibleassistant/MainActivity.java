package com.dafeng.mymodibleassistant;

import java.util.List;

import com.dafeng.mymodibleassistant.b.b;
import com.dafeng.mymodibleassistant.dao.DaoSession;
import com.dafeng.mymodibleassistant.dao.TbAppDis;
import com.dafeng.mymodibleassistant.dao.TbAppDisDao;
import com.dafeng.mymodibleassistant.db.DB;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindow;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindowInt;
import com.dafeng.mymodibleassistant.present.AppPresent;
import com.dafeng.mymodibleassistant.util.Util;

import de.greenrobot.dao.query.QueryBuilder;
import wei.mark.standout.StandOutWindow;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements MainActivityInt {
	public static final String EXTR_AUTO_BOOT = "start_type";

	private PlaceholderFragment mFragmentOne;

	private SQLiteDatabase mDb;
	private DaoSession mDaoSession;
	private TbAppDisDao mAppDisDao;

	private EditText mTextFloatWinSize;

	private SharedPreferences mShare;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				StandOutWindow.show(MainActivity.this,
						SimpleFloatingWindow.class,
						SimpleFloatingWindow.MY_DEFAULT_ID);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mShare = getSharedPreferences(SimpleFloatingWindowInt.PREF_FILE_NAME,
				MODE_PRIVATE);
		initDB();

		if (savedInstanceState == null) {
			mFragmentOne = new PlaceholderFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mFragmentOne).commit();
		}

		reOpenFloatWin();

		if (this.getIntent().getBooleanExtra(EXTR_AUTO_BOOT, false)) {
			finish();
			return;
		}
		initUI();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDb.close();
	}

	private void initUI() {

	}

	private void initDB() {
		mDb = DB.getWritableDb(this);
		mDaoSession = DB.getDaoSession(mDb);
		mAppDisDao = mDaoSession.getTbAppDisDao();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			mTextFloatWinSize = (EditText) rootView
					.findViewById(R.id.text_float_size);
			mTextFloatWinSize.setText(mShare.getInt(
					SimpleFloatingWindowInt.PREF_floatwin_width,
					b.getProperWidth())
					+ "");

			return rootView;
		}

	}

	/* for buttons */
	public void centerDisplay(View v) {
		SimpleFloatingWindow.IsShowInCenter = true;
		reOpenFloatWin();
	}

	public void homePageDispaly(View v) {
		if (AppPresent.setHomePageDisplay(this, mAppDisDao)) {
			Toast.makeText(this, R.string.already_show_homepage,
					Toast.LENGTH_LONG).show();
		}
	}

	public void adjustFloatWinSize(View v) {
		String valStr = mTextFloatWinSize.getText().toString();
		if (valStr == null || valStr.length() == 0) {
			toast(R.string.not_null);
			return;
		}
		int value = Integer.parseInt(valStr);
		if (value < 30 || value > 200) {
			toast(getString(R.string.max_min_value_exceed, 200, 30));
			return;
		}
		mShare.edit()
				.putInt(SimpleFloatingWindowInt.PREF_floatwin_height, value)
				.commit();
		mShare.edit()
				.putInt(SimpleFloatingWindowInt.PREF_floatwin_width, value)
				.commit();
		reOpenFloatWin();
	}

	public void btnTest(View v) {
		// String pkg = "com.android.contacts";
		// String name = "com.android.contacts.activities.PeopleActivity";
		// Intent intent = new Intent();
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setComponent(new ComponentName(pkg, name));
		// this.startActivity(intent);
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am
				.getRunningTasks(100);
		String pkg = "com.android.contacts";
		for (int i = 0; i < taskInfo.size(); i++) {
			ActivityManager.RunningTaskInfo info = taskInfo.get(i);
			if (pkg.equals(info.topActivity.getPackageName())) {
				am.moveTaskToFront(info.id, ActivityManager.MOVE_TASK_WITH_HOME);
				return;
			}
		}
	}

	@Override
	public void reOpenFloatWin() {
		StandOutWindow.closeAll(this, SimpleFloatingWindow.class);
		Message msg = new Message();
		msg.what = 1;
		// must delay it's strange
		handler.sendMessageDelayed(msg, 1000);
	}

	@Override
	public void toast(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void toast(int id) {
		// TODO Auto-generated method stub
		Toast.makeText(this, id, Toast.LENGTH_LONG).show();
	}
}
