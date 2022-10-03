package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class Pfx extends ASN1Object implements PKCSObjectIdentifiers
{
    private ContentInfo contentInfo;
    private MacData macData;
    
    private Pfx(final ASN1Sequence asn1Sequence) {
        this.macData = null;
        if (ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue().intValue() != 3) {
            throw new IllegalArgumentException("wrong version for PFX PDU");
        }
        this.contentInfo = ContentInfo.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() == 3) {
            this.macData = MacData.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public static Pfx getInstance(final Object o) {
        if (o instanceof Pfx) {
            return (Pfx)o;
        }
        if (o != null) {
            return new Pfx(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Pfx(final ContentInfo contentInfo, final MacData macData) {
        this.macData = null;
        this.contentInfo = contentInfo;
        this.macData = macData;
    }
    
    public ContentInfo getAuthSafe() {
        return this.contentInfo;
    }
    
    public MacData getMacData() {
        return this.macData;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(3L));
        asn1EncodableVector.add(this.contentInfo);
        if (this.macData != null) {
            asn1EncodableVector.add(this.macData);
        }
        return new BERSequence(asn1EncodableVector);
    }
}
