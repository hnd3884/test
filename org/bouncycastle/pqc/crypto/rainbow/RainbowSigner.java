package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.rainbow.util.ComputeInField;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class RainbowSigner implements MessageSigner
{
    private static final int MAXITS = 65536;
    private SecureRandom random;
    int signableDocumentLength;
    private short[] x;
    private ComputeInField cf;
    RainbowKeyParameters key;
    
    public RainbowSigner() {
        this.cf = new ComputeInField();
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            if (cipherParameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.random = parametersWithRandom.getRandom();
                this.key = (RainbowPrivateKeyParameters)parametersWithRandom.getParameters();
            }
            else {
                this.random = new SecureRandom();
                this.key = (RainbowPrivateKeyParameters)cipherParameters;
            }
        }
        else {
            this.key = (RainbowPublicKeyParameters)cipherParameters;
        }
        this.signableDocumentLength = this.key.getDocLength();
    }
    
    private short[] initSign(final Layer[] array, final short[] array2) {
        final short[] array3 = new short[array2.length];
        final short[] multiplyMatrix = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters)this.key).getInvA1(), this.cf.addVect(((RainbowPrivateKeyParameters)this.key).getB1(), array2));
        for (int i = 0; i < array[0].getVi(); ++i) {
            this.x[i] = (short)this.random.nextInt();
            this.x[i] &= 0xFF;
        }
        return multiplyMatrix;
    }
    
    public byte[] generateSignature(final byte[] array) {
        final Layer[] layers = ((RainbowPrivateKeyParameters)this.key).getLayers();
        final int length = layers.length;
        this.x = new short[((RainbowPrivateKeyParameters)this.key).getInvA2().length];
        final byte[] array2 = new byte[layers[length - 1].getViNext()];
        final short[] messageRepresentative = this.makeMessageRepresentative(array);
        int n = 0;
        boolean b;
        do {
            b = true;
            int n2 = 0;
            try {
                final short[] initSign = this.initSign(layers, messageRepresentative);
                for (int i = 0; i < length; ++i) {
                    final short[] array3 = new short[layers[i].getOi()];
                    final short[] array4 = new short[layers[i].getOi()];
                    for (int j = 0; j < layers[i].getOi(); ++j) {
                        array3[j] = initSign[n2];
                        ++n2;
                    }
                    final short[] solveEquation = this.cf.solveEquation(layers[i].plugInVinegars(this.x), array3);
                    if (solveEquation == null) {
                        throw new Exception("LES is not solveable!");
                    }
                    for (int k = 0; k < solveEquation.length; ++k) {
                        this.x[layers[i].getVi() + k] = solveEquation[k];
                    }
                }
                final short[] multiplyMatrix = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters)this.key).getInvA2(), this.cf.addVect(((RainbowPrivateKeyParameters)this.key).getB2(), this.x));
                for (int l = 0; l < array2.length; ++l) {
                    array2[l] = (byte)multiplyMatrix[l];
                }
            }
            catch (final Exception ex) {
                b = false;
            }
        } while (!b && ++n < 65536);
        if (n == 65536) {
            throw new IllegalStateException("unable to generate signature - LES not solvable");
        }
        return array2;
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        final short[] array3 = new short[array2.length];
        for (int i = 0; i < array2.length; ++i) {
            array3[i] = (short)(array2[i] & 0xFF);
        }
        final short[] messageRepresentative = this.makeMessageRepresentative(array);
        final short[] verifySignatureIntern = this.verifySignatureIntern(array3);
        boolean b = true;
        if (messageRepresentative.length != verifySignatureIntern.length) {
            return false;
        }
        for (int j = 0; j < messageRepresentative.length; ++j) {
            b = (b && messageRepresentative[j] == verifySignatureIntern[j]);
        }
        return b;
    }
    
    private short[] verifySignatureIntern(final short[] array) {
        final short[][] coeffQuadratic = ((RainbowPublicKeyParameters)this.key).getCoeffQuadratic();
        final short[][] coeffSingular = ((RainbowPublicKeyParameters)this.key).getCoeffSingular();
        final short[] coeffScalar = ((RainbowPublicKeyParameters)this.key).getCoeffScalar();
        final short[] array2 = new short[coeffQuadratic.length];
        final int length = coeffSingular[0].length;
        for (int i = 0; i < coeffQuadratic.length; ++i) {
            int n = 0;
            for (int j = 0; j < length; ++j) {
                for (int k = j; k < length; ++k) {
                    array2[i] = GF2Field.addElem(array2[i], GF2Field.multElem(coeffQuadratic[i][n], GF2Field.multElem(array[j], array[k])));
                    ++n;
                }
                array2[i] = GF2Field.addElem(array2[i], GF2Field.multElem(coeffSingular[i][j], array[j]));
            }
            array2[i] = GF2Field.addElem(array2[i], coeffScalar[i]);
        }
        return array2;
    }
    
    private short[] makeMessageRepresentative(final byte[] array) {
        final short[] array2 = new short[this.signableDocumentLength];
        int n = 0;
        int i = 0;
        while (i < array.length) {
            array2[i] = array[n];
            final short[] array3 = array2;
            final int n2 = i;
            array3[n2] &= 0xFF;
            ++n;
            if (++i >= array2.length) {
                return array2;
            }
        }
        return array2;
    }
}
