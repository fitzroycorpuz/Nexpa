package com.lpoezy.nexpa.activities;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.lpoezy.nexpa.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupActivity extends Activity {
EditText username1, password1, email1, fullName1;
Button register;

public static final String HOST = "198.154.106.139";
public static final int PORT = 5222;
public static final String SERVICE = "198.154.106.139";

private String username;
private String name;
private String email_id;
private String password;
private String cmpassword;
private String user;
private String nam;
private String emailid;
private String pass;
private String cmpass;


@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    
    username1 = (EditText) findViewById(R.id.username);
	password1 = (EditText) findViewById(R.id.password);
	email1 = (EditText) findViewById(R.id.email);
	fullName1 = (EditText) findViewById(R.id.fullName);
	register = (Button) findViewById(R.id.register);
	
	  register.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {	
		//	code here

			    getIntentData();
			    connect();

				Toast.makeText(getApplicationContext(),
	                      "Button Clicked",Toast.LENGTH_SHORT).show();	
				
			}

	    });



}

private void getIntentData() {
    // TODO Auto-generated method stub

    Intent getData=getIntent();
    username=getData.getStringExtra("username");
    name=getData.getStringExtra("name");
    email_id=getData.getStringExtra("email_id");
    password=getData.getStringExtra("password");
    cmpassword=getData.getStringExtra("cmpassword");

}

private void connect() {
    // TODO Auto-generated method stub
    final ProgressDialog dialog = ProgressDialog.show(this,"Creating your account...", "Please wait...", false);

    Thread t = new Thread(new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
        	
        	

            ConnectionConfiguration connConfig = new ConnectionConfiguration(
                    HOST, PORT,SERVICE);
            XMPPConnection connection = new XMPPConnection(connConfig);

            try {
                connection.connect();
                Log.i("XMPPChatDemoActivity",
                        "Connected to " + connection.getHost());
                
            } catch (XMPPException ex) 
            {
                Log.e("XMPPChatDemoActivity", "Failed to connect to "
                        + connection.getHost());
                Log.e("XMPPChatDemoActivity", ex.toString());
            //  setConnection(null);
            }

            try{

                //connection.connect();
                connection.login("admin", "openfire");
                Log.i("XMPPChatDemoActivity","Logged in as " + connection.getUser());
                String kk= connection.getUser();
                AccountManager accountManager = connection.getAccountManager();
       
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username1.getText().toString());
                map.put("name", fullName1.getText().toString());
                map.put("password", password1.getText().toString());
                map.put("email", email1.getText().toString());
                map.put("creationDate", ""+System.currentTimeMillis() / 1000L);
                accountManager.createAccount(username, password, map);
                dialog.dismiss();
            }
            catch(XMPPException ex){

                Log.e("XMPPChatDemoActivity", "Failed to Register in as "+ username);
                 connection.disconnect();

            }


        } 

});
    t.start();
    dialog.show();
    
}
}   