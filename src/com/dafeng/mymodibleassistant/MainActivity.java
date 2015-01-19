package com.dafeng.mymodibleassistant;

import com.dafeng.mymodibleassistant.b.b;
import com.dafeng.mymodibleassistant.dao.DaoSession;
import com.dafeng.mymodibleassistant.dao.TbAppDao;
import com.dafeng.mymodibleassistant.db.DB;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindow;
import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindowInt;
import com.dafeng.mymodibleassistant.present.AppPresent;

import wei.mark.standout.StandOutWindow;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements MainActivityInt {
	public static final String EXTR_AUTO_BOOT = "start_type";

	private PlaceholderFragment mFragmentOne;

	private SQLiteDatabase mDb;
	private DaoSession mDaoSession;
	private TbAppDao mAppDao;

	private EditText mTextFloatWinSize;
	
	private EditText mTextPopWinWidth;
	private EditText mTextPopWinHeight;
	private EditText mTextPopWinAlpha;

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
		if (!isAlreadOpenFlaotWin()) {
			reOpenFloatWin();
		}
		if (this.getIntent().getBooleanExtra(EXTR_AUTO_BOOT, false)) {
			if (!isAlreadOpenFlaotWin()) {
				reOpenFloatWin();
			}
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
		mAppDao = mDaoSession.getTbAppDao();
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
			mTextPopWinWidth = (EditText) rootView.findViewById(R.id.et_width);
			mTextPopWinHeight = (EditText) rootView.findViewById(R.id.et_height);
			mTextPopWinAlpha = (EditText) rootView.findViewById(R.id.et_alpha);
			mTextFloatWinSize.setText(mShare.getInt(
					SimpleFloatingWindowInt.PREF_floatwin_width,
					b.getProperWidth())
					+ "");
			mTextPopWinWidth.setText(getPopWidth()+"");
			mTextPopWinHeight.setText(getPopHeight()+"");
			mTextPopWinAlpha.setText(getPopAlphy()+"");
			return rootView;
		}

	}

	/* for buttons */
	public void centerDisplay(View v) {
		SimpleFloatingWindow.IsShowInCenter = true;
		reOpenFloatWin();
	}

	public void homePageDispaly(View v) {
		if (AppPresent.setHomePageDisplay(this, mAppDao)) {
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
		if (value < 10 || value > 200) {
			toast(getString(R.string.max_min_value_exceed, 200, 10));
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

	public void setClickFloatWinPopWinDefSize(View v) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int maxHeight = displaymetrics.heightPixels;
		int maxWidth = displaymetrics.widthPixels;
		String width = mTextPopWinWidth.getText().toString();
		String height = mTextPopWinHeight.getText().toString();
		if (width==null || width.length()==0 || height==null || height.length()==0){		
			toast(R.string.not_null);
			return;
		}
		int widthN=Integer.parseInt(width);
		int heightN = Integer.parseInt(height);
		if (widthN<30 || widthN>maxWidth){			
			toast(getString(R.string.width)+" "+getString(R.string.max_min_value_exceed, maxWidth, 30));
			return ;
		}
		if (heightN <30 || heightN>maxHeight){
			toast(getString(R.string.height)+" "+getString(R.string.max_min_value_exceed, maxHeight, 30));
			return ;
		}
		setPopWidth(widthN);
		setPopHeight(heightN);
		reOpenFloatWin();
	}
	public void setFloatwinAlphy(View v){
		String alpha = mTextPopWinAlpha.getText().toString();
		if (alpha == null || alpha.length() ==0){
			toast(R.string.not_null);
			return ;
		}
		int alphaNum = Integer.parseInt(alpha);
		if (alphaNum < 5 || alphaNum >255){
			toast(getString(R.string.max_min_value_exceed, 255, 5));
			return ;
		}
		setFloatAlphy(alphaNum);
		reOpenFloatWin();
	}
	private int getPopWidth(){
		return mShare.getInt(SimpleFloatingWindowInt.PREF_floatwin_entry_width, 400);
	}

	private int getPopHeight(){
		return mShare.getInt(SimpleFloatingWindowInt.PREF_floatwin_entry_height, 300);
	}
	private int getPopAlphy(){
		return mShare.getInt(SimpleFloatingWindowInt.PREF_floatwin_alpha, 255);
	}
	private void setPopWidth(int width){
		mShare.edit()
		.putInt(SimpleFloatingWindowInt.PREF_floatwin_entry_width, width)
		.commit();
	}
	private void setPopHeight(int height){
		mShare.edit()
		.putInt(SimpleFloatingWindowInt.PREF_floatwin_entry_height, height)
		.commit();
	}
	private void setFloatAlphy(int alpha){
		mShare.edit()
		.putInt(SimpleFloatingWindowInt.PREF_floatwin_alpha, alpha)
		.commit();
	}
	public void btnTest(View v) {
		// isAlreadOpenFlaotWin();
	}

	private boolean isAlreadOpenFlaotWin() {
		Intent intent = StandOutWindow.getShowIntent(this,
				SimpleFloatingWindow.class, SimpleFloatingWindow.MY_DEFAULT_ID);
		if (intent.getAction().equals(StandOutWindow.ACTION_SHOW)) {
			return false;
		}
		return true;
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
