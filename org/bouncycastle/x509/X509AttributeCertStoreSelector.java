package org.bouncycastle.x509;

import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.asn1.x509.GeneralName;
import java.io.IOException;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.HashSet;
import java.util.Collection;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.util.Selector;

public class X509AttributeCertStoreSelector implements Selector
{
    private AttributeCertificateHolder holder;
    private AttributeCertificateIssuer issuer;
    private BigInteger serialNumber;
    private Date attributeCertificateValid;
    private X509AttributeCertificate attributeCert;
    private Collection targetNames;
    private Collection targetGroups;
    
    public X509AttributeCertStoreSelector() {
        this.targetNames = new HashSet();
        this.targetGroups = new HashSet();
    }
    
    public boolean match(final Object o) {
        if (!(o instanceof X509AttributeCertificate)) {
            return false;
        }
        final X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)o;
        if (this.attributeCert != null && !this.attributeCert.equals(x509AttributeCertificate)) {
            return false;
        }
        if (this.serialNumber != null && !x509AttributeCertificate.getSerialNumber().equals(this.serialNumber)) {
            return false;
        }
        if (this.holder != null && !x509AttributeCertificate.getHolder().equals(this.holder)) {
            return false;
        }
        if (this.issuer != null && !x509AttributeCertificate.getIssuer().equals(this.issuer)) {
            return false;
        }
        if (this.attributeCertificateValid != null) {
            try {
                x509AttributeCertificate.checkValidity(this.attributeCertificateValid);
            }
            catch (final CertificateExpiredException ex) {
                return false;
            }
            catch (final CertificateNotYetValidException ex2) {
                return false;
            }
        }
        if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
            final byte[] extensionValue = x509AttributeCertificate.getExtensionValue(X509Extensions.TargetInformation.getId());
            if (extensionValue != null) {
                TargetInformation instance;
                try {
                    instance = TargetInformation.getInstance(new ASN1InputStream(((DEROctetString)ASN1Primitive.fromByteArray(extensionValue)).getOctets()).readObject());
                }
                catch (final IOException ex3) {
                    return false;
                }
                catch (final IllegalArgumentException ex4) {
                    return false;
                }
                final Targets[] targetsObjects = instance.getTargetsObjects();
                if (!this.targetNames.isEmpty()) {
                    boolean b = false;
                    for (int i = 0; i < targetsObjects.length; ++i) {
                        final Target[] targets = targetsObjects[i].getTargets();
                        for (int j = 0; j < targets.length; ++j) {
                            if (this.targetNames.contains(GeneralName.getInstance(targets[j].getTargetName()))) {
                                b = true;
                                break;
                            }
                        }
                    }
                    if (!b) {
                        return false;
                    }
                }
                if (!this.targetGroups.isEmpty()) {
                    boolean b2 = false;
                    for (int k = 0; k < targetsObjects.length; ++k) {
                        final Target[] targets2 = targetsObjects[k].getTargets();
                        for (int l = 0; l < targets2.length; ++l) {
                            if (this.targetGroups.contains(GeneralName.getInstance(targets2[l].getTargetGroup()))) {
                                b2 = true;
                                break;
                            }
                        }
                    }
                    if (!b2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public Object clone() {
        final X509AttributeCertStoreSelector x509AttributeCertStoreSelector = new X509AttributeCertStoreSelector();
        x509AttributeCertStoreSelector.attributeCert = this.attributeCert;
        x509AttributeCertStoreSelector.attributeCertificateValid = this.getAttributeCertificateValid();
        x509AttributeCertStoreSelector.holder = this.holder;
        x509AttributeCertStoreSelector.issuer = this.issuer;
        x509AttributeCertStoreSelector.serialNumber = this.serialNumber;
        x509AttributeCertStoreSelector.targetGroups = this.getTargetGroups();
        x509AttributeCertStoreSelector.targetNames = this.getTargetNames();
        return x509AttributeCertStoreSelector;
    }
    
    public X509AttributeCertificate getAttributeCert() {
        return this.attributeCert;
    }
    
    public void setAttributeCert(final X509AttributeCertificate attributeCert) {
        this.attributeCert = attributeCert;
    }
    
    public Date getAttributeCertificateValid() {
        if (this.attributeCertificateValid != null) {
            return new Date(this.attributeCertificateValid.getTime());
        }
        return null;
    }
    
    public void setAttributeCertificateValid(final Date date) {
        if (date != null) {
            this.attributeCertificateValid = new Date(date.getTime());
        }
        else {
            this.attributeCertificateValid = null;
        }
    }
    
    public AttributeCertificateHolder getHolder() {
        return this.holder;
    }
    
    public void setHolder(final AttributeCertificateHolder holder) {
        this.holder = holder;
    }
    
    public AttributeCertificateIssuer getIssuer() {
        return this.issuer;
    }
    
    public void setIssuer(final AttributeCertificateIssuer issuer) {
        this.issuer = issuer;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void addTargetName(final GeneralName generalName) {
        this.targetNames.add(generalName);
    }
    
    public void addTargetName(final byte[] array) throws IOException {
        this.addTargetName(GeneralName.getInstance(ASN1Primitive.fromByteArray(array)));
    }
    
    public void setTargetNames(final Collection collection) throws IOException {
        this.targetNames = this.extractGeneralNames(collection);
    }
    
    public Collection getTargetNames() {
        return Collections.unmodifiableCollection((Collection<?>)this.targetNames);
    }
    
    public void addTargetGroup(final GeneralName generalName) {
        this.targetGroups.add(generalName);
    }
    
    public void addTargetGroup(final byte[] array) throws IOException {
        this.addTargetGroup(GeneralName.getInstance(ASN1Primitive.fromByteArray(array)));
    }
    
    public void setTargetGroups(final Collection collection) throws IOException {
        this.targetGroups = this.extractGeneralNames(collection);
    }
    
    public Collection getTargetGroups() {
        return Collections.unmodifiableCollection((Collection<?>)this.targetGroups);
    }
    
    private Set extractGeneralNames(final Collection collection) throws IOException {
        if (collection == null || collection.isEmpty()) {
            return new HashSet();
        }
        final HashSet set = new HashSet();
        for (final Object next : collection) {
            if (next instanceof GeneralName) {
                set.add(next);
            }
            else {
                set.add(GeneralName.getInstance(ASN1Primitive.fromByteArray((byte[])next)));
            }
        }
        return set;
    }
}
