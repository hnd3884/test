package org.apache.xml.security.transforms.implementations;

import javax.xml.transform.TransformerException;
import org.apache.xml.security.exceptions.XMLSecurityRuntimeException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.xml.security.signature.NodeFilter;
import org.w3c.dom.DOMException;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.CachedXPathAPIHolder;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformXPath extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xpath-19991116";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws TransformationException {
        try {
            CachedXPathAPIHolder.setDoc(transform.getElement().getOwnerDocument());
            final Element selectDsNode = XMLUtils.selectDsNode(transform.getElement().getFirstChild(), "XPath", 0);
            if (selectDsNode == null) {
                throw new TransformationException("xml.WrongContent", new Object[] { "ds:XPath", "Transform" });
            }
            final Node item = selectDsNode.getChildNodes().item(0);
            final String strFromNode = CachedXPathFuncHereAPI.getStrFromNode(item);
            xmlSignatureInput.setNeedsToBeExpanded(this.needsCircunvent(strFromNode));
            if (item == null) {
                throw new DOMException((short)3, "Text must be in ds:Xpath");
            }
            xmlSignatureInput.addNodeFilter(new XPathNodeFilter(selectDsNode, item, strFromNode));
            xmlSignatureInput.setNodeSet(true);
            return xmlSignatureInput;
        }
        catch (final DOMException ex) {
            throw new TransformationException("empty", ex);
        }
    }
    
    private boolean needsCircunvent(final String s) {
        return s.indexOf("namespace") != -1 || s.indexOf("name()") != -1;
    }
    
    static class XPathNodeFilter implements NodeFilter
    {
        PrefixResolverDefault prefixResolver;
        CachedXPathFuncHereAPI xPathFuncHereAPI;
        Node xpathnode;
        String str;
        
        XPathNodeFilter(final Element element, final Node xpathnode, final String str) {
            this.xPathFuncHereAPI = new CachedXPathFuncHereAPI(CachedXPathAPIHolder.getCachedXPathAPI());
            this.xpathnode = xpathnode;
            this.str = str;
            this.prefixResolver = new PrefixResolverDefault((Node)element);
        }
        
        public int isNodeInclude(final Node node) {
            try {
                if (this.xPathFuncHereAPI.eval(node, this.xpathnode, this.str, (PrefixResolver)this.prefixResolver).bool()) {
                    return 1;
                }
                return 0;
            }
            catch (final TransformerException ex) {
                throw new XMLSecurityRuntimeException("signature.Transform.node", new Object[] { node }, ex);
            }
            catch (final Exception ex2) {
                throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType", new Object[] { node, new Short(node.getNodeType()) }, ex2);
            }
        }
        
        public int isNodeIncludeDO(final Node node, final int n) {
            return this.isNodeInclude(node);
        }
    }
}
