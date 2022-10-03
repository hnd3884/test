package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLEntityDescription extends XMLResourceIdentifier
{
    void setEntityName(final String p0);
    
    String getEntityName();
}
