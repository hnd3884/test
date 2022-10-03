package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Vector;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.MessageEncryptor;

public class McElieceCipher implements MessageEncryptor
{
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
    private SecureRandom sr;
    private int n;
    private int k;
    private int t;
    public int maxPlainTextSize;
    public int cipherTextSize;
    private McElieceKeyParameters key;
    private boolean forEncryption;
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.sr = parametersWithRandom.getRandom();
                this.key = (McEliecePublicKeyParameters)parametersWithRandom.getParameters();
                this.initCipherEncrypt((McEliecePublicKeyParameters)this.key);
            }
            else {
                this.sr = new SecureRandom();
                this.key = (McEliecePublicKeyParameters)cipherParameters;
                this.initCipherEncrypt((McEliecePublicKeyParameters)this.key);
            }
        }
        else {
            this.key = (McEliecePrivateKeyParameters)cipherParameters;
            this.initCipherDecrypt((McEliecePrivateKeyParameters)this.key);
        }
    }
    
    public int getKeySize(final McElieceKeyParameters mcElieceKeyParameters) {
        if (mcElieceKeyParameters instanceof McEliecePublicKeyParameters) {
            return ((McEliecePublicKeyParameters)mcElieceKeyParameters).getN();
        }
        if (mcElieceKeyParameters instanceof McEliecePrivateKeyParameters) {
            return ((McEliecePrivateKeyParameters)mcElieceKeyParameters).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }
    
    private void initCipherEncrypt(final McEliecePublicKeyParameters mcEliecePublicKeyParameters) {
        this.sr = ((this.sr != null) ? this.sr : new SecureRandom());
        this.n = mcEliecePublicKeyParameters.getN();
        this.k = mcEliecePublicKeyParameters.getK();
        this.t = mcEliecePublicKeyParameters.getT();
        this.cipherTextSize = this.n >> 3;
        this.maxPlainTextSize = this.k >> 3;
    }
    
    private void initCipherDecrypt(final McEliecePrivateKeyParameters mcEliecePrivateKeyParameters) {
        this.n = mcEliecePrivateKeyParameters.getN();
        this.k = mcEliecePrivateKeyParameters.getK();
        this.maxPlainTextSize = this.k >> 3;
        this.cipherTextSize = this.n >> 3;
    }
    
    public byte[] messageEncrypt(final byte[] array) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        return ((GF2Vector)((McEliecePublicKeyParameters)this.key).getG().leftMultiply(this.computeMessageRepresentative(array)).add(new GF2Vector(this.n, this.t, this.sr))).getEncoded();
    }
    
    private GF2Vector computeMessageRepresentative(final byte[] array) {
        final byte[] array2 = new byte[this.maxPlainTextSize + (((this.k & 0x7) != 0x0) ? 1 : 0)];
        System.arraycopy(array, 0, array2, 0, array.length);
        array2[array.length] = 1;
        return GF2Vector.OS2VP(this.k, array2);
    }
    
    public byte[] messageDecrypt(final byte[] array) throws InvalidCipherTextException {
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        final GF2Vector os2VP = GF2Vector.OS2VP(this.n, array);
        final McEliecePrivateKeyParameters mcEliecePrivateKeyParameters = (McEliecePrivateKeyParameters)this.key;
        final GF2mField field = mcEliecePrivateKeyParameters.getField();
        final PolynomialGF2mSmallM goppaPoly = mcEliecePrivateKeyParameters.getGoppaPoly();
        final GF2Matrix sInv = mcEliecePrivateKeyParameters.getSInv();
        final Permutation p = mcEliecePrivateKeyParameters.getP1();
        final Permutation p2 = mcEliecePrivateKeyParameters.getP2();
        final GF2Matrix h = mcEliecePrivateKeyParameters.getH();
        final PolynomialGF2mSmallM[] qInv = mcEliecePrivateKeyParameters.getQInv();
        final Permutation rightMultiply = p.rightMultiply(p2);
        final GF2Vector gf2Vector = (GF2Vector)os2VP.multiply(rightMultiply.computeInverse());
        final GF2Vector syndromeDecode = GoppaCode.syndromeDecode((GF2Vector)h.rightMultiply(gf2Vector), field, goppaPoly, qInv);
        final GF2Vector gf2Vector2 = (GF2Vector)((GF2Vector)gf2Vector.add(syndromeDecode)).multiply(p);
        final GF2Vector gf2Vector3 = (GF2Vector)syndromeDecode.multiply(rightMultiply);
        return this.computeMessage((GF2Vector)sInv.leftMultiply(gf2Vector2.extractRightVector(this.k)));
    }
    
    private byte[] computeMessage(final GF2Vector gf2Vector) throws InvalidCipherTextException {
        byte[] encoded;
        int n;
        for (encoded = gf2Vector.getEncoded(), n = encoded.length - 1; n >= 0 && encoded[n] == 0; --n) {}
        if (n < 0 || encoded[n] != 1) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        final byte[] array = new byte[n];
        System.arraycopy(encoded, 0, array, 0, n);
        return array;
    }
}
