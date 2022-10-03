package com.sun.org.apache.xml.internal.security.keys.content.x509;

import java.util.Arrays;
import java.security.PublicKey;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

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
            throw new XMLSecurityException(ex);
        }
    }
    
    public byte[] getCertificateBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }
    
    public X509Certificate getX509Certificate() throws XMLSecurityException {
        final byte[] certificateBytes = this.getCertificateBytes();
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certificateBytes)) {
            final X509Certificate x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(byteArrayInputStream);
            if (x509Certificate != null) {
                return x509Certificate;
            }
            return null;
        }
        catch (final CertificateException | IOException ex) {
            throw new XMLSecurityException((Exception)ex);
        }
    }
    
    public PublicKey getPublicKey() throws XMLSecurityException, IOException {
        final X509Certificate x509Certificate = this.getX509Certificate();
        if (x509Certificate != null) {
            return x509Certificate.getPublicKey();
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XMLX509Certificate)) {
            return false;
        }
        final XMLX509Certificate xmlx509Certificate = (XMLX509Certificate)o;
        try {
            return Arrays.equals(xmlx509Certificate.getCertificateBytes(), this.getCertificateBytes());
        }
        catch (final XMLSecurityException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int n = 17;
        try {
            final byte[] certificateBytes = this.getCertificateBytes();
            for (int i = 0; i < certificateBytes.length; ++i) {
                n = 31 * n + certificateBytes[i];
            }
        }
        catch (final XMLSecurityException ex) {
            XMLX509Certificate.LOG.debug(ex.getMessage(), ex);
        }
        return n;
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509Certificate";
    }
}
