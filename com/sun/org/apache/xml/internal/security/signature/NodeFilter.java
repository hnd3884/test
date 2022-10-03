package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;

public interface NodeFilter
{
    int isNodeInclude(final Node p0);
    
    int isNodeIncludeDO(final Node p0, final int p1);
}
