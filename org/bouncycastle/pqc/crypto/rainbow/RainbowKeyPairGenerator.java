package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;
import org.bouncycastle.pqc.crypto.rainbow.util.ComputeInField;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class RainbowKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private boolean initialized;
    private SecureRandom sr;
    private RainbowKeyGenerationParameters rainbowParams;
    private short[][] A1;
    private short[][] A1inv;
    private short[] b1;
    private short[][] A2;
    private short[][] A2inv;
    private short[] b2;
    private int numOfLayers;
    private Layer[] layers;
    private int[] vi;
    private short[][] pub_quadratic;
    private short[][] pub_singular;
    private short[] pub_scalar;
    
    public RainbowKeyPairGenerator() {
        this.initialized = false;
    }
    
    public AsymmetricCipherKeyPair genKeyPair() {
        if (!this.initialized) {
            this.initializeDefault();
        }
        this.keygen();
        return new AsymmetricCipherKeyPair(new RainbowPublicKeyParameters(this.vi[this.vi.length - 1] - this.vi[0], this.pub_quadratic, this.pub_singular, this.pub_scalar), new RainbowPrivateKeyParameters(this.A1inv, this.b1, this.A2inv, this.b2, this.vi, this.layers));
    }
    
    public void initialize(final KeyGenerationParameters keyGenerationParameters) {
        this.rainbowParams = (RainbowKeyGenerationParameters)keyGenerationParameters;
        this.sr = this.rainbowParams.getRandom();
        this.vi = this.rainbowParams.getParameters().getVi();
        this.numOfLayers = this.rainbowParams.getParameters().getNumOfLayers();
        this.initialized = true;
    }
    
    private void initializeDefault() {
        this.initialize(new RainbowKeyGenerationParameters(new SecureRandom(), new RainbowParameters()));
    }
    
    private void keygen() {
        this.generateL1();
        this.generateL2();
        this.generateF();
        this.computePublicKey();
    }
    
    private void generateL1() {
        final int n = this.vi[this.vi.length - 1] - this.vi[0];
        this.A1 = new short[n][n];
        this.A1inv = null;
        final ComputeInField computeInField = new ComputeInField();
        while (this.A1inv == null) {
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    this.A1[i][j] = (short)(this.sr.nextInt() & 0xFF);
                }
            }
            this.A1inv = computeInField.inverse(this.A1);
        }
        this.b1 = new short[n];
        for (int k = 0; k < n; ++k) {
            this.b1[k] = (short)(this.sr.nextInt() & 0xFF);
        }
    }
    
    private void generateL2() {
        final int n = this.vi[this.vi.length - 1];
        this.A2 = new short[n][n];
        this.A2inv = null;
        final ComputeInField computeInField = new ComputeInField();
        while (this.A2inv == null) {
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    this.A2[i][j] = (short)(this.sr.nextInt() & 0xFF);
                }
            }
            this.A2inv = computeInField.inverse(this.A2);
        }
        this.b2 = new short[n];
        for (int k = 0; k < n; ++k) {
            this.b2[k] = (short)(this.sr.nextInt() & 0xFF);
        }
    }
    
    private void generateF() {
        this.layers = new Layer[this.numOfLayers];
        for (int i = 0; i < this.numOfLayers; ++i) {
            this.layers[i] = new Layer(this.vi[i], this.vi[i + 1], this.sr);
        }
    }
    
    private void computePublicKey() {
        final ComputeInField computeInField = new ComputeInField();
        final int n = this.vi[this.vi.length - 1] - this.vi[0];
        final int n2 = this.vi[this.vi.length - 1];
        final short[][][] array = new short[n][n2][n2];
        this.pub_singular = new short[n][n2];
        this.pub_scalar = new short[n];
        int n3 = 0;
        final short[] array2 = new short[n2];
        for (int i = 0; i < this.layers.length; ++i) {
            final short[][][] coeffAlpha = this.layers[i].getCoeffAlpha();
            final short[][][] coeffBeta = this.layers[i].getCoeffBeta();
            final short[][] coeffGamma = this.layers[i].getCoeffGamma();
            final short[] coeffEta = this.layers[i].getCoeffEta();
            final int length = coeffAlpha[0].length;
            final int length2 = coeffBeta[0].length;
            for (int j = 0; j < length; ++j) {
                for (int k = 0; k < length; ++k) {
                    for (int l = 0; l < length2; ++l) {
                        final short[] multVect = computeInField.multVect(coeffAlpha[j][k][l], this.A2[k + length2]);
                        array[n3 + j] = computeInField.addSquareMatrix(array[n3 + j], computeInField.multVects(multVect, this.A2[l]));
                        this.pub_singular[n3 + j] = computeInField.addVect(computeInField.multVect(this.b2[l], multVect), this.pub_singular[n3 + j]);
                        this.pub_singular[n3 + j] = computeInField.addVect(computeInField.multVect(this.b2[k + length2], computeInField.multVect(coeffAlpha[j][k][l], this.A2[l])), this.pub_singular[n3 + j]);
                        this.pub_scalar[n3 + j] = GF2Field.addElem(this.pub_scalar[n3 + j], GF2Field.multElem(GF2Field.multElem(coeffAlpha[j][k][l], this.b2[k + length2]), this.b2[l]));
                    }
                }
                for (int n4 = 0; n4 < length2; ++n4) {
                    for (int n5 = 0; n5 < length2; ++n5) {
                        final short[] multVect2 = computeInField.multVect(coeffBeta[j][n4][n5], this.A2[n4]);
                        array[n3 + j] = computeInField.addSquareMatrix(array[n3 + j], computeInField.multVects(multVect2, this.A2[n5]));
                        this.pub_singular[n3 + j] = computeInField.addVect(computeInField.multVect(this.b2[n5], multVect2), this.pub_singular[n3 + j]);
                        this.pub_singular[n3 + j] = computeInField.addVect(computeInField.multVect(this.b2[n4], computeInField.multVect(coeffBeta[j][n4][n5], this.A2[n5])), this.pub_singular[n3 + j]);
                        this.pub_scalar[n3 + j] = GF2Field.addElem(this.pub_scalar[n3 + j], GF2Field.multElem(GF2Field.multElem(coeffBeta[j][n4][n5], this.b2[n4]), this.b2[n5]));
                    }
                }
                for (int n6 = 0; n6 < length2 + length; ++n6) {
                    this.pub_singular[n3 + j] = computeInField.addVect(computeInField.multVect(coeffGamma[j][n6], this.A2[n6]), this.pub_singular[n3 + j]);
                    this.pub_scalar[n3 + j] = GF2Field.addElem(this.pub_scalar[n3 + j], GF2Field.multElem(coeffGamma[j][n6], this.b2[n6]));
                }
                this.pub_scalar[n3 + j] = GF2Field.addElem(this.pub_scalar[n3 + j], coeffEta[j]);
            }
            n3 += length;
        }
        final short[][][] array3 = new short[n][n2][n2];
        final short[][] pub_singular = new short[n][n2];
        final short[] pub_scalar = new short[n];
        for (int n7 = 0; n7 < n; ++n7) {
            for (int n8 = 0; n8 < this.A1.length; ++n8) {
                array3[n7] = computeInField.addSquareMatrix(array3[n7], computeInField.multMatrix(this.A1[n7][n8], array[n8]));
                pub_singular[n7] = computeInField.addVect(pub_singular[n7], computeInField.multVect(this.A1[n7][n8], this.pub_singular[n8]));
                pub_scalar[n7] = GF2Field.addElem(pub_scalar[n7], GF2Field.multElem(this.A1[n7][n8], this.pub_scalar[n8]));
            }
            pub_scalar[n7] = GF2Field.addElem(pub_scalar[n7], this.b1[n7]);
        }
        final short[][][] array4 = array3;
        this.pub_singular = pub_singular;
        this.pub_scalar = pub_scalar;
        this.compactPublicKey(array4);
    }
    
    private void compactPublicKey(final short[][][] array) {
        final int length = array.length;
        final int length2 = array[0].length;
        this.pub_quadratic = new short[length][length2 * (length2 + 1) / 2];
        for (int i = 0; i < length; ++i) {
            int n = 0;
            for (int j = 0; j < length2; ++j) {
                for (int k = j; k < length2; ++k) {
                    if (k == j) {
                        this.pub_quadratic[i][n] = array[i][j][k];
                    }
                    else {
                        this.pub_quadratic[i][n] = GF2Field.addElem(array[i][j][k], array[i][k][j]);
                    }
                    ++n;
                }
            }
        }
    }
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}
