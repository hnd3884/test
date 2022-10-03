package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarPool;

public interface XSGrammarPoolContainer
{
    XMLGrammarPool getGrammarPool();
    
    boolean isFullyComposed();
    
    Boolean getFeature(final String p0);
    
    String getXMLSchemaVersion();
}
