package de.javamark.wcs.wem.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatwire.rest.beans.AssetBean;
import com.fatwire.rest.beans.AssetInfo;
import com.fatwire.rest.beans.AssetTypeBean;
import com.fatwire.rest.beans.Attribute;
import com.fatwire.rest.beans.Attribute.Data;
import com.fatwire.rest.beans.AttributeDefBean;
import com.fatwire.rest.beans.AttributeTypeEnum;
import com.fatwire.rest.beans.EnabledType;
import com.fatwire.rest.beans.IndexConfigBean;
import com.fatwire.rest.beans.IndexFieldDescriptor;
import com.fatwire.rest.beans.IndexFieldTypeEnum;
import com.fatwire.rest.beans.IndexStatus;
import com.fatwire.rest.beans.IndexStatusEnum;
import com.fatwire.rest.beans.SiteBean;
import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.javamark.wcs.wem.WemConfig;
import de.javamark.wcs.wem.model.Product;

@Service
public class InstallCommentAssetTypeService{

	Logger log = Logger.getLogger("de.javamark.wcs.wem.service");

	@Autowired
	RESTService restService;
	
	private String assetTypeName		= "FW_Comment";
	private String assetTypeDescription = "Asset Comments";
	
	@Autowired
	WemConfig config;
	
	public enum STATE {
		WAITING,
		APPROVED,
		REJECTED
	}
	

	
	public void installDummyComments(){
		// install demo data
		String[] titles = {
				"Tolles Produkt.", 
				"Jederzeit wieder", 
				"Kann man nur empfehlen", 
				"Wunderschön", 
				"Ok, aber verbesserungswürdig", 
				"kann man kaufen, muß man aber nicht", 
				"Hatte schon meine Oma!", 
				"Super, schnell geliefert", 
				"Spitzengerät ...", 
				"Jederzeit wieder", 
				"Kann man nur empfehlen", 
				"Wunderschön", 
				"Ok, aber verbesserungswürdig", 
				"kann man kaufen, muß man aber nicht", 
				"Hatte schon meine Oma!", 
				"Super, schnell geliefert", 
				"Spitzengerät ..."
		};

		try {
			List<AssetInfo> assetInfos = restService.search(null, "Product_C", null, null, null, null, null, null).getAssetinfos();	
			List<Product> productList = new ArrayList<Product>();
			for(AssetInfo info : assetInfos){
				String[] id = info.getId().split(":"); // 0=type, 1=id
				Product product = restService.getProduct(id[0], id[1]);
				productList.add(product);
			}
			
			int len = 0;
			if(assetInfos.size() >= titles.length){
				len = titles.length;
			}else{
				len = assetInfos.size();
			}
			

			// create comments on products
			for(int i=0; i<len; i++){

				// "random" states for comments ...
				STATE state = (i%2 == 0)?STATE.APPROVED:STATE.WAITING;
				
				
				try {
					createAsset(
							titles[i], 
							titles[i], 
							"Produkt Kommentare", 
							config.getApplicationName(), 
							titles[i], 
							null, 
							"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
							productList.get(i).getId(), 
							"Product_C",
							state, 
							"4");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// add a comment to each 3rd product
				try{
					
					if(i%3 == 0){
						state = (i%2 == 0)?STATE.APPROVED:STATE.WAITING;
						createAsset(
								titles[0], 
								titles[0], 
								"WEM Kommentare", 
								config.getApplicationName(), 
								titles[0], 
								null, 
								"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
								productList.get(i).getId(), 
								"Product_C",
								state, "3");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SSOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.javamark.wem.service.FWAssetTypeService#checkType()
	 */
    public AssetTypeBean getAssetType() throws Exception{
        // Create Jersey client.
        Client client = Client.create();
        String url = config.getRestUrl() + "/types/" + assetTypeName;
        WebResource res = client.resource(url);
        res = res.queryParam("ticket", SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword()));
        Builder bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
        
        try{
            // Test type for existence.            
            return bld.get(AssetTypeBean.class);
        }
        catch (UniformInterfaceException ex)
        {
            return null;
        }
    }
    
    /* (non-Javadoc)
	 * @see de.javamark.wem.service.FWAssetTypeService#createType()
	 */
    public AssetTypeBean createAssetType() throws Exception{
        // Create Jersey client.
        Client client = Client.create();
        String url = config.getRestUrl() + "/types/" + assetTypeName;
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword());
        res = res.queryParam("ticket", ticket);
        Builder bld = res.accept(MediaType.APPLICATION_XML);     
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);

        // Create asset type object.
        AssetTypeBean type = new AssetTypeBean();
        type.setName(assetTypeName);
        type.setDescription(assetTypeDescription);
        type.setCanBeChild(false);
        
        List<AttributeDefBean> attrs = new ArrayList<AttributeDefBean>();        



        // Populate rating attribute definition.
        AttributeDefBean rating_attr = new AttributeDefBean();
        rating_attr.setName("rating");
        rating_attr.setType(AttributeTypeEnum.STRING);
        rating_attr.setDescription("Rating 1-5");
        rating_attr.setIsDataMandatory(false);
        rating_attr.setIsMetaData(false);
        rating_attr.setDataLength(4);
        attrs.add(rating_attr);
        
        // Populate cat attribute definition.
        AttributeDefBean state_attr = new AttributeDefBean();
        state_attr.setName("state");
        state_attr.setType(AttributeTypeEnum.STRING);
        state_attr.setDescription("Approval State");
        state_attr.setIsDataMandatory(true);
        state_attr.setIsMetaData(false);
        state_attr.setDataLength(256);
        attrs.add(state_attr);

        // Populate cat attribute definition.
        AttributeDefBean reltype_attr = new AttributeDefBean();
        reltype_attr.setName("reltype");
        reltype_attr.setType(AttributeTypeEnum.STRING);
        reltype_attr.setDescription("Related Asset Type");
        reltype_attr.setIsDataMandatory(true);
        reltype_attr.setIsMetaData(false);
        reltype_attr.setDataLength(256);
        attrs.add(reltype_attr);

        // Populate cat attribute definition.
        AttributeDefBean article_attr = new AttributeDefBean();
        article_attr.setName("relid");
        article_attr.setType(AttributeTypeEnum.STRING);
        article_attr.setDescription("Related Asset Id");
        article_attr.setIsDataMandatory(true);
        article_attr.setIsMetaData(false);
        article_attr.setDataLength(256);
        attrs.add(article_attr);
        
        // Populate cat attribute definition.
        AttributeDefBean cat_attr = new AttributeDefBean();
        cat_attr.setName("cat");
        cat_attr.setType(AttributeTypeEnum.STRING);
        cat_attr.setDescription("Comment Category");
        cat_attr.setIsDataMandatory(true);
        cat_attr.setIsMetaData(false);
        cat_attr.setDataLength(256);
        attrs.add(cat_attr);
        
        // Populate source attribute definition.
        AttributeDefBean source_attr = new AttributeDefBean();
        source_attr.setName("source");
        source_attr.setType(AttributeTypeEnum.STRING);
        source_attr.setDescription("Comment Source");
        source_attr.setIsDataMandatory(true);
        source_attr.setIsMetaData(false);
        source_attr.setDataLength(256);
        attrs.add(source_attr);
        
        
        // Populate content attribute definition.
        AttributeDefBean title_attr = new AttributeDefBean();
        title_attr.setName("title");
        title_attr.setType(AttributeTypeEnum.LARGE_TEXT);
        title_attr.setDescription("Comment Title");
        title_attr.setIsDataMandatory(true);
        title_attr.setIsMetaData(false);
        title_attr.setDataLength(2000);
        attrs.add(title_attr);

        
        // Populate content attribute definition.
        AttributeDefBean content_attr = new AttributeDefBean();
        content_attr.setName("content");
        content_attr.setType(AttributeTypeEnum.LARGE_TEXT);
        content_attr.setDescription("Comment Content");
        content_attr.setIsDataMandatory(true);
        content_attr.setIsMetaData(false);
        content_attr.setDataLength(2000);
        attrs.add(content_attr);
        
        type.getAttributes().addAll(attrs);

        return bld.put(AssetTypeBean.class, type);
    }
    
    public IndexConfigBean indexAssetType() throws Exception
    {
        // Create Jersey client.
        Client client = Client.create();
        String url = config.getRestUrl() + "/indexes/" + assetTypeName;
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword());
        res = res.queryParam("ticket", SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword()));
        Builder bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Content-Type", MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        
        // Create index configuration bean.
        IndexConfigBean config = new IndexConfigBean();
        config.setName(assetTypeName);
        config.setSearchEngine("lucene");
        config.setDefaultSearchField("defaultSearchField");
        config.setUniqueIdField("id");
        config.setIndexAllFields(false);
        config.getSortableFields().add("name");

        // Create field description for 'name' field.
        IndexFieldDescriptor nameDescr = new IndexFieldDescriptor();
        nameDescr.setName("name");
        nameDescr.setType(IndexFieldTypeEnum.TEXT);
        nameDescr.setTokenized(true);
        nameDescr.setStored(true);
        nameDescr.setBoost(120);
        config.getFieldDescriptors().add(nameDescr);
        

        // Create field description for 'name' field.
        IndexFieldDescriptor ratingDescr = new IndexFieldDescriptor();
        ratingDescr.setName("rating");
        ratingDescr.setType(IndexFieldTypeEnum.NUMERIC);
        ratingDescr.setTokenized(true);
        ratingDescr.setStored(true);
        ratingDescr.setBoost(120);
        config.getFieldDescriptors().add(ratingDescr);
        
        // Create field description for 'title' field.
        IndexFieldDescriptor titleDescr = new IndexFieldDescriptor();
        titleDescr.setName("title");
        titleDescr.setType(IndexFieldTypeEnum.LARGE_TEXT);
        titleDescr.setTokenized(true);
        titleDescr.setStored(true);
        titleDescr.setBoost(100);
        config.getFieldDescriptors().add(titleDescr);
        
        // Create field description for 'name' field.
        IndexFieldDescriptor descrDescr = new IndexFieldDescriptor();
        descrDescr.setName("description");
        descrDescr.setType(IndexFieldTypeEnum.LARGE_TEXT);
        descrDescr.setTokenized(true);
        descrDescr.setStored(true);
        descrDescr.setBoost(100);
        config.getFieldDescriptors().add(descrDescr);
        
        // Create field description for 'state' field.
        IndexFieldDescriptor stateDescr = new IndexFieldDescriptor();
        stateDescr.setName("state");
        stateDescr.setType(IndexFieldTypeEnum.TEXT);
        stateDescr.setTokenized(true);
        stateDescr.setStored(true);
        stateDescr.setBoost(100);
        config.getFieldDescriptors().add(stateDescr);
        
        // Create field description for 'relid' field.
        IndexFieldDescriptor reltypeDescr = new IndexFieldDescriptor();
        reltypeDescr.setName("reltype");
        reltypeDescr.setType(IndexFieldTypeEnum.TEXT);
        reltypeDescr.setTokenized(true);
        reltypeDescr.setStored(true);
        reltypeDescr.setBoost(100);
        config.getFieldDescriptors().add(reltypeDescr);
        
        // Create field description for 'relid' field.
        IndexFieldDescriptor relidDescr = new IndexFieldDescriptor();
        relidDescr.setName("relid");
        relidDescr.setType(IndexFieldTypeEnum.TEXT);
        relidDescr.setTokenized(true);
        relidDescr.setStored(true);
        relidDescr.setBoost(100);
        config.getFieldDescriptors().add(relidDescr);
        
        // Create field description for 'cat' field.
        IndexFieldDescriptor catDescr = new IndexFieldDescriptor();
        catDescr.setName("cat");
        catDescr.setType(IndexFieldTypeEnum.TEXT);
        catDescr.setTokenized(true);
        catDescr.setStored(true);
        catDescr.setBoost(100);
        config.getFieldDescriptors().add(catDescr);
        
        // Create field description for 'source' field.
        IndexFieldDescriptor sourceDescr = new IndexFieldDescriptor();
        sourceDescr.setName("source");
        sourceDescr.setType(IndexFieldTypeEnum.TEXT);
        sourceDescr.setTokenized(true);
        sourceDescr.setStored(true);
        sourceDescr.setBoost(100);
        config.getFieldDescriptors().add(sourceDescr);
        
        // Create field description for 'content' field.
        IndexFieldDescriptor contentDescr = new IndexFieldDescriptor();
        contentDescr.setName("content");
        contentDescr.setType(IndexFieldTypeEnum.LARGE_TEXT);
        contentDescr.setTokenized(true);
        contentDescr.setStored(true);
        contentDescr.setBoost(100);
        config.getFieldDescriptors().add(contentDescr);
        
        // Create index configuration status.
        IndexStatus status = new IndexStatus();
        status.setAssettype(assetTypeName);
        status.setSubtype("");
        status.setIndexStatus(IndexStatusEnum.ENABLED);
        config.getStatusObjects().add(status);
        
        // Create index configuration.
        return bld.put(IndexConfigBean.class, config);
    }
    public SiteBean enableAssetType() throws Exception
    {
        // Create Jersey client.
        Client client = Client.create();
        String url = config.getRestUrl() + "/sites/" + config.getCsSiteName();
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword());
        res = res.queryParam("ticket", SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword()));
        Builder bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        
        // Get site
        SiteBean site = bld.get(SiteBean.class);
        
