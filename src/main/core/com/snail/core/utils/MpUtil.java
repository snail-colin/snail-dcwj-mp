package com.snail.core.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snail.core.pojo.Paper;
import com.snail.core.pojo.PaperDetail;
import com.snail.core.pojo.TypeConstants;
import com.snail.core.thread.ThreadPool;

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
				params.put("categoryCode", "baks");
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
		final long session = System.currentTimeMillis() + RandomUtils.nextInt(9);
		log.info("[MpUtil][saveAnswer][{}][params={}]",session,params);
		try {
			String json = (String) params.get("question");
			if (StringUtils.isNotBlank(json)) {
				String openid = (String) params.get("openid");
				Integer type = Integer.parseInt(params.get("type") + "");
				Integer currentIndex = Integer.parseInt(params.get("currentIndex") + "");
				
				Map<String, Object> jsonMap = ConvertorUtil.objectMapper.readValue(json, Map.class);
				String questionId = (String) jsonMap.get("id");
				List<Map<String, Object>> optionList = (List<Map<String, Object>>) jsonMap.get("option");
				String answer = "-1";
				for (Map<String, Object> map : optionList) {
					if(map.get("select") == null || (Boolean)map.get("select") == false) {
						continue;
					}
					answer =  (String) map.get("value");
				}
	
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
				
				//异步存储
				final Map<String, Object> mpParams = new HashMap<String, Object>();
				mpParams.put("questionId", questionId);
				mpParams.put("answer", answer);
				mpParams.put("type", type);
				mpParams.put("userMark", openid);
				mpParams.put("paperId", "SYSTEM_QUESTION_BAKS$" +  openid + "$" + type);
				
				log.info("[MpUtil][saveAnswer][{}][开始更新到平台]",session);
				ThreadPool.getFixedInstance().execute(new Runnable() {
					public void run() {
						boolean  mpRst = saveAnswerToMp(mpParams);
						log.info("[MpUtil][saveAnswer][{}][更新到平台完成 mpRst = {}]",session,mpRst);
					}
				});
				log.info("[MpUtil][saveAnswer][{}][结束到平台完成]",session);
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

	public static Map<String, Object> getCacheAnswerSchedule(String openid)
	  {
		Map<String, Object> rst = new HashMap<String, Object>();
	    long session = System.currentTimeMillis() + RandomUtils.nextInt(9);
	    log.info("[MpUtil][getCacheAnswerSchedule][{}][request openid={}]", Long.valueOf(session), openid);

	    int type = 0;
	    String key = openid + "|" + type;
	    PageBean pageBean = (PageBean)CacheUtil.getCache(PageBean.class, key);
	    if (pageBean == null)
	      pageBean = getCacheQuestion(openid, Integer.valueOf(type));

	    rst.put("total" + pageBean.getType(), pageBean.getTotal());
	    rst.put("recordCount" + pageBean.getType(), Integer.valueOf((pageBean.getRecordCount() == null) ? 0 : pageBean.getRecordCount().intValue()));

	    type = 1;
	    key = openid + "|" + type;
	    pageBean = (PageBean)CacheUtil.getCache(PageBean.class, key);
	    if (pageBean == null)
	      pageBean = getCacheQuestion(openid, Integer.valueOf(type));

	    rst.put("total" + pageBean.getType(), pageBean.getTotal());
	    rst.put("recordCount" + pageBean.getType(), Integer.valueOf((pageBean.getRecordCount() == null) ? 0 : pageBean.getRecordCount().intValue()));
	    return rst;
	  }
	
	/**
	 * 获取题目
	 * @param openid
	 * @param type
	 * @param examType
	 * @param categoryCode
	 * @param bn
	 * @return
	 */
	public static PageBean getTestPaper(String openid, Integer type,byte examType,String categoryCode,String bn) {
		log.info("[MpUtil][getTestPaper][openid={}]", openid);
		PageBean pageBean = new PageBean();
		try {
			// 获取token
			String token = "";
			if(StringUtils.isNotBlank(bn)) {
				token = TokenUtil.getToken(bn);
			}else {
				token = TokenUtil.getToken();
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("token", token);
			params.put("categoryCode", "baks");
			//指定题目
			if(StringUtils.isNotBlank(categoryCode)) {
				params.put("categoryCode", categoryCode);
			}
			params.put("pageSize", 100);
			params.put("qtype", type);

			pageBean = MpUtil.getQuestionList(params);
			pageBean.setType(type);
			// 产生一个唯一编码
			String uuid = SequenceUtil.getUid(3);
			log.info("[MpUtil][getTestPaper][openid={}][uuid={}]", openid,uuid);
			String key = openid + "|" + Paper.class;
			Paper paper =		CacheUtil.getCache(Paper.class, key);
			log.info("[MpUtil][getTestPaper][openid={}][缓存中的paper={}]", openid,paper);
			if(paper == null) {
				paper =	new Paper();
			}
			Map<String, PaperDetail> uuidMap = paper.getUuid();
			if(uuidMap == null) {
				uuidMap = new HashMap<String, PaperDetail>();
			}
			log.info("[MpUtil][getTestPaper][openid={}][uuidMap size={}]", openid,uuidMap.size());
			//只能获取一次试卷
			if(TypeConstants.EXAM_TYPE_ONE == examType) {
				log.info("[MpUtil][getTestPaper][openid={}][只能获取一次试卷 examType={}]", openid,examType);
				if(!uuidMap.containsKey(openid)) {
					uuidMap.put(openid, new PaperDetail(openid));
				}
				uuid = openid;
			}else {
				uuidMap.put(uuid, new PaperDetail(uuid));
			}
			paper.setUuid(uuidMap);
			log.info("[MpUtil][getTestPaper][openid={}][准备更新到缓存的paper={}]", openid,paper);
			CacheUtil.updateCache(key ,paper);
			pageBean.setUuid(uuid);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("[MpUtil][getTestPaper][openid={}][exception={}]", openid,e);
		}
		log.info("[MpUtil][getTestPaper][openid={}][pageBean total={}]", openid, pageBean.getTotal());
		return pageBean;
	}
	
	/**
	 * 提交试卷
	 * @param params
	 * @return
	 */
	public static Map<String, Object> submitPaperToCache(Map<String, Object> params){
		Map<String, Object> rst = new HashMap<String, Object>();
		long session = System.currentTimeMillis() + RandomUtils.nextInt(9);
		log.info("[MpUtil][submitPaperToCache][{}][params={}]",session,params);
		try {
			String json = (String) params.get("paperDetail");
			String openid = (String) params.get("openid");
			String uuid = (String) params.get("uuid");
			if (StringUtils.isBlank(json) || StringUtils.isBlank(uuid) || StringUtils.isBlank(openid) ) {
				ResultMapUtil.toMap(rst, 0, "必传参数不能为空");
				return rst;
			}
			PaperDetail  pd = ConvertorUtil.jsonToObject(PaperDetail.class,json);
			String key = openid + "|" + Paper.class;
			Paper paper =	CacheUtil.getCache(Paper.class, key);
			log.info("[MpUtil][submitPaperToCache][openid={}][缓存中的paper={}]", openid,paper);
	
			if(StringUtils.isBlank(ConvertorUtil.jsonToObject(PaperDetail.class, ConvertorUtil.objectMapper.writeValueAsString(paper.getUuid().get(uuid))).getTjsj())) {
				pd.setTjsj(DateUtil.getNowDate2yyyyMMddHHmmss());
				log.info("[MpUtil][submitPaperToCache][openid={}][未提交过paper 允许提交 pd ={}]", openid,pd);
				paper.getUuid().put(uuid, pd);
			}
			log.info("[MpUtil][submitPaperToCache][openid={}][准备更新到缓存的paper={}]", openid,paper);
			CacheUtil.updateCache(key ,paper);
			ResultMapUtil.toMap(rst, 1, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			ResultMapUtil.toMap(rst, 0, "保存失败");
			log.info("[MpUtil][submitPaperToCache][{}][exception={}]",session,e);
		}
		log.info("[MpUtil][submitPaperToCache][{}][rst={}]",session,rst);
		return rst;
	}
	
	
	@SuppressWarnings("unchecked")
	public static boolean saveAnswerToMp(Map<String, Object> params) {
		boolean rst = false;
		String url = PropertiesUtil.MP_URL + "/api/pms/answer";
		log.info("[MpUtil][getQuestionList][前params={}]", params);
		// 获取token
		String token = TokenUtil.getToken();
		params.put("token", token);
		log.info("[MpUtil][getQuestionList][后params={}]", params);
		String result = HttpClientUtil.getRequst(params, url);
		log.info("[MpUtil][getQuestionList][result={}]", result);
		if (StringUtils.isNotBlank(result)) {
			Map<String, Object> ret;
			try {
				ret = ConvertorUtil.objectMapper.readValue(result, Map.class);
				if ((Integer) ret.get("status") == 1) {
					rst = true;
				}
			}catch (Exception e) {
				log.info("[MpUtil][getQuestionList][ e={}]",  e);
			}
		}
		return rst;
	}

	/**
	 * 获取考试结果
	 * @param openid
	 * @param type
	 * @param examType
	 * @param categoryCode
	 * @param bn
	 * @return
	 */
	public static PaperDetail getPaperDetailResult(String openid) {
		log.info("[MpUtil][getPaperDetailResult][openid={}]", openid);
		PaperDetail pd = null;
		try {
			String key = openid + "|" + Paper.class;
			Paper paper =		CacheUtil.getCache(Paper.class, key);
			log.info("[MpUtil][getPaperDetailResult][openid={}][缓存中的paper={}]", openid,paper);
			if(paper == null) {
				paper =	new Paper();
			}
			Map<String, PaperDetail> uuidMap = paper.getUuid();
			if(uuidMap == null) {
				uuidMap = new HashMap<String, PaperDetail>();
			}
			if(StringUtils.isNotBlank(ConvertorUtil.jsonToObject(PaperDetail.class, ConvertorUtil.objectMapper.writeValueAsString(paper.getUuid().get(openid))).getTjsj())) {
				 pd = ConvertorUtil.jsonToObject(PaperDetail.class, ConvertorUtil.objectMapper.writeValueAsString(paper.getUuid().get(openid)));
			}
			log.info("[MpUtil][getPaperDetailResult][openid={}][uuidMap size={}]", openid,uuidMap.size());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("[MpUtil][getPaperDetailResult][openid={}][exception={}]", openid,e);
		}
		log.info("[MpUtil][getPaperDetailResult][openid={}][PaperDetail total={}]", openid, pd);
		return pd;
	}
	
	public static Map<String, Object> decrypt(Map<String, Object> req_params)
		    throws JsonGenerationException, JsonMappingException, IOException
		  {
		    log.info("[MpUtil][decrypt][request={}]", req_params);
		    Map<String,Object> rst = new HashMap<String,Object>();

		    String code = (String)req_params.get("code");
		    String encryptedData = (String)req_params.get("encryptedData");
		    String iv = (String)req_params.get("iv");
		    String result = WeixinUtil.getUserInfo(encryptedData, code, iv);
		    log.info("[MpUtil][decrypt][httpClient result={}]", result);
		    rst = ConvertorUtil.json2map(result);
		    log.info("[MpUtil][decrypt][response={}]", rst);
		    return rst;
		  }
}
