package org.apache.xml.security.utils;

import org.w3c.dom.Text;
import org.apache.xpath.XPathContext;
import javax.xml.transform.ErrorListener;
import org.apache.xml.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;
import org.apache.xpath.XPath;
import org.apache.xml.utils.PrefixResolverDefault;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.implementations.FuncHereContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class XPathFuncHereAPI
{
    public static Node selectSingleNode(final Node node, final Node node2) throws TransformerException {
        return selectSingleNode(node, node2, node);
    }
    
    public static Node selectSingleNode(final Node node, final Node node2, final Node node3) throws TransformerException {
        return selectNodeIterator(node, node2, node3).nextNode();
    }
    
    public static NodeIterator selectNodeIterator(final Node node, final Node node2) throws TransformerException {
        return selectNodeIterator(node, node2, node);
    }
    
    public static NodeIterator selectNodeIterator(final Node node, final Node node2, final Node node3) throws TransformerException {
        return eval(node, node2, node3).nodeset();
    }
    
    public static NodeList selectNodeList(final Node node, final Node node2) throws TransformerException {
        return selectNodeList(node, node2, node);
    }
    
    public static NodeList selectNodeList(final Node node, final Node node2, final Node node3) throws TransformerException {
        return eval(node, node2, node3).nodelist();
    }
    
    public static XObject eval(final Node node, final Node node2) throws TransformerException {
        return eval(node, node2, node);
    }
    
    public static XObject eval(final Node node, final Node node2, final Node node3) throws TransformerException {
        final FuncHereContext funcHereContext = new FuncHereContext(node2);
        final PrefixResolverDefault prefixResolverDefault = new PrefixResolverDefault((node3.getNodeType() == 9) ? ((Document)node3).getDocumentElement() : node3);
        return new XPath(getStrFromNode(node2), (SourceLocator)null, (PrefixResolver)prefixResolverDefault, 0, (ErrorListener)null).execute((XPathContext)funcHereContext, funcHereContext.getDTMHandleFromNode(node), (PrefixResolver)prefixResolverDefault);
    }
    
    public static XObject eval(final Node node, final Node node2, final PrefixResolver prefixResolver) throws TransformerException {
        final XPath xPath = new XPath(getStrFromNode(node2), (SourceLocator)null, prefixResolver, 0, (ErrorListener)null);
        final FuncHereContext funcHereContext = new FuncHereContext(node2);
        return xPath.execute((XPathContext)funcHereContext, funcHereContext.getDTMHandleFromNode(node), prefixResolver);
    }
    
    private static String getStrFromNode(final Node node) {
        if (node.getNodeType() == 3) {
            return ((Text)node).getData();
        }
        if (node.getNodeType() == 2) {
            return node.getNodeValue();
        }
        if (node.getNodeType() == 7) {
            return node.getNodeValue();
        }
        return "";
    }
}
