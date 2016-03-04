package com.lpoezy.nexpa.activities;

import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devspark.appmsg.AppMsg;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.chatservice.LocalBinder;
import com.lpoezy.nexpa.chatservice.XMPPService;
import com.lpoezy.nexpa.chatservice.XMPPService.OnUpdateScreenListener;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends Activity {
	private static final String TAG = CreateAccountActivity.class.getSimpleName();
	private Button btnRegister;
	private Button btnLinkToLogin;
	private EditText inputFullName;
	private EditText inputEmail;
	private EditText inputPassword;
	private ProgressDialog pDialog;
	private SessionManager session;
	private SQLiteHandler db;

	Timer timer;
	Account ac;

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
		setContentView(R.layout.activity_create_account);
		inputFullName = (EditText) findViewById(R.id.name);
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		session = new SessionManager(getApplicationContext());
		db = new SQLiteHandler(this);
		db.openToWrite();
		ac = new Account();

		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();

				if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

					L.makeText(CreateAccountActivity.this, "Please fill out all fields.", AppMsg.STYLE_ALERT);

				} else if (isEmailValid(email) == false) {

					L.makeText(CreateAccountActivity.this, "Email address is not valid.", AppMsg.STYLE_ALERT);

				} else {

					if (mBounded) {

						registerUser(name, email, password);

					} else {
						L.error("service not yet available");
					}

				}
			}
		});

		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), MainSignInActivity.class);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	protected void onResume() {

		super.onResume();

		Intent service = new Intent(this, XMPPService.class);
		bindService(service, mServiceConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {

		super.onPause();

		if (mServiceConn != null) {
			unbindService(mServiceConn);
		}
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	boolean succession;

	private void registerUser(final String uname, final String email, final String password) {

		String tag_string_req = "register";
		pDialog.setMessage("Registering ...");
		showDialog();

		mService.register(uname, email, password, new OnUpdateScreenListener() {

			@Override
			public void onUpdateScreen() {

				hideDialog();
			}

			@Override
			public void onResumeScreen(String errorMsg) {

				hideDialog();

				L.makeText(CreateAccountActivity.this, errorMsg, AppMsg.STYLE_ALERT);

			}
		});

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