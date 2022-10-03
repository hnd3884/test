package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class EnvelopedData extends ASN1Object
{
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private EncryptedContentInfo encryptedContentInfo;
    private ASN1Set unprotectedAttrs;
    
    public EnvelopedData(final OriginatorInfo originatorInfo, final ASN1Set recipientInfos, final EncryptedContentInfo encryptedContentInfo, final ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer(calculateVersion(originatorInfo, recipientInfos, unprotectedAttrs));
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }
    
    public EnvelopedData(final OriginatorInfo originatorInfo, final ASN1Set recipientInfos, final EncryptedContentInfo encryptedContentInfo, final Attributes attributes) {
        this.version = new ASN1Integer(calculateVersion(originatorInfo, recipientInfos, ASN1Set.getInstance(attributes)));
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = ASN1Set.getInstance(attributes);
    }
    
    @Deprecated
    public EnvelopedData(final ASN1Sequence asn1Sequence) {
        int n = 0;
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(n++);
        ASN1Encodable asn1Encodable = asn1Sequence.getObjectAt(n++);
        if (asn1Encodable instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)asn1Encodable, false);
            asn1Encodable = asn1Sequence.getObjectAt(n++);
        }
        this.recipientInfos = ASN1Set.getInstance(asn1Encodable);
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(asn1Sequence.getObjectAt(n++));
        if (asn1Sequence.size() > n) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n), false);
        }
    }
    
    public static EnvelopedData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static EnvelopedData getInstance(final Object o) {
        if (o instanceof EnvelopedData) {
            return (EnvelopedData)o;
        }
        if (o != null) {
            return new EnvelopedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }
    
    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }
    
    public ASN1Set getUnprotectedAttrs() {
        return this.unprotectedAttrs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        asn1EncodableVector.add(this.recipientInfos);
        asn1EncodableVector.add(this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.unprotectedAttrs));
        }
        return new BERSequence(asn1EncodableVector);
    }
    
    public static int calculateVersion(final OriginatorInfo originatorInfo, final ASN1Set set, final ASN1Set set2) {
        int n;
        if (originatorInfo != null || set2 != null) {
            n = 2;
        }
        else {
            n = 0;
            final Enumeration objects = set.getObjects();
            while (objects.hasMoreElements()) {
                if (RecipientInfo.getInstance(objects.nextElement()).getVersion().getValue().intValue() != n) {
                    n = 2;
                    break;
                }
            }
        }
        return n;
    }
}
