package com.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {

	public static String DateToString(Date date){
		SimpleDateFormat sd1 = new SimpleDateFormat("dd-MMM-yyyy");
		return sd1.format(date);
	}
	
	public static String DateToStringServerFormat(Date date){
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
		return sd1.format(date);
	}
	
	public static String TimeToString(Date date){
		SimpleDateFormat sd1 = new SimpleDateFormat("HH:mm");
		return sd1.format(date);
	}
	
	public static Date StringToDateServerFormat(String date){
		SimpleDateFormat sd1  = new SimpleDateFormat("yyyy-MM-dd");
		Date result = null;
		try {
			result = sd1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Date StringToTime(String time){
		SimpleDateFormat sd1  = new SimpleDateFormat("HH:mm");
		Date result = null;
		try {
			result = sd1.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}
