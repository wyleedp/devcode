package com.github.devcode.collector.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OsController {

	@RequestMapping(value="/system/os/resource", method = RequestMethod.GET)
	public String resource(String p) {
		System.out.println("p : " + p);
		
		return "ok";
	}
	
}
