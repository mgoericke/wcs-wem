package de.javamark.wcs.wem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fatwire.rest.beans.Application;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.service.InstallService;
import de.javamark.wcs.wem.service.RESTService;

@Controller
public class BaseController {
	
	@Autowired
	WemConfig wcs;

	@Autowired
	RESTService restService;
	
	@Autowired 
	InstallService installService;
	
	
	/**
	 * Admin Page http://hostname:port/admin
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin")
	public String admin(Model model){		
		model.addAttribute("config", wcs);		
		return "admin/index";
	}
	
	/**
	 * Installer	http://hostname:port/install
	 * 
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/install")
	public String install(Model model) throws Exception{		
		model.addAttribute("config", wcs);		
		
		Application wemapp = installService.getApplication();
		
		if(wemapp == null){
			model.addAttribute("msg", "wem application created");
			wemapp = installService.createApplication();
		}else{
			model.addAttribute("msg", "wem application already installed");
		}
		model.addAttribute("wemapp", wemapp);


		
		
		return "install/index";
	}
}
