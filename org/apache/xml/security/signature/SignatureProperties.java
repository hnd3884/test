package org.apache.xml.security.signature;

import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Node;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.SignatureElementProxy;

public class SignatureProperties extends SignatureElementProxy
{
    public SignatureProperties(final Document document) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public SignatureProperties(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public int getLength() {
        return XMLUtils.selectDsNodes(super._constructionElement, "SignatureProperty").length;
    }
    
    public SignatureProperty item(final int n) throws XMLSignatureException {
        try {
            final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement, "SignatureProperty", n);
            if (selectDsNode == null) {
                return null;
            }
            return new SignatureProperty(selectDsNode, super._baseURI);
        }
        catch (final XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    public void setId(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Id", s);
            IdResolver.registerElementById(super._constructionElement, s);
        }
    }
    
    public String getId() {
        return super._constructionElement.getAttributeNS(null, "Id");
    }
    
    public void addSignatureProperty(final SignatureProperty signatureProperty) {
        super._constructionElement.appendChild(signatureProperty.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public String getBaseLocalName() {
        return "SignatureProperties";
    }
}
