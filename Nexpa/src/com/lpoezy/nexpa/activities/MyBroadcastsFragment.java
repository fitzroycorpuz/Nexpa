package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Announcement;
import com.lpoezy.nexpa.parallaxrecyclerview.HeaderLayoutManagerFixed;
import com.lpoezy.nexpa.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DividerItemDecoration;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        
        
        
        RoundedImageView riv = new RoundedImageView(getActivity());
        Bitmap rawImage = BitmapFactory.decodeResource(getActivity().getResources(),
        R.drawable.pic_sample_girl);
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 400);
        ImageView imgProfile = (ImageView) header.findViewById(R.id.img_profile);
        imgProfile.setImageBitmap(circImage);
        
        
        ((ImageView)header.findViewById(R.id.img_settings)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
			}
		});
		
		return v;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
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
