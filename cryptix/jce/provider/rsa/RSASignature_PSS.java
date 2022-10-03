package cryptix.jce.provider.rsa;

import java.security.NoSuchAlgorithmException;
import cryptix.jce.util.Util;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SignatureSpi;

public abstract class RSASignature_PSS extends SignatureSpi
{
    private static final byte[] MASK;
    private final MessageDigest md;
    private final int hLen;
    private final int sLen;
    private byte[] presetSalt;
    private int emLen;
    private int emBits;
    private BigInteger exp;
    private BigInteger n;
    private BigInteger p;
    private BigInteger q;
    private BigInteger u;
    private SecureRandom rng;
    
    protected Object engineGetParameter(final String a) {
        throw new RuntimeException("NYI");
    }
    
    protected void engineInitSign(final PrivateKey key, final SecureRandom random) throws InvalidKeyException {
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
        this.rng = random;
        this.initCommon();
    }
    
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, new SecureRandom());
    }
    
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
        this.rng = null;
        this.initCommon();
    }
    
    private void initCommon() throws InvalidKeyException {
        this.emBits = this.getModulusBitLen() - 1;
        this.emLen = (this.emBits + 7) / 8;
        if (this.emBits < 8 * this.hLen + 8 * this.sLen + 9) {
            throw new InvalidKeyException("Signer's key modulus too short.");
        }
        this.md.reset();
    }
    
    protected void engineSetParameter(final String name, final Object param) {
        if (name.equalsIgnoreCase("CryptixDebugFixedSalt") && param instanceof byte[]) {
            this.presetSalt = (byte[])param;
        }
    }
    
    protected byte[] engineSign() {
        final byte[] padding1 = new byte[8];
        final byte[] mHash = this.md.digest();
        byte[] salt;
        if (this.presetSalt == null) {
            salt = new byte[this.sLen];
            this.rng.nextBytes(salt);
        }
        else {
            if (this.sLen != this.presetSalt.length) {
                throw new Error("Invalid presetSalt, size mismatch!");
            }
            final byte[] presetSalt = this.presetSalt;
            this.presetSalt = null;
            System.err.println("Using preset salt: " + Util.toString(presetSalt) + "!");
        }
        this.md.update(padding1);
        this.md.update(mHash);
        final byte[] H = this.md.digest(salt);
        final byte[] dbMask = this.mgf1(H, this.emLen - this.hLen - 1);
        final byte[] PS = new byte[this.emLen - this.sLen - this.hLen - 2];
        final byte[] one = { 1 };
        final byte[] DB = this.concat(PS, one, salt);
        final byte[] maskedDB = xor(DB, dbMask);
        final int maskBits = 8 * this.emLen - this.emBits;
        final byte[] array = maskedDB;
        final int n = 0;
        array[n] &= RSASignature_PSS.MASK[maskBits];
        final byte[] EM = this.concat(maskedDB, H, new byte[] { -68 });
        final BigInteger m = new BigInteger(1, EM);
        if (m.compareTo(this.n) != -1) {
            throw new InternalError("message > modulus!");
        }
        final BigInteger s = RSAAlgorithm.rsa(m, this.n, this.exp, this.p, this.q, this.u);
        return cryptix.jce.provider.util.Util.toFixedLenByteArray(s, this.getModulusLen());
    }
    
    private int getModulusLen() {
        return (this.n.bitLength() + 7) / 8;
    }
    
    private int getModulusBitLen() {
        return this.n.bitLength();
    }
    
    private static byte[] xor(final byte[] a, final byte[] b) {
        if (a.length != b.length) {
            throw new InternalError("a.len != b.len");
        }
        final byte[] res = new byte[a.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = (byte)(a[i] ^ b[i]);
        }
        return res;
    }
    
    private byte[] mgf1(final byte[] seed, final int len) {
        final int hashCount = (len + this.hLen - 1) / this.hLen;
        byte[] mask = new byte[0];
        for (int i = 0; i < hashCount; ++i) {
            mask = this.concat(mask, this.mgf1Hash(seed, (byte)i));
        }
        final byte[] res = new byte[len];
        System.arraycopy(mask, 0, res, 0, res.length);
        return res;
    }
    
    private byte[] mgf1Hash(final byte[] seed, final byte c) {
        this.md.update(seed);
        this.md.update(new byte[3]);
        this.md.update(c);
        return this.md.digest();
    }
    
    private byte[] concat(final byte[] a, final byte[] b) {
        final byte[] res = new byte[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
    }
    
    private byte[] concat(final byte[] a, final byte[] b, final byte[] c) {
        return this.concat(a, this.concat(b, c));
    }
    
    protected void engineUpdate(final byte b) {
        this.md.update(b);
    }
    
    protected void engineUpdate(final byte[] buf, final int off, final int len) {
        this.md.update(buf, off, len);
    }
    
    protected boolean engineVerify(final byte[] signature) {
        if (signature.length != this.getModulusLen()) {
            return false;
        }
        final BigInteger s = new BigInteger(1, signature);
        if (s.compareTo(BigInteger.ZERO) < 0 || s.compareTo(this.n) >= 0) {
            return false;
        }
        final BigInteger m = RSAAlgorithm.rsa(s, this.n, this.exp, this.p, this.q, this.u);
        if (m.bitLength() > this.emLen * 8) {
            return false;
        }
        final byte[] em = cryptix.jce.provider.util.Util.toFixedLenByteArray(m, this.emLen);
        return this.pssVerify(this.md.digest(), em, this.getModulusBitLen() - 1);
    }
    
    private boolean pssVerify(final byte[] mHash, final byte[] em, final int emBits) {
        if (emBits < 8 * this.hLen + 8 * this.sLen + 9) {
            return false;
        }
        if (em[em.length - 1] != -68) {
            return false;
        }
        final int maskedDbLen = this.emLen - this.hLen - 1;
        final byte[] maskedDb = new byte[maskedDbLen];
        System.arraycopy(em, 0, maskedDb, 0, maskedDbLen);
        final byte[] H = new byte[this.hLen];
        System.arraycopy(em, maskedDbLen, H, 0, this.hLen);
        final int lmbs = 8 * this.emLen - emBits;
        if ((maskedDb[0] & ~RSASignature_PSS.MASK[lmbs]) != 0x0) {
            return false;
        }
        final byte[] dbMask = this.mgf1(H, this.emLen - this.hLen - 1);
        final byte[] DB = xor(maskedDb, dbMask);
        final int zc = 8 * this.emLen - emBits;
        final byte[] array = DB;
        final int n = 0;
        array[n] &= RSASignature_PSS.MASK[zc];
        final int leftMost = this.emLen - this.hLen - this.sLen - 2;
        for (int i = 0; i < leftMost; ++i) {
            if (DB[i] != 0) {
                return false;
            }
        }
        if (DB[leftMost] != 1) {
            return false;
        }
        final byte[] salt = new byte[this.sLen];
        System.arraycopy(DB, DB.length - this.sLen, salt, 0, this.sLen);
        this.md.reset();
        this.md.update(new byte[8]);
        this.md.update(mHash);
        final byte[] H2 = this.md.digest(salt);
        return Util.equals(H2, H);
    }
    
    public RSASignature_PSS(final String hashName) {
        try {
            this.md = MessageDigest.getInstance(hashName);
            final int digestLength = this.md.getDigestLength();
            this.sLen = digestLength;
            this.hLen = digestLength;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new InternalError("MessageDigest not found! (" + hashName + "): " + ex.toString());
        }
    }
    
    static {
        MASK = new byte[] { -1, 127, 63, 31, 15, 7, 3, 1 };
    }
}
