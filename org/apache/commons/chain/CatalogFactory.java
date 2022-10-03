package org.apache.commons.chain;

import java.util.HashMap;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.Map;

public abstract class CatalogFactory
{
    public static final String DELIMITER = ":";
    private static Map factories;
    
    public abstract Catalog getCatalog();
    
    public abstract void setCatalog(final Catalog p0);
    
    public abstract Catalog getCatalog(final String p0);
    
    public abstract void addCatalog(final String p0, final Catalog p1);
    
    public abstract Iterator getNames();
    
    public Command getCommand(final String commandID) {
        String commandName = commandID;
        String catalogName = null;
        Catalog catalog = null;
        if (commandID != null) {
            final int splitPos = commandID.indexOf(":");
            if (splitPos != -1) {
                catalogName = commandID.substring(0, splitPos);
                commandName = commandID.substring(splitPos + ":".length());
                if (commandName.indexOf(":") != -1) {
                    throw new IllegalArgumentException("commandID [" + commandID + "] has too many delimiters (reserved for future use)");
                }
            }
        }
        if (catalogName != null) {
            catalog = this.getCatalog(catalogName);
            if (catalog == null) {
                final Log log = LogFactory.getLog(CatalogFactory.class);
                log.warn((Object)("No catalog found for name: " + catalogName + "."));
                return null;
            }
        }
        else {
            catalog = this.getCatalog();
            if (catalog == null) {
                final Log log = LogFactory.getLog(CatalogFactory.class);
                log.warn((Object)"No default catalog found.");
                return null;
            }
        }
        return catalog.getCommand(commandName);
    }
    
    public static CatalogFactory getInstance() {
        CatalogFactory factory = null;
        final ClassLoader cl = getClassLoader();
        synchronized (CatalogFactory.factories) {
            factory = CatalogFactory.factories.get(cl);
            if (factory == null) {
                factory = new CatalogFactoryBase();
                CatalogFactory.factories.put(cl, factory);
            }
        }
        return factory;
    }
    
    public static void clear() {
        synchronized (CatalogFactory.factories) {
            CatalogFactory.factories.remove(getClassLoader());
        }
    }
    
    private static ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = CatalogFactory.class.getClassLoader();
        }
        return cl;
    }
    
    static {
        CatalogFactory.factories = new HashMap();
    }
}
