package com.lpoezy.nexpa.objects;

public class Announcement {
	// BROAD_ID, BROADCAST_TYPE, BROADCAST_FROM,
	// BROADCAST_MESSAGE, BROADCAST_DATE, BROADCAST_LOCATION_LONG,
	// BROADCAST_LOCATION_LAT, BROADCAST_LOCATION_LOCAL, BROADCAST_REACH,
	// BROADCAST_STATUS

	private long id;
	private int type;
	private int from;
	private String message;
	private String date;
	private long locLongitude;
	private long locLatitude;
	private String locLocal;
	private int reach;
	private int status;
	
	public Announcement(){}

	public Announcement(long id, int type, int from, String message, String date, long locLongitude, long locLatitude,
			String locLocal, int reach, int status) {

		this.id = id;
		this.type = type;
		this.from = type;
		this.message = message;
		this.date = date;
		this.locLongitude = locLongitude;
		this.locLatitude = locLatitude;
		this.locLocal = locLocal;
		this.reach = reach;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getLocLongitude() {
		return locLongitude;
	}

	public void setLocLongitude(long locLongitude) {
		this.locLongitude = locLongitude;
	}

	public long getLocLatitude() {
		return locLatitude;
	}

	public void setLocLatitude(long locLatitude) {
		this.locLatitude = locLatitude;
	}

	public String getLocLocal() {
		return locLocal;
	}

	public void setLocLocal(String locLocal) {
		this.locLocal = locLocal;
	}

	public int getReach() {
		return reach;
	}

	public void setReach(int reach) {
		this.reach = reach;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
