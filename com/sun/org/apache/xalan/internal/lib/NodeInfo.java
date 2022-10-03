package com.sun.org.apache.xalan.internal.lib;

import org.w3c.dom.NodeList;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;

public class NodeInfo
{
    public static String systemId(final ExpressionContext context) {
        final Node contextNode = context.getContextNode();
        final int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getSystemId();
        }
        return null;
    }
    
    public static String systemId(final NodeList nodeList) {
        if (nodeList == null || nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        final int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getSystemId();
        }
        return null;
    }
    
    public static String publicId(final ExpressionContext context) {
        final Node contextNode = context.getContextNode();
        final int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getPublicId();
        }
        return null;
    }
    
    public static String publicId(final NodeList nodeList) {
        if (nodeList == null || nodeList.getLength() == 0) {
            return null;
        }
        final Node node = nodeList.item(0);
        final int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getPublicId();
        }
        return null;
    }
    
    public static int lineNumber(final ExpressionContext context) {
        final Node contextNode = context.getContextNode();
        final int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }
    
    public static int lineNumber(final NodeList nodeList) {
        if (nodeList == null || nodeList.getLength() == 0) {
            return -1;
        }
        final Node node = nodeList.item(0);
        final int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }
    
    public static int columnNumber(final ExpressionContext context) {
        final Node contextNode = context.getContextNode();
        final int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }
    
    public static int columnNumber(final NodeList nodeList) {
        if (nodeList == null || nodeList.getLength() == 0) {
            return -1;
        }
        final Node node = nodeList.item(0);
        final int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
        final SourceLocator locator = ((DTMNodeProxy)node).getDTM().getSourceLocatorFor(nodeHandler);
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }
}
