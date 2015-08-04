package de.javamark.wcs.wem.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fatwire.rest.beans.AssetInfo;
import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.fatwire.wem.sso.SSOPrincipal;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.model.Comment;
import de.javamark.wcs.wem.model.Product;
import de.javamark.wcs.wem.service.RESTService;

@Controller
public class PubController {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.controller");
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
	public String app(Model model, @RequestParam(value="filter", required=false) String filter){
		
		// always add config to context
		model.addAttribute("config", wcs);

		try {
		List<AssetInfo> assetInfos = restService.search(null, "Product_C", filter, null, null, null, null, null).getAssetinfos();
		List<Product> products = new ArrayList<Product>();
		for(AssetInfo info : assetInfos){
			String[] id = info.getId().split(":");
			Product product = restService.getProduct(id[0], id[1]);
			product.setComments(restService.getComments(id[1]));
			products.add(product);
		}
		
		// add the products to the context
		model.addAttribute("products", products);
		
		if(SSO.getSSOSession() != null && SSO.getSSOSession().getAssertion() != null && SSO.getSSOSession().getAssertion().getPrincipal() != null){
			SSOPrincipal user = SSO.getSSOSession().getAssertion().getPrincipal();
			
			
			model.addAttribute("userDisplayName", user.getDisplayName());
		}
		}catch(SSOException e){
			model.addAttribute("error", e);
		}catch (Exception e) {
			model.addAttribute("error", e);
		}
		
		return "app/index";
	}

	@RequestMapping(value="/comments/product/{productid}")
	public String comments(@PathVariable(value="productid") String productid, Model model){
		
		// lade kommentare und übergib diese an das Comment Template
		log.debug("lade comments für :" + productid);
		try {
			List<Comment> commentsList = restService.getComments(productid);
			model.addAttribute("comments", commentsList);
		} catch (UnsupportedEncodingException e) {
			model.addAttribute("error", e);
		} catch (SSOException e) {
			model.addAttribute("error", e);
		}
		
		return "comments/template";
	}
}
