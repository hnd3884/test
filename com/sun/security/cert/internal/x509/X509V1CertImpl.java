package com.sun.security.cert.internal.x509;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.math.BigInteger;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.CertificateExpiredException;
import java.util.Date;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.security.cert.CertificateEncodingException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.Serializable;
import javax.security.cert.X509Certificate;

public class X509V1CertImpl extends X509Certificate implements Serializable
{
    static final long serialVersionUID = -2048442350420423405L;
    private java.security.cert.X509Certificate wrappedCert;
    
    private static synchronized CertificateFactory getFactory() throws CertificateException {
        return CertificateFactory.getInstance("X.509");
    }
    
    public X509V1CertImpl() {
    }
    
    public X509V1CertImpl(final byte[] array) throws javax.security.cert.CertificateException {
        try {
            this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(new ByteArrayInputStream(array));
        }
        catch (final CertificateException ex) {
            throw new javax.security.cert.CertificateException(ex.getMessage());
        }
    }
    
    public X509V1CertImpl(final InputStream inputStream) throws javax.security.cert.CertificateException {
        try {
            this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(inputStream);
        }
        catch (final CertificateException ex) {
            throw new javax.security.cert.CertificateException(ex.getMessage());
        }
    }
    
    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            return this.wrappedCert.getEncoded();
        }
        catch (final java.security.cert.CertificateEncodingException ex) {
            throw new CertificateEncodingException(ex.getMessage());
        }
    }
    
    @Override
    public void verify(final PublicKey publicKey) throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        try {
            this.wrappedCert.verify(publicKey);
        }
        catch (final CertificateException ex) {
            throw new javax.security.cert.CertificateException(ex.getMessage());
        }
    }
    
    @Override
    public void verify(final PublicKey publicKey, final String s) throws javax.security.cert.CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        try {
            this.wrappedCert.verify(publicKey, s);
        }
        catch (final CertificateException ex) {
            throw new javax.security.cert.CertificateException(ex.getMessage());
        }
    }
    
    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }
    
    @Override
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        try {
            this.wrappedCert.checkValidity(date);
        }
        catch (final java.security.cert.CertificateNotYetValidException ex) {
            throw new CertificateNotYetValidException(ex.getMessage());
        }
        catch (final java.security.cert.CertificateExpiredException ex2) {
            throw new CertificateExpiredException(ex2.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return this.wrappedCert.toString();
    }
    
    @Override
    public PublicKey getPublicKey() {
        return this.wrappedCert.getPublicKey();
    }
    
    @Override
    public int getVersion() {
        return this.wrappedCert.getVersion() - 1;
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return this.wrappedCert.getSerialNumber();
    }
    
    @Override
    public Principal getSubjectDN() {
        return this.wrappedCert.getSubjectDN();
    }
    
    @Override
    public Principal getIssuerDN() {
        return this.wrappedCert.getIssuerDN();
    }
    
    @Override
    public Date getNotBefore() {
        return this.wrappedCert.getNotBefore();
    }
    
    @Override
    public Date getNotAfter() {
        return this.wrappedCert.getNotAfter();
    }
    
    @Override
    public String getSigAlgName() {
        return this.wrappedCert.getSigAlgName();
    }
    
    @Override
    public String getSigAlgOID() {
        return this.wrappedCert.getSigAlgOID();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        return this.wrappedCert.getSigAlgParams();
    }
    
    private synchronized void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        try {
            objectOutputStream.write(this.getEncoded());
        }
        catch (final CertificateEncodingException ex) {
            throw new IOException("getEncoded failed: " + ex.getMessage());
        }
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException {
        try {
            this.wrappedCert = (java.security.cert.X509Certificate)getFactory().generateCertificate(objectInputStream);
        }
        catch (final CertificateException ex) {
            throw new IOException("generateCertificate failed: " + ex.getMessage());
        }
    }
    
    public java.security.cert.X509Certificate getX509Certificate() {
        return this.wrappedCert;
    }
}
