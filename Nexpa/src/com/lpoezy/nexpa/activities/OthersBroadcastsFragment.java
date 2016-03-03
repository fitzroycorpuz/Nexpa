
package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Announcement;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OthersBroadcastsFragment extends Fragment implements Correspondent.OnCorrespondentUpdateListener{
	
	List<Announcement> mAnouncements;
	//private MyBroascastsAdapter mAdapter;
	private RecyclerView mRvBroadcasts;
	protected String mUsername;
	
	
	public static OthersBroadcastsFragment newInstance(long id, String name) {
		OthersBroadcastsFragment fragment = new OthersBroadcastsFragment();
		Bundle args = new Bundle();
		args.putLong(OthersBroadcastActivity.TAG_USER_ID, id);
		args.putLong(OthersBroadcastActivity.TAG_USERNAME, id);
		fragment.setArguments(args);
		return fragment;
	}

	public OthersBroadcastsFragment() {
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
	private Correspondent mCorrespondent;
	protected long userId;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_broadcasts, container, false);
		
		mRvBroadcasts = (RecyclerView)v.findViewById(R.id.rv_my_broadcasts);
		
		//mRvChatHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		mAnouncements = new ArrayList<Announcement>();
		
		//mAdapter = new MyBroascastsAdapter(getActivity());
		
		userId = getArguments().getLong(OthersBroadcastActivity.TAG_USER_ID, -1);
		
		mAdapter = new ParallaxRecyclerAdapter<Announcement>(mAnouncements) {
			
			
			@Override
			public void onBindViewHolderImpl(android.support.v7.widget.RecyclerView.ViewHolder viewHolder,
					ParallaxRecyclerAdapter<Announcement> adapter, int position) {
				
				
				Announcement ann = adapter.getData().get(position);
				
				ViewHolder vh = (ViewHolder)viewHolder;
				
				vh .tvBroadMsg.setText(ann.getMessage());
				vh.tvReply.setText("REACHED " + ann.getReach());
				vh.tvReply.setVisibility(View.INVISIBLE);
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
        View header = getActivity().getLayoutInflater().inflate(R.layout.activity_their_broadcasts_header, mRvBroadcasts, false);
        mAdapter.setParallaxHeader(header, mRvBroadcasts);
        mAdapter.setData(mAnouncements);
        mRvBroadcasts.setAdapter(mAdapter);
        
        mImgProfile = (ImageView) header.findViewById(R.id.img_profile);
        
        
        mTvJobTitle = (TextView)header.findViewById(R.id.tv_job_title);
        mTvUname = (TextView)header.findViewById(R.id.tv_uname);
        mTvUrl0 = (TextView)header.findViewById(R.id.tv_url0);
        mTvUrl1 = (TextView)header.findViewById(R.id.tv_url1);
        mTvUrl2 = (TextView)header.findViewById(R.id.tv_url2);
        
        
		return v;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		
	}
	
	@Override
	public void onResume() {
		L.debug("MyBroadcastFragment, onResume");
		super.onResume();
	
		resetProfilePic();
		resetUserInfo();
		
		SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());
		db.openToRead();
		
		final List<Announcement> announcements = db.downloadOthersBroadcasts(mUsername);
		
		//mUsername = GroupChatHomeActivity.displayName(db.getFName() + "", db.getUsername());
		mAnouncements.clear();
		mAnouncements.addAll(announcements);
		
		L.debug("mAnouncements.size "+mAnouncements.size());
		mAdapter.notifyDataSetChanged();
		db.close();
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				
//				SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());
//				db.openToRead();
//				
//				final List<Announcement> announcements = db.downloadPersonalBroadcasts();
//				
//				mUsername = GroupChatHomeActivity.displayName(db.getFName() + "", db.getUsername());
//	
//				db.close();
//				
//				
//				mImgProfile.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						L.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//						mAnouncements.clear();
//						mAnouncements.addAll(announcements);
//						
//						L.debug("mAnouncements.size "+mAnouncements.size());
//						mAdapter.notifyDataSetChanged();
//					}
//				});
//				
//			}
//		}).start();
	}
	
	
	private void resetUserInfo() {
		
		SQLiteHandler db = new SQLiteHandler(getActivity());
		db.openToRead();
		
		UserProfile profile = new UserProfile();
		profile.setId(this.userId);
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
			mUsername = profile.getUsername();
			mTvUname.setText(mUsername);
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
		
        Bitmap rawImage = BitmapFactory.decodeResource(getResources(),
        R.drawable.pic_sample_girl);
      
        RoundedImageView riv = new RoundedImageView(getActivity());
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 100);
        mImgProfile.setImageBitmap(circImage);
        
        mCorrespondent = new Correspondent();
		mCorrespondent.addListener(this);
        
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				mCorrespondent.downloadProfilePicOnline(getActivity(), userId);
				
			}
		}).start();
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
	
	@Override
	public void onCorrespondentUpdate() {
		Bitmap rawImage = mCorrespondent.getProfilePic();
		try{
			RoundedImageView riv = new RoundedImageView(getActivity());
	        final Bitmap circImage = riv.getCroppedBitmap(rawImage, 100);
	        
	        mImgProfile.post(new Runnable() {
				
				@Override
				public void run() {
					
					mImgProfile.setImageBitmap(circImage);
					
				}
			});
		}catch(Exception e){}
		
	}
	
	
}
