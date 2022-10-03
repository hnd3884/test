package io.netty.handler.ssl.util;

import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.security.cert.CertificateException;
import java.security.PublicKey;
import java.security.Principal;
import java.math.BigInteger;
import java.util.Date;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.CertificateExpiredException;
import io.netty.util.internal.ObjectUtil;
import javax.security.cert.X509Certificate;

public final class LazyJavaxX509Certificate extends X509Certificate
{
    private final byte[] bytes;
    private X509Certificate wrapped;
    
    public LazyJavaxX509Certificate(final byte[] bytes) {
        this.bytes = ObjectUtil.checkNotNull(bytes, "bytes");
    }
    
    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity();
    }
    
    @Override
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity(date);
    }
    
    @Override
    public int getVersion() {
        return this.unwrap().getVersion();
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return this.unwrap().getSerialNumber();
    }
    
    @Override
    public Principal getIssuerDN() {
        return this.unwrap().getIssuerDN();
    }
    
    @Override
    public Principal getSubjectDN() {
        return this.unwrap().getSubjectDN();
    }
    
    @Override
    public Date getNotBefore() {
        return this.unwrap().getNotBefore();
    }
    
    @Override
    public Date getNotAfter() {
        return this.unwrap().getNotAfter();
    }
    
    @Override
    public String getSigAlgName() {
        return this.unwrap().getSigAlgName();
    }
    
    @Override
    public String getSigAlgOID() {
        return this.unwrap().getSigAlgOID();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        return this.unwrap().getSigAlgParams();
    }
    
    @Override
    public byte[] getEncoded() {
        return this.bytes.clone();
    }
    
    byte[] getBytes() {
        return this.bytes;
    }
    
    @Override
    public void verify(final PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify(key);
    }
    
    @Override
    public void verify(final PublicKey key, final String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify(key, sigProvider);
    }
    
    @Override
    public String toString() {
        return this.unwrap().toString();
    }
    
    @Override
    public PublicKey getPublicKey() {
        return this.unwrap().getPublicKey();
    }
    
    private X509Certificate unwrap() {
        X509Certificate wrapped = this.wrapped;
        if (wrapped == null) {
            try {
                final X509Certificate instance = X509Certificate.getInstance(this.bytes);
                this.wrapped = instance;
                wrapped = instance;
            }
            catch (final CertificateException e) {
                throw new IllegalStateException(e);
            }
        }
        return wrapped;
    }
}
