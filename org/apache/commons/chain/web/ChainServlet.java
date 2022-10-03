package org.apache.commons.chain.web;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import javax.servlet.ServletException;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.Catalog;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import org.apache.commons.chain.CatalogFactory;
import javax.servlet.http.HttpServlet;

public class ChainServlet extends HttpServlet
{
    public static final String CONFIG_ATTR = "org.apache.commons.chain.CONFIG_ATTR";
    public static final String CONFIG_CLASS_RESOURCE = "org.apache.commons.chain.CONFIG_CLASS_RESOURCE";
    public static final String CONFIG_WEB_RESOURCE = "org.apache.commons.chain.CONFIG_WEB_RESOURCE";
    public static final String RULE_SET = "org.apache.commons.chain.RULE_SET";
    
    public void destroy() {
        final ServletConfig config = this.getServletConfig();
        final ServletContext context = this.getServletContext();
        final String attr = config.getInitParameter("org.apache.commons.chain.CONFIG_ATTR");
        if (attr != null) {
            context.removeAttribute(attr);
        }
        CatalogFactory.clear();
    }
    
    public void init() throws ServletException {
        final Log log = LogFactory.getLog(ChainServlet.class);
        final ServletConfig config = this.getServletConfig();
        final ServletContext context = this.getServletContext();
        if (log.isInfoEnabled()) {
            log.info((Object)("Initializing chain servlet '" + config.getServletName() + "'"));
        }
        final String attr = config.getInitParameter("org.apache.commons.chain.CONFIG_ATTR");
        final String classResources = context.getInitParameter("org.apache.commons.chain.CONFIG_CLASS_RESOURCE");
        final String ruleSet = context.getInitParameter("org.apache.commons.chain.RULE_SET");
        final String webResources = context.getInitParameter("org.apache.commons.chain.CONFIG_WEB_RESOURCE");
        Catalog catalog = null;
        if (attr != null) {
            catalog = (Catalog)context.getAttribute(attr);
            if (catalog == null) {
                catalog = new CatalogBase();
            }
        }
        final ConfigParser parser = new ConfigParser();
        if (ruleSet != null) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null) {
                    loader = this.getClass().getClassLoader();
                }
                final Class clazz = loader.loadClass(ruleSet);
                parser.setRuleSet(clazz.newInstance());
            }
            catch (final Exception e) {
                throw new ServletException("Exception initalizing RuleSet '" + ruleSet + "' instance", (Throwable)e);
            }
        }
        if (attr == null) {
            ChainResources.parseClassResources(classResources, parser);
            ChainResources.parseWebResources(context, webResources, parser);
        }
        else {
            ChainResources.parseClassResources(catalog, classResources, parser);
            ChainResources.parseWebResources(catalog, context, webResources, parser);
        }
        if (attr != null) {
            context.setAttribute(attr, (Object)catalog);
        }
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    }
}
