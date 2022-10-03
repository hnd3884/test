package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class BiometricData extends ASN1Object
{
    private TypeOfBiometricData typeOfBiometricData;
    private AlgorithmIdentifier hashAlgorithm;
    private ASN1OctetString biometricDataHash;
    private DERIA5String sourceDataUri;
    
    public static BiometricData getInstance(final Object o) {
        if (o instanceof BiometricData) {
            return (BiometricData)o;
        }
        if (o != null) {
            return new BiometricData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private BiometricData(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.typeOfBiometricData = TypeOfBiometricData.getInstance(objects.nextElement());
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.biometricDataHash = ASN1OctetString.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.sourceDataUri = DERIA5String.getInstance(objects.nextElement());
        }
    }
    
    public BiometricData(final TypeOfBiometricData typeOfBiometricData, final AlgorithmIdentifier hashAlgorithm, final ASN1OctetString biometricDataHash, final DERIA5String sourceDataUri) {
        this.typeOfBiometricData = typeOfBiometricData;
        this.hashAlgorithm = hashAlgorithm;
        this.biometricDataHash = biometricDataHash;
        this.sourceDataUri = sourceDataUri;
    }
    
    public BiometricData(final TypeOfBiometricData typeOfBiometricData, final AlgorithmIdentifier hashAlgorithm, final ASN1OctetString biometricDataHash) {
        this.typeOfBiometricData = typeOfBiometricData;
        this.hashAlgorithm = hashAlgorithm;
        this.biometricDataHash = biometricDataHash;
        this.sourceDataUri = null;
    }
    
    public TypeOfBiometricData getTypeOfBiometricData() {
        return this.typeOfBiometricData;
    }
    
    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public ASN1OctetString getBiometricDataHash() {
        return this.biometricDataHash;
    }
    
    public DERIA5String getSourceDataUri() {
        return this.sourceDataUri;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.typeOfBiometricData);
        asn1EncodableVector.add(this.hashAlgorithm);
        asn1EncodableVector.add(this.biometricDataHash);
        if (this.sourceDataUri != null) {
            asn1EncodableVector.add(this.sourceDataUri);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
