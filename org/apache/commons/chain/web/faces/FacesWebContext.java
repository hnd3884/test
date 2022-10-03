package org.apache.commons.chain.web.faces;

import java.util.Map;
import javax.faces.context.FacesContext;
import org.apache.commons.chain.web.WebContext;

public class FacesWebContext extends WebContext
{
    private FacesContext context;
    
    public FacesWebContext() {
        this.context = null;
    }
    
    public FacesWebContext(final FacesContext context) {
        this.context = null;
        this.initialize(context);
    }
    
    public FacesContext getContext() {
        return this.context;
    }
    
    public void initialize(final FacesContext context) {
        this.context = context;
    }
    
    public void release() {
        this.context = null;
    }
    
    public Map getApplicationScope() {
        return this.context.getExternalContext().getApplicationMap();
    }
    
    public Map getHeader() {
        return this.context.getExternalContext().getRequestHeaderMap();
    }
    
    public Map getHeaderValues() {
        return this.context.getExternalContext().getRequestHeaderValuesMap();
    }
    
    public Map getInitParam() {
        return this.context.getExternalContext().getInitParameterMap();
    }
    
    public Map getParam() {
        return this.context.getExternalContext().getRequestParameterMap();
    }
    
    public Map getParamValues() {
        return this.context.getExternalContext().getRequestParameterValuesMap();
    }
    
    public Map getCookies() {
        return this.context.getExternalContext().getRequestCookieMap();
    }
    
    public Map getRequestScope() {
        return this.context.getExternalContext().getRequestMap();
    }
    
    public Map getSessionScope() {
        return this.context.getExternalContext().getSessionMap();
    }
}
