package com.lpoezy.nexpa.activities;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;

import com.appyvet.rangebar.RangeBar;
import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.JSON.Profile;
import com.lpoezy.nexpa.chatservice.ChatMessagesService;
import com.lpoezy.nexpa.objects.Announcement;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.NiceDialog;
import com.lpoezy.nexpa.utility.RoundedImageView;
import com.lpoezy.nexpa.utility.Utilz;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity implements EditProfileFragment.OnShowProfilePicScreenListener
{
	RangeBar rbDistance;
	
	NiceDialog nd;
    LinearLayout ln_personal;
    LinearLayout ln_preference;
    LinearLayout ln_settings;
    LinearLayout ln_distance;
    Button ln_status;
    Dialog dialog;
    Dialog dialogPref;
    Dialog dialogSettings;
    Dialog dialogStatus;

    ImageView imgProfile;
    EditText edtName;
    EditText edtStatus;
    TextView txtStat;
    TextView txtCharLeft;
    
    RadioButton rad_lookingfor_friends;
    RadioButton rad_lookingfor_date;
    RadioButton rad_lookingfor_serious_relationship;
    RadioButton rad_lookingfor_networking;
    String strLookingForStat;
    
    RadioButton rad_sexual_orientation_unspecified;
    RadioButton rad_sexual_orientation_straight;
    RadioButton rad_sexual_orientation_gay_lesbian;
    RadioButton rad_sexual_orientation_bisexual;
    RadioButton rad_sexual_orientation_transgendered;
    String strSexOrien;
    
    RadioButton rad_looking_for_men;
    RadioButton rad_looking_for_women;
    RadioButton rad_looking_for_both;
    String strGenderPref;
    
    CheckBox cbx_orien_straight;
    CheckBox cbx_orien_gay;
    CheckBox cbx_orien_lesbian;
    CheckBox cbx_orien_bisexual;
    CheckBox cbx_orien_transgendered;
    CheckBox cbx_orien_unspecified;
    String strShowOrientation;
    
    RadioButton rad_relationship_unspecified;
    RadioButton rad_relationship_single;
    RadioButton rad_relationship_in_a_rel;
    RadioButton rad_relationship_married;
    RadioButton rad_relationship_separated;
    RadioButton rad_relationship_widowed;
    RadioButton rad_relationship_complicated;
    String strRelStat;
    
    RadioButton rad_chat_everyone;
    RadioButton rad_chat_friends;
    RadioButton rad_chat_noone;
    String strIndieChat;
    
    RadioButton rad_com_receive;
    RadioButton rad_com_dont_receive;
    String strComChat;
    
    EditText edOldp;
    EditText edNewp;
    EditText edVerip;
    Button btnSavePass;
    
    int statIndexer;
    String distTick = "";
   
    SQLiteHandler db;

    Profile jsonProfile;
    
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
	      super.onActivityResult(requestCode, resultCode, intent);
	      
	      L.debug("UserProfileActivity, onActivityResult");
	      if(profPicDialog!=null)profPicDialog.onActivityResult(requestCode, resultCode, intent);
	      
	 }
    

    
