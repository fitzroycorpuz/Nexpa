package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appyvet.rangebar.RangeBar;
import com.lpoezy.nexpa.activities.CustomGrid;
import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.JSON.JSONParser;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.objects.Users;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.MyLocation;
import com.lpoezy.nexpa.utility.MyLocation.LocationResult;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class AroundMeActivity extends AppCompatActivity
		implements OnRefreshListener, Correspondent.OnCorrespondentUpdateListener {
	private static final String TAG = AroundMeActivity.class.getSimpleName();
	// Button btnUpdate;
	LocationManager locationManager;
	// MyLocationListener locationListener;
	float ftLatitude = 0;
	float ftLongitude = 0;
	Handler mHandler;
	GridView grid;
	CustomGrid adapter;
	SQLiteHandler db;
	float i = 0;
	ArrayList<String> web = new ArrayList<String>();
	ArrayList<String> availabilty = new ArrayList<String>();
	ArrayList<Integer> distance = new ArrayList<Integer>();
	ArrayList<Integer> imageId = new ArrayList<Integer>();
	ArrayList<Bitmap> images = new ArrayList<Bitmap>();
	ArrayList<Correspondent> arr_correspondents = new ArrayList<Correspondent>();
	ArrayList<String> arr_fname = new ArrayList<String>();
	ArrayList<String> arr_age = new ArrayList<String>();
	ArrayList<String> arr_uname = new ArrayList<String>();
	ArrayList<String> arr_gender = new ArrayList<String>();
	ArrayList<String> arr_looking_type = new ArrayList<String>();
	ArrayList<String> arr_date_seen = new ArrayList<String>();
	ArrayList<String> arr_about = new ArrayList<String>();
	ArrayList<String> arr_email = new ArrayList<String>();
	ArrayList<String> arr_status = new ArrayList<String>();

	ArrayList<Users> us = new ArrayList<Users>();
	ArrayList<Users> list = new ArrayList<Users>();

	SwipeRefreshLayout mSwipeRefreshLayout;

	boolean gps_enabled = false;
	boolean network_enabled = false;
	boolean hasGps = false;
	float longitude = 0;
	float latitude = 0;
	String gpsProvider = "";

	String ins_user = "";
	float ins_latitude = 0;
	float ins_longitude = 0;
	String existingUsers;

	DateUtils du;
	private AsyncTask<String, String, String> mTask;

	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUCCESS = "success";

	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> userList;
	JSONArray nearby_users = null;

	// JSON Node names
	private static final String TAG_GEO_SUCCESS = "success";

	private static final String TAG_GEO = "geo";
	private static final String TAG_GEO_PID = "id";
	private static final String TAG_GEO_USER = "user";
	private static final String TAG_GEO_LATITITUDE = "latitude";
	private static final String TAG_GEO_LONGI = "longitude";
	private static final String TAG_GEO_PROVIDER = "gps_provider";
	private static final String TAG_GEO_DATE_CREATE = "date_create";
	private static final String TAG_GEO_FNAME = "firstname";
	private static final String TAG_GEO_LNAME = "lastname";
	private static final String TAG_GEO_BIRTHDAY = "birthday";
	private static final String TAG_GEO_GENDER = "gender";
	private static final String TAG_GEO_DISTANCE = "geo_distance";

	private static final String TAG_GEO_ABOUTME = "about_me";
	private static final String TAG_GEO_LOOKING_TYPE = "looking_type";
	private static final String TAG_GEO_STATUS = "status";

	private static final String TAG_GEO_EMAIL = "email_address";

	public static boolean isRunning = false;
	private int dst;
	private int oldDst;

	// @Override
	// public void onBackPressed() {
	//
	// //super.onBackPressed();
	// SessionManager session = new SessionManager(getApplicationContext());
	// if(session.isLoggedIn()){
	// UserProfileActivity.promptYesNoDialog("Quit Toucan?",
	// "Are you sure you want to log off?",
	// this,
	// "DEAC",
	// true);
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.toolbar, menu);
		return true;
	}

	Dialog dialogPref;
	RangeBar rbDistance;
	EditText rbDistance1;
	String distTick = "";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_distance:
			dialogPref = new Dialog(AroundMeActivity.this);
			dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogPref.setContentView(R.layout.activity_profile_distance_settings);

			rbDistance = (RangeBar) dialogPref.findViewById(R.id.rbDistance);
			rbDistance.setRangeBarEnabled(false);
			dst = 100;
			try {
				dst = Integer.parseInt(db.getBroadcastDist());
			} catch (Exception e) {
				dst = 100;
			}
			rbDistance.setSeekPinByValue(dst);

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
					dst = Integer.parseInt(distTick);
					tryGridToUpdate();
					dialogPref.dismiss();
				}
			});
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(dialogPref.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogPref.show();
			dialogPref.getWindow().setAttributes(lp);
			return true;

		case R.id.action_distance_test:
			dialogPref = new Dialog(AroundMeActivity.this);
			dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogPref.setContentView(R.layout.android_profile_distance_tester);

			rbDistance1 = (EditText) dialogPref.findViewById(R.id.rbDistance);
			dst = 100;
			try {
				dst = Integer.parseInt(db.getBroadcastDist());
			} catch (Exception e) {
				dst = 100;
			}

			Button dialogButton1 = (Button) dialogPref.findViewById(R.id.dialogButtonOK);
			dialogButton1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						dst = Integer.parseInt(rbDistance1.getText().toString());
					} catch (Exception e) {
						dst = 100;
					}
					Log.e("dst", dst + " c");
					db.updateBroadcastDist(distTick);
					tryGridToUpdate();
					dialogPref.dismiss();
				}
			});

			WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
			lp1.copyFrom(dialogPref.getWindow().getAttributes());
			lp1.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogPref.show();
			dialogPref.getWindow().setAttributes(lp1);
			return true;

		default:
			// If we got here, the user's action was not recognized.
			// Invoke the superclass to handle it.
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around_me);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);
		myToolbar.setLogo(R.drawable.icon_nexpa);
		myToolbar.setTitle("");
		oldDst = 0;

		du = new DateUtils();
		db = new SQLiteHandler(this);
		db.openToWrite();
		userList = new ArrayList<HashMap<String, String>>();

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.niagara, R.color.buttercup, R.color.niagara);
		mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.carrara));
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getNewLoc();
					}
				}, 2000);
			}
		});

		// web.add(0, "You");
		// distance.add(0, 0);
		// imageId.add(0, R.drawable.pic_sample_girl);
		// availabilty.add(0, "Online");
		adapter = new CustomGrid(AroundMeActivity.this, web, arr_correspondents/* imageId */, availabilty, distance);
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				if (msg.what == 1) {
					mSwipeRefreshLayout.setRefreshing(false);
					makeNotify("Failed To Retrieve GPS Location", AppMsg.STYLE_ALERT);
				} else {
					Log.e("UU", "CANNOT");
				}
			}
		};

		grid = (GridView) findViewById(R.id.grid);

		grid.setAdapter(adapter);
		grid.setBackgroundColor(Color.WHITE);
		grid.setVerticalSpacing(1);
		grid.setHorizontalSpacing(1);

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<Users> us = new ArrayList<Users>();
				try {
					// Toast.makeText(AroundMeActivity.this, "You Clicked at "
					// +arr_fname.get(position) , Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(AroundMeActivity.this, PeopleProfileActivity.class);
					Log.e("ccc", arr_about.get(position));
					// intent.putExtra("TAG_GEO_PID", us.get(position).getId());
					long correspondentId = arr_correspondents.get(position).getId();

					intent.putExtra("TAG_GEO_USER_ID", correspondentId);
					intent.putExtra("TAG_GEO_USER", arr_uname.get(position));
					intent.putExtra("TAG_GEO_EMAIL", arr_email.get(position));
					intent.putExtra("TAG_GEO_FNAME", arr_fname.get(position));
					intent.putExtra("TAG_GEO_AGE", arr_age.get(position));
					intent.putExtra("TAG_GEO_GENDER", arr_gender.get(position));
					intent.putExtra("TAG_GEO_DISTANCE", "25");
					intent.putExtra("TAG_GEO_ABOUTME", arr_about.get(position));
					intent.putExtra("TAG_GEO_LOOKING_TYPE", arr_looking_type.get(position));
					intent.putExtra("TAG_GEO_STATUS", arr_status.get(position));

					startActivity(intent);
				} catch (Exception e) {
				}
				// finish();
			}
		});

		updateGrid("0");

	}

	private void getNewLoc() {

		String dtUpdate = db.getLocationDateUpdate();
		L.error("AroundMeActivity, getNewLoc dtUpdate: " + dtUpdate);
		if ((dtUpdate == "") || (du.hoursAgo(dtUpdate))) {

			Log.e("LOCATION INTELLIGENCE", "Update needed...");

			LocationResult locationResult = new LocationResult() {

				@Override
				public void gotLocation(Location location) {

					if (location != null) {
						ftLatitude = (float) location.getLatitude();
						ftLongitude = (float) location.getLongitude();
						latitude = ftLatitude;
						longitude = ftLongitude;
						db.insertLocation(longitude, latitude);
						SendLocToServer();

					} else {
						// Looper.prepare();

						try {

							mHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							Log.e("FAIL", "FAILED A:LL");
						}
						// makeNotify("Failed To Retrieve GPS Location",
						// AppMsg.STYLE_INFO);
						// mSwipeRefreshLayout.setRefreshing(false);
					}
				}
			};

			PackageManager packMan = getPackageManager();
			hasGps = packMan.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

			MyLocation myLocation = new MyLocation();
			boolean availLoc = myLocation.getLocation(this, locationResult);
			if (availLoc == false) {
				makeNotify("GPS Services Unavailable", AppMsg.STYLE_ALERT);
				mSwipeRefreshLayout.setRefreshing(false);
			}
		} else {
			Log.e("LOCATION INTELLIGENCE", "Getting db location...");
			ftLatitude = Float.parseFloat(db.getLocationLatitude());
			ftLongitude = Float.parseFloat(db.getLocationLongitude());
			latitude = ftLatitude;
			longitude = ftLongitude;
			SendLocToServer();
		}
	}

	private void updateGrid(String sentType) {
		us = null;
		us = new ArrayList<Users>();
		list = new ArrayList<Users>();
		int dst = 100;

		try {
			dst = Integer.parseInt(db.getBroadcastDist());
		} catch (Exception e) {
			dst = 100;
		}

		grid.invalidateViews();
		list = db.getNearByUserDetails();

		imageId.clear();
		availabilty.clear();
		web.clear();
		distance.clear();

		Comparator<Users> comparator = new Comparator<Users>() {
			@Override
			public int compare(Users lhs, Users rhs) {
				return lhs.getDistance() - rhs.getDistance();
			}
		};
		Collections.sort(list, comparator);

		us = list;

		Animation in = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_r);
		Animation out = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out_r);

		int userSize = us.size();
		int disSize = distance.size();

		//
		

		/*
		 * try { if (userSize < disSize) { for (int i = disSize; i > userSize;
		 * i--) { adapter.removeItem(i - 1); } } if (sentType.equals("1")) { if
		 * (userSize > disSize) { for (int i = disSize; i < userSize; i++) {
		 * imageId.add(i - 1, R.drawable.pic_sample_girl); availabilty.add(i -
		 * 1, ""); web.add(i - 1, ""); distance.add(i - 1, 9999); } } } } catch
		 * (Exception e) {}
		 */


		//roy@vps.gigapros.com/Smack
		//L.debug("xxxxxxxxxxxxxxxxxx isAvailable, "+roster.getPresence("leki@vps.gigapros.com/Smack"));
		
		
		//Roster roster = xmppManager.connection.getRoster();
		//roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        //Presence subscribed = new Presence(Presence.Type.subscribed);
 
