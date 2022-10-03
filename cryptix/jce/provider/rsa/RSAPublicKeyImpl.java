package cryptix.jce.provider.rsa;

import java.io.IOException;
import cryptix.jce.provider.asn.AsnBitString;
import cryptix.jce.provider.asn.AsnAlgorithmId;
import cryptix.jce.provider.asn.AsnObjectId;
import cryptix.jce.provider.asn.AsnObject;
import cryptix.jce.provider.asn.AsnSequence;
import cryptix.jce.provider.asn.AsnInteger;
import cryptix.jce.provider.asn.AsnOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public final class RSAPublicKeyImpl implements RSAPublicKey
{
    private final BigInteger n;
    private final BigInteger e;
    
    public BigInteger getModulus() {
        return this.n;
    }
    
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        try {
            final AsnOutputStream asnOutputStream = new AsnOutputStream();
            asnOutputStream.write(new AsnSequence(new AsnInteger(this.n), new AsnInteger(this.e)));
            final byte[] bitStringBytes = asnOutputStream.toByteArray();
            final AsnOutputStream asnOutputStream2 = new AsnOutputStream();
            asnOutputStream2.write(new AsnSequence(new AsnAlgorithmId(AsnObjectId.OID_rsaEncryption), new AsnBitString(bitStringBytes)));
            return asnOutputStream2.toByteArray();
        }
        catch (final IOException e) {
            e.printStackTrace();
            throw new InternalError("PANIC: Unexpected exception during ASN encoding...");
        }
    }
    
    public RSAPublicKeyImpl(final BigInteger n, final BigInteger e) {
        this.n = n;
        this.e = e;
    }
}
