package com.lpoezy.nexpa.activities;

import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.PresenceState;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.objects.ProfilePicture;
import com.lpoezy.nexpa.objects.UserProfile;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.RoundedImageView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGrid extends BaseAdapter {
	 private Context mContext;
    // private final String[] web;
	 private ArrayList<String> web = new ArrayList<String>();
	 private ArrayList<String> availabilty = new ArrayList<String>();
	 private ArrayList<Integer> Imageid = new ArrayList<Integer>();
     private ArrayList<Integer> distance = new ArrayList<Integer>();
	private ArrayList<Correspondent> mCorrespondents;
	private ArrayList<Long> userIds;
    
     
       public CustomGrid(Context c, ArrayList<String> web,ArrayList < Correspondent > correspondents/*ArrayList<Integer> Imageid*/,ArrayList<String> availabilty,ArrayList<Integer> distance ) {
           mContext = c;
           //this.Imageid = Imageid;
           this.userIds = userIds;
           this.distance = distance;
           this.web = web;
           this.availabilty = availabilty;
           this.mCorrespondents = correspondents;
           
       }

       @Override
       public int getCount() {
           // TODO Auto-generated method stub
           return web.size();
       }

       @Override
       public Object getItem(int position) {
           // TODO Auto-generated method stub
           return null;
       }

       @Override
       public long getItemId(int position) {
           // TODO Auto-generated method stub
           return 0;
       }

       public void removeItem(int position){
       	
           web.remove(position);
           Imageid.remove(position);
           availabilty.remove(position);
           distance.remove(position);
        }
       
       @Override
       public View getView(int position, View convertView, ViewGroup parent) {
           // TODO Auto-generated method stub
           View grid;
           grid = new View(mContext);
           
           
           if (convertView == null) {
        	   LayoutInflater inflater = (LayoutInflater) mContext
                       .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	   grid = inflater.inflate(R.layout.grid_single, null);
        	   
               
           
           } else {
               grid = (View) convertView;
           }

           
           TextView textView = (TextView) grid.findViewById(R.id.grid_text);
           ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
           //TextView txtAvailView = (TextView) grid.findViewById(R.id.txtAvail);
           
           
           //Roster roster = Roster.
           
          // Presence presence = 
          
           textView.setText(web.get(position)+ " | " + distance.get(position) +"m");
           //imageView.setImageResource(Imageid.get(position));
          
           
           Bitmap rawImage = BitmapFactory.decodeResource(mContext.getResources(),
			        R.drawable.pic_sample_girl);
           
	
			if(mCorrespondents.get(position).getProfilePic()!=null){
				rawImage = mCorrespondents.get(position).getProfilePic();
			}
           
   			imageView.setImageBitmap(rawImage);
           
          // txtAvailView.setText(availabilty.get(position));
           
           return grid;
       }
       
//       private Bitmap getCorrespondentPic(final Correspondent correspondent) {
//			
//			Bitmap rawImage = BitmapFactory.decodeResource(mContext.getResources(),
//			        R.drawable.pic_sample_girl);
//			//L.debug("CustomGrid, getCorrespondentPic: "+this.userIds.get(pos)+", "+this.web.get(pos));
//			
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					
//					correspondent.downloadProfilePicOnline(mContext);
//					
//				}
//			}).start();
//			
//			return rawImage;
//		}

	
}