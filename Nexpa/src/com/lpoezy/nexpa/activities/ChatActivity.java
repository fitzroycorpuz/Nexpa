package com.lpoezy.nexpa.activities;

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
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.openfire.OnXMPPConnectedListener;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.L;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity {
	private com.lpoezy.nexpa.chatservice.ChatAdapterActivity adapter;

	public boolean isMine;
	private XMPPConnection connection;
	private Handler mHandler = new Handler();

	private EditText recipient;
	private EditText textMessage;
	private ListView listview;
	
	String int_mes;
	String int_broad;
	String int_b_date;
	Intent intentMes;

	private boolean mFrmRotation;

	private Correspondent mCorrespondent;

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
		listview = (ListView) this.findViewById(R.id.listMessages);
		
		adapter = new ChatAdapterActivity(getApplicationContext(), R.layout.listitem);
		listview.setAdapter(adapter);
	
		intentMes = getIntent(); 
		//String email = intentMes.getStringExtra("email");
		final long userId=-1;
		final String username = intentMes.getStringExtra("username");
		final String email = getIntent().getStringExtra("email");
		final String fname = getIntent().getStringExtra("fname");
		
		
		mCorrespondent = new Correspondent(userId, username, email, fname);
		
		if(mCorrespondent.isExisting(this)){	//check if current correspondent exists in db
			
			//get all existing messages frm db
			mCorrespondent.downloadOfflineMessages(this);
			
			
			for(OneComment comment : mCorrespondent.getConversation()){
				
				
				adapter.add(comment);
			}
			//clear conversation, 
			//so only newly send and accepted messages will be save later
			mCorrespondent.clearExistingConversation();
			
		}else{
			//1st time chatting with correspondent
		}
		
		
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
			public void onClick(View view) {
				//String to = username + "@vps.gigapros.com/Smack";
				final String to = recipient.getText().toString();
				final String text = textMessage.getText().toString();
				textMessage.setText("");
				
				

				Log.e("XMPPChatDemoActivity", "Sending text " + text + " to " + to);
				final Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);	
				
					connection = XMPPLogic.getInstance().getConnection();
					
				
					 if ((connection == null)||(!connection.isConnected()))  {	
				       		Account ac = new Account();
				       		
				        	final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
				    		db.openToWrite();
				    		
				            ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), new OnXMPPConnectedListener() {
								
								@Override
								public void onXMPPConnected(XMPPConnection connection) {
									Log.e("ADDING AFTER LOGIN","g");
						            runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											
											 //connection.sendPacket(msg);
											// messages.add(connection.getUser() + ":");
											OneComment comment = new OneComment(true, text, false);
											 
											adapter.add(comment);
											listview.setAdapter(adapter);
											mCorrespondent.addMessage(comment);
											db.close();
											
										}
									});
									
									
								}
							});
				            
				            
							
				       }
				      
				       else{
				    		connection.sendPacket(msg);
							//messages.add(connection.getUser() + ":");
				    		OneComment comment = new OneComment(true, text, true);
							adapter.add(comment);
							
							listview.setAdapter(adapter);
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
					L.debug("msg received...");
					
					String msg = intent.getStringExtra("msg");
					OneComment comment = new OneComment(false, msg, true);
					adapter.add(comment);
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
								adapter.add(comment);
								listview.setAdapter(adapter);
								
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
	
	
	
}