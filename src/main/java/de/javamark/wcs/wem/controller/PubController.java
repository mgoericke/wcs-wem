package de.javamark.wcs.wem.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.fatwire.wem.sso.SSOPrincipal;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.service.RESTService;

@Controller
public class PubController {
	@Autowired
	WemConfig wcs;

	@Autowired
	RESTService restService;
	
	
	/**
	 * Home Page http://hostname:port/
	 * 
	 * @param model
	 * @return
	 * @throws SSOException 
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/app")
	public String app(Model model, @RequestParam(value="filter", required=false) String filter) throws UnsupportedEncodingException, SSOException{
		
		// always add config to context
		model.addAttribute("config", wcs);
		
		// add the products to the context
		model.addAttribute("products", restService.search(null, "Product_C", filter, null, null, null, null, null).getAssetinfos());
		
		if(SSO.getSSOSession() != null && SSO.getSSOSession().getAssertion() != null && SSO.getSSOSession().getAssertion().getPrincipal() != null){
			SSOPrincipal user = SSO.getSSOSession().getAssertion().getPrincipal();
			
			
			model.addAttribute("userDisplayName", user.getDisplayName());
		}
		
		return "app/index";
	}
}
