/**
 * 
 */
package de.javamark.wcs.wem.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatwire.rest.beans.AssetTypeBean;
import com.fatwire.rest.beans.AssetsBean;
import com.fatwire.rest.beans.EnabledTypesBean;
import com.fatwire.rest.beans.SitesBean;
import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.javamark.wcs.wem.WemConfig;

/**
 * @author mark
 *
 */
@Service
public class RESTService {

	@Autowired
	WemConfig wcs;
	
	String multiTicket;

	/**
	 * 
	 * @param sites			wcs sites
	 * @param assetType		wcs asset type
	 * @param query			query
	 * @param fields		fields included in response (optional)
	 * @param startindex	start at (optional)
	 * @param count			size of the result (optional)
	 * @param sortfield		asset attribute name (optional)
	 * @param sortDirection	direction (asc|des) ... yes, it is des and not desc (optional)
	 * @return
	 * @throws SSOException 
	 * @throws UnsupportedEncodingException 
	 */
	public AssetsBean search(String sites, 
			String assetType,
			String query,
			String fields,
			String startindex,
			String count,
			String sortfield,
			String sortDirection) throws SSOException, UnsupportedEncodingException{
		
		// http://<hostname>:<port>/cs/REST/types/{assetType}/search?q={query}&startindex={startindex}&count={count}&sortfield:{sortfield}:{sortDirection}
		
        // build a path
        WebResource webResource = getWebResource().path("sites").
        		path((sites!=null)?sites:wcs.getCsSiteName()).
		path("types").
		path(assetType).
		path("search");
        

		// query
		if(query != null){
			webResource = webResource.queryParam("q", query); // default = contains
		}
		// fields
		if(fields != null){
			webResource = webResource.queryParam("fields", URLEncoder.encode(fields, "UTF-8"));			
		}
		// startindex
		if(startindex != null){
			webResource = webResource.queryParam("startindex", startindex);			
		}
		// count
		if(count != null){
			webResource = webResource.queryParam("count", count);			
		}
		// count
		if(sortfield != null){
			webResource = webResource.queryParam("sortfield:"+sortfield+":" + (("asc".equalsIgnoreCase(sortDirection))?"asc":"des"), "");			
		}
        
		// Add the Media Type
		Builder builder = webResource.accept(MediaType.APPLICATION_XML);
        builder = builder.header("X-CSRF-Token", this.multiTicket);
		

        System.out.println(webResource.getURI());
        
		// result
        AssetsBean assetsBean = builder.get(AssetsBean.class); 
        return assetsBean;
	}
	/**
	 * return a list of assettypes of a site or all asset types if sites is null
	 * 
	 * @param sites
	 * @return
	 * @throws SSOException
	 * @throws UnsupportedEncodingException
	 */
	public EnabledTypesBean enabledTypes(String sites) throws SSOException, UnsupportedEncodingException{
		
		// http://<hostname>:<port>/cs/REST/sites/{sites}/types
		
        // build a path
		WebResource webResource = getWebResource().path("sites").
		path((sites!=null)?sites:wcs.getCsSiteName()).
		path("types");
       
        
		// Add the Media Type
		Builder builder = webResource.accept(MediaType.APPLICATION_JSON);
        builder = builder.header("X-CSRF-Token", this.multiTicket);
		
        System.out.println(webResource.getURI());
		// result
        EnabledTypesBean assetTypesBean = builder.get(EnabledTypesBean.class); 
        
        return assetTypesBean;
	}	
	/**
	 * load asset type configuration
	 * 
	 * @param type
	 * @return
	 * @throws SSOException
	 * @throws UnsupportedEncodingException
	 */
	public AssetTypeBean getType(String type) throws SSOException, UnsupportedEncodingException{
		
		// http://<hostname>:<port>/cs/REST/sites/{sites}/types
		
        // build a path
		WebResource webResource = getWebResource().
				path("types")
				.path(type);
       
        
		// Add the Media Type
		Builder builder = webResource.accept(MediaType.APPLICATION_JSON);
        builder = builder.header("X-CSRF-Token", this.multiTicket);
		
        System.out.println(webResource.getURI());
		// result
        AssetTypeBean assetTypeBean = builder.get(AssetTypeBean.class); 
        
        return assetTypeBean;
	}
	
	/**
	 * return the available sites
	 * @return
	 * @throws SSOException
	 * @throws UnsupportedEncodingException
	 */
	public SitesBean getSites() throws SSOException, UnsupportedEncodingException{
		
		// http://<hostname>:<port>/cs/REST/sites
		
        // build a path
		WebResource webResource = getWebResource().path("sites");
       
        
		// Add the Media Type
		Builder builder = webResource.accept(MediaType.APPLICATION_JSON);
        builder = builder.header("X-CSRF-Token", this.multiTicket);
		
        System.out.println(webResource.getURI());
		// result
        SitesBean sitesBean = builder.get(SitesBean.class); 
        return sitesBean;
	}
	
	
	private WebResource getWebResource() throws SSOException{
		// 1. Jersey client
		Client client = Client.create();
				
		// WeResource with base url
		WebResource webResource = client.resource(wcs.getRestUrl());
		        
		// Step 3: Authenticate over SSO-CAS to acquire a ticket specific to a
		// service or a multi-ticket over multiple services.
		this.multiTicket = SSO.getSSOSession().getMultiTicket(wcs.getCsUsername(), wcs.getCsPassword());
		        
		// Step 4: Provide the ticket into the REST request
		webResource = webResource.queryParam("multiticket", this.multiTicket);
		
		return webResource;
	}
}
