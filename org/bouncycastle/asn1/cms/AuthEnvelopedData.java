package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class AuthEnvelopedData extends ASN1Object
{
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private EncryptedContentInfo authEncryptedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;
    
    public AuthEnvelopedData(final OriginatorInfo originatorInfo, final ASN1Set recipientInfos, final EncryptedContentInfo authEncryptedContentInfo, final ASN1Set authAttrs, final ASN1OctetString mac, final ASN1Set unauthAttrs) {
        this.version = new ASN1Integer(0L);
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        this.authEncryptedContentInfo = authEncryptedContentInfo;
        this.authAttrs = authAttrs;
        if (!authEncryptedContentInfo.getContentType().equals(CMSObjectIdentifiers.data) && (authAttrs == null || authAttrs.size() == 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }
    
    private AuthEnvelopedData(final ASN1Sequence asn1Sequence) {
        int n = 0;
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(n++).toASN1Primitive();
        if (this.version.getValue().intValue() != 0) {
            throw new IllegalArgumentException("AuthEnvelopedData version number must be 0");
        }
        ASN1Primitive asn1Primitive = asn1Sequence.getObjectAt(n++).toASN1Primitive();
        if (asn1Primitive instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)asn1Primitive, false);
            asn1Primitive = asn1Sequence.getObjectAt(n++).toASN1Primitive();
        }
        this.recipientInfos = ASN1Set.getInstance(asn1Primitive);
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(asn1Sequence.getObjectAt(n++).toASN1Primitive());
        ASN1Primitive asn1Primitive2 = asn1Sequence.getObjectAt(n++).toASN1Primitive();
        if (asn1Primitive2 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Primitive2, false);
            asn1Primitive2 = asn1Sequence.getObjectAt(n++).toASN1Primitive();
        }
        else if (!this.authEncryptedContentInfo.getContentType().equals(CMSObjectIdentifiers.data) && (this.authAttrs == null || this.authAttrs.size() == 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = ASN1OctetString.getInstance(asn1Primitive2);
        if (asn1Sequence.size() > n) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n).toASN1Primitive(), false);
        }
    }
    
    public static AuthEnvelopedData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AuthEnvelopedData getInstance(final Object o) {
        if (o == null || o instanceof AuthEnvelopedData) {
            return (AuthEnvelopedData)o;
        }
        if (o instanceof ASN1Sequence) {
            return new AuthEnvelopedData((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("Invalid AuthEnvelopedData: " + o.getClass().getName());
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
    
    public EncryptedContentInfo getAuthEncryptedContentInfo() {
        return this.authEncryptedContentInfo;
    }
    
    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }
    
    public ASN1OctetString getMac() {
        return this.mac;
    }
    
    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        asn1EncodableVector.add(this.recipientInfos);
        asn1EncodableVector.add(this.authEncryptedContentInfo);
        if (this.authAttrs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.authAttrs));
        }
        asn1EncodableVector.add(this.mac);
        if (this.unauthAttrs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.unauthAttrs));
        }
        return new BERSequence(asn1EncodableVector);
    }
}
