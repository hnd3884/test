package org.apache.axiom.om;

import javax.xml.namespace.QName;

public interface OMText extends OMNode
{
    String getText();
    
    @Deprecated
    char[] getTextCharacters();
    
    @Deprecated
    boolean isCharacters();
    
    @Deprecated
    QName getTextAsQName();
    
    @Deprecated
    OMNamespace getNamespace();
    
    Object getDataHandler();
    
    boolean isOptimized();
    
    void setOptimize(final boolean p0);
    
    boolean isBinary();
    
    void setBinary(final boolean p0);
    
    String getContentID();
    
    void setContentID(final String p0);
}
