package org.apache.commons.chain.web.servlet;

import org.apache.commons.chain.Catalog;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.generic.LookupCommand;

public class PathInfoMapper extends LookupCommand implements Command
{
    private String catalogKey;
    
    public PathInfoMapper() {
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
        String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
        if (pathInfo == null) {
            pathInfo = request.getPathInfo();
        }
        return pathInfo;
    }
    
    protected Catalog getCatalog(final Context context) {
        Catalog catalog = context.get(this.getCatalogKey());
        if (catalog == null) {
            catalog = super.getCatalog(context);
        }
        return catalog;
    }
}
