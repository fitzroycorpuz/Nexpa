package com.lpoezy.nexpa.utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class LocationName {

	public String getLocationNameFromCoordinates(Context cn, double longit, double lat){
		String strLocation = "";
		Geocoder geocoder = new Geocoder(cn, Locale.getDefault());                 
		try {
		    List<Address> listAddresses = geocoder.getFromLocation(lat, longit, 1);			if(null!=listAddresses&&listAddresses.size()>0){
		    	strLocation = listAddresses.get(0).getAddressLine(0);
			}
		} catch (IOException e) {
		   Log.e("Error getting location name", "" + e.getLocalizedMessage());
		   strLocation = "";
		}
		 Log.e("TRTR", "" + strLocation);
		return strLocation;
	}

}