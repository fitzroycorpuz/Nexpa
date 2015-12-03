package com.lpoezy.nexpa.activities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.appyvet.rangebar.RangeBar;
import com.devspark.appmsg.AppMsg;
import com.devspark.appmsg.AppMsg.Style;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Users;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.LocationName;
import com.lpoezy.nexpa.utility.StringFormattingUtils;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.utility.MyLocation;
import com.lpoezy.nexpa.utility.MyLocation.LocationResult;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GroupChatHomeActivity extends AppCompatActivity implements OnItemClickListener{
	Button btnStartChat;
	Button btnCancel;
	Button dialogButtonOK;
	EditText edBroad;
	String mNickName;
	SQLiteHandler db;
	Message msg;
	String strUser;
	int repeater;
	int interactor;
	LinearLayout lnBroadcast;
	LinearLayout lnBroadcastMini;
	LinearLayout lnEmpty;
	LinearLayout lnBroadcastExist;
	LinearLayout btnReply;
	LinearLayout btnFave;
	LinearLayout btnDel;
	TextView txBroad;
	Dialog dialogBroadcast;
	EditText btnOptions;
	Button btnRefresher;
	static TextView txtConnection;
	static Animation animFade;
	Animation animTremor;
	Animation in ;
	Animation out;
	
	TextView txtReply;
	TextView txtUser;
	
	DateUtils du;
	ListView mListView;
	SimpleCursorAdapter mAdapter;
	Handler mHandler;
	Handler mNotifier;
	Handler mRepeater;
	Runnable mStatusChecker;
	String senderEdited;
	String messageToSend;
	boolean isReceivingBroadcast;
	Cursor crBroadcast;
	private XMPPConnection connection;
	boolean flag_loading;
	int limit_loader;
	int broadCount;
	int limit_listen_maker;
	
	ImageView btnSearch;
	ImageView btnPost;
	ImageView btnDistance;
	
	int dst;
	
	Dialog dialogPref;
	RangeBar rbDistance;
	String distTick = "";
	
	private PacketListener packetListener;
	
	public static String displayName(String fname, String user) {
		if ((fname.equals("")) || (fname == null) || (fname.equals("null"))) {
			return user;
		} else {
			return fname;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onPause();
		db.updateBroadcastTicker(1);
		Log.e("WINDOW", "DESTROY ");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.e("WINDOW", "PAUSED ");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("WINDOW", "RESUME ");
		try{
		((SimpleCursorAdapter) mListView.getAdapter()).notifyDataSetChanged();
		}
		catch(Exception e){}
		
		makeRepeatingChecker();
		if (interactor < 50) {
			resetInteractor();
		}
		
		if (limit_listen_maker == 0){
			limit_listen_maker = 3;
			resetInteractor();
			interactor = 31;
		}
		dst = 100;
		try {
			dst = Integer.parseInt(db.getBroadcastDist());
		} catch (Exception e) {
			dst = 100;
		}
		
			
	}
	
	private void makeRepeatingChecker() {
		
		if (db.getBroadcastTickerStatus().equals("0")) {
			db.updateBroadcastTicker(1);
			mRepeater = new Handler();
			mStatusChecker = new Runnable() {
				@Override
				public void run() {
					checkConnection();
					mRepeater.postDelayed(mStatusChecker, 10000);
				}
			};
			mStatusChecker.run();
		}
	}
	
	private void resetInteractor(){
		
		try {
			//packet listener closes on idle at (76 (24 x 30 seconds), so close it and create another one to make the listener alive
			connection.removePacketListener(packetListener);
			packetListener = null;
			interactor = 99;
			
			Log.e("XMPP Interactor", "Interactor killed old packetlistener, counter has been reset.");
			
		} catch (Exception e) { }
	}
	
	@Override
	public void onUserInteraction() {
		interactor = 99;
		limit_listen_maker = 3;
	}
	
	private void checkConnection() {
		Log.e("XMPP CHAT limit_listen_maker", limit_listen_maker+ "");
		if (limit_listen_maker > 0){
			
		connection = XMPPLogic.getInstance().getConnection();
		if (interactor == 30) {
			resetInteractor();
			limit_listen_maker--;
		}
		if ((connection == null) || (!connection.isConnected())) {
			Log.e("XMPP CHAT", "Not connected");
			
			Account ac = new Account();
			SQLiteHandler db = new SQLiteHandler(getApplicationContext());
			db.openToWrite();
			ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), null);
			
			Log.e("XMPP CHAT", "Logging in...");
			 updateStatusText(1);
			db.updateBroadcasting(0);
			try {
				connection.removePacketListener(packetListener);
				packetListener = null;
			} catch (Exception e) {}
		} else {
			 updateStatusText(2);
			Log.e("XMPP CHAT", "Connected... ");
			crBroadcast = db.getAllBroadCast(limit_loader);
			mNotifier.sendEmptyMessage(2);
			createPacketListener();
		}
		}
	}
	
	private void createPacketListener() {
		if (packetListener == null) {
			packetListener = new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					final Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());
						Log.e("XMPPChatDemoActivity", "Text Recieved: " + message.getBody() + " from " + fromName);
						String senderRaw = message.getFrom();
						String[] arr = senderRaw.split("@");
						String senderEdited = arr[0];
						messageToSend = message.getBody();
						String formattedMsg = StringFormattingUtils.getBroadcastChatEquivalentMes(messageToSend);
						String formattedLoc = StringFormattingUtils.getBroadcastChatEquivalentLocation(messageToSend);
						db.insertBroadcast(2, senderEdited + "", formattedMsg , longitude, latitude, formattedLoc, 0);
						mNotifier.sendEmptyMessage(2);
					}
				}
			};
			Log.e("XMPP STATUS", "Adding packet listener...");
			PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
			db.updateBroadcasting(1);
			connection.addPacketListener(packetListener, filter);
		}
		interactor--;
		Log.e("XMPP interactor",interactor + "");
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_chat_home);
		Log.e("WINDOW", "CREATE ");
		
		///Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
	   // setSupportActionBar(myToolbar);
	    
		in = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in_r);
		
		out = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out_r);
		limit_listen_maker = 3;
		interactor = 100;
		db = new SQLiteHandler(this);
		db.openToWrite();
		du = new DateUtils();
		getNewLoc();
		repeater = 0;
		broadCount = 0;
		limit_loader = 10;
		broadCount = db.getBraodCastRowCount();
		
		lnBroadcast = (LinearLayout) findViewById(R.id.lnBroadcast);
		lnEmpty = (LinearLayout) findViewById(R.id.lnBroadcastEmpty);
		lnBroadcastExist = (LinearLayout) findViewById(R.id.lnBroadcastExist);
		//lnBroadcastExist.setOnClickListener(mBuyButtonClickListener);
		
		lnBroadcastMini = (LinearLayout) findViewById(R.id.lnBroadcastMini);
		dialogBroadcast = new Dialog(GroupChatHomeActivity.this);
		dialogBroadcast.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogBroadcast.setContentView(R.layout.activity_group_chat_main);
		
		edBroad = (EditText) dialogBroadcast.findViewById(R.id.txtBroadcast);
		btnCancel = (Button) dialogBroadcast.findViewById(R.id.btnClose);
		btnOptions = (EditText) findViewById(R.id.btnOptions);
		txtConnection = (TextView) findViewById(R.id.txt_broad_stat);
		btnOptions.setOnClickListener(buttonAddOnClickListener);
		btnOptions.setHintTextColor(getResources().getColor(R.color.white_smoke));
		mListView = (ListView) findViewById(R.id.listview);
		
		// mListView.setOnItemClickListener(this);
		 mListView.setOnItemClickListener(onItemClickListener);
		 btnSearch = (ImageView) findViewById(R.id.img_search);
		 btnPost = (ImageView) findViewById(R.id.img_megaphone);
		 btnDistance = (ImageView) findViewById(R.id.img_here);
		 
		/*btnReply = (LinearLayout) findViewById(R.id.btnReply);
		btnReply.setOnClickListener(new View.OnClickListener() {@Override
		public void onClick(View v) {
			//View parentRow = (View) v.getParent();
			//ListView listView = (ListView) parentRow.getParent();
			//final int position = listView.getPositionForView(parentRow);
			Log.e("XefweeMPP interactor","position" + "");
		}
		});*/
		
		//btnReply.setOnClickListener(myButtonClickListener);
		animFade =  AnimationUtils.loadAnimation(GroupChatHomeActivity.this, R.anim.anim_fade_in_r);
		animTremor =  AnimationUtils.loadAnimation(GroupChatHomeActivity.this, R.anim.anim_shake);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogBroadcast.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogBroadcast.getWindow().setAttributes(lp);
		
		
		btnDistance.setOnClickListener(new View.OnClickListener() {@Override
			public void onClick(View arg0) {
			dialogPref = new Dialog(GroupChatHomeActivity.this);
			dialogPref.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogPref.setContentView(R.layout.activity_profile_distance_settings);

			rbDistance = (RangeBar) dialogPref.findViewById(R.id.rbDistance);
			rbDistance.setRangeBarEnabled(false);
				dst = 100;
        		try{
        			dst = Integer.parseInt(db.getBroadcastDist());
        		}
        		catch (Exception e){
        			dst = 100;
        		}
                rbDistance.setSeekPinByValue(dst);

			rbDistance.setPinColor(getResources().getColor(R.color.EDWARD));
			rbDistance.setConnectingLineColor(getResources().getColor(R.color.EDWARD));
			rbDistance.setSelectorColor(getResources().getColor(R.color.EDWARD));
			rbDistance.setPinRadius(30f);
			rbDistance.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
				@Override
				public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex,
						String leftPinValue, String rightPinValue) {
					distTick = rightPinValue;
				}
			});

			Button dialogButton = (Button) dialogPref.findViewById(R.id.dialogButtonOK);
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					db.updateBroadcastDist(distTick);
					dst = Integer.parseInt(distTick);
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
		
		btnPost.setOnClickListener(new View.OnClickListener() {@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInputFromWindow(lnBroadcast.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
				openBroadcastDialog();
			}
		});
		
		btnSearch.setOnClickListener(new View.OnClickListener() {@Override
			public void onClick(View arg0) {
			
			Intent iv = new Intent(GroupChatHomeActivity.this,
                    SearchPostActivity.class);

            if (iv != null) {
            	iv.putExtra("TYPE", "hashtag"); 
            	
            	String txtSearch = btnOptions.getText().toString();
            	if (txtSearch.equals(null) || txtSearch.equals("")){
            		
            		btnOptions.startAnimation(animTremor);
            	}
            	else{
            		iv.putExtra("SEARCH", txtSearch); 
                    // startActivity(iv);
            		startActivity(iv);
            	}
            
            }
            }
		});
		
		lnBroadcast.setOnClickListener(new View.OnClickListener() {@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInputFromWindow(lnBroadcast.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
				openBroadcastDialog();
			}
		});
		
	
		
		showList();
	}
	
	TextView txtBroadId;
	String br_id;
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, final View arg1, int position,
				long arg3) {	
			 btnReply = (LinearLayout) arg1.findViewById(R.id.btnReply);
			 txtBroadId= (TextView) arg1.findViewById(R.id.broad_id);
			 br_id = txtBroadId.getText().toString();
			 
			 btnDel = (LinearLayout) arg1.findViewById(R.id.btnDelete);
			 btnDel.setOnClickListener(new View.OnClickListener() {
				 @Override
				 public void onClick(View arg0) {
					txtBroadId= (TextView) arg1.findViewById(R.id.broad_id);
					br_id = txtBroadId.getText().toString();
					promptYesNoDialog("Are you sure you want to delete this post?", 
							"This post will be deleted on your local memory.",
							GroupChatHomeActivity.this,"DEL_POST",br_id);
				}	
			 });
			 
			/* btnFave = (LinearLayout) arg1.findViewById(R.id.btnFavorite);
			 btnFave.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						 Log.e("cc","FAVE");
					};
				});*/
			// btnStarred = (LinearLayout) arg1.findViewById(R.id.btnReply);
			
			
			 btnReply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					txtReply = (TextView) arg1.findViewById(R.id.txtReply);
					String strReply = txtReply.getText().toString();
						if (strReply.equals("REPLY")){
							txBroad = (TextView) arg1.findViewById(R.id.broad_message);
							txtUser = (TextView) arg1.findViewById(R.id.broad_from_raw);
							TextView db = (TextView) arg1.findViewById(R.id.date_broad);
							String dBroad = db.getText().toString();
							
							Intent explicitIntent = new Intent(GroupChatHomeActivity.this,
									ChatActivity.class);
							explicitIntent.putExtra("INTENT_MESSAGE",txBroad.getText().toString());
							explicitIntent.putExtra("INTENT_MESSAGE_DATE", dBroad);
							explicitIntent.putExtra("INTENT_MESSAGE_TYPE","BROADCAST");
							explicitIntent.putExtra("username",txtUser.getText().toString());
							Log.e("111sdf", "XX"+txBroad.getText().toString());
							Log.e("111sdf", "XX"+txtUser.getText().toString());
							startActivity(explicitIntent);
							//finish();
						}
					//else{
					//	Log.e("yy","UNINTENT");
					//}
				};
			});
			 txtBroadId= (TextView) arg1.findViewById(R.id.broad_id);
			 br_id = txtBroadId.getText().toString();
		}
	};
	int broad_user_type = 0;
	String strReach;
	boolean setType;
	int incr = 0;
	private void showList(){
		
		crBroadcast = db.getAllBroadCast(limit_loader);
		Log.e("BROADCOUNT: ",broadCount + "");
		L.debug("BROADCOUNT, "+broadCount + "");
		if (broadCount != 0) {
			Log.e("BROADCOUNT showing: ",broadCount + "");
			L.debug("BROADCOUNT showing: "+broadCount + "");
			lnEmpty.setEnabled(false);
			lnEmpty.setVisibility(LinearLayout.GONE);
			lnBroadcast.setVisibility(LinearLayout.GONE);
			lnBroadcastMini.setVisibility(LinearLayout.VISIBLE);
			lnBroadcastExist.setEnabled(true);
			lnBroadcastExist.setVisibility(LinearLayout.VISIBLE);
			mAdapter = new SimpleCursorAdapter(this, R.layout.list_broadcast, crBroadcast, new String[] {
				SQLiteHandler.BROAD_ID, SQLiteHandler.BROADCAST_FROM, SQLiteHandler.BROADCAST_DATE, SQLiteHandler.BROADCAST_LOCATION_LOCAL, SQLiteHandler.BROADCAST_MESSAGE,  SQLiteHandler.BROADCAST_REACH,  SQLiteHandler.BROADCAST_TYPE,  SQLiteHandler.BROADCAST_TYPE,  SQLiteHandler.BROADCAST_TYPE, SQLiteHandler.BROADCAST_FROM
			}, new int[] {
					R.id.broad_id, R.id.broad_from, R.id.date_broad, R.id.location_local, R.id.broad_message, R.id.reach, R.id.txtReply, R.id.imgReply, R.id.btnReply, R.id.broad_from_raw
					}, 0);
			mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				 @Override
				public boolean setViewValue(View view, Cursor cursor, int column) {
					 
					
					String broadType = "";
					String statVal = "";
					broadType = cursor.getString(cursor.getColumnIndex("broad_type_of"));
					
					if (view.getId() == R.id.broad_from) {
						TextView tv = (TextView) view;
						if (broadType.equals("1")) {
							statVal = displayName(db.getFName() + "", db.getUsername());
							tv.setText(statVal);
							
								//if (setType == true){
									broad_user_type = 1;
								//	setType = false;
								//}
							return true;
						} else {
							//if (setType == true){
								broad_user_type = 0;
							//	setType = false;
							//}
							statVal = displayName(db.getUserByUsername(cursor.getString(cursor.getColumnIndex("broad_from"))) + "", cursor.getString(cursor.getColumnIndex("broad_from")));
							tv.setText(statVal);
							return true;
						}
					}
					
					if (view.getId() == R.id.date_broad) {
						TextView txv = (TextView) view;
						String dateFormatted = du.getMinAgo(cursor.getString(cursor.getColumnIndex("date_broad")));
						txv.setText(dateFormatted);
						return true;
					}
					
					/*if (view.getId() == R.id.reach) {
						TextView btnReach = (TextView) view;
						
						//btnReach.setText(strReach);
						return true;
					}*/
				
					if (view.getId() == R.id.txtReply){	
						strReach = (cursor.getString(cursor.getColumnIndex("reach")));
						if (strReach.equals("null")||strReach.equals("")){
							strReach = "0";
						}
						
						TextView txReply = (TextView) view;
						if (broad_user_type == 1){
							txReply.setText("REACHED " + strReach);
						}
						else{
							txReply.setText("REPLY");
							//  setType = true;
						}
						return true;
					}
					
					if (view.getId() == R.id.location_local){	
						String strLoc = cursor.getString(cursor.getColumnIndex("location_local"));
						if (strLoc.equals("null")||strLoc.equals("")){
							TextView txReply = (TextView) view;
						  txReply.setVisibility(TextView.GONE);
							
						}
						else{
							strLoc = "near "+ strLoc;
							TextView txReply = (TextView) view;
							txReply.setText(strLoc);
							txReply.setVisibility(TextView.VISIBLE);
							
						}
						return true;
					}
					
					if(view.getId() == R.id.imgReply){
						ImageView noteTypeIcon = (ImageView) view;
						if (broad_user_type == 1){
							noteTypeIcon.setBackgroundResource(R.drawable.btn_reach);
						}
						else{
							noteTypeIcon.setBackgroundResource(R.drawable.btn_reply);
						}
						return true;      
					}
					
					if(view.getId() == R.id.btnReply){
						LinearLayout noteTypeIcon = (LinearLayout) view;
						// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 100);
						if (broad_user_type == 1){
							// lp.weight = (float) 1;
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
		                            0, LayoutParams.WRAP_CONTENT , 1.2f);
							 noteTypeIcon.setLayoutParams(param);
						}
						else{
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
		                            0, LayoutParams.WRAP_CONTENT , 0.9f);
							
							 noteTypeIcon.setLayoutParams(param);
						}
						return true;      
					}
					return false;
				}
			
			});
			mListView.setAdapter(mAdapter);
			
			mListView.setOnScrollListener(new OnScrollListener() {

		        public void onScrollStateChanged(AbsListView view, int scrollState) {
		        	if (scrollState == 0){
		        		is_scrolled  = true;
		        		
		        	}
		        }

		        public void onScroll(AbsListView view, int firstVisibleItem,
		                int visibleItemCount, int totalItemCount) {
		        	if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
		            {
		            	//
		            	if (is_scrolled ){
		                if(flag_loading == false)
		                {
		                	is_scrolled = false;
		                    flag_loading = true;
		                    loadItems();	                
		                }
		            	}
		            }
		        }
		    });
		} else {
			lnEmpty.setEnabled(true);
			lnEmpty.setVisibility(LinearLayout.VISIBLE);
			lnBroadcast.setVisibility(LinearLayout.VISIBLE);
			lnBroadcastMini.setVisibility(LinearLayout.GONE);
			lnBroadcastExist.setEnabled(false);
			lnBroadcastExist.setVisibility(LinearLayout.GONE);
		}
		flag_loading = false;
		isReceivingBroadcast = false;
		mNotifier = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					/*mNotifier.postDelayed(new Runnable() {@Override
						public void run() {
							btnRefresher.performClick();
						}
					}, 10000);*/
				} else if (msg.what == 2) {
					crBroadcast.requery();
					try{
						mAdapter.changeCursor(crBroadcast);
					}
					catch (Exception ex){}
					//mAdapter.notifyDataSetChanged();
					
					try{
						((SimpleCursorAdapter) mListView.getAdapter()).notifyDataSetChanged();
					}
					catch (Exception ex){}	
				}
			}
		};
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {} 
				else if (msg.what == 2) {
					crBroadcast.requery();
					try {
						mAdapter.notifyDataSetChanged();
					} catch (Exception e) {}
				} else if (msg.what == 3) {
					limit_loader = limit_loader + 10;
					crBroadcast = db.getAllBroadCast(10);
					mAdapter.changeCursor(crBroadcast);
					mNotifier.sendEmptyMessage(2);
				} 
			}
		};
	}
	
	Button.OnClickListener buttonAddOnClickListener = new Button.OnClickListener() {@Override
		public void onClick(View arg0) {
			mHandler.sendEmptyMessage(3);
		}
	};
	boolean is_scrolled = false;
	private void loadItems(){
		if (limit_loader < db.getBraodCastRowCount()){
		limit_loader = limit_loader + 10;
		crBroadcast = db.getAllBroadCast(limit_loader);
		//crBroadcast.requery();
		mAdapter.changeCursor(crBroadcast);
		mNotifier.sendEmptyMessage(2);
		flag_loading = false;
		}
		
	}
	
	float ftLatitude = 0;
	float ftLongitude = 0;
	float latitude = 0;
	float longitude = 0;
	Button btnDialogButtonOK;
	ProgressBar pbBroadcast;
	private void openBroadcastDialog() {
		edBroad.setText("");
		dialogBroadcast.show();
		btnDialogButtonOK = (Button) dialogBroadcast.findViewById(R.id.dialogButtonOK);
		pbBroadcast = (ProgressBar) dialogBroadcast.findViewById(R.id.pbBroadcast);
		pbBroadcast.setVisibility(View.GONE);
		btnDialogButtonOK.setVisibility(View.VISIBLE);
		btnStartChat = (Button) dialogBroadcast.findViewById(R.id.btnStartLocChat);
		btnStartChat.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				btnDialogButtonOK.setVisibility(View.GONE);
				btnDialogButtonOK.startAnimation(out);
				pbBroadcast.setVisibility(View.VISIBLE);
				pbBroadcast.startAnimation( in );
				connection = XMPPLogic.getInstance().getConnection();
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				if (connection == null) {
					Account ac = new Account();
					ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), null);
					Log.e("Null Chat", "ENTERED NULL CHAT");
					failedToBroadcast(6);
				} else if (!connection.isConnected()) {
					Account ac = new Account();
					ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), null);
					failedToBroadcast(6);
				} else {
					
					getReceivers();
					
					if (broadCount == 0){
						broadCount = 1;
						showList();
						
					}
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				dialogBroadcast.dismiss();
			}
		});
	}
	String locationName = "";
	private void getNewLoc() {
		String dtUpdate = db.getLocationDateUpdate();
		if ((dtUpdate != "")) {
			Log.e("LOCATION INTELLIGENCE", "Getting db location...");
			ftLatitude = Float.parseFloat(db.getLocationLatitude());
			ftLongitude = Float.parseFloat(db.getLocationLongitude());
			latitude = ftLatitude;
			longitude = ftLongitude;
			
			LocationName lnCurrent = new LocationName();
			locationName = lnCurrent.getLocationNameFromCoordinates(GroupChatHomeActivity.this, longitude,latitude);
			
		}
	}
	
	private void makeNotify(CharSequence con, Style style) {
		AppMsg.makeText(this, con, style).show();
	}
	
	/*
	private OnItemClickListener onBroadItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Log.e("OI",position +": "+ arg3);
			// TODO Auto-generated method stub
			//do your job here, position is the item position in ListView
		}
	};
	*/
	public static void updateStatusText(int broad_stat_id){
		
			if (broad_stat_id == 1){
				if (broad_stat_id == 2){
					txtConnection.startAnimation(animFade);
				}
				txtConnection.setText("Not Connected To Server");
			}
			else if (broad_stat_id == 2){
				if (broad_stat_id == 1){
					txtConnection.startAnimation(animFade);
				}
				txtConnection.setText("Connected To Server");
			}
			else if (broad_stat_id == 3){
				txtConnection.setText("Connecting To Server...");
			}
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	private static Dialog dialogStatusYN;
    static LinearLayout lnHeader;
    static TextView edtStatusHead;
    static TextView edtStatus1;
    private void promptYesNoDialog(String caption, String message, Context cn, final String fcType, final String rawVal){
    	Log.e("11111","222");
    	dialogStatusYN = new Dialog(cn);
    	dialogStatusYN.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialogStatusYN.setContentView(R.layout.dialog_yesno);
        Button dialogButton = (Button) dialogStatusYN.findViewById(R.id.dialogButtonYes);
        Button dialogButtonNo = (Button) dialogStatusYN.findViewById(R.id.dialogButtonNo);
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
        		if (fcType.equals("DEL_POST")){
        			Log.e("few", "Deleted from sqlite ");
        			db.deleteBroadcastPostStatus(rawVal);
                    dialogStatusYN.dismiss();
                    mNotifier.sendEmptyMessage(2);
                    
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
    
    
    JSONArray nearby_users = null;
	private static final String TAG_GEO_USER = "user";
	private static final String TAG_GEO_DIS = "geo_distance";
	int isSuccess;
	private void getReceivers() {
		LocationResult locationResult = new LocationResult() {
			Boolean isLocAvail = false;@Override
			public void gotLocation(Location location) {
				if (location != null) {
					ftLatitude = (float) location.getLatitude();
					ftLongitude = (float) location.getLongitude();
					latitude = ftLatitude;
					longitude = ftLongitude;
					db.insertLocation(longitude, latitude);
					isLocAvail = true;
					getNearbyUsersId();
				}
			}
		};
		MyLocation myLocation = new MyLocation();
		boolean availLoc = myLocation.getLocation(this, locationResult);
		if (availLoc == false) {
			makeNotify("GPS Services Unavailable", AppMsg.STYLE_ALERT);
			failedToBroadcast(5);
		} else {
			Log.e("LOCATION INTELLIGENCE", "Getting db location...");
			ftLatitude = Float.parseFloat(db.getLocationLatitude());
			ftLongitude = Float.parseFloat(db.getLocationLongitude());
			latitude = ftLatitude;
			longitude = ftLongitude;
		}
	}
	int exactBroadCount;
	private void getNearbyUsersId() {
		isSuccess = 0;
		exactBroadCount = 0;
		final String tag_string_req = "collect_user_id";
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_NEARBY, new Response.Listener < String > () {@Override
			public void onResponse(String response) {
				Log.e("UsersByID", "GET GEO Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if (!error) {
						Log.e("JSON", "GEO COLLECTED");
						nearby_users = jObj.getJSONArray("geo");
						Log.e("LOG", "*****JARRAY*****" + nearby_users.length());
						if (nearby_users.length() == 0) {
							isSuccess = 1;
						} else {
							for (int i = 0; i < nearby_users.length(); i++) {
								JSONObject c = nearby_users.getJSONObject(i);
								String uname = c.getString(TAG_GEO_USER);
								String rawDis = c.getString(TAG_GEO_DIS);
								float dis = Float.parseFloat(rawDis);
								strUser = "";
								if (dis <= dst) {
									//try{
									msg = new Message(uname + "@vps.gigapros.com/Smack", Message.Type.normal);
									msg.setBody(StringFormattingUtils.setBroadcastChatEquivalent(locationName, edBroad.getText().toString()));
									try{
									connection.sendPacket(msg);
									exactBroadCount++;
									}catch(Exception xmp){
										Log.e("ERR", "ds"+ xmp.getLocalizedMessage());
									}
								}
								Log.e("XMPPChatDemoActivity", "Sending broadcast to: " + uname);
								if (i + 1 == nearby_users.length()) {
									db.insertBroadcast(1, db.getLoggedInID() + "" + 0, edBroad.getText().toString(), longitude, latitude, locationName, exactBroadCount);
									dialogBroadcast.dismiss();
									makeNotify("Message Successfully Broadcasted", AppMsg.STYLE_INFO);
									mNotifier.sendEmptyMessage(2);
								}
								if (i == nearby_users.length() - 1) {
									isSuccess = 2;
								}
							}
						}
					} else {
						isSuccess = 3;
						failedToBroadcast(isSuccess);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					isSuccess = 4;
					failedToBroadcast(isSuccess);
				}
			}
		}, new Response.ErrorListener() {@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("UsersByID", "Error: " + error.getMessage());
				isSuccess = 4;
				failedToBroadcast(isSuccess);
			}
		}) {@Override
			protected Map < String, String > getParams() {
				Map < String, String > params = new HashMap < String, String > ();
				String ins_latitude = db.getLocationLatitude();
				String ins_longitude = db.getLocationLongitude();
				String ins_user = db.getLoggedInID();
				params.put("tag", tag_string_req);
				params.put("pid", ins_user);
				params.put("longitude", ins_longitude + "");
				params.put("latitude", ins_latitude + "");
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}
	private void failedToBroadcast(int type){
		if (type == 3){
			makeNotify("Failed To Contact Server", AppMsg.STYLE_ALERT);
		}
		else if (type == 4){
			makeNotify("Connection Error", AppMsg.STYLE_ALERT);
		}
		else if (type == 5){
			makeNotify("GPS Unavailavle", AppMsg.STYLE_ALERT);
		}
		else if (type == 6){
			makeNotify("No Connection Available", AppMsg.STYLE_ALERT);
		}
		dialogBroadcast.dismiss();
		
	}
}