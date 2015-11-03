package com.lpoezy.nexpa.activities;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jivesoftware.smack.XMPPConnection;

import com.appyvet.rangebar.RangeBar;
import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.JSON.Profile;
import com.lpoezy.nexpa.chatservice.ChatMessagesService;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.NiceDialog;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
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

public class UserProfileActivity extends Activity
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
    
    private DatePicker dpBDay;
    
    private Button btnLogout;
    
    SQLiteHandler db;
    private SessionManager session;
    Profile jsonProfile;
    
    private void updateStatusDisplay(){
        if ((db.getStatusMessage().equals(""))||(db.getStatusMessage().equals("null"))) {
            txtStat.setText("No status yet.\nTap 'CHANGE' to update status");
        }
        else{
            txtStat.setText('"' + db.getStatusMessage() + '"');
        }
    }
    
    @Override
	public void onBackPressed() {
		
		//super.onBackPressed();
		SessionManager session = new SessionManager(getApplicationContext());
		if(session.isLoggedIn()){
			UserProfileActivity.promptYesNoDialog("Quit Toucan?",
					"Are you sure you want to log off?",
   					this,
   					"DEAC",
   					true);
		}
	}
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.activity_userprofile);

        db = new SQLiteHandler(this);
		db.openToWrite();
        jsonProfile = new Profile();
        // .setMaxDate(calendar.getTimeInMillis());
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        txtStat = (TextView) findViewById(R.id.txtStat);
        updateStatusDisplay();
        RoundedImageView riv = new RoundedImageView(this);
        Bitmap rawImage = BitmapFactory.decodeResource(this.getResources(),
        R.drawable.pic_sample_girl);
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 400);
        imgProfile.setImageBitmap(circImage);
        ln_status = (Button) findViewById(R.id.btnStatus);
        
        Date date = new Date();
    	DateUtils du;
    	du = new DateUtils();

        ln_status.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                //Intent intent = new Intent(UserProfileActivity.this, UserPersonalActivity.class);
                //intent.putExtra("user_id", "");
                dialogStatus = new Dialog(UserProfileActivity.this);
                dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogStatus.setContentView(R.layout.activity_profile_status);
                Button dialogButton = (Button) dialogStatus.findViewById(R.id.dialogButtonOK);
                txtCharLeft = (TextView) dialogStatus.findViewById(R.id.txtCharLeft);
                statIndexer = 0;
                edtStatus =  (EditText) dialogStatus.findViewById(R.id.edtStatus);
                edtStatus.addTextChangedListener(new TextWatcher(){
                    public void afterTextChanged(Editable s) {
                        statIndexer = 150- edtStatus.getText().toString().length();
                        txtCharLeft.setText(statIndexer + "");
                    }
                    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                    public void onTextChanged(CharSequence s, int start, int before, int count){}
                });
                dialogButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                    	db.updateUserStatus(edtStatus.getText().toString());
                        jsonProfile.updateStatOnServer(UserProfileActivity.this);
                        updateStatusDisplay();
                        dialogStatus.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogStatus.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogStatus.show();
                dialogStatus.getWindow().setAttributes(lp);
            }
        });
        ln_personal = (LinearLayout) findViewById(R.id.ln_personal);
        ln_personal.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                final DateUtils du = new DateUtils();
                //Intent intent = new Intent(UserProfileActivity.this, UserPersonalActivity.class);
                //intent.putExtra("user_id", "");
                dialog = new Dialog(UserProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_profile_personal);
                
                final RadioButton radBoy;
                final RadioButton radGirl;
                final RadioButton radUnspec;
                
                dpBDay = (DatePicker) dialog.findViewById(R.id.dbBirthday);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18 );
                dpBDay.setMaxDate(c.getTime().getTime());
                
                edtName = (EditText) dialog.findViewById(R.id.edtName);
                
                radBoy = (RadioButton) dialog.findViewById(R.id.radioMale);
                radGirl = (RadioButton) dialog.findViewById(R.id.radioFemale);
                radUnspec = (RadioButton) dialog.findViewById(R.id.radioUnspecified);
                
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                edtName.setText(db.getName());
                
                if  ((db.getBDate().equals(""))||(db.getBDate().equals("null"))){
                    dpBDay.updateDate(1980, 0, 1);
                }
                else{
                    Date dtParse = new Date();
                    dtParse = du.convertStringToDateToLocal(db.getBDate());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dtParse);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    dpBDay.updateDate(year, month , day);
                }
                if (db.getGender().equals("M")){
                    radBoy.setChecked(true);
                }
                else if (db.getGender().equals("F")){
                    radGirl.setChecked(true);
                }
                else{
                    radUnspec.setChecked(true);
                }
                dialogButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String name = edtName.getText().toString();
                        String gender = "";
                        String dateBday = "";
                        if (radGirl.isChecked()){
                            gender = "F";
                        }
                        else if (radBoy.isChecked()){
                            gender = "M";
                        }
                        else {
                            gender = "U";
                        }
                        int day = dpBDay.getDayOfMonth();
                        int month= dpBDay.getMonth();
                        int year = dpBDay.getYear();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                        @SuppressWarnings("deprecation")
                        String formatedDate = sdf.format(new Date(year, month, day));
                        
                        try{
                            Date date = sdf.parse(formatedDate);
                            dateBday = du.convertDateToStringToLocalTime(date);
                            } catch (ParseException e){
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        db.updateUserPersonal(name, dateBday, gender);
                        jsonProfile.updateBasicOnServer(UserProfileActivity.this);
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });
        ln_distance = (LinearLayout) findViewById(R.id.ln_distance);
        ln_distance.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                dialogPref = new Dialog(UserProfileActivity.this);
                dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogPref.setContentView(R.layout.activity_profile_distance_settings);
               
                rbDistance = (RangeBar) dialogPref.findViewById(R.id.rbDistance);
                rbDistance.setRangeBarEnabled(false);
                rbDistance.setSeekPinByValue(Float.parseFloat(db.getBroadcastDist()));
                
                rbDistance.setPinColor(getResources().getColor(R.color.EDWARD));
                rbDistance.setConnectingLineColor(getResources().getColor(R.color.EDWARD));
                rbDistance.setSelectorColor(getResources().getColor(R.color.EDWARD));
                rbDistance.setPinRadius(30f);
                rbDistance.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                    @Override
                    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                            int rightPinIndex, String leftPinValue, String rightPinValue) {
                    	distTick = rightPinValue;
                    }
                });
            
                Button dialogButton = (Button) dialogPref.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                       
                        db.updateBroadcastDist(distTick);
                        dialogPref.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogPref.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogPref.show();
                dialogPref.getWindow().setAttributes(lp);
            }
        });
        //////////////
        ln_preference = (LinearLayout) findViewById(R.id.ln_preference);
        ln_preference.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                dialogPref = new Dialog(UserProfileActivity.this);
                dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogPref.setContentView(R.layout.activity_profile_preferences);
                rad_lookingfor_friends = (RadioButton) dialogPref.findViewById(R.id.rad_lookingfor_friends);
                rad_lookingfor_date = (RadioButton) dialogPref.findViewById(R.id.rad_lookingfor_date);
                rad_lookingfor_serious_relationship = (RadioButton) dialogPref.findViewById(R.id.rad_lookingfor_serious_relationship);
                rad_lookingfor_networking = (RadioButton) dialogPref.findViewById(R.id.rad_lookingfor_networking);
                //Looking for
                if  ((db.getLookingForStatus().equals(""))||(db.getLookingForStatus().equals("null"))){
                    rad_lookingfor_friends.setChecked(true);
                }
                else if (db.getLookingForStatus().equals("DA")){
                    rad_lookingfor_date.setChecked(true);
                }
                else if (db.getLookingForStatus().equals("SE")){
                    rad_lookingfor_serious_relationship.setChecked(true);
                }
                else if (db.getLookingForStatus().equals("NE")){
                    rad_lookingfor_networking.setChecked(true);
                }
                else{
                    rad_lookingfor_friends.setChecked(true);
                }
                //Sexual Orientation
                rad_sexual_orientation_unspecified = (RadioButton) dialogPref.findViewById(R.id.rad_sexual_orientation_unspecified);
                rad_sexual_orientation_straight = (RadioButton) dialogPref.findViewById(R.id.rad_sexual_orientation_straight);
                rad_sexual_orientation_gay_lesbian = (RadioButton) dialogPref.findViewById(R.id.rad_sexual_orientation_gay_lesbian);
                rad_sexual_orientation_bisexual = (RadioButton) dialogPref.findViewById(R.id.rad_sexual_orientation_bisexual);
                rad_sexual_orientation_transgendered = (RadioButton) dialogPref.findViewById(R.id.rad_sexual_orientation_transgendered);
                if  ((db.getSexOrien().equals(""))||(db.getSexOrien().equals("null"))){
                    rad_sexual_orientation_unspecified.setChecked(true);
                }
                else if (db.getSexOrien().equals("ST")){
                    rad_sexual_orientation_straight.setChecked(true);
                }
                else if (db.getSexOrien().equals("LB")){
                    rad_sexual_orientation_gay_lesbian.setChecked(true);
                }
                else if (db.getSexOrien().equals("BI")){
                    rad_sexual_orientation_bisexual.setChecked(true);
                }
                else if (db.getSexOrien().equals("TR")){
                    rad_sexual_orientation_transgendered.setChecked(true);
                }
                else{
                    rad_sexual_orientation_unspecified.setChecked(true);
                }
                //Show gender
                rad_looking_for_men = (RadioButton) dialogPref.findViewById(R.id.rad_looking_for_men);
                rad_looking_for_women = (RadioButton) dialogPref.findViewById(R.id.rad_looking_for_women);
                rad_looking_for_both = (RadioButton) dialogPref.findViewById(R.id.rad_looking_for_both);
                
                String strLookingGender = db.getLookingGender();
                if  ((strLookingGender.equals(""))||(strLookingGender.equals("null"))){
                    rad_looking_for_both.setChecked(true);
                }
                else if (strLookingGender.equals("ML")){
                    rad_looking_for_men.setChecked(true);
                }
                else if (strLookingGender.equals("FM")){
                    rad_looking_for_women.setChecked(true);
                }
                else{
                    rad_looking_for_both.setChecked(true);
                }
                //Show Profile Sexual Orientation
                cbx_orien_straight = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_straight);
                cbx_orien_gay = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_gay);
                cbx_orien_lesbian = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_lesbian);
                cbx_orien_bisexual = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_bisexual);
                cbx_orien_transgendered = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_transgendered);
                cbx_orien_unspecified =  (CheckBox) dialogPref.findViewById(R.id.cbx_orien_unspecified);
                String strOrien = db.getOrientationToShow();
                
                char ch_orien_straight = strOrien.charAt(0);
                char ch_orien_gay = strOrien.charAt(2);
                char ch_orien_lesbian = strOrien.charAt(4);
                char ch_orien_bisexual = strOrien.charAt(6);
                char ch_orien_transgendered = strOrien.charAt(8);
                char ch_orien_unspecified = strOrien.charAt(10);
                
                if  ((db.getOrientationToShow().equals(""))||(db.getOrientationToShow().equals("null"))){
                    //   S-G-L-B-T-U
                    cbx_orien_straight.setChecked(true);
                    cbx_orien_gay.setChecked(true);
                    cbx_orien_lesbian.setChecked(true);
                    cbx_orien_bisexual.setChecked(true);
                    cbx_orien_transgendered.setChecked(true);
                    cbx_orien_unspecified.setChecked(true);
                }
                else{
                    if (ch_orien_straight == '1'){
                        cbx_orien_straight.setChecked(true);
                    }
                    else{
                        cbx_orien_straight.setChecked(false);
                    }
                    if (ch_orien_gay == '1'){
                        cbx_orien_gay.setChecked(true);
                    }
                    else{
                        cbx_orien_gay.setChecked(false);
                    }
                    if (ch_orien_lesbian == '1'){
                        cbx_orien_lesbian.setChecked(true);
                    }
                    else{
                        cbx_orien_lesbian.setChecked(false);
                    }
                    if (ch_orien_bisexual == '1'){
                        cbx_orien_bisexual.setChecked(true);
                    }
                    else{
                        cbx_orien_bisexual.setChecked(false);
                    }
                    if (ch_orien_transgendered == '1'){
                        cbx_orien_transgendered.setChecked(true);
                    }
                    else{
                        cbx_orien_transgendered.setChecked(false);
                    }
                    if (ch_orien_unspecified == '1'){
                        cbx_orien_unspecified.setChecked(true);
                    }
                    else{
                        cbx_orien_unspecified.setChecked(false);
                    }
                }
                //Relationship status
                rad_relationship_unspecified = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_unspecified);
                rad_relationship_single = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_single);
                rad_relationship_in_a_rel = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_in_a_rel);
                rad_relationship_married = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_married);
                rad_relationship_separated = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_separated);
                rad_relationship_widowed = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_widowed);
                rad_relationship_complicated = (RadioButton) dialogPref.findViewById(R.id.rad_relationship_complicated);
                
                String relStatus = db.getRelStatus();
                if  ((relStatus.equals(""))||(relStatus.equals("null"))){
                    rad_relationship_unspecified.setChecked(true);
                }
                else if (relStatus.equals("SI")){
                    rad_relationship_single.setChecked(true);
                }
                else if (relStatus.equals("IN")){
                    rad_relationship_in_a_rel.setChecked(true);
                }
                else if (relStatus.equals("MA")){
                    rad_relationship_married.setChecked(true);
                }
                else if (relStatus.equals("SE")){
                    rad_relationship_separated.setChecked(true);
                }
                else if (relStatus.equals("WI")){
                    rad_relationship_widowed.setChecked(true);
                }
                else if (relStatus.equals("CO")){
                    rad_relationship_complicated.setChecked(true);
                }
                else{
                    rad_relationship_unspecified.setChecked(true);
                }
                Button dialogButton = (Button) dialogPref.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Save changes in 'Looking For'
                        if    (rad_lookingfor_date.isChecked()){
                            strLookingForStat = "DA";
                        }
                        else if    (rad_lookingfor_serious_relationship.isChecked()){
                            strLookingForStat = "SE";
                        }
                        else if    (rad_lookingfor_networking.isChecked()){
                            strLookingForStat = "NE";
                        }
                        else {
                            strLookingForStat = "FR";
                        }
                        //Save changes in 'Sexual Orientation'
                        if    (rad_sexual_orientation_straight.isChecked()){
                            strSexOrien = "ST";
                        }
                        else if    (rad_sexual_orientation_gay_lesbian.isChecked()){
                            strSexOrien = "LB";
                        }
                        else if    (rad_sexual_orientation_bisexual.isChecked()){
                            strSexOrien = "BI";
                        }
                        else if    (rad_sexual_orientation_transgendered.isChecked()){
                            strSexOrien = "TR";
                        }
                        else {
                            strSexOrien = "UN";
                        }
                        //Save changes in 'Gender Pref'
                        if    (rad_looking_for_men.isChecked()){
                            strGenderPref = "ML";
                        }
                        else if    (rad_looking_for_women.isChecked()){
                            strGenderPref = "FM";
                        }
                        else {
                            strGenderPref = "SB";
                        }
                        //Save changes in Show Sex Orien
                        //Show Profile Sexual Orientation
                        cbx_orien_straight = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_straight);
                        cbx_orien_gay = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_gay);
                        cbx_orien_lesbian = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_lesbian);
                        cbx_orien_bisexual = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_bisexual);
                        cbx_orien_transgendered = (CheckBox) dialogPref.findViewById(R.id.cbx_orien_transgendered);
                        cbx_orien_unspecified =  (CheckBox) dialogPref.findViewById(R.id.cbx_orien_unspecified);
                        strShowOrientation = "S-G-L-B-T-U";
                        StringBuilder SBorien = new StringBuilder(strShowOrientation);
                        SBorien.setCharAt(4, 'x');
                        //   S-G-L-B-T-U
                        if    (cbx_orien_straight.isChecked()){
                            SBorien.setCharAt(0,'1');
                        }
                        else{
                            SBorien.setCharAt(0, '0');
                        }
                        if    (cbx_orien_gay.isChecked()){
                            SBorien.setCharAt(2, '1');
                        }
                        else{
                            SBorien.setCharAt(2, '0');
                        }
                        if    (cbx_orien_lesbian.isChecked()){
                            SBorien.setCharAt(4,'1');
                        }
                        else{
                            SBorien.setCharAt(4,'0');
                        }
                        if    (cbx_orien_bisexual.isChecked()){
                            SBorien.setCharAt(6, '1');
                        }
                        else{
                            SBorien.setCharAt(6,'0');
                        }
                        if    (cbx_orien_transgendered.isChecked()){
                            SBorien.setCharAt(8, '1');
                        }
                        else{
                            SBorien.setCharAt(8,'0');
                        }
                        if    (cbx_orien_unspecified.isChecked()){
                            SBorien.setCharAt(10,'1');
                        }
                        else{
                            SBorien.setCharAt(10, '0');
                        }
                        Log.e("SBORIEN",SBorien.toString());
                        //Save changes in 'Relationship status'
                        if    (rad_relationship_single.isChecked()){
                            strRelStat = "SI";
                        }
                        else if    (rad_relationship_in_a_rel.isChecked()){
                            strRelStat = "IN";
                        }
                        else if    (rad_relationship_married.isChecked()){
                            strRelStat = "MA";
                        }
                        else if    (rad_relationship_separated.isChecked()){
                            strRelStat = "SE";
                        }
                        else if    (rad_relationship_widowed.isChecked()){
                            strRelStat = "WI";
                        }
                        else if    (rad_relationship_complicated.isChecked()){
                            strRelStat = "CO";
                        }
                        else {
                            strRelStat = "UN";
                        }
                        
                        db.updateUserPreference(strLookingForStat, strSexOrien, strGenderPref, strRelStat, SBorien.toString());
                        jsonProfile.updateProfileOnServer(UserProfileActivity.this);
                        dialogPref.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogPref.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialogPref.show();
                dialogPref.getWindow().setAttributes(lp);
            }
        });
        ln_settings = (LinearLayout) findViewById(R.id.ln_settings);
        ln_settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                //Intent intent = new Intent(UserProfileActivity.this, UserPersonalActivity.class);
                //intent.putExtra("user_id", "");
                dialogSettings = new Dialog(UserProfileActivity.this);
                dialogSettings.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogSettings.setContentView(R.layout.activity_profile_settings);
                
                edOldp = (EditText) dialogSettings.findViewById(R.id.edOldp);
                edNewp = (EditText) dialogSettings.findViewById(R.id.edNewp);
                edVerip = (EditText) dialogSettings.findViewById(R.id.edVerip);
                btnSavePass = (Button) dialogSettings.findViewById(R.id.btnSavePass);
                edOldp.setTypeface(Typeface.DEFAULT);
                edNewp.setTypeface(Typeface.DEFAULT);
                edVerip.setTypeface(Typeface.DEFAULT);
                
                session = new SessionManager(getApplicationContext());
                btnLogout = (Button) dialogSettings.findViewById(R.id.btn_deac);
                if (!session.isLoggedIn()) {
                    logoutUser(UserProfileActivity.this, false, null);
                }
                else{
                // Fetching user details from sqlite
                //HashMap<String, String> user = db.getUserDetails();
         
                //String name = user.get("name");
                //String email = user.get("email");
         
                // Displaying the user details on the screen
                //txtName.setText(name);
                //txtEmail.setText(email);
         
                // Logout button click event
                	
                	nd = new NiceDialog();
//                btnLogout.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    	promptYesNoDialog("Deactivate Account","You will lose all Toucan's data on this device but won't remove your data on our server.\n\nDo you wish to continue? ",UserProfileActivity.this,"DEAC", false);
//                     }
//                });
                }
            
                
                btnSavePass.setOnClickListener(new OnClickListener()
                {
                	@Override
                    public void onClick(View v)
                    {
                		if (edOldp.getText().toString().trim().length() > 0 && edNewp.getText().toString().trim().length() > 0 && edVerip.getText().toString().trim().length() > 0){
                			if (edNewp.getText().toString().trim().length() > 7){
                				if (edOldp.getText().toString().equals(db.getPass())){
                				if (edVerip.getText().toString().equals(edNewp.getText().toString())){
                					db.updatePass(edVerip.getText().toString());
                					jsonProfile.updatePasswordOnServer(UserProfileActivity.this);
                				}
                				else{
                					NiceDialog.promptDialog("Invalid Password!","Password doesn't match confirmation.",UserProfileActivity.this,"warning");
                                }
                			}
                			else{
                				NiceDialog.promptDialog("Invalid Password!","Current password did not match.",UserProfileActivity.this,"warning");
                                
                			}
                			}
                			else{
                				NiceDialog.promptDialog("Weak Password!","Password should be atleast 8 characters.",UserProfileActivity.this,"warning");
                                
                			}
                		}
                		else{
                			NiceDialog.promptDialog("Invalid input!","Please fill-out all fields in order to change your password.",UserProfileActivity.this,"warning");
                		}
                    }
                });
                
                rad_chat_everyone = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_everyone);
                rad_chat_friends = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_friends);
                rad_chat_noone = (RadioButton) dialogSettings.findViewById(R.id.rad_chat_noone);
                strIndieChat ="";
                
                if  ((db.getIndiSetChatPrivacy().equals(""))||(db.getIndiSetChatPrivacy().equals("null"))){
                	rad_chat_everyone.setChecked(true);
                }
                else if  (db.getIndiSetChatPrivacy().equals("NO")){
                	rad_chat_noone.setChecked(true);
                }
                else if  (db.getIndiSetChatPrivacy().equals("FR")){
                	rad_chat_friends.setChecked(true);
                }
                else{
                	rad_chat_everyone.setChecked(true);
                }
               
                rad_com_receive = (RadioButton) dialogSettings.findViewById(R.id.rad_com_receive);
                rad_com_dont_receive = (RadioButton) dialogSettings.findViewById(R.id.rad_com_dont_receive);
                strComChat = "";
                if  ((db.getComSetChatPrivacy().equals(""))||(db.getComSetChatPrivacy().equals("null"))){
                	rad_com_receive.setChecked(true);
                }
                else if  (db.getComSetChatPrivacy().equals("DO")){
                	rad_com_dont_receive.setChecked(true);
                }
                else{
                	rad_com_receive.setChecked(true);
                }
                
                Button dialogButton = (Button) dialogSettings.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new OnClickListener()
                {
                	@Override
                    public void onClick(View v)
                    {
                		 //Save changes in 'Individual Chat'
                		 if (rad_chat_everyone.isChecked()){
                			 strIndieChat = "EV";
                         }
                         else if (rad_chat_friends.isChecked()){
                        	 strIndieChat = "FR";
                         }
                         else if (rad_chat_noone.isChecked()){
                        	 strIndieChat = "NO";
                         }
                         else{
                        	 strIndieChat = "EV";
                         }
                         
                         //Save changes in 'Community Chat'
                         if    (rad_com_receive.isChecked()){
                        	 strComChat = "RE";
                         }
                         else if    (rad_com_dont_receive.isChecked()){
                        	 strComChat = "DO";
                         }
                         else{
                        	 strComChat = "RE";
                         }
                         db.updateUserSettings(strIndieChat, strComChat);
                         
                         dialogSettings.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogSettings.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialogSettings.show();
                dialogSettings.getWindow().setAttributes(lp);
            }
        });
    }
    
    private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    public static void logoutUser(Context context, boolean isExitingApp, Object callback) {
    	
    	SessionManager session = new SessionManager(context);
        session.setLogin(false);
        //LogoutFB(this);
        SQLiteHandler db = new SQLiteHandler(context);
        db.openToWrite();
        db.deleteUsers();
        db.deleteAllPeople();
        db.updateAccountValidate(0);
        XMPPConnection connection = XMPPLogic.getInstance().getConnection();
       
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
	        context.startActivity(intent);
	       
       // }
        
	        
	     
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
    
    
}