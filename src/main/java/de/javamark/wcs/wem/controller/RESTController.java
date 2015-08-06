package de.javamark.wcs.wem.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fatwire.rest.beans.AssetBean;
import com.fatwire.rest.beans.Attribute;
import com.fatwire.rest.beans.Attribute.Data;
import com.fatwire.wem.sso.SSOException;

import de.javamark.wcs.wem.model.BlogPost;
import de.javamark.wcs.wem.model.Comment;
import de.javamark.wcs.wem.service.RESTService;
import de.javamark.wcs.wem.utils.Mapper;

@RestController
public class RESTController {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.controller");
	
	@Autowired
	RESTService restService;
	
	

	/**
	 * return a list of sites
	 * 
	 * @param sites
	 * @return
	 * @throws SSOException
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value="/wcs/sites",
			method=RequestMethod.GET,
			produces="application/json")
	public String sites(@RequestParam(value="sites", required=false) String sites) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(restService.getSites());		
	}
	
	
	/**
	 * return a list of assettypes for a site
	 * 
	 * @param sites
	 * @return
	 * @throws SSOException
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value="/wcs/assets/types",
			method=RequestMethod.GET,
			produces="application/json")
	public String enabledTypes(@RequestParam(value="sites", required=false) String sites) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(restService.enabledTypes(sites));		
		
	}
	
	/**
	 * return the definition for an asset type
	 * 
	 * @param assettype
	 * @return
	 * @throws SSOException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RequestMapping(value="/wcs/assets/types/{assettype}",
			method=RequestMethod.GET,
			produces="application/json")
	public String types(@PathVariable(value="assettype") String assettype) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(restService.getType(assettype));		
		
	}
	
	/**
	 * Generic handler to get get assets of a given type
	 * 
	 * @param assettype
	 * @return
	 * @throws SSOException
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value="/wcs/assets/types/{assettype}/search",
			method=RequestMethod.GET,
			produces="application/json")
	public String searchAssets(@PathVariable(value="assettype") String assettype,
			@RequestParam(value="sites", required=false) String sites,
			@RequestParam(value="query", required=false) String query,
			@RequestParam(value="fields", required=false) String fields,
			@RequestParam(value="startIndex", required=false) String startIndex,
			@RequestParam(value="count", required=false) String count,
			@RequestParam(value="sortField", required=false) String sortField,
			@RequestParam(value="sortDirection", required=false) String sortDirection) throws SSOException, JsonGenerationException, JsonMappingException, IOException{
		
		return Mapper.toJSON(restService.search(sites, assettype, query, fields, startIndex, count, sortField, sortDirection));		
	}

	@RequestMapping(value="/comments",
			method=RequestMethod.GET,
			produces="application/json")
	public @ResponseBody List<Comment> comments() throws UnsupportedEncodingException, SSOException{
		return restService.getComments(null);
	}
	@RequestMapping(value="/blogposts",
			method=RequestMethod.GET,
			produces="application/json")
	public @ResponseBody List<BlogPost> blogposts() throws UnsupportedEncodingException, SSOException{
		return restService.getBlogPosts(null);
	}
	@RequestMapping(value="/comments/{commentid}/state/{state}",
			method=RequestMethod.POST)
	public @ResponseBody Comment commentsstate(@PathVariable(value="commentid") String commentid, @PathVariable(value="state") String state) throws UnsupportedEncodingException, SSOException{
		
		// lade comment
		AssetBean asset = restService.read("FW_Comment", commentid);
		List<Attribute> attrs = asset.getAttributes();
		for(Attribute attr : attrs){
			// setze state
			if(attr.getName().equalsIgnoreCase("state")){
				Data data = new Data();
				data.setStringValue(state);
				attr.setData(data);
			}
		}		
		restService.update(asset, "FW_Comment");			
		return restService.getComment(commentid);
		
	}		@RequestMapping(value="/blogposts/{blogpostid}/state/{state}",
			method=RequestMethod.POST)
	public @ResponseBody BlogPost blogpoststate(@PathVariable(value="blogpostid") String blogpostid, @PathVariable(value="state") String state) throws UnsupportedEncodingException, SSOException{
		
		// lade comment
		AssetBean asset = restService.read("FW_BlogPost", blogpostid);
		List<Attribute> attrs = asset.getAttributes();
		for(Attribute attr : attrs){
			// setze state
			if(attr.getName().equalsIgnoreCase("state")){
				Data data = new Data();
				data.setStringValue(state);
				attr.setData(data);
			}
		}		
		restService.update(asset, "FW_BlogPost");			
		return restService.getBlogPosts(blogpostid).get(0);
		
	}	
	
	
	@RequestMapping(value="/comments/{commentid}",
			method=RequestMethod.GET)
	public @ResponseBody Comment getcomment(@PathVariable(value="commentid") String commentid) throws UnsupportedEncodingException, SSOException{		
		// lade comment
		return restService.getComment(commentid);
	}
	
	
	
}
