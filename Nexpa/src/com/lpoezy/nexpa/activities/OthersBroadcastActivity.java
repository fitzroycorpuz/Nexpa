package com.lpoezy.nexpa.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.utility.L;

public class OthersBroadcastActivity extends Activity {
	
	public static final String TAG_USERNAME = "TAG_USERNAME";
	public static final String TAG_USER_ID = "TAG_USER_ID";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_others_broadcast);

		if (savedInstanceState == null) {

			long userId = getIntent().getLongExtra(TAG_USER_ID, -1);
			String username = getIntent().getStringExtra(TAG_USERNAME);

			Fragment frag = OthersBroadcastsFragment.newInstance(userId, username);
			getFragmentManager().beginTransaction()
					.add(R.id.frag_container, frag, "OthersBroadcastsList")
					.commit();
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			L.error("actionBar " + actionBar);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		} else {
			Log.e("NOTICE", "Device cannot handle ActionBar");
		}

	}

}
