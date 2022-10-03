package net.oauth.signature.pem;

import java.math.BigInteger;
import java.io.IOException;
import java.security.spec.RSAPrivateCrtKeySpec;

public class PKCS1EncodedKeySpec
{
    private RSAPrivateCrtKeySpec keySpec;
    
    public PKCS1EncodedKeySpec(final byte[] keyBytes) throws IOException {
        this.decode(keyBytes);
    }
    
    public RSAPrivateCrtKeySpec getKeySpec() {
        return this.keySpec;
    }
    
    private void decode(final byte[] keyBytes) throws IOException {
        DerParser parser = new DerParser(keyBytes);
        final Asn1Object sequence = parser.read();
        if (sequence.getType() != 16) {
            throw new IOException("Invalid DER: not a sequence");
        }
        parser = sequence.getParser();
        parser.read();
        final BigInteger modulus = parser.read().getInteger();
        final BigInteger publicExp = parser.read().getInteger();
        final BigInteger privateExp = parser.read().getInteger();
        final BigInteger prime1 = parser.read().getInteger();
        final BigInteger prime2 = parser.read().getInteger();
        final BigInteger exp1 = parser.read().getInteger();
        final BigInteger exp2 = parser.read().getInteger();
        final BigInteger crtCoef = parser.read().getInteger();
        this.keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
    }
}