//        Presence response = new Presence(Presence.Type.subscribed);
//        response.setTo("leki");
//        connection.sendPacket(response);
//		
//		try {
//			roster.createEntry("leki", "leki@tes.com", null);
//		} catch (XMPPException e) {
//			L.error(""+e);
//		}
//
//		Collection<RosterEntry> entries = roster.getEntries();
//		for (RosterEntry entry : entries) {
//
//
//		    Presence presence = roster.getPresence(entry.getUser());
//
//		    // User A always not available even I set User A to available
//		    L.error("presence.isAvailable() = " + presence.isAvailable());
//
//		    // User A's status always empty
//		    L.error("presence.getStatus() = " + presence.getStatus());
//		    L.error("presence.getType() = " + presence.getType());
//		    L.debug("entry.getUser() "+entry.getUser()); 
//		    // User A's getName() always null
////		    if (entry.getName() != null)
////		    {
////		        L.debug(entry.getName());
////		    } 
////		    else
////		    	L.debug("GetName is null"); 
//		    
//		    if (presence.getType() == Presence.Type.subscribe){
//		    	L.debug(entry.getUser()+", subscribe"); 
//		    }else if(presence.getType() == Presence.Type.unsubscribe){
//		    	L.debug(entry.getUser()+", unsubscribe"); 
//		    }
//
//		}
		
		for (int j = 0; j < us.size(); j++) {
			 //L.debug("ccccccccccc userID: "+us.get(j).getUserId()+", username:"+us.get(j).getUserName());

			final Correspondent correspondent = new Correspondent();
			correspondent.setUsername(us.get(j).getUserName());
			correspondent.addListener(this);
			final long userId = Long.parseLong(Integer.toString(us.get(j).getUserId()));
			correspondent.setId(userId);
			arr_correspondents.add(j, correspondent);

			new Thread(new Runnable() {

				@Override
				public void run() {
					
					correspondent.checkIfOnline();
					
					correspondent.downloadProfilePicOnline(AroundMeActivity.this, userId);

				}
			}).start();

			if (sentType.equals("1")) {// user is already added
				// if (us.get(j).getShown().equals("0")) {
				
				imageId.add(j, R.drawable.pic_sample_girl);
				availabilty.add(j, "INSERTED");
				web.add(j, displayGridCellName(us.get(j).getFName(), us.get(j).getUserName()) + ", "
						+ displayAge(us.get(j).getAge()));

				distance.add(j, us.get(j).getDistance());
				arr_uname.add(j, us.get(j).getUserName());
				arr_fname.add(j, displayGridCellName(us.get(j).getFName(), us.get(j).getUserName()));
				arr_age.add(j, us.get(j).getAge());
				arr_gender.add(j, us.get(j).getGender());
				arr_looking_type.add(j, us.get(j).getLookingType());
				arr_about.add(j, us.get(j).getAboutMe());
				arr_email.add(j, us.get(j).getEmail());
				arr_status.add(j, us.get(j).getStatus());

				// }

				// }
				// else
				/*
				 * else if (us.get(j).getShown().equals("1")) {
				 * us.get(j).setShown("1"); availabilty.set(j, "UPDATED");
				 * web.set(j, displayGridCellName(us.get(j).getFName(),
				 * us.get(j).getUserName()) + ", " +
				 * displayAge(us.get(j).getAge()));
				 * arr_user_id.add(j,Long.parseLong(Integer.toString(us.get(j).
				 * getUserId()))); distance.set(j, us.get(j).getDistance());
				 * arr_uname.add(j, us.get(j).getUserName()); arr_fname.add(j,
				 * displayGridCellName(us.get(j).getFName(),
				 * us.get(j).getUserName())); arr_age.add(j,
				 * us.get(j).getAge()); arr_gender.add(j,
				 * us.get(j).getGender()); arr_looking_type.add(j,
				 * us.get(j).getLookingType()); arr_about.add(j,
				 * us.get(j).getAboutMe()); arr_email.add(j,
				 * us.get(j).getEmail()); arr_status.add(j,
				 * us.get(j).getStatus()); }
				 */
			} else if (sentType.equals("0")) {//
				imageId.add(j, R.drawable.pic_sample_girl);
				availabilty.add(j, "ADDED");
				web.add(j, displayGridCellName(us.get(j).getFName(), us.get(j).getUserName()) + ", "
						+ displayAge(us.get(j).getAge()));
				distance.add(j, us.get(j).getDistance());
				// arr_correspondents.add(Long.parseLong(Integer.toString(us.get(j).getUserId())));

				arr_uname.add(j, us.get(j).getUserName());
				arr_fname.add(j, displayGridCellName(us.get(j).getFName(), us.get(j).getUserName()));
				arr_age.add(j, us.get(j).getAge());
				arr_gender.add(j, us.get(j).getGender());
				arr_looking_type.add(j, us.get(j).getLookingType());
				arr_about.add(j, us.get(j).getAboutMe());
				arr_email.add(j, us.get(j).getEmail());
				arr_status.add(j, us.get(j).getStatus());
			}
		}
		adapter.notifyDataSetChanged();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	private String displayGridCellName(String fname, String user) {

		if (fname.equals("")) {
			return user;
		} else {
			return fname;
		}
	}

	private String displayAge(String age) {
		if (age.length() < 4) {
			return age;
		} else {
			return "";
		}
	}

	private void SendLocToServer() {
		String tag_string_req = "getgeo";
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_GETGEO, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e(TAG, "SAVE GEO Response: " + response.toString());

				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if (!error) {
						// User successfully stored in MySQL
						// Now store the user in sqlite
						GetNearbyUsers();
						Log.e("JSON", "User geo stored on mySql");
						// finish();
					} else {

						// Error occurred in registration. Get the error
						// message
						Log.e("JSON", "Error occurred in registration");
						String errorMsg = jObj.getString("error_msg");

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Error: " + error.getMessage());
				makeNotify("Cannot connect to server", AppMsg.STYLE_ALERT);
				// Toast.makeText(getApplicationContext(),error.getMessage(),
				// Toast.LENGTH_LONG).show();
				mSwipeRefreshLayout.setRefreshing(false);
				// hideDialog();
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();

				ins_user = db.getLoggedInID();
				ins_latitude = latitude;
				ins_longitude = longitude;
				String ins_g_provider = gpsProvider;
				Date dateNow = new Date();

				String curDate = du.convertDateToString(dateNow);

				params.put("tag", "getgeo");
				params.put("p_user", ins_user);
				params.put("p_longitude", ins_longitude + "");
				params.put("p_latitude", ins_latitude + "");
				params.put("p_gps_provider", ins_g_provider);
				params.put("p_date_update", curDate);
				// params.put("$user_id", longitude +"");
				// params.put("latitude", latitude +"");

				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}

	private void GetNearbyUsers() {
		// Tag used to cancel the request
		String tag_string_req = "collect";
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_NEARBY, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e(TAG, "GET GEO Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if (!error) {
						// User successfully stored in MySQL
						// Now store the user in sqlite
						Log.e("JSON", "GEO COLLECTED");
						// finish();

						nearby_users = jObj.getJSONArray("geo");

						existingUsers = db.getExistingOnDBUsers();

						// JSONObject json = new JSONObject(jsonString);
						// JSONArray jArray = jObj.getJSONArray("geo");

						Log.e("LOG", "*****JARRAY*****" + nearby_users.length());

						///////////////////////
						if (nearby_users.length() == 0) {
							mSwipeRefreshLayout.setRefreshing(false);
						} else {
							for (int i = 0; i < nearby_users.length(); i++) {
								JSONObject c = nearby_users.getJSONObject(i);

								// Not sure what will be the effect,
								// if I remove the other variables,
								// so I will just initialized it with default
								// value
								// -ldonios

								// Storing each json item in variable
								String id = c.getString(TAG_GEO_PID);

								String lname = ""/*
													 * c.getString(
													 * TAG_GEO_LNAME)
													 */;

								String status = ""/*
													 * c.getString(
													 * TAG_GEO_STATUS)
													 */;
								String about_me = ""/*
													 * c.getString(
													 * TAG_GEO_ABOUTME)
													 */;
								String looking_type = ""/*
														 * c.getString(
														 * TAG_GEO_LOOKING_TYPE)
														 */;
								String email_address = ""/*
															 * c.getString(
															 * TAG_GEO_EMAIL)
															 */;

								Date bday = new Date()/*
														 * du.
														 * convertStringToDateToLocal
														 * (c.getString(
														 * TAG_GEO_BIRTHDAY))
														 */;
								String age = ""/* du.getAge(bday) */;
								int distance = Math.round(Float.parseFloat(c.getString(TAG_GEO_DISTANCE)));
								String sex = ""/* c.getString(TAG_GEO_GENDER) */;

								// user profile
								String userId = c.getString("user_id");
								String uname = c.getString(TAG_GEO_USER);
								String fname = uname;
								String description = c.getString("description");
								String title = c.getString("title");
								String url0 = c.getString("url0");
								String url1 = c.getString("url1");
								String url2 = c.getString("url2");
								String dateUpdated = c.getString("date_updated");

								// save profile of specific users
								UserProfile userProfile = new UserProfile(Long.parseLong(userId), uname, description,
										title, url0, url1, url2, dateUpdated, true);

								userProfile.updateOffline(AroundMeActivity.this);

								// replace geo id with userid
								id = userId;
								// profile pic info
								String imgDir = c.getString("img_dir");
								String imgFile = c.getString("img_file");
								String dateCreated = c.getString("date_uploaded");

								// L.debug("getting profile picture of userId:
								// "+userId+", imgDir
								// "+imgDir.equalsIgnoreCase("null")+", imgFile
								// "+imgFile);
								if ((imgDir != null && !imgDir.isEmpty() && !imgDir.equalsIgnoreCase("null"))
										&& (imgFile != null && !imgFile.isEmpty()
												&& !imgFile.equalsIgnoreCase("null"))) {
									L.error("getting profile picture of userId: " + userId + ", uname: " + uname
											+ ", imgDir: " + imgDir + ", imgFile: " + imgFile);
									ProfilePicture profilePic = new ProfilePicture(Long.parseLong(userId), imgDir,
											imgFile, dateCreated, true);
									profilePic.saveOffline(AroundMeActivity.this);
								}

								boolean containerContainsContent = org.apache.commons.lang.StringUtils
										.containsIgnoreCase(existingUsers, "." + id + ".");
								if (containerContainsContent == true) {
									if (distance <= dst) {
										db.updateUser(id, uname, distance, fname, lname, age, sex, "",
												"2012-12-12 09:09:09", 1, about_me, looking_type, status, email_address,
												"1");
									} else {
										db.updateUser(id, uname, distance, fname, lname, age, sex, "",
												"2012-12-12 09:09:09", 0, about_me, looking_type, status, email_address,
												"0");
									}
								} else {
									if (distance <= dst) {

										db.insertNearbyUser(id, uname, distance, fname, lname, age, sex, "",
												"2012-12-12 09:09:09", 0, about_me, looking_type, status, email_address,
												"1");
									}
								}

								if (i == nearby_users.length() - 1) {
									updateGrid("1");
								}

							}
						}
					} else {
						makeNotify("Error occurred while collecting users", AppMsg.STYLE_ALERT);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Error: " + error.getMessage());
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				// hideDialog();
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();

				ins_latitude = latitude;
				ins_longitude = longitude;

				params.put("tag", "collect");
				params.put("pid", ins_user);
				params.put("longitude", ins_longitude + "");
				params.put("latitude", +ins_latitude + "");

				// Log.e("MAP", ins_user + " + "+ins_longitude+" :
				// "+ins_latitude);
				// params.put("latitude", latitude +"");

				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {

		super.onResume();
		isRunning = true;

		dst = 100;
		try {
			dst = Integer.parseInt(db.getBroadcastDist());
		} catch (Exception e) {
			dst = 100;
		}

		if (oldDst != dst) {
			// force grid update when new distance detected
			tryGridToUpdate();
			oldDst = dst;
		}
		adapter.notifyDataSetChanged();

	}

	private void tryGridToUpdate() {
		mSwipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
				getNewLoc();
			}
		}, 2000);
	}

	protected void onPause() {
		super.onPause();
		// locationManager.removeUpdates(locationListener);
		isRunning = false;
		Log.e("ON PAUSE", "APP PAUSE");

	}

	@Override
	public void onCorrespondentUpdate() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});

	}
}