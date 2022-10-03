package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.MessageEncryptor;

public class McEliecePointchevalCipher implements MessageEncryptor
{
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.2.2";
    private Digest messDigest;
    private SecureRandom sr;
    private int n;
    private int k;
    private int t;
    McElieceCCA2KeyParameters key;
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
    
    public int getKeySize(final McElieceCCA2KeyParameters mcElieceCCA2KeyParameters) throws IllegalArgumentException {
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PublicKeyParameters) {
            return ((McElieceCCA2PublicKeyParameters)mcElieceCCA2KeyParameters).getN();
        }
        if (mcElieceCCA2KeyParameters instanceof McElieceCCA2PrivateKeyParameters) {
            return ((McElieceCCA2PrivateKeyParameters)mcElieceCCA2KeyParameters).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }
    
    protected int decryptOutputSize(final int n) {
        return 0;
    }
    
    protected int encryptOutputSize(final int n) {
        return 0;
    }
    
    private void initCipherEncrypt(final McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters) {
        this.sr = ((this.sr != null) ? this.sr : new SecureRandom());
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
        final int n = this.k >> 3;
        final byte[] array2 = new byte[n];
        this.sr.nextBytes(array2);
        final GF2Vector gf2Vector = new GF2Vector(this.k, this.sr);
        final byte[] encoded = gf2Vector.getEncoded();
        final byte[] concatenate = ByteUtils.concatenate(array, array2);
        this.messDigest.update(concatenate, 0, concatenate.length);
        final byte[] array3 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(array3, 0);
        final byte[] encoded2 = McElieceCCA2Primitives.encryptionPrimitive((McElieceCCA2PublicKeyParameters)this.key, gf2Vector, Conversions.encode(this.n, this.t, array3)).getEncoded();
        final DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(encoded);
        final byte[] array4 = new byte[array.length + n];
        digestRandomGenerator.nextBytes(array4);
        for (int i = 0; i < array.length; ++i) {
            final byte[] array5 = array4;
            final int n2 = i;
            array5[n2] ^= array[i];
        }
        for (int j = 0; j < n; ++j) {
            final byte[] array6 = array4;
            final int n3 = array.length + j;
            array6[n3] ^= array2[j];
        }
        return ByteUtils.concatenate(encoded2, array4);
    }
    
    public byte[] messageDecrypt(final byte[] array) throws InvalidCipherTextException {
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        final int n = this.n + 7 >> 3;
        final int n2 = array.length - n;
        final byte[][] split = ByteUtils.split(array, n);
        final byte[] array2 = split[0];
        final byte[] array3 = split[1];
        final GF2Vector[] decryptionPrimitive = McElieceCCA2Primitives.decryptionPrimitive((McElieceCCA2PrivateKeyParameters)this.key, GF2Vector.OS2VP(this.n, array2));
        final byte[] encoded = decryptionPrimitive[0].getEncoded();
        final GF2Vector gf2Vector = decryptionPrimitive[1];
        final DigestRandomGenerator digestRandomGenerator = new DigestRandomGenerator(new SHA1Digest());
        digestRandomGenerator.addSeedMaterial(encoded);
        final byte[] array4 = new byte[n2];
        digestRandomGenerator.nextBytes(array4);
        for (int i = 0; i < n2; ++i) {
            final byte[] array5 = array4;
            final int n3 = i;
            array5[n3] ^= array3[i];
        }
        this.messDigest.update(array4, 0, array4.length);
        final byte[] array6 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(array6, 0);
        if (!Conversions.encode(this.n, this.t, array6).equals(gf2Vector)) {
            throw new InvalidCipherTextException("Bad Padding: Invalid ciphertext.");
        }
        return ByteUtils.split(array4, n2 - (this.k >> 3))[0];
    }
}
