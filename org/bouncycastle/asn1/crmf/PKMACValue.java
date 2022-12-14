package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PKMACValue extends ASN1Object
{
    private AlgorithmIdentifier algId;
    private DERBitString value;
    
    private PKMACValue(final ASN1Sequence asn1Sequence) {
        this.algId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.value = DERBitString.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static PKMACValue getInstance(final Object o) {
        if (o instanceof PKMACValue) {
            return (PKMACValue)o;
        }
        if (o != null) {
            return new PKMACValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static PKMACValue getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public PKMACValue(final PBMParameter pbmParameter, final DERBitString derBitString) {
        this(new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, pbmParameter), derBitString);
    }
    
    public PKMACValue(final AlgorithmIdentifier algId, final DERBitString value) {
        this.algId = algId;
        this.value = value;
    }
    
    public AlgorithmIdentifier getAlgId() {
        return this.algId;
    }
    
    public DERBitString getValue() {
        return this.value;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.algId);
        asn1EncodableVector.add(this.value);
        return new DERSequence(asn1EncodableVector);
    }
}
