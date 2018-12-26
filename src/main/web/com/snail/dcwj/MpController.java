package com.snail.dcwj;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snail.core.pojo.PaperDetail;
import com.snail.core.utils.ConvertorUtil;
import com.snail.core.utils.MpUtil;
import com.snail.core.utils.PageBean;
import com.snail.core.utils.RequestUtils;
import com.snail.core.utils.ResultMapUtil;

@Controller
@RequestMapping
public class MpController {

	private static final Logger log = LoggerFactory.getLogger(MpController.class);

	@RequestMapping("/test.htm")
	@ResponseBody
	public String test(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		RequestUtils re = new RequestUtils(request);
		Map<String, Object> req_params2 = re.toSmartMap();
		System.out.println(req_params2);
		return "{'a':'b'}";
	}

	/**
	 * 获取题目
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getQuestionList.htm")
	@ResponseBody
	public String getQuestionList(HttpServletRequest request)
			throws JsonGenerationException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		PageBean pageBean = new PageBean();
		log.info("[/getQuestionList.htm][getQuestionList][req_params={}]", req_params);
		String openid = (String) req_params.get("openid");
		Integer type = Integer.parseInt(req_params.get("type") + "");
		if (StringUtils.isNotBlank(openid) && type != null) {
			pageBean = MpUtil.getCacheQuestion(openid, type);
		}
		log.info("[/getQuestionList.htm][getQuestionList][pageBean total={}]", pageBean.getTotal());
		return ConvertorUtil.objectToJson(pageBean);
	}

	/**
	 * 将答案保存到题库
	 * 
	 * @param request
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/saveAnswer.htm")
	public Map<String, Object> saveAnswer(HttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/saveAnswer.htm][saveAnswer][req_params={}]", req_params);
		String openid = (String) req_params.get("openid");
		Integer type = Integer.parseInt(req_params.get("type") + "");
		Map<String, Object> rst = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(openid) && type != null) {
			rst = MpUtil.saveAnswer(req_params);
		} else {
			ResultMapUtil.toMap(rst, 0, "保存失败");
		}
		return rst;
	}

	/**
	 * 获取答题进度
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getAnswerSchedule.htm")
	public Map<String, Object> getAnswerSchedule(HttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/getAnswerSchedule.htm][getAnswerSchedule][req_params={}]", req_params);
		String openid = (String) req_params.get("openid");
		Map<String, Object> rst = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(openid)) {
			rst = MpUtil.getCacheAnswerSchedule(req_params);
		}
		return rst;
	}

	/**
	 * 获取试卷
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getTestPaper.htm")
	@ResponseBody
	public String getTestPaper(HttpServletRequest request)
			throws JsonGenerationException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		PageBean pageBean = new PageBean();
		log.info("[/getTestPaper.htm][getTestPaper][req_params={}]", req_params);
		String openid = (String) req_params.get("openid");
		String categoryCode = (String) req_params.get("categoryCode");
		String bn = (String) req_params.get("bn");
		Integer type = Integer.parseInt(req_params.get("type") + "");
		byte examType = 0;
		if (StringUtils.isNotBlank((String) req_params.get("examType"))) {
			examType = Byte.parseByte((String) req_params.get("examType"));
		}

//		if (StringUtils.isNotBlank(openid) && type != null) {
//			pageBean = MpUtil.getTestPaper(openid, type, examType, categoryCode, bn);
//		}
		
		 if ((StringUtils.isNotBlank(openid)) && (type != null)) {
		      PaperDetail pd = MpUtil.getPaperDetailResult(openid);
		      if ((pd != null) && (StringUtils.isNotBlank(pd.getTjsj())) && (pd.getTime().intValue() != 0) && (1 == examType)) {
		        log.info("[/getTestPaper.htm][getTestPaper][已考试不允许重复考试={}]", pageBean.getTotal());
		        return ConvertorUtil.objectMapper.writeValueAsString(pd);
		      }
		      pageBean = MpUtil.getTestPaper(openid, type, examType, categoryCode, bn);
		}
		 
		log.info("[/getTestPaper.htm][getTestPaper][pageBean total={}]", pageBean.getTotal());
		return ConvertorUtil.objectToJson(pageBean);
	}

	/**
	 * 交卷
	 * 
	 * @param request
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/submitPaper.htm")
	@ResponseBody
	public Map<String, Object> submitPaper(HttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/submitPaper.htm][submitPaper][req_params={}]", req_params);
		Map<String, Object> rst = new HashMap<String, Object>();
		rst = MpUtil.submitPaperToCache(req_params);
		log.info("[/submitPaper.htm][submitPaper][rst={}]", rst);
		return rst;
	}

	/**
	 * 获取考试结果
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getTestPaperResult.htm")
	@ResponseBody
	public String getTestPaperResult(HttpServletRequest request)
			throws JsonGenerationException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		PaperDetail pd = new PaperDetail();
		log.info("[/getTestPaperResult.htm][getTestPaperResult][req_params={}]", req_params);
		String openid = (String) req_params.get("openid");
		if (StringUtils.isNotBlank(openid)) {
			pd = MpUtil.getPaperDetailResult(openid);
		}
		log.info("[/getTestPaperResult.htm][getTestPaperResult][response ={}]", pd);
		return ConvertorUtil.objectMapper.writeValueAsString(pd);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/login.htm" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response, String code,
			String encryptedData, String iv) throws IOException {
		Cookie cookie;
		String body = RequestUtils.getReq(request);
		Map<String,Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/login.htm][login][request ={}]", req_params);
		Map<String,Object>  rst = new HashMap<String,Object>();
		Cookie[] cookies = request.getCookies();
		String openid = "";
		if (cookies != null) {
			Cookie[] arrayOfCookie1;
			int j = (arrayOfCookie1 = cookies).length;
			for (int i = 0; i < j; ++i) {
				cookie = arrayOfCookie1[i];
				if ("openid".equals(cookie.getName()))
					openid = cookie.getValue();
			}
		}

		log.info("[/login.htm][login][cookie openid ={}]", openid);
		if (StringUtils.isBlank(openid)) {
			rst = MpUtil.decrypt(req_params);
			openid = (String) rst.get("openId");
		}

		log.info("[/login.htm][login][openid ={}]", openid);
		if (StringUtils.isNotBlank(openid)) {
			rst.put("answerSchedule", MpUtil.getCacheAnswerSchedule(openid));
			cookie = new Cookie("openid", openid);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		log.info("[/login.htm][login][response ={}]", rst);
		return rst;
	}

}
