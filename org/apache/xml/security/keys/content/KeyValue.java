package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.security.Key;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import org.w3c.dom.Element;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.SignatureElementProxy;

public class KeyValue extends SignatureElementProxy implements KeyInfoContent
{
    public KeyValue(final Document document, final DSAKeyValue dsaKeyValue) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        super._constructionElement.appendChild(dsaKeyValue.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public KeyValue(final Document document, final RSAKeyValue rsaKeyValue) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        super._constructionElement.appendChild(rsaKeyValue.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public KeyValue(final Document document, final Element element) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        super._constructionElement.appendChild(element);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public KeyValue(final Document document, final PublicKey publicKey) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        if (publicKey instanceof DSAPublicKey) {
            super._constructionElement.appendChild(new DSAKeyValue(super._doc, publicKey).getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
        else if (publicKey instanceof RSAPublicKey) {
            super._constructionElement.appendChild(new RSAKeyValue(super._doc, publicKey).getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public KeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "RSAKeyValue", 0);
        if (selectDsNode != null) {
            return new RSAKeyValue(selectDsNode, super._baseURI).getPublicKey();
        }
        final Element selectDsNode2 = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "DSAKeyValue", 0);
        if (selectDsNode2 != null) {
            return new DSAKeyValue(selectDsNode2, super._baseURI).getPublicKey();
        }
        return null;
    }
    
    public String getBaseLocalName() {
        return "KeyValue";
    }
}
