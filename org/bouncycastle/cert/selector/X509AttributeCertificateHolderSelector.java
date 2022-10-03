package org.bouncycastle.cert.selector;

import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Collection;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.util.Selector;

public class X509AttributeCertificateHolderSelector implements Selector
{
    private final AttributeCertificateHolder holder;
    private final AttributeCertificateIssuer issuer;
    private final BigInteger serialNumber;
    private final Date attributeCertificateValid;
    private final X509AttributeCertificateHolder attributeCert;
    private final Collection targetNames;
    private final Collection targetGroups;
    
    X509AttributeCertificateHolderSelector(final AttributeCertificateHolder holder, final AttributeCertificateIssuer issuer, final BigInteger serialNumber, final Date attributeCertificateValid, final X509AttributeCertificateHolder attributeCert, final Collection targetNames, final Collection targetGroups) {
        this.holder = holder;
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.attributeCertificateValid = attributeCertificateValid;
        this.attributeCert = attributeCert;
        this.targetNames = targetNames;
        this.targetGroups = targetGroups;
    }
    
    public boolean match(final Object o) {
        if (!(o instanceof X509AttributeCertificateHolder)) {
            return false;
        }
        final X509AttributeCertificateHolder x509AttributeCertificateHolder = (X509AttributeCertificateHolder)o;
        if (this.attributeCert != null && !this.attributeCert.equals(x509AttributeCertificateHolder)) {
            return false;
        }
        if (this.serialNumber != null && !x509AttributeCertificateHolder.getSerialNumber().equals(this.serialNumber)) {
            return false;
        }
        if (this.holder != null && !x509AttributeCertificateHolder.getHolder().equals(this.holder)) {
            return false;
        }
        if (this.issuer != null && !x509AttributeCertificateHolder.getIssuer().equals(this.issuer)) {
            return false;
        }
        if (this.attributeCertificateValid != null && !x509AttributeCertificateHolder.isValidOn(this.attributeCertificateValid)) {
            return false;
        }
        if (!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) {
            final Extension extension = x509AttributeCertificateHolder.getExtension(Extension.targetInformation);
            if (extension != null) {
                TargetInformation instance;
                try {
                    instance = TargetInformation.getInstance((Object)extension.getParsedValue());
                }
                catch (final IllegalArgumentException ex) {
                    return false;
                }
                final Targets[] targetsObjects = instance.getTargetsObjects();
                if (!this.targetNames.isEmpty()) {
                    boolean b = false;
                    for (int i = 0; i < targetsObjects.length; ++i) {
                        final Target[] targets = targetsObjects[i].getTargets();
                        for (int j = 0; j < targets.length; ++j) {
                            if (this.targetNames.contains(GeneralName.getInstance((Object)targets[j].getTargetName()))) {
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
                            if (this.targetGroups.contains(GeneralName.getInstance((Object)targets2[l].getTargetGroup()))) {
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
        return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups);
    }
    
    public X509AttributeCertificateHolder getAttributeCert() {
        return this.attributeCert;
    }
    
    public Date getAttributeCertificateValid() {
        if (this.attributeCertificateValid != null) {
            return new Date(this.attributeCertificateValid.getTime());
        }
        return null;
    }
    
    public AttributeCertificateHolder getHolder() {
        return this.holder;
    }
    
    public AttributeCertificateIssuer getIssuer() {
        return this.issuer;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    public Collection getTargetNames() {
        return this.targetNames;
    }
    
    public Collection getTargetGroups() {
        return this.targetGroups;
    }
}
