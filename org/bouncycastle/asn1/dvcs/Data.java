package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class Data extends ASN1Object implements ASN1Choice
{
    private ASN1OctetString message;
    private DigestInfo messageImprint;
    private ASN1Sequence certs;
    
    public Data(final byte[] array) {
        this.message = new DEROctetString(array);
    }
    
    public Data(final ASN1OctetString message) {
        this.message = message;
    }
    
    public Data(final DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }
    
    public Data(final TargetEtcChain targetEtcChain) {
        this.certs = new DERSequence(targetEtcChain);
    }
    
    public Data(final TargetEtcChain[] array) {
        this.certs = new DERSequence(array);
    }
    
    private Data(final ASN1Sequence certs) {
        this.certs = certs;
    }
    
    public static Data getInstance(final Object o) {
        if (o instanceof Data) {
            return (Data)o;
        }
        if (o instanceof ASN1OctetString) {
            return new Data((ASN1OctetString)o);
        }
        if (o instanceof ASN1Sequence) {
            return new Data(DigestInfo.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject) {
            return new Data(ASN1Sequence.getInstance((ASN1TaggedObject)o, false));
        }
        throw new IllegalArgumentException("Unknown object submitted to getInstance: " + o.getClass().getName());
    }
    
    public static Data getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.message != null) {
            return this.message.toASN1Primitive();
        }
        if (this.messageImprint != null) {
            return this.messageImprint.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.certs);
    }
    
    @Override
    public String toString() {
        if (this.message != null) {
            return "Data {\n" + this.message + "}\n";
        }
        if (this.messageImprint != null) {
            return "Data {\n" + this.messageImprint + "}\n";
        }
        return "Data {\n" + this.certs + "}\n";
    }
    
    public ASN1OctetString getMessage() {
        return this.message;
    }
    
    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }
    
    public TargetEtcChain[] getCerts() {
        if (this.certs == null) {
            return null;
        }
        final TargetEtcChain[] array = new TargetEtcChain[this.certs.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = TargetEtcChain.getInstance(this.certs.getObjectAt(i));
        }
        return array;
    }
}
