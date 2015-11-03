package com.lpoezy.nexpa.utility;

import com.lpoezy.nexpa.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NiceDialog {
    private static Dialog dialogStatus;
    static LinearLayout lnHeader;
    static TextView edtStatusHead;
    static TextView edtStatus;
    public static void promptDialog(String caption, String message, Context cn, String type){
    	
    	 
    	 dialogStatus = new Dialog(cn);
         dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialogStatus.setContentView(R.layout.dialog_info);
         Button dialogButton = (Button) dialogStatus.findViewById(R.id.dialogButtonCancel);
         lnHeader = (LinearLayout) dialogStatus.findViewById(R.id.lnHeader);
         edtStatusHead = (TextView) dialogStatus.findViewById(R.id.edtStatusHead);
         edtStatus = (TextView) dialogStatus.findViewById(R.id.edtStatus);
         
         edtStatusHead.setText(caption);
         edtStatus.setText(message);
         
         if (type.equals("warning")){
        	 dialogButton.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
        	 lnHeader.setBackgroundColor(cn.getResources().getColor(R.color.toucan_yellow));
    	 }
    	 else{
    		 dialogButton.setBackgroundColor(cn.getResources().getColor(R.color.toucan_red));
        	 lnHeader.setBackgroundColor(cn.getResources().getColor(R.color.toucan_red));
    	 }
         
         dialogButton.setOnClickListener(new OnClickListener()
         {
             @Override
             public void onClick(View v)
             {
               dialogStatus.dismiss();
             }
         });
         WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
         lp.copyFrom(dialogStatus.getWindow().getAttributes());
         lp.width = WindowManager.LayoutParams.MATCH_PARENT;
         lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
         dialogStatus.show();
         dialogStatus.getWindow().setAttributes(lp);
    }
}
