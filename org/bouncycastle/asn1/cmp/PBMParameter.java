package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class PBMParameter extends ASN1Object
{
    private ASN1OctetString salt;
    private AlgorithmIdentifier owf;
    private ASN1Integer iterationCount;
    private AlgorithmIdentifier mac;
    
    private PBMParameter(final ASN1Sequence asn1Sequence) {
        this.salt = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0));
        this.owf = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.iterationCount = ASN1Integer.getInstance(asn1Sequence.getObjectAt(2));
        this.mac = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(3));
    }
    
    public static PBMParameter getInstance(final Object o) {
        if (o instanceof PBMParameter) {
            return (PBMParameter)o;
        }
        if (o != null) {
            return new PBMParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PBMParameter(final byte[] array, final AlgorithmIdentifier algorithmIdentifier, final int n, final AlgorithmIdentifier algorithmIdentifier2) {
        this(new DEROctetString(array), algorithmIdentifier, new ASN1Integer(n), algorithmIdentifier2);
    }
    
    public PBMParameter(final ASN1OctetString salt, final AlgorithmIdentifier owf, final ASN1Integer iterationCount, final AlgorithmIdentifier mac) {
        this.salt = salt;
        this.owf = owf;
        this.iterationCount = iterationCount;
        this.mac = mac;
    }
    
    public ASN1OctetString getSalt() {
        return this.salt;
    }
    
    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }
    
    public ASN1Integer getIterationCount() {
        return this.iterationCount;
    }
    
    public AlgorithmIdentifier getMac() {
        return this.mac;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.salt);
        asn1EncodableVector.add(this.owf);
        asn1EncodableVector.add(this.iterationCount);
        asn1EncodableVector.add(this.mac);
        return new DERSequence(asn1EncodableVector);
    }
}
