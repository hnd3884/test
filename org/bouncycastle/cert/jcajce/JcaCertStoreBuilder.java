package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Collection;
import java.security.GeneralSecurityException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.Provider;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.util.ArrayList;
import java.util.List;

public class JcaCertStoreBuilder
{
    private List certs;
    private List crls;
    private Object provider;
    private JcaX509CertificateConverter certificateConverter;
    private JcaX509CRLConverter crlConverter;
    private String type;
    
    public JcaCertStoreBuilder() {
        this.certs = new ArrayList();
        this.crls = new ArrayList();
        this.certificateConverter = new JcaX509CertificateConverter();
        this.crlConverter = new JcaX509CRLConverter();
        this.type = "Collection";
    }
    
    public JcaCertStoreBuilder addCertificates(final Store store) {
        this.certs.addAll(store.getMatches((Selector)null));
        return this;
    }
    
    public JcaCertStoreBuilder addCertificate(final X509CertificateHolder x509CertificateHolder) {
        this.certs.add(x509CertificateHolder);
        return this;
    }
    
    public JcaCertStoreBuilder addCRLs(final Store store) {
        this.crls.addAll(store.getMatches((Selector)null));
        return this;
    }
    
    public JcaCertStoreBuilder addCRL(final X509CRLHolder x509CRLHolder) {
        this.crls.add(x509CRLHolder);
        return this;
    }
    
    public JcaCertStoreBuilder setProvider(final String provider) {
        this.certificateConverter.setProvider(provider);
        this.crlConverter.setProvider(provider);
        this.provider = provider;
        return this;
    }
    
    public JcaCertStoreBuilder setProvider(final Provider provider) {
        this.certificateConverter.setProvider(provider);
        this.crlConverter.setProvider(provider);
        this.provider = provider;
        return this;
    }
    
    public JcaCertStoreBuilder setType(final String type) {
        this.type = type;
        return this;
    }
    
    public CertStore build() throws GeneralSecurityException {
        final CollectionCertStoreParameters convertHolders = this.convertHolders(this.certificateConverter, this.crlConverter);
        if (this.provider instanceof String) {
            return CertStore.getInstance(this.type, convertHolders, (String)this.provider);
        }
        if (this.provider instanceof Provider) {
            return CertStore.getInstance(this.type, convertHolders, (Provider)this.provider);
        }
        return CertStore.getInstance(this.type, convertHolders);
    }
    
    private CollectionCertStoreParameters convertHolders(final JcaX509CertificateConverter jcaX509CertificateConverter, final JcaX509CRLConverter jcaX509CRLConverter) throws CertificateException, CRLException {
        final ArrayList list = new ArrayList(this.certs.size() + this.crls.size());
        final Iterator iterator = this.certs.iterator();
        while (iterator.hasNext()) {
            list.add(jcaX509CertificateConverter.getCertificate((X509CertificateHolder)iterator.next()));
        }
        final Iterator iterator2 = this.crls.iterator();
        while (iterator2.hasNext()) {
            list.add(jcaX509CRLConverter.getCRL((X509CRLHolder)iterator2.next()));
        }
        return new CollectionCertStoreParameters(list);
    }
}
