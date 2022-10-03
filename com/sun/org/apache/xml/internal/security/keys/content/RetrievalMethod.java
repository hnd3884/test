package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

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
        this.setLocalAttribute("URI", s);
        if (s2 != null) {
            this.setLocalAttribute("Type", s2);
        }
        if (transforms != null) {
            this.appendSelf(transforms);
            this.addReturnToSelf();
        }
    }
    
    public Attr getURIAttr() {
        return this.getElement().getAttributeNodeNS(null, "URI");
    }
    
    public String getURI() {
        return this.getLocalAttribute("URI");
    }
    
    public String getType() {
        return this.getLocalAttribute("Type");
    }
    
    public Transforms getTransforms() throws XMLSecurityException {
        try {
            final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "Transforms", 0);
            if (selectDsNode != null) {
                return new Transforms(selectDsNode, this.baseURI);
            }
            return null;
        }
        catch (final XMLSignatureException ex) {
            throw new XMLSecurityException(ex);
        }
    }
    
    @Override
    public String getBaseLocalName() {
        return "RetrievalMethod";
    }
}
