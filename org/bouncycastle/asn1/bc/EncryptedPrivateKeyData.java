package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedPrivateKeyData extends ASN1Object
{
    private final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
    private final Certificate[] certificateChain;
    
    public EncryptedPrivateKeyData(final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo, final Certificate[] array) {
        this.encryptedPrivateKeyInfo = encryptedPrivateKeyInfo;
        System.arraycopy(array, 0, this.certificateChain = new Certificate[array.length], 0, array.length);
    }
    
    private EncryptedPrivateKeyData(final ASN1Sequence asn1Sequence) {
        this.encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(asn1Sequence.getObjectAt(0));
        final ASN1Sequence instance = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        this.certificateChain = new Certificate[instance.size()];
        for (int i = 0; i != this.certificateChain.length; ++i) {
            this.certificateChain[i] = Certificate.getInstance(instance.getObjectAt(i));
        }
    }
    
    public static EncryptedPrivateKeyData getInstance(final Object o) {
        if (o instanceof EncryptedPrivateKeyData) {
            return (EncryptedPrivateKeyData)o;
        }
        if (o != null) {
            return new EncryptedPrivateKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Certificate[] getCertificateChain() {
        final Certificate[] array = new Certificate[this.certificateChain.length];
        System.arraycopy(this.certificateChain, 0, array, 0, this.certificateChain.length);
        return array;
    }
    
    public EncryptedPrivateKeyInfo getEncryptedPrivateKeyInfo() {
        return this.encryptedPrivateKeyInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.encryptedPrivateKeyInfo);
        asn1EncodableVector.add(new DERSequence(this.certificateChain));
        return new DERSequence(asn1EncodableVector);
    }
}
