package com.lpoezy.nexpa.chatservice;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.L;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

public class SyncProfilePictureService extends Service {
	
	private static final int MINUTE =  1000 * 60;

	private IBinder mBinder = new LocalBinder();
	
	private volatile Looper mServiceLooper;
	private volatile Handler mServiceHandler;

	private int retry = MINUTE;
	private int n = 0;

	public static boolean isRunning;
	
	public class LocalBinder extends Binder{
		public SyncProfilePictureService getService(){
			return SyncProfilePictureService.this;
			
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		mServiceLooper.quit();
		isRunning = false;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		isRunning = true;
		
		HandlerThread t = new HandlerThread("SynchProfilePictureService");
		t.start();
		
		mServiceLooper = t.getLooper();
		
//		for(int n=0;n<5;n++){
//			L.debug(""+(2<<n));
//		}
		
		mServiceHandler = new Handler(mServiceLooper);
		mServiceHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				ProfilePicture pic = new ProfilePicture();
				pic.setSyncedOnline(true);
				pic.downloadMyUnsyncPicProfileOffline(getApplicationContext());
				
				if(!pic.isSyncedOnline()){
					
					if(pic.saveImgOnline(getApplicationContext())){
						pic.setSyncedOnline(true);
						pic.saveOffline(getApplicationContext());
					}
					retry = MINUTE;
				}else{
					L.debug("(2<<"+n+") : "+(2<<n));
					retry = (2<<n)* MINUTE;
					if(n<2)n++;
					
					L.debug("SyncProfilePictureService, no changes to update online");
				}
				L.debug("SyncProfilePictureService, next update is after  "+TimeUnit.MILLISECONDS.toMinutes(retry)+" minute(s)");
				mServiceHandler.postDelayed(this, retry);
			}
		}, retry);
		
	}

}
