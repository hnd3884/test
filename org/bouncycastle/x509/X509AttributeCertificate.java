package org.bouncycastle.x509;

import java.io.IOException;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.PublicKey;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import java.math.BigInteger;
import java.security.cert.X509Extension;

public interface X509AttributeCertificate extends X509Extension
{
    int getVersion();
    
    BigInteger getSerialNumber();
    
    Date getNotBefore();
    
    Date getNotAfter();
    
    AttributeCertificateHolder getHolder();
    
    AttributeCertificateIssuer getIssuer();
    
    X509Attribute[] getAttributes();
    
    X509Attribute[] getAttributes(final String p0);
    
    boolean[] getIssuerUniqueID();
    
    void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;
    
    void checkValidity(final Date p0) throws CertificateExpiredException, CertificateNotYetValidException;
    
    byte[] getSignature();
    
    void verify(final PublicKey p0, final String p1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
    
    byte[] getEncoded() throws IOException;
}
