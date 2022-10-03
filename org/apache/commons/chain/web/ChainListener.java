package org.apache.commons.chain.web;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.net.URL;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.Catalog;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletContext;
import org.apache.commons.chain.CatalogFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ChainListener implements ServletContextListener
{
    public static final String CONFIG_ATTR = "org.apache.commons.chain.CONFIG_ATTR";
    public static final String CONFIG_CLASS_RESOURCE = "org.apache.commons.chain.CONFIG_CLASS_RESOURCE";
    public static final String CONFIG_WEB_RESOURCE = "org.apache.commons.chain.CONFIG_WEB_RESOURCE";
    public static final String RULE_SET = "org.apache.commons.chain.RULE_SET";
    
    public void contextDestroyed(final ServletContextEvent event) {
        final ServletContext context = event.getServletContext();
        final String attr = context.getInitParameter("org.apache.commons.chain.CONFIG_ATTR");
        if (attr != null) {
            context.removeAttribute(attr);
        }
        CatalogFactory.clear();
    }
    
    public void contextInitialized(final ServletContextEvent event) {
        final Log log = LogFactory.getLog(ChainListener.class);
        if (log.isInfoEnabled()) {
            log.info((Object)"Initializing chain listener");
        }
        final ServletContext context = event.getServletContext();
        final String attr = context.getInitParameter("org.apache.commons.chain.CONFIG_ATTR");
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
                throw new RuntimeException("Exception initalizing RuleSet '" + ruleSet + "' instance: " + e.getMessage());
            }
        }
        if (attr == null) {
            this.parseJarResources(context, parser, log);
            ChainResources.parseClassResources(classResources, parser);
            ChainResources.parseWebResources(context, webResources, parser);
        }
        else {
            this.parseJarResources(catalog, context, parser, log);
            ChainResources.parseClassResources(catalog, classResources, parser);
            ChainResources.parseWebResources(catalog, context, webResources, parser);
        }
        if (attr != null) {
            context.setAttribute(attr, (Object)catalog);
        }
    }
    
    private void parseJarResources(final ServletContext context, final ConfigParser parser, final Log log) {
        Set jars = context.getResourcePaths("/WEB-INF/lib");
        if (jars == null) {
            jars = new HashSet();
        }
        String path = null;
        final Iterator paths = jars.iterator();
        while (paths.hasNext()) {
            path = paths.next();
            if (!path.endsWith(".jar")) {
                continue;
            }
            URL resourceURL = null;
            try {
                final URL jarURL = context.getResource(path);
                resourceURL = new URL("jar:" + this.translate(jarURL.toExternalForm()) + "!/META-INF/chain-config.xml");
                if (resourceURL == null) {
                    continue;
                }
                InputStream is = null;
                try {
                    is = resourceURL.openStream();
                }
                catch (final Exception ex) {}
                if (is == null) {
                    if (!log.isDebugEnabled()) {
                        continue;
                    }
                    log.debug((Object)("Not Found: " + resourceURL));
                }
                else {
                    is.close();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Parsing: " + resourceURL));
                    }
                    parser.parse(resourceURL);
                }
            }
            catch (final Exception e) {
                throw new RuntimeException("Exception parsing chain config resource '" + resourceURL.toExternalForm() + "': " + e.getMessage());
            }
        }
    }
    
    private void parseJarResources(final Catalog catalog, final ServletContext context, final ConfigParser parser, final Log log) {
        Set jars = context.getResourcePaths("/WEB-INF/lib");
        if (jars == null) {
            jars = new HashSet();
        }
        String path = null;
        final Iterator paths = jars.iterator();
        while (paths.hasNext()) {
            path = paths.next();
            if (!path.endsWith(".jar")) {
                continue;
            }
            URL resourceURL = null;
            try {
                final URL jarURL = context.getResource(path);
                resourceURL = new URL("jar:" + this.translate(jarURL.toExternalForm()) + "!/META-INF/chain-config.xml");
                if (resourceURL == null) {
                    continue;
                }
                InputStream is = null;
                try {
                    is = resourceURL.openStream();
                }
                catch (final Exception ex) {}
                if (is == null) {
                    if (!log.isDebugEnabled()) {
                        continue;
                    }
                    log.debug((Object)("Not Found: " + resourceURL));
                }
                else {
                    is.close();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Parsing: " + resourceURL));
                    }
                    parser.parse(catalog, resourceURL);
                }
            }
            catch (final Exception e) {
                throw new RuntimeException("Exception parsing chain config resource '" + resourceURL.toExternalForm() + "': " + e.getMessage());
            }
        }
    }
    
    private String translate(String value) {
        while (true) {
            final int index = value.indexOf(32);
            if (index < 0) {
                break;
            }
            value = value.substring(0, index) + "%20" + value.substring(index + 1);
        }
        return value;
    }
}
