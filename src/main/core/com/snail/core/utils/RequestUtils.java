package com.snail.core.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * http请求转换工具
 *
 */
public class RequestUtils {

	private ServletRequest request;

	public RequestUtils(ServletRequest request) {
		this.request = request;
	}

	public ServletRequest getRequest() {
		return request;
	}

	/**
	 * 获取boolean型参数
	 * 
	 * @param name
	 * @return
	 */
	public boolean getBoolean(String name) {
		return getBoolean(request, name);
	}

	/**
	 * 获取boolean型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		return getBoolean(request, name, defaultValue);
	}

	/**
	 * 获取boolean型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(ServletRequest request, String name,
			boolean defaultValue) {
		if (StringUtils.isBlank(getString(request, name)))
			return defaultValue;
		else
			return getBoolean(request, name);
	}

	/**
	 * 获取boolean型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static boolean getBoolean(ServletRequest request, String name) {
		return Boolean.parseBoolean(getString(request, name));
	}

	/**
	 * 获取byte型参数
	 * 
	 * @param name
	 * @return
	 */
	public byte getByte(String name) {
		return getByte(request, name);
	}

	/**
	 * 获取byte型参数
	 * 
	 * @param name
	 * @return
	 */
	public static byte getByte(ServletRequest request, String name) {
		return Byte.parseByte(request.getParameter(name));
	}

	/**
	 * 获取byte型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public byte getByte(String name, byte defaultValue) {
		return getByte(request, name, defaultValue);
	}

	/**
	 * 获取byte型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static byte getByte(ServletRequest request, String name,
			byte defaultValue) {
		if (StringUtils.isBlank(getString(request, name)))
			return defaultValue;
		else
			return getByte(request, name);
	}

	/**
	 * 获取int型参数
	 * 
	 * @param name
	 * @return
	 */
	public int getInt(String name) {
		return getInt(request, name);
	}

	/**
	 * 获取int型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static int getInt(ServletRequest request, String name) {
		return NumberUtils.toInt(request.getParameter(name));
	}

	/**
	 * 获取int型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String name, int defaultValue) {
		return getInt(request, name, defaultValue);
	}

	/**
	 * 获取int型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(ServletRequest request, String name,
			int defaultValue) {
		return NumberUtils.toInt(request.getParameter(name), defaultValue);
	}

	/**
	 * 获取long型参数
	 * 
	 * @param name
	 * @return
	 */
	public long getLong(String name) {
		return getLong(request, name);
	}

	/**
	 * 获取long型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static long getLong(ServletRequest request, String name) {
		return NumberUtils.toLong(request.getParameter(name));
	}

	/**
	 * 获取long型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String name, int defaultValue) {
		return getLong(request, name, defaultValue);
	}

	/**
	 * 获取long型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(ServletRequest request, String name,
			int defaultValue) {
		return NumberUtils.toLong(request.getParameter(name), defaultValue);
	}

	/**
	 * 获取float型参数
	 * 
	 * @param name
	 * @return
	 */
	public float getFloat(String name) {
		return getFloat(request, name);
	}

	/**
	 * 获取float型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static float getFloat(ServletRequest request, String name) {
		return NumberUtils.toFloat(request.getParameter(name));
	}

	/**
	 * 获取float型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(String name, int defaultValue) {
		return getFloat(request, name, defaultValue);
	}

	/**
	 * 获取float型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(ServletRequest request, String name,
			int defaultValue) {
		return NumberUtils.toFloat(request.getParameter(name), defaultValue);
	}

	/**
	 * 获取double型参数
	 * 
	 * @param name
	 * @return
	 */
	public double getDouble(String name) {
		return getDouble(request, name);
	}

