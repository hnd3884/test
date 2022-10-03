package cryptix.jce.provider.rsa;

import cryptix.jce.provider.asn.AsnObject;
import java.io.IOException;
import cryptix.jce.provider.asn.AsnInteger;
import cryptix.jce.provider.asn.AsnBitString;
import cryptix.jce.provider.asn.AsnInputStream;
import cryptix.jce.provider.asn.AsnSequence;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.RSAPublicKeySpec;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.KeyFactorySpi;

public final class RSAKeyFactory extends KeyFactorySpi
{
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RSAPrivateKeySpec) {
            final RSAPrivateKeySpec s = (RSAPrivateKeySpec)keySpec;
            return new RSAPrivateKeyCryptix(s.getModulus(), s.getPrivateExponent());
        }
        if (keySpec instanceof RSAPrivateCrtKeySpec) {
            final RSAPrivateCrtKeySpec s2 = (RSAPrivateCrtKeySpec)keySpec;
            return new RSAPrivateCrtKeyCryptix(s2.getModulus(), s2.getPublicExponent(), s2.getPrivateExponent(), s2.getPrimeP(), s2.getPrimeQ(), s2.getPrimeExponentP(), s2.getPrimeExponentQ(), s2.getCrtCoefficient());
        }
        if (keySpec instanceof X509EncodedKeySpec) {
            return this.decodePrivateKey((X509EncodedKeySpec)keySpec);
        }
        throw new InvalidKeySpecException(this.getClass().getName() + ".engineGeneratePrivate: " + "KeySpec of type " + keySpec.getClass() + " not supported.");
    }
    
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof RSAPublicKeySpec) {
            final RSAPublicKeySpec s = (RSAPublicKeySpec)keySpec;
            return new RSAPublicKeyCryptix(s.getModulus(), s.getPublicExponent());
        }
        if (keySpec instanceof X509EncodedKeySpec) {
            final PublicKey tmp = this.decodePublicKey((X509EncodedKeySpec)keySpec);
            final X509EncodedKeySpec ks = new X509EncodedKeySpec(tmp.getEncoded());
            return this.decodePublicKey(ks);
        }
        throw new InvalidKeySpecException(this.getClass().getName() + ".engineGeneratePublic: " + "KeySpec type " + keySpec.getClass() + " not supported.");
    }
    
    protected KeySpec engineGetKeySpec(final Key key, final Class keySpec) throws InvalidKeySpecException {
        throw new RuntimeException("NYI");
    }
    
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        throw new RuntimeException("NYI");
    }
    
    private PrivateKey decodePrivateKey(final X509EncodedKeySpec keySpec) throws InvalidKeySpecException {
        throw new RuntimeException("NYI");
    }
    
    private PublicKey decodePublicKey(final X509EncodedKeySpec keySpec) throws InvalidKeySpecException {
        try {
            AsnSequence seq = (AsnSequence)new AsnInputStream(keySpec.getEncoded()).read();
            if (seq.size() != 2) {
                throw new InvalidKeySpecException("First SEQUENCE has " + seq.size() + " elements.");
            }
            final AsnObject uh = seq.get(0);
            System.out.println(uh);
            final AsnBitString bs = (AsnBitString)seq.get(1);
            seq = (AsnSequence)new AsnInputStream(bs.toByteArray()).read();
            if (seq.size() != 2) {
                throw new InvalidKeySpecException("Second SEQUENCE has " + seq.size() + " elements.");
            }
            final AsnInteger n = (AsnInteger)seq.get(0);
            final AsnInteger e = (AsnInteger)seq.get(1);
            return new RSAPublicKeyImpl(n.toBigInteger(), e.toBigInteger());
        }
        catch (final ClassCastException ex) {
            throw new InvalidKeySpecException("Unexpected ASN.1 type detected: " + ex.getMessage());
        }
        catch (final IOException e2) {
            throw new InvalidKeySpecException("Could not parse key.");
        }
    }
}
