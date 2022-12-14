package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERGeneralizedTime;
import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class ObjectStoreData extends ASN1Object
{
    private final BigInteger version;
    private final AlgorithmIdentifier integrityAlgorithm;
    private final ASN1GeneralizedTime creationDate;
    private final ASN1GeneralizedTime lastModifiedDate;
    private final ObjectDataSequence objectDataSequence;
    private final String comment;
    
    public ObjectStoreData(final AlgorithmIdentifier integrityAlgorithm, final Date date, final Date date2, final ObjectDataSequence objectDataSequence, final String comment) {
        this.version = BigInteger.valueOf(1L);
        this.integrityAlgorithm = integrityAlgorithm;
        this.creationDate = new DERGeneralizedTime(date);
        this.lastModifiedDate = new DERGeneralizedTime(date2);
        this.objectDataSequence = objectDataSequence;
        this.comment = comment;
    }
    
    private ObjectStoreData(final ASN1Sequence asn1Sequence) {
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue();
        this.integrityAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.creationDate = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(2));
        this.lastModifiedDate = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(3));
        this.objectDataSequence = ObjectDataSequence.getInstance(asn1Sequence.getObjectAt(4));
        this.comment = ((asn1Sequence.size() == 6) ? DERUTF8String.getInstance(asn1Sequence.getObjectAt(5)).getString() : null);
    }
    
    public static ObjectStoreData getInstance(final Object o) {
        if (o instanceof ObjectStoreData) {
            return (ObjectStoreData)o;
        }
        if (o != null) {
            return new ObjectStoreData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public ASN1GeneralizedTime getCreationDate() {
        return this.creationDate;
    }
    
    public AlgorithmIdentifier getIntegrityAlgorithm() {
        return this.integrityAlgorithm;
    }
    
    public ASN1GeneralizedTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }
    
    public ObjectDataSequence getObjectDataSequence() {
        return this.objectDataSequence;
    }
    
    public BigInteger getVersion() {
        return this.version;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(this.version));
        asn1EncodableVector.add(this.integrityAlgorithm);
        asn1EncodableVector.add(this.creationDate);
        asn1EncodableVector.add(this.lastModifiedDate);
        asn1EncodableVector.add(this.objectDataSequence);
        if (this.comment != null) {
            asn1EncodableVector.add(new DERUTF8String(this.comment));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
