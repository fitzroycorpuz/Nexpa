package com.lpoezy.nexpa.chatservice;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.AlreadyLoggedInException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;

import com.lpoezy.nexpa.openfire.XMPPManager;
import com.lpoezy.nexpa.utility.L;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

public class XMPPService extends Service {
	private static final String DOMAIN = "198.154.106.139";// vps.gigapros.com
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "9db9b6749ffbf61e19aea4358a11d837";
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
		xmpp = XMPPManager.getInstance(XMPPService.this, DOMAIN, USERNAME, PASSWORD);
		xmpp.connect("onCreate");

		isRunning = true;
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
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

	public void register(final String uname, final String email, final String password, final OnUpdateScreenListener callback) {
		
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

	public void login(final String uname, final String password, final OnUpdateScreenListener callback) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (xmpp.connection.isConnected()) {

					try {
						xmpp.login(uname, password);
						
						if (xmpp.connection.isAuthenticated()) {

							callback.onUpdateScreen();
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
