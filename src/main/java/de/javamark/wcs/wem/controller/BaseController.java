package de.javamark.wcs.wem.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fatwire.rest.beans.Application;
import com.fatwire.rest.beans.AssetTypeBean;
import com.fatwire.wem.sso.SSOException;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.model.BlogPost;
import de.javamark.wcs.wem.model.Comment;
import de.javamark.wcs.wem.service.InstallApplicationService;
import de.javamark.wcs.wem.service.InstallBlogPostAssetTypeService;
import de.javamark.wcs.wem.service.InstallCommentAssetTypeService;
import de.javamark.wcs.wem.service.RESTService;

@Controller
public class BaseController {
	
	Logger log = Logger.getLogger("de.javamark.wcs.wem.controller");
	
	@Autowired
	WemConfig wcs;

	@Autowired
	RESTService restService;
	
	@Autowired 
	InstallApplicationService installService;

	@Autowired 
	InstallCommentAssetTypeService installCommentAssetTypeService;

	@Autowired 
	InstallBlogPostAssetTypeService installBlogPostAssetTypeService;
	
	
	/**
	 * Admin Page http://hostname:port/admin
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin")
	public String admin(Model model){		
		model.addAttribute("config", wcs);

		model.addAttribute("currentType", "comments");
		model.addAttribute("currentTypeDesc", "Kommentare");
		
		return "admin/index";
	}
	@RequestMapping(value="/admin/comments")
	public String adminComments(@RequestParam(value="state", required=false, defaultValue="waiting") String state,Model model){		
		
		if(state.equalsIgnoreCase("all")){
			state = null;
		}
				
		model.addAttribute("config", wcs);
		
		// lade kommentare
		try {
			List<Comment> comments = restService.getComments(state);
			
			if(comments != null){
				// h�nge das Product an, zu dem der Kommentar geh�rt
				for(Comment comment : comments){
					comment.setProduct(restService.getProduct(comment.getReltype(), comment.getRelid()));
				}
				
				model.addAttribute("comments", comments);
			}else{
				System.out.println("hmmm ... no comments found");
			}
		} catch (UnsupportedEncodingException e) {
			model.addAttribute("error", e);
		} catch (SSOException e) {
			model.addAttribute("error", e);
		}

		model.addAttribute("currentState", state);
		model.addAttribute("currentType", "comments");
		model.addAttribute("currentTypeDesc", "Kommentare");
		
		return "admin/index";
	}	
	
	@RequestMapping(value="/admin/blogposts")
	public String adminBlogPosts(@RequestParam(value="state", required=false, defaultValue="waiting") String state,Model model){		

		if(state.equalsIgnoreCase("all")){
			state = null;
		}
				
		model.addAttribute("config", wcs);
		
		// lade blogposts
		try {
			List<BlogPost> blogposts = restService.getBlogPosts(state);
			
			if(blogposts != null){
				
				
				model.addAttribute("blogposts", blogposts);
			}else{
				System.out.println("hmmm ... no blogposts found");
			}
		} catch (UnsupportedEncodingException e) {
			model.addAttribute("error", e);
		} catch (SSOException e) {
			model.addAttribute("error", e);
		}

		model.addAttribute("currentState", state);
		model.addAttribute("currentType", "blogposts");
		model.addAttribute("currentTypeDesc", "Blog Posts");
		
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
		
		// WEM Application
		if(wemapp == null){			
			wemapp = installService.createApplication();
			model.addAttribute("msg", "wem application created");
			wemapp = installService.createApplication();
		}else{
			model.addAttribute("msg", "wem application already installed");
		}
		model.addAttribute("wemapp", wemapp);

		// Comments AssetType
		AssetTypeBean commentType = installCommentAssetTypeService.getAssetType();
		if(commentType == null){
			commentType = installCommentAssetTypeService.createAssetType();
			installCommentAssetTypeService.enableAssetType();
			installCommentAssetTypeService.indexAssetType();
			installCommentAssetTypeService.installDummyComments();
			model.addAttribute("commentTypeMsg", "comment assettype created and dummy content installed (<a href=\""+wcs.getRestUrl() + "/types/FW_Comment/search\" target=\"_blank\">Search Assets</a>)");			
		}else{
			model.addAttribute("commentTypeMsg", "comment assettype already installed");
		}
		model.addAttribute("commentType", commentType);

		try{
			
		// BlogPost AssetType
		AssetTypeBean blogPostType = installBlogPostAssetTypeService.getAssetType();
		if(blogPostType == null){
			blogPostType = installBlogPostAssetTypeService.createAssetType();
			installBlogPostAssetTypeService.enableAssetType();
			installBlogPostAssetTypeService.indexAssetType();
			installBlogPostAssetTypeService.installDummyContents();
			model.addAttribute("blogPostTypeMsg", "blog post assettype created and dummy content installed (<a href=\""+wcs.getRestUrl() + "/types/FW_BlogPost/search\" target=\"_blank\">Search Assets</a>)");			
		}else{
			model.addAttribute("blogPostTypeMsg", "blog post assettype already installed");
		}
		model.addAttribute("blogPostType", blogPostType);

		}catch(Exception e){
			
		}
		return "install/index";
	}
}
