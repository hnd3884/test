package org.apache.xml.security.keys.content;

import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Attr;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class RetrievalMethod extends SignatureElementProxy implements KeyInfoContent
{
    public static final String TYPE_DSA = "http://www.w3.org/2000/09/xmldsig#DSAKeyValue";
    public static final String TYPE_RSA = "http://www.w3.org/2000/09/xmldsig#RSAKeyValue";
    public static final String TYPE_PGP = "http://www.w3.org/2000/09/xmldsig#PGPData";
    public static final String TYPE_SPKI = "http://www.w3.org/2000/09/xmldsig#SPKIData";
    public static final String TYPE_MGMT = "http://www.w3.org/2000/09/xmldsig#MgmtData";
    public static final String TYPE_X509 = "http://www.w3.org/2000/09/xmldsig#X509Data";
    public static final String TYPE_RAWX509 = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";
    
    public RetrievalMethod(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public RetrievalMethod(final Document document, final String s, final Transforms transforms, final String s2) {
        super(document);
        super._constructionElement.setAttributeNS(null, "URI", s);
        if (s2 != null) {
            super._constructionElement.setAttributeNS(null, "Type", s2);
        }
        if (transforms != null) {
            super._constructionElement.appendChild(transforms.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public Attr getURIAttr() {
        return super._constructionElement.getAttributeNodeNS(null, "URI");
    }
    
    public String getURI() {
        return this.getURIAttr().getNodeValue();
    }
    
    public String getType() {
        return super._constructionElement.getAttributeNS(null, "Type");
    }
    
    public Transforms getTransforms() throws XMLSecurityException {
        try {
            final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "Transforms", 0);
            if (selectDsNode != null) {
                return new Transforms(selectDsNode, super._baseURI);
            }
            return null;
        }
        catch (final XMLSignatureException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }
    
    public String getBaseLocalName() {
        return "RetrievalMethod";
    }
}
