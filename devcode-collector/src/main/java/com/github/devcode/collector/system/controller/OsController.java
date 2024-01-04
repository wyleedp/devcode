package com.github.devcode.collector.system.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.github.devcode.collector.DevcodeCollectorConstant;

/**
 * OS의 정보 및 리소스를 수집하는 Controller 
 */
@Controller
public class OsController {

	private static final Logger logger = LogManager.getLogger(OsController.class);
	
	/**
	 * Call Test
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/system/os/info", method = RequestMethod.POST)
	public ResponseEntity info(
			  HttpServletRequest request
		) {
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			logger.info("{}={}", headerName, headerValue);
		}
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * OS 리눅스 정보 수집
	 * 
	 * @param request
	 * @param systemOsInfo
	 * @param systemOsInfoDf
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/system/os/info/linux", method = RequestMethod.POST)
	public ResponseEntity systemOsInfoLinux(
			  HttpServletRequest request
			, @RequestParam(name = "systemOsInfo", required = false) MultipartFile systemOsInfo
			, @RequestParam(name = "systemOsInfoEnv", required = false) MultipartFile systemOsInfoEnv
			, @RequestParam(name = "systemOsInfoDf", required = false) MultipartFile systemOsInfoDf
			, @RequestParam(name = "systemOsInfoNet", required = false) MultipartFile systemOsInfoNet
		) {
		
		long systemOsInfoDataSize = -1;
		long systemOsInfoEnvDataSize = -1;
		long systemOsInfoDfDataSize = -1;
		long systemOsInfoNetDataSize = -1;
		
		// OS 리눅스 기본정보
		if(systemOsInfo != null && systemOsInfo.getSize() > 0) {
			systemOsInfoDataSize = systemOsInfo.getSize();
		}
		
		// OS 리눅스 env
		if(systemOsInfoEnv != null && systemOsInfoEnv.getSize() > 0) {
			systemOsInfoEnvDataSize = systemOsInfoEnv.getSize();
		}
		
		// OS 리눅스 df
		if(systemOsInfoDf != null && systemOsInfoDf.getSize() > 0) {
			systemOsInfoDfDataSize = systemOsInfoDf.getSize();
		}
		
		// OS 리눅스 Net
		if(systemOsInfoNet != null && systemOsInfoNet.getSize() > 0) {
			systemOsInfoNetDataSize = systemOsInfoNet.getSize();
		}
		
		logger.info("systemOsInfoDataSize : {}, systemOsInfoDfDataSize : {}", systemOsInfoDataSize, systemOsInfoDfDataSize);
		
		try {
			// 경로 예) C:\Users\wylee\AppData\Local\Temp/devcode/system
			String devcodeSystemTempDir = SystemUtils.getJavaIoTmpDir() + "/devcode/system";
			FileUtils.forceMkdir(new File(devcodeSystemTempDir));
			logger.debug("TempDir : {}", devcodeSystemTempDir);
			
			String sysDateTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
			
			// OS Info
			if(systemOsInfoDataSize > 0) {
				String osInfoFileName = "osInfo." + sysDateTime;
				
				// 파일경로 예) C:\Users\wylee\AppData\Local\Temp/devcode/system/osInfo.20230809095557606
				String osInfoFilePath = devcodeSystemTempDir + "/" + osInfoFileName;
				File osInfoFile = new File(osInfoFilePath);
				systemOsInfo.transferTo(osInfoFile);

				// 파일내용출력
				logger.debug("osInfoFileRead - {} \n{}", osInfoFilePath, FileUtils.readFileToString(osInfoFile, DevcodeCollectorConstant.FILE_ENCODING));
			}
			
			// OS Info Env
			if(systemOsInfoEnvDataSize > 0) {
				String envFileName = "osInfoEnv." + sysDateTime;
				
				// 파일경로 예) C:\Users\wylee\AppData\Local\Temp/devcode/system/osInfoEnv.20230809095557606
				String filePath = devcodeSystemTempDir + "/" + envFileName;
				File file = new File(filePath);
				systemOsInfoEnv.transferTo(file);

				// 파일내용출력
				logger.debug("osInfoEnvFileRead - {} \n{}", filePath, FileUtils.readFileToString(file, DevcodeCollectorConstant.FILE_ENCODING));
			}
			
			// OS Info Df
			if(systemOsInfoDfDataSize > 0) {
				String dfFileName = "osInfoDf." + sysDateTime;
				
				// 파일경로 예) C:\Users\wylee\AppData\Local\Temp/devcode/system/osInfoDf.20230809095557606
				String filePath = devcodeSystemTempDir + "/" + dfFileName;
				File file = new File(filePath);
				systemOsInfoDf.transferTo(file);

				// 파일내용출력
				logger.debug("osInfoDfFileRead - {} \n{}", filePath, FileUtils.readFileToString(file, DevcodeCollectorConstant.FILE_ENCODING));
			}
			
			// OS Info Net
			if(systemOsInfoNetDataSize > 0) {
				String netFileName = "osInfoNet." + sysDateTime;
				
				// 파일경로 예) C:\Users\wylee\AppData\Local\Temp/devcode/system/osInfoNet.20230809095557606
				String filePath = devcodeSystemTempDir + "/" + netFileName;
				File file = new File(filePath);
				systemOsInfoNet.transferTo(file);

				String netReadFile = FileUtils.readFileToString(file, DevcodeCollectorConstant.FILE_ENCODING);
				
				// 파일내용출력
				logger.debug("osInfoNetFileRead - {} \n{}", filePath, netReadFile);
			}
		} catch (IOException e) {
			logger.error(e);
		}
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * OS 리소스를 수집
	 * 
	 * @param p
	 * @return
	 */
	@RequestMapping(value="/system/os/resource")
	public String resource(String p) {
		logger.info("p : {}", p);
		
		return "ok";
	}
	
}
