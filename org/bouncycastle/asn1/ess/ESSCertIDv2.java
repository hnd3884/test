package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ESSCertIDv2 extends ASN1Object
{
    private AlgorithmIdentifier hashAlgorithm;
    private byte[] certHash;
    private IssuerSerial issuerSerial;
    private static final AlgorithmIdentifier DEFAULT_ALG_ID;
    
    public static ESSCertIDv2 getInstance(final Object o) {
        if (o instanceof ESSCertIDv2) {
            return (ESSCertIDv2)o;
        }
        if (o != null) {
            return new ESSCertIDv2(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ESSCertIDv2(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        int n = 0;
        if (asn1Sequence.getObjectAt(0) instanceof ASN1OctetString) {
            this.hashAlgorithm = ESSCertIDv2.DEFAULT_ALG_ID;
        }
        else {
            this.hashAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n++).toASN1Primitive());
        }
        this.certHash = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(n++).toASN1Primitive()).getOctets();
        if (asn1Sequence.size() > n) {
            this.issuerSerial = IssuerSerial.getInstance(asn1Sequence.getObjectAt(n));
        }
    }
    
    public ESSCertIDv2(final byte[] array) {
        this(null, array, null);
    }
    
    public ESSCertIDv2(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) {
        this(algorithmIdentifier, array, null);
    }
    
    public ESSCertIDv2(final byte[] array, final IssuerSerial issuerSerial) {
        this(null, array, issuerSerial);
    }
    
    public ESSCertIDv2(final AlgorithmIdentifier hashAlgorithm, final byte[] array, final IssuerSerial issuerSerial) {
        if (hashAlgorithm == null) {
            this.hashAlgorithm = ESSCertIDv2.DEFAULT_ALG_ID;
        }
        else {
            this.hashAlgorithm = hashAlgorithm;
        }
        this.certHash = Arrays.clone(array);
        this.issuerSerial = issuerSerial;
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public byte[] getCertHash() {
        return Arrays.clone(this.certHash);
    }
    
    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (!this.hashAlgorithm.equals(ESSCertIDv2.DEFAULT_ALG_ID)) {
            asn1EncodableVector.add(this.hashAlgorithm);
        }
        asn1EncodableVector.add(new DEROctetString(this.certHash).toASN1Primitive());
        if (this.issuerSerial != null) {
            asn1EncodableVector.add(this.issuerSerial);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        DEFAULT_ALG_ID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    }
}
