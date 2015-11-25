package com.lpoezy.nexpa.activities;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.activities.ChatHistoryListFragment.OnShowChatHistoryListener;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.sqlite.SessionManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;


public class ChatHistoryActivity extends AppCompatActivity implements OnShowChatHistoryListener{

	public static boolean isRunning;
	
	@Override
	public void onBackPressed() {
		
		//super.onBackPressed();
		SessionManager session = new SessionManager(getApplicationContext());
		if(session.isLoggedIn()){
			UserProfileActivity.promptYesNoDialog("Quit Toucan?",
					"Are you sure you want to log off?",
   					this,
   					"DEAC",
   					true);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_history);
	
		
		if(savedInstanceState==null){
			Fragment chatHistoryList = ChatHistoryListFragment.newInstance();
			getFragmentManager().beginTransaction()
			.add(R.id.frag_container, chatHistoryList, "ChatHistoryList")
			.commit();
		}
		
		
	    
//		if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
//			 ActionBar actionBar = getActionBar();
//			 actionBar.setHomeButtonEnabled(true);
//			 actionBar.setDisplayHomeAsUpEnabled(true);
//		}
//		else{
//			 Log.e("NOTICE","Device cannot handle ActionBar");
//		}
		
		
		//RecyclerView
		Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(myToolbar);
	    TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title);
	    mTitle.setText("MESSAGES");
	    myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
	    myToolbar.setTitle("");
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		isRunning = false;
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		isRunning = true;
	}

	@Override
	public void onShowChatHistory(Correspondent buddy) {

		Intent intentMes = new Intent(this, ChatActivity.class);
		intentMes.putExtra("userid", buddy.getId());
		intentMes.putExtra("email", buddy.getEmail());
		intentMes.putExtra("username", buddy.getUsername());
		intentMes.putExtra("fname", buddy.getFname());
		
		//L.debug("username "+buddy.getUsername()+", email: "+email);
		startActivity(intentMes);
	}
}
