package org.apache.xml.security.keys.content.x509;

import org.apache.commons.logging.LogFactory;
import java.security.MessageDigest;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Element;
import org.apache.xml.security.exceptions.XMLSecurityException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent
{
    static Log log;
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
            throw new XMLSecurityException("certificate.noSki.lowVersion", new Object[] { new Integer(x509Certificate.getVersion()) });
        }
        final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.14");
        if (extensionValue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
        }
        final byte[] array = new byte[extensionValue.length - 4];
        System.arraycopy(extensionValue, 4, array, 0, array.length);
        if (XMLX509SKI.log.isDebugEnabled()) {
            XMLX509SKI.log.debug((Object)("Base64 of SKI is " + Base64.encode(array)));
        }
        return array;
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!this.getClass().getName().equals(o.getClass().getName())) {
            return false;
        }
        final XMLX509SKI xmlx509SKI = (XMLX509SKI)o;
        try {
            return MessageDigest.isEqual(xmlx509SKI.getSKIBytes(), this.getSKIBytes());
        }
        catch (final XMLSecurityException ex) {
            return false;
        }
    }
    
    public int hashCode() {
        return 92;
    }
    
    public String getBaseLocalName() {
        return "X509SKI";
    }
    
    static {
        XMLX509SKI.log = LogFactory.getLog(XMLX509SKI.class.getName());
    }
}
