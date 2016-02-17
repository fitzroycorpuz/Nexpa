package com.lpoezy.nexpa.activities;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.openfire.Account;
import com.lpoezy.nexpa.openfire.OnXMPPConnectedListener;
import com.lpoezy.nexpa.openfire.XMPPLogic;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BuddyRequestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buddy_request);

		final EditText etSubscribeTo = (EditText) this.findViewById(R.id.et_subscribe_to);
		etSubscribeTo.setText("momo@vps.gigapros.com");

		((Button) this.findViewById(R.id.btn_subscribe)).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// final String address = name+"@vps.gigapros.com";
				final String address = etSubscribeTo.getText().toString();
				final XMPPConnection connection = XMPPLogic.getInstance().getConnection();

				if (connection == null || !connection.isConnected()) {

					SQLiteHandler db = new SQLiteHandler(getApplicationContext());
					db.openToWrite();

					Account ac = new Account();
					ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), new OnXMPPConnectedListener() {

						@Override
						public void onXMPPConnected(XMPPConnection con) {

							requestSubscription(con, address);
						}

					});

					db.close();
				} else {
					requestSubscription(connection, address);
				}

			}
		});
	}

	private void requestSubscription(XMPPConnection connection, String address) {

		L.error("sending subscription request to address: " + address);
		Presence subscribe = new Presence(Presence.Type.subscribe);
		subscribe.setTo(address);
		connection.sendPacket(subscribe);

		Roster roster = connection.getRoster();
		try {
			roster.createEntry(address, null, null);
		} catch (XMPPException e) {
			L.error("" + e);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		final XMPPConnection connection = XMPPLogic.getInstance().getConnection();

		if (connection == null || !connection.isConnected()) {
			SQLiteHandler db = new SQLiteHandler(getApplicationContext());
			db.openToWrite();

			// db.updateBroadcasting(0);
			// db.updateBroadcastTicker(0);

			Account ac = new Account();
			ac.LogInChatAccount(db.getUsername(), db.getPass(), db.getEmail(), new OnXMPPConnectedListener() {

				@Override
				public void onXMPPConnected(XMPPConnection con) {

					subscriptionRequestListener(con);
				}

			});

			db.close();
		} else {

			subscriptionRequestListener(connection);

		}
	}

	private void subscriptionRequestListener(final XMPPConnection connection) {

		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				
				final Presence presence = (Presence) packet;
		        final String fromId = presence.getFrom();
		        //final RosterEntry newEntry = connection.getRoster().getEntry(fromId);
		        
				if (presence.getType() == Type.subscribe) {
					
					L.debug("subscribe: "+fromId);
					//approved request
					Presence subscribed = new Presence(Presence.Type.subscribed);
					subscribed.setTo(fromId);
					connection.sendPacket(subscribed);
					
				} else if (presence.getType() == Type.unsubscribe) {
					L.debug("unsubscribe: "+fromId);
				} else if (presence.getType() == Type.subscribed) {
					L.debug("subscribed: "+fromId);
				} else if (presence.getType() == Type.unsubscribed) {
					L.debug("unsubscribed: "+fromId);
				} else if (presence.getType() == Type.available) {
					L.debug("available: "+fromId);
				} else if (presence.getType() == Type.unavailable) {
					L.debug("unavailable: "+fromId);
				}
			}
		}, new PacketTypeFilter(Presence.class));

	}
}
