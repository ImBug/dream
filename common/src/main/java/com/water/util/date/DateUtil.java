package com.water.util.date;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ！！！非线程安全的
 * @author honghm
 *
 */
public class DateUtil {
	
	private final static String pattern = "yyyy-MM-dd HH:mm:ss";
	private final static String pattern_withoutYear = "MM-dd HH:mm:ss";
	private final static SimpleDateFormat dsf = new SimpleDateFormat(pattern);
	private final static SimpleDateFormat dsf_n_y = new SimpleDateFormat(pattern_withoutYear);
	private final static SimpleDateFormat dsf_1 = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
	private final static SimpleDateFormat dsf_2 = new SimpleDateFormat("HH:mm yyyy-MM-dd");
	private final static SimpleDateFormat dsf_3 = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat hour = new SimpleDateFormat("yyyy-MM-dd HH:00");
	private final static SimpleDateFormat minute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private final static SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss");
	private final static SimpleDateFormat hms_ampm = new SimpleDateFormat("HH:mm:ss a",Locale.ENGLISH);
	// 短日期格式
	public static String DATE_FORMAT = "yyyyMMdd";

	
	/**
	 * am pm 转24小时制
	 * @param time
	 * @return
	 */
	public static String ampmparseTo24(String time){
		try {
			Date date = hms_ampm.parse(time);
			return hms.format(date);
		} catch (ParseException e) {
		}
		return null;
	}
	/** 
	 * 获取指定时间对应的毫秒数 
	 * @param time "HH:mm:ss" 
	 * @return 
	 */  
	public static long getTimeMillis(String time) {  
	    try {  
	    		time  = formatTime(time);
	        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);  
	        return curDate.getTime();  
	    } catch (ParseException e) {
	    }  
	    return 1;  
	} 
	
	private static String formatTime(String time){
		if(time == null) return "00:00:00";
		String[] p = time.split(":");
		String t = p[0];
		if(t.length() < 2) t = "0" + t;
		if(p.length < 2) return t + ":00:00";
		if(p[1].length() < 2) t = t + ":0" + p[1];
		else t+= ":" + p[1];
		if(p.length < 3) return t + ":00";
		if(p[2].length() < 2) return t + ":0" + p[2];
		else t+= ":" + p[2];
		return t;
	}
	
	/**
	 * 带毫秒数
	 * @param date
	 * @return
	 */
	public static String formatWithMs(Date date){
		long time = date.getTime();
		long ms = time % 1000;
		return dsf.format(date) + "." + ms;
	}
	
	/**
	 * 不带年的
	 * @param date
	 * @return
	 */
	public static String formatWithoutYear(Date date){
		if (null == date) {
			return null;
		}
		return dsf_n_y.format(date);
	}
	
	/**
	 * 格式化日期
	 * 
	 * 默认格式：yyyy-MM-dd hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if (null == date) {
			return null;
		}
		return dsf.format(date);
	}

	public static String format1(Date date) {
		if (null == date) {
			return null;
		}
		return dsf_1.format(date);
	}
	
	public static String format2(Date date) {
		if (null == date) {
			return null;
		}
		return dsf_2.format(date);
	}

	public static String formatDay(Date date) {
		if (null == date) {
			return null;
		}
		return dsf_3.format(date);
	}
	
	public static String formatHms(Date date) {
		if (null == date) {
			return null;
		}
		return hms.format(date);
	}

	public static long formatHourLong(long time){
		return time - time %3600000;
	}
	
	public static long formatDayLong(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(formatHourLong(time));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTimeInMillis();
	}
	
	public static String formatHour(Date date) {
		if (null == date) {
			return null;
		}
		return hour.format(date);
	}
	
	public static String formatMinute(Date date) {
		if (null == date) {
			return null;
		}
		return minute.format(date);
	}
	
	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date, String format) {
		if (null == date || null == format) {
			return null;
		}
		SimpleDateFormat dsf = new SimpleDateFormat(format);
		return dsf.format(date);
	}

	/**
	 * 解析字符串
	 * 
	 * 默认格式：yyyy-MM-dd hh:mm:ss
	 * 
	 * @param str
	 * @return
	 */
	public static Date parse(String str) {
		if (null == str || str.length() < pattern.length()) {
			return null;
		}
		Date date = null;
		try {
			date = dsf.parse(str);
		} catch (ParseException e) {
		}
		return date;
	}

	/**
	 * 解析字符串
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static Date parse(String str, String pattern) {
		if (null == str || null == pattern || str.length() < pattern.length()) {
			return null;
		}
		SimpleDateFormat dsf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = dsf.parse(str);
		} catch (ParseException e) {
		}
		return date;
	}
	
	/**
	 * utc时间转本地时间
	 * @param utcTime
	 * @return
	 */
	public static long parseUtcToLocal(long utcTime){
		long delta = Math.abs(System.currentTimeMillis() - utcTime);
		if(delta < 15*60000){//一定的容错范围
			return utcTime;
		}else{
			int zoneOffset = TimeZone.getDefault().getRawOffset();   
			int dstOffset =  TimeZone.getDefault().getDSTSavings();
			return (utcTime + zoneOffset + dstOffset);
		}
	}
	
	/**
	 * 获取某天时间范围
	 * @return
	 */
	public static long[] getDateRange(long timelong){
		long start = timelong/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
		long end = start + 1000*3600*24-1;
		return new long[]{start,end};
	}
	
	/**
	 * 最近24小时
	 * @return
	 */
	public static long[] getLatest24(){
		long end = System.currentTimeMillis();
		long start = end - 24*3600*1000;
		return new long[]{start,end};
	}
	
	/**
	 * 
	 * @param date 毫秒
	 * @return
	 */
	public static String format(long date){
		return format(new Date(date));
	}

	/**
	 * 将日期格式的字符串转换为长整型
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static long convertTolong(String date, String format) {
		try {
			if (StringUtils.isNotBlank(date)) {
				if (StringUtils.isBlank(format))
					format = pattern;
				SimpleDateFormat sf = new SimpleDateFormat(format);
				return sf.parse(date).getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	 * 将长整型数字转换为日期格式的字符串
	 *
	 * @param time
	 * @param format
	 * @return
	 */
	public static String convertToString(long time, String format) {
		if (time > 0l) {
			if (StringUtils.isBlank(format))
				format = pattern;
			SimpleDateFormat sf = new SimpleDateFormat(format);
			Date date = new Date(time);
			return sf.format(date);
		}
		return "19700101";
	}
}
