package cryptix.jce.provider.rsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import cryptix.jce.provider.util.Util;
import java.security.SignatureException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.security.SignatureSpi;

public abstract class RSASignature_PKCS1 extends SignatureSpi
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    private BigInteger n;
    private BigInteger exp;
    private BigInteger p;
    private BigInteger q;
    private BigInteger u;
    private final MessageDigest md;
    
    protected void engineInitVerify(final PublicKey key) throws InvalidKeyException {
        if (!(key instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Not an RSA public key");
        }
        final RSAPublicKey rsa = (RSAPublicKey)key;
        this.n = rsa.getModulus();
        this.exp = rsa.getPublicExponent();
        final BigInteger p = null;
        this.u = p;
        this.q = p;
        this.p = p;
        this.initCommon();
    }
    
    protected void engineInitSign(final PrivateKey key) throws InvalidKeyException {
        if (!(key instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Not an RSA private key");
        }
        final RSAPrivateKey rsa = (RSAPrivateKey)key;
        this.n = rsa.getModulus();
        this.exp = rsa.getPrivateExponent();
        if (key instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey crt = (RSAPrivateCrtKey)key;
            this.p = crt.getPrimeP();
            this.q = crt.getPrimeQ();
            this.u = crt.getCrtCoefficient();
        }
        else {
            final BigInteger p = null;
            this.u = p;
            this.q = p;
            this.p = p;
        }
        this.initCommon();
    }
    
    private void initCommon() throws InvalidKeyException {
        this.md.reset();
        final int mdl = this.md.digest().length;
        final int length = this.modulusByteLength();
        final int aidl = this.getAlgorithmEncoding().length;
        final int padLen = length - 3 - aidl - mdl;
        if (padLen < 0) {
            throw new InvalidKeyException("Signer's key modulus too short.");
        }
    }
    
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        this.engineInitSign(privateKey);
    }
    
    protected void engineUpdate(final byte b) throws SignatureException {
        this.md.update(b);
    }
    
    protected void engineUpdate(final byte[] in, final int offset, final int length) throws SignatureException {
        this.md.update(in, offset, length);
    }
    
    protected byte[] engineSign() throws SignatureException {
        final BigInteger pkcs = this.makePKCS1();
        final BigInteger result = RSAAlgorithm.rsa(pkcs, this.n, this.exp, this.p, this.q, this.u);
        return Util.toFixedLenByteArray(result, this.modulusByteLength());
    }
    
    protected boolean engineVerify(final byte[] signature) throws SignatureException {
        final BigInteger M = new BigInteger(1, signature);
        final BigInteger computed = RSAAlgorithm.rsa(M, this.n, this.exp, this.p, this.q, this.u);
        final BigInteger actual = this.makePKCS1();
        return computed.equals(actual);
    }
    
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {
        throw new InvalidParameterException("This algorithm does not accept parameters.");
    }
    
    protected void engineSetParameter(final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("This algorithm does not accept AlgorithmParameterSpec.");
    }
    
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        throw new InvalidParameterException("This algorithm does not have parameters.");
    }
    
    private BigInteger makePKCS1() throws SignatureException {
        final byte[] theMD = this.md.digest();
        final int mdl = theMD.length;
        final int length = this.modulusByteLength();
        final byte[] r = new byte[length];
        r[1] = 1;
        final byte[] aid = this.getAlgorithmEncoding();
        final int aidl = aid.length;
        final int padLen = length - 3 - aidl - mdl;
        if (padLen < 0) {
            throw new SignatureException("Signer's public key modulus too short.");
        }
        for (int i = 0; i < padLen; r[2 + i++] = -1) {}
        System.arraycopy(aid, 0, r, padLen + 3, aidl);
        System.arraycopy(theMD, 0, r, length - mdl, mdl);
        return new BigInteger(r);
    }
    
    private int modulusByteLength() {
        return (this.n.bitLength() + 7) / 8;
    }
    
    protected abstract byte[] getAlgorithmEncoding();
    
    RSASignature_PKCS1(final String mdAlgorithm) {
        try {
            this.md = MessageDigest.getInstance(mdAlgorithm);
        }
        catch (final Exception e) {
            throw new InternalError("Unable to instantiate messagedigest:" + mdAlgorithm);
        }
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
    }
}
