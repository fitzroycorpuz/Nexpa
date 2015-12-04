package com.lpoezy.nexpa.activities;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.sqlite.SessionManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity {
    // LogCat tag
    private static final String TAG = CreateAccountActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
 
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
 
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
 
        // Session manager
        session = new SessionManager(getApplicationContext());
 
        // Check if user is already logged in or not
//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(LogInActivity.this, ProfileActivity.class);
//            startActivity(intent);
//            finish();
//        }
 
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
            	
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
 
                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                	
                    //checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
 
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        CreateAccountActivity.class);
                startActivity(i);
                finish();
            }
        });
 
    }
 
    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
    	
    	String tag_string_req = "login";

        pDialog.setMessage("Logging in ...");
        
        showDialog();
        
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
        	
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Login Response: " + response.toString());
                        hideDialog();
 
                        try {
                        	
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
 
                            // Check for error node in json
                            if (!error) {
                                // user successfully logged in
                                // Create login session
                                session.setLogin(true);
 
                                // Launch main activity
                                Intent intent = new Intent(LogInActivity.this,
                                		ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error in login. Get the error message
                            	Log.e("Error in login","Error in login");
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
 
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    	Log.e("","break3");
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {
 
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
            	//Log.e("","break4");
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
 
                return params;
            }
 
        };
        
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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