	/**
	 * 获取double型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static double getDouble(ServletRequest request, String name) {
		return NumberUtils.toDouble(request.getParameter(name));
	}

	/**
	 * 获取double型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String name, int defaultValue) {
		return getDouble(request, name, defaultValue);
	}

	/**
	 * 获取double型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static double getDouble(ServletRequest request, String name,
			int defaultValue) {
		return NumberUtils.toDouble(request.getParameter(name), defaultValue);
	}

	/**
	 * 获取String型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getString(String name, String defaultValue) {
		return getString(request, name, defaultValue);
	}

	/**
	 * 获取String型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getString(ServletRequest request, String name,
			String defaultValue) {
		String rst = getString(request, name);
		return StringUtils.isBlank(rst) ? defaultValue : rst;
	}

	/**
	 * 获取String型参数
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		return getString(request, name);
	}

	/**
	 * 获取String型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	private static String getString(ServletRequest request, String name) {
		return request.getParameter(name);
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	public Date getDate(String name) throws ParseException {
		return getDate(request, name);
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param request
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(ServletRequest request, String name)
			throws ParseException {
		return getDate(request, name, "yyyy-MM-dd");
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 * @throws ParseException
	 */
	public Date getDate(String name, Date defaultValue) throws ParseException {
		return getDate(request, name, defaultValue);
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(ServletRequest request, String name,
			Date defaultValue) throws ParseException {
		if (StringUtils.isBlank(getString(request, name)))
			return defaultValue;
		else
			return getDate(request, name, "yyyy-MM-dd");
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param name
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public Date getDate(String name, String format) throws ParseException {
		return getDate(request, name, format);
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param request
	 * @param name
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(ServletRequest request, String name,
			String format) throws ParseException {
		return new SimpleDateFormat(format).parse(getString(request, name));
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param name
	 * @param format
	 * @param defaultValue
	 * @return
	 * @throws ParseException
	 */
	public Date getDate(String name, String format, Date defaultValue)
			throws ParseException {
		return getDate(request, name, format, defaultValue);
	}

	/**
	 * 获取Date型参数
	 * 
	 * @param request
	 * @param name
	 * @param format
	 * @param defaultValue
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(ServletRequest request, String name,
			String format, Date defaultValue) throws ParseException {
		if (StringUtils.isBlank(getString(request, name)))
			return defaultValue;
		else
			return getDate(request, name, format);
	}

	/**
	 * 返回map数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String[]> toMap() {
		return request.getParameterMap();
	}

	/**
	 * 返回经过处理的map
	 * 
	 * @return
	 */
	public Map<String, Object> toSmartMap() {
		Map<String, Object> rst = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			String[] values = request.getParameterValues(name);
			if (values == null)
				rst.put(name, null);
			else if (values.length == 1)
				rst.put(name, values[0]);
			else
				rst.put(name, values);
		}
		return rst;
	}

	/**
	 * 获取请求来源
	 * 
	 * @return
	 */
	public String getReferer() {
		String rst = null;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest _request = (HttpServletRequest) request;
			rst = _request.getHeader("Referer");
		}
		return rst;
	}

	/**
	 * 获取所有IP地址集合
	 * 
	 * @return
	 */
	public String[] getRemoteAddrs() {
		String[] rst = new String[] { request.getRemoteAddr() };
		if (request instanceof HttpServletRequest) {
			HttpServletRequest _request = (HttpServletRequest) request;
			String[] headers = new String[] { "x-forwarded-for",
					"Proxy-Client-IP", "WL-Proxy-Client-IP" };
			String _ip = null;
			for (String header : headers) {
				_ip = _request.getHeader(header);
				if (StringUtils.isNotBlank(_ip)
						&& !"unknown".equalsIgnoreCase(_ip))
					break;
			}
			if (StringUtils.isNotBlank(_ip))
				rst = _ip.split(",");
		}
		return rst;
	}

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	public String getRemoteAddr() {
		String[] ips = getRemoteAddrs();
		return ips != null && ips.length > 0 ? ips[0] : null;
	}

	/**
	 * 解析请求包
	 * @param request
	 * @return
	 */
	public static String getReq(HttpServletRequest request) {
		String reqBody = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while((line = br.readLine())!=null){
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
		} 
		return reqBody;
	}
}
