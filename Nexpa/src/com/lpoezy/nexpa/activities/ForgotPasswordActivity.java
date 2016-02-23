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
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.os.Bundle;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		inputEmail = (EditText) findViewById(R.id.email);
		btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
		
		btnResetPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				final String email = inputEmail.getText().toString();
				if (email.isEmpty()) {
					makeNotify("Please fill out your registered email", AppMsg.STYLE_ALERT);
					
				} else if (isEmailValid(email) == false) {
					makeNotify("Email address is not valid", AppMsg.STYLE_ALERT);
				} else {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							
							HashMap<String, String> postDataParams = new HashMap<String, String>();
							postDataParams.put("tag", "reset_password");
							postDataParams.put("email", email);
							

							final String spec = AppConfig.URL_SEND_EMAIL;
							String webPage = HttpUtilz.makeRequest(spec, postDataParams);

							
//							JSONObject result;
//							try {
//								result = new JSONObject(webPage);
//								boolean error = result.getBoolean("error");
//								
//								if(!error){
//									final String msg = result.getString("msg");
//									
//									inputEmail.post(new Runnable() {
//										
//										@Override
//										public void run() {
//											makeNotify(msg, AppMsg.STYLE_INFO);
//											
//										}
//									});
//									
//								}
//								
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							
							
						}
					}).start();
					
					
					inputEmail.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							String msg = getResources().getString(R.string.msg_request_to_reset_password_success);
							makeNotify(msg, AppMsg.STYLE_INFO);
							
						}
					}, 500);
					
					
					
				}
				
				
				
			}
		});
	
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
	
	private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
	
	
}