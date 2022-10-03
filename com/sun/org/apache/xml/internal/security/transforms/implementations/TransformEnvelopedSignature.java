package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;

public class TransformEnvelopedSignature extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    
    @Override
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    }
    
    @Override
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final OutputStream outputStream, final Transform transform) throws TransformationException {
        final Node searchSignatureElement = searchSignatureElement(transform.getElement());
        xmlSignatureInput.setExcludeNode(searchSignatureElement);
        xmlSignatureInput.addNodeFilter(new EnvelopedNodeFilter(searchSignatureElement));
        return xmlSignatureInput;
    }
    
    private static Node searchSignatureElement(Node parentNode) throws TransformationException {
        boolean b = false;
        while (parentNode != null) {
            if (parentNode.getNodeType() == 9) {
                break;
            }
            final Element element = (Element)parentNode;
            if (element.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && element.getLocalName().equals("Signature")) {
                b = true;
                break;
            }
            parentNode = parentNode.getParentNode();
        }
        if (!b) {
            throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement");
        }
        return parentNode;
    }
    
    static class EnvelopedNodeFilter implements NodeFilter
    {
        Node exclude;
        
        EnvelopedNodeFilter(final Node exclude) {
            this.exclude = exclude;
        }
        
        @Override
        public int isNodeIncludeDO(final Node node, final int n) {
            if (node == this.exclude) {
                return -1;
            }
            return 1;
        }
        
        @Override
        public int isNodeInclude(final Node node) {
            if (node == this.exclude || XMLUtils.isDescendantOrSelf(this.exclude, node)) {
                return -1;
            }
            return 1;
        }
    }
}
