package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.lpoezy.nexpa.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatLoginActivity extends Activity{
	
	public static final String HOST = "198.154.106.160";
	public static final int PORT = 5222;
	public static final String SERVICE = "@198.154.106.160/Smack";

	private XMPPConnection connection;
	private ArrayList<String> messages = new ArrayList<String>();
	private Handler mHandler = new Handler();

	//private EditText recipient;
	private EditText textMessage;
	private ListView listview;
	
	private Button btnLogin;
	private EditText inputEmail;
	private EditText inputPassword; 
	private ProgressDialog pDialog;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		
		//Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		
		
		btnLogin.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {	
			//	code here

					Log.i("EMAIL",inputEmail.getText().toString()+ SERVICE);
					Log.i("PASSWORD",inputPassword.getText().toString());
				    connect();
				    Toast.makeText(getApplicationContext(),
							"You are logging in as " + inputEmail.getText().toString(),Toast.LENGTH_SHORT).show();	
				    Intent in = new Intent(ChatLoginActivity.this, ChatActivity.class);
				    in.putExtra("username", inputEmail.getText().toString());
				    in.putExtra("password", inputPassword.getText().toString());
		            startActivity(in);
				}

		    });
		
	}
	
	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						Log.i("XMPPChatDemoActivity", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						messages.add(fromName + ":");
						messages.add(message.getBody());
						// Add the incoming message to the list view
						mHandler.post(new Runnable() {
							public void run() {
								setListAdapter();
							}
						});
					}
				}
			}, filter);
		}
	}

	private void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.listitem, messages);
		listview.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (connection != null)
				connection.disconnect();
		} catch (Exception e) {

		}
	}
	
	public void connect() {

		final ProgressDialog dialog = ProgressDialog.show(this,
				"Connecting...", "Please wait...", false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						HOST, PORT, inputEmail.getText().toString()+ SERVICE);
				XMPPConnection connection = new XMPPConnection(connConfig);

				try {
					connection.connect();
					Log.i("XMPPChatDemoActivity",
							"Connected to " + connection.getHost());
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to connect to "
							+ connection.getHost());
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}
				try {
					// SASLAuthentication.supportSASLMechanism("PLAIN", 0);
					connection.login(inputEmail.getText().toString(), inputPassword.getText().toString());
					Log.i("XMPPChatDemoActivity",
							"Logged in as " + connection.getUser());
					dialog.dismiss();
					

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					//setConnection(connection);

					Roster roster = connection.getRoster();
					Collection<RosterEntry> entries = roster.getEntries();
					for (RosterEntry entry : entries) {
						Log.e("XMPPChatDemoActivity",
								"--------------------------------------");
						Log.e("XMPPChatDemoActivity", "RosterEntry " + entry);
						Log.e("XMPPChatDemoActivity",
								"User: " + entry.getUser());
						Log.e("XMPPChatDemoActivity",
								"Name: " + entry.getName());
						Log.e("XMPPChatDemoActivity",
								"Status: " + entry.getStatus());
						Log.e("XMPPChatDemoActivity",
								"Type: " + entry.getType());
						Presence entryPresence = roster.getPresence(entry
								.getUser());

						Log.d("XMPPChatDemoActivity", "Presence Status: "
								+ entryPresence.getStatus());
						Log.d("XMPPChatDemoActivity", "Presence Type: "
								+ entryPresence.getType());
						Presence.Type type = entryPresence.getType();
						if (type == Presence.Type.available)
							Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
						Log.d("XMPPChatDemoActivity", "Presence : "
								+ entryPresence);

					}
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to log in as "
							+ inputEmail.getText().toString());
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}

					
			}
			
		});	
		t.start();
		dialog.show();
	}

}
