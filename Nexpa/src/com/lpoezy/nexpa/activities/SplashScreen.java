package com.lpoezy.nexpa.activities;

import org.jivesoftware.smack.XMPPConnection;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.chatservice.ChatMessagesService;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreen extends Activity {

   // Splash screen timer
   private static int SPLASH_TIME_OUT = 2000;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.activity_splash);
      
       new Handler().postDelayed(new Runnable() {

           /*
            * Showing splash screen with a timer. This will be useful when you
            * want to show case your app logo / company
            */

           @Override
           public void run() {
        	   
        	SessionManager session = new SessionManager(getApplicationContext());
        	//this will always ask the user to login,
        	//whenever the app crashed,
        	//or the user forced to terminate the app
       		if (session.isLoggedIn()) {
       		
       			//erase all existing records in db,
       			//except the recorded conversation for future use
       			SQLiteHandler db = new SQLiteHandler(SplashScreen.this);
       			db.openToWrite();
       			db.deleteUsers();
       			db.deleteAllPeople();
       			db.updateAccountValidate(0);
       			db.close();
       			session.setLogin(false);
       			
       			//disconnect established connection from xmpp server
       			XMPPConnection connection = XMPPLogic.getInstance().getConnection();
       	       
       	        if(connection != null && connection.isConnected()) 
       	        {
       	        	connection.disconnect();
       	        }
       	        
       	        //stop any running service
       	        if(ChatMessagesService.isRunning){
       	        	stopService(new Intent(SplashScreen.this, ChatMessagesService.class));
       	        }
       			
       		}
        	   
        	  
               // This method will be executed once the timer is over
               // Start your app main activity
               Intent i = new Intent(SplashScreen.this, TabHostActivity.class);
               startActivity(i);

               // close this activity
               finish();
           }
       }, SPLASH_TIME_OUT);
   }

}