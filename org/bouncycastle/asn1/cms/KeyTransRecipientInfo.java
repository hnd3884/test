package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class KeyTransRecipientInfo extends ASN1Object
{
    private ASN1Integer version;
    private RecipientIdentifier rid;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1OctetString encryptedKey;
    
    public KeyTransRecipientInfo(final RecipientIdentifier rid, final AlgorithmIdentifier keyEncryptionAlgorithm, final ASN1OctetString encryptedKey) {
        if (rid.toASN1Primitive() instanceof ASN1TaggedObject) {
            this.version = new ASN1Integer(2L);
        }
        else {
            this.version = new ASN1Integer(0L);
        }
        this.rid = rid;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }
    
    @Deprecated
    public KeyTransRecipientInfo(final ASN1Sequence asn1Sequence) {
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(0);
        this.rid = RecipientIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(2));
        this.encryptedKey = (ASN1OctetString)asn1Sequence.getObjectAt(3);
    }
    
    public static KeyTransRecipientInfo getInstance(final Object o) {
        if (o instanceof KeyTransRecipientInfo) {
            return (KeyTransRecipientInfo)o;
        }
        if (o != null) {
            return new KeyTransRecipientInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public RecipientIdentifier getRecipientIdentifier() {
        return this.rid;
    }
    
    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }
    
    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.rid);
        asn1EncodableVector.add(this.keyEncryptionAlgorithm);
        asn1EncodableVector.add(this.encryptedKey);
        return new DERSequence(asn1EncodableVector);
    }
}
