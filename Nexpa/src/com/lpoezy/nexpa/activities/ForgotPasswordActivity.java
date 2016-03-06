package com.lpoezy.nexpa.activities;

import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.chatservice.LocalBinder;
import com.lpoezy.nexpa.chatservice.XMPPService;
import com.lpoezy.nexpa.chatservice.XMPPService.OnUpdateScreenListener;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity {
	private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
	private Button btnResetPassword;
	private EditText inputEmail;

	Timer timer;
	Account ac;
	private ProgressDialog pDialog;

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
		setContentView(R.layout.activity_forgot_password);
		inputEmail = (EditText) findViewById(R.id.email);
		btnResetPassword = (Button) findViewById(R.id.btnResetPassword);

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		btnResetPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				String email = inputEmail.getText().toString();
				if (email.isEmpty()) {
					L.makeText(ForgotPasswordActivity.this, "Please fill out your registered email.",
							AppMsg.STYLE_ALERT);
				} else if (isEmailValid(email) == false) {
					L.makeText(ForgotPasswordActivity.this, "Email address is not valid.", AppMsg.STYLE_ALERT);
				} else {
					if (mBounded) {
						resetPassword(email);
					} else {
						L.error("service not yet available");
					}

				}

			}
		});

	}

	protected void resetPassword(final String email) {

		String msg = getResources().getString(R.string.dialog_msg_sending_email);
		pDialog.setMessage(msg);
		showDialog();

		mService.resetPassword(email, new OnUpdateScreenListener() {
			
			@Override
			public void onUpdateScreen() {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						
						hideDialog();
						
					}
				});
				
				String res = getResources().getString(R.string.msg_request_to_reset_password_success);
				L.makeText(ForgotPasswordActivity.this, res , AppMsg.STYLE_INFO);
				
			}
			
			@Override
			public void onResumeScreen(String errorMsg) {	}
		});

	}

	@Override
	protected void onStop() {

		super.onStop();

		if (mServiceConn != null) {
			unbindService(mServiceConn);
		}

	}

	@Override
	protected void onResume() {

		super.onResume();

		Intent service = new Intent(this, XMPPService.class);
		bindService(service, mServiceConn, Context.BIND_AUTO_CREATE);
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

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}