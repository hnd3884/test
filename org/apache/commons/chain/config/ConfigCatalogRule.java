package org.apache.commons.chain.config;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;

class ConfigCatalogRule extends Rule
{
    private String catalogClass;
    private String nameAttribute;
    
    public ConfigCatalogRule(final String nameAttribute, final String catalogClass) {
        this.catalogClass = null;
        this.nameAttribute = null;
        this.nameAttribute = nameAttribute;
        this.catalogClass = catalogClass;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        Catalog catalog = null;
        final CatalogFactory factory = CatalogFactory.getInstance();
        final String nameValue = attributes.getValue(this.nameAttribute);
        if (nameValue == null) {
            catalog = factory.getCatalog();
        }
        else {
            catalog = factory.getCatalog(nameValue);
        }
        if (catalog == null) {
            final Class clazz = this.digester.getClassLoader().loadClass(this.catalogClass);
            catalog = clazz.newInstance();
            if (nameValue == null) {
                factory.setCatalog(catalog);
            }
            else {
                factory.addCatalog(nameValue, catalog);
            }
        }
        this.digester.push((Object)catalog);
    }
}
