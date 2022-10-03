package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedObjectStoreData extends ASN1Object
{
    private final AlgorithmIdentifier encryptionAlgorithm;
    private final ASN1OctetString encryptedContent;
    
    public EncryptedObjectStoreData(final AlgorithmIdentifier encryptionAlgorithm, final byte[] array) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.encryptedContent = new DEROctetString(array);
    }
    
    private EncryptedObjectStoreData(final ASN1Sequence asn1Sequence) {
        this.encryptionAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.encryptedContent = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static EncryptedObjectStoreData getInstance(final Object o) {
        if (o instanceof EncryptedObjectStoreData) {
            return (EncryptedObjectStoreData)o;
        }
        if (o != null) {
            return new EncryptedObjectStoreData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1OctetString getEncryptedContent() {
        return this.encryptedContent;
    }
    
    public AlgorithmIdentifier getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.encryptionAlgorithm);
        asn1EncodableVector.add(this.encryptedContent);
        return new DERSequence(asn1EncodableVector);
    }
}
