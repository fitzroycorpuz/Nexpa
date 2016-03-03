
package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Announcement;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyBroadcastsFragment extends Fragment {
	

	List<Announcement> mAnouncements;
	//private MyBroascastsAdapter mAdapter;
	private RecyclerView mRvBroadcasts;
	protected String mUsername;
	
	
	public static MyBroadcastsFragment newInstance() {
		MyBroadcastsFragment fragment = new MyBroadcastsFragment();
		
		return fragment;
	}

	public MyBroadcastsFragment() {
		// Required empty public constructor
	}
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			
		}
	}
	
	
	ParallaxRecyclerAdapter<Announcement> mAdapter;
	private ImageView mImgProfile;
	private TextView mTvJobTitle;
	private TextView mTvUname;
	private TextView mTvUrl0;
	private TextView mTvUrl1;
	private TextView mTvUrl2;
	
	private BroadcastReceiver mActionUserProfileUpdatedReceived = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			resetProfilePic();
			resetUserInfo();
		}
	};
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_broadcasts, container, false);
		
		mRvBroadcasts = (RecyclerView)v.findViewById(R.id.rv_my_broadcasts);
		
		//mRvChatHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		mAnouncements = new ArrayList<Announcement>();
		
		//mAdapter = new MyBroascastsAdapter(getActivity());
		
		mAdapter = new ParallaxRecyclerAdapter<Announcement>(mAnouncements) {
			
			

			@Override
			public void onBindViewHolderImpl(android.support.v7.widget.RecyclerView.ViewHolder viewHolder,
					ParallaxRecyclerAdapter<Announcement> adapter, int position) {
				
				
				Announcement ann = adapter.getData().get(position);
				
				ViewHolder vh = (ViewHolder)viewHolder;
				
				vh .tvBroadMsg.setText(ann.getMessage());
				vh.tvReply.setText("REACHED " + ann.getReach());
				vh.ImgReply.setBackgroundResource(R.drawable.btn_reach);
				vh.tvBroadFrm.setText(mUsername);
				
				DateUtils du = new DateUtils();
				String dateFormatted = du.getMinAgo(ann.getDate());
				
				vh.tvDateBroad.setText(dateFormatted);
				 
				vh.tvLocLocal.setVisibility(View.GONE);
				
				if(ann.getLocLocal()!=null && !ann.getLocLocal().isEmpty())
				{
					String strLoc = "near "+ ann.getLocLocal();
					
					vh.tvLocLocal.setText(strLoc);
					vh.tvLocLocal.setVisibility(TextView.VISIBLE);
				}
				
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
	                    0, LayoutParams.WRAP_CONTENT , 1.2f);
				vh.btnReply.setLayoutParams(param);
				
				
			}

			@Override
			public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup parent, ParallaxRecyclerAdapter<Announcement> adapter, int i) {
				View itemView = getActivity().getLayoutInflater().inflate(R.layout.list_broadcast, parent, false);
				return new ViewHolder(itemView);
			}

			@Override
			public int getItemCountImpl(ParallaxRecyclerAdapter<Announcement> adapter) {
				
				return mAnouncements.size();
			}
			
			
		};
		
		mRvBroadcasts.setLayoutManager(new LinearLayoutManager(getActivity()));
        View header = getActivity().getLayoutInflater().inflate(R.layout.activity_userprofile_header, mRvBroadcasts, false);
        mAdapter.setParallaxHeader(header, mRvBroadcasts);
        mAdapter.setData(mAnouncements);
        mRvBroadcasts.setAdapter(mAdapter);
        
        mImgProfile = (ImageView) header.findViewById(R.id.img_profile);
        
        
        mTvJobTitle = (TextView)header.findViewById(R.id.tv_job_title);
        mTvUname = (TextView)header.findViewById(R.id.tv_uname);
        mTvUrl0 = (TextView)header.findViewById(R.id.tv_url0);
        mTvUrl1 = (TextView)header.findViewById(R.id.tv_url1);
        mTvUrl2 = (TextView)header.findViewById(R.id.tv_url2);
        
        
        ((ImageView)header.findViewById(R.id.img_settings)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
			}
		});
        
        ((Button)header.findViewById(R.id.btn_edit_profile)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				//mCallback.onShowEditProfileScreen();
				EditProfileFragment editProfileFrag = EditProfileFragment.newInstance();
				
				editProfileFrag.show(getFragmentManager().beginTransaction(), EditProfileFragment.TAG);
				
			}
		});
		
		return v;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		getActivity().unregisterReceiver(mActionUserProfileUpdatedReceived);
	}
	
	@Override
	public void onResume() {
		L.debug("MyBroadcastFragment, onResume");
		super.onResume();
		
		getActivity().registerReceiver(mActionUserProfileUpdatedReceived , new IntentFilter(AppConfig.ACTION_USER_PROFILE_UPDATED));
		
		resetProfilePic();
		resetUserInfo();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());
				db.openToRead();
				
				final List<Announcement> announcements = db.downloadPersonalBroadcasts();
				
				mUsername = GroupChatHomeActivity.displayName(db.getFName() + "", db.getUsername());
				
				db.close();
				
				
				mRvBroadcasts.post(new Runnable() {
					
					@Override
					public void run() {
						
						mAnouncements.clear();
						mAnouncements.addAll(announcements);
						
						L.debug("mAnouncements.size "+mAnouncements.size());
						mAdapter.notifyDataSetChanged();
					}
				});
				
			}
		}).start();
	}
	
	
	private void resetUserInfo() {
		
		SQLiteHandler db = new SQLiteHandler(getActivity());
		db.openToRead();
		
		UserProfile profile = new UserProfile();
		profile.setId(Long.parseLong(db.getLoggedInID()));
		profile.downloadOffline(getActivity());
		
		mTvJobTitle.setVisibility(View.GONE);
		mTvUname.setVisibility(View.GONE);
		mTvUrl0.setVisibility(View.GONE);
		mTvUrl1.setVisibility(View.GONE);
		mTvUrl2.setVisibility(View.GONE);
		
		if(profile.getProfession()!=null &&!profile.getProfession().equalsIgnoreCase("null") && !profile.getProfession().equals("")){
			mTvJobTitle.setVisibility(View.VISIBLE);
			mTvJobTitle.setText(profile.getProfession());
		}
		
		if(profile.getUsername()!=null &&!profile.getUsername().equalsIgnoreCase("null") && !profile.getUsername().equals("")){
			mTvUname.setVisibility(View.VISIBLE);
			mTvUname.setText(profile.getUsername());
		}
		
		if(profile.getUrl0()!=null &&!profile.getUrl0().equalsIgnoreCase("null") && !profile.getUrl0().equals("")){
			mTvUrl0.setVisibility(View.VISIBLE);
			mTvUrl0.setText(profile.getUrl0());
		}
		
		if(profile.getUrl1()!=null &&!profile.getUrl1().equalsIgnoreCase("null") && !profile.getUrl1().equals("")){
			mTvUrl1.setVisibility(View.VISIBLE);
			mTvUrl1.setText(profile.getUrl1());
		}
		
		if(profile.getUrl2()!=null &&!profile.getUrl2().equalsIgnoreCase("null") && !profile.getUrl2().equals("")){
			mTvUrl2.setVisibility(View.VISIBLE);
			mTvUrl2.setText(profile.getUrl2());
		}
		
		db.close();
		
	}

	private void resetProfilePic(){
		
		String imgDecodableString = ProfilePicture.getUserImgDecodableString(getActivity());
		
        Bitmap rawImage = BitmapFactory.decodeResource(getActivity().getResources(),
        R.drawable.pic_sample_girl);
       L.debug("MyBroadcastFragment, imgDecodableString "+imgDecodableString);
        if(imgDecodableString!=null && !imgDecodableString.isEmpty()){
        	
        	// Get the dimensions of the View
            int targetW = mImgProfile.getWidth();
            int targetH = mImgProfile.getHeight();
            
            BmpFactory  bmpFactory = new BmpFactory();
        	
        	Bitmap newImage = bmpFactory.getBmpWithTargetWTargetHFrm(targetW, targetH, imgDecodableString);
          
        	if(newImage!=null)rawImage = newImage;
        }
        
        L.debug("imgDecodableString "+imgDecodableString+", rawImage "+rawImage);
        RoundedImageView riv = new RoundedImageView(getActivity());
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 100);
        mImgProfile.setImageBitmap(circImage);
       // mImgProfile.setImageBitmap(rawImage);
	}
	
