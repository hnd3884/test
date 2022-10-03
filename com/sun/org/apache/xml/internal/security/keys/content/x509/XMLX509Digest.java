package com.sun.org.apache.xml.internal.security.keys.content.x509;

import java.security.MessageDigest;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import org.w3c.dom.Attr;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;

public class XMLX509Digest extends Signature11ElementProxy implements XMLX509DataContent
{
    public XMLX509Digest(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509Digest(final Document document, final byte[] array, final String s) {
        super(document);
        this.addBase64Text(array);
        this.setLocalAttribute("Algorithm", s);
    }
    
    public XMLX509Digest(final Document document, final X509Certificate x509Certificate, final String s) throws XMLSecurityException {
        super(document);
        this.addBase64Text(getDigestBytesFromCert(x509Certificate, s));
        this.setLocalAttribute("Algorithm", s);
    }
    
    public Attr getAlgorithmAttr() {
        return this.getElement().getAttributeNodeNS(null, "Algorithm");
    }
    
    public String getAlgorithm() {
        return this.getAlgorithmAttr().getNodeValue();
    }
    
    public byte[] getDigestBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }
    
    public static byte[] getDigestBytesFromCert(final X509Certificate x509Certificate, final String s) throws XMLSecurityException {
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(s);
        if (translateURItoJCEID == null) {
            throw new XMLSecurityException("XMLX509Digest.UnknownDigestAlgorithm", new Object[] { s });
        }
        try {
            return MessageDigest.getInstance(translateURItoJCEID).digest(x509Certificate.getEncoded());
        }
        catch (final Exception ex) {
            throw new XMLSecurityException("XMLX509Digest.FailedDigest", new Object[] { translateURItoJCEID });
        }
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509Digest";
    }
}
