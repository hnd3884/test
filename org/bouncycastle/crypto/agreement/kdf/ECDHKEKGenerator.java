package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.crypto.DataLengthException;
import java.io.IOException;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Pack;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.DigestDerivationFunction;

public class ECDHKEKGenerator implements DigestDerivationFunction
{
    private DigestDerivationFunction kdf;
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;
    
    public ECDHKEKGenerator(final Digest digest) {
        this.kdf = new KDF2BytesGenerator(digest);
    }
    
    public void init(final DerivationParameters derivationParameters) {
        final DHKDFParameters dhkdfParameters = (DHKDFParameters)derivationParameters;
        this.algorithm = dhkdfParameters.getAlgorithm();
        this.keySize = dhkdfParameters.getKeySize();
        this.z = dhkdfParameters.getZ();
    }
    
    public Digest getDigest() {
        return this.kdf.getDigest();
    }
    
    public int generateBytes(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalArgumentException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE));
        asn1EncodableVector.add(new DERTaggedObject(true, 2, new DEROctetString(Pack.intToBigEndian(this.keySize))));
        try {
            this.kdf.init(new KDFParameters(this.z, new DERSequence(asn1EncodableVector).getEncoded("DER")));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("unable to initialise kdf: " + ex.getMessage());
        }
        return this.kdf.generateBytes(array, n, n2);
    }
}
