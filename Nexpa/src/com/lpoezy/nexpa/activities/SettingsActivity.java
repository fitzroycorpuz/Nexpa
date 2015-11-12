package com.lpoezy.nexpa.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jivesoftware.smack.util.Base64;

import com.appyvet.rangebar.RangeBar;
import com.devspark.appmsg.AppMsg;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.JSON.Profile;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.NiceDialog;
import com.lpoezy.nexpa.utility.SystemUtilz;
import com.lpoezy.nexpa.utility.Utilz;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	RangeBar rbDistance;

	NiceDialog nd;
	LinearLayout ln_personal;
	// LinearLayout ln_preference;
	LinearLayout ln_settings;
	LinearLayout ln_distance;
	Button ln_status;
	Dialog dialog;
	Dialog dialogPref;
	Dialog dialogSettings;
	Dialog dialogStatus;

	ImageView imgProfile;
	EditText edtName;
	EditText edtStatus;
	TextView txtStat;
	TextView txtCharLeft;

	RadioButton rad_lookingfor_friends;
	RadioButton rad_lookingfor_date;
	RadioButton rad_lookingfor_serious_relationship;
	RadioButton rad_lookingfor_networking;
	String strLookingForStat;

	RadioButton rad_sexual_orientation_unspecified;
	RadioButton rad_sexual_orientation_straight;
	RadioButton rad_sexual_orientation_gay_lesbian;
	RadioButton rad_sexual_orientation_bisexual;
	RadioButton rad_sexual_orientation_transgendered;
	String strSexOrien;

	RadioButton rad_looking_for_men;
	RadioButton rad_looking_for_women;
	RadioButton rad_looking_for_both;
	String strGenderPref;

	CheckBox cbx_orien_straight;
	CheckBox cbx_orien_gay;
	CheckBox cbx_orien_lesbian;
	CheckBox cbx_orien_bisexual;
	CheckBox cbx_orien_transgendered;
	CheckBox cbx_orien_unspecified;
	String strShowOrientation;

	RadioButton rad_relationship_unspecified;
	RadioButton rad_relationship_single;
	RadioButton rad_relationship_in_a_rel;
	RadioButton rad_relationship_married;
	RadioButton rad_relationship_separated;
	RadioButton rad_relationship_widowed;
	RadioButton rad_relationship_complicated;
	String strRelStat;

	RadioButton rad_chat_everyone;
	RadioButton rad_chat_friends;
	RadioButton rad_chat_noone;
	String strIndieChat;

	RadioButton rad_com_receive;
	RadioButton rad_com_dont_receive;
	String strComChat;

	EditText edOldp;
	EditText edNewp;
	EditText edVerip;
	Button btnSavePass;

	int statIndexer;
	String distTick = "";

	private DatePicker dpBDay;

	private Button btnLogout;

	SQLiteHandler db;
	private SessionManager session;
	Profile jsonProfile;

	protected ImageView mProfilePic;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	@Override
	protected void onResume() {

		super.onResume();
		if (dialog != null && dialog.isShowing()) {
			resetProfilePic();
		}

	}

	

	@Override
	protected void onDestroy() {

		super.onDestroy();

		db.close();
		db = null;
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			Log.e("NOTICE", "Device cannot handle ActionBar");
		}

		db = new SQLiteHandler(this);
		db.openToWrite();
		
		

		ln_personal = (LinearLayout) findViewById(R.id.ln_personal);
		ln_personal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				final DateUtils du = new DateUtils();
				// Intent intent = new Intent(UserProfileActivity.this,
				// UserPersonalActivity.class);
				// intent.putExtra("user_id", "");
				dialog = new Dialog(SettingsActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.activity_profile_personal);
				
				resetProfilePic();

				((LinearLayout) dialog.findViewById(R.id.btn_profile_pic))
						.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						DialogFragment profPicDialog = ProfilePicFragment.newInstance();

						profPicDialog.show(getFragmentManager().beginTransaction(), ProfilePicFragment.TAG);

					}
				});
				
				
				
				((Button)dialog.findViewById(R.id.dialogButtonOK)).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
						//check internet connection before performing http request
						if(!SystemUtilz.isNetworkAvailable(SettingsActivity.this)){
							
							String msg = getResources().getString(R.string.msg_no_internet);
							L.error(msg);
							//L.makeText(SettingsActivity.this, msg, AppMsg.STYLE_INFO);
							return;
						}
						
						final String imgDecodableString = Utilz.getDataFrmSharedPref(SettingsActivity.this, UserProfile.PROFILE_PIC_LOC, "");
						
						if (imgDecodableString!=null && !imgDecodableString.isEmpty()) {
							
							final ImageView profilePic = (ImageView) dialog.findViewById(R.id.img_profile_pic);
							// Get the dimensions of the View
				            int targetW = profilePic.getWidth();
				            int targetH = profilePic.getHeight();
							BmpFactory bmpFactory = new BmpFactory();
							final int MAX_SIZE = 100;
							Bitmap bmp = bmpFactory.getBmpWithTargetWTargetHFrm(
									MAX_SIZE , 
									MAX_SIZE , 
									imgDecodableString);
							
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							
							bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
							
					        byte [] byte_arr = stream.toByteArray();
					        
					        final String imageStr = Base64.encodeBytes(byte_arr);
					        
				        	new Thread(new Runnable() {
								
								@Override
								public void run() {
									L.debug("started sending profile pic to server directory...");
									final String spec = AppConfig.URL_PROFILE_PIC;
									
									SQLiteHandler db = new SQLiteHandler(SettingsActivity.this);
							         db.openToRead();
							         String userId = db.getLoggedInID();
							         db.close();
							         
							         Uri uri = Uri.parse(imgDecodableString);
							         final String imgFile = SystemUtilz.getDeviceUniqueId(getApplicationContext())+userId+uri.getLastPathSegment().replace(" ", "");
							         L.debug("imgFile "+imgFile);
							        
							         long now = System.currentTimeMillis();
							         final String dateCreated = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
							         
							         HashMap<String, String> postDataParams = new HashMap<String, String>();
							         postDataParams.put("tag", "upload");
							         postDataParams.put("image", imageStr);
							         postDataParams.put("img_file", imgFile);
							         postDataParams.put("user_id", userId);
							         postDataParams.put("date_created", dateCreated);
									
							         String webPage = HttpUtilz.makeRequest(spec, postDataParams);
							         
									L.debug("webPage: "+ webPage );
									
									
								}
							}).start();
						}
						
					}
				});

				final RadioButton radBoy;
				final RadioButton radGirl;
				// final RadioButton radUnspec;

				dpBDay = (DatePicker) dialog.findViewById(R.id.dbBirthday);
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18);
				dpBDay.setMaxDate(c.getTime().getTime());

				edtName = (EditText) dialog.findViewById(R.id.edtName);

				radBoy = (RadioButton) dialog.findViewById(R.id.radioMale);
				radGirl = (RadioButton) dialog.findViewById(R.id.radioFemale);
				// radUnspec = (RadioButton)
				// dialog.findViewById(R.id.radioUnspecified);

				//Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
				edtName.setText(db.getName());

				if ((db.getBDate().equals("")) || (db.getBDate().equals("null"))) {
					dpBDay.updateDate(1980, 0, 1);
				} else {
					Date dtParse = new Date();
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

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				dialog.show();
				dialog.getWindow().setAttributes(lp);
			}
		});
		ln_distance = (LinearLayout) findViewById(R.id.ln_distance);
		ln_distance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialogPref = new Dialog(SettingsActivity.this);
				dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogPref.setContentView(R.layout.activity_profile_distance_settings);

				rbDistance = (RangeBar) dialogPref.findViewById(R.id.rbDistance);
				rbDistance.setRangeBarEnabled(false);
				rbDistance.setSeekPinByValue(Float.parseFloat(db.getBroadcastDist()));

				rbDistance.setPinColor(getResources().getColor(R.color.EDWARD));
				rbDistance.setConnectingLineColor(getResources().getColor(R.color.EDWARD));
				rbDistance.setSelectorColor(getResources().getColor(R.color.EDWARD));
				rbDistance.setPinRadius(30f);
				rbDistance.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
					@Override
					public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
							String leftPinValue, String rightPinValue) {
						distTick = rightPinValue;
					}
				});

				Button dialogButton = (Button) dialogPref.findViewById(R.id.dialogButtonOK);
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						db.updateBroadcastDist(distTick);
						dialogPref.dismiss();
					}
				});
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialogPref.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				dialogPref.show();
				dialogPref.getWindow().setAttributes(lp);
			}
		});
		// //////////////
		// ln_preference = (LinearLayout) findViewById(R.id.ln_preference);
		// ln_preference.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View arg0)
		// {
		// dialogPref = new Dialog(SettingsActivity.this);
		// dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialogPref.setContentView(R.layout.activity_profile_preferences);
		// rad_lookingfor_friends = (RadioButton)
		// dialogPref.findViewById(R.id.rad_lookingfor_friends);
		// rad_lookingfor_date = (RadioButton)
		// dialogPref.findViewById(R.id.rad_lookingfor_date);
		// rad_lookingfor_serious_relationship = (RadioButton)
		// dialogPref.findViewById(R.id.rad_lookingfor_serious_relationship);
		// rad_lookingfor_networking = (RadioButton)
		// dialogPref.findViewById(R.id.rad_lookingfor_networking);
		// //Looking for
		// if
		// ((db.getLookingForStatus().equals(""))||(db.getLookingForStatus().equals("null"))){
		// rad_lookingfor_friends.setChecked(true);
		// }
		// else if (db.getLookingForStatus().equals("DA")){
		// rad_lookingfor_date.setChecked(true);
		// }
		// else if (db.getLookingForStatus().equals("SE")){
		// rad_lookingfor_serious_relationship.setChecked(true);
		// }
		// else if (db.getLookingForStatus().equals("NE")){
		// rad_lookingfor_networking.setChecked(true);
		// }
		// else{
		// rad_lookingfor_friends.setChecked(true);
		// }
		// //Sexual Orientation
		// rad_sexual_orientation_unspecified = (RadioButton)
		// dialogPref.findViewById(R.id.rad_sexual_orientation_unspecified);
		// rad_sexual_orientation_straight = (RadioButton)
		// dialogPref.findViewById(R.id.rad_sexual_orientation_straight);
		// rad_sexual_orientation_gay_lesbian = (RadioButton)
		// dialogPref.findViewById(R.id.rad_sexual_orientation_gay_lesbian);
		// rad_sexual_orientation_bisexual = (RadioButton)
		// dialogPref.findViewById(R.id.rad_sexual_orientation_bisexual);
		// rad_sexual_orientation_transgendered = (RadioButton)
		// dialogPref.findViewById(R.id.rad_sexual_orientation_transgendered);
		// if
		// ((db.getSexOrien().equals(""))||(db.getSexOrien().equals("null"))){
		// rad_sexual_orientation_unspecified.setChecked(true);
		// }
		// else if (db.getSexOrien().equals("ST")){
		// rad_sexual_orientation_straight.setChecked(true);
		// }
		// else if (db.getSexOrien().equals("LB")){
		// rad_sexual_orientation_gay_lesbian.setChecked(true);
		// }
		// else if (db.getSexOrien().equals("BI")){
		// rad_sexual_orientation_bisexual.setChecked(true);
		// }
		// else if (db.getSexOrien().equals("TR")){
		// rad_sexual_orientation_transgendered.setChecked(true);
		// }
		// else{
		// rad_sexual_orientation_unspecified.setChecked(true);
		// }
		// //Show gender
		// rad_looking_for_men = (RadioButton)
		// dialogPref.findViewById(R.id.rad_looking_for_men);
		// rad_looking_for_women = (RadioButton)
		// dialogPref.findViewById(R.id.rad_looking_for_women);
		// rad_looking_for_both = (RadioButton)
		// dialogPref.findViewById(R.id.rad_looking_for_both);
		//
		// String strLookingGender = db.getLookingGender();
		// if
		// ((strLookingGender.equals(""))||(strLookingGender.equals("null"))){
		// rad_looking_for_both.setChecked(true);
		// }
		// else if (strLookingGender.equals("ML")){
		// rad_looking_for_men.setChecked(true);
		// }
		// else if (strLookingGender.equals("FM")){
		// rad_looking_for_women.setChecked(true);
		// }
		// else{
		// rad_looking_for_both.setChecked(true);
		// }
		// //Show Profile Sexual Orientation
		// cbx_orien_straight = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_straight);
		// cbx_orien_gay = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_gay);
		// cbx_orien_lesbian = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_lesbian);
		// cbx_orien_bisexual = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_bisexual);
		// cbx_orien_transgendered = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_transgendered);
		// cbx_orien_unspecified = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_unspecified);
		// String strOrien = db.getOrientationToShow();
		//
		// char ch_orien_straight = strOrien.charAt(0);
		// char ch_orien_gay = strOrien.charAt(2);
		// char ch_orien_lesbian = strOrien.charAt(4);
		// char ch_orien_bisexual = strOrien.charAt(6);
		// char ch_orien_transgendered = strOrien.charAt(8);
		// char ch_orien_unspecified = strOrien.charAt(10);
		//
		// if
		// ((db.getOrientationToShow().equals(""))||(db.getOrientationToShow().equals("null"))){
		// // S-G-L-B-T-U
		// cbx_orien_straight.setChecked(true);
		// cbx_orien_gay.setChecked(true);
		// cbx_orien_lesbian.setChecked(true);
		// cbx_orien_bisexual.setChecked(true);
		// cbx_orien_transgendered.setChecked(true);
		// cbx_orien_unspecified.setChecked(true);
		// }
		// else{
		// if (ch_orien_straight == '1'){
		// cbx_orien_straight.setChecked(true);
		// }
		// else{
		// cbx_orien_straight.setChecked(false);
		// }
		// if (ch_orien_gay == '1'){
		// cbx_orien_gay.setChecked(true);
		// }
		// else{
		// cbx_orien_gay.setChecked(false);
		// }
		// if (ch_orien_lesbian == '1'){
		// cbx_orien_lesbian.setChecked(true);
		// }
		// else{
		// cbx_orien_lesbian.setChecked(false);
		// }
		// if (ch_orien_bisexual == '1'){
		// cbx_orien_bisexual.setChecked(true);
		// }
		// else{
		// cbx_orien_bisexual.setChecked(false);
		// }
		// if (ch_orien_transgendered == '1'){
		// cbx_orien_transgendered.setChecked(true);
		// }
		// else{
		// cbx_orien_transgendered.setChecked(false);
		// }
		// if (ch_orien_unspecified == '1'){
		// cbx_orien_unspecified.setChecked(true);
		// }
		// else{
		// cbx_orien_unspecified.setChecked(false);
		// }
		// }
		// //Relationship status
		// rad_relationship_unspecified = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_unspecified);
		// rad_relationship_single = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_single);
		// rad_relationship_in_a_rel = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_in_a_rel);
		// rad_relationship_married = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_married);
		// rad_relationship_separated = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_separated);
		// rad_relationship_widowed = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_widowed);
		// rad_relationship_complicated = (RadioButton)
		// dialogPref.findViewById(R.id.rad_relationship_complicated);
		//
		// String relStatus = db.getRelStatus();
		// if ((relStatus.equals(""))||(relStatus.equals("null"))){
		// rad_relationship_unspecified.setChecked(true);
		// }
		// else if (relStatus.equals("SI")){
		// rad_relationship_single.setChecked(true);
		// }
		// else if (relStatus.equals("IN")){
		// rad_relationship_in_a_rel.setChecked(true);
		// }
		// else if (relStatus.equals("MA")){
		// rad_relationship_married.setChecked(true);
		// }
		// else if (relStatus.equals("SE")){
		// rad_relationship_separated.setChecked(true);
		// }
		// else if (relStatus.equals("WI")){
		// rad_relationship_widowed.setChecked(true);
		// }
		// else if (relStatus.equals("CO")){
		// rad_relationship_complicated.setChecked(true);
		// }
		// else{
		// rad_relationship_unspecified.setChecked(true);
		// }
		// Button dialogButton = (Button)
		// dialogPref.findViewById(R.id.dialogButtonOK);
		// dialogButton.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// //Save changes in 'Looking For'
		// if (rad_lookingfor_date.isChecked()){
		// strLookingForStat = "DA";
		// }
		// else if (rad_lookingfor_serious_relationship.isChecked()){
		// strLookingForStat = "SE";
		// }
		// else if (rad_lookingfor_networking.isChecked()){
		// strLookingForStat = "NE";
		// }
		// else {
		// strLookingForStat = "FR";
		// }
		// //Save changes in 'Sexual Orientation'
		// if (rad_sexual_orientation_straight.isChecked()){
		// strSexOrien = "ST";
		// }
		// else if (rad_sexual_orientation_gay_lesbian.isChecked()){
		// strSexOrien = "LB";
		// }
		// else if (rad_sexual_orientation_bisexual.isChecked()){
		// strSexOrien = "BI";
		// }
		// else if (rad_sexual_orientation_transgendered.isChecked()){
		// strSexOrien = "TR";
		// }
		// else {
		// strSexOrien = "UN";
		// }
		// //Save changes in 'Gender Pref'
		// if (rad_looking_for_men.isChecked()){
		// strGenderPref = "ML";
		// }
		// else if (rad_looking_for_women.isChecked()){
		// strGenderPref = "FM";
		// }
		// else {
		// strGenderPref = "SB";
		// }
		// //Save changes in Show Sex Orien
		// //Show Profile Sexual Orientation
		// cbx_orien_straight = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_straight);
		// cbx_orien_gay = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_gay);
		// cbx_orien_lesbian = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_lesbian);
		// cbx_orien_bisexual = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_bisexual);
		// cbx_orien_transgendered = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_transgendered);
		// cbx_orien_unspecified = (CheckBox)
		// dialogPref.findViewById(R.id.cbx_orien_unspecified);
		// strShowOrientation = "S-G-L-B-T-U";
		// StringBuilder SBorien = new StringBuilder(strShowOrientation);
		// SBorien.setCharAt(4, 'x');
		// // S-G-L-B-T-U
		// if (cbx_orien_straight.isChecked()){
		// SBorien.setCharAt(0,'1');
		// }
		// else{
		// SBorien.setCharAt(0, '0');
		// }
		// if (cbx_orien_gay.isChecked()){
		// SBorien.setCharAt(2, '1');
		// }
		// else{
		// SBorien.setCharAt(2, '0');
		// }
		// if (cbx_orien_lesbian.isChecked()){
		// SBorien.setCharAt(4,'1');
		// }
		// else{
		// SBorien.setCharAt(4,'0');
		// }
		// if (cbx_orien_bisexual.isChecked()){
		// SBorien.setCharAt(6, '1');
		// }
		// else{
		// SBorien.setCharAt(6,'0');
		// }
		// if (cbx_orien_transgendered.isChecked()){
		// SBorien.setCharAt(8, '1');
		// }
		// else{
		// SBorien.setCharAt(8,'0');
		// }
		// if (cbx_orien_unspecified.isChecked()){
		// SBorien.setCharAt(10,'1');
		// }
		// else{
		// SBorien.setCharAt(10, '0');
		// }
		// Log.e("SBORIEN",SBorien.toString());
		// //Save changes in 'Relationship status'
		// if (rad_relationship_single.isChecked()){
		// strRelStat = "SI";
		// }
		// else if (rad_relationship_in_a_rel.isChecked()){
		// strRelStat = "IN";
		// }
		// else if (rad_relationship_married.isChecked()){
		// strRelStat = "MA";
		// }
		// else if (rad_relationship_separated.isChecked()){
		// strRelStat = "SE";
		// }
		// else if (rad_relationship_widowed.isChecked()){
		// strRelStat = "WI";
		// }
		// else if (rad_relationship_complicated.isChecked()){
		// strRelStat = "CO";
		// }
		// else {
		// strRelStat = "UN";
		// }
		//
		// db.updateUserPreference(strLookingForStat, strSexOrien,
		// strGenderPref, strRelStat, SBorien.toString());
		// jsonProfile.updateProfileOnServer(SettingsActivity.this);
		// dialogPref.dismiss();
		// }
		// });
		// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.copyFrom(dialogPref.getWindow().getAttributes());
		// lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		// lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		// dialogPref.show();
		// dialogPref.getWindow().setAttributes(lp);
		// }
		// });
		ln_settings = (LinearLayout) findViewById(R.id.ln_settings);
		ln_settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Intent intent = new Intent(UserProfileActivity.this,
				// UserPersonalActivity.class);
				// intent.putExtra("user_id", "");
				dialogSettings = new Dialog(SettingsActivity.this);
				dialogSettings.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogSettings.setContentView(R.layout.activity_profile_settings);

				edOldp = (EditText) dialogSettings.findViewById(R.id.edOldp);
				edNewp = (EditText) dialogSettings.findViewById(R.id.edNewp);
				edVerip = (EditText) dialogSettings.findViewById(R.id.edVerip);
				btnSavePass = (Button) dialogSettings.findViewById(R.id.btnSavePass);
				edOldp.setTypeface(Typeface.DEFAULT);
				edNewp.setTypeface(Typeface.DEFAULT);
				edVerip.setTypeface(Typeface.DEFAULT);

				session = new SessionManager(getApplicationContext());
				btnLogout = (Button) dialogSettings.findViewById(R.id.btn_deac);
				if (!session.isLoggedIn()) {
					UserProfileActivity.logoutUser(SettingsActivity.this, false, null);
				} else {
					// Fetching user details from sqlite
					// HashMap<String, String> user = db.getUserDetails();

					// String name = user.get("name");
					// String email = user.get("email");

					// Displaying the user details on the screen
					// txtName.setText(name);
					// txtEmail.setText(email);

					// Logout button click event

					nd = new NiceDialog();
					// btnLogout.setOnClickListener(new OnClickListener() {
					// @Override
					// public void onClick(View v) {
					// promptYesNoDialog("Deactivate Account","You will lose all
					// Toucan's data on this device but won't remove your data
					// on our server.\n\nDo you wish to continue?
					// ",UserProfileActivity.this,"DEAC", false);
					// }
					// });
				}

				btnSavePass.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (edOldp.getText().toString().trim().length() > 0
								&& edNewp.getText().toString().trim().length() > 0
								&& edVerip.getText().toString().trim().length() > 0) {
							if (edNewp.getText().toString().trim().length() > 7) {
								if (edOldp.getText().toString().equals(db.getPass())) {
									if (edVerip.getText().toString().equals(edNewp.getText().toString())) {
										db.updatePass(edVerip.getText().toString());
										jsonProfile.updatePasswordOnServer(SettingsActivity.this);
									} else {
										NiceDialog.promptDialog("Invalid Password!",
												"Password doesn't match confirmation.", SettingsActivity.this,
												"warning");
									}
								} else {
									NiceDialog.promptDialog("Invalid Password!", "Current password did not match.",
											SettingsActivity.this, "warning");

								}
							} else {
								NiceDialog.promptDialog("Weak Password!", "Password should be atleast 8 characters.",
										SettingsActivity.this, "warning");

							}
						} else {
							NiceDialog.promptDialog("Invalid input!",
									"Please fill-out all fields in order to change your password.",
									SettingsActivity.this, "warning");
						}
					}
				});

				rad_chat_everyone = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_everyone);
				rad_chat_friends = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_friends);
				rad_chat_noone = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_noone);
				strIndieChat = "";

				if ((db.getIndiSetChatPrivacy().equals("")) || (db.getIndiSetChatPrivacy().equals("null"))) {
					rad_chat_everyone.setChecked(true);
				} else if (db.getIndiSetChatPrivacy().equals("NO")) {
					rad_chat_noone.setChecked(true);
				} else if (db.getIndiSetChatPrivacy().equals("FR")) {
					rad_chat_friends.setChecked(true);
				} else {
					rad_chat_everyone.setChecked(true);
				}

				rad_com_receive = (RadioButton) dialogSettings.findViewById(R.id.rad_com_receive);
				rad_com_dont_receive = (RadioButton) dialogSettings.findViewById(R.id.rad_com_dont_receive);
				strComChat = "";
				if ((db.getComSetChatPrivacy().equals("")) || (db.getComSetChatPrivacy().equals("null"))) {
					rad_com_receive.setChecked(true);
				} else if (db.getComSetChatPrivacy().equals("DO")) {
					rad_com_dont_receive.setChecked(true);
				} else {
					rad_com_receive.setChecked(true);
				}

				Button dialogButton = (Button) dialogSettings.findViewById(R.id.dialogButtonOK);
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Save changes in 'Individual Chat'
						if (rad_chat_everyone.isChecked()) {
							strIndieChat = "EV";
						} else if (rad_chat_friends.isChecked()) {
							strIndieChat = "FR";
						} else if (rad_chat_noone.isChecked()) {
							strIndieChat = "NO";
						} else {
							strIndieChat = "EV";
						}

						// Save changes in 'Community Chat'
						if (rad_com_receive.isChecked()) {
							strComChat = "RE";
						} else if (rad_com_dont_receive.isChecked()) {
							strComChat = "DO";
						} else {
							strComChat = "RE";
						}
						db.updateUserSettings(strIndieChat, strComChat);

						dialogSettings.dismiss();
					}
				});
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialogSettings.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				dialogSettings.show();
				dialogSettings.getWindow().setAttributes(lp);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	private void resetProfilePic() {
		ImageView profilePic = (ImageView) dialog.findViewById(R.id.img_profile_pic);

		if (profilePic != null) {
			String imgDecodableString = Utilz.getDataFrmSharedPref(SettingsActivity.this, UserProfile.PROFILE_PIC_LOC, "");
			
			 Bitmap rawImage = BitmapFactory.decodeResource(getResources(),
				        R.drawable.pic_sample_girl);
			 
			
			if (imgDecodableString!=null && !imgDecodableString.isEmpty()) {
				
				// Get the dimensions of the View
	            int targetW = profilePic.getWidth();
	            int targetH = profilePic.getHeight();
	            
	            BmpFactory  bmpFactory = new BmpFactory();
	        	rawImage = bmpFactory.getBmpWithTargetWTargetHFrm(targetW, targetH, imgDecodableString);
			}
			
			profilePic.setImageBitmap(rawImage);
		}

	}

}