//	private class MyBroascastsAdapter extends RecyclerView.Adapter<MyBroascastsAdapter.ViewHolder>{
//		
//
//
//		private LayoutInflater inflater;
//		
//
//		public MyBroascastsAdapter(Context context) {
//			
//			this.inflater = LayoutInflater.from(context);
//			
//		}
//
//		@Override
//		public int getItemCount() {
//			
//			return mAnouncements.size();
//		}
//
//		@Override
//		public void onBindViewHolder(ViewHolder vh, int position) {
//
//			Announcement ann = mAnouncements.get(position);
//			
//			vh.tvBroadMsg.setText(ann.getMessage());
//			vh.tvReply.setText("REACHED " + ann.getReach());
//			vh.ImgReply.setBackgroundResource(R.drawable.btn_reach);
//			vh.tvBroadFrm.setText(mUsername);
//			
//			DateUtils du = new DateUtils();
//			String dateFormatted = du.getMinAgo(ann.getDate());
//			
//			vh.tvDateBroad.setText(dateFormatted);
//			 
//			vh.tvLocLocal.setVisibility(View.GONE);
//			
//			if(ann.getLocLocal()!=null && !ann.getLocLocal().isEmpty())
//			{
//				String strLoc = "near "+ ann.getLocLocal();
//				
//				vh.tvLocLocal.setText(strLoc);
//				vh.tvLocLocal.setVisibility(TextView.VISIBLE);
//			}
//			
//			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                    0, LayoutParams.WRAP_CONTENT , 1.2f);
//			vh.btnReply.setLayoutParams(param);
//			
//			
//			
//		}
//
//		@Override
//		public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
//			
//			View itemView = inflater.inflate(R.layout.list_broadcast, parent, false);
//			return new ViewHolder(itemView);
//		} 
//		
//		class ViewHolder extends RecyclerView.ViewHolder{
//
//			 TextView tvBroadId;
//			 TextView tvBroadFrm;
//			 TextView tvDateBroad;
//			 TextView tvLocLocal;
//			 TextView tvBroadMsg;
//			 TextView tvReach;
//			 TextView tvReply;
//			 ImageView ImgReply;
//			 LinearLayout btnReply;
//			 TextView tvBroadFrmRaw;
//
//			public ViewHolder(View itemView) {
//				super(itemView);
//				
//				tvBroadId = (TextView) itemView.findViewById(R.id.broad_id);
//				tvBroadFrm = (TextView) itemView.findViewById(R.id.broad_from);
//				tvDateBroad = (TextView) itemView.findViewById(R.id.date_broad);
//				tvLocLocal = (TextView) itemView.findViewById(R.id.location_local);
//				tvBroadMsg = (TextView) itemView.findViewById(R.id.broad_message);
//				tvReach = (TextView) itemView.findViewById(R.id.reach);
//				tvReply = (TextView) itemView.findViewById(R.id.txtReply);
//				ImgReply = (ImageView) itemView.findViewById(R.id.imgReply);
//				btnReply = (LinearLayout) itemView.findViewById(R.id.btnReply);
//				tvBroadFrmRaw = (TextView) itemView.findViewById(R.id.broad_from_raw);
//				
//			
//			}
//			
//		}
//	}
	
	static class ViewHolder extends RecyclerView.ViewHolder{

		 TextView tvBroadId;
		 TextView tvBroadFrm;
		 TextView tvDateBroad;
		 TextView tvLocLocal;
		 TextView tvBroadMsg;
		 TextView tvReach;
		 TextView tvReply;
		 ImageView ImgReply;
		 LinearLayout btnReply;
		 TextView tvBroadFrmRaw;

		public ViewHolder(View itemView) {
			super(itemView);
			
			tvBroadId = (TextView) itemView.findViewById(R.id.broad_id);
			tvBroadFrm = (TextView) itemView.findViewById(R.id.broad_from);
			tvDateBroad = (TextView) itemView.findViewById(R.id.date_broad);
			tvLocLocal = (TextView) itemView.findViewById(R.id.location_local);
			tvBroadMsg = (TextView) itemView.findViewById(R.id.broad_message);
			tvReach = (TextView) itemView.findViewById(R.id.reach);
			tvReply = (TextView) itemView.findViewById(R.id.txtReply);
			ImgReply = (ImageView) itemView.findViewById(R.id.imgReply);
			btnReply = (LinearLayout) itemView.findViewById(R.id.btnReply);
			tvBroadFrmRaw = (TextView) itemView.findViewById(R.id.broad_from_raw);
			
		
		}
	}
	
	
}
