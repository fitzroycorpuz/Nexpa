package com.lpoezy.nexpa.configuration;

public class AppConfig {

	public static final String URL = "http://www.lpoezy.com/happn";
	// Server user login url
	public static String URL_LOGIN = "http://www.lpoezy.com/happn/json/user.php/";

	// Server user register url
	public static String URL_REGISTER = "http://www.lpoezy.com/happn/json/user.php/";
	public static String URL_NEARBY = "http://www.lpoezy.com/happn/json/collect_users.php/";
	public static String URL_GETGEO = "http://www.lpoezy.com/happn/json/get_geo.php/";
	public static String URL_PROFILE = "http://www.lpoezy.com/happn/json/update_profile.php/";

	public static String URL_PROFILE_PIC = "http://www.lpoezy.com/happn/json/get_profile_picture.php";
	public static final String URL_MSG = "http://www.lpoezy.com/happn/json/messages.php";
	public static final String URL_USER_PROFILES = "http://www.lpoezy.com/happn/json/user_profiles.php";
	public static String URL_SEND_EMAIL = "http://198.154.106.139/~nexpa/php/send_email.php";

	public static int MSG_NOTIFICATION_ID = 1000;
	public static final String ACTION_RECEIVED_MSG = "com.lpoezy.nexpa.actions.ACTION_RECEIVED_MSG";
	public static final String ACTION_USER_PROFILE_UPDATED = "com.lpoezy.nexpa.actions.ACTION_USER_PROFILE_UPDATED";
	public static final int SUPERUSER_MIN_DISTANCE_KM = 1;

	public static int SUPERUSER_MAX_DISTANCE_KM = 7000;

}
