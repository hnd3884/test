package org.apache.xml.security.keys.content.keyvalues;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.apache.xml.security.utils.I18n;
import java.security.interfaces.RSAPublicKey;
import java.security.Key;
import org.apache.xml.security.utils.XMLUtils;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class RSAKeyValue extends SignatureElementProxy implements KeyValueContent
{
    public RSAKeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public RSAKeyValue(final Document document, final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        this.addBigIntegerElement(bigInteger, "Modulus");
        this.addBigIntegerElement(bigInteger2, "Exponent");
    }
    
    public RSAKeyValue(final Document document, final Key key) throws IllegalArgumentException {
        super(document);
        XMLUtils.addReturnToElement(super._constructionElement);
        if (key instanceof RSAPublicKey) {
            this.addBigIntegerElement(((RSAPublicKey)key).getModulus(), "Modulus");
            this.addBigIntegerElement(((RSAPublicKey)key).getPublicExponent(), "Exponent");
            return;
        }
        throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", new Object[] { "RSAKeyValue", key.getClass().getName() }));
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(this.getBigIntegerFromChildElement("Modulus", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("Exponent", "http://www.w3.org/2000/09/xmldsig#")));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSecurityException("empty", ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new XMLSecurityException("empty", ex2);
        }
    }
    
    public String getBaseLocalName() {
        return "RSAKeyValue";
    }
}
