package com.dafeng.mymodibleassistant.receiver;


import com.dafeng.mymodibleassistant.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			startActivity(context);
		}
	}

	private void startActivity(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.putExtra(MainActivity.EXTR_AUTO_BOOT, true);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
