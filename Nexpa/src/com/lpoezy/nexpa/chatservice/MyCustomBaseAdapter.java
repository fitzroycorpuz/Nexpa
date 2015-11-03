package com.lpoezy.nexpa.chatservice;

import java.util.ArrayList;

import com.lpoezy.nexpa.R;
import com.lpoezy.nexpa.activities.SearchResults;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomBaseAdapter extends BaseAdapter {
	private static ArrayList<SearchResults> searchArrayList;
	
	private LayoutInflater mInflater;

	public MyCustomBaseAdapter(Context context, ArrayList<SearchResults> results) {
		searchArrayList = results;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return searchArrayList.size();
	}

	public Object getItem(int position) {
		return searchArrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.name);
			holder.txtCityState = (TextView) convertView.findViewById(R.id.cityState);
			holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
			holder.txtPlace1 = (TextView) convertView.findViewById(R.id.place1);
			holder.txtPlace2 = (TextView) convertView.findViewById(R.id.place2);
			holder.txtPlace3 = (TextView) convertView.findViewById(R.id.place3);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtName.setText(searchArrayList.get(position).getName());
		holder.txtCityState.setText(searchArrayList.get(position).getCityState());
		holder.txtPhone.setText(searchArrayList.get(position).getPhone());
		holder.txtPlace1.setText(searchArrayList.get(position).getPlace1());
		holder.txtPlace2.setText(searchArrayList.get(position).getPlace2());
		holder.txtPlace3.setText(searchArrayList.get(position).getPlace3());

		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtCityState;
		TextView txtPhone;
		TextView txtPlace1;
		TextView txtPlace2;
		TextView txtPlace3;
	}
}
