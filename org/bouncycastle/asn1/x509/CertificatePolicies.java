package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertificatePolicies extends ASN1Object
{
    private final PolicyInformation[] policyInformation;
    
    public static CertificatePolicies getInstance(final Object o) {
        if (o instanceof CertificatePolicies) {
            return (CertificatePolicies)o;
        }
        if (o != null) {
            return new CertificatePolicies(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static CertificatePolicies getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static CertificatePolicies fromExtensions(final Extensions extensions) {
        return getInstance(extensions.getExtensionParsedValue(Extension.certificatePolicies));
    }
    
    public CertificatePolicies(final PolicyInformation policyInformation) {
        this.policyInformation = new PolicyInformation[] { policyInformation };
    }
    
    public CertificatePolicies(final PolicyInformation[] policyInformation) {
        this.policyInformation = policyInformation;
    }
    
    private CertificatePolicies(final ASN1Sequence asn1Sequence) {
        this.policyInformation = new PolicyInformation[asn1Sequence.size()];
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            this.policyInformation[i] = PolicyInformation.getInstance(asn1Sequence.getObjectAt(i));
        }
    }
    
    public PolicyInformation[] getPolicyInformation() {
        final PolicyInformation[] array = new PolicyInformation[this.policyInformation.length];
        System.arraycopy(this.policyInformation, 0, array, 0, this.policyInformation.length);
        return array;
    }
    
    public PolicyInformation getPolicyInformation(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        for (int i = 0; i != this.policyInformation.length; ++i) {
            if (asn1ObjectIdentifier.equals(this.policyInformation[i].getPolicyIdentifier())) {
                return this.policyInformation[i];
            }
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.policyInformation);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.policyInformation.length; ++i) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(this.policyInformation[i]);
        }
        return "CertificatePolicies: [" + (Object)sb + "]";
    }
}
