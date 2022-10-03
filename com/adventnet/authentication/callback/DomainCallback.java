package com.adventnet.authentication.callback;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class DomainCallback implements Callback, Serializable
{
    private String domainName;
    
    public DomainCallback() {
        this.domainName = null;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
}
