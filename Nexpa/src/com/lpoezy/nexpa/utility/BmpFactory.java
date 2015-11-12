package com.lpoezy.nexpa.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BmpFactory {

	public Bitmap getBmpWithTargetWTargetHFrm(int targetW, int targetH, String path) {

		// First decode with inJustDecodeBounds=true to check dimensions
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, bmOptions);

		// Determine how much to scale down the image
		bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);

		// Decode bitmap with inSampleSize set
		bmOptions.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, bmOptions);
		
	}

	// calculate a sample size value that is a power of two based on a target
	// width and height:
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (reqWidth != 0 && reqHeight != 0) {
			if (height > reqHeight || width > reqWidth) {

				final int halfHeight = height / 2;
				final int halfWidth = width / 2;

				// Calculate the largest inSampleSize value that is a power of 2
				// and
				// keeps both
				// height and width larger than the requested height and width.
				while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
					inSampleSize *= 2;
				}
			}
		}

		return inSampleSize;
	}
	
//	public static Bitmap decodeSampledBitmapFromResource(String imgDecodableString, int targetW, int targetH){
//		
//		
//		 // First decode with inJustDecodeBounds=true to check dimensions
//       BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//       bmOptions.inJustDecodeBounds = true;
//       BitmapFactory.decodeFile(imgDecodableString, bmOptions);
//      
//       // Determine how much to scale down the image
//       bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);
//
//       // Decode bitmap with inSampleSize set
//       bmOptions.inJustDecodeBounds = false;
//   	
//   	
//   	
//		return BitmapFactory.decodeFile(imgDecodableString, bmOptions);
//	}

}