//    @Override
//	public void onBackPressed() {
//		
//		//super.onBackPressed();
//		SessionManager session = new SessionManager(getApplicationContext());
//		if(session.isLoggedIn()){
//			UserProfileActivity.promptYesNoDialog("Quit Toucan?",
//					"Are you sure you want to log off?",
//   					this,
//   					"DEAC",
//   					true);
//		}
//	}
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.activity_userprofile);
        
    	Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(myToolbar);
	  //  myToolbar.setTitle("Settings");
	    TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title);
	    mTitle.setText("SETTINGS");
	    myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
	    myToolbar.setTitle("");
	    
    	if(savedInstanceState==null){
			Fragment myBroadcasts = MyBroadcastsFragment.newInstance();
			getFragmentManager().beginTransaction()
			.add(R.id.frag_container, myBroadcasts, "MyBroadcasts")
			.commit();
		}
    	

    }
    
    private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
    
  
    
    public static void logoutUser(Context context, boolean isExitingApp, Object callback) {
    	
    	SessionManager session = new SessionManager(context);
        session.setLogin(false);
        //LogoutFB(this);
        SQLiteHandler db = new SQLiteHandler(context);
        db.openToWrite();
        db.deleteCorrespondents();
        db.deleteMessages();
        db.deleteProfilePictures();
        db.deleteUsers();
        db.deleteAllPeople();
        db.updateAccountValidate(0);
        
        db.close();
        XMPPConnection connection = XMPPLogic.getInstance().getConnection();
        
        Utilz.clearSharedPref(context);
       
        if(connection != null && connection.isConnected()) 
        {
        	connection.disconnect();
        }
        
        if(ChatMessagesService.isRunning){
        	context.stopService(new Intent(context, ChatMessagesService.class));
	   }
        
        if(callback!=null){
        	((onUserIsLoggedOutListener)callback).onUserIsLoggedOut();
        }
        
        // Launching the login activity
        //if(!isExitingApp){
	        Intent intent = new Intent(context, MainSignInActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(intent);
	       
        //}
        
	   
	     
        ((Activity)context).finish();
    }
    
    private static Dialog dialogStatusYN;
    static LinearLayout lnHeader;
    static TextView edtStatusHead;
    static TextView edtStatus1;
    
    private static ProgressDialog pDialog;
    public static void promptYesNoDialog(final String caption, final String message, final Context cn, final String fcType, final boolean isExitingApp){
    	
    	dialogStatusYN = new Dialog(cn);
    	dialogStatusYN.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialogStatusYN.setContentView(R.layout.dialog_yesno);
        final Button dialogButton = (Button) dialogStatusYN.findViewById(R.id.dialogButtonYes);
        final Button dialogButtonNo = (Button) dialogStatusYN.findViewById(R.id.dialogButtonNo);
        lnHeader = (LinearLayout) dialogStatusYN.findViewById(R.id.lnHeader);
        edtStatusHead = (TextView) dialogStatusYN.findViewById(R.id.edtStatusHead);
        edtStatus1 = (TextView) dialogStatusYN.findViewById(R.id.edtStatus);
        
        edtStatusHead.setText(caption);
        edtStatus1.setText(message);
        
        dialogButton.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
        dialogButtonNo.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
       	lnHeader.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
       	
       	
       	
        
        dialogButton.setOnClickListener(new OnClickListener()
        {	
        	@Override
            public void onClick(View v){	
        		if (fcType.equals("DEAC")){
        			
        			
        			//dismiss quit toucan dialog
        			dialogStatusYN.dismiss();
        			
//        			dialogButton.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							//show progress dialog
//							L.error("showing dialog...");
//							if(pDialog!=null)pDialog.show();
//						}
//					});
        			
        			
					if(pDialog==null){
						L.error("showing dialog...");
						 // Progress dialog
				        pDialog = new ProgressDialog(cn);
				        pDialog.setCancelable(false);
				        pDialog.setMessage("Logging out ...");
						
						pDialog.show();
					}
        			
        			new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							
							 logoutUser(cn, isExitingApp, new onUserIsLoggedOutListener() {
								
								@Override
								public void onUserIsLoggedOut() {
									L.error("onUserIsLoggedOut...");
									if (pDialog != null) {
										L.error("dismissing dialog...");
										pDialog.dismiss();
										pDialog = null;
									}
									
									
//									dialogButton.post(new Runnable() {
//										
//										@Override
//										public void run() {
//											
//												
//											
//											
//										}
//									});
									
								}
							});
						        
						      
							
						}
					}).start();
        			
        		}
            }
        });
        
        dialogButtonNo.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {	
            	dialogStatusYN.dismiss();
            }
        });
        
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogStatusYN.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogStatusYN.show();
        dialogStatusYN.getWindow().setAttributes(lp);
    }
    
    public interface onUserIsLoggedOutListener{
    	public void onUserIsLoggedOut();
    }
    
    ProfilePicFragment profPicDialog;
	@Override
	public void onShowProfilePicScreen() {
		 profPicDialog = ProfilePicFragment.newInstance();

		profPicDialog.show(getFragmentManager().beginTransaction(), ProfilePicFragment.TAG);
		
	}

}