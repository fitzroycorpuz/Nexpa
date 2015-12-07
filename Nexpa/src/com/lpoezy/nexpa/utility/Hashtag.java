package com.lpoezy.nexpa.utility;
import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.activities.GroupChatHomeActivity;
import com.lpoezy.nexpa.activities.SearchPostActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Hashtag extends ClickableSpan{

	Context context;
	 TextPaint textPaint;
	     public Hashtag(Context ctx) {
	     super();
	     context = ctx;
	 }

	  @Override
	 public void updateDrawState(TextPaint ds) {
	    textPaint = ds;
	    ds.setColor(context.getResources().getColor(R.color.toucan_green));
	    ds.setARGB(255, 3, 166, 120);
	    ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
	 }
	  
	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		 TextView tv = (TextView) widget;
	     Spanned s = (Spanned) tv.getText();
	     int start = s.getSpanStart(this);
	     int end = s.getSpanEnd(this);
	     String theWord = s.subSequence(start + 1, end).toString();
	     // you can start another activity here 
	    // Toast.makeText(context, String.format("Tag : %s", theWord), 10 ).show();
	     
			Intent iv = new Intent(context,
                    SearchPostActivity.class);

            if (iv != null) {
            	iv.putExtra("TYPE", "hashtag"); 
            	iv.putExtra("SEARCH", "#"+theWord); 
            	context.startActivity(iv);
            }
	}
	

}
