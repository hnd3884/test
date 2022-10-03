package com.me.mdm.core.windows.xmlbeans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UPN")
class EnrollmentUserPrincipalName
{
    private String userPrincipalName;
    private String name;
    private String discoveryServiceUrl;
    private String authPolicy;
    private String authSecret;
    
    public String getUserPrincipalName() {
        return this.userPrincipalName;
    }
    
    @XmlAttribute(name = "UPN")
    public void setUserPrincipalName(final String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }
    
    public String getName() {
        return this.name;
    }
    
    @XmlAttribute(name = "Name")
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDiscoveryServiceUrl() {
        return this.discoveryServiceUrl;
    }
    
    @XmlElement(name = "DiscoveryServiceFullUrl")
    public void setDiscoveryServiceUrl(final String discoveryServiceUrl) {
        this.discoveryServiceUrl = discoveryServiceUrl;
    }
    
    public String getAuthPolicy() {
        return this.authPolicy;
    }
    
    @XmlElement(name = "AuthPolicy")
    public void setAuthPolicy(final String authPolicy) {
        this.authPolicy = authPolicy;
    }
    
    public String getAuthSecret() {
        return this.authSecret;
    }
    
    @XmlElement(name = "Secret")
    public void setAuthSecret(final String authSecret) {
        this.authSecret = authSecret;
    }
}
