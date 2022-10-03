package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class AuthenticatedSafe extends ASN1Object
{
    private ContentInfo[] info;
    private boolean isBer;
    
    private AuthenticatedSafe(final ASN1Sequence asn1Sequence) {
        this.isBer = true;
        this.info = new ContentInfo[asn1Sequence.size()];
        for (int i = 0; i != this.info.length; ++i) {
            this.info[i] = ContentInfo.getInstance(asn1Sequence.getObjectAt(i));
        }
        this.isBer = (asn1Sequence instanceof BERSequence);
    }
    
    public static AuthenticatedSafe getInstance(final Object o) {
        if (o instanceof AuthenticatedSafe) {
            return (AuthenticatedSafe)o;
        }
        if (o != null) {
            return new AuthenticatedSafe(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AuthenticatedSafe(final ContentInfo[] info) {
        this.isBer = true;
        this.info = info;
    }
    
    public ContentInfo[] getContentInfo() {
        return this.info;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.info.length; ++i) {
            asn1EncodableVector.add(this.info[i]);
        }
        if (this.isBer) {
            return new BERSequence(asn1EncodableVector);
        }
        return new DLSequence(asn1EncodableVector);
    }
}
