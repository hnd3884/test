package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedSecretKeyData extends ASN1Object
{
    private final AlgorithmIdentifier keyEncryptionAlgorithm;
    private final ASN1OctetString encryptedKeyData;
    
    public EncryptedSecretKeyData(final AlgorithmIdentifier keyEncryptionAlgorithm, final byte[] array) {
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKeyData = new DEROctetString(Arrays.clone(array));
    }
    
    private EncryptedSecretKeyData(final ASN1Sequence asn1Sequence) {
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.encryptedKeyData = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static EncryptedSecretKeyData getInstance(final Object o) {
        if (o instanceof EncryptedSecretKeyData) {
            return (EncryptedSecretKeyData)o;
        }
        if (o != null) {
            return new EncryptedSecretKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }
    
    public byte[] getEncryptedKeyData() {
        return Arrays.clone(this.encryptedKeyData.getOctets());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyEncryptionAlgorithm);
        asn1EncodableVector.add(this.encryptedKeyData);
        return new DERSequence(asn1EncodableVector);
    }
}
