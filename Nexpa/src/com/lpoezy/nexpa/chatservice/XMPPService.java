package com.lpoezy.nexpa.chatservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.SmackException.AlreadyLoggedInException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp;
import org.jivesoftware.smackx.hoxt.packet.HttpMethod;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppReq;
import org.jivesoftware.smackx.hoxt.packet.HttpOverXmppResp;
import org.jivesoftware.smackx.shim.packet.Header;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;
import org.json.JSONException;
import org.json.JSONObject;

import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.openfire.OfUserIQ;
import com.lpoezy.nexpa.openfire.XMPPManager;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

public class XMPPService extends Service {
	private static final String DOMAIN = "198.154.106.139";// vps.gigapros.com ||
														// 198.154.106.139
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "9db9b6749ffbf61e19aea4358a11d837";// 9db9b6749ffbf61e19aea4358a11d837
	public static ConnectivityManager cm;
	public static XMPPManager xmpp;
	public static boolean ServerchatCreated = false;
	private static boolean isRunning = false;
	String text = "";

	@Override
	public IBinder onBind(final Intent intent) {
		return new LocalBinder<XMPPService>(XMPPService.this);
	}

	public Chat chat;

	@Override
	public void onCreate() {
		super.onCreate();
		cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		xmpp = XMPPManager.getInstance(XMPPService.this, DOMAIN, USERNAME,
				PASSWORD);
		xmpp.connect("onCreate");

		isRunning = true;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		xmpp.connection.disconnect();
		isRunning = false;
	}

	public static boolean isNetworkConnected() {
		return cm.getActiveNetworkInfo() != null;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	public void resetPassword(final String email,
			final OnUpdateScreenListener callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				L.debug("resetPassword " + AppConfig.URL_SEND_EMAIL);
				HashMap<String, String> postDataParams = new HashMap<String, String>();
				postDataParams.put("tag", "reset_password");
				postDataParams.put("email", email);

				final String spec = AppConfig.URL_SEND_EMAIL;
				String webPage = HttpUtilz.makeRequest(spec, postDataParams);
				// L.debug("webPage: "+webPage);
				JSONObject result;
				try {
					result = new JSONObject(webPage);
					final boolean error = result.getBoolean("error");

					callback.onUpdateScreen();

				} catch (JSONException e) {
					L.error("" + e);
				}

			}
		}).start();

	}

	public void register(final String uname, final String email,
			final String password, final OnUpdateScreenListener callback) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (xmpp.connection.isConnected()) {

					try {
						xmpp.register(uname, password, email);

						callback.onUpdateScreen();

					} catch (NoResponseException | NotConnectedException e) {
						callback.onResumeScreen("User is not, or no longer, connected.");
					} catch (XMPPErrorException e) {
						callback.onResumeScreen("User Name already exists, please enter another one.");
					}

				} else {

					L.error("Not connected to openfire server!!!");

				}

			}
		}).start();

	}

	public void login(final String uname, final String password,
			final OnUpdateScreenListener callback) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (xmpp.connection.isConnected()) {

					try {
						xmpp.login(uname, password);

						if (xmpp.connection.isAuthenticated()) {
 
//							HttpOverXmppReq req = new HttpOverXmppReq(
//									HttpMethod.GET, "ldonios-pc/nexpa/json/?name=c");
//							req.setVersion("1.1");
//
//							// prepare headers
//							List<Header> list = new ArrayList<Header>();
//							list.add(new Header("Host", "ldonios-pc"));
//							list.add(new Header("Content-Type", "application/x-www-form- urlencoded"));
//							req.setHeaders(new HeadersExtension(list));
//
//							req.setTo("momo@ldonios-pc/Smack");
//
//							xmpp.connection.sendIqWithResponseCallback(req,
//									new PacketListener() {
//										public void processPacket(Stanza packet) {
//											HttpOverXmppResp resp = (HttpOverXmppResp) packet;
//											// check HTTP response code
//											L.debug("resp.getStatusCode() "
//													+ resp.getStatusCode());
//											if (resp.getStatusCode() == 200) {
//												// get content of the response
//												NamedElement child = resp
//														.getData().getChild();
//												// check which type of content
//												// of the response arrived
//												if (child instanceof AbstractHttpOverXmpp.Xml) {
//													// print the message and
//													// anxiously read if from
//													// console ;)
//													L.debug(((AbstractHttpOverXmpp.Xml) child)
//															.getText());
//												} else {
//													// process other
//													// AbstractHttpOverXmpp.DataChild
//													// subtypes
//													L.error("error!!!");
//												}
//											}
//										}
//									});
							
							
							
							
							
							callback.onUpdateScreen();
							// download user info

							//
							// OfUserIQ ofUserIQ = new OfUserIQ();
							// ofUserIQ.setTo("ldonios-pc");
							// //ofUserIQ.setFrom(uname+"@ldonios-pc");
							// ofUserIQ.setType(IQ.Type.set);
							//
							// //ServiceDiscoveryManager.getInstanceFor(xmpp.connection).addFeature("urn:xmpp:http");
							// xmpp.connection.sendIqWithResponseCallback(ofUserIQ,
							// new StanzaListener() {
							//
							// @Override
							// public void processPacket(Stanza stanza) throws
							// NotConnectedException {
							// L.debug("Send IQ with Response, ****** message "
							// + stanza);
							//
							// }
							// }, new ExceptionCallback() {
							// @Override
							// public void processException(Exception exception)
							// {
							// exception.printStackTrace();
							// L.error("IO archjieve Exception, "+
							// exception.getMessage());
							// }
							// }, 5000);

							// save user info
						}

					} catch (AlreadyLoggedInException e) {
						callback.onResumeScreen("This user is already logged in, please use another login name.");
					} catch (SmackException e) {
						callback.onResumeScreen("User is not, or no longer, connected.");

					}

				} else {

					L.error("Not conncted to openfire server!!!");

				}

			}
		}).start();

	}

	public interface OnUpdateScreenListener {
		public void onResumeScreen(String errorMsg);

		public void onUpdateScreen();
	}

}
