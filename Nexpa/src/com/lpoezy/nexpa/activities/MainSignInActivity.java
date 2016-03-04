package com.lpoezy.nexpa.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.chatservice.LocalBinder;
import com.lpoezy.nexpa.chatservice.XMPPService;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.Correspondents;
import com.lpoezy.nexpa.objects.Messages;
import com.lpoezy.nexpa.objects.NewMessage;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
	protected boolean mBounded;
	protected XMPPService mService;

	private ServiceConnection mServiceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBounded = false;
			mService = null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBounded = true;

			mService = ((LocalBinder<XMPPService>) service).getService();
		}
	};

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

		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String username = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				if (username.trim().length() > 0 && password.trim().length() > 0) {

					if (mBounded) {

						checkLogin(username, password);

					} else {

						L.error("service not yet available");
					}

				} else {
					L.makeText(MainSignInActivity.this, "Please enter your username and password.", AppMsg.STYLE_ALERT);
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
				// finish();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		Intent service = new Intent(this, XMPPService.class);
		startService(service);

		bindService(service, mServiceConn, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mServiceConn != null) {
			unbindService(mServiceConn);
		}
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

	private void updateChatHistory() {
		L.debug("MainSignActivity, updateChatHistory");
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "download_all_msgs");// download_profile_and_pic_info
		postDataParams.put("username", server_name);
		L.debug("server_name: " + server_name);

		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("webPage" + webPage);

		JSONObject result;
		try {
			result = new JSONObject(webPage);
			Correspondents correspondents = new Correspondents();
			Messages comments = new Messages();
			boolean error = result.getBoolean("error");
			if (!error) {

				JSONArray msgs = result.getJSONArray("messages");

				for (int i = 0; i < msgs.length(); i++) {
					JSONObject msg = msgs.getJSONObject(i);

					String senderName = msg.getString("sender_name");
					String receiverName = msg.getString("receiver_name");
					String body = msg.getString("body");
					boolean isLeft = !senderName.equals(server_name) ? true : false;
					String correspondentName = isLeft ? senderName : receiverName;

					correspondents.add(new Correspondent(correspondentName));

					boolean isSuccessful = true;
					boolean isUnread = false;
					boolean isSyncedOnline = true;
					long date = Long.parseLong(msg.getString("date"));
					NewMessage comment = new NewMessage(senderName, receiverName, body, isLeft, isSuccessful, isUnread,
							isSyncedOnline, date);
					comments.add(comment);
				}

			}

			correspondents.saveOffline(MainSignInActivity.this);

			comments.saveOffline(MainSignInActivity.this);

		} catch (JSONException e) {
			L.error("" + e);
		}

	}

	private void updateUserProfile() {
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

				profilePictureJson = jResult.getJSONArray("profile_and_pic_info").getJSONObject(0);

				description = profilePictureJson.getString("description");
				profession = profilePictureJson.getString("title");
				url0 = profilePictureJson.getString("url0");
				url1 = profilePictureJson.getString("url1");
				url2 = profilePictureJson.getString("url2");
				dateUpdated = profilePictureJson.getString("date_updated");

				String imgDir = profilePictureJson.getString("img_dir");
				String imgFile = profilePictureJson.getString("img_file");
				String dateUploaded = profilePictureJson.getString("date_uploaded");

				ProfilePicture profilePicture = new ProfilePicture(uId, imgDir, imgFile, dateUploaded, true);
				profilePicture.downloadImageOnline();
				profilePicture.saveOffline(MainSignInActivity.this);
			}

		} catch (JSONException e) {
			L.error("" + e);
		}

		UserProfile profile = new UserProfile(uId, uname, description, profession, url0, url1, url2, dateUpdated, true);
		profile.updateOffline(MainSignInActivity.this);

		L.debug("MainSignInActivity, server_uid: " + server_uid + ", server_name: " + server_name + ", webPage: "
				+ webPage);
	}

	private void checkLogin(final String uname, final String password) {

		final String tag_string_req = "login";
		pDialog.setMessage("Logging in ...");
		showDialog();

		mService.login(uname, password, new XMPPService.OnUpdateScreenListener() {

			@Override
			public void onUpdateScreen() {

				hideDialog();
			}

			@Override
			public void onResumeScreen(String errorMsg) {

				hideDialog();

				L.makeText(MainSignInActivity.this, errorMsg, AppMsg.STYLE_ALERT);
			}
		});

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

		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				if (pDialog.isShowing())
					pDialog.dismiss();

			}
		});

	}
}