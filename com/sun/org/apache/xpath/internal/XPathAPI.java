package com.sun.org.apache.xpath.internal;

import javax.xml.transform.ErrorListener;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import org.w3c.dom.Document;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class XPathAPI
{
    public static Node selectSingleNode(final Node contextNode, final String str) throws TransformerException {
        return selectSingleNode(contextNode, str, contextNode);
    }
    
    public static Node selectSingleNode(final Node contextNode, final String str, final Node namespaceNode) throws TransformerException {
        final NodeIterator nl = selectNodeIterator(contextNode, str, namespaceNode);
        return nl.nextNode();
    }
    
    public static NodeIterator selectNodeIterator(final Node contextNode, final String str) throws TransformerException {
        return selectNodeIterator(contextNode, str, contextNode);
    }
    
    public static NodeIterator selectNodeIterator(final Node contextNode, final String str, final Node namespaceNode) throws TransformerException {
        final XObject list = eval(contextNode, str, namespaceNode);
        return list.nodeset();
    }
    
    public static NodeList selectNodeList(final Node contextNode, final String str) throws TransformerException {
        return selectNodeList(contextNode, str, contextNode);
    }
    
    public static NodeList selectNodeList(final Node contextNode, final String str, final Node namespaceNode) throws TransformerException {
        final XObject list = eval(contextNode, str, namespaceNode);
        return list.nodelist();
    }
    
    public static XObject eval(final Node contextNode, final String str) throws TransformerException {
        return eval(contextNode, str, contextNode);
    }
    
    public static XObject eval(final Node contextNode, final String str, final Node namespaceNode) throws TransformerException {
        final XPathContext xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
        final PrefixResolverDefault prefixResolver = new PrefixResolverDefault((namespaceNode.getNodeType() == 9) ? ((Document)namespaceNode).getDocumentElement() : namespaceNode);
        final XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        final int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
    }
    
    public static XObject eval(final Node contextNode, final String str, final PrefixResolver prefixResolver) throws TransformerException {
        final XPath xpath = new XPath(str, null, prefixResolver, 0, null);
        final XPathContext xpathSupport = new XPathContext(JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
        final int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
        return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
    }
}
