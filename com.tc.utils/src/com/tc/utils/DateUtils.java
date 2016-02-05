/*
 * © Copyright Tek Counsel LLC 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */


package com.tc.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mark ambler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DateUtils {


	private static final Logger logger = Logger.getLogger(DateUtils.class.getName());
	
	//fields used for time difference methods
    public static final int TD_SECOND = 0;
    public static final int TD_MINUTE = 1;
    public static final int TD_HOUR = 2;
    public static final int TD_DAY = 3;
    public static final int TD_MIL = 4;
    public static final String DEFAULT_FORMAT = "MM/dd/yyyy hh:mm:ss a";
    public static final String ISO_DATE_TIME="yyyy-MM-dd hh:mm:ss a";
    public static final String ISO_DATE="yyyy-MM-dd";

    public static Date adjustDay(Date dt, int numDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.DAY_OF_YEAR, numDays);
        return cal.getTime();

    }

    public static Date adjustMonth(Date dt, int month) {
//month is zero based so assumption that we are calling it one based.
//so if you want december you can pass in 12.
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.MONTH, month--);
        return cal.getTime();
    }

    public static Date adjustYear(Date dt, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.YEAR, year);
        return cal.getTime();
    }

    public static Date adjustHour(Date dt, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }


    public static Date adjustMinute(Date dt, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }

    public static Date adjustSecond(Date dt, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();

    }

    public static Date adjustMilliSecond(Date dt, int milli) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.MILLISECOND, milli);
        return cal.getTime();

    }
    
    public static Date adjustToDayOfMonth(Date dt, int day){
    	Calendar cal = Calendar.getInstance();
    	Date newDate = new Date(dt.getTime());
    	cal.setTime(newDate);
    	cal.set(Calendar.DAY_OF_MONTH, day);
    	return resetTime(cal.getTime());
    }
    
    

    
    
     public static Date resetTime(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        return cal.getTime();
    }
     
     
 
    public static Date getNow() {
        return new Date(System.currentTimeMillis());
    }

    public static int getHourOfDay(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int getTimeDiffDays(Date source, Date target){
        long hrs = DateUtils.getTimeDiffHrs(source, target);
        int days = (int) hrs/24;
        return days;
    }

    public static long getTimeDiffHrs(Date source, Date target) {
        long min = DateUtils.getTimeDiffMin(source, target);
        return min / 60;
    }

    public static long getTimeDiffMin(Date source, Date target) {
        long secs = DateUtils.getTimeDiffSec(source, target);
        return secs / 60;
    }

    public static long getTimeDiffSec(Date source, Date target) {
        long l1 = source.getTime();
        long l2 = target.getTime();
        long difference = l2 - l1;
        return (difference / 1000);
    }

    public static long getTimeDiffMiliSec(Date source, Date target) {
        long l1 = source.getTime();
        long l2 = target.getTime();
        long difference = l2 - l1;
        return difference;
    }

    public static double getTimeDiffSecDouble(Date source, Date target) {
        double l1 = source.getTime();
        double l2 = target.getTime();
        double difference = (double)(l2 - l1);
        return (difference / 1000);
    }
    
    

    

    
   
    

    

    public static long getTimeDifference(int type, Date src, Date target) {
        if (type == TD_SECOND) {
            return DateUtils.getTimeDiffSec(src, target);
        } else if (type == TD_MINUTE) {
            return (DateUtils.getTimeDiffSec(src, target) / 60);
        } else if (type == TD_HOUR) {
            return (DateUtils.getTimeDiffSec(src, target) / 3600);
        } else if (type == TD_DAY) {
            return (DateUtils.getTimeDiffSec(src, target) / 86400);
        } else if (type == TD_MIL) {
            return DateUtils.getTimeDiffMiliSec(src, target);
        }

        return 0;
    }

    public static void main(String[] args) {
    	Date nearestDay = DateUtils.getNearestDayOfWeek(Calendar.TUESDAY);
    	System.out.println(nearestDay);
    }

    public static Date getDateByString(String format, String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(format);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            logger.log(Level.SEVERE,null,e);
        }
        return null;
    }

    public static Date getDateByString(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(DEFAULT_FORMAT);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
           logger.log(Level.SEVERE,null,e);
        }
        return null;
    }

    
    public static boolean isSaturday(Date date){
    	Calendar cal = Calendar.getInstance();
    	 cal.setTime(date);
    	 return cal.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY;
    	 
    }

    
    public static boolean isSunday(Date date){
    	Calendar cal = Calendar.getInstance();
    	 cal.setTime(date);
    	 return cal.get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY;
    	 
    }
    
    public static boolean isWeekend(Date date){
    	return isSaturday(date) || isSunday(date);
    }
    

    public static Date getDate(int year, int month, int day) {
        month--;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }


    public static java.sql.Date getSqlDate(int year, int month, int day) {
        Date dt = DateUtils.getDate(year, month, day);
        java.sql.Date sDate = new java.sql.Date(dt.getTime());
        return sDate;

    }

    public static java.sql.Date getSqlDate(Date dt) {
        java.sql.Date sDate = new java.sql.Date(dt.getTime());
        return sDate;
    }

    public static int getYear(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.YEAR);
    }
    
    public static int getMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.DATE);
    }
    
    public static Date getNearestDayOfWeek(int dayOfWeek) {
    	Date dt = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        
        int cntr = 0;
        
        while(cal.get(Calendar.DAY_OF_WEEK)!=dayOfWeek){
        	dt = DateUtils.adjustDay(dt, 1);
        	cal.setTime(dt);
        	
        	cntr ++;
        	if (cntr >30){
        		break;
        	}
        }
        return cal.getTime();
    }
    
//assumes this year's year
//	remember month is zero based
//	this function is user friendly... decrements for caller.
    public static Date getDate(int month, int day) {
        month--;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }
    
    

    public static String toISODateTime(Date dt) {
        SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        return ISODateFormat.format(dt);
    }


    public static String toISODate(Date dt) {
        SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return ISODateFormat.format(dt);
    }
    
    public static String toUSDateFormat(Date dt){
        SimpleDateFormat ISODateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return ISODateFormat.format(dt);
    }


    public static Date parseISODate(String strDate){
        SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dt = null;
        if (strDate.length()==10){
            strDate = strDate + " 00:00:00";
        }
        try {
            dt = ISODateFormat.parse(strDate);
        } catch (ParseException ex) {
            logger.log(Level.SEVERE,null,ex);
        }



        return dt;

    }
    
    
    public static String toLDAPDateString(Date dt) {
        
        if(dt==null){
            return null;
        }
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = dateFormat.format(new Date());
        return strDate;
    }
    
      public static Date toLDAPDate(String strDate) {
          
        if(strDate==null || "".equals(strDate)){
            return null;
        }
          
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date=null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }


    public static String format(Date dt, String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(dt);
    }
}
