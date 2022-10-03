package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;

public class ObjectStore extends ASN1Object
{
    private final ASN1Encodable storeData;
    private final ObjectStoreIntegrityCheck integrityCheck;
    
    public ObjectStore(final ObjectStoreData storeData, final ObjectStoreIntegrityCheck integrityCheck) {
        this.storeData = storeData;
        this.integrityCheck = integrityCheck;
    }
    
    public ObjectStore(final EncryptedObjectStoreData storeData, final ObjectStoreIntegrityCheck integrityCheck) {
        this.storeData = storeData;
        this.integrityCheck = integrityCheck;
    }
    
    private ObjectStore(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("malformed sequence");
        }
        final ASN1Encodable object = asn1Sequence.getObjectAt(0);
        if (object instanceof EncryptedObjectStoreData) {
            this.storeData = object;
        }
        else if (object instanceof ObjectStoreData) {
            this.storeData = object;
        }
        else {
            final ASN1Sequence instance = ASN1Sequence.getInstance(object);
            if (instance.size() == 2) {
                this.storeData = EncryptedObjectStoreData.getInstance(instance);
            }
            else {
                this.storeData = ObjectStoreData.getInstance(instance);
            }
        }
        this.integrityCheck = ObjectStoreIntegrityCheck.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static ObjectStore getInstance(final Object o) {
        if (o instanceof ObjectStore) {
            return (ObjectStore)o;
        }
        if (o != null) {
            return new ObjectStore(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ObjectStoreIntegrityCheck getIntegrityCheck() {
        return this.integrityCheck;
    }
    
    public ASN1Encodable getStoreData() {
        return this.storeData;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.storeData);
        asn1EncodableVector.add(this.integrityCheck);
        return new DERSequence(asn1EncodableVector);
    }
}
