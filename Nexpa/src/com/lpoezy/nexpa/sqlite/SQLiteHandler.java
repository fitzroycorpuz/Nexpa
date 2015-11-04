package com.lpoezy.nexpa.sqlite;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lpoezy.nexpa.chatservice.OneComment;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.Users;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLiteHandler{
	private static final String TAG = SQLiteHandler.class.getSimpleName();
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db_kookaboora";
	
	private static final String TABLE_LOGIN = "user";
	private static final String KEY_ID = "_id";
	private static final String KEY_PASS = "password";
	private static final String KEY_NAME = "username";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_UTYPE = "utype";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_FNAME = "fname";
	private static final String KEY_AGE = "age";
	private static final String KEY_GENDER = "gender";
	private static final String KEY_ABOUTME = "about_me";
	private static final String KEY_LOOKING_FOR_STATUS = "looking_status";
	private static final String KEY_SEX_ORIEN = "sex_orien";
	private static final String KEY_LOOKING_FOR_GENDER = "looking_gender";
	private static final String KEY_ORIEN_TO_SHOW = "orien_to_show";
	private static final String KEY_REL_STATUS = "rel_status";
	private static final String KEY_PRIVACY_IN_CHAT = "in_chat";
	private static final String KEY_PRIVACY_COM_CHAT = "com_chat";
	private static final String KEY_UPDATE_BASIC = "update_status";
	private static final String KEY_UPDATE_PREF = "update_status_pref";
	private static final String KEY_UPDATE_PASS = "update_status_pass";
	private static final String KEY_UPDATE_STATUS_MES = "update_status_mes";
	private static final String KEY_LAST_UPDATE = "last_update";
	
	private static final String TABLE_CORRESPONDENTS = "correspondents";
	public static final String CORRESPONDENT_ID = "_id";
	public static final String CORRESPONDENT_USERNAME = "correspondent_username";
	public static final String CORRESPONDENT_EMAIL = "correspondent_email";
	public static final String CORRESPONDENT_FNAME = "correspondent_fname";
	
	//boolean left, String comment,boolean success, String date
	private static final String TABLE_MESSAGES = "messages";
	public static final String MSG_ID = "_id";//KEY_ID
	public static final String MSG_USER_ID = "msg_user_id";
	public static final String MSG_CORRESPONDENT_ID = "correspondent_id";
	public static final String MSG_LEFT = "msg_left";
	public static final String MSG_BODY = "msg_body";
	public static final String MSG_SUCCESS = "msg_success";
	public static final String MSG_DATE = "msg_date";
	public static final String MSG_IS_UNREAD = "msg_is_unread";
	
	
	
	private static final String DATABASE_TABLE_2 = "profile";
	public static final String PROFILE_ID = "_id";
	public static final String PROFILE_USER_ID = "user_id";
	public static final String PROFILE_USERNAME = "username";
	public static final String PROFILE_DISTANCE = "distance";
	public static final String PROFILE_FNAME = "firstname";
	public static final String PROFILE_LNAME = "lastname";
	public static final String PROFILE_AGE = "age";
	public static final String PROFILE_GENDER = "gender";
	public static final String PROFILE_LOOKING_FOR = "looking_for";
	public static final String PROFILE_DATE_SEEN = "date_seen";
	public static final String PROFILE_SHOWN = "is_shown";
	public static final String PROFILE_ABOUTME = "about_me";
	public static final String PROFILE_LOOKING_TYPE = "looking_type";
	public static final String PROFILE_STATUS = "status";
	public static final String PROFILE_EMAIL = "email";
	
	public static final String DATABASE_TABLE_3 = "broadcast";
	public static final String BROAD_ID = "_id";
	public static final String BROADCAST_TYPE = "broad_type_of";
	public static final String BROADCAST_FROM = "broad_from";
	public static final String BROADCAST_MESSAGE = "broad_message";
	public static final String BROADCAST_DATE = "date_broad";
	public static final String BROADCAST_LOCATION_LONG = "location_long";
	public static final String BROADCAST_LOCATION_LAT = "location_lat";
	public static final String BROADCAST_LOCATION_LOCAL = "location_local";
	public static final String BROADCAST_REACH = "reach";
	public static final String BROADCAST_STATUS = "status";
	
	private static final String DATABASE_TABLE_4 = "location";
	private static final String LOC_ID = "_id";
	private static final String LOC_LONGI = "longitude";
	private static final String LOC_LATI = "latitude";
	private static final String LOC_DATE = "date_written";
	
	private static final String DATABASE_TABLE_5 = "options";
	private static final String OPTION_ID = "_id";
	private static final String OPTION_IS_RECIEVING_BROADCAST = "_is_rec_broadcast";
	private static final String OPTION_IS_BROADCAST_TIMER_TICKING = "_is_broadcast_tick";
	private static final String OPTION_IS_ACCOUNT_FAILED_TO_VALIDATE = "_is_failed_to_validate";
	private static final String OPTION_DISTANCE_PREF = "_distance_pref";
	

	 private SQLiteDatabase sqLiteDatabase;
	 private SQLiteHelper sqLiteHelper;
	 private Context context;
	DateUtils dateFunc;
	private SQLiteDatabase mDB;
	private String fname; 

	public SQLiteHandler(Context c){
		  context = c;
		 }
	
	
		 
		 
	public class SQLiteHelper extends SQLiteOpenHelper {

		  public SQLiteHelper(Context context, String name,
		    CursorFactory factory, int version) {
		   super(context, name, factory, version);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase db) {
		   // TODO Auto-generated method stub
		   //db.execSQL(SCRIPT_CREATE_DATABASE);
			  	String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PASS + " TEXT," + KEY_EMAIL + " TEXT UNIQUE," + KEY_UTYPE + " TEXT," + KEY_CREATED_AT + " TEXT," + KEY_FNAME + " TEXT," + KEY_AGE + " TEXT," + KEY_GENDER + " TEXT," + KEY_ABOUTME + " TEXT, " + KEY_LOOKING_FOR_STATUS + " TEXT, " + KEY_SEX_ORIEN + " TEXT, " + KEY_LOOKING_FOR_GENDER + " TEXT," + KEY_ORIEN_TO_SHOW + " TEXT, " + KEY_REL_STATUS + " TEXT, " + KEY_PRIVACY_IN_CHAT + " TEXT, " + KEY_PRIVACY_COM_CHAT + " TEXT, " + KEY_UPDATE_BASIC + " TEXT, " + KEY_UPDATE_PREF + " TEXT, " + KEY_UPDATE_PASS + " TEXT, " + KEY_UPDATE_STATUS_MES + " TEXT, " + KEY_LAST_UPDATE + " TEXT );";
				db.execSQL(CREATE_LOGIN_TABLE);
				
				String CREATE_TABLE_CORRESPONDENTS = "CREATE TABLE " + TABLE_CORRESPONDENTS + "(" + CORRESPONDENT_ID + " INTEGER PRIMARY KEY, "+  CORRESPONDENT_USERNAME + " TEXT, " +   CORRESPONDENT_EMAIL + " TEXT," +   CORRESPONDENT_FNAME + " TEXT);";
				Log.e("CREATE_TABLE_CORRESPONDENTS", CREATE_TABLE_CORRESPONDENTS);
				db.execSQL(CREATE_TABLE_CORRESPONDENTS);
				
				String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "(" + MSG_ID + " INTEGER PRIMARY KEY, "+ MSG_USER_ID +" INTEGER, "+ MSG_CORRESPONDENT_ID + " INTEGER, "+  MSG_LEFT + " TEXT, " +   MSG_BODY + " TEXT," +   MSG_SUCCESS + " TEXT," + MSG_DATE + " TEXT, "+ MSG_IS_UNREAD + " TEXT );";
				db.execSQL(CREATE_TABLE_MESSAGES);
				
				String DATABASE_CREATE_2 = "create table " + DATABASE_TABLE_2 + "(" + PROFILE_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, " + PROFILE_USER_ID + " integer," + PROFILE_USERNAME + " TEXT," + PROFILE_DISTANCE + " integer," + PROFILE_FNAME + " TEXT," + PROFILE_LNAME + " TEXT," + PROFILE_AGE + " integer," + PROFILE_GENDER + " TEXT," + PROFILE_LOOKING_FOR + " TEXT," + PROFILE_DATE_SEEN + " date, " + PROFILE_SHOWN + " integer, " + PROFILE_ABOUTME + " TEXT, " + PROFILE_LOOKING_TYPE + " TEXT, " + PROFILE_STATUS + " TEXT, " + PROFILE_EMAIL + " TEXT );";
				db.execSQL(DATABASE_CREATE_2);
				
				String DATABASE_CREATE_3 = "create table " + DATABASE_TABLE_3 + "(" + BROAD_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, " + BROADCAST_TYPE + " integer," + BROADCAST_FROM + " TEXT," + BROADCAST_MESSAGE + " TEXT," + BROADCAST_DATE + " date," + BROADCAST_LOCATION_LONG + " float, "+ BROADCAST_LOCATION_LAT + " float, "+ BROADCAST_LOCATION_LOCAL + " TEXT, " + BROADCAST_REACH + " TEXT, "+ BROADCAST_STATUS  + " TEXT );";
				Log.e("CREATE 3", DATABASE_CREATE_3);
				db.execSQL(DATABASE_CREATE_3);
				
				String DATABASE_CREATE_4 = "create table " + DATABASE_TABLE_4 + "(" + LOC_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, " + LOC_LONGI + " float," + LOC_LATI + " float," + LOC_DATE + " TEXT);";
				db.execSQL(DATABASE_CREATE_4);
				
				String DATABASE_CREATE_5 = "create table " + DATABASE_TABLE_5 + "(" + OPTION_ID + " integer PRIMARY KEY AUTOINCREMENT UNIQUE, " + OPTION_IS_RECIEVING_BROADCAST + " integer, " + OPTION_IS_BROADCAST_TIMER_TICKING +" integer, "+ OPTION_IS_ACCOUNT_FAILED_TO_VALIDATE +" integer, "+ OPTION_DISTANCE_PREF+ " TEXT);";
				db.execSQL(DATABASE_CREATE_5);
				
				String DATABASE_TABLE_5_INSERT_DEFAULT = "INSERT INTO " +  DATABASE_TABLE_5 + "(" 
						+ OPTION_IS_RECIEVING_BROADCAST +  " ," +
						OPTION_IS_BROADCAST_TIMER_TICKING  + " ," +
						OPTION_IS_ACCOUNT_FAILED_TO_VALIDATE + ", " + OPTION_DISTANCE_PREF + " ) VALUES  ("+ 0 + " ," + 0 + "," + 0 + "," + 100 + ");";
				
				db.execSQL(DATABASE_TABLE_5_INSERT_DEFAULT);
				Log.e(TAG, "Database tables created: "+ DATABASE_TABLE_5_INSERT_DEFAULT);
		  }
		  
		  @Override
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		   // TODO Auto-generated method stub
		  }
		 }
	
	/** Returns all the customers in the table */
	public Cursor getAllBroadCast(int limiter){   
		 String[] columns = new String[]{BROAD_ID, BROADCAST_FROM, BROADCAST_DATE,BROADCAST_LOCATION_LOCAL, BROADCAST_MESSAGE,  BROADCAST_REACH, BROADCAST_TYPE
			};
	  	  	//SQLiteDatabase db = sqLiteDatabase.getWritableDatabase();
	  	  	Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE_3, columns,
	  	  		BROADCAST_STATUS + "=?", new String[] { "1" }, null, null, BROADCAST_DATE + " desc ",limiter +"");
	  	  	//Log.e("CURSOR REQ","TOUCHED REQUERY");
			return cursor;
	}
	
	 public SQLiteHandler openToRead() throws android.database.SQLException {
		  sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		  sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		  return this; 
		 }
		 
		 public SQLiteHandler openToWrite() throws android.database.SQLException {
		  sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		  sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		  return this; 
		 }
		 
		 public void close(){
		  sqLiteHelper.close();
		 }

	/*public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mDB = getWritableDatabase();
	}*
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}*/

	public void updateBroadcastDist(String val) {
				//SQLiteDatabase db = this.getReadableDatabase();
				ContentValues contentValues = new ContentValues();
				contentValues.put(OPTION_DISTANCE_PREF, val);
				int succ = sqLiteDatabase.update(DATABASE_TABLE_5, contentValues, OPTION_ID + "=" + 1, null);
				Log.e("HI", succ + " : " + val);
	}
	
	public String getBroadcastDist() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + OPTION_DISTANCE_PREF + " from " + DATABASE_TABLE_5 + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last+"";
	}
		 
	public void updateBroadcasting(int val) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(OPTION_IS_RECIEVING_BROADCAST, val);
		sqLiteDatabase.update(DATABASE_TABLE_5, contentValues, OPTION_ID + "=" + 1, null);
	}
	public void updateBroadcastTicker(int val) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(OPTION_IS_BROADCAST_TIMER_TICKING, val);
		sqLiteDatabase.update(DATABASE_TABLE_5, contentValues, OPTION_ID + "=" + 1, null);
	}
	
	public String getBroadcastTickerStatus() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + OPTION_IS_BROADCAST_TIMER_TICKING + " from " + DATABASE_TABLE_5 + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last+"";
	}
	
	public String checkAccountValidate() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + OPTION_IS_ACCOUNT_FAILED_TO_VALIDATE + " from " + DATABASE_TABLE_5 + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last+"";
	}
	
	public void updateAccountValidate(int val) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(OPTION_IS_ACCOUNT_FAILED_TO_VALIDATE, val);
		sqLiteDatabase.update(DATABASE_TABLE_5, contentValues, OPTION_ID + "=" + 1, null);
	}
	
	public String getBroadcastingStatus() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + OPTION_IS_RECIEVING_BROADCAST + " from " + DATABASE_TABLE_5 + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		Log.e("B STATUS",last + "");
		cursor.close();
		return last+"";
	}
	
	
	public void deleteBroadcastPostStatus(String val) {
		//SQLiteDatabase db = this.getReadableDatabase();
		Log.e(TAG, "Deleted from sqlite "+ val);
		ContentValues contentValues = new ContentValues();
		contentValues.put(BROADCAST_STATUS, "0");
		sqLiteDatabase.update(DATABASE_TABLE_3, contentValues, BROAD_ID + "=" + val, null);
	}
