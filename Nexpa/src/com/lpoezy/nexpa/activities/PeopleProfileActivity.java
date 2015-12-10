package com.lpoezy.nexpa.activities;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PeopleProfileActivity extends Activity implements Correspondent.OnCorrespondentUpdateListener{

	private ImageView imgProfile;
	private Button btnMessage;
	private TextView txtDistance;
	private TextView txtFName;
	private TextView txtAge;
	private TextView txtGender;
	private TextView txtStatus;
	private TextView txtAboutMe;
	private TextView txtLookingType;


	String username;
	String email;
	String fname;
	String age;
	String gender;
	String distance;
	String about_me;
	String looking_type;
	String status;
	private long userId;
	private TextView mTvJobTitle;
	private TextView mTvUname;
	private TextView mTvUrl0;
	private TextView mTvUrl1;
	private TextView mTvUrl2;
	protected Correspondent mCorrespondent;
	public static boolean isRunning = false;

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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people_profile);

		imgProfile = (ImageView) findViewById(R.id.img_profile);
//		RoundedImageView riv = new RoundedImageView(this);
//		Bitmap rawImage = BitmapFactory.decodeResource(this.getResources(),
//		R.drawable.pic_sample_girl);
//
//		Bitmap circImage = riv.getCroppedBitmap(rawImage, 400);
//		imgProfile.setImageBitmap(circImage);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			Log.e("NOTICE", "Device cannot handle ActionBar");
		}
		
		Intent intent = getIntent();
		userId = intent.getLongExtra("TAG_GEO_USER_ID", -1);
		username = intent.getStringExtra("TAG_GEO_USER");
		email = intent.getStringExtra("TAG_GEO_EMAIL");

