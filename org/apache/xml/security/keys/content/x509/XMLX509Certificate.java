package org.apache.xml.security.keys.content.x509;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent
{
    public static final String JCA_CERT_ID = "X.509";
    
    public XMLX509Certificate(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public XMLX509Certificate(final Document document, final byte[] array) {
        super(document);
        this.addBase64Text(array);
    }
    
    public XMLX509Certificate(final Document document, final X509Certificate x509Certificate) throws XMLSecurityException {
        super(document);
        try {
            this.addBase64Text(x509Certificate.getEncoded());
        }
        catch (final CertificateEncodingException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }
    
    public byte[] getCertificateBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }
    
    public X509Certificate getX509Certificate() throws XMLSecurityException {
        try {
            final X509Certificate x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(this.getCertificateBytes()));
            if (x509Certificate != null) {
                return x509Certificate;
            }
            return null;
        }
        catch (final CertificateException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException {
        final X509Certificate x509Certificate = this.getX509Certificate();
        if (x509Certificate != null) {
            return x509Certificate.getPublicKey();
        }
        return null;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!this.getClass().getName().equals(o.getClass().getName())) {
            return false;
        }
        final XMLX509Certificate xmlx509Certificate = (XMLX509Certificate)o;
        try {
            return MessageDigest.isEqual(xmlx509Certificate.getCertificateBytes(), this.getCertificateBytes());
        }
        catch (final XMLSecurityException ex) {
            return false;
        }
    }
    
    public int hashCode() {
        return 72;
    }
    
    public String getBaseLocalName() {
        return "X509Certificate";
    }
}
