package de.javamark.wcs.wem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WemConfig {
	
	@Value("${cs.SiteName}")
    private String csSiteName;

	@Value("${cs.Url}")
    private String csUrl;

	@Value("${cs.Username}")
    private String csUsername;

	@Value("${cs.Password}")
    private String csPassword;

	public String getCsSiteName() {
		return csSiteName;
	}
	
	
	@Value("${wem.contextPathUrl}")
    private String contextPathUrl;

	@Value("${wem.iconUrl}")
    private String iconUrl;

	@Value("${wem.iconUrlActive}")
    private String iconUrlActive;

	@Value("${wem.layoutUrl}")
    private String layoutUrl;

	@Value("${wem.viewName}")
    private String viewName;

	@Value("${wem.viewDescription}")
    private String viewDescription;

	@Value("${wem.viewSourceUrl}")
    private String viewSourceUrl;


	@Value("${wem.applicationName}")
    private String applicationName;

	@Value("${wem.applicationDescription}")
    private String applicationDescription;

	@Value("${wem.applicationShortDescription}")
    private String applicationShortDescription;

	@Value("${wem.applicationToolTip}")
    private String applicationToolTip;

	public void setCsSiteName(String csSiteName) {
		this.csSiteName = csSiteName;
	}

	public String getCsUrl() {
		return csUrl;
	}

	public void setCsUrl(String csUrl) {
		this.csUrl = csUrl;
	}

	public String getCsUsername() {
		return csUsername;
	}

	public void setCsUsername(String csUsername) {
		this.csUsername = csUsername;
	}

	public String getCsPassword() {
		return csPassword;
	}

	public void setCsPassword(String csPassword) {
		this.csPassword = csPassword;
	}
	

	public String getContextPathUrl() {
		return contextPathUrl;
	}

	public void setContextPathUrl(String contextPathUrl) {
		this.contextPathUrl = contextPathUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconUrlActive() {
		return iconUrlActive;
	}

	public void setIconUrlActive(String iconUrlActive) {
		this.iconUrlActive = iconUrlActive;
	}

	public String getLayoutUrl() {
		return layoutUrl;
	}

	public void setLayoutUrl(String layoutUrl) {
		this.layoutUrl = layoutUrl;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewDescription() {
		return viewDescription;
	}

	public void setViewDescription(String viewDescription) {
		this.viewDescription = viewDescription;
	}

	public String getViewSourceUrl() {
		return viewSourceUrl;
	}

	public void setViewSourceUrl(String viewSourceUrl) {
		this.viewSourceUrl = viewSourceUrl;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	public String getApplicationShortDescription() {
		return applicationShortDescription;
	}

	public void setApplicationShortDescription(String applicationShortDescription) {
		this.applicationShortDescription = applicationShortDescription;
	}

	public String getApplicationToolTip() {
		return applicationToolTip;
	}

	public void setApplicationToolTip(String applicationToolTip) {
		this.applicationToolTip = applicationToolTip;
	}

	/**
     * Retrieved the URL, which is used to invoke Content Server
     * REST Web services, e.g. http://localhost:8080/cs/REST
     * @return
     */
    public String getRestUrl()
    {
        return csUrl + "/REST";
    }
	
}
