package com.lpoezy.nexpa.chatservice;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import com.devspark.appmsg.AppMsg;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BroadcastService {
	private XMPPConnection connection;
	SQLiteHandler db;
	Message msg;
	public class MyService extends Service {
		public MyService() {
		}

		@Override
		public IBinder onBind(Intent intent) {
			// TODO: Return the communication channel to the service.
			throw new UnsupportedOperationException("Not yet implemented");
		}

		float ftLatitude = 0;
		float ftLongitude = 0;
		float latitude = 0;
		float longitude = 0;
		@Override
	    public void onCreate() {
			Log.e("OI","XXX");
			 Toast.makeText(this, " Service Created", Toast.LENGTH_LONG).show();

	    }

	    @Override
	    public void onStart(Intent intent, int startId) {
	    	// For time consuming an long tasks you can launch a new thread here...
	    	 Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();
	        db = new SQLiteHandler(this);
			db.openToWrite();
			
			/*/
			connection = XMPPLogic.getInstance().getConnection();
		//	if ((longitude != 0) && (latitude != 0)) {
				if (connection != null) {
					PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
					connection.addPacketListener(new PacketListener() {@Override
						public void processPacket(Packet packet) {
							final Message message = (Message) packet;
							if (message.getBody() != null) {
								String fromName = StringUtils.parseBareAddress(message.getFrom());
								Log.e("XMPPChatDemoActivity", "Text Recieved: " + message.getBody() + " from " + fromName);
								String senderRaw = message.getFrom();
								String[] arr = senderRaw.split("@");
								String senderEdited = arr[0];
								Log.e("XMPPChatDemoActivity", senderEdited);
								db.insertBroadcast(2, senderEdited + "", msg.getBody(), longitude, latitude, "", 0);
								
							}
						}
					}, filter);
					connection.addPacketSendingListener(new PacketListener() {
						@Override
						public void processPacket(Packet packet) {
							final Message message = (Message) packet;
							if (message.getBody() != null) {
								String fromName = StringUtils.parseBareAddress(message.getFrom());
								Log.e("XMPPChatDemoActivity", "Text Sent " + message.getBody() + " from " + fromName);
							}
						}
					}, filter);
				}
			//} else {
				//makeNotify("Broadcasting Unavailable", AppMsg.STYLE_INFO);
			//}
				
				//*/
	    }

	    @Override
	    public void onDestroy() {
	        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

	    }
	}
}
