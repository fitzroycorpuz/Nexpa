package com.lpoezy.nexpa.utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings.Secure;

public class SystemUtilz {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isConnected = false;
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						if (!isConnected) {

							isConnected = true;

						}
						return true;
					}
				}
			}
		}

		return false;
	}

	// add your photo to the Media Provider's database, making it available in
	// the Android Gallery application and to other apps.
	public static void galleryAddPic(Context context, String currentPhotoPath) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(currentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.getApplicationContext().sendBroadcast(mediaScanIntent);
	}

	// returns a unique file name for a new photo using a date-time stamp:
	public static File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		return image;
	}

	// get unique device id
	// may change after a factory reset
	// A 64-bit number (as a hex string)
	public static String getDeviceUniqueId(Context context) {

		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

	}

}
