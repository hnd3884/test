package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Object;

public class Holder extends ASN1Object
{
    public static final int V1_CERTIFICATE_HOLDER = 0;
    public static final int V2_CERTIFICATE_HOLDER = 1;
    IssuerSerial baseCertificateID;
    GeneralNames entityName;
    ObjectDigestInfo objectDigestInfo;
    private int version;
    
    public static Holder getInstance(final Object o) {
        if (o instanceof Holder) {
            return (Holder)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new Holder(ASN1TaggedObject.getInstance(o));
        }
        if (o != null) {
            return new Holder(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private Holder(final ASN1TaggedObject asn1TaggedObject) {
        this.version = 1;
        switch (asn1TaggedObject.getTagNo()) {
            case 0: {
                this.baseCertificateID = IssuerSerial.getInstance(asn1TaggedObject, true);
                break;
            }
            case 1: {
                this.entityName = GeneralNames.getInstance(asn1TaggedObject, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag in Holder");
            }
        }
        this.version = 0;
    }
    
    private Holder(final ASN1Sequence asn1Sequence) {
        this.version = 1;
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(i));
            switch (instance.getTagNo()) {
                case 0: {
                    this.baseCertificateID = IssuerSerial.getInstance(instance, false);
                    break;
                }
                case 1: {
                    this.entityName = GeneralNames.getInstance(instance, false);
                    break;
                }
                case 2: {
                    this.objectDigestInfo = ObjectDigestInfo.getInstance(instance, false);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag in Holder");
                }
            }
        }
        this.version = 1;
    }
    
    public Holder(final IssuerSerial issuerSerial) {
        this(issuerSerial, 1);
    }
    
    public Holder(final IssuerSerial baseCertificateID, final int version) {
        this.version = 1;
        this.baseCertificateID = baseCertificateID;
        this.version = version;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public Holder(final GeneralNames generalNames) {
        this(generalNames, 1);
    }
    
    public Holder(final GeneralNames entityName, final int version) {
        this.version = 1;
        this.entityName = entityName;
        this.version = version;
    }
    
    public Holder(final ObjectDigestInfo objectDigestInfo) {
        this.version = 1;
        this.objectDigestInfo = objectDigestInfo;
    }
    
    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }
    
    public GeneralNames getEntityName() {
        return this.entityName;
    }
    
    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.version == 1) {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            if (this.baseCertificateID != null) {
                asn1EncodableVector.add(new DERTaggedObject(false, 0, this.baseCertificateID));
            }
            if (this.entityName != null) {
                asn1EncodableVector.add(new DERTaggedObject(false, 1, this.entityName));
            }
            if (this.objectDigestInfo != null) {
                asn1EncodableVector.add(new DERTaggedObject(false, 2, this.objectDigestInfo));
            }
            return new DERSequence(asn1EncodableVector);
        }
        if (this.entityName != null) {
            return new DERTaggedObject(true, 1, this.entityName);
        }
        return new DERTaggedObject(true, 0, this.baseCertificateID);
    }
}
