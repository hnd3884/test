package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public interface XSGrammarPoolContainer
{
    XMLGrammarPool getGrammarPool();
    
    boolean isFullyComposed();
    
    Boolean getFeature(final String p0);
    
    void setFeature(final String p0, final boolean p1);
    
    Object getProperty(final String p0);
    
    void setProperty(final String p0, final Object p1);
}