/*	
	public Cursor getAllBroadCast(){   
      

	}*/
	
	public void deleteAllPeople() {
		//SQLiteDatabase db = this.getWritableDatabase();
		sqLiteDatabase.delete(DATABASE_TABLE_2, null, null);
		//sqLiteDatabase.close();
		Log.d(TAG, "Deleted all user info from sqlite");
	}
	/*@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
		onCreate(db);
	}*/
	
	public int getUnReadMsgCount(long id) {
		
		Cursor cursor = sqLiteDatabase.query(
				TABLE_MESSAGES, 
				new String[]{MSG_LEFT, MSG_BODY, MSG_SUCCESS, MSG_DATE, MSG_IS_UNREAD}, 
				MSG_USER_ID+"=? AND "+MSG_IS_UNREAD+"=?", new String[]{Long.toString(id), Boolean.toString(true)}, null, null, null);
		int count = 0;
		if(cursor.moveToFirst()){
			do{
				
				++count;
				
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		
		return count;
	}
	
	public void markMessageAsReadOffline(long correspondentId) {
		
		ContentValues values = new ContentValues();
		
		values.put(MSG_IS_UNREAD, ""+false);
		
		long id = sqLiteDatabase.update(TABLE_MESSAGES, values, MSG_CORRESPONDENT_ID+"= ?", new String[]{Long.toString(correspondentId)});
		
		
		
	}
	
	public Map<String, String> downloadLatestMsgOffline(long userId, long correpondentId) {
		
		
		Cursor cursor = sqLiteDatabase.query(
				TABLE_MESSAGES, 
				new String[]{MSG_LEFT, MSG_BODY, MSG_SUCCESS, MSG_DATE, MSG_IS_UNREAD}, 
				MSG_USER_ID + "= ? AND "+MSG_CORRESPONDENT_ID + "= ?", 
				new String[]{Long.toString(userId), Long.toString(correpondentId)}, null, null, null);
		Map<String, String> map = new HashMap<String, String>();
		if(cursor.moveToLast()){
			String left 		= cursor.getString(cursor.getColumnIndex(MSG_LEFT));
			String msg			= cursor.getString(cursor.getColumnIndex(MSG_BODY));
			String success		= cursor.getString(cursor.getColumnIndex(MSG_SUCCESS));
			String date			= cursor.getString(cursor.getColumnIndex(MSG_DATE));
			String isUnread			= cursor.getString(cursor.getColumnIndex(MSG_IS_UNREAD));
			
			map.put(MSG_LEFT, left);
			map.put(MSG_BODY, msg);
			map.put(MSG_SUCCESS, success);
			map.put(MSG_DATE, date);
			map.put(MSG_IS_UNREAD, isUnread);
			
			L.debug("SQLiteHandler, getting last received msg of correspondent id: "+correpondentId+", isUnread: "+isUnread);
		}
		
		cursor.close();
		return map;
	}
	
	public List<Correspondent> downloadAllCorrespondents() {
	
		
		Log.e("SQLiteHandler", "getting message senders");
		
		
		Cursor cursor = sqLiteDatabase.query(
				TABLE_CORRESPONDENTS, 
				new String[]{CORRESPONDENT_ID, CORRESPONDENT_USERNAME, CORRESPONDENT_EMAIL, CORRESPONDENT_FNAME}, 
				null, null, null, null, null);
		
		List<Correspondent> correspondents = new ArrayList<Correspondent>();
		if(cursor.moveToFirst()){
			do{
				long userId = cursor.getLong(cursor.getColumnIndex(CORRESPONDENT_ID));
				String username = cursor.getString(cursor.getColumnIndex(CORRESPONDENT_USERNAME));
				String email = cursor.getString(cursor.getColumnIndex(CORRESPONDENT_EMAIL));
				String fname = cursor.getString(cursor.getColumnIndex(CORRESPONDENT_FNAME));
				Log.e("SQLiteHandler",username + ", "+email+", "+fname);
				Correspondent correspondent = new Correspondent(userId, username, email, fname);
				correspondents.add(correspondent);
			}while(cursor.moveToNext());
		}
		
		//Log.e("B STATUS",last + "");
		cursor.close();
		
		return correspondents;
		
	}
	
	public List<OneComment> downloadMessages(long id) {
		Log.e("SQLiteHandler", "getting messages");
		Cursor cursor = sqLiteDatabase.query(
				TABLE_MESSAGES, 
				new String[]{MSG_LEFT, MSG_BODY, MSG_SUCCESS, MSG_DATE, MSG_IS_UNREAD}, 
				MSG_CORRESPONDENT_ID + "= '"+id+"'", 
				null, null, null, null);
		List<OneComment> conversation = new ArrayList<OneComment>();
		
		if(cursor.moveToFirst()){
			do{
				
				String left 		= cursor.getString(cursor.getColumnIndex(MSG_LEFT));
				String msg			= cursor.getString(cursor.getColumnIndex(MSG_BODY));
				String success		= cursor.getString(cursor.getColumnIndex(MSG_SUCCESS));
				String date			= cursor.getString(cursor.getColumnIndex(MSG_DATE));
				String isUnread			= cursor.getString(cursor.getColumnIndex(MSG_IS_UNREAD));
				
				OneComment comment 	= new OneComment(Boolean.parseBoolean(left), msg, Boolean.parseBoolean(success), date, Boolean.parseBoolean(isUnread));
				L.debug(id+", left: "+comment.left+", msg: "+comment.comment+", success: "+comment.success);
				conversation.add(comment);
				
			}while(cursor.moveToNext());
		}
		cursor.close();
		return conversation;
		
	}
	
	
	
	public Correspondent downloadCorrespondentByEmail(String email) {
		
		Cursor cursor = sqLiteDatabase.query(
				TABLE_CORRESPONDENTS, 
				new String[]{CORRESPONDENT_ID, CORRESPONDENT_USERNAME, CORRESPONDENT_EMAIL, CORRESPONDENT_FNAME}, 
				CORRESPONDENT_EMAIL+" = ?", 
				new String[]{ email}, 
				null, null, null);
		Correspondent correspondent = null;
		if(cursor.moveToFirst()){
			 
			long id = cursor.getInt(cursor.getColumnIndex(CORRESPONDENT_ID));
			String username = cursor.getString(cursor.getColumnIndex(CORRESPONDENT_USERNAME));
			String fname = cursor.getString(cursor.getColumnIndex(CORRESPONDENT_FNAME));
			
			correspondent =  new Correspondent(id, username, email, fname);
		}
		cursor.close();
		return correspondent;
		
	}
	
	public long saveCorrespondentOffline(String username, String email, String fname) {
		ContentValues values = new ContentValues();
		values.put(CORRESPONDENT_USERNAME, username);
		values.put(CORRESPONDENT_EMAIL, email);
		values.put(CORRESPONDENT_FNAME, fname);
		
		long id = sqLiteDatabase.insert(TABLE_CORRESPONDENTS, null, values);
		Log.e(TAG, "new correspondent inserted into sqlite: " + id + " : " + fname);
		return id;
	}
	
	public void saveMessageOffline(long userId, long correspondentId,  boolean left, String comment, boolean success, String date, boolean isUnread){
		ContentValues values = new ContentValues();
		
		
		//String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "(" + MSG_ID + " INTEGER PRIMARY KEY, "+ MSG_CORRESPONDENT_ID + " INTEGER, "+  MSG_LEFT + " TEXT, " +   MSG_BODY + " TEXT," +   MSG_SUCCESS + " TEXT," + MSG_DATE + " TEXT );";
		values.put(MSG_USER_ID, userId);
		values.put(MSG_CORRESPONDENT_ID, correspondentId);
		values.put(MSG_LEFT, ""+left);
		values.put(MSG_BODY, comment);
		values.put(MSG_SUCCESS, ""+success);
		values.put(MSG_DATE, date);
		values.put(MSG_IS_UNREAD, ""+isUnread);
	
		long id = sqLiteDatabase.insert(TABLE_MESSAGES, null, values);
		
		L.debug("SqliteHandler, new msg inserted into sqlite: " + correspondentId + " left: "+left+", msg: "+comment+", success: "+success+", isUnread: "+isUnread);
	}
	
	public void insertNearbyUser(String userid, String username, int distance, String fname, String lname, String age, String gender, String looking_for, String date_seen, int shown, String about_me, String looking_type, String status, String email) {
	//	SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PROFILE_USER_ID, userid);
		values.put(PROFILE_USERNAME, username);
		values.put(PROFILE_DISTANCE, distance);
		values.put(PROFILE_FNAME, fname);
		values.put(PROFILE_LNAME, lname);
		values.put(PROFILE_AGE, age);
		values.put(PROFILE_GENDER, gender);
		values.put(PROFILE_LOOKING_FOR, looking_for);
		values.put(PROFILE_DATE_SEEN, date_seen);
		values.put(PROFILE_SHOWN, shown);
		values.put(PROFILE_ABOUTME, about_me);
		values.put(PROFILE_LOOKING_TYPE, looking_type);
		values.put(PROFILE_STATUS, status);
		values.put(PROFILE_EMAIL, email);
		long id = sqLiteDatabase.insert(DATABASE_TABLE_2, null, values);
		//db.close();
		Log.e(TAG, "Nearby user inserted into sqlite: " + id + ",fname: " + fname+",lname: " + lname+", username: "+username);
	}
	public void insertLocation(float longitude, float latitude) {
		String dateNow = requestLocalDate();
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LOC_LONGI, longitude);
		values.put(LOC_LATI, latitude);
		values.put(LOC_DATE, dateNow);
		long id = sqLiteDatabase.insert(DATABASE_TABLE_4, null, values);
		//db.close();
		Log.e(TAG, "Broadcast inserted to sqlite: message " + id);
	}
	
	public void downloadPersonalBroadcasts() {
//		sqLiteDatabase.query(
//				table, 
//				columns, 
//				selection, 
//				selectionArgs, 
//				null, nu, orderBy)
	}
	
	public void insertBroadcast(int broad_type, String broad_from, String broad_message, float loc_long, float loc_lat, String local, int reach) {
		String dateNow = requestDate();
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		Log.e("BROADCASTING:","BROAD TYPE: "+broad_type+ " BROAD FROM: "+broad_from);
		values.put(BROADCAST_TYPE, broad_type);
		values.put(BROADCAST_FROM, broad_from+ "");
		values.put(BROADCAST_MESSAGE, broad_message + "");
		values.put(BROADCAST_DATE, dateNow);
		values.put(BROADCAST_LOCATION_LOCAL, local +"");
		values.put(BROADCAST_LOCATION_LONG, loc_long);
		values.put(BROADCAST_LOCATION_LAT, loc_lat);
		values.put(BROADCAST_REACH, reach);
		values.put(BROADCAST_STATUS, "1");
		
		long id = sqLiteDatabase.insert(DATABASE_TABLE_3, null, values);
		//db.close();
		Log.e(TAG, "Broadcast inserted to sqlite: message " + broad_message+ " TYPE:"+ broad_type);
	}
	public void formatBroadcast() {
		//SQLiteDatabase db = this.getWritableDatabase();
		sqLiteDatabase.delete(DATABASE_TABLE_3, null, null);
		//db.close();
		Log.d(TAG, "Deleted all broadcast on sqlite");
	}
	public String getExistingOnDBUsers() {
		String selectQuery = "SELECT " + PROFILE_USER_ID + " FROM " + DATABASE_TABLE_2;
		String ids = ".";
		//SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				ids = ids + "." + cursor.getString(0);
				Log.e(TAG, "RETRIEVED PATTERN " + ids);
			} while (cursor.moveToNext());
			cursor.close();
		}
		ids = ids + ".";
		//db.close();
		return ids;
	}
	public String getUserById(String userId) {
		
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery( "SELECT " + PROFILE_FNAME + " FROM " + DATABASE_TABLE_2 + " WHERE " + userId + " = '"+ PROFILE_USER_ID+ "'", null);
		try{
			cursor.moveToFirst();
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	public String getUserByUsername(String userId) {
		
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery( "SELECT " + PROFILE_FNAME + " FROM " + DATABASE_TABLE_2 + " WHERE " + PROFILE_USERNAME  + " = '"+userId + "'", null);
		try{
			cursor.moveToFirst();
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	
	public Cursor queueAll(){
		Log.e("SQLite","Queued broadcast objects");
		  String[] columns = new String[]{BROAD_ID, BROADCAST_FROM, BROADCAST_DATE,  BROADCAST_LOCATION_LOCAL, BROADCAST_MESSAGE, BROADCAST_REACH, BROADCAST_TYPE
					};
			//SQLiteDatabase db = this.getWritableDatabase();
		  Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE_3, columns,
		    null, null, null, null, null);
		  return cursor;

		 }
	
	public void updateUser(String userid, String username, int distance, String fname, String lname, String age, String gender, String looking_for, String date_seen, int shown, String about_me, String looking_type, String status, String email) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(PROFILE_DISTANCE, distance);
		contentValues.put(PROFILE_USERNAME, username);
		contentValues.put(PROFILE_FNAME, fname);
		contentValues.put(PROFILE_LNAME, lname);
		contentValues.put(PROFILE_AGE, age);
		contentValues.put(PROFILE_GENDER, gender);
		contentValues.put(PROFILE_LOOKING_FOR, looking_for);
		contentValues.put(PROFILE_DATE_SEEN, date_seen);
		contentValues.put(PROFILE_SHOWN, shown);
		contentValues.put(PROFILE_ABOUTME, about_me);
		Log.e("upfate", looking_type);
		contentValues.put(PROFILE_LOOKING_TYPE, looking_type);
		contentValues.put(PROFILE_STATUS, status);
		contentValues.put(PROFILE_EMAIL, email);
		sqLiteDatabase.update(DATABASE_TABLE_2, contentValues, PROFILE_USER_ID + "=" + userid, null);
	}
	public void updateUserPersonal(String fname, String age, String gender) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FNAME, fname);
		contentValues.put(KEY_AGE, age);
		contentValues.put(KEY_GENDER, gender);
		contentValues.put(KEY_UPDATE_BASIC, "1");
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully updated key on db with fname #" + fname);
	}
	public void updateUserPreference(String looking_for, String sexOrien, String strGenderPref, String strRelStat, String strSBOrien) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_LOOKING_FOR_STATUS, looking_for);
		contentValues.put(KEY_SEX_ORIEN, sexOrien);
		contentValues.put(KEY_LOOKING_FOR_GENDER, strGenderPref);
		contentValues.put(KEY_REL_STATUS, strRelStat);
		contentValues.put(KEY_ORIEN_TO_SHOW, strSBOrien);
		contentValues.put(KEY_UPDATE_PREF, "1");
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully updated key on db with ltype #" + looking_for);
	}
	public void updateUserSettings(String strIndieChat, String strComChat) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_PRIVACY_IN_CHAT, strIndieChat);
		contentValues.put(KEY_PRIVACY_COM_CHAT, strComChat);
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully updated key on db with strIndieChat #" + strIndieChat);
	}
	public void updatePass(String pass) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_PASS, pass);
		contentValues.put(KEY_UPDATE_PASS, "1");
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully updated key on db with pass #" + pass);
	}
	private String requestDate() {
		Date date = new Date();
		DateUtils du;
		du = new DateUtils();
		String dateNow = du.convertDateToString(date);
		return dateNow;
	}
	private String requestLocalDate() {
		Date date = new Date();
		DateUtils du;
		du = new DateUtils();
		String dateNow = du.convertDateToStringToLocalTime(date);
		return dateNow;
	}
	public void updateUploadStatus(String profToUpdate) {
		final String dateNow = requestDate();
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		if (profToUpdate.equals("profile_basic")) {
			contentValues.put(KEY_UPDATE_BASIC, "0");
		} else if (profToUpdate.equals("profile_pref")) {
			contentValues.put(KEY_UPDATE_PREF, "0");
		} else if (profToUpdate.equals("profile_pass")) {
			contentValues.put(KEY_UPDATE_PASS, "0");
		} else if (profToUpdate.equals("profile_stat")) {
			contentValues.put(KEY_UPDATE_STATUS_MES, "0");
		}
		contentValues.put(KEY_LAST_UPDATE, dateNow);
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully renewed status on db with dateNow #" + dateNow);
	}
	public void updateUserStatus(String status) {
		//SQLiteDatabase db = this.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_ABOUTME, status);
		contentValues.put(KEY_UPDATE_STATUS_MES, "1");
		sqLiteDatabase.update(TABLE_LOGIN, contentValues, " 1 = 1", null);
		Log.e("DB Success", "Successfully updated key on db with status #" + status);
	}
	public boolean addUser(String name, String email, String uid, String created_at, String pass) {
		Account ac = new Account();
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_EMAIL, email);
		values.put(KEY_UTYPE, uid);
		values.put(KEY_PASS, pass);
		values.put(KEY_CREATED_AT, created_at);
		values.put(KEY_LOOKING_FOR_STATUS, "FR");
		values.put(KEY_SEX_ORIEN, "UN");
		values.put(KEY_LOOKING_FOR_GENDER, "SB");
		values.put(KEY_ORIEN_TO_SHOW, "1-1-1-1-1-1");
		values.put(KEY_REL_STATUS, "UN");
		values.put(KEY_PRIVACY_IN_CHAT, "EV");
		values.put(KEY_PRIVACY_COM_CHAT, "RE");
		values.put(KEY_UPDATE_BASIC, "0");
		values.put(KEY_UPDATE_PREF, "0");
		values.put(KEY_UPDATE_PASS, "0");
		values.put(KEY_UPDATE_STATUS_MES, "0");
		Date date = new Date();
		DateUtils du;
		du = new DateUtils();
		final String dateNow = du.convertDateToString(date);
		values.put(KEY_LAST_UPDATE, dateNow);
		long id = sqLiteDatabase.insert(TABLE_LOGIN, null, values);
		//db.close();
		Log.d(TAG, "New user inserted into sqlite: " + id);
		return true;
	}
	public String getServerUpdateBasic() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_UPDATE_BASIC + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getServerUpdatePref() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_UPDATE_PREF + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getServerUpdatePass() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_UPDATE_PASS + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getServerUpdateStatMess() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_UPDATE_STATUS_MES + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getLastUpdate() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_LAST_UPDATE + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getLoggedInID() {
		String last;
	//	SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_UTYPE + ", " + KEY_ID + ", " + KEY_NAME + ", " + KEY_EMAIL + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		Log.e("keyid", cursor.getString(1));
		Log.e("KEY_UTYPE", cursor.getString(0));
		Log.e("KEY_NAME", cursor.getString(2));
		Log.e("KEY_EMAIL", cursor.getString(3));
		cursor.close();
		return last;
	}
	
	
	public String getUsername() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_NAME + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		try{
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	public String getFName() {
		String last="";
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_FNAME + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		if(cursor.moveToFirst()){
			last = cursor.getString(0);
		}
		
		cursor.close();
		return last;
	}
	public String getEmail() {
		String last = "";
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_EMAIL + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		if(cursor.moveToFirst()){
			last = cursor.getString(0);
		}
		
		cursor.close();
		return last;
	}
	public String getPass() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_PASS + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		try{
		last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		Log.e("pass 1", last + " .xx");
		cursor.close();
		return last;
	}
	public String getName() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_FNAME + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last;
	}
	public String getGender() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_GENDER + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getBDate() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_AGE + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getStatusMessage() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_ABOUTME + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getLookingForStatus() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_LOOKING_FOR_STATUS + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getSexOrien() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_SEX_ORIEN + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getLookingGender() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_LOOKING_FOR_GENDER + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getOrientationToShow() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_ORIEN_TO_SHOW + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getRelStatus() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_REL_STATUS + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getIndiSetChatPrivacy() {
		String last;
	//	SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_PRIVACY_IN_CHAT + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getComSetChatPrivacy() {
		String last;
	//	SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + KEY_PRIVACY_COM_CHAT + " from " + TABLE_LOGIN + " DESC LIMIT 1 ", null);
		cursor.moveToFirst();
		last = cursor.getString(0);
		cursor.close();
		return last + "";
	}
	public String getLocationLongitude() {
		String last;
		//SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + LOC_LONGI + " from " + DATABASE_TABLE_4 + " ORDER BY "+ LOC_ID +" DESC LIMIT 1 ", null);
		try{
			cursor.moveToFirst();
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	
	public String getLocationLatitude() {
		String last;
	//	SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + LOC_LATI + " from " + DATABASE_TABLE_4 + " ORDER BY "+ LOC_ID +" DESC LIMIT 1 ", null);
		try{
			cursor.moveToFirst();
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	public String getLocationDateUpdate() {
		String last;
	//	SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery("select " + LOC_DATE + " from " + DATABASE_TABLE_4 + " ORDER BY "+ LOC_ID +" DESC LIMIT 1 ", null);
		try{
			cursor.moveToFirst();
			last = cursor.getString(0);
		}
		catch(Exception e){
			last = "";
		}
		cursor.close();
		return last;
	}
	public ArrayList < Users > getNearByUserDetails() {
		ArrayList < Users > userlist = new ArrayList < Users > ();
		String selectQuery = "SELECT * FROM " + DATABASE_TABLE_2;
	//	SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Users us = new Users();
			
				us.setId(cursor.getInt(cursor.getColumnIndex(PROFILE_ID)));
				us.setUserId(cursor.getInt(cursor.getColumnIndex(PROFILE_USER_ID)));
				us.setUserName(cursor.getString(cursor.getColumnIndex(PROFILE_USERNAME)));
				us.setDistance(cursor.getInt(cursor.getColumnIndex(PROFILE_DISTANCE)));
				us.setFName(cursor.getString(cursor.getColumnIndex(PROFILE_FNAME)));
				us.setLName(cursor.getString(cursor.getColumnIndex(PROFILE_LNAME)));
				us.setAge(cursor.getString(cursor.getColumnIndex(PROFILE_AGE)));
				us.setGender(cursor.getString(cursor.getColumnIndex(PROFILE_GENDER)));
				us.setLookingFor(cursor.getString(cursor.getColumnIndex(PROFILE_LOOKING_FOR)));
				us.setDateSeen(cursor.getString(cursor.getColumnIndex(PROFILE_DATE_SEEN)));
				us.setShown(cursor.getString(cursor.getColumnIndex(PROFILE_SHOWN)));
				us.setAboutMe(cursor.getString(cursor.getColumnIndex(PROFILE_ABOUTME)));
				us.setLookingType(cursor.getString(cursor.getColumnIndex(PROFILE_LOOKING_TYPE)));
				us.setStatus(cursor.getString(cursor.getColumnIndex(PROFILE_STATUS)));
				us.setEmail(cursor.getString(cursor.getColumnIndex(PROFILE_EMAIL)));
				userlist.add(us);
				
				L.debug("SqliteHelper, us.getFName() "+us.getFName());
			} while (cursor.moveToNext());
			cursor.close();
		} else {
			cursor.close();
		}
		return userlist;
	}
	public HashMap < String, String > getUserDetails() {
		HashMap < String, String > user = new HashMap < String, String > ();
		String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
	//	SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
			user.put("email", cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
			user.put("uid", cursor.getString(cursor.getColumnIndex(KEY_ID)));
			user.put("created_at", cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));
		}
		cursor.close();
		//db.close();
		Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
		return user;
	}
	public int getRowCount() {
		String countQuery = "SELECT * FROM " + TABLE_LOGIN;
	//	SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
	//	db.close();
		cursor.close();
		return rowCount;
	}
	public int getBraodCastRowCount() {
		String countQuery = "SELECT * FROM " + DATABASE_TABLE_3;
	//	SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		Log.e("BROADCAST COUNT", rowCount +"");
		//db.close();
		cursor.close();
		return rowCount;
	}
	public void deleteUsers() {
	//	SQLiteDatabase db = this.getWritableDatabase();
		sqLiteDatabase.delete(TABLE_LOGIN, null, null);
		sqLiteDatabase.delete(DATABASE_TABLE_3, null, null);
		sqLiteDatabase.delete(DATABASE_TABLE_4, null, null);
	//	db.close();
		Log.d(TAG, "Deleted all user info from sqlite");
	}
}