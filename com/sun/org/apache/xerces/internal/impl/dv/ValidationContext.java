package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.Locale;

public interface ValidationContext
{
    boolean needFacetChecking();
    
    boolean needExtraChecking();
    
    boolean needToNormalize();
    
    boolean useNamespaces();
    
    boolean isEntityDeclared(final String p0);
    
    boolean isEntityUnparsed(final String p0);
    
    boolean isIdDeclared(final String p0);
    
    void addId(final String p0);
    
    void addIdRef(final String p0);
    
    String getSymbol(final String p0);
    
    String getURI(final String p0);
    
    Locale getLocale();
}
