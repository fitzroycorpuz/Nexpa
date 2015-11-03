package com.lpoezy.nexpa.chatservice;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapterActivity extends ArrayAdapter<OneComment>{
	private TextView countryName;
	private List<OneComment> countries = new ArrayList<OneComment>();
	private LinearLayout wrapper;
	
	public void add(OneComment object){
		countries.add(object);
		super.add(object);
	}

	public ChatAdapterActivity(Context context, int textViewResourceId){
		super(context, textViewResourceId);
	
	}
	
	public int getCount(){
		return this.countries.size();
	}
	
	public OneComment getItem(int index){
		return this.countries.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		if (row == null){
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem, parent, false);
		}
		
		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
		OneComment coment = getItem(position);
		countryName = (TextView) row.findViewById(R.id.comment);
		countryName.setText(coment.comment);
		if (coment.success){
			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_green : R.drawable.bubble_yellow);
		}
		else{
			countryName.setBackgroundResource(coment.left ? R.drawable.bubble_failed : R.drawable.bubble_yellow);
		}
		wrapper.setGravity(coment.left ? Gravity.RIGHT : Gravity.LEFT);
		return row;
	}
	
	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
}