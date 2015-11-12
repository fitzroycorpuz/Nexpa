package com.lpoezy.nexpa.utility;

import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;

import android.app.Activity;
import android.content.Context;
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
	
	public static void makeText(Activity context, CharSequence msg, Style style) {
		AppMsg.makeText(context, msg, style).show();
	}
	
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT);
	}

}
