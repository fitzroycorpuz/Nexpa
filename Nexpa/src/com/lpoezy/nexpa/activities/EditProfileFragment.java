package com.lpoezy.nexpa.activities;

import com.lpoezy.nexpa.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditProfileFragment extends DialogFragment{
	
	public EditProfileFragment newInstance(){
		
		return new EditProfileFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.activity_profile_personal, container, false);
		
		return v;
	}

}
