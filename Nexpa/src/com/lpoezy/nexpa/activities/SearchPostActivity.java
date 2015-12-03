package com.lpoezy.nexpa.activities;

import android.widget.AbsListView;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import com.lpoezy.nexpa.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;

import android.database.Cursor;

public class SearchPostActivity extends Activity implements OnItemClickListener{

	String strSearch;
	String strType;
	SimpleCursorAdapter mAdapter;
	SQLiteHandler db;
	Cursor crBroadcast;
	int limit_loader;
	DateUtils du;
	boolean flag_loading;
	boolean is_scrolled = false;
	ListView mListView;
	
	LinearLayout btnReply;
	LinearLayout btnFave;
	LinearLayout btnDel;
	
	Handler mHandler;
	Handler mNotifier;
	
	int broadCount;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		SearchPostActivity.this.overridePendingTransition(R.anim.anim_appear, R.anim.anim_enter_right);
	    return true;

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_post);
		
		// overridePendingTransition(R.anim.anim_leave, R.anim.anim_enter);
		   overridePendingTransition(R.anim.anim_enter, R.anim.anim_leave);
		   ActionBar actionBar = getActionBar();
		   actionBar.setDisplayHomeAsUpEnabled(true);
		   mListView = (ListView) findViewById(R.id.listview);
			
			// mListView.setOnItemClickListener(this);
		   mListView.setOnItemClickListener(onItemClickListener);
		   Intent intent = getIntent();
		   strSearch = intent.getStringExtra("SEARCH");
		   strType = intent.getStringExtra("TYPE");
		   TextView txtMes  = (TextView)findViewById(R.id.txtMes);
		   TextView txtNo  = (TextView)findViewById(R.id.txtNo);
		   if (strType.equals("hashtag")){
			   txtMes.setText("Search results for '"+ strSearch + "'");
		   }
		   limit_loader = 10;
		   
		   db = new SQLiteHandler(this);
			db.openToWrite();
			du = new DateUtils();
			
			broadCount = 0;
			limit_loader = 10;
			broadCount = db.getFilteredBroadCastCount(strSearch);
			
			if (broadCount == 0){
				txtNo.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
			}
			else{
				txtNo.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			showList();
			}
	}
	
	int broad_user_type = 0;
	String strReach;
	boolean setType;
	int incr = 0;
	private void showList(){
		
		crBroadcast = db.getFilteredBroadCast(limit_loader,strSearch);
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
	}
		
	public static String displayName(String fname, String user) {
		if ((fname.equals("")) || (fname == null) || (fname.equals("null"))) {
			return user;
		} else {
			return fname;
		}
	}
	
	private void loadItems(){
		if (limit_loader < db.getFilteredBroadCastCount(strSearch)){
		limit_loader = limit_loader + 10;
		crBroadcast = 	crBroadcast = db.getFilteredBroadCast(limit_loader,strSearch);
		//crBroadcast.requery();
		mAdapter.changeCursor(crBroadcast);
		//mNotifier.sendEmptyMessage(2);
		flag_loading = false;
		}
		
	}
	TextView txtReply;
	TextView txtUser;
	TextView txtBroadId;
	TextView txBroad;
	
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
							SearchPostActivity.this,"DEL_POST",br_id);
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
							
							Intent explicitIntent = new Intent(SearchPostActivity.this,
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
		
    
}
