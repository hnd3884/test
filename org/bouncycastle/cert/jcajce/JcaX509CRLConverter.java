package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509CRL;
import org.bouncycastle.cert.X509CRLHolder;
import java.security.Provider;

public class JcaX509CRLConverter
{
    private CertHelper helper;
    
    public JcaX509CRLConverter() {
        this.helper = new DefaultCertHelper();
        this.helper = new DefaultCertHelper();
    }
    
    public JcaX509CRLConverter setProvider(final Provider provider) {
        this.helper = new ProviderCertHelper(provider);
        return this;
    }
    
    public JcaX509CRLConverter setProvider(final String s) {
        this.helper = new NamedCertHelper(s);
        return this;
    }
    
    public X509CRL getCRL(final X509CRLHolder x509CRLHolder) throws CRLException {
        try {
            return (X509CRL)this.helper.getCertificateFactory("X.509").generateCRL(new ByteArrayInputStream(x509CRLHolder.getEncoded()));
        }
        catch (final IOException ex) {
            throw new ExCRLException("exception parsing certificate: " + ex.getMessage(), ex);
        }
        catch (final NoSuchProviderException ex2) {
            throw new ExCRLException("cannot find required provider:" + ex2.getMessage(), ex2);
        }
        catch (final CertificateException ex3) {
            throw new ExCRLException("cannot create factory: " + ex3.getMessage(), ex3);
        }
    }
    
    private class ExCRLException extends CRLException
    {
        private Throwable cause;
        
        public ExCRLException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
