package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CertificationRequest extends ASN1Object
{
    private static final ASN1Integer ZERO;
    private final CertificationRequestInfo certificationRequestInfo;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final DERBitString signature;
    
    public CertificationRequest(final X500Name x500Name, final AlgorithmIdentifier algorithmIdentifier, final DERBitString derBitString, final ASN1Set set, final AlgorithmIdentifier signatureAlgorithm, final DERBitString signature) {
        this.certificationRequestInfo = new CertificationRequestInfo(x500Name, algorithmIdentifier, derBitString, set);
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = signature;
    }
    
    private CertificationRequest(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.certificationRequestInfo = new CertificationRequestInfo(ASN1Sequence.getInstance(asn1Sequence.getObjectAt(0)));
        this.signatureAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.signature = DERBitString.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static CertificationRequest getInstance(final Object o) {
        if (o instanceof CertificationRequest) {
            return (CertificationRequest)o;
        }
        if (o != null) {
            return new CertificationRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getVersion() {
        return this.certificationRequestInfo.getVersion().getValue();
    }
    
    public X500Name getSubject() {
        return this.certificationRequestInfo.getSubject();
    }
    
    public ASN1Set getAttributes() {
        return this.certificationRequestInfo.getAttributes();
    }
    
    public AlgorithmIdentifier getSubjectPublicKeyAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(0));
    }
    
    public DERBitString getSubjectPublicKey() {
        return DERBitString.getInstance(this.certificationRequestInfo.getSubjectPublicKeyInfo().getObjectAt(1));
    }
    
    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.getSubjectPublicKey().getOctets());
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }
    
    public DERBitString getSignature() {
        return this.signature;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certificationRequestInfo);
        asn1EncodableVector.add(this.signatureAlgorithm);
        asn1EncodableVector.add(this.signature);
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        ZERO = new ASN1Integer(0L);
    }
    
    private class CertificationRequestInfo extends ASN1Object
    {
        private final ASN1Integer version;
        private final X500Name subject;
        private final ASN1Sequence subjectPublicKeyInfo;
        private final ASN1Set attributes;
        
        private CertificationRequestInfo(final ASN1Sequence asn1Sequence) {
            if (asn1Sequence.size() != 4) {
                throw new IllegalArgumentException("incorrect sequence size for CertificationRequestInfo");
            }
            this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
            this.subject = X500Name.getInstance(asn1Sequence.getObjectAt(1));
            this.subjectPublicKeyInfo = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(2));
            if (this.subjectPublicKeyInfo.size() != 2) {
                throw new IllegalArgumentException("incorrect subjectPublicKeyInfo size for CertificationRequestInfo");
            }
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(3);
            if (asn1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("incorrect tag number on attributes for CertificationRequestInfo");
            }
            this.attributes = ASN1Set.getInstance(asn1TaggedObject, false);
        }
        
        private CertificationRequestInfo(final X500Name subject, final AlgorithmIdentifier algorithmIdentifier, final DERBitString derBitString, final ASN1Set attributes) {
            this.version = CertificationRequest.ZERO;
            this.subject = subject;
            this.subjectPublicKeyInfo = new DERSequence(new ASN1Encodable[] { algorithmIdentifier, derBitString });
            this.attributes = attributes;
        }
        
        private ASN1Integer getVersion() {
            return this.version;
        }
        
        private X500Name getSubject() {
            return this.subject;
        }
        
        private ASN1Sequence getSubjectPublicKeyInfo() {
            return this.subjectPublicKeyInfo;
        }
        
        private ASN1Set getAttributes() {
            return this.attributes;
        }
        
        @Override
        public ASN1Primitive toASN1Primitive() {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add(this.version);
            asn1EncodableVector.add(this.subject);
            asn1EncodableVector.add(this.subjectPublicKeyInfo);
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.attributes));
            return new DERSequence(asn1EncodableVector);
        }
    }
}
