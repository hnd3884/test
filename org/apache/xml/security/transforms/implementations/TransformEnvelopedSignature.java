package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.signature.NodeFilter;
import org.w3c.dom.Node;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;

public class TransformEnvelopedSignature extends TransformSpi
{
    public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    
    protected String engineGetURI() {
        return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    }
    
    protected XMLSignatureInput enginePerformTransform(final XMLSignatureInput xmlSignatureInput, final Transform transform) throws TransformationException {
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
            throw new TransformationException("envelopedSignatureTransformNotInSignatureElement");
        }
        return parentNode;
    }
    
    static class EnvelopedNodeFilter implements NodeFilter
    {
        Node exclude;
        
        EnvelopedNodeFilter(final Node exclude) {
            this.exclude = exclude;
        }
        
        public int isNodeIncludeDO(final Node node, final int n) {
            if (node == this.exclude) {
                return -1;
            }
            return 1;
        }
        
        public int isNodeInclude(final Node node) {
            if (node == this.exclude || XMLUtils.isDescendantOrSelf(this.exclude, node)) {
                return -1;
            }
            return 1;
        }
    }
}
