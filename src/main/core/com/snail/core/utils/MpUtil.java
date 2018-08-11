package com.snail.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于获取平台数据
 * @author colin
 */
public class MpUtil {
	
	private static final Logger log = LoggerFactory.getLogger(MpUtil.class);
	
	
	/**
	 * 获取平台的题目列表
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  PageBean  getQuestionList(Map<String, Object> params) {
		PageBean pageBean = new PageBean();
		String url = PropertiesUtil.MP_URL + "/api/pms/question/list";
		log.info("[MpUtil][getQuestionList][url={}]",url);
		log.info("[MpUtil][getQuestionList][params={}]",params);
		String result = HttpClientUtil.getRequst(params, url);
		log.info("[MpUtil][getQuestionList][result={}]",result);
		if(StringUtils.isNotBlank(result)) {
			Map<String, Object> ret;
			try {
				ret = ConvertorUtil.objectMapper.readValue(result, Map.class);
				if((Integer)ret.get("status") == 1) {
					ret = (LinkedHashMap<String, Object>) ret.get("data");
					log.info("[MpUtil][getQuestionList][ret={}]",ret);
					pageBean.setTotal(Long.valueOf(ret.get("total")+""));
					pageBean.setPageSize((Integer)ret.get("pageSize"));
					if(params.get("pageNum") != null) {
						pageBean.setCurrentPage((Integer)params.get("pageNum"));
					}
				
					List<Map<String, Object>> recordsList =  (ArrayList<Map<String, Object>>) ret.get("records");
					//封装后的结果集
					List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> map:recordsList) {
						log.info("[MpUtil][getQuestionList][map={}]",map);
						Map<String, Object> temMap = new HashMap<String, Object>();
						temMap.put("question", map.get("question"));
						temMap.put("type", map.get("type"));
						temMap.put("id", map.get("id"));
						temMap.put("remark", map.get("answerDesc"));
						temMap.put("ishow",false);
						temMap.put("checked", false);
						List<Map<String, Object>> optionList =  (ArrayList<Map<String, Object>>) map.get("options");
						for (Map<String, Object> optionMap : optionList) {
							optionMap.put("name", optionMap.get("answer"));
							boolean isRight = false;
							if((Integer)optionMap.get("isTrue") == 1) {
								isRight = true;
							}
							optionMap.put("isRight", isRight);
							optionMap.put("checked", false);
							optionMap.put("isShow", false);
							optionMap.remove("isInput");
							optionMap.remove("updator");
							optionMap.remove("answer");
							optionMap.remove("isTrue");
							optionMap.remove("createDate");
							optionMap.remove("updateDate");
							optionMap.remove("creator");
						}
						temMap.put("option", optionList);
						resultList.add(temMap);
					}
					pageBean.setResultList(resultList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[MpUtil][getQuestionList][e={}]",e);
			}
		}
		log.info("[MpUtil][getQuestionList][result={}]",pageBean);
		return pageBean;
	}

}
