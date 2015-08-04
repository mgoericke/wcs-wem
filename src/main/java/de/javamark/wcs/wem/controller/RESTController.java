package de.javamark.wcs.wem.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatwire.wem.sso.SSOException;

import de.javamark.wcs.wem.service.RESTService;
import de.javamark.wcs.wem.utils.Mapper;

@RestController
public class RESTController {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.controller");
	
	@Autowired
	RESTService assetService;
	

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
	@RequestMapping(value="/sites",
			method=RequestMethod.GET,
			produces="application/json")
	public String sites(@RequestParam(value="sites", required=false) String sites) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(assetService.getSites());		
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
	@RequestMapping(value="/assets/types",
			method=RequestMethod.GET,
			produces="application/json")
	public String enabledTypes(@RequestParam(value="sites", required=false) String sites) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(assetService.enabledTypes(sites));		
		
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
	@RequestMapping(value="/assets/types/{assettype}",
			method=RequestMethod.GET,
			produces="application/json")
	public String types(@PathVariable(value="assettype") String assettype) throws SSOException, JsonGenerationException, JsonMappingException, IOException{		
		return Mapper.toJSON(assetService.getType(assettype));		
		
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
	@RequestMapping(value="/assets/types/{assettype}/search",
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
		
		return Mapper.toJSON(assetService.search(sites, assettype, query, fields, startIndex, count, sortField, sortDirection));		
	}
}
