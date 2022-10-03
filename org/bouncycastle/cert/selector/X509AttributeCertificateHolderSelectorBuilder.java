package org.bouncycastle.cert.selector;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.HashSet;
import java.util.Collection;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.AttributeCertificateHolder;

public class X509AttributeCertificateHolderSelectorBuilder
{
    private AttributeCertificateHolder holder;
    private AttributeCertificateIssuer issuer;
    private BigInteger serialNumber;
    private Date attributeCertificateValid;
    private X509AttributeCertificateHolder attributeCert;
    private Collection targetNames;
    private Collection targetGroups;
    
    public X509AttributeCertificateHolderSelectorBuilder() {
        this.targetNames = new HashSet();
        this.targetGroups = new HashSet();
    }
    
    public void setAttributeCert(final X509AttributeCertificateHolder attributeCert) {
        this.attributeCert = attributeCert;
    }
    
    public void setAttributeCertificateValid(final Date date) {
        if (date != null) {
            this.attributeCertificateValid = new Date(date.getTime());
        }
        else {
            this.attributeCertificateValid = null;
        }
    }
    
    public void setHolder(final AttributeCertificateHolder holder) {
        this.holder = holder;
    }
    
    public void setIssuer(final AttributeCertificateIssuer issuer) {
        this.issuer = issuer;
    }
    
    public void setSerialNumber(final BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void addTargetName(final GeneralName generalName) {
        this.targetNames.add(generalName);
    }
    
    public void setTargetNames(final Collection collection) throws IOException {
        this.targetNames = this.extractGeneralNames(collection);
    }
    
    public void addTargetGroup(final GeneralName generalName) {
        this.targetGroups.add(generalName);
    }
    
    public void setTargetGroups(final Collection collection) throws IOException {
        this.targetGroups = this.extractGeneralNames(collection);
    }
    
    private Set extractGeneralNames(final Collection collection) throws IOException {
        if (collection == null || collection.isEmpty()) {
            return new HashSet();
        }
        final HashSet set = new HashSet();
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            set.add(GeneralName.getInstance(iterator.next()));
        }
        return set;
    }
    
    public X509AttributeCertificateHolderSelector build() {
        return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, Collections.unmodifiableCollection((Collection<?>)new HashSet<Object>(this.targetNames)), Collections.unmodifiableCollection((Collection<?>)new HashSet<Object>(this.targetGroups)));
    }
}
