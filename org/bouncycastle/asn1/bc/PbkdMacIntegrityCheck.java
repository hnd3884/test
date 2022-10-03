package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class PbkdMacIntegrityCheck extends ASN1Object
{
    private final AlgorithmIdentifier macAlgorithm;
    private final KeyDerivationFunc pbkdAlgorithm;
    private final ASN1OctetString mac;
    
    public PbkdMacIntegrityCheck(final AlgorithmIdentifier macAlgorithm, final KeyDerivationFunc pbkdAlgorithm, final byte[] array) {
        this.macAlgorithm = macAlgorithm;
        this.pbkdAlgorithm = pbkdAlgorithm;
        this.mac = new DEROctetString(Arrays.clone(array));
    }
    
    private PbkdMacIntegrityCheck(final ASN1Sequence asn1Sequence) {
        this.macAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.pbkdAlgorithm = KeyDerivationFunc.getInstance(asn1Sequence.getObjectAt(1));
        this.mac = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static PbkdMacIntegrityCheck getInstance(final Object o) {
        if (o instanceof PbkdMacIntegrityCheck) {
            return (PbkdMacIntegrityCheck)o;
        }
        if (o != null) {
            return new PbkdMacIntegrityCheck(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }
    
    public KeyDerivationFunc getPbkdAlgorithm() {
        return this.pbkdAlgorithm;
    }
    
    public byte[] getMac() {
        return Arrays.clone(this.mac.getOctets());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.macAlgorithm);
        asn1EncodableVector.add(this.pbkdAlgorithm);
        asn1EncodableVector.add(this.mac);
        return new DERSequence(asn1EncodableVector);
    }
}
