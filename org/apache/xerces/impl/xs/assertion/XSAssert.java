package org.apache.xerces.impl.xs.assertion;

import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSObject;

public interface XSAssert extends XSObject
{
    XSObjectList getAnnotations();
    
    Test getTest();
    
    XSTypeDefinition getTypeDefinition();
}
