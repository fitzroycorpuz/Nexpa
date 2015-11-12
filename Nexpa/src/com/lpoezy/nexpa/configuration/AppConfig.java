package com.lpoezy.nexpa.configuration;

public class AppConfig {
	 // Server user login url
    public static String URL_LOGIN = "http://www.lpoezy.com/happn/json/user.php/";
 
    // Server user register url
    public static String URL_REGISTER = "http://www.lpoezy.com/happn/json/user.php/";
    public static String URL_NEARBY = "http://www.lpoezy.com/happn/json/collect_users.php/";
    public static String URL_GETGEO = "http://www.lpoezy.com/happn/json/get_geo.php/";
    public static String URL_PROFILE = "http://www.lpoezy.com/happn/json/update_profile.php/";
    public static String URL_PROFILE_PIC = " http://www.lpoezy.com/happn/json/get_profile_picture.php";
   
    public static int MSG_NOTIFICATION_ID = 1000;
    
    public static final String ACTION_RECEIVED_MSG = "com.lpoezy.nexpa.actions.ACTION_RECEIVED_MSG";
    
   
}
