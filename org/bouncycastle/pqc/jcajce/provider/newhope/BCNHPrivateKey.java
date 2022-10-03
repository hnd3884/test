package org.bouncycastle.pqc.jcajce.provider.newhope;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Pack;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.jcajce.interfaces.NHPrivateKey;

public class BCNHPrivateKey implements NHPrivateKey
{
    private static final long serialVersionUID = 1L;
    private final NHPrivateKeyParameters params;
    
    public BCNHPrivateKey(final NHPrivateKeyParameters params) {
        this.params = params;
    }
    
    public BCNHPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.params = new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets()));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof BCNHPrivateKey && Arrays.areEqual(this.params.getSecData(), ((BCNHPrivateKey)o).params.getSecData());
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.params.getSecData());
    }
    
    public final String getAlgorithm() {
        return "NH";
    }
    
    public byte[] getEncoded() {
        try {
            final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            final short[] secData = this.params.getSecData();
            final byte[] array = new byte[secData.length * 2];
            for (int i = 0; i != secData.length; ++i) {
                Pack.shortToLittleEndian(secData[i], array, i * 2);
            }
            return new PrivateKeyInfo(algorithmIdentifier, new DEROctetString(array)).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public short[] getSecretData() {
        return this.params.getSecData();
    }
    
    CipherParameters getKeyParams() {
        return this.params;
    }
    
    private static short[] convert(final byte[] array) {
        final short[] array2 = new short[array.length / 2];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = Pack.littleEndianToShort(array, i * 2);
        }
        return array2;
    }
}