        // Add the site to the list of enabled sites
        EnabledType type = new EnabledType();
        type.setName(assetTypeName);
        site.getEnabledAssetTypes().getTypes().add(type);

        // Update site
        res = client.resource(url);
        res = res.queryParam("ticket", ticket);
        bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        return bld.post(SiteBean.class, site);
    }
    
    
    public AssetBean createAsset(String name, 
    		String desc, 
    		String cat, 
    		String source, 
    		String title,
    		String filename,
    		String content,
    		String relid,
    		String reltype,
    		STATE state,
    		String rating) throws Exception
    {


    	
        Client client = Client.create();
        String url = config.getRestUrl() + "/sites/" + config.getCsSiteName() + "/types/"+assetTypeName+"/assets/0";
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword());
        res = res.queryParam("ticket", SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword()));
        Builder bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        
                
        AssetBean asset = new AssetBean();
        asset.setId(this.assetTypeName + ":0");
        asset.setName(name);
        asset.setDescription(desc);

        Attribute rating_attr = new Attribute();
        Data rating_data = new Data();
        rating_data.setStringValue(rating);
        rating_attr.setData(rating_data);
        rating_attr.setName("rating");
        asset.getAttributes().add(rating_attr);

        Attribute state_attr = new Attribute();
        Data state_data = new Data();
        state_data.setStringValue(state.toString());
        state_attr.setData(state_data);
        state_attr.setName("state");
        asset.getAttributes().add(state_attr);

        Attribute reltype_attr = new Attribute();
        Data reltype_data = new Data();
        reltype_data.setStringValue(reltype);
        reltype_attr.setData(reltype_data);
        reltype_attr.setName("reltype");
        asset.getAttributes().add(reltype_attr);

        Attribute relid_attr = new Attribute();
        Data relid_data = new Data();
        relid_data.setStringValue(relid);
        relid_attr.setData(relid_data);
        relid_attr.setName("relid");
        asset.getAttributes().add(relid_attr);

        Attribute title_attr = new Attribute();
        Data title_data = new Data();
        title_data.setStringValue(title);
        title_attr.setData(title_data);
        title_attr.setName("title");
        asset.getAttributes().add(title_attr);

        Attribute cat_attr = new Attribute();
        Data cat_data = new Data();
        cat_data.setStringValue(cat);
        cat_attr.setData(cat_data);
        cat_attr.setName("cat");
        asset.getAttributes().add(cat_attr);

        Attribute source_attr = new Attribute();
        Data source_data = new Data();
        source_data.setStringValue(source);
        source_attr.setData(source_data);
        source_attr.setName("source");
        asset.getAttributes().add(source_attr);

        
        // read content from file ... DEMO CONTENT
        if(filename != null){
            ClassLoader cl = InstallCommentAssetTypeService.class.getClassLoader();
            Reader txt = new InputStreamReader(cl.getResourceAsStream("install/" + filename + ".txt"), "UTF-8");
            int numread = 0;
            char[] cbuff = new char[1024];
            while(-1 != (numread = txt.read(cbuff))){
                content += String.valueOf(cbuff, 0, numread);
            }
        }else{
        	// assume that content is not null ;)
        }

        Attribute content_attr = new Attribute();
        Data content_data = new Data();
        content_data.setStringValue(content);
        content_attr.setData(content_data);
        content_attr.setName("content");
        asset.getAttributes().add(content_attr);

        

        asset.getPublists().addAll(Collections.singletonList(config.getCsSiteName()));

        return bld.put(AssetBean.class, asset);
    }
    
}
