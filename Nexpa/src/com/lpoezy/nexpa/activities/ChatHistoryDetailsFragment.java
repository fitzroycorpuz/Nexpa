package com.lpoezy.nexpa.activities;

import com.lpoezy.nexpa.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass. Use the
 * {@link ChatHistoryDetailsFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class ChatHistoryDetailsFragment extends Fragment {
	

	
	public static ChatHistoryDetailsFragment newInstance() {
		ChatHistoryDetailsFragment fragment = new ChatHistoryDetailsFragment();
		
		return fragment;
	}

	public ChatHistoryDetailsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_chat_history_details,
				container, false);
	}

}
