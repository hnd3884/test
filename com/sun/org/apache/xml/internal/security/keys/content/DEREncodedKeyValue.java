package com.sun.org.apache.xml.internal.security.keys.content;

import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;

public class DEREncodedKeyValue extends Signature11ElementProxy implements KeyInfoContent
{
    private static final String[] supportedKeyTypes;
    
    public DEREncodedKeyValue(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public DEREncodedKeyValue(final Document document, final PublicKey publicKey) throws XMLSecurityException {
        super(document);
        this.addBase64Text(this.getEncodedDER(publicKey));
    }
    
    public DEREncodedKeyValue(final Document document, final byte[] array) {
        super(document);
        this.addBase64Text(array);
    }
    
    public void setId(final String s) {
        this.setLocalIdAttribute("Id", s);
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    @Override
    public String getBaseLocalName() {
        return "DEREncodedKeyValue";
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException {
        final byte[] bytesFromTextChild = this.getBytesFromTextChild();
        for (final String s : DEREncodedKeyValue.supportedKeyTypes) {
            try {
                final PublicKey generatePublic = KeyFactory.getInstance(s).generatePublic(new X509EncodedKeySpec(bytesFromTextChild));
                if (generatePublic != null) {
                    return generatePublic;
                }
            }
            catch (final NoSuchAlgorithmException ex) {}
            catch (final InvalidKeySpecException ex2) {}
        }
        throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
    }
    
    protected byte[] getEncodedDER(final PublicKey publicKey) throws XMLSecurityException {
        try {
            return KeyFactory.getInstance(publicKey.getAlgorithm()).getKeySpec(publicKey, X509EncodedKeySpec.class).getEncoded();
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSecurityException(ex, "DEREncodedKeyValue.UnsupportedPublicKey", new Object[] { publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName() });
        }
        catch (final InvalidKeySpecException ex2) {
            throw new XMLSecurityException(ex2, "DEREncodedKeyValue.UnsupportedPublicKey", new Object[] { publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName() });
        }
    }
    
    static {
        supportedKeyTypes = new String[] { "RSA", "DSA", "EC" };
    }
}
