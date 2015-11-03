package com.lpoezy.nexpa.JSON;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;

public class Profile {
	
	boolean succession;
	SQLiteHandler db;
	
    public void updateProfileOnServer(Context cn) {
    	db = new SQLiteHandler(cn);
  		db.openToWrite();
    	if ((db.getServerUpdatePref().equals("1"))||(db.getServerUpdatePref().equals("null"))){
    	succession = false;
        // Tag used to cancel the request
        final String tag_string_req = "profile_pref";
 
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.e("Update Profile", "Updating Response: " + response.toString());
                      
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                            	// Inserting row in users table
                                db.updateUploadStatus(tag_string_req);
                                succession = true;
                            } 
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Updating Profile", "Updating Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("tag", tag_string_req);
                params.put("user", db.getLoggedInID());
                params.put("looking_status", db.getLookingForStatus());
                params.put("sex_orien",db.getSexOrien());
                params.put("looking_gender", db.getLookingGender());
                params.put("rel_status", db.getRelStatus());
                params.put("date_update", db.getLastUpdate());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }
    
    public void updateBasicOnServer(Context cn) {
    	db = new SQLiteHandler(cn);
  		db.openToWrite();
    	if ((db.getServerUpdateBasic().equals("1"))||(db.getServerUpdateBasic().equals("null"))){
    	succession = false;
        // Tag used to cancel the request
        final String tag_string_req = "profile_basic";
 
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.e("Update Profile", "Updating Response: " + response.toString());
                      
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                            	// Inserting row in users table
                                db.updateUploadStatus(tag_string_req);
                                succession = true;
                            } 
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Updating Profile", "Updating Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("tag", tag_string_req);
                params.put("user", db.getLoggedInID());
                params.put("fname", db.getName());
                params.put("age", db.getBDate());
                params.put("gender", db.getGender());
                params.put("date_update", db.getLastUpdate());
                
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }
    
    public void updatePasswordOnServer(Context cn) {
    	db = new SQLiteHandler(cn);
  		db.openToWrite();
    	if ((db.getServerUpdatePass().equals("1"))||(db.getServerUpdatePass().equals("null"))){
    	succession = false;
        // Tag used to cancel the request
        final String tag_string_req = "profile_pass";
 
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.e("Update Profile Pass", "Update Pass Response: " + response.toString());
                      
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                            	// Inserting row in users table
                                db.updateUploadStatus(tag_string_req);
                                succession = true;
                            } 
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Updating Profile Pass", "Update Pass Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("user", db.getLoggedInID());
                params.put("password", db.getPass());
                
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }
    
    public void updateStatOnServer(Context cn) {
    	db = new SQLiteHandler(cn);
  		db.openToWrite();
    	if ((db.getServerUpdateStatMess().equals("1"))||(db.getServerUpdateStatMess().equals("null"))){
        // Tag used to cancel the request
        final String tag_string_req = "profile_stat";
 
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {
 
                    @Override
                    public void onResponse(String response) {
                        Log.e("Update Profile Stat", "Update Pass Response: " + response.toString());
                      
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                            	// Inserting row in users table
                                db.updateUploadStatus(tag_string_req);
                                succession = true;
                            } 
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
 
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Updating Profile Stat", "Update Pass Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                
                params.put("tag", tag_string_req);
                params.put("user", db.getLoggedInID());
                params.put("stat", db.getStatusMessage());
                params.put("date_update", db.getLastUpdate());
                
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }
}
