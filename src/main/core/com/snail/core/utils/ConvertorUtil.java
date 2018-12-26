package com.snail.core.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 对象转换工具
 * 
 * @author tuwj 2016/12/10
 */
public class ConvertorUtil {

	public final static ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * map转json
	 * 
	 * @param map
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String mapToJson(Map<String, Object> map)
			throws JsonGenerationException, JsonMappingException, IOException {
		return objectMapper.writeValueAsString(map);
	}

	/**
	 * 特殊map转json value : 为实体对象类型
	 * 
	 * @param map
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static String specialMapToJson(Map<String, ? extends Object> map)
			throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> rst = new HashMap<String, Object>();
		for (Entry<String, ?> entry : map.entrySet()) {
			if (entry.getValue() instanceof Collection)
				rst.put(entry.getKey(), listToListMap((List<? extends Object>) entry.getValue()));
			else
				rst.put(entry.getKey(), objectToMap(entry.getValue()));
		}
		return objectMapper.writeValueAsString(rst);
	}

	/**
	 * list转换成json
	 * 
	 * @param objs
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String listToJson(List<? extends Object> objs)
			throws JsonGenerationException, JsonMappingException, IOException {
		List<Map<String, Object>> rst = new ArrayList<Map<String, Object>>();
		for (Object obj : objs)
			rst.add(objectToMap(obj));
		return objectMapper.writeValueAsString(rst);
	}

	/**
	 * list转换list<Map>对象
	 * 
	 * @param objs
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private static List<Map<String, Object>> listToListMap(List<? extends Object> objs)
			throws JsonGenerationException, JsonMappingException, IOException {
		List<Map<String, Object>> rst = new ArrayList<Map<String, Object>>();
		for (Object obj : objs)
			rst.add(objectToMap(obj));
		return rst;
	}

	/**
	 * json字符串转为对象
	 * 
	 * @param clazz
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToObject(Class<T> clazz, String json) {
		T instance = null;
		String name = "";
		try {
			instance = clazz.newInstance();
			for (; clazz != Object.class; clazz = (Class<T>) clazz.getSuperclass()) {
				Field fields[] = clazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					name = field.getName();
					Object value;
					Map<String, Object> map = objectMapper.readValue(json, Map.class);
					if (map.containsKey(name) && !"serialVersionUID".equals(name) && map.get(name) != null) {
						if (field.getGenericType().toString().equals("class java.lang.Integer")) {
							value = Integer.parseInt(map.get(name).toString());
							field.set(instance, value);
						} else if (field.getGenericType().toString().equals("class java.lang.Byte")) {
							value = Byte.parseByte(map.get(name).toString());
							field.set(instance, value);
						} else if (field.getGenericType().toString().equals("class java.lang.String")) {
							if (StringUtils.isNotBlank((String) map.get(name))) {
								value = map.get(name);
								field.set(instance, value);
							}
						} else {
							field.set(instance, map.get(name));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

	/**
	 * 对象转json
	 * 
	 * @param object
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String objectToJson(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		return objectMapper.writeValueAsString(objectToMap(object));
	}

	/**
	 * 对象转map
	 * 
	 * @param object
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static Map<String, Object> objectToMap(Object object)
			throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Class<?> clazz = object.getClass();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field p_fields[] = clazz.getDeclaredFields();
			if (p_fields == null || p_fields.length <= 0)
				continue;
			try {
				for (Field field : p_fields) {
					field.setAccessible(true);
					final String name = field.getName();

					if ("serialVersionUID".equals(name))
						continue;
					if (field.get(object) != null) {
						if (field.get(object) instanceof Collection && ((Collection) field.get(object)).size() == 0)
							continue;
						jsonMap.put(name, field.get(object));
					}
				}
			} catch (Exception e) {
			}
		}
		return jsonMap;
	}
	
	/**
	 * 把数组转换成set
	 * @param array
	 * @return
	 */
	public static Set<?> array2Set(Object[] array) {
		Set<Object> set = new TreeSet<Object>();
		for (Object id : array) {
			if(null != id){
				set.add(id);
			}
		}
		return set;
	}
	
	/**
	 * 将jsonArray转成List
	 * @param clazz
	 * @param jsonArray
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<? extends Object> jsonarray2List(Class clazz,JSONArray jsonArray) {
		List list = new ArrayList();
		for(int i =0;i<jsonArray.size();i++){
			list.add(json2pojo(clazz, jsonArray.getJSONObject(i)));
		}
		return list;
	}
	
	/**
	 * @desc list2jsonA
	 * @param objs
	 * @return
	 */
	public static JSONArray list2jsonA(List<? extends Object> objs) {
		JSONArray jsonA = new JSONArray();
		for (Object obj:objs) {
			jsonA.add(pojo2json(obj));
		}
		return jsonA;
	}
	
	/**
	 * @desc pojo2json
	 * @author yuhs 2015-9-18
	 * @param pojo
	 * @return
	 */
	public static JSONObject pojo2json(Object pojo) {
		Class<?> clazz = pojo.getClass();
		JSONObject json = new JSONObject();
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field p_fields[] = clazz.getDeclaredFields();
			if (p_fields == null || p_fields.length <= 0)
				continue;
			try {
				for (Field field : p_fields) {
					field.setAccessible(true);
					final String name = field.getName();
					if ("serialVersionUID".equals(name)) {
						continue;
					}

					json.put(name, field.get(pojo));
				}
			} catch (Exception e) {
			}

		}
		return json;
	}
	/**
	 * @desc json2pojo
	 * @param clazz
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T json2pojo(Class<T> clazz, JSONObject json) {
		T instance = null;
		String name = "";
		try {
			instance = clazz.newInstance();		
			for (; clazz != Object.class; clazz = (Class<T>) clazz.getSuperclass()) {
				Field fields[] = clazz.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					name = field.getName();
					Object value;
					if (json.containsKey(name) && !"serialVersionUID".equals(name) && json.get(name)!=null) {
						if(field.getGenericType().toString().equals("class java.lang.Integer")){
							value= Integer.parseInt(json.get(name).toString());
							field.set(instance, value);
						}else if(field.getGenericType().toString().equals("class java.lang.Byte")){
							value= Byte.parseByte(json.get(name).toString());
							field.set(instance, value);
						}else if(field.getGenericType().toString().equals("class java.lang.String")){
							if(StringUtil.isNotEmptyEx(String.valueOf(json.get(name)))){
								value= String.valueOf(json.get(name));
								field.set(instance, value);
							}
						}else{
							field.set(instance, json.get(name));
						}					
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return instance;
	}
	
	public static Map<String, Object> json2map(String json)
	  {
	    Map<String, Object> rst = null;
	    try {
	      rst = objectMapper.readValue(json, Map.class);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return rst;
	  }

	  public static <T> T json2Pojo(Class<T> clazz, String json)
	  {
	    Object instance = null;
	    try {
	      instance = objectMapper.readValue(json, clazz);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return (T) instance;
	  }

	  public static String pojo2Json(Object pojo)
	  {
	    String rst = "";
	    try {
	      rst = objectMapper.writeValueAsString(pojo);
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	    return rst;
	  }

}
