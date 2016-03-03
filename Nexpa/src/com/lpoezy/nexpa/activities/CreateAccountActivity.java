package com.lpoezy.nexpa.activities;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
		
//		if (session.isLoggedIn()) {
//			Intent intent = new Intent(CreateAccountActivity.this, ProfileActivity.class);
//			startActivity(intent);
//			finish();
//		}
		
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_LONG).show();
				} else if (isEmailValid(email) == false) {
					Toast.makeText(getApplicationContext(), "Email address is not valid", Toast.LENGTH_LONG).show();
				} else {
					if (registerUser(name, email, password) == true) {
						Log.e("ABLE", "reg chat");
					} else {
						Log.e("ENABLABLE", "reg chat");
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
	
	private boolean registerUser(final String name, final String email, final String password) {
		succession = false;
		String tag_string_req = "register";
		pDialog.setMessage("Registering ...");
		showDialog();
		
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener < String > () {@Override
			
			public void onResponse(String response) {
				Log.e(TAG, "Register Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if (!error) {
						/*/
						ac.CreateChatAccount(CreateAccountActivity.this, name, password, email);
						String uid = jObj.getString("id");
						JSONObject user = jObj.getJSONObject("user");
						String name = user.getString("name");
						String email = user.getString("email");
						String created_at = user.getString("created_at");
						db.addUser(name, email, uid, created_at, password);
						succession = true;
						session.setLogin(true);
						timer = new Timer();
						initializeTimerTask();
						timer.scheduleAtFixedRate(showMainPageIntent, 1000, 2000);
						//*/
					} else {
						String errorMsg = jObj.getString("error_msg");
						makeNotify(errorMsg, AppMsg.STYLE_ALERT);
						//Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
						hideDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Registration Error: " + error.getMessage());
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
				hideDialog();
			}
		}) {@Override
			protected Map < String, String > getParams() {
				Map < String, String > params = new HashMap < String, String > ();
				params.put("tag", "register");
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				params.put("user_type", "1");
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
		return succession;
	}

	TimerTask showMainPageIntent;
	final Handler handler = new Handler();
	int load_timeout = 10;
	public void initializeTimerTask() {
		showMainPageIntent = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						Log.e(TAG, "Timer ticking...");
						load_timeout--;
						if (load_timeout < 1){
							hideDialog();
							session.setLogin(false);
					        db.deleteUsers();
					        promptOkWaitDialog("Failed to sync on server","Toucan was able to register your account but wasnt able to sync data. \n\nPlease try to log-in later.",CreateAccountActivity.this,"warning"); 
							timer.cancel();
							
						}
						else{
								if (db.checkAccountValidate().equals("1")) {
								hideDialog();
								Intent intent = new Intent(CreateAccountActivity.this, TabHostActivity.class);
								startActivity(intent);
								finish();
								timer.cancel();
							}
						}
					}
				});
			}
		};
	}
	
	private static Dialog dialogStatusYN;
	    static LinearLayout lnHeader;
	    static TextView edtStatusHead;
	    static TextView edtStatus1;
	    private void promptOkWaitDialog(String caption, String message, Context cn, final String fcType){
	    	
	    	dialogStatusYN = new Dialog(cn);
	    	dialogStatusYN.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    	dialogStatusYN.setContentView(R.layout.dialog_yesno);
	        Button dialogButton = (Button) dialogStatusYN.findViewById(R.id.dialogButtonYes);
	        Button dialogButtonNo = (Button) dialogStatusYN.findViewById(R.id.dialogButtonNo);
	        lnHeader = (LinearLayout) dialogStatusYN.findViewById(R.id.lnHeader);
	        edtStatusHead = (TextView) dialogStatusYN.findViewById(R.id.edtStatusHead);
	        edtStatus1 = (TextView) dialogStatusYN.findViewById(R.id.edtStatus);
	        
	        edtStatusHead.setText(caption);
	        edtStatus1.setText(message);
	        
	        dialogButton.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
	        dialogButtonNo.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
	        dialogButton.setVisibility(View.INVISIBLE);
	       	lnHeader.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
	       	dialogButtonNo.setText("OK");
	       	dialogButtonNo.setOnClickListener(new OnClickListener()
	        {	
	        	@Override
	            public void onClick(View v){	
	        			Intent intent = new Intent(CreateAccountActivity.this, MainSignInActivity.class);
						startActivity(intent);
						finish();
	            }
	        });

	        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	        lp.copyFrom(dialogStatusYN.getWindow().getAttributes());
	        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	        dialogStatusYN.show();
	        dialogStatusYN.getWindow().setAttributes(lp);
	    }
	    
	private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
	
	private void showDialog() {
		if (!pDialog.isShowing()) pDialog.show();
	}
	
	private void hideDialog() {
		if (pDialog.isShowing()) pDialog.dismiss();
	}
}