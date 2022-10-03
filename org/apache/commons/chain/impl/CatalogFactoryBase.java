package org.apache.commons.chain.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;

public class CatalogFactoryBase extends CatalogFactory
{
    private Catalog catalog;
    private Map catalogs;
    
    public CatalogFactoryBase() {
        this.catalog = null;
        this.catalogs = new HashMap();
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    public void setCatalog(final Catalog catalog) {
        this.catalog = catalog;
    }
    
    public Catalog getCatalog(final String name) {
        synchronized (this.catalogs) {
            return this.catalogs.get(name);
        }
    }
    
    public void addCatalog(final String name, final Catalog catalog) {
        synchronized (this.catalogs) {
            this.catalogs.put(name, catalog);
        }
    }
    
    public Iterator getNames() {
        synchronized (this.catalogs) {
            return this.catalogs.keySet().iterator();
        }
    }
}
