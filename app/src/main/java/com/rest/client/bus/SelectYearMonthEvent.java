package com.rest.client.bus;

public final class SelectYearMonthEvent {
	private int mYear;
	private int mMonth;

	public SelectYearMonthEvent( int year, int month ) {
		mYear = year;
		mMonth = month;
	}


	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}
}
