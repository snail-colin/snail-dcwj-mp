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
import com.snail.core.utils.TokenUtil;

@Controller
@RequestMapping
public class MpController {
	
	
	private static final Logger log = LoggerFactory.getLogger(MpController.class);

	@RequestMapping("/index.htm")
	@ResponseBody
	public String index() {
		return "xxxx";
	}
	
	/**
	 * 获取题目
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getQuestionList.htm")
	@ResponseBody
	public String getQuestionList(HttpServletRequest request,Integer pageSize) throws JsonGenerationException, JsonMappingException, IOException {
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		PageBean pageBean  = new PageBean();
		log.info("/getQuestionList.htm");
		//获取token
		String token = TokenUtil.getToken();
		if(StringUtils.isNotBlank(token)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("token",token);
			if(req_params.get("pageSize") != null) {
				params.put("pageSize",(Integer)req_params.get("pageSize"));
			}
			if(req_params.get("pageNum") != null) {
				params.put("pageNum",(Integer)req_params.get("pageNum"));
			}
			
			params.put("categoryCode","one");
			log.info("[getQuestionList][params={}]",params);
			pageBean = MpUtil.getQuestionList(params);
		}
		log.info("[getQuestionList][pageBean={}]",pageBean);
		return ConvertorUtil.objectToJson(pageBean);
	}
	
	
	
	public Map<String, Object> subjectOneStatistics(HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException{
		String body = RequestUtils.getReq(request);
		Map<String, Object> req_params = ConvertorUtil.objectMapper.readValue(body, Map.class);
		return null;
		
	}
    
	
}
