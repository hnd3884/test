package org.apache.commons.chain.web.servlet;

import java.io.IOException;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Catalog;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import org.apache.commons.chain.web.ChainServlet;

public class ChainProcessor extends ChainServlet
{
    public static final String CATALOG = "org.apache.commons.chain.CATALOG";
    public static final String CATALOG_DEFAULT = "org.apache.commons.chain.CATALOG";
    public static final String COMMAND = "org.apache.commons.chain.COMMAND";
    private static final String COMMAND_DEFAULT = "command";
    private String attribute;
    private String catalog;
    private String command;
    
    public ChainProcessor() {
        this.attribute = null;
        this.catalog = null;
        this.command = null;
    }
    
    public void destroy() {
        super.destroy();
        this.attribute = null;
        this.catalog = null;
        this.command = null;
    }
    
    public void init() throws ServletException {
        super.init();
        this.attribute = this.getServletConfig().getInitParameter("org.apache.commons.chain.CONFIG_ATTR");
        this.catalog = this.getServletConfig().getInitParameter("org.apache.commons.chain.CATALOG");
        this.command = this.getServletConfig().getInitParameter("org.apache.commons.chain.COMMAND");
        if (this.command == null) {
            this.command = "command";
        }
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final ServletWebContext context = new ServletWebContext(this.getServletContext(), request, response);
        Catalog theCatalog = null;
        if (this.attribute != null) {
            theCatalog = (Catalog)this.getServletContext().getAttribute(this.attribute);
        }
        else if (this.catalog != null) {
            theCatalog = CatalogFactory.getInstance().getCatalog(this.catalog);
        }
        else {
            theCatalog = CatalogFactory.getInstance().getCatalog();
        }
        if (this.attribute == null) {
            request.setAttribute("org.apache.commons.chain.CATALOG", (Object)theCatalog);
        }
        final Command command = theCatalog.getCommand(this.command);
        try {
            command.execute(context);
        }
        catch (final Exception e) {
            throw new ServletException((Throwable)e);
        }
    }
}
