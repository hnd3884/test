package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedData extends ASN1Object
{
    private ASN1Integer version;
    private EncryptedContentInfo encryptedContentInfo;
    private ASN1Set unprotectedAttrs;
    
    public static EncryptedData getInstance(final Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData)o;
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public EncryptedData(final EncryptedContentInfo encryptedContentInfo) {
        this(encryptedContentInfo, null);
    }
    
    public EncryptedData(final EncryptedContentInfo encryptedContentInfo, final ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer((unprotectedAttrs == null) ? 0L : 2L);
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }
    
    private EncryptedData(final ASN1Sequence asn1Sequence) {
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() == 3) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(2), false);
        }
    }
    
    public ASN1Integer getVersion() {
        return this.version;
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
        asn1EncodableVector.add(this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            asn1EncodableVector.add(new BERTaggedObject(false, 1, this.unprotectedAttrs));
        }
        return new BERSequence(asn1EncodableVector);
    }
}
