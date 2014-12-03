package com.dafeng.mymodibleassistant.receiver;

import com.dafeng.mymodibleassistant.floatwin.SimpleFloatingWindow;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

public class UninstallAppReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String pkg = intent.getDataString().split(":")[1];
		if (!pkg.equals(context.getPackageName())) {
			if (SimpleFloatingWindow.handle != null) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = pkg;
				SimpleFloatingWindow.handle.sendMessage(msg);
			}
		}
	}
}
