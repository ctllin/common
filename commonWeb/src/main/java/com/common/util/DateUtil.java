package com.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	  public static SimpleDateFormat sdfdatemmdd = new SimpleDateFormat("yyyyMMdd");
	  public static SimpleDateFormat sdfdatemmdd2 = new SimpleDateFormat("yyyy-MM-dd");
	  public static SimpleDateFormat sdfMMddHHmmss=new SimpleDateFormat("MMddHHmmss");
	  public static SimpleDateFormat sdfyyyyMMddHHmmssSSS=new SimpleDateFormat("yyyyMMddHHmmssSSS");
	  public static SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	  public static SimpleDateFormat sdfyyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	  public static SimpleDateFormat sdfyyyyMM = new SimpleDateFormat("yyyyMM");
	  public static SimpleDateFormat sdfyyyy_MM = new SimpleDateFormat("yyyy-MM");
	  public static SimpleDateFormat sdfyyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	  public static SimpleDateFormat sdfyyMMddHHmmss=new SimpleDateFormat("yyMMddHHmmss");
	  public static SimpleDateFormat sdfyy_MM_dd_HH_mm_ss=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
	  public static SimpleDateFormat sdfyyyyMMddHHmmss=new SimpleDateFormat("yyyyMMddHHmmss");
	  public static SimpleDateFormat sdfyyyy_MM_dd_HH_mm_ss=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  /**
	   * 获得n天前时间
	   * @param day
	   * @return
	   */
	  public static Date getDayBefore(int day){
		    Calendar calendarBefore=new GregorianCalendar();
			calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
			return calendarBefore.getTime();
	  }
	  public static String getDayBeforeStr(int day){
		    Calendar calendarBefore=new GregorianCalendar();
			calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
			return sdfyyyy_MM_dd_HH_mm_ss.format(calendarBefore.getTime());
	  }
	  /**
	   * 获得n天前0:0:0时间
	   * @param day
	   * @return
	   */
	  public static Date getDayBefore0H0M0S(int day){
		  Calendar calendarBefore=new GregorianCalendar();
		  calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
		  calendarBefore.set(Calendar.HOUR_OF_DAY,0);
		  calendarBefore.set(Calendar.MINUTE, 0);
		  calendarBefore.set(Calendar.SECOND, 0);
		  return calendarBefore.getTime();
	  }
	  public static String getDayBefore0H0M0SStr(int day){
		  Calendar calendarBefore=new GregorianCalendar();
		  calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
		  calendarBefore.set(Calendar.HOUR_OF_DAY,0);
		  calendarBefore.set(Calendar.MINUTE, 0);
		  calendarBefore.set(Calendar.SECOND, 0);
		 return sdfyyyy_MM_dd_HH_mm_ss.format(calendarBefore.getTime());
	  }
	  /**
	   * 获得n天前23:59:59时间
	   * @param day
	   * @return
	   */
	  public static Date getDayBefore23H59M59S(int day){
		    Calendar calendarBefore=new GregorianCalendar();
			calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
			calendarBefore.set(Calendar.HOUR_OF_DAY,23);
			calendarBefore.set(Calendar.MINUTE, 59);
			calendarBefore.set(Calendar.SECOND, 59);
			return calendarBefore.getTime();
	  }
	  public static String getDayBefore23H59M59SStr(int day){
		  Calendar calendarBefore=new GregorianCalendar();
		  calendarBefore.set(Calendar.DAY_OF_YEAR,calendarBefore.get(Calendar.DAY_OF_YEAR)-day);
		  calendarBefore.set(Calendar.HOUR_OF_DAY,23);
		  calendarBefore.set(Calendar.MINUTE, 59);
		  calendarBefore.set(Calendar.SECOND, 59);
		  return sdfyyyy_MM_dd_HH_mm_ss.format(calendarBefore.getTime());
	  }
	  public static String getDayNow23H59M59SStr(){
		  Calendar calendarBefore=new GregorianCalendar();
		  calendarBefore.set(Calendar.HOUR_OF_DAY,23);
		  calendarBefore.set(Calendar.MINUTE, 59);
		  calendarBefore.set(Calendar.SECOND, 59);
		  return sdfyyyy_MM_dd_HH_mm_ss.format(calendarBefore.getTime());
	  }
	  public static String getDayNow0H0M0SStr(){
		  Calendar calendarBefore=new GregorianCalendar();
		  calendarBefore.set(Calendar.HOUR_OF_DAY,0);
		  calendarBefore.set(Calendar.MINUTE, 0);
		  calendarBefore.set(Calendar.SECOND, 0);
		  return sdfyyyy_MM_dd_HH_mm_ss.format(calendarBefore.getTime());
	  }
	  /**
	   * unix时间转字符串 yyyy-MM-dd HH:mm:ss
	   * @param timestampString
	   * @return
	   */
	  public static String UnixTimeStamp2DateStr_yyyy_MM_dd_HH_mm_ss(long timestampString){    
		  Long timestamp = timestampString*1000;    
		  String date = sdfyyyy_MM_dd_HH_mm_ss.format(new java.util.Date(timestamp));    
		  return date;    
	  }  
	  /**
	   *  unix时间转Date时间
	   * @param timestampString
	   * @return
	   */
	  public static Date UnixTimeStamp2Date(long timestampString){    
		  Long timestamp = timestampString*1000;    
		  return new java.util.Date(timestamp);
	  }  
	  public static void main(String[] args) throws ParseException {
		System.out.println(sdfyyyy_MM_dd_HH_mm_ss.format(getDayBefore(1)));
		System.out.println(sdfyyyy_MM_dd_HH_mm_ss.format(getDayBefore0H0M0S(1)));
		System.out.println(sdfyyyy_MM_dd_HH_mm_ss.format(getDayBefore23H59M59S(1)));
		System.out.println(getDayNow23H59M59SStr());
	}
}
