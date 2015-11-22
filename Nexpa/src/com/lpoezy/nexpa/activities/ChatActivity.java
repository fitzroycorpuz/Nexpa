package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.chatservice.ChatAdapterActivity;
import com.lpoezy.nexpa.chatservice.OneComment;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.openfire.OnXMPPConnectedListener;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DividerItemDecoration;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatActivity extends Activity implements Correspondent.OnCorrespondentUpdateListener{
	private com.lpoezy.nexpa.chatservice.ChatAdapterActivity adapter;

	public boolean isMine;
	private XMPPConnection connection;
	private Handler mHandler = new Handler();

	private EditText recipient;
	private EditText textMessage;
	//private ListView listview;
	private RecyclerView listview;
	String int_mes;
	String int_broad;
	String int_b_date;
	Intent intentMes;

	private boolean mFrmRotation;

	private Correspondent mCorrespondent;

	private ChatAdapter mAdapter;

	private List<OneComment> mComments;

	private long mCorrespondentId;

	public static boolean isRunning = false;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutmain);
		
		if(savedInstanceState!=null){
			mFrmRotation = true;
		}else{
			mFrmRotation = false;
		}

		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			 ActionBar actionBar = getActionBar();
			 actionBar.setHomeButtonEnabled(true);
			 actionBar.setDisplayHomeAsUpEnabled(true);
		}
		else{
			 Log.e("NOTICE","Device cannot handle ActionBar");
		}
		
		
		//setConnection();
		recipient = (EditText) this.findViewById(R.id.toET);
		textMessage = (EditText) this.findViewById(R.id.chatET);
		//listview = (ListView) this.findViewById(R.id.listMessages);
		listview = (RecyclerView) this.findViewById(R.id.listMessages);
		LinearLayoutManager lm = new LinearLayoutManager(ChatActivity.this);
		
		
		lm.setStackFromEnd(true);
		listview.setLayoutManager(lm);
		
		
		
		//listview.addItemDecoration(new DividerItemDecoration(ChatActivity.this, DividerItemDecoration.VERTICAL_LIST));
		
		intentMes = getIntent(); 
		//String email = intentMes.getStringExtra("email");
		final long userId=getIntent().getLongExtra("userid", -1);
		mCorrespondentId = userId;
		final String username = intentMes.getStringExtra("username");
		final String email = getIntent().getStringExtra("email");
		final String fname = getIntent().getStringExtra("fname");
		
		mComments = new ArrayList<OneComment>();
		mAdapter = new ChatAdapter(ChatActivity.this);
		
		
		listview.setAdapter(mAdapter);
		//adapter = new ChatAdapterActivity(getApplicationContext(), R.layout.listitem, userId);
		//listview.setAdapter(adapter);
	
		int_mes = "";
		int_broad = "";
		int_b_date = "";
		
		try{
			 int_mes = intentMes.getStringExtra("INTENT_MESSAGE");
			 int_broad = intentMes.getStringExtra("INTENT_MESSAGE_TYPE");
			 int_b_date = intentMes.getStringExtra("INTENT_MESSAGE_DATE");
		}
		catch(Exception e){}
		
		
//		intentMes.putExtra("email", email);
//		intentMes.putExtra("username", username);
//		intentMes.putExtra("fname", fname);
		
		
		
      //  String fname = intentMes.getStringExtra("fname");
        
        recipient.setText(username + "@vps.gigapros.com/Smack", TextView.BufferType.EDITABLE);
        recipient.setVisibility(0);
