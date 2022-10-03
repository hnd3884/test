package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class TimeStampReq extends ASN1Object
{
    ASN1Integer version;
    MessageImprint messageImprint;
    ASN1ObjectIdentifier tsaPolicy;
    ASN1Integer nonce;
    ASN1Boolean certReq;
    Extensions extensions;
    
    public static TimeStampReq getInstance(final Object o) {
        if (o instanceof TimeStampReq) {
            return (TimeStampReq)o;
        }
        if (o != null) {
            return new TimeStampReq(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private TimeStampReq(final ASN1Sequence asn1Sequence) {
        final int size = asn1Sequence.size();
        int n = 0;
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(n));
        ++n;
        this.messageImprint = MessageImprint.getInstance(asn1Sequence.getObjectAt(n));
        for (int i = ++n; i < size; ++i) {
            if (asn1Sequence.getObjectAt(i) instanceof ASN1ObjectIdentifier) {
                this.tsaPolicy = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(i));
            }
            else if (asn1Sequence.getObjectAt(i) instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance(asn1Sequence.getObjectAt(i));
            }
            else if (asn1Sequence.getObjectAt(i) instanceof ASN1Boolean) {
                this.certReq = ASN1Boolean.getInstance(asn1Sequence.getObjectAt(i));
            }
            else if (asn1Sequence.getObjectAt(i) instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(i);
                if (asn1TaggedObject.getTagNo() == 0) {
                    this.extensions = Extensions.getInstance(asn1TaggedObject, false);
                }
            }
        }
    }
    
    public TimeStampReq(final MessageImprint messageImprint, final ASN1ObjectIdentifier tsaPolicy, final ASN1Integer nonce, final ASN1Boolean certReq, final Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.messageImprint = messageImprint;
        this.tsaPolicy = tsaPolicy;
        this.nonce = nonce;
        this.certReq = certReq;
        this.extensions = extensions;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }
    
    public ASN1ObjectIdentifier getReqPolicy() {
        return this.tsaPolicy;
    }
    
    public ASN1Integer getNonce() {
        return this.nonce;
    }
    
    public ASN1Boolean getCertReq() {
        return this.certReq;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.messageImprint);
        if (this.tsaPolicy != null) {
            asn1EncodableVector.add(this.tsaPolicy);
        }
        if (this.nonce != null) {
            asn1EncodableVector.add(this.nonce);
        }
        if (this.certReq != null && this.certReq.isTrue()) {
            asn1EncodableVector.add(this.certReq);
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.extensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
