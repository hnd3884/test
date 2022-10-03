package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateParsingException;
import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.Provider;

public class JcaX509CertificateConverter
{
    private CertHelper helper;
    
    public JcaX509CertificateConverter() {
        this.helper = new DefaultCertHelper();
        this.helper = new DefaultCertHelper();
    }
    
    public JcaX509CertificateConverter setProvider(final Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }
    
    public JcaX509CertificateConverter setProvider(final String s) {
        this.helper = new NamedCertHelper(s);
        return this;
    }
    
    public X509Certificate getCertificate(final X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            return (X509Certificate)this.helper.getCertificateFactory("X.509").generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        }
        catch (final IOException ex) {
            throw new ExCertificateParsingException("exception parsing certificate: " + ex.getMessage(), ex);
        }
        catch (final NoSuchProviderException ex2) {
            throw new ExCertificateException("cannot find required provider:" + ex2.getMessage(), ex2);
        }
    }
    
    private class ExCertificateException extends CertificateException
    {
        private Throwable cause;
        
        public ExCertificateException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
    
    private class ExCertificateParsingException extends CertificateParsingException
    {
        private Throwable cause;
        
        public ExCertificateParsingException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
