package com.lpoezy.nexpa.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DividerItemDecoration;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatHistoryListFragment extends Fragment {

	private OnShowChatHistoryListener mCallback;
	private List<Correspondent> mBuddys;
	private ChatHistoryAdapter mAdapter;

	public static ChatHistoryListFragment newInstance() {
		ChatHistoryListFragment fragment = new ChatHistoryListFragment();
		return fragment;
	}

	public ChatHistoryListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {

		}
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		try {
			mCallback = (OnShowChatHistoryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.getClass().getSimpleName() + " must implement OnShowChatHistoryListener interface");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chat_history_list, container, false);

		RecyclerView rvChatHistory = (RecyclerView) v.findViewById(R.id.rv_chat_history);
		rvChatHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvChatHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

		mBuddys = new ArrayList<Correspondent>();

		mAdapter = new ChatHistoryAdapter(getActivity(), mCallback);
		rvChatHistory.setAdapter(mAdapter);
		return v;
	}

	// receiving messages will be handle by receivedMessage
	// in ChatMessagesService
	private BroadcastReceiver mReceivedMessage = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			L.debug("ChatHistoryList, message received");
			updateList();

		}

	};

	// receiving update from correspondent model
	private BroadcastReceiver mReceivedCorrespondentUpdate = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			L.debug("ChatHistoryList, correspondent update "+mBuddys.get(0).getProfilePic());
			
			mAdapter.notifyDataSetChanged();

		}
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		getActivity().unregisterReceiver(mReceivedMessage);
		getActivity().unregisterReceiver(mReceivedCorrespondentUpdate);
	}

	@Override
	public void onResume() {

		super.onResume();

		getActivity().registerReceiver(mReceivedMessage, new IntentFilter(AppConfig.ACTION_RECEIVED_MSG));
		getActivity().registerReceiver(mReceivedCorrespondentUpdate, new IntentFilter(Correspondent.ACTION_UPDATE));

		updateList();
	}

	private void updateList() {
		
		List<Correspondent> correspondents = Correspondent.downloadAllOffline(getActivity());
		
		for (Correspondent correspondent : correspondents) {
			correspondent.downloadProfilePicOnline(getActivity());
			L.debug("ChatHistory, updateList "+correspondent.getId());
			correspondent.downloadLatestMsgOffline(getActivity());
		}

		mBuddys.clear();
		mBuddys.addAll(correspondents);
		mAdapter.notifyDataSetChanged();

	}

	private class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

		private LayoutInflater inflater;
		//private List<Correspondent> buddys;
		private OnShowChatHistoryListener listener;

		public ChatHistoryAdapter(Context context, OnShowChatHistoryListener listener) {
			this.inflater = LayoutInflater.from(context);
			//this.buddys = buddys;
			this.listener = listener;
		}

		@Override
		public int getItemCount() {

			return mBuddys.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder vh, int position) {
			vh.position = position;
			vh.tvBuddys.setText(mBuddys.get(position).getUsername());

			boolean isMsgUnread = mBuddys.get(position).getConversation().get(0).isUnread;

			if (isMsgUnread)
				vh.tvMsg.setTypeface(null, Typeface.BOLD); // only text
															// style(only bold)
			else
				vh.tvMsg.setTypeface(null, Typeface.NORMAL);
			L.debug("update view holder "+mBuddys.get(position).getProfilePic());
            if(mBuddys.get(position).getProfilePic()!=null){
            	
            	RoundedImageView riv = new RoundedImageView(getActivity());
                Bitmap circImage = riv.getCroppedBitmap(mBuddys.get(position).getProfilePic(), 68);
               
            	vh.imgProfilePic.setImageBitmap(circImage);
            	
            }
			vh.tvMsg.setText(mBuddys.get(position).getConversation().get(0).comment);

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			View itemView = inflater.inflate(R.layout.row_chat_history, parent, false);
			return new ViewHolder(itemView);
		}

		class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

			TextView tvBuddys;
			TextView tvMsg;
			ImageView imgProfilePic;
			int position;

			public ViewHolder(View view) {
				super(view);
				tvBuddys = (TextView) view.findViewById(R.id.tv_buddys_name);
				tvMsg = (TextView) view.findViewById(R.id.tv_buddys_msg);
				imgProfilePic=(ImageView)view.findViewById(R.id.img_profile_pic);
				view.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {

				Correspondent buddy = mBuddys.get(position);
				listener.onShowChatHistory(buddy);
			}

		}
	}

	public interface OnShowChatHistoryListener {

		public void onShowChatHistory(Correspondent buddy);
	}

}
