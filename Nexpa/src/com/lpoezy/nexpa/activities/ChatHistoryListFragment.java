package com.lpoezy.nexpa.activities;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.utility.DividerItemDecoration;
import com.lpoezy.nexpa.utility.L;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
		
		try{
			mCallback = (OnShowChatHistoryListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.getClass().getSimpleName()+" must implement OnShowChatHistoryListener interface");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chat_history_list, container, false);
		
		RecyclerView rvChatHistory = (RecyclerView)v.findViewById(R.id.rv_chat_history);
		rvChatHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvChatHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		
		mBuddys = new ArrayList<Correspondent>();
		
	
		mAdapter = new ChatHistoryAdapter(getActivity(), mBuddys, mCallback);
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
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		getActivity().unregisterReceiver(mReceivedMessage);
	}
	
	
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		getActivity().registerReceiver(mReceivedMessage, new IntentFilter(AppConfig.ACTION_RECEIVED_MSG));
		
		updateList();
	}
	
	private void updateList() {
		L.debug("ChatHistory, updateList");
		List<Correspondent> correspondents = Correspondent.downloadAllOffline(getActivity());
		
		for(Correspondent correspondent : correspondents){
			correspondent.downloadLatestMsgOffline(getActivity());
		}
		
		mBuddys.clear();
		mBuddys.addAll(correspondents);
		mAdapter.notifyDataSetChanged();
		
	}

	private class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>{
		
		private LayoutInflater inflater;
		private List<Correspondent> buddys;
		private OnShowChatHistoryListener listener;


		public ChatHistoryAdapter(Context context, List<Correspondent> buddys, OnShowChatHistoryListener listener){
			this.inflater = LayoutInflater.from(context);
			this.buddys = buddys;
			this.listener = listener;
		}
		@Override
		public int getItemCount() {
			
			return buddys.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder vh, int position) {
			vh.position = position;
			vh.tvBuddys.setText(buddys.get(position).getUsername());
			
			boolean isMsgUnread = buddys.get(position).getConversation().get(0).isUnread;
			
			if(isMsgUnread)
				vh.tvMsg.setTypeface(null,Typeface.BOLD); //only text style(only bold)
			else 
				vh.tvMsg.setTypeface(null,Typeface.NORMAL);
			
			vh.tvMsg.setText(buddys.get(position).getConversation().get(0).comment);
			vh.tvMsg.setText("sdfasfas");
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			View itemView = inflater.inflate(R.layout.row_chat_history, parent, false);
			return new ViewHolder(itemView);
		}
		
		
		class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

			 TextView tvBuddys ;
			 TextView tvMsg;
			int position;
			 

			public ViewHolder(View view) {
				super(view);
				tvBuddys =(TextView)view.findViewById(R.id.tv_buddys_name);
				tvMsg =(TextView)view.findViewById(R.id.tv_buddys_msg);
				view.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {
				
				Correspondent buddy = buddys.get(position);
				listener.onShowChatHistory(buddy);
			}
			
		}
	}
	
	public interface OnShowChatHistoryListener{
		
		public void onShowChatHistory(Correspondent buddy);
	}

}
