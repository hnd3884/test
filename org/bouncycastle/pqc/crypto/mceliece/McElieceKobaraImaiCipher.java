package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.pqc.math.linearalgebra.IntegerFunctions;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.MessageEncryptor;

public class McElieceKobaraImaiCipher implements MessageEncryptor
{
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.3";
    private static final String DEFAULT_PRNG_NAME = "SHA1PRNG";
    public static final byte[] PUBLIC_CONSTANT;
    private Digest messDigest;
    private SecureRandom sr;
    McElieceCCA2KeyParameters key;
    private int n;
    private int k;
    private int t;
    private boolean forEncryption;
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.sr = parametersWithRandom.getRandom();
                this.key = (McElieceCCA2PublicKeyParameters)parametersWithRandom.getParameters();
                this.initCipherEncrypt((McElieceCCA2PublicKeyParameters)this.key);
            }
            else {
                this.sr = new SecureRandom();
                this.key = (McElieceCCA2PublicKeyParameters)cipherParameters;
                this.initCipherEncrypt((McElieceCCA2PublicKeyParameters)this.key);
            }
        }
        else {
            this.key = (McElieceCCA2PrivateKeyParameters)cipherParameters;
            this.initCipherDecrypt((McElieceCCA2PrivateKeyParameters)this.key);
        }
    }
    
    public int getKeySize(final McElieceCCA2KeyParameters mcElieceCCA2KeyParameters) {
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PublicKeyParameters) {
            return ((McElieceCCA2PublicKeyParameters)mcElieceCCA2KeyParameters).getN();
        }
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PrivateKeyParameters) {
            return ((McElieceCCA2PrivateKeyParameters)mcElieceCCA2KeyParameters).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }
    
    private void initCipherEncrypt(final McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters) {
        this.messDigest = Utils.getDigest(mcElieceCCA2PublicKeyParameters.getDigest());
        this.n = mcElieceCCA2PublicKeyParameters.getN();
        this.k = mcElieceCCA2PublicKeyParameters.getK();
        this.t = mcElieceCCA2PublicKeyParameters.getT();
    }
    
    private void initCipherDecrypt(final McElieceCCA2PrivateKeyParameters mcElieceCCA2PrivateKeyParameters) {
        this.messDigest = Utils.getDigest(mcElieceCCA2PrivateKeyParameters.getDigest());
        this.n = mcElieceCCA2PrivateKeyParameters.getN();
        this.k = mcElieceCCA2PrivateKeyParameters.getK();
        this.t = mcElieceCCA2PrivateKeyParameters.getT();
    }
    
    public byte[] messageEncrypt(final byte[] array) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        final int digestSize = this.messDigest.getDigestSize();
        final int n = this.k >> 3;
        final int n2 = IntegerFunctions.binomial(this.n, this.t).bitLength() - 1 >> 3;
        int length = n + n2 - digestSize - McElieceKobaraImaiCipher.PUBLIC_CONSTANT.length;
        if (array.length > length) {
            length = array.length;
        }
        final int n3 = length + McElieceKobaraImaiCipher.PUBLIC_CONSTANT.length;
        final int n4 = n3 + digestSize - n - n2;
        final byte[] array2 = new byte[n3];
        System.arraycopy(array, 0, array2, 0, array.length);
        System.arraycopy(McElieceKobaraImaiCipher.PUBLIC_CONSTANT, 0, array2, length, McElieceKobaraImaiCipher.PUBLIC_CONSTANT.length);
        final byte[] array3 = new byte[digestSize];
        this.sr.nextBytes(array3);
        final DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(array3);
        final byte[] array4 = new byte[n3];
        digestRandomGenerator.nextBytes(array4);
        for (int i = n3 - 1; i >= 0; --i) {
            final byte[] array5 = array4;
            final int n5 = i;
            array5[n5] ^= array2[i];
        }
        final byte[] array6 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.update(array4, 0, array4.length);
        this.messDigest.doFinal(array6, 0);
        for (int j = digestSize - 1; j >= 0; --j) {
            final byte[] array7 = array6;
            final int n6 = j;
            array7[n6] ^= array3[j];
        }
        final byte[] concatenate = ByteUtils.concatenate(array6, array4);
        byte[] array8 = new byte[0];
        if (n4 > 0) {
            array8 = new byte[n4];
            System.arraycopy(concatenate, 0, array8, 0, n4);
        }
        final byte[] array9 = new byte[n2];
        System.arraycopy(concatenate, n4, array9, 0, n2);
        final byte[] array10 = new byte[n];
        System.arraycopy(concatenate, n4 + n2, array10, 0, n);
        final byte[] encoded = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters)this.key, GF2Vector.OS2VP(this.k, array10), Conversions.encode(this.n, this.t, array9)).getEncoded();
        if (n4 > 0) {
            return ByteUtils.concatenate(array8, encoded);
        }
        return encoded;
    }
    
    public byte[] messageDecrypt(final byte[] array) throws InvalidCipherTextException {
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        final int n = this.n >> 3;
        if (array.length < n) {
            throw new InvalidCipherTextException("Bad Padding: Ciphertext too short.");
        }
        final int digestSize = this.messDigest.getDigestSize();
        final int n2 = this.k >> 3;
        final int n3 = array.length - n;
        byte[] array2;
        byte[] array3;
        if (n3 > 0) {
            final byte[][] split = ByteUtils.split(array, n3);
            array2 = split[0];
            array3 = split[1];
        }
        else {
            array2 = new byte[0];
            array3 = array;
        }
        final GF2Vector[] decryptionPrimitive = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters)this.key, GF2Vector.OS2VP(this.n, array3));
        byte[] array4 = decryptionPrimitive[0].getEncoded();
        final GF2Vector gf2Vector = decryptionPrimitive[1];
        if (array4.length > n2) {
            array4 = ByteUtils.subArray(array4, 0, n2);
        }
        final byte[] concatenate = ByteUtils.concatenate(ByteUtils.concatenate(array2, Conversions.decode(this.n, this.t, gf2Vector)), array4);
        final int n4 = concatenate.length - digestSize;
        final byte[][] split2 = ByteUtils.split(concatenate, digestSize);
        final byte[] array5 = split2[0];
        final byte[] array6 = split2[1];
        final byte[] array7 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.update(array6, 0, array6.length);
        this.messDigest.doFinal(array7, 0);
        for (int i = digestSize - 1; i >= 0; --i) {
            final byte[] array8 = array7;
            final int n5 = i;
            array8[n5] ^= array5[i];
        }
        final DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(array7);
        final byte[] array9 = new byte[n4];
        digestRandomGenerator.nextBytes(array9);
        for (int j = n4 - 1; j >= 0; --j) {
            final byte[] array10 = array9;
            final int n6 = j;
            array10[n6] ^= array6[j];
        }
        if (array9.length < n4) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        final byte[][] split3 = ByteUtils.split(array9, n4 - McElieceKobaraImaiCipher.PUBLIC_CONSTANT.length);
        final byte[] array11 = split3[0];
        if (!ByteUtils.equals(split3[1], McElieceKobaraImaiCipher.PUBLIC_CONSTANT)) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        return array11;
    }
    
    static {
        PUBLIC_CONSTANT = "a predetermined public constant".getBytes();
    }
}
