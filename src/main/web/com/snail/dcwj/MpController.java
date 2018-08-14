package com.snail.dcwj;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.snail.core.utils.ConvertorUtil;
import com.snail.core.utils.MpUtil;
import com.snail.core.utils.PageBean;
import com.snail.core.utils.RequestUtils;
import com.snail.core.utils.ResultMapUtil;
import com.snail.core.utils.TokenUtil;

@Controller
@RequestMapping
public class MpController {
	
	
	private static final Logger log = LoggerFactory.getLogger(MpController.class);

	/**
	 * 获取题目
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getQuestionList.htm")
	@ResponseBody
	public String getQuestionList(HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		PageBean pageBean  = new PageBean();
		log.info("[/getQuestionList.htm][getQuestionList][req_params={}]",req_params);
		String openid = (String) req_params.get("openid");
		Integer type = Integer.parseInt(req_params.get("type") + "");
		if(StringUtils.isNotBlank(openid) && type != null) {
			pageBean = MpUtil.getCacheQuestion(openid,type);
		}
		log.info("[/getQuestionList.htm][getQuestionList][pageBean total={}]",pageBean.getTotal());
		return ConvertorUtil.objectToJson(pageBean);
	}
	
	
	/**
	 * 将答案保存到题库
	 * @param request
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/saveAnswer.htm")
	public Map<String, Object> saveAnswer(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException{
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/saveAnswer.htm][saveAnswer][req_params={}]",req_params);
		String openid = (String) req_params.get("openid");
		Integer type = Integer.parseInt(req_params.get("type") + "");
		Map<String, Object> rst = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(openid) && type != null) {
			rst = MpUtil.saveAnswer(req_params);
		}else {
			ResultMapUtil.toMap(rst,0,"保存失败");
		}
		return rst;
	}
    
	/**
	 * 获取答题进度
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/getAnswerSchedule.htm")
	public Map<String, Object> getAnswerSchedule(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException{
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		log.info("[/getAnswerSchedule.htm][getAnswerSchedule][req_params={}]",req_params);
		String openid = (String) req_params.get("openid");
		Map<String, Object> rst = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(openid) ) {
			rst = MpUtil.getCacheAnswerSchedule(req_params);
		}
		return rst;
	}
	
}
