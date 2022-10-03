package org.bouncycastle.cert.path.validations;

import org.bouncycastle.util.Memoable;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Store;
import org.bouncycastle.cert.path.CertPathValidation;

public class CRLValidation implements CertPathValidation
{
    private Store crls;
    private X500Name workingIssuerName;
    
    public CRLValidation(final X500Name workingIssuerName, final Store crls) {
        this.workingIssuerName = workingIssuerName;
        this.crls = crls;
    }
    
    public void validate(final CertPathValidationContext certPathValidationContext, final X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        final Collection matches = this.crls.getMatches((Selector)new Selector() {
            public boolean match(final Object o) {
                return ((X509CRLHolder)o).getIssuer().equals((Object)CRLValidation.this.workingIssuerName);
            }
            
            public Object clone() {
                return this;
            }
        });
        if (matches.isEmpty()) {
            throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found");
        }
        final Iterator iterator = matches.iterator();
        while (iterator.hasNext()) {
            if (((X509CRLHolder)iterator.next()).getRevokedCertificate(x509CertificateHolder.getSerialNumber()) != null) {
                throw new CertPathValidationException("Certificate revoked");
            }
        }
        this.workingIssuerName = x509CertificateHolder.getSubject();
    }
    
    public Memoable copy() {
        return (Memoable)new CRLValidation(this.workingIssuerName, this.crls);
    }
    
    public void reset(final Memoable memoable) {
        final CRLValidation crlValidation = (CRLValidation)memoable;
        this.workingIssuerName = crlValidation.workingIssuerName;
        this.crls = crlValidation.crls;
    }
}
