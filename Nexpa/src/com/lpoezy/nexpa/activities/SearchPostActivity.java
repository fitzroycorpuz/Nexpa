package com.lpoezy.nexpa.activities;

import com.lpoezy.nexpa.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchPostActivity extends Activity implements OnItemClickListener{

	String strSearch;
	String strType;
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		SearchPostActivity.this.overridePendingTransition(R.anim.anim_appear, R.anim.anim_enter_right);
	    return true;

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_post);
		
		//  overridePendingTransition(R.anim.anim_leave, R.anim.anim_enter);
		   overridePendingTransition(R.anim.anim_enter, R.anim.anim_leave);
		   ActionBar actionBar = getActionBar();
		   actionBar.setDisplayHomeAsUpEnabled(true);
		   
		   Intent intent = getIntent();
		   strSearch = intent.getStringExtra("SEARCH");
		   strType = intent.getStringExtra("TYPE");
		   TextView txtMes  = (TextView)findViewById(R.id.txtMes);
		   if (strType.equals("hashtag")){
			   txtMes.setText("Search results for '"+ strSearch + "'");
		   }
		   
	}

}
