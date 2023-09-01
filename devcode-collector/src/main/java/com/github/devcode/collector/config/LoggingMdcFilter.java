package com.github.devcode.collector.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.github.devcode.collector.common.util.CommonUtil;

/**
 * request 호출시 Log4j2의 MDC를 이용한 requestId 생성
 */
@Component
public class LoggingMdcFilter implements Filter{

	private static final Logger logger = LogManager.getLogger(LoggingMdcFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String taskId = CommonUtil.getTaskId();
		MDC.put("requestId", taskId);
		chain.doFilter(request, response);
		
		// request 정보 출력
		logger.debug("{}, RemoteAddr : {}", taskId, request.getRemoteAddr());
		
		MDC.clear();
	}

}
