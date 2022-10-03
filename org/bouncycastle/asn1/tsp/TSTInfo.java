package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class TSTInfo extends ASN1Object
{
    private ASN1Integer version;
    private ASN1ObjectIdentifier tsaPolicyId;
    private MessageImprint messageImprint;
    private ASN1Integer serialNumber;
    private ASN1GeneralizedTime genTime;
    private Accuracy accuracy;
    private ASN1Boolean ordering;
    private ASN1Integer nonce;
    private GeneralName tsa;
    private Extensions extensions;
    
    public static TSTInfo getInstance(final Object o) {
        if (o instanceof TSTInfo) {
            return (TSTInfo)o;
        }
        if (o != null) {
            return new TSTInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private TSTInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.version = ASN1Integer.getInstance(objects.nextElement());
        this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        this.messageImprint = MessageImprint.getInstance(objects.nextElement());
        this.serialNumber = ASN1Integer.getInstance(objects.nextElement());
        this.genTime = ASN1GeneralizedTime.getInstance(objects.nextElement());
        this.ordering = ASN1Boolean.getInstance(false);
        while (objects.hasMoreElements()) {
            final ASN1Object asn1Object = objects.nextElement();
            if (asn1Object instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Object;
                switch (asn1TaggedObject.getTagNo()) {
                    case 0: {
                        this.tsa = GeneralName.getInstance(asn1TaggedObject, true);
                        continue;
                    }
                    case 1: {
                        this.extensions = Extensions.getInstance(asn1TaggedObject, false);
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown tag value " + asn1TaggedObject.getTagNo());
                    }
                }
            }
            else if (asn1Object instanceof ASN1Sequence || asn1Object instanceof Accuracy) {
                this.accuracy = Accuracy.getInstance(asn1Object);
            }
            else if (asn1Object instanceof ASN1Boolean) {
                this.ordering = ASN1Boolean.getInstance(asn1Object);
            }
            else {
                if (!(asn1Object instanceof ASN1Integer)) {
                    continue;
                }
                this.nonce = ASN1Integer.getInstance(asn1Object);
            }
        }
    }
    
    public TSTInfo(final ASN1ObjectIdentifier tsaPolicyId, final MessageImprint messageImprint, final ASN1Integer serialNumber, final ASN1GeneralizedTime genTime, final Accuracy accuracy, final ASN1Boolean ordering, final ASN1Integer nonce, final GeneralName tsa, final Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.tsaPolicyId = tsaPolicyId;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.genTime = genTime;
        this.accuracy = accuracy;
        this.ordering = ordering;
        this.nonce = nonce;
        this.tsa = tsa;
        this.extensions = extensions;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }
    
    public ASN1ObjectIdentifier getPolicy() {
        return this.tsaPolicyId;
    }
    
    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }
    
    public Accuracy getAccuracy() {
        return this.accuracy;
    }
    
    public ASN1GeneralizedTime getGenTime() {
        return this.genTime;
    }
    
    public ASN1Boolean getOrdering() {
        return this.ordering;
    }
    
    public ASN1Integer getNonce() {
        return this.nonce;
    }
    
    public GeneralName getTsa() {
        return this.tsa;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.tsaPolicyId);
        asn1EncodableVector.add(this.messageImprint);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.genTime);
        if (this.accuracy != null) {
            asn1EncodableVector.add(this.accuracy);
        }
        if (this.ordering != null && this.ordering.isTrue()) {
            asn1EncodableVector.add(this.ordering);
        }
        if (this.nonce != null) {
            asn1EncodableVector.add(this.nonce);
        }
        if (this.tsa != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.tsa));
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.extensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
