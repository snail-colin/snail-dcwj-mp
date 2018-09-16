package com.snail.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DateUtil {

	static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
	
/*---------------------------------------------------------------------*/
	
	public static String format(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
		
	}
	
	/**
	 * 获取系统当前日期 格式: yyyyMMddHHmmss
	 */
	public static String getNowDate2yyyyMMddHHmmss() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	/**
	 * 获取系统当前日期 格式: yyyyMMdd
	 */
	public static String getNowDate2YYYYMMDD() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	
	/**
	 * 获取系统当前年 格式: HHmmss
	 */
	public static String getNowDate2HHmmss() {
		return new SimpleDateFormat("HHmmss").format(new Date());
	}
	
	/**
	 * 获取系统当前年 格式: yyyy
	 */
	public static String getNowYear2YYYY() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}
	
	/**
	 * 获取系统当前日期 格式: pattern
	 */
	public static String getNowDateByPattern(String pattern) {
		return new SimpleDateFormat(pattern).format(new Date());
	}
	
	/*---------------------------------------------------------------------*/
	
	/**
	 * 获取相应的日期格式 格式: yyyyMMdd
	 */
	public static String toDateStringWithyyyyMMdd(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}
	
	/**
	 * 获取相应的日期格式 格式: yyyyMMddHHmmss
	 */
	public static String toDateStringWithyyyyMMddHHmmss(Date date) {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}
	
	/**
	 * 获取相应的日期格式 
	 */
	public static String toDateString(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static String format(String detPattern, String srcPattern, String source) {
		Date date = null;
		try {
			date = new SimpleDateFormat(srcPattern).parse(source);
		} catch (ParseException e) {
			LOGGER.error("[" + Thread.currentThread() + "][获取相应的日期]", e);
			return source;
		}
		return new SimpleDateFormat(detPattern).format(date);
	}
	
	/*---------------------------------------------------------------------*/
	
	/**
	 * 获取相应的日期 
	 * @param source 格式: YYYYMMDD
	 */
	public static Date toDateByYYYYMMDD(String source) {
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(source);
		} catch (ParseException e) {
			LOGGER.error("[" + Thread.currentThread() + "][获取相应的日期]", e);
			return null;
		}
	}
	
	/**
	 * 获取相应的日期 
	 * @param source 格式: yyyyMMddHHmmss
	 */
	public static Date toDateByyyyyMMddHHmmss(String source) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss").parse(source);
		} catch (ParseException e) {
			LOGGER.error("[" + Thread.currentThread() + "][获取相应的日期]", e);
			return null;
		}
	}
	
	/**
	 * 获取相应的日期
	 */
	public static Date toDate(String pattern, String source) {
		try {
			return new SimpleDateFormat(pattern).parse(source);
		} catch (ParseException e) {
			LOGGER.error("[" + Thread.currentThread() + "][获取相应的日期]", e);
			return null;
		}
	}
	
	/*---------------------------------------------------------------------*/
	/**
	 * 得到当天开始日期(当天的凌晨 00:00:00)
	 */
	public static Date getTodayFirstTime(){
		return getFirstTimeByDate(new Date());
	}
	
	/**
	 * 得到当天结束日期(当天的凌晨 23:59:59)
	 */
	public static Date getTodayLastTime(){
		return getLastTimeByDate(new Date());
	}
	
	/**
	 * 得到指定天的开始日期(当天的凌晨 00:00:00)
	 */
	public static Date getFirstTimeByDate(Date date){
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 得到指定天的结束日期(当天的凌晨 23:59:59)
	 */
	public static Date getLastTimeByDate(Date date){
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
		
		return cal.getTime();
	}
	
	/*---------------------------------------------------------------------*/
	/**
	 * 获取当前时间的前一天日期
	 */
	public static Date getYesterday(){
		return add(new Date(), Calendar.DATE, -1);
	}
	
	/**
     * Adds or subtracts the specified amount of time to the given calendar field,
     * based on the calendar's rules. For example, to subtract 5 days from
     * the current time of the calendar, you can achieve it by calling:
     * <p><code>add(new Date(), Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param date the calendar
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     */
	public static Date add(Date date, int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return cal.getTime();
	}
	
	/*---------------------------------------------------------------------*/
	/**
	 * 判断日期是否是当天时间
	 */
	public static boolean isToday(Date date) {
		long time = date.getTime();
		long todayStart = getTodayFirstTime().getTime();
		long todayEnd = getTodayLastTime().getTime();
		
		return todayStart <= time && time <= todayEnd;
	}
	
	/**
	 * 比较日期相差的天数
	 * <ul>
	 * <li> differenceDays(今天, 明天) = 1
	 * <li> differenceDays(今天, 昨天) = -1
	 * </ul>
	 */
	public static int differenceDays(Date dateOne, Date dateTwo){
		return (int)((dateTwo.getTime() - dateOne.getTime()) / (24 * 60 * 60 * 1000));
	}
	
	/**
	 * 将“yyyyMMddhhmmss”格式转换为“yyyy-MM-dd hh:mm”格式
	 * @param timeStr
	 * @return
	 */
	public static String convertA( String timeStr ){
		
		if( timeStr == null || "".equals( timeStr ) ){
			return timeStr ;
		}
		
		StringBuilder sb = new StringBuilder( timeStr ) ;
		sb.insert( 4 , '-' ).insert( 7 , '-' ).insert( 10 , ' ' ).insert( 13 , ':' ) ;
		
		return sb.substring( 0 , 16 ) ;
	}
	
	/**
	 * 将“yyyyMMdd”格式转换为“yyyy-MM-dd”格式
	 * @param timeStr
	 * @return
	 */
	public static String convertB( String timeStr ){
		
		if( timeStr == null || "".equals( timeStr ) ){
			return timeStr ;
		}
		
		StringBuilder sb = new StringBuilder( timeStr ) ;
		sb.insert( 4 , '-' ).insert( 7 , '-' ) ;
		
		return sb.substring( 0 , 10 ) ;
	}
	
	/**
	 * 将“yyyyMMdd”格式转换为“yyyy-MM-dd hh:mm:ss”格式
	 * @param timeStr
	 * @return
	 */
	public static String convertC( String timeStr ){
		
		if( timeStr == null || "".equals( timeStr ) ){
			return timeStr ;
		}
		
		StringBuilder sb = new StringBuilder( timeStr ) ;
		sb.insert( 4 , '-' ).insert( 7 , '-' ).insert( 10 , ' ' ).insert( 13 , ':' ).insert( 16 , ':' ) ;
		
		return sb.substring( 0 , 19 ) ;
	}
}
