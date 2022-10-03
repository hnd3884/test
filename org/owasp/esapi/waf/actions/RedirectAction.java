package org.owasp.esapi.waf.actions;

public class RedirectAction extends Action
{
    private String url;
    
    public RedirectAction() {
        this.url = null;
    }
    
    public void setRedirectURL(final String s) {
        this.url = s;
    }
    
    public String getRedirectURL() {
        return this.url;
    }
}
