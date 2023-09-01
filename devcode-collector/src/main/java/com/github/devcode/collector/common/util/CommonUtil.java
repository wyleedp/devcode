package com.github.devcode.collector.common.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 공통 유틸
 */
public class CommonUtil {

	/**
	 * 영문과 알파벳으로 7자리 랜덤 문자열 생성<br/>
	 *   예) ClgsxAu
	 */
	public static String getTaskId() {
		return RandomStringUtils.randomAlphanumeric(7);
	}
	
}
