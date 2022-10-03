package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Object;

public class MetaData extends ASN1Object
{
    private ASN1Boolean hashProtected;
    private DERUTF8String fileName;
    private DERIA5String mediaType;
    private Attributes otherMetaData;
    
    public MetaData(final ASN1Boolean hashProtected, final DERUTF8String fileName, final DERIA5String mediaType, final Attributes otherMetaData) {
        this.hashProtected = hashProtected;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.otherMetaData = otherMetaData;
    }
    
    private MetaData(final ASN1Sequence asn1Sequence) {
        this.hashProtected = ASN1Boolean.getInstance(asn1Sequence.getObjectAt(0));
        int n = 1;
        if (n < asn1Sequence.size() && asn1Sequence.getObjectAt(n) instanceof DERUTF8String) {
            this.fileName = DERUTF8String.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (n < asn1Sequence.size() && asn1Sequence.getObjectAt(n) instanceof DERIA5String) {
            this.mediaType = DERIA5String.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (n < asn1Sequence.size()) {
            this.otherMetaData = Attributes.getInstance(asn1Sequence.getObjectAt(n++));
        }
    }
    
    public static MetaData getInstance(final Object o) {
        if (o instanceof MetaData) {
            return (MetaData)o;
        }
        if (o != null) {
            return new MetaData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.hashProtected);
        if (this.fileName != null) {
            asn1EncodableVector.add(this.fileName);
        }
        if (this.mediaType != null) {
            asn1EncodableVector.add(this.mediaType);
        }
        if (this.otherMetaData != null) {
            asn1EncodableVector.add(this.otherMetaData);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public boolean isHashProtected() {
        return this.hashProtected.isTrue();
    }
    
    public DERUTF8String getFileName() {
        return this.fileName;
    }
    
    public DERIA5String getMediaType() {
        return this.mediaType;
    }
    
    public Attributes getOtherMetaData() {
        return this.otherMetaData;
    }
}
