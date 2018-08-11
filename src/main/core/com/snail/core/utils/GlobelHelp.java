package com.snail.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
/**
 * 
 * @author chenwh
 *
 */
public class GlobelHelp {
	/**
     * 连接超时 15*1000 15秒
     */
    private final static int CONNECTIONTIMEOUT = 15 * 1000;
    
    /**
     * 交易超时 50*1000 50 秒
     */
    private final static int SOTIMEOUT = 50 * 1000;
    
    /**
	 * 解析请求包
	 * @param request
	 * @return
	 */
	public static String getRequest(HttpServletRequest request) {
		String reqBody = (String)request.getAttribute("RequestBody");
		return reqBody;
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
	/**
	 * 模拟请求
	 * @param url 地址
	 * @param content 内容
	 * @param type eg:json
	 * @return
	 */
	public static String httpPost(String url, String content, String type) {
		String ret = "";
		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod(url);
		HttpConnectionManager hcManager = httpClient.getHttpConnectionManager();
		HttpConnectionManagerParams hcmParams = hcManager.getParams();
		hcmParams.setConnectionTimeout(CONNECTIONTIMEOUT);// 连接超时设置
		hcmParams.setSoTimeout(SOTIMEOUT);// 交易超时设置
		hcManager.setParams(hcmParams);
		httpClient.setHttpConnectionManager(hcManager);
		try {
			if (type.equals("xml")) {
				post.setRequestEntity(new StringRequestEntity(content, "application/xml", "utf-8"));
			} else if ("json".equals(type)) {
				post.setRequestEntity(new StringRequestEntity(content, "application/json", "utf-8"));
			}
			int i = httpClient.executeMethod(post);
			if (i == 200) {
				ret =inputStream2String(post.getResponseBodyAsStream());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
    }
	
	
	
	
	/**
     * 将字节流转成字符串
     * @param is 待转换的字节流
     * @return
     */
	public static String inputStream2String(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		String result = "";
		try 
		{
			while ((i = is.read()) != -1) 
			{
				baos.write(i);
			}
			result = new String(baos.toByteArray(),"utf-8");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getRequestV2(HttpServletRequest request) {
		Map<String, String[]> map=request.getParameterMap();
		Map<String, String> data = new HashMap<String, String>();
		for(String key:map.keySet()){
			data.put(key, map.get(key)[0]);
		}
		return data;
	}
}
