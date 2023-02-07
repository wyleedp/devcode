package com.github.devcode.cmd;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class DevcodeCmdApp {

	public static void printUsage() {
		System.out.println("Usage: devcode-cmd [options] or dcmd [options]");
		System.out.println("");
		System.out.println("Opions");
		System.out.println("  -i, --info       OS 및 JAVA 정보");
		System.out.println("");
	}
	
	public static void info() {
		System.out.println("Summary");
		System.out.println("HOST_NAME          : " + SystemUtils.getHostName());
		System.out.println("OS_NAME            : " + SystemUtils.OS_NAME);
		System.out.println("OS_VERSION         : " + SystemUtils.OS_VERSION);
		System.out.println("OS_ARCH            : " + SystemUtils.OS_ARCH);
		System.out.println("JAVA_VERSION       : " + SystemUtils.JAVA_VERSION);
		System.out.println("JAVA_HOME          : " + SystemUtils.JAVA_HOME);
		System.out.println("JAVA_VENDOR        : " + SystemUtils.JAVA_VENDOR);
		System.out.println("JAVA_IO_TMPDIR     : " + SystemUtils.JAVA_IO_TMPDIR);
		System.out.println("PID                : " + ManagementFactory.getRuntimeMXBean().getPid());
		
		System.out.println("java.vm.name       : " + System.getProperty("java.vm.name"));
		System.out.println("java.vm.version    : " + System.getProperty("java.vm.version"));
		System.out.println("java.class.version : " + System.getProperty("java.class.version"));
		System.out.println("sun.java.command   : " + System.getProperty("sun.java.command"));            // 실행클래스명 + 인자 - 예) com.github.devcode.cmd.DevcodeCmdApp -i
		System.out.println("user.name          : " + System.getProperty("user.name"));
		System.out.println("user.home          : " + System.getProperty("user.home"));
		System.out.println("user.language      : " + System.getProperty("user.language"));
		System.out.println("user.dir           : " + System.getProperty("user.dir"));
		
		Map<String, String> envMap = System.getenv();
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		
		System.out.println("");
		System.out.println("System.getenv()");
		for(String key : envMap.keySet()){
			String value = envMap.get(key);
			treeMap.put(key, value);
		}
		
		for(String key : treeMap.keySet()){
			String value = treeMap.get(key);
			System.out.println(key + "=" + value);
		}
		
		System.out.println("");
		System.out.println("System.getProperties()");
		Properties properties = System.getProperties();
		treeMap.clear();
		
		for (Object key : properties.keySet()) {
			treeMap.put(key.toString(), properties.getProperty(key.toString()));
		}
		
		for(String key : treeMap.keySet()){
			String value = treeMap.get(key);
			if(StringUtils.equals(key, "line.separator")) {
				System.out.println(key + "=");
			}else {
				System.out.println(key + "=" + value);
			}
		}
	}
	
	public static void main(String[] args) {
		if(args == null || args.length == 0) {
			printUsage();
			return;
		}
		
		String arg = args[0];
		if(StringUtils.equalsAny(arg, "-i", "--info")) {
			info();
			return;
		}
		
		printUsage();
	}

}
