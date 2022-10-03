package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.security.interfaces.DSAPublicKey;
import java.security.Key;
import java.math.BigInteger;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class DSAKeyValue extends SignatureElementProxy implements KeyValueContent
{
    public DSAKeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public DSAKeyValue(final Document document, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        super(document);
        this.addReturnToSelf();
        this.addBigIntegerElement(bigInteger, "P");
        this.addBigIntegerElement(bigInteger2, "Q");
        this.addBigIntegerElement(bigInteger3, "G");
        this.addBigIntegerElement(bigInteger4, "Y");
    }
    
    public DSAKeyValue(final Document document, final Key key) throws IllegalArgumentException {
        super(document);
        this.addReturnToSelf();
        if (key instanceof DSAPublicKey) {
            final DSAParams params = ((DSAPublicKey)key).getParams();
            this.addBigIntegerElement(params.getP(), "P");
            this.addBigIntegerElement(params.getQ(), "Q");
            this.addBigIntegerElement(params.getG(), "G");
            this.addBigIntegerElement(((DSAPublicKey)key).getY(), "Y");
            return;
        }
        throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", new Object[] { "DSAKeyValue", key.getClass().getName() }));
    }
    
    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            return KeyFactory.getInstance("DSA").generatePublic(new DSAPublicKeySpec(this.getBigIntegerFromChildElement("Y", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("P", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("Q", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("G", "http://www.w3.org/2000/09/xmldsig#")));
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
        return "DSAKeyValue";
    }
}
