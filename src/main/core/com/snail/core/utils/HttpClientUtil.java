package com.snail.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpClientUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	private static CloseableHttpClient httpClient;
	
	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		//连接池最大生成连接数200
		cm.setMaxTotal(200);
	
		// 创建httpClient
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}
	
	public static boolean getQRCode(File file,String url) {
		logger.info("GetRequst*********");
		HttpGet httpGet = new HttpGet(url);
		logger.info("HttpGet********");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(2000).build();
		logger.info("RequestConfig********");
		httpGet.setConfig(requestConfig);
		logger.info("RequestConfig********");
		try {
			//执行get请求
			logger.info("开始发送请求...");
			logger.info("请求URL信息：{}",url);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			//获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			logger.info("发送返回.");
			//响应状态
			logger.info("status:" + httpResponse.getStatusLine());
			//判断响应实体是否为空
			if (entity != null) {
				logger.info("contentEncoding:" + entity.getContentEncoding());
				
				FileOutputStream out = null;
				InputStream instream = entity.getContent();
				logger.info("build qrcode begin...");
				try {
					out = new FileOutputStream(file);
					byte b[] = new byte[1024];
					int j = 0;
					while( (j = instream.read(b))!=-1){
						out.write(b,0,j);
					}
					logger.info("build qrcode end");
					return true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (instream != null) {
						try {
							instream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			//关闭流并释放资源
			httpGet.releaseConnection();
		}
		return false;
	}
	
	/**
	 * map2string key1=value1&key2=value2&...
	 * @param params
	 * @return
	 */
	public static String map2Str(Map<String, Object> params) {
		StringBuilder sb = new StringBuilder();
		for (String key:params.keySet()) {
			String value = params.get(key)+"";
			if (!StringUtil.isEmpty(value)) {
				sb.append(key).append("=").append(value).append("&");
			}
		}
		return sb.toString();
	}
	
	/**
	 * get方式请求数据
	 * @param url
	 * @return
	 */
	public static String getRequst(Map<String,Object> params, String url){
		logger.info("GetRequst*********");
		if (params != null && params.size() > 0) {
			if (url.indexOf("?") == -1) {
				url += "?";
			} else {
				if (!url.endsWith("&")) {
					url += "&";
				}
			}
			url += map2Str(params);
		}
		HttpGet httpGet = new HttpGet(url);
		logger.info("HttpGet********");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(2000).build();
		logger.info("RequestConfig********");
		httpGet.setConfig(requestConfig);
		logger.info("RequestConfig********");
		String result = null;
		try {
			//执行get请求
			logger.info("开始发送请求...");
			logger.info("请求URL信息：{}",url);
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			//获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			logger.info("发送返回.");
			//响应状态
			logger.info("status:" + httpResponse.getStatusLine());
			//判断响应实体是否为空
			if (entity != null) {
				logger.info("contentEncoding:" + entity.getContentEncoding());
				result = EntityUtils.toString(entity, "UTF-8");
				logger.info("response content:\r\n" + result +"\r\n");
			}
			return result;
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			//关闭流并释放资源
			httpGet.releaseConnection();
		} 
		return result;
	}
	
	public static String postRequst(String request,String url) {
		logger.info("请求URL信息：{}",url);
		HttpPost httpPost = new HttpPost(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(5000).build();
		httpPost.setConfig(requestConfig);
		
		if (request != null) {
			HttpEntity reqEntity = new StringEntity(request, "UTF-8");
			logger.info("请求报文实体：{}", reqEntity);
			httpPost.setEntity(reqEntity);
		}

		String result = null;
		try {
			//执行get请求
			logger.info("开始发送请求...");
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			//获取响应消息实体
			HttpEntity entity = httpResponse.getEntity();
			logger.info("发送返回.");
			//响应状态
			logger.info("status:" + httpResponse.getStatusLine());
			//判断响应实体是否为空
			if (entity != null) {
				logger.info("contentEncoding:" + entity.getContentEncoding());
				result = EntityUtils.toString(entity, "UTF-8");
				logger.info("response content:\r\n" + result +"\r\n");
			}else{
                logger.info("微信返回的entity is null\r\n");
            }
			
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			//关闭流并释放资源
			httpPost.releaseConnection();
		}
		return result;
	}
}
