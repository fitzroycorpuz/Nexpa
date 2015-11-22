package com.lpoezy.nexpa.chatservice;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapterActivity extends ArrayAdapter<OneComment> implements Correspondent.OnCorrespondentUpdateListener{
	private TextView countryName;
	private List<OneComment> countries = new ArrayList<OneComment>();
	private LinearLayout wrapper;
	private long correspondentId;
	
	private Bitmap correspondentPic;

	private Context context;
	
	
	
	

	public void add(OneComment object){
		countries.add(object);
		super.add(object);
	}

	public ChatAdapterActivity(Context context, int textViewResourceId, long correspondentId){
		super(context, textViewResourceId);
		this.context = context;
		this.correspondentId = correspondentId;
	}
	
	public int getCount(){
		return this.countries.size();
	}
	
	public OneComment getItem(int index){
		return this.countries.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
//		if (row == null){
//			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			row = inflater.inflate(R.layout.listitem, parent, false);
//		}
//		
//		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
//		OneComment coment = getItem(position);
//		countryName = (TextView) row.findViewById(R.id.comment);
//		countryName.setText(coment.comment);
//		if (coment.success){
//			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_green : R.drawable.bubble_yellow);
//		}
//		else{
//			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_failed : R.drawable.bubble_yellow);
//		}
//		
//	
//		
//		wrapper.setGravity(coment.left ? Gravity.RIGHT : Gravity.LEFT);
		return row;
	}
	
	private Bitmap getCorrespondentPic() {
		
		final Correspondent correspondent = new Correspondent();
		correspondent.addListener(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				correspondent.downloadProfilePicOnline(getContext());
				
			}
		}).start();
		
		Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(),
		        R.drawable.pic_sample_girl);
		
		
		if(correspondent.getProfilePic()!=null){
			rawImage = correspondent.getProfilePic();
		}
		
		 RoundedImageView riv = new RoundedImageView(context);
		 Bitmap circImage = riv.getCroppedBitmap(rawImage, 80);
		 
		return circImage;
	}

	private Bitmap getUserPic(ImageView imgRight) {
		
		String imgDecodableString = ProfilePicture.getUserImgDecodableString(context);
		
        Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.pic_sample_girl);
       
        if(imgDecodableString!=null && !imgDecodableString.isEmpty()){
        	
        	// Get the dimensions of the View
            int targetW = imgRight.getWidth();
            int targetH = imgRight.getHeight();
            
            BmpFactory  bmpFactory = new BmpFactory();
        	rawImage = bmpFactory.getBmpWithTargetWTargetHFrm(targetW, targetH, imgDecodableString);
        	
        }
        
        RoundedImageView riv = new RoundedImageView(context);
        Bitmap circImage = riv.getCroppedBitmap(rawImage, 80);
        
		return circImage;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}



	@Override
	public void onCorrespondentUpdate() {
		((Activity)context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				L.debug("ChatAdapter, onCorrespondentUpdate");
				notifyDataSetChanged();
				
			}
		});
		
	}
}