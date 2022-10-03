package org.jscep.client.inspect;

import org.slf4j.LoggerFactory;
import java.security.cert.X509CertSelector;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.Certificate;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.security.cert.CertStore;
import org.slf4j.Logger;

public abstract class AbstractCertStoreInspector implements CertStoreInspector
{
    static final Logger LOGGER;
    protected final CertStore store;
    protected X509Certificate signer;
    protected X509Certificate recipient;
    protected X509Certificate issuer;
    
    public AbstractCertStoreInspector(final CertStore store) {
        this.store = store;
        try {
            this.inspect();
        }
        catch (final CertStoreException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void inspect() throws CertStoreException {
        final Collection<? extends Certificate> certs = this.store.getCertificates(null);
        AbstractCertStoreInspector.LOGGER.debug("CertStore contains {} certificate(s):", (Object)certs.size());
        int i = 0;
        for (final Certificate cert : certs) {
            final X509Certificate x509 = (X509Certificate)cert;
            AbstractCertStoreInspector.LOGGER.debug("{}. '[dn={}; serial={}]'", new Object[] { ++i, x509.getSubjectDN(), x509.getSerialNumber() });
        }
        AbstractCertStoreInspector.LOGGER.debug("Looking for recipient entity");
        this.recipient = this.selectCertificate(this.store, this.getRecipientSelectors());
        AbstractCertStoreInspector.LOGGER.debug("Using [dn={}; serial={}] for recipient entity", (Object)this.recipient.getSubjectDN(), (Object)this.recipient.getSerialNumber());
        AbstractCertStoreInspector.LOGGER.debug("Looking for message signing entity");
        this.signer = this.selectCertificate(this.store, this.getSignerSelectors());
        AbstractCertStoreInspector.LOGGER.debug("Using [dn={}; serial={}] for message signing entity", (Object)this.signer.getSubjectDN(), (Object)this.signer.getSerialNumber());
        AbstractCertStoreInspector.LOGGER.debug("Looking for issuing entity");
        this.issuer = this.selectCertificate(this.store, this.getIssuerSelectors(this.recipient.getIssuerX500Principal().getEncoded()));
        AbstractCertStoreInspector.LOGGER.debug("Using [dn={}; serial={}] for issuing entity", (Object)this.issuer.getSubjectDN(), (Object)this.issuer.getSerialNumber());
    }
    
    X509Certificate selectCertificate(final CertStore store, final Collection<X509CertSelector> selectors) throws CertStoreException {
        for (final CertSelector selector : selectors) {
            AbstractCertStoreInspector.LOGGER.debug("Selecting certificate using {}", (Object)selector);
            final Collection<? extends Certificate> certs = store.getCertificates(selector);
            if (certs.size() > 0) {
                AbstractCertStoreInspector.LOGGER.debug("Selected {} certificate(s) using {}", (Object)certs.size(), (Object)selector);
                return (X509Certificate)certs.iterator().next();
            }
            AbstractCertStoreInspector.LOGGER.debug("No certificates selected");
        }
        return (X509Certificate)store.getCertificates(null).iterator().next();
    }
    
    protected abstract Collection<X509CertSelector> getIssuerSelectors(final byte[] p0);
    
    protected abstract Collection<X509CertSelector> getSignerSelectors();
    
    protected abstract Collection<X509CertSelector> getRecipientSelectors();
    
    @Override
    public final X509Certificate getSigner() {
        return this.signer;
    }
    
    @Override
    public final X509Certificate getRecipient() {
        return this.recipient;
    }
    
    @Override
    public final X509Certificate getIssuer() {
        return this.issuer;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractCertStoreInspector.class);
    }
}
