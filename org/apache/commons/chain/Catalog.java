package org.apache.commons.chain;

import java.util.Iterator;

public interface Catalog
{
    public static final String CATALOG_KEY = "org.apache.commons.chain.CATALOG";
    
    void addCommand(final String p0, final Command p1);
    
    Command getCommand(final String p0);
    
    Iterator getNames();
}
