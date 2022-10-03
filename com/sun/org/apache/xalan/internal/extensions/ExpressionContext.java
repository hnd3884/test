package com.sun.org.apache.xalan.internal.extensions;

import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.utils.QName;
import javax.xml.transform.ErrorListener;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;

public interface ExpressionContext
{
    Node getContextNode();
    
    NodeIterator getContextNodes();
    
    ErrorListener getErrorListener();
    
    double toNumber(final Node p0);
    
    String toString(final Node p0);
    
    XObject getVariableOrParam(final QName p0) throws TransformerException;
    
    XPathContext getXPathContext() throws TransformerException;
}
