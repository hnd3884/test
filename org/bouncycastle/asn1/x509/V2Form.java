package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Object;

public class V2Form extends ASN1Object
{
    GeneralNames issuerName;
    IssuerSerial baseCertificateID;
    ObjectDigestInfo objectDigestInfo;
    
    public static V2Form getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static V2Form getInstance(final Object o) {
        if (o instanceof V2Form) {
            return (V2Form)o;
        }
        if (o != null) {
            return new V2Form(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public V2Form(final GeneralNames generalNames) {
        this(generalNames, null, null);
    }
    
    public V2Form(final GeneralNames generalNames, final IssuerSerial issuerSerial) {
        this(generalNames, issuerSerial, null);
    }
    
    public V2Form(final GeneralNames generalNames, final ObjectDigestInfo objectDigestInfo) {
        this(generalNames, null, objectDigestInfo);
    }
    
    public V2Form(final GeneralNames issuerName, final IssuerSerial baseCertificateID, final ObjectDigestInfo objectDigestInfo) {
        this.issuerName = issuerName;
        this.baseCertificateID = baseCertificateID;
        this.objectDigestInfo = objectDigestInfo;
    }
    
    @Deprecated
    public V2Form(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        int n = 0;
        if (!(asn1Sequence.getObjectAt(0) instanceof ASN1TaggedObject)) {
            ++n;
            this.issuerName = GeneralNames.getInstance(asn1Sequence.getObjectAt(0));
        }
        for (int i = n; i != asn1Sequence.size(); ++i) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(i));
            if (instance.getTagNo() == 0) {
                this.baseCertificateID = IssuerSerial.getInstance(instance, false);
            }
            else {
                if (instance.getTagNo() != 1) {
                    throw new IllegalArgumentException("Bad tag number: " + instance.getTagNo());
                }
                this.objectDigestInfo = ObjectDigestInfo.getInstance(instance, false);
            }
        }
    }
    
    public GeneralNames getIssuerName() {
        return this.issuerName;
    }
    
    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }
    
    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.issuerName != null) {
            asn1EncodableVector.add(this.issuerName);
        }
        if (this.baseCertificateID != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.baseCertificateID));
        }
        if (this.objectDigestInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.objectDigestInfo));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
