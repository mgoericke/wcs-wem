package de.javamark.wcs.wem.service;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fatwire.wem.sso.SSO;
import com.fatwire.wem.sso.SSOException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.javamark.wcs.wem.WemConfig;

public abstract class AbstractService {
	Logger log = Logger.getLogger("de.javamark.wcs.wem.service");

	@Autowired
	WemConfig config;
	
	Builder getBuilder(String uri) throws SSOException{
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

}
