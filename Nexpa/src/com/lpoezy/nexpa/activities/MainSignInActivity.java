package com.lpoezy.nexpa.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.Correspondents;
import com.lpoezy.nexpa.objects.Messages;
import com.lpoezy.nexpa.objects.NewMessage;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.openfire.OnXMPPConnectedListener;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.NiceDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainSignInActivity extends Activity {
	private static final String TAG = MainSignInActivity.class.getSimpleName();
	private Button btnLogin;
	private Button btnLinkToRegister;
	private Button btnLinkToForgotPassword;
	private EditText inputEmail;
	private EditText inputPassword;
	private ProgressDialog pDialog;
	private SessionManager session;
	// private LoginButton loginBtn;
	private TextView username;
	// private UiLifecycleHelper uiHelper;
	private SQLiteHandler db;
	Bitmap proPic;
	private ImageView imgProfile;
	private ImageView imgRotator;
	private ImageView imgRotatorB;
	private ImageView imgRotatorDark;
	private ImageView imgRotatorDarkB;
	private Animation animScrollLeftSlow;
	private Animation animScrollLeftSlowB;
	private Animation animScrollLeft;
	private Animation animScrollLeftB;
	Timer timer;

	String server_uid;
	String server_name;
	String server_email;
	String server_created_at;
	String public_pass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_sign_in);
		imgRotator = (ImageView) findViewById(R.id.rotator_disp);
		imgRotatorB = (ImageView) findViewById(R.id.rotator_disp_b);
		imgRotatorDark = (ImageView) findViewById(R.id.rotator_disp_2);
		imgRotatorDarkB = (ImageView) findViewById(R.id.rotator_disp_2_b);
		animScrollLeftSlow = AnimationUtils.loadAnimation(this, R.anim.anim_scroll_slow);
		animScrollLeft = AnimationUtils.loadAnimation(this, R.anim.anim_scroll);
		animScrollLeftB = AnimationUtils.loadAnimation(this, R.anim.anim_scroll_b);
		animScrollLeftSlowB = AnimationUtils.loadAnimation(this, R.anim.anim_scroll_slow_b);
		imgRotator.startAnimation(animScrollLeft);
		imgRotatorB.startAnimation(animScrollLeftB);
		imgRotatorDark.startAnimation(animScrollLeftSlow);
		imgRotatorDarkB.startAnimation(animScrollLeftSlowB);
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		btnLinkToForgotPassword = (Button) findViewById(R.id.btnLinkToForgotPassword);
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		session = new SessionManager(getApplicationContext());
		db = new SQLiteHandler(this);
		db.openToWrite();
		final CreateAccountActivity cAct;

		if (session.isLoggedIn()) {
			// Intent intent = new Intent(MainSignInActivity.this,
			// ProfileActivity.class);
			// startActivity(intent);
			// finish();
		} else {
			/*
			 * uiHelper = new UiLifecycleHelper(this, statusCallback);
			 * uiHelper.onCreate(savedInstanceState); username = (TextView)
			 * findViewById(R.id.username); loginBtn = (LoginButton)
			 * findViewById(R.id.fb_login_button);
			 * loginBtn.setReadPermissions(Arrays.asList("email",
			 * "public_profile", "user_birthday"));
			 * loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback()
			 * {@Override public void onUserInfoFetched(GraphUser user) { if
			 * (user != null) {
			 * loginBtn.setReadPermissions(Arrays.asList("email",
			 * "public_profile", "user_birthday")); if
			 * (hasFacebookPermissions(Arrays.asList("email", "public_profile",
			 * "user_birthday"))) { session.setLogin(true); String gender =
			 * (String) user.getProperty("gender"); String email = (String)
			 * user.getProperty("email"); URL image_value; } else {
			 * Log.e("xxxxxxxxxxxxxxz", user.getFirstName()); Session sess =
			 * Session.getActiveSession(); sess.closeAndClearTokenInformation();
			 * } } else { Log.e("DEVICE X", "1"); } } });
			 */

			btnLogin.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					String email = inputEmail.getText().toString();
					String password = inputPassword.getText().toString();
					if (email.trim().length() > 0 && password.trim().length() > 0) {
						checkLogin(email, password);
					} else {
						makeNotify("Please enter your username and password", AppMsg.STYLE_INFO);
					}
				}
			});
			btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
					startActivity(i);
					finish();
				}
			});
			
			btnLinkToForgotPassword.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
					startActivity(i);
					finish();
				}
			});
		}
	}

	/*
	 * private void registerFacebookUser(final String name, final String email,
	 * final String password, final String fname, final String lname, final
	 * String bday, final String gender) { String tag_string_req = "register";
	 * pDialog.setMessage("Updating"); showDialog(); StringRequest strReq = new
	 * StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener
	 * < String > () {@Override public void onResponse(String response) {
	 * Log.e(TAG, "Register Response: " + response.toString()); try { JSONObject
	 * jObj = new JSONObject(response); boolean error =
	 * jObj.getBoolean("error"); if (!error) { hideDialog(); String uid =
	 * jObj.getString("uid"); JSONObject user = jObj.getJSONObject("user");
	 * String name = user.getString("name"); String email =
	 * user.getString("email"); String created_at =
	 * user.getString("created_at"); db.addUser(name, email, uid, created_at,
	 * password); session.setLogin(true); Account ac = new Account();
	 * ac.LogInChatAccount(name, password, email); Intent intent = new
	 * Intent(MainSignInActivity.this, TabHostActivity.class);
	 * startActivity(intent); finish(); } else { String errorMsg =
	 * jObj.getString("error_msg"); Toast.makeText(getApplicationContext(),
	 * "ERROR " + errorMsg, Toast.LENGTH_LONG).show(); } } catch (JSONException
	 * e) { makeNotify("Server Failed To Respond", AppMsg.STYLE_ALERT);
	 * hideDialog(); } } }, new Response.ErrorListener() {@Override public void
	 * onErrorResponse(VolleyError error) { Log.e(TAG, "Registration Error: " +
	 * error.getMessage()); Toast.makeText(getApplicationContext(),
	 * error.getMessage(), Toast.LENGTH_LONG).show(); hideDialog(); } })
	 * {@Override protected Map < String, String > getParams() { Map < String,
	 * String > params = new HashMap < String, String > (); params.put("tag",
	 * "register"); params.put("name", name); params.put("email", email);
	 * params.put("password", password); params.put("user_type", "2");
	 * params.put("prof_fname", fname); params.put("prof_lname", lname);
	 * params.put("prof_bday", bday); params.put("prof_gender", gender); return
	 * params; } }; AppController.getInstance().addToRequestQueue(strReq,
	 * tag_string_req); }
	 * 
	 * public static boolean hasFacebookPermissions(List < String > permissions)
	 * { Session activeSession = Session.getActiveSession(); return
	 * activeSession != null && activeSession.isOpened() &&
	 * activeSession.getPermissions().containsAll(permissions); }
	 * 
	 * private Session.StatusCallback statusCallback = new
	 * Session.StatusCallback() {@Override public void call(Session session,
	 * SessionState state, Exception exception) { if (state.isOpened()) {
	 * Log.e("MainActivity", "Facebook session opened."); } else if
	 * (state.isClosed()) { Log.e("MainActivity", "Facebook session closed."); }
	 * } };
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		// uiHelper.onSaveInstanceState(savedState);
	}

	private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
	
	private void updateChatHistory(){
		L.debug("MainSignActivity, updateChatHistory");
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "download_all_msgs");// download_profile_and_pic_info
		postDataParams.put("username", server_name);
		L.debug("server_name: "+server_name);
		
		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("webPage"+webPage);
		
		JSONObject result;
		try {
			result = new JSONObject(webPage);
			Correspondents correspondents = new Correspondents();
			Messages comments = new Messages();
			boolean error = result.getBoolean("error");
			if(!error){
				
				JSONArray msgs = result.getJSONArray("messages");
				
				for(int i=0;i<msgs.length();i++){
					JSONObject msg = msgs.getJSONObject(i);
					
					String senderName = msg.getString("sender_name");
					String receiverName= msg.getString("receiver_name");
					String body= msg.getString("body");
					boolean isLeft = !senderName.equals(server_name)?true:false;
					String correspondentName = isLeft?senderName:receiverName;
					
					correspondents.add(new Correspondent(correspondentName));
					
					
					boolean isSuccessful = true;
					boolean isUnread = false;
					boolean isSyncedOnline = true;
					long date= Long.parseLong(msg.getString("date"));
					NewMessage comment = new NewMessage(senderName, receiverName, body, isLeft, isSuccessful, isUnread, isSyncedOnline, date);
					comments.add(comment);
				}
				
			}
			
			correspondents.saveOffline(MainSignInActivity.this);
			
			comments.saveOffline(MainSignInActivity.this);
			
		} catch (JSONException e) {
			L.error(""+e);
		}
		
	}
	
	private void updateUserProfile(){
		L.debug("MainSignActivity, updateUserProfile");
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "download_profile_and_pic_info");// download_profile_and_pic_info
		postDataParams.put("user_id", server_uid);

		final String spec = AppConfig.URL_PROFILE_PIC;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		JSONObject jResult;
		// reusing userid
		// and username
		long uId = Long.parseLong(server_uid);
		String uname = server_name;
		String description = "";
		String profession = "";
		String url0 = "";
		String url1 = "";
		String url2 = "";
		String dateUpdated = "";
	
		try {
			jResult = new JSONObject(webPage);

			JSONObject profilePictureJson;
			if (!jResult.getBoolean("error")) {

				profilePictureJson = jResult
						.getJSONArray("profile_and_pic_info")
						.getJSONObject(0);

				
				description = profilePictureJson.getString("description");
				profession = profilePictureJson.getString("title");
				url0 = profilePictureJson.getString("url0");
				url1 = profilePictureJson.getString("url1");
				url2 = profilePictureJson.getString("url2");
				dateUpdated = profilePictureJson.getString("date_updated");

				String imgDir = profilePictureJson.getString("img_dir");
				String imgFile = profilePictureJson.getString("img_file");
				String dateUploaded = profilePictureJson
						.getString("date_uploaded");

				ProfilePicture profilePicture = new ProfilePicture(uId,
						imgDir, imgFile, dateUploaded, true);
				profilePicture.downloadImageOnline();
				profilePicture.saveOffline(MainSignInActivity.this);
			}

		} catch (JSONException e) {
			L.error("" + e);
		}

		UserProfile profile = new UserProfile(uId, uname, description,
					profession, url0, url1, url2, dateUpdated, true);
		profile.updateOffline(MainSignInActivity.this);

		L.debug("MainSignInActivity, server_uid: " + server_uid
				+ ", server_name: " + server_name + ", webPage: "
				+ webPage);
	}

	private void checkLogin(final String email, final String password) {
		final String tag_string_req = "login";
		pDialog.setMessage("Logging in ...");

		showDialog();

		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.e(TAG, "Login Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");

					if (!error) {
						
						server_uid = jObj.getString("uid");
						JSONObject user = jObj.getJSONObject("user");
						server_name = user.getString("name");
						server_email = user.getString("email");
						server_created_at = user.getString("created_at");
						String is_activated = user.getString("isActive");

						if (is_activated.equals("1")) {

							db.addUser(server_name, server_email, server_uid, server_created_at, password);
							session.setLogin(true);
							
							
							Account ac = new Account();
							
							ac.LogInChatAccount(server_name, password, server_email, new OnXMPPConnectedListener() {

								@Override
								public void onXMPPConnected(final XMPPConnection connection) {

									// download user profile pic info
									ExecutorService exec = Executors.newCachedThreadPool();
									exec.submit(new Runnable() {

										@Override
										public void run() {
											
											updateChatHistory();
											
											updateUserProfile();
											
											
											btnLogin.post(new Runnable() {

												@Override
												public void run() {
													
													
													
													//Intent intent = new Intent(MainSignInActivity.this, BuddyRequestActivity.class);
													

													Intent intent = new Intent(MainSignInActivity.this,TabHostActivity.class);
													startActivity(intent);

													finish();

													hideDialog();
												}
											});
											
										}
									});
									exec.shutdown();
									try {
										exec.awaitTermination(1, TimeUnit.HOURS);
									} catch (InterruptedException e) {
										L.error("" + e);
									}

								}

							});

						} else {
							
							Account ac = new Account();
							ac.ReTryCreateChatAccount(MainSignInActivity.this, server_name, password, server_email);
							public_pass = password;
							timer = new Timer();
							initializeTimerTask();
							timer.scheduleAtFixedRate(showMainPageIntent, 1000, 3000);
							
						}
					} else {
						hideDialog();
						String errorMsg = jObj.getString("error_msg");
						makeNotify("ERROR: " + errorMsg, AppMsg.STYLE_ALERT);
					}
				} catch (JSONException e) {
					hideDialog();
					makeNotify("Cannot reach data on server.", AppMsg.STYLE_ALERT);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				Log.e(TAG, "" + error);
				makeNotify("Cannot connect to server", AppMsg.STYLE_ALERT);
				hideDialog();
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "login");
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};

		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	TimerTask showMainPageIntent;
	int loader = 10;
	final Handler handler = new Handler();

	public void initializeTimerTask() {
		showMainPageIntent = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						Log.e(TAG, "Broadcast Timer ticking...");
						loader--;
						if (db.checkAccountValidate().equals("1")) {
							hideDialog();
							db.addUser(server_name, server_email, server_uid, server_created_at, public_pass);
							public_pass = "";
							session.setLogin(true);
							
							
							
							//Intent intent = new Intent(MainSignInActivity.this, BuddyRequestActivity.class);
							Intent intent = new Intent(MainSignInActivity.this, TabHostActivity.class);
							startActivity(intent);
							finish();
							timer.cancel();
						} else {
							
							if (loader < 0) {
								hideDialog();
								NiceDialog.promptDialog("Failed to sync on server",
										"Unable to sync on server. \nPlease try again.", MainSignInActivity.this,
										"error");
								session.setLogin(false);
								timer.cancel();
							}
						}
					}
				});
			}
		};
	}

	private void saveBitmap(Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
		File f = new File(getCacheDir() + File.separator + "test.jpg");
		Log.e("", getCacheDir() + " bb");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fo.write(bytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}