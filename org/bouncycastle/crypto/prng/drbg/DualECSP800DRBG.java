package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;

public class DualECSP800DRBG implements SP80090DRBG
{
    private static final BigInteger p256_Px;
    private static final BigInteger p256_Py;
    private static final BigInteger p256_Qx;
    private static final BigInteger p256_Qy;
    private static final BigInteger p384_Px;
    private static final BigInteger p384_Py;
    private static final BigInteger p384_Qx;
    private static final BigInteger p384_Qy;
    private static final BigInteger p521_Px;
    private static final BigInteger p521_Py;
    private static final BigInteger p521_Qx;
    private static final BigInteger p521_Qy;
    private static final DualECPoints[] nistPoints;
    private static final long RESEED_MAX = 2147483648L;
    private static final int MAX_ADDITIONAL_INPUT = 4096;
    private static final int MAX_ENTROPY_LENGTH = 4096;
    private static final int MAX_PERSONALIZATION_STRING = 4096;
    private Digest _digest;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private int _securityStrength;
    private int _seedlen;
    private int _outlen;
    private ECCurve.Fp _curve;
    private ECPoint _P;
    private ECPoint _Q;
    private byte[] _s;
    private int _sLength;
    private ECMultiplier _fixedPointMultiplier;
    
    public DualECSP800DRBG(final Digest digest, final int n, final EntropySource entropySource, final byte[] array, final byte[] array2) {
        this(DualECSP800DRBG.nistPoints, digest, n, entropySource, array, array2);
    }
    
    public DualECSP800DRBG(final DualECPoints[] array, final Digest digest, final int securityStrength, final EntropySource entropySource, final byte[] array2, final byte[] array3) {
        this._fixedPointMultiplier = new FixedPointCombMultiplier();
        this._digest = digest;
        this._entropySource = entropySource;
        this._securityStrength = securityStrength;
        if (Utils.isTooLarge(array2, 512)) {
            throw new IllegalArgumentException("Personalization string too large");
        }
        if (entropySource.entropySize() < securityStrength || entropySource.entropySize() > 4096) {
            throw new IllegalArgumentException("EntropySource must provide between " + securityStrength + " and " + 4096 + " bits");
        }
        final byte[] concatenate = Arrays.concatenate(this.getEntropy(), array3, array2);
        int i = 0;
        while (i != array.length) {
            if (securityStrength <= array[i].getSecurityStrength()) {
                if (Utils.getMaxSecurityStrength(digest) < array[i].getSecurityStrength()) {
                    throw new IllegalArgumentException("Requested security strength is not supported by digest");
                }
                this._seedlen = array[i].getSeedLen();
                this._outlen = array[i].getMaxOutlen() / 8;
                this._P = array[i].getP();
                this._Q = array[i].getQ();
                break;
            }
            else {
                ++i;
            }
        }
        if (this._P == null) {
            throw new IllegalArgumentException("security strength cannot be greater than 256 bits");
        }
        this._s = Utils.hash_df(this._digest, concatenate, this._seedlen);
        this._sLength = this._s.length;
        this._reseedCounter = 0L;
    }
    
    public int getBlockSize() {
        return this._outlen * 8;
    }
    
