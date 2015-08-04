/**
 * 
 */
package de.javamark.wcs.wem.service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatwire.rest.beans.AssetBean;
import com.fatwire.rest.beans.AssetInfo;
import com.fatwire.rest.beans.AssetTypeBean;
import com.fatwire.rest.beans.AssetsBean;
import com.fatwire.rest.beans.Attribute;
import com.fatwire.rest.beans.EnabledTypesBean;
import com.fatwire.rest.beans.SitesBean;
import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.model.Comment;
import de.javamark.wcs.wem.model.Product;
import de.javamark.wcs.wem.utils.Mapper;

/**
 * @author mark
 *
 */
@Service
public class RESTService {

	Logger log = Logger.getLogger("de.javamark.wcs.wem.service");
	
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
	
	
	/**
	 * return an AssetBean containing all properties of the corresponding asset
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws SSOException
	 */
	public AssetBean read(String type, String id) throws SSOException{		
		// path
		WebResource resource = getWebResource();		
		resource = resource.path("sites").
        		path(wcs.getCsSiteName()).
        		path("types").
        		path(type).
        		path("assets").
        		path(id);
		
		// media type
        Builder builder = resource.accept(MediaType.APPLICATION_XML);
        builder = builder.header("X-CSRF-Token", this.multiTicket);
        
        // result
        AssetBean resultAsset = builder.get(AssetBean.class);        
        return resultAsset;
	}
	

	public List<Comment> getComments(String query) throws UnsupportedEncodingException, SSOException{

		// name of all attributes included in result
        String fields = "name,description,content,cat,source,title,relid,reltype,state,createdby,updateddate,rating";
		
        List<AssetInfo> assetinfoList = search(wcs.getCsSiteName(), "FW_Comment", query, fields, null, null, null, null).getAssetinfos();
        List<Comment> assets = null;
        
        
        if(assetinfoList!= null && assetinfoList.size()>0){
        	assets = new ArrayList<Comment>();
	        for(AssetInfo info : assetinfoList){
	        	
	        		
	        	
	        	String relid = Mapper.getField("relid",info.getFieldinfos());
	        	String reltype = Mapper.getField("reltype",info.getFieldinfos());


	        	Comment a = new Comment();
	        	a.setId(info.getId().split(":")[1]);
	        	a.setDescription(Mapper.getField("description",info.getFieldinfos()) );
	        	a.setName(Mapper.getField("name",info.getFieldinfos()) );
	        	a.setTitle(Mapper.getField("title",info.getFieldinfos()) );
	        	a.setContent(Mapper.getField("content",info.getFieldinfos()) );
	        	a.setState(Mapper.getField("state",info.getFieldinfos()) );
	        	a.setRating(Mapper.getField("rating",info.getFieldinfos()) );		        	
	        	a.setRelid(relid);
	        	a.setReltype(reltype);

	        	a.setCreatedby(Mapper.getField("createdby",info.getFieldinfos()) );
	        	a.setCreateddate(Mapper.getField("updateddate",info.getFieldinfos()) );


	        	assets.add(a);
	        }
        }
        
        return assets;
		
	}
	
	public Product getProduct(String type, String id) throws SSOException{
		AssetBean aBean = read(type, id);
		
		if(aBean!=null){
			Product asset = new Product();		

			asset.setCreatedby(aBean.getCreatedby());
			asset.setCreateddate(aBean.getUpdateddate().toString());
			asset.setName(aBean.getName());
			asset.setId(aBean.getId().split(":")[1]);

			String titleAttrName 	= "FSIIProductName";
			String descAttrName 	= "FSIILongDescription";
			String imgAttrName 		= "FSIIImage";
			String imgFileAttr 		= "FSII_ImageFile";
			
			
			List<Attribute> attributes = aBean.getAttributes();
			for(Attribute attr : attributes){
				
				//log.debug("attr ["+attr.getName()+"]:");
				

				if(attr.getName().equalsIgnoreCase(titleAttrName)){
					asset.setTitle(attr.getData().getStringValue());
				}
				if(attr.getName().equalsIgnoreCase(descAttrName)){
					asset.setDescription(attr.getData().getStringValue());
				}
				if(attr.getName().equalsIgnoreCase("updateddate")){
					asset.setCreateddate(attr.getData().getStringValue());
				}
				

				// image file Media_C
				// complex blob!
	            try {
					if(attr.getName().equalsIgnoreCase(imgFileAttr)){
						asset.setImageFile(attr.getData().getBlobValue().getHref());
					}
				} catch (Exception e) {
					System.err.println(e);
				}
				
	            
	            // 
				if(attr.getName().equalsIgnoreCase(imgAttrName)){
					asset.setImage(attr.getData().getStringValue());
					
					// assuming it has the format:
		            try {
			            String[] assetId = asset.getImage().split(":");
			            
			            // image laden 
			            Product img = getProduct(assetId[0], assetId[1]);
			            
			            // ziel: http://localhost:9080/cs/Satellite?blobcol=urldata&blobkey=id&blobtable=MungoBlobs&blobwhere=1114083738284&ssbinary=true
			            // quelle: http://localhost:9080/cs/BlobServer?blobkey=id&amp;blobnocache=true&amp;blobwhere=1114083738284&amp;blobheadername3=MDT-Type&amp;blobheadername2=Content-Disposition&amp;blobheadername1=Content-Type&amp;blobheadervalue3=abinary%3B+charset%3DUTF-8&amp;blobheadervalue2=attachment%3B+filename%3D%22SS_HDPlasmaTV.jpg%22%3Bfilename*%3DUTF-8%27%27SS_HDPlasmaTV.jpg&amp;blobheadervalue1=image%2Fjpeg&amp;blobcol=urldata&amp;blobtable=MungoBlobs
			            if(img.getImageFile() != null){
			            	String imgFile = img.getImageFile().replaceAll("amp;", "");
			            	URL url = new URL(imgFile);
			            	Map<String, String> params = new HashMap<String, String>();
			            	String[] queryparams = url.getQuery().split("&");
			            	for(String qp : queryparams){        
			            		int idx = qp.indexOf("=");
			            		params.put(URLDecoder.decode(qp.substring(0, idx), "UTF-8"), URLDecoder.decode(qp.substring(idx + 1), "UTF-8"));
			            	}
			            	
			            	String bloburl = "";
			            	bloburl += wcs.getCsUrl();
			            	bloburl += "/Satellite?";
			            	bloburl += "blobcol=urldata&";
			            	bloburl += "blobkey=id&";
			            	bloburl += "blobtable=MungoBlobs&";
			            	bloburl += "blobwhere=" + params.get("blobwhere") + "&";
			            	bloburl += "ssbinary=true";

				            
							asset.setBlobUrl(bloburl);
			            }			            			            
					} catch (Exception e) {
						System.err.println(e);
					}
				}
			}
			return asset;
		}		
		return null;
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
