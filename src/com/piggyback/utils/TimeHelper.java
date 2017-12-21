package com.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

	public static String DateToString(long millis){
		SimpleDateFormat sd1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
		Date date = new Date();
		date.setTime(millis);
		return sd1.format(date);
	}
	
	public static String DateToStringServerFormat(long millis){
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = new Date();
		date.setTime(millis);
		return sd1.format(date);
	}
	
	
	public static String TimeToString(long millis){
		SimpleDateFormat sd1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Date date = new Date();
		date.setTime(millis);
		return sd1.format(date);
	}
	
	public static long StringToDateServerFormat(String date){
		SimpleDateFormat sd1  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date result = null;
		try {
			result = sd1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result.getTime();
	}
	
	public static long StringToTime(String time){
		SimpleDateFormat sd1  = new SimpleDateFormat("HH:mm", Locale.getDefault());
		Date result = null;
		try {
			result = sd1.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result.getTime();
	}
}