/*
		txtFName = (TextView) findViewById(R.id.user_fname);
		//txtLName = (TextView) findViewById(R.id.email);
		txtAge = (TextView) findViewById(R.id.user_age);
		txtGender = (TextView) findViewById(R.id.user_gender);
		txtLookingType = (TextView) findViewById(R.id.user_looking_for);
		txtStatus = (TextView) findViewById(R.id.user_status);
		txtAboutMe = (TextView) findViewById(R.id.user_about);

		Intent intent = getIntent();
		userId = intent.getLongExtra("TAG_GEO_USER_ID", -1);
		username = intent.getStringExtra("TAG_GEO_USER");
		email = intent.getStringExtra("TAG_GEO_EMAIL");
		fname = intent.getStringExtra("TAG_GEO_FNAME");
		age = intent.getStringExtra("TAG_GEO_AGE");
		gender = intent.getStringExtra("TAG_GEO_GENDER");
		distance = intent.getStringExtra("TAG_GEO_DISTANCE");
		about_me = intent.getStringExtra("TAG_GEO_ABOUTME");
		looking_type = intent.getStringExtra("TAG_GEO_LOOKING_TYPE");
		status = intent.getStringExtra("TAG_GEO_STATUS");


		//txtLName = (TextView) findViewById(R.id.email);
		
		if (age.length() < 4) {
			txtFName.setText(fname + ",");
			txtAge.setText(age);
		} else {
			txtFName.setText(fname);
			txtAge.setText("");
		}
		if (gender.length() == 0) {
			if ((looking_type.length() == 0) || (looking_type.equals("null"))) {
				txtGender.setText("");
				txtLookingType.setText("");
			} else {
				txtGender.setText("");
				txtLookingType.setText("Looking for " + looking_type);
			}
		} else {
			if ((looking_type.length() == 0) || (looking_type.equals("null"))) {
				txtGender.setText(gender);
				txtLookingType.setText("");
			} else {
				txtGender.setText(gender + ",");
				txtLookingType.setText("Looking for " + looking_type);
			}
		}

		if ((about_me.length() == 0) || (about_me.equals("null"))) {
			txtAboutMe.setText("No status yet");
		} else {
			txtAboutMe.setText(about_me);
		}

		txtStatus.setText(status);
		
		*/
		
		
		mTvJobTitle = (TextView)this.findViewById(R.id.tv_job_title);
        mTvUname = (TextView)this.findViewById(R.id.tv_uname);
        mTvUrl0 = (TextView)this.findViewById(R.id.tv_url0);
        mTvUrl1 = (TextView)this.findViewById(R.id.tv_url1);
        mTvUrl2 = (TextView)this.findViewById(R.id.tv_url2);
		

		btnMessage = (Button) findViewById(R.id.btn_mes);
		btnMessage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intentMes = new Intent(PeopleProfileActivity.this, ChatActivity.class);
				intentMes.putExtra("userid", userId);
				intentMes.putExtra("email", email);
				intentMes.putExtra("username", username);
				intentMes.putExtra("fname", fname);
				startActivity(intentMes);
			}
		});


	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		isRunning = true;
		resetProfilePic();
		resetUserInfo();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		isRunning = false;
	}
	
	private void resetProfilePic(){
		
		String imgDecodableString = ProfilePicture.getUserImgDecodableString(this);
		
        Bitmap rawImage = BitmapFactory.decodeResource(getResources(),
        R.drawable.pic_sample_girl);
      
        RoundedImageView riv = new RoundedImageView(this);
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 400);
        imgProfile.setImageBitmap(circImage);
        
        mCorrespondent = new Correspondent();
		mCorrespondent.setId(userId);
		mCorrespondent.addListener(PeopleProfileActivity.this);
        
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				mCorrespondent.downloadProfilePicOnline(PeopleProfileActivity.this);
				
			}
		}).start();
	}
	
	private void resetUserInfo() {
		
		SQLiteHandler db = new SQLiteHandler(this);
		db.openToRead();
		
		UserProfile profile = new UserProfile();
		profile.setId(userId);
		profile.downloadOffline(this);
		
		mTvJobTitle.setVisibility(View.GONE);
		mTvUname.setVisibility(View.GONE);
		mTvUrl0.setVisibility(View.GONE);
		mTvUrl1.setVisibility(View.GONE);
		mTvUrl2.setVisibility(View.GONE);
		
		if(profile.getProfession()!=null && !profile.getProfession().equalsIgnoreCase("null") && !profile.getProfession().equals("")){
			mTvJobTitle.setVisibility(View.VISIBLE);
			mTvJobTitle.setText(profile.getProfession());
		}
		
		if(profile.getUsername()!=null &&!profile.getUsername().equalsIgnoreCase("null") && !profile.getUsername().equals("")){
			mTvUname.setVisibility(View.VISIBLE);
			mTvUname.setText(profile.getUsername());
		}
		
		if(profile.getUrl0()!=null &&!profile.getUrl0().equalsIgnoreCase("null") && !profile.getUrl0().equals("")){
			mTvUrl0.setVisibility(View.VISIBLE);
			mTvUrl0.setText(profile.getUrl0());
		}
		
		if(profile.getUrl1()!=null &&!profile.getUrl1().equalsIgnoreCase("null") && !profile.getUrl1().equals("")){
			mTvUrl1.setVisibility(View.VISIBLE);
			mTvUrl1.setText(profile.getUrl1());
		}
		
		if(profile.getUrl2()!=null &&!profile.getUrl2().equalsIgnoreCase("null") && !profile.getUrl2().equals("")){
			mTvUrl2.setVisibility(View.VISIBLE);
			mTvUrl2.setText(profile.getUrl2());
		}
		
		db.close();
		
	}

	

	@Override
	public void onCorrespondentUpdate() {
		Bitmap rawImage = mCorrespondent.getProfilePic();
		
		RoundedImageView riv = new RoundedImageView(PeopleProfileActivity.this);
        final Bitmap circImage = riv.getCroppedBitmap(rawImage, 400);
        
        imgProfile.post(new Runnable() {
			
			@Override
			public void run() {
				
				imgProfile.setImageBitmap(circImage);
				
			}
		});
		
	}

}