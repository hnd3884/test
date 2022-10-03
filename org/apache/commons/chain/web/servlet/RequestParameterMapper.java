package org.apache.commons.chain.web.servlet;

import org.apache.commons.chain.Catalog;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.generic.LookupCommand;

public class RequestParameterMapper extends LookupCommand implements Command
{
    private String catalogKey;
    private String parameter;
    
    public RequestParameterMapper() {
        this.catalogKey = "org.apache.commons.chain.CATALOG";
        this.parameter = "command";
    }
    
    public String getCatalogKey() {
        return this.catalogKey;
    }
    
    public void setCatalogKey(final String catalogKey) {
        this.catalogKey = catalogKey;
    }
    
    public String getParameter() {
        return this.parameter;
    }
    
    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }
    
    protected String getCommandName(final Context context) {
        final ServletWebContext swcontext = (ServletWebContext)context;
        final HttpServletRequest request = swcontext.getRequest();
        final String value = request.getParameter(this.getParameter());
        return value;
    }
    
    protected Catalog getCatalog(final Context context) {
        Catalog catalog = context.get(this.getCatalogKey());
        if (catalog == null) {
            catalog = super.getCatalog(context);
        }
        return catalog;
    }
}
