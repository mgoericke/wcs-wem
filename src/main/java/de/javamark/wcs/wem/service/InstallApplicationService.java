package de.javamark.wcs.wem.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatwire.rest.beans.Application;
import com.fatwire.rest.beans.ApplicationBean;
import com.fatwire.rest.beans.ApplicationsBean;
import com.fatwire.rest.beans.LayoutTypeEnum;
import com.fatwire.rest.beans.SiteRoles;
import com.fatwire.rest.beans.View;
import com.fatwire.rest.beans.ViewTypeEnum;
import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.javamark.wcs.wem.WemConfig;

@Service
public class InstallApplicationService {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.service");
	
	@Autowired
	WemConfig wemConfig;

	/**
	 * return an Application object
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SSOException
	 */
	public Application getApplication() throws UnsupportedEncodingException, SSOException{
		
        // Use Jersey client to query CS assets.
        Client client = Client.create();
        String url = wemConfig.getRestUrl() + "/applications";
        WebResource res = client.resource( url );
                
        // Construct URL and add token (for authentication purposes)
        // and fields (specify which fields to retrieve back) parameters.        
        res = res.queryParam("fields", URLEncoder.encode("name,description,content,cat,source", "UTF-8"));
        res = res.queryParam("ticket", SSO.getSSOSession().getTicket(res.getURI().toString(), wemConfig.getCsUsername(), wemConfig.getCsPassword()));
        // Put Pragma: auth-redirect=false to avoid redirects to the CAS login page.
        Builder bld = res.header("Pragma", "auth-redirect=false");
        
        // because we don't know the id of the application we have to try to find the application by its name
        ApplicationsBean appsBean = (bld.get(ApplicationsBean.class));
        for(Application app : appsBean.getApplications()){
        	if(app.getName().equalsIgnoreCase(wemConfig.getApplicationName())){
        		return app;
        	}
        }
        
		return null;
	}
	
	/**
	 * will create an application based on 
	 * @return
	 * @throws Exception
	 */
	public Application createApplication() throws Exception{
		
		if(getApplication() != null){
			return getApplication();
		}
		
        // Create Jersey client.
        Client client = Client.create();
        
        String url = wemConfig.getRestUrl() + "/applications/0";
        WebResource res = client.resource(url);
        String ticket = SSO.getSSOSession().getTicket(url, wemConfig.getCsUsername(), wemConfig.getCsPassword());
        res = res.queryParam("ticket", ticket);
        Builder bld = res.accept(MediaType.APPLICATION_XML);
        bld = bld.header("Pragma", "auth-redirect=false");
		//Add the CSRF header
        bld = bld.header("X-CSRF-Token", ticket);
        
        // Create a new view object.
        View view = new View();
        view.setName(wemConfig.getViewName());
        view.setDescription(wemConfig.getViewDescription());
        view.setParentnode("frame1");
        view.setSourceurl(wemConfig.getViewSourceUrl());
        view.setViewtype(ViewTypeEnum.FW_WEM_FRAMEWORK_IFRAME_RENDERER);
        
        // Create a new application object.
        ApplicationBean app = new ApplicationBean();
        app.setName(wemConfig.getApplicationName());
        app.setShortdescription(wemConfig.getApplicationDescription());
        app.setDescription(wemConfig.getApplicationDescription());
        app.setTooltip(wemConfig.getApplicationDescription());     
        
        // icons
        app.setIconurl(wemConfig.getIconUrl());
        app.setIconurlactive(wemConfig.getIconUrlActive());
        
        // layout
        app.setLayouttype(LayoutTypeEnum.FW_WEM_FRAMEWORK_LAYOUT_RENDERER);
        app.setLayouturl(wemConfig.getLayoutUrl());
        
        app.setParentid(0L);
        app.setSystemapplication(true);   
        app.getViews().add(view);
        
        // Set site assignment.
        SiteRoles role = new SiteRoles();
        role.setSite(wemConfig.getCsSiteName());
        role.getRoles().add("SiteAdmin");
        role.getRoles().add("GeneralAdmin");
        app.getSites().add(role);
        
        // Call REST service to create an application.
        bld.put(ApplicationBean.class, app);
        
        return getApplication();
    }
	
	
}
