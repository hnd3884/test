package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class RSAESOAEPparams extends ASN1Object
{
    private AlgorithmIdentifier hashAlgorithm;
    private AlgorithmIdentifier maskGenAlgorithm;
    private AlgorithmIdentifier pSourceAlgorithm;
    public static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM;
    public static final AlgorithmIdentifier DEFAULT_MASK_GEN_FUNCTION;
    public static final AlgorithmIdentifier DEFAULT_P_SOURCE_ALGORITHM;
    
    public static RSAESOAEPparams getInstance(final Object o) {
        if (o instanceof RSAESOAEPparams) {
            return (RSAESOAEPparams)o;
        }
        if (o != null) {
            return new RSAESOAEPparams(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RSAESOAEPparams() {
        this.hashAlgorithm = RSAESOAEPparams.DEFAULT_HASH_ALGORITHM;
        this.maskGenAlgorithm = RSAESOAEPparams.DEFAULT_MASK_GEN_FUNCTION;
        this.pSourceAlgorithm = RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM;
    }
    
    public RSAESOAEPparams(final AlgorithmIdentifier hashAlgorithm, final AlgorithmIdentifier maskGenAlgorithm, final AlgorithmIdentifier pSourceAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
        this.maskGenAlgorithm = maskGenAlgorithm;
        this.pSourceAlgorithm = pSourceAlgorithm;
    }
    
    @Deprecated
    public RSAESOAEPparams(final ASN1Sequence asn1Sequence) {
        this.hashAlgorithm = RSAESOAEPparams.DEFAULT_HASH_ALGORITHM;
        this.maskGenAlgorithm = RSAESOAEPparams.DEFAULT_MASK_GEN_FUNCTION;
        this.pSourceAlgorithm = RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM;
        for (int i = 0; i != asn1Sequence.size(); ++i) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(i);
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.hashAlgorithm = AlgorithmIdentifier.getInstance(asn1TaggedObject, true);
                    break;
                }
                case 1: {
                    this.maskGenAlgorithm = AlgorithmIdentifier.getInstance(asn1TaggedObject, true);
                    break;
                }
                case 2: {
                    this.pSourceAlgorithm = AlgorithmIdentifier.getInstance(asn1TaggedObject, true);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag");
                }
            }
        }
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public AlgorithmIdentifier getMaskGenAlgorithm() {
        return this.maskGenAlgorithm;
    }
    
    public AlgorithmIdentifier getPSourceAlgorithm() {
        return this.pSourceAlgorithm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (!this.hashAlgorithm.equals(RSAESOAEPparams.DEFAULT_HASH_ALGORITHM)) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.hashAlgorithm));
        }
        if (!this.maskGenAlgorithm.equals(RSAESOAEPparams.DEFAULT_MASK_GEN_FUNCTION)) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.maskGenAlgorithm));
        }
        if (!this.pSourceAlgorithm.equals(RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM)) {
            asn1EncodableVector.add(new DERTaggedObject(true, 2, this.pSourceAlgorithm));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    static {
        DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        DEFAULT_MASK_GEN_FUNCTION = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, RSAESOAEPparams.DEFAULT_HASH_ALGORITHM);
        DEFAULT_P_SOURCE_ALGORITHM = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(new byte[0]));
    }
}
