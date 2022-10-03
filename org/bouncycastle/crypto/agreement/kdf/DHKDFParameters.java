package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.DerivationParameters;

public class DHKDFParameters implements DerivationParameters
{
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;
    private byte[] extraInfo;
    
    public DHKDFParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final int n, final byte[] array) {
        this(asn1ObjectIdentifier, n, array, null);
    }
    
    public DHKDFParameters(final ASN1ObjectIdentifier algorithm, final int keySize, final byte[] z, final byte[] extraInfo) {
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.z = z;
        this.extraInfo = extraInfo;
    }
    
    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }
    
    public int getKeySize() {
        return this.keySize;
    }
    
    public byte[] getZ() {
        return this.z;
    }
    
    public byte[] getExtraInfo() {
        return this.extraInfo;
    }
}
