package org.apache.xerces.xs;

public interface XSTypeAlternative extends XSObject
{
    XSObjectList getAnnotations();
    
    String getTestStr();
    
    XSTypeDefinition getTypeDefinition();
}
