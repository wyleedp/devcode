package com.github.devcode.collector.common.util;

import org.apache.commons.lang3.RandomStringUtils;

public class CommonUtil {

	public static String getTaskId() {
		return RandomStringUtils.randomAlphanumeric(6);
	}
	
}
