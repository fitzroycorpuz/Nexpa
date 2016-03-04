package com.lpoezy.nexpa.utility;

import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class L {
	
	private static final String TAG = "NEXPA";

	public static void debug(String msg) {
		Log.d(TAG, msg);
	}
	
	public static void error(String msg) {
		Log.e(TAG, msg);
	}
	
	public static void makeText(final Activity context, final CharSequence msg, final Style style) {
		
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				AppMsg.makeText(context, msg, style).show();
				
			}
		});
		
		
		
	}
	
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT);
	}

}
