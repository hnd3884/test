package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class SignatureProperties extends SignatureElementProxy
{
    public SignatureProperties(final Document document) {
        super(document);
        this.addReturnToSelf();
    }
    
    public SignatureProperties(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, "Id");
        if (attributeNodeNS != null) {
            element.setIdAttributeNode(attributeNodeNS, true);
        }
        for (int length = this.getLength(), i = 0; i < length; ++i) {
            final Element selectDsNode = XMLUtils.selectDsNode(this.getElement(), "SignatureProperty", i);
            final Attr attributeNodeNS2 = selectDsNode.getAttributeNodeNS(null, "Id");
            if (attributeNodeNS2 != null) {
                selectDsNode.setIdAttributeNode(attributeNodeNS2, true);
            }
        }
    }
    
    public int getLength() {
        return XMLUtils.selectDsNodes(this.getElement(), "SignatureProperty").length;
    }
    
    public SignatureProperty item(final int n) throws XMLSignatureException {
        try {
            final Element selectDsNode = XMLUtils.selectDsNode(this.getElement(), "SignatureProperty", n);
            if (selectDsNode == null) {
                return null;
            }
            return new SignatureProperty(selectDsNode, this.baseURI);
        }
        catch (final XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }
    
    public void setId(final String s) {
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public void addSignatureProperty(final SignatureProperty signatureProperty) {
        this.appendSelf(signatureProperty);
        this.addReturnToSelf();
    }
    
    @Override
    public String getBaseLocalName() {
        return "SignatureProperties";
    }
}