//Smack
        if (int_broad != null && int_broad.equals("BROADCAST")){
			int_broad = "";
			textMessage.setText("In reply to: '"+int_mes+"',\nPosted "+int_b_date+"\n\n");
			textMessage.setSelection(textMessage.getText().length());
        }
        else{
        	textMessage.setText("");
        }
        
        
		// Set a listener to send a chat text message
		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			private boolean isReconnecting;

			public void onClick(View view) {
				//String to = username + "@vps.gigapros.com/Smack";
				final String to = recipient.getText().toString();
				final String text = textMessage.getText().toString();
				textMessage.setText("");
				
				
				final Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);	
				
					connection = XMPPLogic.getInstance().getConnection();
					
					
					 if ((connection == null)||(!connection.isConnected()))  {
						 L.debug("XMPPChatDemoActivity, reconnecting...");
						 	
						 	OneComment comment = new OneComment(true, text, false);
						 
						 	mComments.add(comment);
						 	mAdapter.notifyDataSetChanged();
						 	listview.smoothScrollToPosition(mComments.size()-1); 
							//adapter.add(comment);
							//listview.setAdapter(adapter);
							mCorrespondent.addMessage(comment);
							
				       		
				       		if(!isReconnecting)
				       		{
				       			isReconnecting = true;
				       			Account ac = new Account();
				       			final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
					    		db.openToWrite();
					    		
					            ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), new OnXMPPConnectedListener() {
									
									@Override
									public void onXMPPConnected(XMPPConnection connection) {
										
										isReconnecting = false;
										
									}
								});
					            
					            db.close();
				       		}
				        	
							
				       }
				      
				       else{
				    	   L.debug("XMPPChatDemoActivity, Sending text " + text + " to " + to);
				    		connection.sendPacket(msg);
							//messages.add(connection.getUser() + ":");
				    		OneComment comment = new OneComment(true, text, true);
				    		
				    		mComments.add(comment);
							mAdapter.notifyDataSetChanged();
							listview.smoothScrollToPosition(mComments.size()-1); 
				    		
							//adapter.add(comment);
							
							//listview.setAdapter(adapter);
							//Log.e("ADD","ayayay");
							
							mCorrespondent.addMessage(comment);

							
				       }
			}
		});

	}
	
	
	//receiving messages will be handle by receivedMessage
	//in ChatMessagesService
	private BroadcastReceiver mReceivedMessage = new BroadcastReceiver(){

		@Override
		public void onReceive(final Context context, final Intent intent) {
			
			
			
			mHandler.post(new Runnable() {
				public void run() {
					
					
					String msg = intent.getStringExtra("msg");
					
					final long userId = intent.getLongExtra("userid", -1);
					//mCorrespondentId = userId;
					mCorrespondent.setId(userId);
					L.debug("msg received from ..."+userId);
					//final String username = intentMes.getStringExtra("username");
					//final String email = getIntent().getStringExtra("email");
					//final String fname = getIntent().getStringExtra("fname");
					
					OneComment comment = new OneComment(false, msg, true);
					
					mComments.add(comment);
					mAdapter.notifyDataSetChanged();
					listview.smoothScrollToPosition(mComments.size()-1); 
					//adapter.add(comment);
					
					
					//listview.setAdapter(adapter);
					
					mCorrespondent.addMessage(comment);
					
				}
			});
			
		}
		
	};

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection() {
		connection = XMPPLogic.getInstance().getConnection();
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					final Message message = (Message) packet;
					
					
					if (message.getBody() != null) {
						final String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						
						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						
				
						//messages.add(fromName + ":");
						
						
						// Add the incoming message to the list view
						mHandler.post(new Runnable() {
							public void run() {
								
								OneComment comment = new OneComment(false, message.getBody(), true);
								//adapter.add(comment);
								//listview.setAdapter(adapter);
								
								mComments.add(comment);
								mAdapter.notifyDataSetChanged();
								listview.smoothScrollToPosition(mComments.size()-1); 
								mCorrespondent.addMessage(comment);
								
							}
						});
					}
				}
			}, filter);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		isRunning = true;
		
		registerReceiver(mReceivedMessage, new IntentFilter(AppConfig.ACTION_RECEIVED_MSG));
		
		final long userId		= getIntent().getLongExtra("userid", -1);
		final String username 	= getIntent().getStringExtra("username");
		final String email 		= getIntent().getStringExtra("email");
		final String fname 		= getIntent().getStringExtra("fname");
		
		mCorrespondent = new Correspondent(userId, username, email, fname);
		mCorrespondent.addListener(this);
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mCorrespondent.downloadProfilePicOnline(ChatActivity.this);
				
			}
		}).start();
		
		if(mComments!=null)mComments.clear();
		if(mCorrespondent.isExisting(this)){	//check if current correspondent exists in db
			
			//get all existing messages frm db
			mCorrespondent.downloadOfflineMessages(this);
			
			
			for(OneComment comment : mCorrespondent.getConversation()){
				
				mComments.add(comment);
				mAdapter.notifyDataSetChanged();
				listview.smoothScrollToPosition(mComments.size()-1); 
				//adapter.add(comment);
			}
			//clear conversation, 
			//so only newly send and accepted messages will be save later
			mCorrespondent.clearExistingConversation();
			
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		L.debug("ChatActivity, onPause... ");
		//connection.disconnect();
		//Log.e("NULLIFIED","");
		
		isRunning = false;
		
		unregisterReceiver(mReceivedMessage);
		
		// save messages here, 
		//and clear all  the conversation array
		L.debug("ChatActivity, saving msgs... ");
					
		mCorrespondent.saveOffline(ChatActivity.this, true);
		
		if(isFinishing()){
			//will make sure that the othe activities/fragments,
			//that are dependent on message count will automatically,
			//update themselves
			L.debug("ChatActivity, update msg count in tab... ");
			Intent broadcast = new Intent(AppConfig.ACTION_RECEIVED_MSG);
			sendBroadcast(broadcast);
		}
	}
	
	private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
		
		 class ViewHolder extends RecyclerView.ViewHolder{

			 RelativeLayout wrapper;
			TextView countryName;
			 ImageView iv;
			 

			public ViewHolder(View itemView) {
				super(itemView);
				wrapper = (RelativeLayout) itemView.findViewById(R.id.wrapper);
				//countryName = (TextView) itemView.findViewById(R.id.comment);

						
				iv = new ImageView(context);
				iv.setId(1);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(68, 68);
				//layoutParams.addRule(RelativeLayout.LEFT_OF, countryName.getId());
				//layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, countryName.getId());
				wrapper.addView(iv, layoutParams);
				
				countryName = new TextView(context);
				countryName.setId(2);
				//RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(80, 80);
				//layoutParams.addRule(RelativeLayout.RIGHT_OF, iv.getId());
				
				wrapper.addView(countryName);
				
			}
			
		}

		private LayoutInflater inflater;
		private Context context;

		public ChatAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getItemCount() {
			
			return mComments.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder vh, int pos) {
			
			OneComment coment = mComments.get(pos);
			vh.countryName.setText(coment.comment);
			
			if (coment.success){
				vh.countryName.setBackgroundResource(coment.left ? R.drawable.bubble_green : R.drawable.bubble_yellow);
			}
			else{
				vh.countryName.setBackgroundResource(coment.left ? R.drawable.bubble_failed : R.drawable.bubble_yellow);
			}
			
			
			if(!coment.left){
				
				Bitmap bmp = getCorrespondentPic();
				vh.iv.setImageBitmap(bmp);
				
				//will remove previously added rule to this view
				((RelativeLayout.LayoutParams)vh.iv.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, 0);
				//add new rule for the layout to implement
				((RelativeLayout.LayoutParams)vh.countryName.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, vh.iv.getId());
				
			}else{
				
				Bitmap bmp = getUserPic(vh.iv);
				vh.iv.setImageBitmap(bmp);
				//will remove previously added rule to this view
				((RelativeLayout.LayoutParams)vh.countryName.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, 0);
				//add new rule for the layout to implement
				((RelativeLayout.LayoutParams)vh.iv.getLayoutParams()).addRule(RelativeLayout.RIGHT_OF, vh.countryName.getId());
				
			}
			
			vh.wrapper.setGravity(coment.left ? Gravity.RIGHT : Gravity.LEFT);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup root, int arg1) {
			
			View itemView = inflater.inflate(R.layout.listitem, root, false);
			return new ViewHolder(itemView );
		}
		
		
		
		private Bitmap getCorrespondentPic() {
			
			Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(),
			        R.drawable.pic_sample_girl);
			
			if(mCorrespondent.getProfilePic()!= null){
				rawImage = mCorrespondent.getProfilePic();
			}
			
	
			RoundedImageView riv = new RoundedImageView(context);
			Bitmap  circImage = riv.getCroppedBitmap(rawImage, 48);
			
			return circImage;
		}
		
		private Bitmap userPic; 
		private Bitmap getUserPic(ImageView imgRight) {
			
			String imgDecodableString = ProfilePicture.getUserImgDecodableString(context);
			
	        Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(),
	        R.drawable.pic_sample_girl);
	       if(userPic==null){
	    	   if(imgDecodableString!=null && !imgDecodableString.isEmpty()){
		        	
		        	// Get the dimensions of the View
		            int targetW = imgRight.getWidth();
		            int targetH = imgRight.getHeight();
		            
		            BmpFactory  bmpFactory = new BmpFactory();
		            userPic = rawImage = bmpFactory.getBmpWithTargetWTargetHFrm(targetW, targetH, imgDecodableString);
		        	
		        }
		        
		        
		        
	       }else{
	    	   rawImage = userPic;
	       }
	       
	       RoundedImageView riv = new RoundedImageView(context);
	       Bitmap circImage = riv.getCroppedBitmap(rawImage, 48);
	       
			return circImage;
		}

		
	}

	@Override
	public void onCorrespondentUpdate() {
		
		listview.post(new Runnable() {
			
			@Override
			public void run() {
				L.debug("CAhapterActivity, onCorrespondentUpdate "+mCorrespondent.getProfilePic());
				
				mAdapter.notifyDataSetChanged();
			}
		});
		
		
	}
	
}