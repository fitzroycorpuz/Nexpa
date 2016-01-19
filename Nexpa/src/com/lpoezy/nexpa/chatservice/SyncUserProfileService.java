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

public class SyncUserProfileService extends Service {
	
	private static final int MINUTE =  1000 * 60;

	private IBinder mBinder = new LocalBinder();
	
	private volatile Looper mServiceLooper;
	private volatile Handler mServiceHandler;

	private int retry = 1000 * 60;
	private int n = 0;

	public static boolean isRunning;
	
	public class LocalBinder extends Binder{
		public SyncUserProfileService getService(){
			return SyncUserProfileService.this;
			
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
		
		HandlerThread t = new HandlerThread("SyncUserProfileService");
		t.start();
		
		mServiceLooper = t.getLooper();
		
//		for(int n=0;n<5;n++){
//			L.debug(""+(2<<n));
//		}
		
		mServiceHandler = new Handler(mServiceLooper);
		mServiceHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				UserProfile userProfile = new UserProfile();
				userProfile.setSyncedOnline(true);
				userProfile.downloadMyUnsyncedDetailsOffline(getApplicationContext());
				
				if(!userProfile.isSyncedOnline()){
					
					if(userProfile.saveOnline(getApplicationContext())){
						userProfile.setSyncedOnline(true);
						userProfile.updateOffline(getApplicationContext());
					}
					retry = MINUTE;
				}else{
					
					retry = (2<<n)* MINUTE;
					if(n<3)n++;
					
					L.debug("SyncUserProfileService, no changes to update online");
				}
				L.debug("SyncUserProfileService, next update is after  "+TimeUnit.MILLISECONDS.toMinutes(retry)+" minute(s)");
				mServiceHandler.postDelayed(this, retry);
			}
		}, retry);
		
	}

}
