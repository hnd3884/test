package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public interface XPathAPI
{
    NodeList selectNodeList(final Node p0, final Node p1, final String p2, final Node p3) throws TransformerException;
    
    boolean evaluate(final Node p0, final Node p1, final String p2, final Node p3) throws TransformerException;
    
    void clear();
}
