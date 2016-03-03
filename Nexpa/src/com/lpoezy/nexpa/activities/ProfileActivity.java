package com.lpoezy.nexpa.activities;


import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends Activity {
 
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
 
    private SQLiteHandler db;
    private SessionManager session;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
 
        Log.e("","ENTERED PROFILE ACT");
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
   
		
 
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        db.openToRead();
        // session manager
        session = new SessionManager(getApplicationContext());
 
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        else{
       
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
 
        String name = user.get("name");
        String email = user.get("email");
 
        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
 
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                logoutUser();
                db.deleteAllPeople();
            }
        });
        }
        
        db.close();
    }
 
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
        LogoutFB(this);
        db.deleteUsers();
//        XMPPConnection connection = XMPPLogic.getInstance().getConnection();
//		connection.disconnect();
        
        // Launching the login activity
        Intent intent = new Intent(ProfileActivity.this, MainSignInActivity.class);
        startActivity(intent);
        finish();
    }
    
    public static void LogoutFB(Context context) {
       /* //Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
                //clear your preferences if saved
        }
*/
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
    	  Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
    	      bitmap.getHeight(), Config.ARGB_8888);
    	  Canvas canvas = new Canvas(output);
    	 
    	  final int color = 0xff424242;
    	  final Paint paint = new Paint();
    	  final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    	  final RectF rectF = new RectF(rect);
    	  final float roundPx = 12;
    	 
    	  paint.setAntiAlias(true);
    	  canvas.drawARGB(0, 0, 0, 0);
    	  paint.setColor(color);
    	  canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	 
    	  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	  canvas.drawBitmap(bitmap, rect, rect, paint);
    	 
    	  return output;
    	}
}
