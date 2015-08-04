package de.javamark.wcs.wem.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.controller");

//	@RequestMapping(value="/user/login")
//	public String login(){
//		return "user/logintemplate";
//	}
	@RequestMapping(value="/user/welcome")
	public String welcome(){
		return "user/welcome";
	}
}
