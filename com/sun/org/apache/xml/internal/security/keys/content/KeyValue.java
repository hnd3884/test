package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.ECKeyValue;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.Key;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class KeyValue extends SignatureElementProxy implements KeyInfoContent
{
    public KeyValue(final Document document, final DSAKeyValue dsaKeyValue) {
        super(document);
        this.addReturnToSelf();
        this.appendSelf(dsaKeyValue);
        this.addReturnToSelf();
    }
    
    public KeyValue(final Document document, final RSAKeyValue rsaKeyValue) {
        super(document);
        this.addReturnToSelf();
        this.appendSelf(rsaKeyValue);
        this.addReturnToSelf();
    }
    
    public KeyValue(final Document document, final Element element) {
        super(document);
        this.addReturnToSelf();
        this.appendSelf(element);
        this.addReturnToSelf();
    }
    
    public KeyValue(final Document document, final PublicKey publicKey) {
        super(document);
        this.addReturnToSelf();
        if (publicKey instanceof DSAPublicKey) {
            this.appendSelf(new DSAKeyValue(this.getDocument(), publicKey));
            this.addReturnToSelf();
        }
        else if (publicKey instanceof RSAPublicKey) {
            this.appendSelf(new RSAKeyValue(this.getDocument(), publicKey));
            this.addReturnToSelf();
        }
        else {
            if (!(publicKey instanceof ECPublicKey)) {
                throw new IllegalArgumentException("The given PublicKey type " + publicKey + " is not supported. Only DSAPublicKey and RSAPublicKey and ECPublicKey types are currently supported");
            }
            this.appendSelf(new ECKeyValue(this.getDocument(), publicKey));
            this.addReturnToSelf();
        }
    }
    
    public KeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "RSAKeyValue", 0);
        if (selectDsNode != null) {
            return new RSAKeyValue(selectDsNode, this.baseURI).getPublicKey();
        }
        final Element selectDsNode2 = XMLUtils.selectDsNode(this.getFirstChild(), "DSAKeyValue", 0);
        if (selectDsNode2 != null) {
            return new DSAKeyValue(selectDsNode2, this.baseURI).getPublicKey();
        }
        return null;
    }
    
    @Override
    public String getBaseLocalName() {
        return "KeyValue";
    }
}
