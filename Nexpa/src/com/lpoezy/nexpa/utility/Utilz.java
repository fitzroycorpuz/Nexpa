package com.lpoezy.nexpa.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utilz {

	public static void saveToSharedPref(Context context, String key, String value) {
		context.getApplicationContext().getSharedPreferences("Nexpa", Context.MODE_PRIVATE).edit().putString(key, value)
				.apply();

	}

	public static String getDataFrmSharedPref(Context context, String key, String defValue) {

		String value = context.getApplicationContext().getSharedPreferences("Nexpa", Context.MODE_PRIVATE)
				.getString(key, defValue == null ? "" : defValue);
		return value;

	}

	public static void clearSharedPref(Context context) {
		context.getApplicationContext().getSharedPreferences("Nexpa", Context.MODE_PRIVATE).edit().clear().apply();
	}

	
	
	

}