    public int generate(final byte[] array, byte[] hash_df, final boolean b) {
        final int n = array.length * 8;
        final int n2 = array.length / this._outlen;
        if (Utils.isTooLarge(hash_df, 512)) {
            throw new IllegalArgumentException("Additional input too large");
        }
        if (this._reseedCounter + n2 > 2147483648L) {
            return -1;
        }
        if (b) {
            this.reseed(hash_df);
            hash_df = null;
        }
        BigInteger bigInteger;
        if (hash_df != null) {
            hash_df = Utils.hash_df(this._digest, hash_df, this._seedlen);
            bigInteger = new BigInteger(1, this.xor(this._s, hash_df));
        }
        else {
            bigInteger = new BigInteger(1, this._s);
        }
        Arrays.fill(array, (byte)0);
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            bigInteger = this.getScalarMultipleXCoord(this._P, bigInteger);
            final byte[] byteArray = this.getScalarMultipleXCoord(this._Q, bigInteger).toByteArray();
            if (byteArray.length > this._outlen) {
                System.arraycopy(byteArray, byteArray.length - this._outlen, array, n3, this._outlen);
            }
            else {
                System.arraycopy(byteArray, 0, array, n3 + (this._outlen - byteArray.length), byteArray.length);
            }
            n3 += this._outlen;
            ++this._reseedCounter;
        }
        if (n3 < array.length) {
            bigInteger = this.getScalarMultipleXCoord(this._P, bigInteger);
            final byte[] byteArray2 = this.getScalarMultipleXCoord(this._Q, bigInteger).toByteArray();
            final int n4 = array.length - n3;
            if (byteArray2.length > this._outlen) {
                System.arraycopy(byteArray2, byteArray2.length - this._outlen, array, n3, n4);
            }
            else {
                System.arraycopy(byteArray2, 0, array, n3 + (this._outlen - byteArray2.length), n4);
            }
            ++this._reseedCounter;
        }
        this._s = BigIntegers.asUnsignedByteArray(this._sLength, this.getScalarMultipleXCoord(this._P, bigInteger));
        return n;
    }
    
    public void reseed(final byte[] array) {
        if (Utils.isTooLarge(array, 512)) {
            throw new IllegalArgumentException("Additional input string too large");
        }
        this._s = Utils.hash_df(this._digest, Arrays.concatenate(this.pad8(this._s, this._seedlen), this.getEntropy(), array), this._seedlen);
        this._reseedCounter = 0L;
    }
    
    private byte[] getEntropy() {
        final byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }
    
    private byte[] xor(final byte[] array, final byte[] array2) {
        if (array2 == null) {
            return array;
        }
        final byte[] array3 = new byte[array.length];
        for (int i = 0; i != array3.length; ++i) {
            array3[i] = (byte)(array[i] ^ array2[i]);
        }
        return array3;
    }
    
    private byte[] pad8(final byte[] array, final int n) {
        if (n % 8 == 0) {
            return array;
        }
        final int n2 = 8 - n % 8;
        int n3 = 0;
        for (int i = array.length - 1; i >= 0; --i) {
            final int n4 = array[i] & 0xFF;
            array[i] = (byte)(n4 << n2 | n3 >> 8 - n2);
            n3 = n4;
        }
        return array;
    }
    
    private BigInteger getScalarMultipleXCoord(final ECPoint ecPoint, final BigInteger bigInteger) {
        return this._fixedPointMultiplier.multiply(ecPoint, bigInteger).normalize().getAffineXCoord().toBigInteger();
    }
    
    static {
        p256_Px = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16);
        p256_Py = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16);
        p256_Qx = new BigInteger("c97445f45cdef9f0d3e05e1e585fc297235b82b5be8ff3efca67c59852018192", 16);
        p256_Qy = new BigInteger("b28ef557ba31dfcbdd21ac46e2a91e3c304f44cb87058ada2cb815151e610046", 16);
        p384_Px = new BigInteger("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16);
        p384_Py = new BigInteger("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16);
        p384_Qx = new BigInteger("8e722de3125bddb05580164bfe20b8b432216a62926c57502ceede31c47816edd1e89769124179d0b695106428815065", 16);
        p384_Qy = new BigInteger("023b1660dd701d0839fd45eec36f9ee7b32e13b315dc02610aa1b636e346df671f790f84c5e09b05674dbb7e45c803dd", 16);
        p521_Px = new BigInteger("c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16);
        p521_Py = new BigInteger("11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16);
        p521_Qx = new BigInteger("1b9fa3e518d683c6b65763694ac8efbaec6fab44f2276171a42726507dd08add4c3b3f4c1ebc5b1222ddba077f722943b24c3edfa0f85fe24d0c8c01591f0be6f63", 16);
        p521_Qy = new BigInteger("1f3bdba585295d9a1110d1df1f9430ef8442c5018976ff3437ef91b81dc0b8132c8d5c39c32d0e004a3092b7d327c0e7a4d26d2c7b69b58f9066652911e457779de", 16);
        nistPoints = new DualECPoints[3];
        final ECCurve.Fp fp = (ECCurve.Fp)NISTNamedCurves.getByName("P-256").getCurve();
        DualECSP800DRBG.nistPoints[0] = new DualECPoints(128, fp.createPoint(DualECSP800DRBG.p256_Px, DualECSP800DRBG.p256_Py), fp.createPoint(DualECSP800DRBG.p256_Qx, DualECSP800DRBG.p256_Qy), 1);
        final ECCurve.Fp fp2 = (ECCurve.Fp)NISTNamedCurves.getByName("P-384").getCurve();
        DualECSP800DRBG.nistPoints[1] = new DualECPoints(192, fp2.createPoint(DualECSP800DRBG.p384_Px, DualECSP800DRBG.p384_Py), fp2.createPoint(DualECSP800DRBG.p384_Qx, DualECSP800DRBG.p384_Qy), 1);
        final ECCurve.Fp fp3 = (ECCurve.Fp)NISTNamedCurves.getByName("P-521").getCurve();
        DualECSP800DRBG.nistPoints[2] = new DualECPoints(256, fp3.createPoint(DualECSP800DRBG.p521_Px, DualECSP800DRBG.p521_Py), fp3.createPoint(DualECSP800DRBG.p521_Qx, DualECSP800DRBG.p521_Qy), 1);
    }
}
