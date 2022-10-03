package org.bouncycastle.cert.path;

import java.util.Collection;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.util.Memoable;

public class CertPathValidationContext implements Memoable
{
    private Set criticalExtensions;
    private Set handledExtensions;
    private boolean endEntity;
    private int index;
    
    public CertPathValidationContext(final Set criticalExtensions) {
        this.handledExtensions = new HashSet();
        this.criticalExtensions = criticalExtensions;
    }
    
    public void addHandledExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this.handledExtensions.add(asn1ObjectIdentifier);
    }
    
    public void setIsEndEntity(final boolean endEntity) {
        this.endEntity = endEntity;
    }
    
    public Set getUnhandledCriticalExtensionOIDs() {
        final HashSet set = new HashSet(this.criticalExtensions);
        set.removeAll(this.handledExtensions);
        return set;
    }
    
    public boolean isEndEntity() {
        return this.endEntity;
    }
    
    public Memoable copy() {
        return null;
    }
    
    public void reset(final Memoable memoable) {
    }
}
