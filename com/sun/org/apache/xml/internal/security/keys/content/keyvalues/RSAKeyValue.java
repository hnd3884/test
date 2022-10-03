package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.security.interfaces.RSAPublicKey;
import java.security.Key;
import java.math.BigInteger;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class RSAKeyValue extends SignatureElementProxy implements KeyValueContent
{
    public RSAKeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public RSAKeyValue(final Document document, final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(document);
        this.addReturnToSelf();
        this.addBigIntegerElement(bigInteger, "Modulus");
        this.addBigIntegerElement(bigInteger2, "Exponent");
    }
    
    public RSAKeyValue(final Document document, final Key key) throws IllegalArgumentException {
        super(document);
        this.addReturnToSelf();
        if (key instanceof RSAPublicKey) {
            this.addBigIntegerElement(((RSAPublicKey)key).getModulus(), "Modulus");
            this.addBigIntegerElement(((RSAPublicKey)key).getPublicExponent(), "Exponent");
            return;
        }
        throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", new Object[] { "RSAKeyValue", key.getClass().getName() }));
    }
    
    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(this.getBigIntegerFromChildElement("Modulus", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("Exponent", "http://www.w3.org/2000/09/xmldsig#")));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSecurityException(ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new XMLSecurityException(ex2);
        }
    }
    
    @Override
    public String getBaseLocalName() {
        return "RSAKeyValue";
    }
}
