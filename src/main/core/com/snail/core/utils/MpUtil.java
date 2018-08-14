package com.snail.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于获取平台数据
 * 
 * @author colin
 */
public class MpUtil {

	private static final Logger log = LoggerFactory.getLogger(MpUtil.class);

	/**
	 * 系统默认题目集合
	 */
	private static final String SYSTEM_QUESTION_LIST = "SYSTEM_QUESTION_LIST";

	/**
	 * 获取平台的题目列表
	 * 
	 * @param params
	 * @return TODO 需要做合并答案处理
	 */
	@SuppressWarnings("unchecked")
	public static PageBean getQuestionList(Map<String, Object> params) {
		PageBean pageBean = new PageBean();
		Integer type = (Integer) params.get("qtype");
		String url = PropertiesUtil.MP_URL + "/api/pms/question/list";
		// 随机题目
		if (type == 1) {
			url += "?isRandom=true";
		}
		log.info("[MpUtil][getQuestionList][url={}]", url);
		log.info("[MpUtil][getQuestionList][params={}]", params);
		String result = HttpClientUtil.getRequst(params, url);
		log.info("[MpUtil][getQuestionList][result={}]", result);
		if (StringUtils.isNotBlank(result)) {
			Map<String, Object> ret;
			try {
				ret = ConvertorUtil.objectMapper.readValue(result, Map.class);
				if ((Integer) ret.get("status") == 1) {
					ret = (LinkedHashMap<String, Object>) ret.get("data");
					log.info("[MpUtil][getQuestionList][ret={}]", ret);
					pageBean.setTotal(Integer.valueOf(ret.get("total") + ""));
					pageBean.setPageSize((Integer) ret.get("pageSize"));
					if (params.get("pageNum") != null) {
						pageBean.setCurrentPage((Integer) params.get("pageNum"));
					}

					List<Map<String, Object>> recordsList = (ArrayList<Map<String, Object>>) ret.get("records");
					// 封装后的结果集
					List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> map : recordsList) {
						log.info("[MpUtil][getQuestionList][map={}]", map);
						Map<String, Object> temMap = new HashMap<String, Object>();
						temMap.put("question", map.get("question"));
						temMap.put("type", map.get("type"));
						temMap.put("id", map.get("id"));
						temMap.put("remark", map.get("answerDesc"));
						temMap.put("ishow", false);
						temMap.put("checked", false);
						List<Map<String, Object>> optionList = (ArrayList<Map<String, Object>>) map.get("options");
						for (Map<String, Object> optionMap : optionList) {
							optionMap.put("name", optionMap.get("answer"));
							boolean isRight = false;
							if ((Integer) optionMap.get("isTrue") == 1) {
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
				log.info("[MpUtil][getQuestionList][e={}]", e);
			}
		}
		log.info("[MpUtil][getQuestionList][result={}]", pageBean);
		return pageBean;
	}

	public static PageBean getCacheQuestion(String openid, Integer type) {
		log.info("[MpUtil][getCacheQuestion][openid={}]", openid);
		String key = openid + "|" + type;
		PageBean pageBean = CacheUtil.getCache(PageBean.class, key);
		if (pageBean == null) {
			// 先检查系统题库是否存在
			pageBean = CacheUtil.getCache(PageBean.class, SYSTEM_QUESTION_LIST + "|" + type);
			if (pageBean == null) {
				log.info("[MpUtil][getCacheQuestion][openid={}][系统题库不存在，需请求远程题库]", openid);
				// 获取token
				String token = TokenUtil.getToken();
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("token", token);
				params.put("categoryCode", "one");
				params.put("pageSize", 9999);
				params.put("qtype", type);
				pageBean = MpUtil.getQuestionList(params);
				pageBean.setType(type);
				if (pageBean.getTotal() > 0) {
					CacheUtil.updateCache(SYSTEM_QUESTION_LIST + "|" + type, pageBean);
				}
			}
			log.info("[MpUtil][getCacheQuestion][openid={}][pageBean total={}]", openid, pageBean.getTotal());
			if (pageBean.getTotal() > 0) {
				// 更新个人题库
				CacheUtil.updateCache(key, pageBean);
				log.info("[MpUtil][getCacheQuestion][openid={}][更新个人题库]", openid);
			}
		}
		log.info("[MpUtil][getCacheQuestion][openid={}][pageBean total={}]", openid, pageBean.getTotal());
		return pageBean;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> saveAnswer(Map<String, Object> params) {
		Map<String, Object> rst = new HashMap<String, Object>();
		long session = System.currentTimeMillis() + RandomUtils.nextInt(9);
		log.info("[MpUtil][saveAnswer][{}][params={}]",session,params);
		try {
			String json = (String) params.get("question");
			if (StringUtils.isNotBlank(json)) {
				String openid = (String) params.get("openid");
				Integer type = Integer.parseInt(params.get("type") + "");
				Integer currentIndex = Integer.parseInt(params.get("currentIndex") + "");
				String key = openid + "|" + type;
				PageBean pageBean = CacheUtil.getCache(PageBean.class, key);
				if (pageBean == null) {
					ResultMapUtil.toMap(rst,0, "获取不到缓存题目数据");
					log.info("[MpUtil][saveAnswer][{}][rst={}]",session,rst);
					return rst;
				}
				List<Map<String, Object>> resultList = (List<Map<String, Object>>) pageBean.getResultList();
				if (resultList == null ||  resultList.size() == 0) {
					ResultMapUtil.toMap(rst,0, "缓存数据为空");
					log.info("[MpUtil][saveAnswer][{}][rst={}]",session,rst);
					return rst;
				}
				Map<String, Object> cacheQuestionMap = resultList.get(currentIndex);
				Map<String, Object> questionMap = ConvertorUtil.objectMapper.readValue(json, Map.class);
				if(cacheQuestionMap == null ||  StringUtils.isBlank((String)cacheQuestionMap.get("id")) || !cacheQuestionMap.get("id").equals(questionMap.get("id"))) {
					ResultMapUtil.toMap(rst,0, "找不到匹配的数据");
					log.info("[MpUtil][saveAnswer][{}][rst={}]",session,rst);
					return rst;
				}
				questionMap.put("ishow", false);
				resultList.set(currentIndex, questionMap);
				pageBean.setResultList(resultList);
				pageBean.setRecordCount(currentIndex + 1);
				//准备更新缓存数据
				log.info("[MpUtil][saveAnswer][{}][准备更新缓存数据]",session);
				CacheUtil.updateCache(key, pageBean);
				log.info("[MpUtil][saveAnswer][{}][更新缓存数据完成]",session);
				ResultMapUtil.toMap(rst, 1, "保存成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResultMapUtil.toMap(rst, 0, "保存失败");
			log.info("[MpUtil][saveAnswer][{}][exception={}]",session,e);
		}
		log.info("[MpUtil][saveAnswer][{}][rst={}]",session,rst);
		return rst;
	}
	
	
	public static Map<String, Object> getCacheAnswerSchedule(Map<String, Object> params){
		Map<String, Object> rst = new HashMap<String, Object>();
		long session = System.currentTimeMillis() + RandomUtils.nextInt(9);
		log.info("[MpUtil][getCacheAnswerSchedule][{}][params={}]",session,params);
		String openid = (String) params.get("openid");
		//顺序答题
		int type =0;
		String key = openid + "|" + type;
		PageBean pageBean = CacheUtil.getCache(PageBean.class, key);
		if(pageBean != null) {
			rst.put("total_" + pageBean.getType(), pageBean.getTotal());
			rst.put("recordCount_" + pageBean.getType(), pageBean.getRecordCount());
		}
		//随机答题
		type =1;
		key = openid + "|" + type;
		 pageBean = CacheUtil.getCache(PageBean.class, key);
		if(pageBean != null) {
			rst.put("total_" + pageBean.getType(), pageBean.getTotal());
			rst.put("recordCount_" + pageBean.getType(), pageBean.getRecordCount());
		}
		
		return rst;
	}
}
