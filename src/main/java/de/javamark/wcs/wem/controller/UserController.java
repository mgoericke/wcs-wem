package de.javamark.wcs.wem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
	
	@RequestMapping(value="/user/login")
	public String login(){
		return "user/logintemplate";
	}
}
