package org.apache.commons.chain.web;

import java.util.Map;
import org.apache.commons.chain.impl.ContextBase;

public abstract class WebContext extends ContextBase
{
    public abstract Map getApplicationScope();
    
    public abstract Map getHeader();
    
    public abstract Map getHeaderValues();
    
    public abstract Map getInitParam();
    
    public abstract Map getParam();
    
    public abstract Map getParamValues();
    
    public abstract Map getCookies();
    
    public abstract Map getRequestScope();
    
    public abstract Map getSessionScope();
}
