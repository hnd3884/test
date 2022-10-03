package cryptix.jce.provider.dh;

import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.SecureRandom;
import java.security.Key;
import java.math.BigInteger;
import javax.crypto.KeyAgreementSpi;

public final class DHKeyAgreement extends KeyAgreementSpi
{
    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_NEED_PUB_KEY = 1;
    private static final int STATE_COMPLETE = 2;
    private static final BigInteger ZERO;
    private BigInteger privG;
    private BigInteger privP;
    private BigInteger privX;
    private BigInteger masterSecret;
    private int masterSecretLen;
    private int state;
    
    protected void engineInit(final Key key, final SecureRandom unused) throws InvalidKeyException {
        if (!(key instanceof DHPrivateKey)) {
            throw new InvalidKeyException("key: not a DHPrivateKey");
        }
        final DHPrivateKey priv = (DHPrivateKey)key;
        final BigInteger privX = priv.getX();
        final BigInteger privG = priv.getParams().getG();
        final BigInteger privP = priv.getParams().getP();
        if (!privP.testBit(0)) {
            throw new InvalidKeyException("key: P is not odd");
        }
        if (privG.compareTo(DHKeyAgreement.ZERO) != 1 || privG.compareTo(privP) != -1) {
            throw new InvalidKeyException("key: G is invalid");
        }
        this.privX = privX;
        this.privG = privG;
        this.privP = privP;
        this.masterSecretLen = (privP.bitLength() + 7) / 8;
        System.out.println("privP.bitLen: " + this.privP.bitLength());
        System.out.println("masterSecretLen: " + this.masterSecretLen);
        this.state = 1;
    }
    
    protected void engineInit(final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new RuntimeException("NYI");
    }
    
    protected Key engineDoPhase(final Key key, final boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        if (this.state != 1) {
            throw new IllegalStateException();
        }
        if (!lastPhase) {
            throw new IllegalArgumentException("lastPhase: not 'true'");
        }
        if (!(key instanceof DHPublicKey)) {
            throw new IllegalArgumentException("key: not a DHPublicKey");
        }
        final DHPublicKey pub = (DHPublicKey)key;
        final BigInteger pubY = pub.getY();
        final BigInteger pubG = pub.getParams().getG();
        final BigInteger pubP = pub.getParams().getP();
        if (!pubG.equals(this.privG) || !pubP.equals(this.privP)) {
            throw new InvalidKeyException("key: incompatible group");
        }
        this.masterSecret = pubY.modPow(this.privX, this.privP);
        if (lastPhase) {
            this.state = 2;
        }
        return null;
    }
    
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.state != 2) {
            throw new IllegalStateException();
        }
        final byte[] returnBuf = new byte[this.masterSecretLen];
        final byte[] integerBuf = this.masterSecret.toByteArray();
        final int toCopy = (this.masterSecret.bitLength() + 7) / 8;
        System.out.println("this.masterSecret.bitLength(): " + this.masterSecret.bitLength());
        System.out.println("toCopy            : " + toCopy);
        System.out.println("returnBuf.length  : " + returnBuf.length);
        System.out.println("integerBuf.length : " + integerBuf.length);
        System.arraycopy(integerBuf, 0, returnBuf, returnBuf.length - toCopy, toCopy);
        return returnBuf;
    }
    
    protected int engineGenerateSecret(final byte[] sharedSecret, final int offset) throws IllegalStateException, ShortBufferException {
        final byte[] masterBytes = this.engineGenerateSecret();
        final int masterBytesLen = masterBytes.length;
        if (masterBytesLen > sharedSecret.length + offset) {
            throw new ShortBufferException();
        }
        System.arraycopy(masterBytes, 0, sharedSecret, offset, masterBytesLen);
        return masterBytesLen;
    }
    
    protected SecretKey engineGenerateSecret(final String algorithm) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        throw new RuntimeException("NYI");
    }
    
    public DHKeyAgreement() {
        this.state = 0;
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
