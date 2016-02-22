package com.lpoezy.nexpa.activities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.Correspondents;
import com.lpoezy.nexpa.objects.NewMessage;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DividerItemDecoration;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

public class ChatHistoryListFragment extends Fragment implements Correspondent.OnCorrespondentUpdateListener {

	private OnShowChatHistoryListener mCallback;
	//private List<Correspondent> mBuddys;
	private Correspondents mBuddys;
	private ChatHistoryAdapter mAdapter;
	//private SwipeRefreshLayout mSwipeRefreshLayout;
	private SwipyRefreshLayout mSwipeRefreshLayout;
	private RecyclerView rvChatHistory;

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

		rvChatHistory = (RecyclerView) v.findViewById(R.id.rv_chat_history);
		rvChatHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvChatHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

		//mBuddys = new ArrayList<Correspondent>();
		mBuddys = new Correspondents();

		mAdapter = new ChatHistoryAdapter(getActivity(), mCallback);
		rvChatHistory.setAdapter(mAdapter);
		
		return v;
	}

	// receiving messages will be handle by receivedMessage
	// in ChatMessagesService
	private BroadcastReceiver mReceivedMessage = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			L.debug("=============ChatHistoryList, message received================");
			updateList();

		}

	};

	// receiving update from correspondent model
	// private BroadcastReceiver mReceivedCorrespondentUpdate = new
	// BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(final Context context, final Intent intent) {
	// L.debug("ChatHistoryList, correspondent update
	// "+mBuddys.get(0).getProfilePic());
	//
	// mAdapter.notifyDataSetChanged();
	//
	// }
	// };
	// List<Correspondent> correspondents;
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		getActivity().unregisterReceiver(mReceivedMessage);
		// getActivity().unregisterReceiver(mReceivedCorrespondentUpdate);

//		for (int i = 0; i < mBuddys.size(); i++) {
//			Correspondent correspondent = mBuddys.get(i);
//			correspondent.removeListener(this);
//		}
	}

	@Override
	public void onResume() {
		L.debug("ChatHistory, onResume");
		super.onResume();

		getActivity().registerReceiver(mReceivedMessage, new IntentFilter(AppConfig.ACTION_RECEIVED_MSG));
		
		updateList();
		// getActivity().registerReceiver(mReceivedCorrespondentUpdate, new
		// IntentFilter(Correspondent.ACTION_UPDATE));
//		int count = OneComment.getUnReadMsgCountOffline(getActivity());
//		L.debug("count: "+count+", mBuddys.isEmpty: "+mBuddys.isEmpty());

	}



	private void updateList() {
		L.debug("ChatHistory, updateList");
		mBuddys.clear();
		mBuddys.downloadOffline(getActivity());
		mBuddys.downloadLatestMsgOffline(getActivity());
		onCorrespondentUpdate();
		downloadProfilePics(mBuddys);
	}

	//protected void downloadProfilePics(final List<Correspondent> correspondents) {
	protected void downloadProfilePics(final Correspondents correspondents) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				final int MAX_THREAD = 5;
				int n = correspondents.size() < MAX_THREAD && correspondents.size() != 0 ? correspondents.size()
						: MAX_THREAD;
				ExecutorService exec = Executors.newFixedThreadPool(n);
				//L.debug("correspondents.size() "+correspondents.size());
				for (int i = 0; i < correspondents.size(); i++) {
					
					final Correspondent correspondent = correspondents.get(i);
					//L.debug("xxxxxxxxxx correspondent username"+correspondent.getUsername()+"xxxxxxx");
					correspondent.addListener(ChatHistoryListFragment.this);

					exec.execute(new Runnable() {

						@Override
						public void run() {
							correspondent.downloadCorrespondentIdOffline(getActivity());
							long userId = correspondent.getId();
							correspondent.downloadProfilePicOnline(getActivity(), userId);
							 

						}
					});

				}
				exec.shutdown();
				try {
					exec.awaitTermination(1, TimeUnit.MINUTES);
				} catch (InterruptedException e) {

				}
				
			}
		}).start();
		
	}

	private class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

		private LayoutInflater inflater;
		// private List<Correspondent> buddys;
		private OnShowChatHistoryListener listener;

		public ChatHistoryAdapter(Context context, OnShowChatHistoryListener listener) {
			this.inflater = LayoutInflater.from(context);
			// this.buddys = buddys;
			this.listener = listener;
		}

		@Override
		public int getItemCount() {

			return mBuddys.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder vh, int position) {
			vh.position = position;

			// only use username if fname has no value
			String name = mBuddys.get(position).getUsername();
					
			vh.tvBuddys.setText(name);

			NewMessage msg = mBuddys.get(position).getConversation().get(0);

			// only make the text bold if the msg is from a correpondent
			boolean isMsgUnread = msg.isUnread();

			SQLiteHandler db = new SQLiteHandler(getActivity());
			db.openToRead();
			//long userId = Long.parseLong(db.getLoggedInID());
			String username = db.getUsername();
			db.close();

			if (isMsgUnread && !username.equals( msg.getSenderName()))
				vh.tvMsg.setTypeface(null, Typeface.BOLD); // only text //
															// style(only bold)
			else
				vh.tvMsg.setTypeface(null, Typeface.NORMAL);
//
//			// L.debug("update view holder
//			// "+mBuddys.get(position).getProfilePic());
			 Bitmap rawImage = BitmapFactory.decodeResource(getActivity().getResources(),
				        R.drawable.pic_sample_girl);
			if (mBuddys.get(position).getProfilePic() != null) {

				
				rawImage = mBuddys.get(position).getProfilePic();
			}
			
			RoundedImageView riv = new RoundedImageView(getActivity());
			Bitmap circImage = riv.getCroppedBitmap(rawImage, 100);
			
			vh.imgProfilePic.setImageBitmap(circImage);
			
			vh.tvMsg.setText(msg.getBody());

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
				imgProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
				
				view.setOnClickListener(this);
			}

			@Override
			public void onClick(View v) {

				Correspondent buddy = mBuddys.get(position);
				// will set all the unread flags,
				// of the messages to read
				for (NewMessage comment : buddy.getConversation()) {
					if (comment.isUnread()) {
						comment.setUnread(false);
					}
				}

				// update adapter
				notifyDataSetChanged();

				listener.onShowChatHistory(buddy);
			}

		}
	}

	public interface OnShowChatHistoryListener {

		public void onShowChatHistory(Correspondent buddy);
	}

	@Override
	public void onCorrespondentUpdate() {
		
		rvChatHistory.post(new Runnable() {
			
			@Override
			public void run() {
				L.debug("ChatHistoryList, onCorrespondentUpdate");
				mAdapter.notifyDataSetChanged();
			}
		});
	}

}
