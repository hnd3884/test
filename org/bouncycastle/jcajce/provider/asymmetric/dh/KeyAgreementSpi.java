package org.bouncycastle.jcajce.provider.asymmetric.dh;

import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.util.DigestFactory;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPublicKey;
import java.security.Key;
import org.bouncycastle.crypto.DerivationFunction;
import java.math.BigInteger;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;

public class KeyAgreementSpi extends BaseAgreementSpi
{
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    private BigInteger x;
    private BigInteger p;
    private BigInteger g;
    private BigInteger result;
    
    public KeyAgreementSpi() {
        super("Diffie-Hellman", null);
    }
    
    public KeyAgreementSpi(final String s, final DerivationFunction derivationFunction) {
        super(s, derivationFunction);
    }
    
    protected byte[] bigIntToBytes(final BigInteger bigInteger) {
        final int n = (this.p.bitLength() + 7) / 8;
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == n) {
            return byteArray;
        }
        if (byteArray[0] == 0 && byteArray.length == n + 1) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            return array;
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
        return array2;
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (this.x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        if (!(key instanceof DHPublicKey)) {
            throw new InvalidKeyException("DHKeyAgreement doPhase requires DHPublicKey");
        }
        final DHPublicKey dhPublicKey = (DHPublicKey)key;
        if (!dhPublicKey.getParams().getG().equals(this.g) || !dhPublicKey.getParams().getP().equals(this.p)) {
            throw new InvalidKeyException("DHPublicKey not for this KeyAgreement!");
        }
        final BigInteger y = ((DHPublicKey)key).getY();
        if (y == null || y.compareTo(KeyAgreementSpi.TWO) < 0 || y.compareTo(this.p.subtract(KeyAgreementSpi.ONE)) >= 0) {
            throw new InvalidKeyException("Invalid DH PublicKey");
        }
        this.result = y.modPow(this.x, this.p);
        if (this.result.compareTo(KeyAgreementSpi.ONE) == 0) {
            throw new InvalidKeyException("Shared key can't be 1");
        }
        if (b) {
            return null;
        }
        return new BCDHPublicKey(this.result, dhPublicKey.getParams());
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        return super.engineGenerateSecret();
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        if (this.x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        return super.engineGenerateSecret(array, n);
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws NoSuchAlgorithmException {
        if (this.x == null) {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }
        final byte[] bigIntToBytes = this.bigIntToBytes(this.result);
        if (s.equals("TlsPremasterSecret")) {
            return new SecretKeySpec(BaseAgreementSpi.trimZeroes(bigIntToBytes), s);
        }
        return super.engineGenerateSecret(s);
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey for initialisation");
        }
        final DHPrivateKey dhPrivateKey = (DHPrivateKey)key;
        if (algorithmParameterSpec != null) {
            if (algorithmParameterSpec instanceof DHParameterSpec) {
                final DHParameterSpec dhParameterSpec = (DHParameterSpec)algorithmParameterSpec;
                this.p = dhParameterSpec.getP();
                this.g = dhParameterSpec.getG();
            }
            else {
                if (!(algorithmParameterSpec instanceof UserKeyingMaterialSpec)) {
                    throw new InvalidAlgorithmParameterException("DHKeyAgreement only accepts DHParameterSpec");
                }
                this.p = dhPrivateKey.getParams().getP();
                this.g = dhPrivateKey.getParams().getG();
                this.ukmParameters = ((UserKeyingMaterialSpec)algorithmParameterSpec).getUserKeyingMaterial();
            }
        }
        else {
            this.p = dhPrivateKey.getParams().getP();
            this.g = dhPrivateKey.getParams().getG();
        }
        final BigInteger x = dhPrivateKey.getX();
        this.result = x;
        this.x = x;
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey");
        }
        final DHPrivateKey dhPrivateKey = (DHPrivateKey)key;
        this.p = dhPrivateKey.getParams().getP();
        this.g = dhPrivateKey.getParams().getG();
        final BigInteger x = dhPrivateKey.getX();
        this.result = x;
        this.x = x;
    }
    
    @Override
    protected byte[] calcSecret() {
        return this.bigIntToBytes(this.result);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
    }
    
    public static class DHwithRFC2631KDF extends KeyAgreementSpi
    {
        public DHwithRFC2631KDF() {
            super("DHwithRFC2631KDF", new DHKEKGenerator(DigestFactory.createSHA1()));
        }
    }
}
