package org.apache.commons.chain.web.servlet;

import org.apache.commons.chain.Catalog;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.generic.LookupCommand;

public class ServletPathMapper extends LookupCommand implements Command
{
    private String catalogKey;
    
    public ServletPathMapper() {
        this.catalogKey = "org.apache.commons.chain.CATALOG";
    }
    
    public String getCatalogKey() {
        return this.catalogKey;
    }
    
    public void setCatalogKey(final String catalogKey) {
        this.catalogKey = catalogKey;
    }
    
    protected String getCommandName(final Context context) {
        final ServletWebContext swcontext = (ServletWebContext)context;
        final HttpServletRequest request = swcontext.getRequest();
        String servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        return servletPath;
    }
    
    protected Catalog getCatalog(final Context context) {
        Catalog catalog = context.get(this.getCatalogKey());
        if (catalog == null) {
            catalog = super.getCatalog(context);
        }
        return catalog;
    }
}
