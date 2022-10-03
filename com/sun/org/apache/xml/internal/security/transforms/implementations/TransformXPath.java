package com.sun.org.apache.xml.internal.security.transforms.implementations;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformXPath extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/TR/1999/REC-xpath-19991116";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws TransformationException {
        try {
            final Element selectDsNode = XMLUtils.selectDsNode(transform.getElement().getFirstChild(), "XPath", 0);
            if (selectDsNode == null) {
                throw new TransformationException("xml.WrongContent", new Object[] { "ds:XPath", "Transform" });
            }
            final Node firstChild = selectDsNode.getFirstChild();
            if (firstChild == null) {
                throw new DOMException((short)3, "Text must be in ds:Xpath");
            }
            final String strFromNode = XMLUtils.getStrFromNode(firstChild);
            xmlSignatureInput.setNeedsToBeExpanded(this.needsCircumvent(strFromNode));
            xmlSignatureInput.addNodeFilter(new XPathNodeFilter(selectDsNode, firstChild, strFromNode, XPathFactory.newInstance().newXPathAPI()));
            xmlSignatureInput.setNodeSet(true);
            return xmlSignatureInput;
        }
        catch (final DOMException ex) {
            throw new TransformationException(ex);
        }
    }
    
    private boolean needsCircumvent(final String s) {
        return s.indexOf("namespace") != -1 || s.indexOf("name()") != -1;
    }
    
    static class XPathNodeFilter implements NodeFilter
    {
        XPathAPI xPathAPI;
        Node xpathnode;
        Element xpathElement;
        String str;
        
        XPathNodeFilter(final Element xpathElement, final Node xpathnode, final String str, final XPathAPI xPathAPI) {
            this.xpathnode = xpathnode;
            this.str = str;
            this.xpathElement = xpathElement;
            this.xPathAPI = xPathAPI;
        }
        
        @Override
        public int isNodeInclude(final Node node) {
            try {
                if (this.xPathAPI.evaluate(node, this.xpathnode, this.str, this.xpathElement)) {
                    return 1;
                }
                return 0;
            }
            catch (final TransformerException ex) {
                throw new XMLSecurityRuntimeException("signature.Transform.node", new Object[] { node }, ex);
            }
            catch (final Exception ex2) {
                throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType", new Object[] { node, node.getNodeType() }, ex2);
            }
        }
        
        @Override
        public int isNodeIncludeDO(final Node node, final int n) {
            return this.isNodeInclude(node);
        }
    }
}
