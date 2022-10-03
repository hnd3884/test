package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class SignedData extends ASN1Object implements PKCSObjectIdentifiers
{
    private ASN1Integer version;
    private ASN1Set digestAlgorithms;
    private ContentInfo contentInfo;
    private ASN1Set certificates;
    private ASN1Set crls;
    private ASN1Set signerInfos;
    
    public static SignedData getInstance(final Object o) {
        if (o instanceof SignedData) {
            return (SignedData)o;
        }
        if (o != null) {
            return new SignedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public SignedData(final ASN1Integer version, final ASN1Set digestAlgorithms, final ContentInfo contentInfo, final ASN1Set certificates, final ASN1Set crls, final ASN1Set signerInfos) {
        this.version = version;
        this.digestAlgorithms = digestAlgorithms;
        this.contentInfo = contentInfo;
        this.certificates = certificates;
        this.crls = crls;
        this.signerInfos = signerInfos;
    }
    
    public SignedData(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.version = (ASN1Integer)objects.nextElement();
        this.digestAlgorithms = (ASN1Set)objects.nextElement();
        this.contentInfo = ContentInfo.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final ASN1Primitive asn1Primitive = objects.nextElement();
            if (asn1Primitive instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Primitive;
                switch (asn1TaggedObject.getTagNo()) {
                    case 0: {
                        this.certificates = ASN1Set.getInstance(asn1TaggedObject, false);
                        continue;
                    }
                    case 1: {
                        this.crls = ASN1Set.getInstance(asn1TaggedObject, false);
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException("unknown tag value " + asn1TaggedObject.getTagNo());
                    }
                }
            }
            else {
                this.signerInfos = (ASN1Set)asn1Primitive;
            }
        }
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public ASN1Set getDigestAlgorithms() {
        return this.digestAlgorithms;
    }
    
    public ContentInfo getContentInfo() {
        return this.contentInfo;
    }
    
    public ASN1Set getCertificates() {
        return this.certificates;
    }
    
    public ASN1Set getCRLs() {
        return this.crls;
    }
    
    public ASN1Set getSignerInfos() {
        return this.signerInfos;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.digestAlgorithms);
        asn1EncodableVector.add(this.contentInfo);
        if (this.certificates != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.certificates));
        }
        if (this.crls != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.crls));
        }
        asn1EncodableVector.add(this.signerInfos);
        return new BERSequence(asn1EncodableVector);
    }
}
