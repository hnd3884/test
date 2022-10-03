package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class TaggedRequest extends ASN1Object implements ASN1Choice
{
    public static final int TCR = 0;
    public static final int CRM = 1;
    public static final int ORM = 2;
    private final int tagNo;
    private final ASN1Encodable value;
    
    public TaggedRequest(final TaggedCertificationRequest value) {
        this.tagNo = 0;
        this.value = value;
    }
    
    public TaggedRequest(final CertReqMsg value) {
        this.tagNo = 1;
        this.value = value;
    }
    
    private TaggedRequest(final ASN1Sequence value) {
        this.tagNo = 2;
        this.value = value;
    }
    
    public static TaggedRequest getInstance(final Object o) {
        if (o instanceof TaggedRequest) {
            return (TaggedRequest)o;
        }
        if (o == null) {
            return null;
        }
        if (!(o instanceof ASN1Encodable)) {
            if (o instanceof byte[]) {
                try {
                    return getInstance(ASN1Primitive.fromByteArray((byte[])o));
                }
                catch (final IOException ex) {
                    throw new IllegalArgumentException("unknown encoding in getInstance()");
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
        }
        final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(((ASN1Encodable)o).toASN1Primitive());
        switch (instance.getTagNo()) {
            case 0: {
                return new TaggedRequest(TaggedCertificationRequest.getInstance(instance, false));
            }
            case 1: {
                return new TaggedRequest(CertReqMsg.getInstance(instance, false));
            }
            case 2: {
                return new TaggedRequest(ASN1Sequence.getInstance(instance, false));
            }
            default: {
                throw new IllegalArgumentException("unknown tag in getInstance(): " + instance.getTagNo());
            }
        }
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public ASN1Encodable getValue() {
        return this.value;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.value);
    }
}
