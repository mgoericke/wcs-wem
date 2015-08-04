package de.javamark.wcs.wem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatwire.rest.beans.AssetBean;
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

@Service
public class InstallBlogPostAssetTypeService {

	Logger log = Logger.getLogger("de.javamark.wcs.wem.service");
	
	
	private String assetTypeName		= "FW_BlogPost";
	private String assetTypeDescription = "Blog Posts";
	
	@Autowired
	WemConfig config;
	
	public enum STATE {
		WAITING,
		APPROVED,
		REJECTED
	}
	
	
	
	public void installDummyContents(){
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
		String[] tags = {
				"Produkte", 
				"Einkauf", 
				"Empfehlung", 
				"Aussehen", 
				"Empfehlung", 
				"Empfehlung", 
				"Empfehlung", 
				"Lieferung", 
				"Produkte", 
				"Empfehlung", 
				"Empfehlung", 
				"Produkte", 
				"Lieferung", 
				"Produkte", 
				"Empfehlung", 
				"Lieferung", 
				"Produkte"
		};

		// create comments on products
		int cnt = 0;
		for(int i=0; i<titles.length; i++){

			// "random" states for comments ...
			STATE state = (i%2 == 0)?STATE.APPROVED:STATE.WAITING;
			
			
			try {
				createAsset(
						titles[i], 
						titles[i], 
						"WEM Blog", 
						config.getApplicationName(), 
						titles[i], 
						"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
						state, 
						tags[i]);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			cnt++;
			// add a comment to each 3rd product
			try{
				
				if(i%3 == 0){
					state = (i%2 == 0)?STATE.APPROVED:STATE.WAITING;
					createAsset(
							titles[i], 
							titles[i], 
							"WEM Blog", 
							config.getApplicationName(), 
							titles[i], 
							"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", 
							state, 
							tags[i]);
					cnt++;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private Builder getBuilder(String uri) throws SSOException{
        // Create Jersey client.
        Client client = Client.create();
        String url = config.getRestUrl() + uri;
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, config.getCsUsername(), config.getCsPassword());
        res = res.queryParam("ticket", ticket);
        Builder bld = res.accept(MediaType.APPLICATION_XML);     
        bld = bld.header("Pragma", "auth-redirect=false");
        //Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        
        return bld;
	}

	/* (non-Javadoc)
	 * @see de.javamark.wem.service.FWAssetTypeService#checkType()
	 */
    public AssetTypeBean getAssetType() throws Exception{

        try{
            // Test type for existence.            
            return getBuilder("/types/" + assetTypeName).get(AssetTypeBean.class);
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
    	
        // Create asset type object.
        AssetTypeBean type = new AssetTypeBean();
        type.setName(assetTypeName);
        type.setDescription(assetTypeDescription);
        type.setCanBeChild(false);
        
        // attributes
        List<AttributeDefBean> attrs = new ArrayList<AttributeDefBean>();        

        
        // Populate state attribute definition.
        AttributeDefBean state_attr = new AttributeDefBean();
        state_attr.setName("state");
        state_attr.setType(AttributeTypeEnum.STRING);
        state_attr.setDescription("Approval State");
        state_attr.setIsDataMandatory(true);
        state_attr.setIsMetaData(false);
        state_attr.setDataLength(256);
        attrs.add(state_attr);

        
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
        source_attr.setDescription("Source");
        source_attr.setIsDataMandatory(true);
        source_attr.setIsMetaData(false);
        source_attr.setDataLength(256);
        attrs.add(source_attr);
        
        
        // Populate title attribute definition.
        AttributeDefBean title_attr = new AttributeDefBean();
        title_attr.setName("title");
        title_attr.setType(AttributeTypeEnum.LARGE_TEXT);
        title_attr.setDescription("Title");
        title_attr.setIsDataMandatory(true);
        title_attr.setIsMetaData(false);
        title_attr.setDataLength(2000);
        attrs.add(title_attr);

        
        // Populate tags attribute definition.
        AttributeDefBean tags_attr = new AttributeDefBean();
        tags_attr.setName("tags");
        tags_attr.setType(AttributeTypeEnum.STRING);
        tags_attr.setDescription("Tags");
        tags_attr.setIsDataMandatory(true);
        tags_attr.setIsMetaData(false);
        tags_attr.setDataLength(2000);
        attrs.add(tags_attr);

        
        // Populate content attribute definition.
        AttributeDefBean content_attr = new AttributeDefBean();
        content_attr.setName("content");
        content_attr.setType(AttributeTypeEnum.LARGE_TEXT);
        content_attr.setDescription("Blog Content");
        content_attr.setIsDataMandatory(true);
        content_attr.setIsMetaData(false);
        content_attr.setDataLength(2000);
        attrs.add(content_attr);
        
        type.getAttributes().addAll(attrs);

        return getBuilder("/types/" + assetTypeName).put(AssetTypeBean.class, type);
    }
    
    public IndexConfigBean indexAssetType() throws Exception
    {
        
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
        
        // Create field description for 'title' field.
        IndexFieldDescriptor titleDescr = new IndexFieldDescriptor();
        titleDescr.setName("title");
        titleDescr.setType(IndexFieldTypeEnum.LARGE_TEXT);
        titleDescr.setTokenized(true);
        titleDescr.setStored(true);
        titleDescr.setBoost(100);
        config.getFieldDescriptors().add(titleDescr);
        
        // Create field description for 'state' field.
        IndexFieldDescriptor stateDescr = new IndexFieldDescriptor();
        stateDescr.setName("state");
        stateDescr.setType(IndexFieldTypeEnum.TEXT);
        stateDescr.setTokenized(true);
        stateDescr.setStored(true);
        stateDescr.setBoost(100);
        config.getFieldDescriptors().add(stateDescr);

        
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
        
        // Create field description for 'tags' field.
        IndexFieldDescriptor tagsDescr = new IndexFieldDescriptor();
        tagsDescr.setName("tags");
        tagsDescr.setType(IndexFieldTypeEnum.LARGE_TEXT);
        tagsDescr.setTokenized(true);
        tagsDescr.setStored(true);
        tagsDescr.setBoost(100);
        config.getFieldDescriptors().add(tagsDescr);
        
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
        return getBuilder("/indexes/" + assetTypeName).put(IndexConfigBean.class, config);
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
    		String content,
    		STATE state,
    		String tags) throws Exception
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
        
//    	System.out.println("name: " + name);
//    	System.out.println("desc: " + desc);
//    	System.out.println("cat: " + cat);
//    	System.out.println("source: " + source);
//    	System.out.println("title: " + title);
//    	System.out.println("state: " + state);
//    	System.out.println("tags: " + tags);
                
    	// name
        AssetBean asset = new AssetBean();
        asset.setId(this.assetTypeName + ":0");
        asset.setName(name);
        asset.setDescription(desc);

        // state
        Attribute state_attr = new Attribute();
        Data state_data = new Data();
        state_data.setStringValue(state.toString());
        state_attr.setData(state_data);
        state_attr.setName("state");
        asset.getAttributes().add(state_attr);

     
        // tags
        Attribute tags_attr = new Attribute();
        Data tags_data = new Data();
        tags_data.setStringValue(tags);
        tags_attr.setData(tags_data);
        tags_attr.setName("tags");
        asset.getAttributes().add(tags_attr);

        // cat
        Attribute cat_attr = new Attribute();
        Data cat_data = new Data();
        cat_data.setStringValue(cat);
        cat_attr.setData(cat_data);
        cat_attr.setName("cat");
        asset.getAttributes().add(cat_attr);

        // source
        Attribute source_attr = new Attribute();
        Data source_data = new Data();
        source_data.setStringValue(source);
        source_attr.setData(source_data);
        source_attr.setName("source");
        asset.getAttributes().add(source_attr);

        // title
        Attribute title_attr = new Attribute();
        Data title_data = new Data();
        title_data.setStringValue(title);
        title_attr.setData(title_data);
        title_attr.setName("title");
        asset.getAttributes().add(title_attr);

        // content
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
