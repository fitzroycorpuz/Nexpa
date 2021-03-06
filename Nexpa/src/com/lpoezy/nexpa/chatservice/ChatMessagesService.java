package com.lpoezy.nexpa.chatservice;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.activities.ChatActivity;
import com.lpoezy.nexpa.activities.ChatHistoryActivity;
import com.lpoezy.nexpa.activities.TabHostActivity;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.L;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

//started in TabHostActivity class onResume
public class ChatMessagesService extends Service {
	
	private IBinder mBinder = new LocalBinder();
	public static boolean isRunning;
	
	public class LocalBinder extends Binder{
		public ChatMessagesService getService(){ return ChatMessagesService.this; }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		isRunning = false;
		
		L.debug("ChatMessagesService onDestroy");
	}
	
	@Override
	public void onCreate() {
		L.debug("ChatMessagesService, onCreate");
		super.onCreate();
		
		isRunning = true;
		onReceiveChatMessages();
	}
	
	
	//create a connection with xmpp,
	//and listen to any incoming messages
	public void onReceiveChatMessages() {
		
		L.debug("onReceiveChatMessages");
		SQLiteHandler db = new SQLiteHandler(this);
		db.openToWrite();
		db.updateBroadcasting(0);
		db.updateBroadcastTicker(0);
		
		
		
		XMPPConnection connection = XMPPLogic.getInstance().getConnection();
		
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		if(connection!=null){
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					final Message message = (Message) packet;
					
					if (message.getBody() != null) {
						
						final String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						
			
						final String tag_string_req = "get_single";
						
						StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener < String > () {
							
							@Override
							public void onResponse(String response) {
								L.debug("response: " + response.toString());
								try {
									JSONObject jObj = new JSONObject(response);
									boolean error = jObj.getBoolean("error");
									L.debug("msg? "+message.getBody());
									if (!error) {
										L.debug("getting sender's credential");
										String uid = jObj.getString("uid");
										JSONObject user = jObj.getJSONObject("user");
										String name = user.getString("name");
										String email = user.getString("email");
										
										Correspondent correspondent = new Correspondent();
										correspondent.setUsername(name);
										correspondent.setEmail(email);
										
										OneComment comment = new OneComment(false, message.getBody(), true);
										comment.isUnread = true;
										correspondent.addMessage(comment);
										
										//check if there is an available activity to,
										//receive the brodacast
										if(!TabHostActivity.isRunning 
												&& !ChatHistoryActivity.isRunning 
												&& !ChatActivity.isRunning){
											
											saveMesageOffline(correspondent);
											
											L.debug("sending nottification!");
											
											//send notification 
											sendNotification(correspondent);
											
										}else{
											
											//save mesage offline
											//if ChatHistoryActivity is running
											if(!ChatActivity.isRunning){
												saveMesageOffline(correspondent);
											}
											//send broadcast
											Intent broadcast = new Intent(AppConfig.ACTION_RECEIVED_MSG);
											broadcast.putExtra("email", correspondent.getEmail());
											broadcast.putExtra("username", correspondent.getUsername());
											broadcast.putExtra("fname", correspondent.getFname());
											broadcast.putExtra("msg", message.getBody());
											L.debug("sending broadcast!");
											sendBroadcast(broadcast);
										}
										
										
									} else {
										String errorMsg = jObj.getString("error_msg");
										Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							

						
						}, new Response.ErrorListener() {@Override
							public void onErrorResponse(VolleyError error) {
								L.error("error: " + error.getMessage());
								Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
								
							}
						}) {@Override
							protected Map < String, String > getParams() {
								Map < String, String > params = new HashMap < String, String > ();
								params.put("tag", tag_string_req);
								params.put("username", fromName.split("@")[0]);
								
								
								return params;
							}
						};
						
						AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
						//OneComment comment = new OneComment(false, message.getBody(), true);
								
								
					}
				}
			}, filter);
		}
		
		
		
		
//		Account ac = new Account();
//		ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), new OnXMPPConnectedListener() {
//			
//			@Override
//			public void onXMPPConnected() {
//				
//				onTabHostActivtyReadyListener.onTabHostActivtyReady();
//				
//				
//				
//			}
//		});
		
		
	}
	
	private void saveMesageOffline(Correspondent correspondent) {
		
		//save only the message,
		//if sender is already exist in db
		if(correspondent.isExisting(getApplicationContext())){
			correspondent.getConversation().get(0).saveOffline(getApplicationContext(), correspondent.getId());
			
		//save both the sender and the message,
		//if sender doesn't exist in db
		}else{
			correspondent.saveOffline(getApplicationContext(), false);
		}
	}
	
	 private PendingIntent getNotificationPendingIntent(Correspondent correspondent) {
			// Creates an explicit intent for an Activity in your app
		 Intent resultIntent = new Intent(this, ChatActivity.class);
			resultIntent.putExtra("email", correspondent.getEmail());
			resultIntent.putExtra("username", correspondent.getUsername());
			resultIntent.putExtra("fname", correspondent.getFname());

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(ChatActivity.class);

			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);

			return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

		}
	
	private void sendNotification(Correspondent correspondent) {
		
		int msgCount = OneComment.getUnReadMsgCountOffline(getApplicationContext());
		
		String title = (msgCount>1)?  " new messages" : " new message";
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(msgCount + title)
		        .setAutoCancel(true)
		        .setContentText(correspondent.getUsername());
		
		mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
				
		PendingIntent resultPendingIntent = getNotificationPendingIntent(correspondent);
		
//		if (Build.VERSION.SDK_INT == 19) {
//			resultPendingIntent.cancel();
//		}
		
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(AppConfig.MSG_NOTIFICATION_ID, mBuilder.build());
		
		
	}

	
}
