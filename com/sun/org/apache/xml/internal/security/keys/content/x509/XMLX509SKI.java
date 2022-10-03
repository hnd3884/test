package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.util.Arrays;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent
{
    private static final Logger LOG;
    public static final String SKI_OID = "2.5.29.14";
    
    public XMLX509SKI(final Document document, final byte[] array) {
        super(document);
        this.addBase64Text(array);
    }
    
    public XMLX509SKI(final Document document, final X509Certificate x509Certificate) throws XMLSecurityException {
        super(document);
        this.addBase64Text(getSKIBytesFromCert(x509Certificate));
    }
    
    public XMLX509SKI(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public byte[] getSKIBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }
    
    public static byte[] getSKIBytesFromCert(final X509Certificate x509Certificate) throws XMLSecurityException {
        if (x509Certificate.getVersion() < 3) {
            throw new XMLSecurityException("certificate.noSki.lowVersion", new Object[] { x509Certificate.getVersion() });
        }
        final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.14");
        if (extensionValue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
        }
        final byte[] array = new byte[extensionValue.length - 4];
        System.arraycopy(extensionValue, 4, array, 0, array.length);
        if (XMLX509SKI.LOG.isDebugEnabled()) {
            XMLX509SKI.LOG.debug("Base64 of SKI is " + XMLUtils.encodeToString(array));
        }
        return array;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XMLX509SKI)) {
            return false;
        }
        final XMLX509SKI xmlx509SKI = (XMLX509SKI)o;
        try {
            return Arrays.equals(xmlx509SKI.getSKIBytes(), this.getSKIBytes());
        }
        catch (final XMLSecurityException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int n = 17;
        try {
            final byte[] skiBytes = this.getSKIBytes();
            for (int i = 0; i < skiBytes.length; ++i) {
                n = 31 * n + skiBytes[i];
            }
        }
        catch (final XMLSecurityException ex) {
            XMLX509SKI.LOG.debug(ex.getMessage(), ex);
        }
        return n;
    }
    
    @Override
    public String getBaseLocalName() {
        return "X509SKI";
    }
    
    static {
        LOG = LoggerFactory.getLogger(XMLX509SKI.class);
    }
}
