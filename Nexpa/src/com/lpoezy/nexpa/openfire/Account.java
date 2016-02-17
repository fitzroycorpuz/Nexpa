package com.lpoezy.nexpa.openfire;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lpoezy.nexpa.activities.GroupChatHomeActivity;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.configuration.AppController;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;
import android.util.Log;

public class Account {
	public static final String HOST = "198.154.106.139";
	public static final int PORT = 5222;
	public static final String SERVICE = "198.154.106.139";
	public static final String SERVICE_LOGIN = "@198.154.106.139/Smack";
	public static final String SERVICE_UNAME = "admin";
	private static final String SERVICE_PWORD = "9db9b6749ffbf61e19aea4358a11d837";

	SQLiteHandler db;
	private XMPPConnection connection;

	public void ReTryCreateChatAccount(final Context cnt, final String username, final String password,
			final String email) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
					// connConfig.setRosterLoadedAtLogin(false);
					XMPPConnection connection = new XMPPConnection(connConfig);

					connection.disconnect();

					connection.connect();
					Log.e("XMPPChatDemoActivity", "Connected to " + connection.getHost());

					try {
						connection.login(SERVICE_UNAME, SERVICE_PWORD);
						Log.e("XMPPChatDemoActivity", "Logged in as " + connection.getUser());
						String kk = connection.getUser();
						AccountManager accountManager = connection.getAccountManager();
						Map<String, String> map = new HashMap<String, String>();
						map.put("username", username + "@vps.gigapros.com");
						map.put("name", "");
						map.put("password", password);
						map.put("email", email);
						map.put("creationDate", "" + System.currentTimeMillis() / 1000L);
						accountManager.createAccount(username, password, map);
						connection.disconnect();
						setConnection(null);

						validateAccountOnServer(cnt, username);

					} catch (XMPPException ex) {
						Log.e("XMPPChatDemoActivity",
								"Failed to Register in as " + username + " reason: " + ex.getLocalizedMessage());
						connection.disconnect();
					}

				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Chat server failed");
					Log.e("XMPPChatDemoActivity", "" + ex.getLocalizedMessage());
				}
			}
		});

		t.start();
	}

	public boolean CreateChatAccount(final Context cnt, final String username, final String password,
			final String email) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
					// connConfig.setRosterLoadedAtLogin(false);
					XMPPConnection connection = new XMPPConnection(connConfig);
					connection.disconnect();
					connection.connect();
					Log.e("XMPPChatDemoActivity", email + " Connected to " + connection.getHost());

					try {
						connection.login(SERVICE_UNAME, SERVICE_PWORD);
						Log.e("XMPP REGISTER", "Logged in as: " + connection.getUser());
						String kk = connection.getUser();
						AccountManager accountManager = connection.getAccountManager();
						Map<String, String> map = new HashMap<String, String>();
						map.put("username", username + "@vps.gigapros.com");
						map.put("name", "");
						map.put("password", password);
						map.put("email", email);
						map.put("creationDate", "" + System.currentTimeMillis() / 1000L);
						accountManager.createAccount(username, password, map);
						validateAccountOnServer(cnt, username);
						connection.disconnect();
						setConnection(null);

					} catch (XMPPException ex) {
						Log.e("XMPP REGISTER",
								"Failed to Register in as " + username + " reason: " + ex.getLocalizedMessage());
						connection.disconnect();
					}

				} catch (XMPPException ex) {
					Log.e("XMPP REGISTER", "XMPPException Chat server failed: " + ex.getLocalizedMessage());
				}
			}
		});

		t.start();

		Thread tj = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, email + SERVICE_LOGIN);
					// connConfig.setRosterLoadedAtLogin(false);
					XMPPConnection connection = new XMPPConnection(connConfig);
					L.error("user: " + username);
					L.error("password: " + password);
					L.error("email: " + email);
					connection.disconnect();
					connection.connect();
					L.error("XMPP REGISTER, Connected to " + connection.getHost());

					try {
						// XMPP REGISTER, Failed to log in as tet@test.com :
						// SASL authentication failed using mechanism
						// DIGEST-MD5:

						connection.login(username, password);
						Log.e("XMPP REGISTER", "Logged in as " + connection.getUser());
						Presence presence = new Presence(Presence.Type.available);
						connection.sendPacket(presence);
						setConnection(connection);
						XMPPLogic.getInstance().setConnection(connection);
						Roster roster = connection.getRoster();
						Collection<RosterEntry> entries = roster.getEntries();

						for (RosterEntry entry : entries) {
							L.error("XMPPChatDemoActivity, --------------------------------------");
							L.error("XMPPChatDemoActivity, RosterEntry " + entry);
							L.error("XMPPChatDemoActivity, User: " + entry.getUser());
							Presence entryPresence = roster.getPresence(entry.getUser());
							L.error("XMPPChatDemoActivity, Presence Status: " + entryPresence.getStatus());
							L.error("XMPPChatDemoActivity, Presence Type: " + entryPresence.getType());
							Presence.Type type = entryPresence.getType();

							if (type == Presence.Type.available)
								Log.e("XMPPChatDemoActivity", "Presence AVAILABLE");
							L.debug("XMPPChatDemoActivity, Presence : " + entryPresence);
						}

					} catch (XMPPException ex) {
						L.error("XMPP REGISTER, Failed to log in as " + email + " : " + ex.toString());
						setConnection(null);
					}

				} catch (XMPPException ex) {
					L.error("XMPP REGISTER, Chat server failed:" + ex.getLocalizedMessage());
					setConnection(null);
				}
			}
		});
		tj.start();

		try {
			tj.join();
			return true;
		} catch (InterruptedException e) {
			Log.e("AC FULL ERROR:", e.getLocalizedMessage() + " x ");
			return false;
		}
	}

	private void validateAccountOnServer(Context cn, final String uname) {

		db = new SQLiteHandler(cn);
		db.openToWrite();
		Log.e("Validate Account", "Register Responsexxxx: " + uname);

		// Tag used to cancel the request
		final String tag_string_req = "update_valid";
		StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e("Validate Account", "Register Response: " + response.toString());

				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");
					if (!error) {
						db.updateAccountValidate(1);
						Log.e("Validate Account", "Successfully Validated Chat Use On Server!: " + uname);
					} else {

						String errorMsg = jObj.getString("error_msg");
						db.updateAccountValidate(0);
						Log.e("Validate Account", "Registration Error lvl 1: " + errorMsg);
					}
				} catch (JSONException e) {
					Log.e("Validate Account", "Registration Error lvl 2: " + e.getLocalizedMessage());
					db.updateAccountValidate(0);
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// db.updateAccountValidate(0);
				// Log.e("Validate Account", "Registration Error lvl 3: " +
				// error.getMessage());

			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", tag_string_req);
				params.put("username", uname);
				params.put("is_valid", "1");

				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
	}

	public void LogInChatAccount(final String username, final String password, final String email,
			final OnXMPPConnectedListener callback) {
		try {
			GroupChatHomeActivity.updateStatusText(3);
		} catch (Exception e) {
			//Log.e("XMPP STATUS", "Trying to log-in... " + e);
			//L.debug("XMPP STATUS, Trying to log-in... " + e);
		}
		Log.e("XMPP STATUS", "Trying to log-in...");
		L.debug("XMPP STATUS, Trying to log-in...");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, email + SERVICE_LOGIN);

					XMPPConnection connection = new XMPPConnection(connConfig);
					Log.e("user", username);
					Log.e("password", password);
					Log.e("email", email);

					//connection.disconnect();

					connection.connect();
					L.debug("XMPPChatDemoActivity, Connected to " + connection.getHost());
					try {

						connection.login(username, password);
						Log.i("XMPPChatDemoActivity", "Logged in as " + connection.getUser());
						L.debug("XMPPChatDemoActivity, Logged in as " + connection.getUser());
						Presence presence = new Presence(Presence.Type.available);
						connection.sendPacket(presence);
						setConnection(connection);
						XMPPLogic.getInstance().setConnection(connection);
						
						// Roster roster = connection.getRoster();
						// Collection < RosterEntry > entries =
						// roster.getEntries();
						// L.debug("XMPPChatDemoActivity, entries: " +
						// entries.size());
						//
						// for (RosterEntry entry: entries) {
						// L.error("XMPPChatDemoActivity,
						// --------------------------------------");
						// L.error("XMPPChatDemoActivity, RosterEntry " +
						// entry);
						// L.error("XMPPChatDemoActivity, User: " +
						// entry.getUser());
						//
						// L.debug("XMPPChatDemoActivity, User: " +
						// entry.getUser());
						// Presence entryPresence =
						// roster.getPresence(entry.getUser());
						// Log.e("XMPPChatDemoActivity", "Presence Status: " +
						// entryPresence.getStatus());
						// Log.e("XMPPChatDemoActivity", "Presence Type: " +
						// entryPresence.getType());
						// Presence.Type type = entryPresence.getType();
						// if (type == Presence.Type.available)
						// L.error("XMPPChatDemoActivity, Presence AVIALABLE");
						// Log.d("XMPPChatDemoActivity", "Presence : " +
						// entryPresence);
						// }

						if (callback != null)
							callback.onXMPPConnected(connection);

					} catch (XMPPException ex) {
						Log.e("XMPPChatDemoActivity", "Failed to log in as " + email);
						Log.e("XMPPChatDemoActivity", ex.toString());
						setConnection(null);
					}
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Chat server failed: " + ex.getLocalizedMessage());
					L.error("XMPPChatDemoActivity, Chat server failed: " + ex.getLocalizedMessage());
					setConnection(null);
				}
			}
		});
		t.start();
	}

	public void ReLogInChatAccount(final String username, final String password, final String email,
			final String message) {
		Log.e("", "ENTER!");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, email + SERVICE_LOGIN);
					XMPPConnection connection = new XMPPConnection(connConfig);
					Log.e("user", username);
					Log.e("password", password);
					Log.e("email", email);
					connection.disconnect();
					connection.connect();
					Log.e("XMPPChatDemoActivity", "Connected to " + connection.getHost());
					try {
						connection.login(username, password);
						Log.i("XMPPChatDemoActivity", "Logged in as " + connection.getUser());
						Presence presence = new Presence(Presence.Type.available);
						connection.sendPacket(presence);
						Message msg = new Message(message, Message.Type.chat);
						connection.sendPacket(msg);
						setConnection(connection);
						XMPPLogic.getInstance().setConnection(connection);
						Roster roster = connection.getRoster();
						Collection<RosterEntry> entries = roster.getEntries();
						for (RosterEntry entry : entries) {
							Log.e("XMPPChatDemoActivity", "--------------------------------------");
							Log.e("XMPPChatDemoActivity", "RosterEntry " + entry);
							Log.e("XMPPChatDemoActivity", "User: " + entry.getUser());
							Presence entryPresence = roster.getPresence(entry.getUser());
							Log.e("XMPPChatDemoActivity", "Presence Status: " + entryPresence.getStatus());
							Log.e("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
							Presence.Type type = entryPresence.getType();
							if (type == Presence.Type.available)
								Log.e("XMPPChatDemoActivity", "Presence AVIALABLE");
							Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
						}
					} catch (XMPPException ex) {
						Log.e("XMPPChatDemoActivity", "Failed to log in as " + email);
						Log.e("XMPPChatDemoActivity", ex.toString());
						setConnection(null);
					}
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Chat server failed");
					setConnection(null);
				}
			}
		});
		t.start();
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		if (connection != null) {
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());
						Log.e("XMPPChatDemoActivity", "Text Recieved " + message.getBody() + " from " + fromName);
					}
				}
			}, filter);
		}
	}
}