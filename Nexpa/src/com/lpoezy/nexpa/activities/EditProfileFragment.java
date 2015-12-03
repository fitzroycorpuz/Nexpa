package com.lpoezy.nexpa.activities;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jivesoftware.smack.util.Base64;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.SystemUtilz;
import com.lpoezy.nexpa.utility.Utilz;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class EditProfileFragment extends DialogFragment {

	public static final String TAG = "EditProfileFragment";

	public static EditProfileFragment newInstance() {
		EditProfileFragment fragment = new EditProfileFragment();
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return fragment;
	}

	protected ProgressDialog pDialog;
	private EditText edtName;
	private DatePicker dpBDay;
	private ImageView profilePic;
	private RadioButton radBoy;
	private RadioButton radGirl;
	private OnShowProfilePicScreenListener mCallback;
	
	 public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	      super.onActivityResult(requestCode, resultCode, intent);
//	      ProfilePicFragment fragment = (ProfilePicFragment) getChildFragmentManager().findFragmentByTag(ProfilePicFragment.TAG);
//	      if(fragment != null){
//	            fragment.onActivityResult(requestCode, resultCode, intent);
//	      }
	      
	      L.debug("EditProfileFragment, onActivityResult");
	 }
	 
	 @Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		try{
			mCallback = (OnShowProfilePicScreenListener)activity;
		}catch(ClassCastException e){L.error(""+e);}
	}
	 
	 @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		resetProfilePic();
		resetProfileInfo();
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_profile_personal, container, false);

		profilePic = (ImageView) v.findViewById(R.id.img_profile_pic);

		

		addClickListenerToBtnProfilePic(v);

		addClickListenerToBtnOk(v);

		// final RadioButton radUnspec;

		dpBDay = (DatePicker) v.findViewById(R.id.dbBirthday);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18);
		dpBDay.setMaxDate(c.getTime().getTime());

		edtName = (EditText) v.findViewById(R.id.edtName);

		radBoy = (RadioButton) v.findViewById(R.id.radioMale);
		radGirl = (RadioButton) v.findViewById(R.id.radioFemale);
		// radUnspec = (RadioButton)
		// dialog.findViewById(R.id.radioUnspecified);

		SQLiteHandler db = new SQLiteHandler(getActivity());
		db.openToRead();
		// Button dialogButton = (Button)
		// dialog.findViewById(R.id.dialogButtonOK);
		edtName.setText(db.getName());

		if ((db.getBDate().equals("")) || (db.getBDate().equals("null"))) {

			dpBDay.updateDate(1980, 0, 1);

		} else {

			Date dtParse = new Date();
			DateUtils du = new DateUtils();
			dtParse = du.convertStringToDateToLocal(db.getBDate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtParse);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			dpBDay.updateDate(year, month, day);
		}

		if (db.getGender().equals("M")) {
			radBoy.setChecked(true);
		} else if (db.getGender().equals("F")) {
			radGirl.setChecked(true);
		}

		db.close();

		return v;
	}

	private void addClickListenerToBtnOk(View v) {

		((Button) v.findViewById(R.id.dialogButtonOK)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// check internet connection before performing http request
				if (!SystemUtilz.isNetworkAvailable(getActivity())) {

					String msg = getResources().getString(R.string.msg_no_internet);
					L.error(msg);
					// L.makeText(SettingsActivity.this, msg,
					// AppMsg.STYLE_INFO);
					return;
				}

				pDialog = new ProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.setMessage("Saving ...");
				pDialog.show();

				new Thread(new Runnable() {

					@Override
					public void run() {

						saveProfilePicOnline();

						saveUserInfoOnline();

						profilePic.post(new Runnable() {

							@Override
							public void run() {
								pDialog.dismiss();
								pDialog = null;
							}
						});
					}
				}).start();

			}
		});

	}

	protected void saveUserInfoOnline() {
		String fullname = edtName.getText().toString();
		
		String fname = fullname;
		String lname = null;
		String birthday = null;

		int day = dpBDay.getDayOfMonth();
		int month = dpBDay.getMonth();
		int year = dpBDay.getYear();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
		@SuppressWarnings("deprecation")
		String formatedDate = sdf.format(new Date(year, month, day));

		try {
			Date date = sdf.parse(formatedDate);
			DateUtils du = new DateUtils();
			
			birthday = du.convertDateToStringToLocalTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String gender = null;

		if (radGirl.isChecked()) {
			gender = "F";
		} else if (radBoy.isChecked()) {
			gender = "M";
		}
		long now = System.currentTimeMillis();
		
		String dateUpdated = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
		
		UserProfile userProfile = new UserProfile(fname, lname, birthday, gender, dateUpdated);

		userProfile.saveOnline(getActivity());
		userProfile.updateOffline(getActivity());

	}

	protected void saveProfilePicOnline() {
		
		SQLiteHandler db = new SQLiteHandler(getActivity());
		db.openToRead();
		ProfilePicture pic = new ProfilePicture();
		pic.setUserId(Long.parseLong(db.getLoggedInID()));
		pic.saveImgOnline(getActivity());
		db.close();
		
	}

	private void addClickListenerToBtnProfilePic(View v) {
		((LinearLayout) v.findViewById(R.id.btn_profile_pic)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onShowProfilePicScreen();
//				DialogFragment profPicDialog = ProfilePicFragment.newInstance();
//
//				profPicDialog.show(getFragmentManager().beginTransaction(), ProfilePicFragment.TAG);

			}
		});

	}
	
	private void resetProfileInfo() {
		
		UserProfile userProfile = new UserProfile();
		userProfile.downloadOffline(getActivity());
		
		edtName.setText(userProfile.getFname());
		edtName.setSelection(edtName.getText().length());
		
		if(userProfile.getGender().equalsIgnoreCase("M")){
			radGirl.setChecked(false);
			radBoy.setChecked(true);
		}else{
			radGirl.setChecked(true);
			radBoy.setChecked(false);
		}
		
		String dtStart = userProfile.getBirthday();  
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {  
		    Date date = format.parse(dtStart);  
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    int year 	= calendar.get(Calendar.YEAR);
			int month 	= calendar.get(Calendar.MONTH);
			int day 	= calendar.get(Calendar.DAY_OF_MONTH);
			dpBDay.updateDate(year, month, day); 
		} catch (ParseException e) {  
		   L.error(""+e);  
		}
	}

	private void resetProfilePic() {
		

		if (profilePic != null) {
			// String imgDecodableString =
			// Utilz.getDataFrmSharedPref(SettingsActivity.this,
			// UserProfile.PROFILE_PIC_LOC, "");

			long userId = -1;
			SQLiteHandler db = new SQLiteHandler(getActivity());
			db.openToRead();
			userId = Long.parseLong(db.getLoggedInID());
			db.close();

			ProfilePicture pic = new ProfilePicture();
			pic.setUserId(userId);
			pic.downloadOffline(getActivity());

			String imgDecodableString = pic.getImgDir() + "/" + pic.getImgFile();

			Bitmap rawImage = BitmapFactory.decodeResource(getResources(), R.drawable.pic_sample_girl);

			if ((pic.getImgDir() != null && !pic.getImgDir().isEmpty())
					&& (pic.getImgFile() != null && !pic.getImgFile().isEmpty())) {
				L.debug("SettingsActivity, imgDecodableString " + imgDecodableString);
				// Get the dimensions of the View
				int targetW = profilePic.getWidth();
				int targetH = profilePic.getHeight();

				BmpFactory bmpFactory = new BmpFactory();
				rawImage = bmpFactory.getBmpWithTargetWTargetHFrm(targetW, targetH, imgDecodableString);
			}

			profilePic.setImageBitmap(rawImage);
		}

	}
	
	public interface OnShowProfilePicScreenListener
	{
		public void onShowProfilePicScreen();
	}

}
