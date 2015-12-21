package com.lpoezy.nexpa.activities;

import android.os.Bundle;
import android.provider.ContactsContract.Profile;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.util.Base64;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.R.layout;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.SystemUtilz;
import com.lpoezy.nexpa.utility.Utilz;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass. Use the
 * {@link ProfilePicFragment#newInstance} factory method to create an instance
 * of this fragment.
 *
 */
public class ProfilePicFragment extends DialogFragment {

	public static final String TAG = "ProfilePicFragment";
	protected static final int RESULT_LOAD_IMG = 1001;
	protected static final int REQUEST_TAKE_PHOTO = 1002;

	public static ProfilePicFragment newInstance() {
		ProfilePicFragment fragment = new ProfilePicFragment();
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return fragment;
	}

	protected String mCurrentPhotoPath;

	public ProfilePicFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {

		}
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		Utilz.saveToSharedPref(getActivity(), ProfilePicture.TEMP_LOC, "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_profile_pic, container, false);

		((LinearLayout) v.findViewById(R.id.btn_take_photo)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
					// Create the File where the photo should go
					File photoFile = null;
					try {
						photoFile = SystemUtilz.createImageFile();
					} catch (IOException ex) {

					}
					// Continue only if the File was successfully created
					if (photoFile != null) {
						mCurrentPhotoPath  = photoFile.getAbsolutePath();
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
						getActivity().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
					}
				}

			}
		});

		((LinearLayout) v.findViewById(R.id.btn_choose_existing_photo)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// SystemUtilz.openGalery(getActivity(), RESULT_LOAD_IMG);

				// Create intent to Open Image applications like Gallery, Google
				// Photos
				Intent galleryIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// Start the Intent
				getActivity().startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

			}
		});

		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		L.debug("ProfilePicFragment onActivityResult");
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null,
						null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				mCurrentPhotoPath = cursor.getString(columnIndex);
				cursor.close();

			} else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK && null != data) {
				Bundle extras = data.getExtras();
				SystemUtilz.galleryAddPic(getActivity(), mCurrentPhotoPath);
				//Bitmap imageBitmap = (Bitmap) extras.get("data");
				
			}
			
			if(resultCode == Activity.RESULT_OK){
				//L.debug("ProfilePicFragment, mCurrentPhotoPath: "+mCurrentPhotoPath);
				Utilz.saveToSharedPref(getActivity(), ProfilePicture.TEMP_LOC, mCurrentPhotoPath);
				
				
//				int pos = mCurrentPhotoPath.lastIndexOf("/");
//				 
//				String imgDir = mCurrentPhotoPath.substring(0 , pos);
//				String imgFile = Uri.parse(mCurrentPhotoPath).getLastPathSegment();
//				L.debug("ProfilePicFragment, imgDir: "+imgDir+", imgFile: "+imgFile+", pos "+pos);
//				
//				long userId = -1;
//				SQLiteHandler db = new SQLiteHandler(getActivity());
//				db.openToRead();
//				userId = Long.parseLong(db.getLoggedInID());
//				db.close();
//				
//				long now = System.currentTimeMillis();
//				
//				String dateCreated = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
//				boolean isSyncedOnline = false;
//				ProfilePicture pic = new ProfilePicture(userId, imgDir, imgFile, dateCreated, isSyncedOnline);
//				pic.saveOffline(getActivity());
				
			}
		
			dismiss();
			
		} catch (Exception e) {
		}
	}
	
	

}
