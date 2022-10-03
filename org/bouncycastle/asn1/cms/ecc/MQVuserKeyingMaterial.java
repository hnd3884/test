package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.ASN1Object;

public class MQVuserKeyingMaterial extends ASN1Object
{
    private OriginatorPublicKey ephemeralPublicKey;
    private ASN1OctetString addedukm;
    
    public MQVuserKeyingMaterial(final OriginatorPublicKey ephemeralPublicKey, final ASN1OctetString addedukm) {
        if (ephemeralPublicKey == null) {
            throw new IllegalArgumentException("Ephemeral public key cannot be null");
        }
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.addedukm = addedukm;
    }
    
    private MQVuserKeyingMaterial(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 1 && asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Sequence has incorrect number of elements");
        }
        this.ephemeralPublicKey = OriginatorPublicKey.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.addedukm = ASN1OctetString.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(1), true);
        }
    }
    
    public static MQVuserKeyingMaterial getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static MQVuserKeyingMaterial getInstance(final Object o) {
        if (o instanceof MQVuserKeyingMaterial) {
            return (MQVuserKeyingMaterial)o;
        }
        if (o != null) {
            return new MQVuserKeyingMaterial(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public OriginatorPublicKey getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }
    
    public ASN1OctetString getAddedukm() {
        return this.addedukm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.ephemeralPublicKey);
        if (this.addedukm != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.addedukm));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
