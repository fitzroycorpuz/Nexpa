package com.lpoezy.nexpa.activities;

import java.util.ArrayList;

import com.lpoezy.nexpa.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGrid extends BaseAdapter{
	 private Context mContext;
    // private final String[] web;
	 private ArrayList<String> web = new ArrayList<String>();
	 private ArrayList<String> availabilty = new ArrayList<String>();
	 private ArrayList<Integer> Imageid = new ArrayList<Integer>();
     private ArrayList<Integer> distance = new ArrayList<Integer>();
    
     
       public CustomGrid(Context c, ArrayList<String> web,ArrayList<Integer> Imageid,ArrayList<String> availabilty,ArrayList<Integer> distance ) {
           mContext = c;
           this.Imageid = Imageid;
           this.distance = distance;
           this.web = web;
           this.availabilty = availabilty;
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
           TextView txtAvailView = (TextView) grid.findViewById(R.id.txtAvail);
           
           textView.setText(web.get(position)+ " | " + distance.get(position) +"m");
           imageView.setImageResource(Imageid.get(position));
           txtAvailView.setText(availabilty.get(position));
           
           return grid;
       }
}