package com.rest.client.bus;

import java.util.Calendar;

public final class SelectDateTime {
	private int mType;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String mTimeZone;

	public SelectDateTime(int type) {
		mType = type;
		Calendar calendar = Calendar.getInstance();
		mTimeZone = calendar.getTimeZone()
							.getID();
	}

	public SelectDateTime( int type, int year, int month ) {
		this(type);
		mYear = year;
		mMonth = month;

	}

	public SelectDateTime( int type, int year, int month, int day ) {
		this(
				type,
				year,
				month
		);
		mDay = day;
	}


	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}


	public int getDay() {
		return mDay;
	}

	public String getTimeZone() {
		return mTimeZone;
	}

	public int getQueryType() {
		return mType;
	}
}
