package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;
import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;
import java.security.SecureRandom;

public class Layer
{
    private int vi;
    private int viNext;
    private int oi;
    private short[][][] coeff_alpha;
    private short[][][] coeff_beta;
    private short[][] coeff_gamma;
    private short[] coeff_eta;
    
    public Layer(final byte b, final byte b2, final short[][][] coeff_alpha, final short[][][] coeff_beta, final short[][] coeff_gamma, final short[] coeff_eta) {
        this.vi = (b & 0xFF);
        this.viNext = (b2 & 0xFF);
        this.oi = this.viNext - this.vi;
        this.coeff_alpha = coeff_alpha;
        this.coeff_beta = coeff_beta;
        this.coeff_gamma = coeff_gamma;
        this.coeff_eta = coeff_eta;
    }
    
    public Layer(final int vi, final int viNext, final SecureRandom secureRandom) {
        this.vi = vi;
        this.viNext = viNext;
        this.oi = viNext - vi;
        this.coeff_alpha = new short[this.oi][this.oi][this.vi];
        this.coeff_beta = new short[this.oi][this.vi][this.vi];
        this.coeff_gamma = new short[this.oi][this.viNext];
        this.coeff_eta = new short[this.oi];
        final int oi = this.oi;
        for (int i = 0; i < oi; ++i) {
            for (int j = 0; j < this.oi; ++j) {
                for (int k = 0; k < this.vi; ++k) {
                    this.coeff_alpha[i][j][k] = (short)(secureRandom.nextInt() & 0xFF);
                }
            }
        }
        for (int l = 0; l < oi; ++l) {
            for (int n = 0; n < this.vi; ++n) {
                for (int n2 = 0; n2 < this.vi; ++n2) {
                    this.coeff_beta[l][n][n2] = (short)(secureRandom.nextInt() & 0xFF);
                }
            }
        }
        for (int n3 = 0; n3 < oi; ++n3) {
            for (int n4 = 0; n4 < this.viNext; ++n4) {
                this.coeff_gamma[n3][n4] = (short)(secureRandom.nextInt() & 0xFF);
            }
        }
        for (int n5 = 0; n5 < oi; ++n5) {
            this.coeff_eta[n5] = (short)(secureRandom.nextInt() & 0xFF);
        }
    }
    
    public short[][] plugInVinegars(final short[] array) {
        final short[][] array2 = new short[this.oi][this.oi + 1];
        final short[] array3 = new short[this.oi];
        for (int i = 0; i < this.oi; ++i) {
            for (int j = 0; j < this.vi; ++j) {
                for (int k = 0; k < this.vi; ++k) {
                    array3[i] = GF2Field.addElem(array3[i], GF2Field.multElem(GF2Field.multElem(this.coeff_beta[i][j][k], array[j]), array[k]));
                }
            }
        }
        for (int l = 0; l < this.oi; ++l) {
            for (int n = 0; n < this.oi; ++n) {
                for (int n2 = 0; n2 < this.vi; ++n2) {
                    array2[l][n] = GF2Field.addElem(array2[l][n], GF2Field.multElem(this.coeff_alpha[l][n][n2], array[n2]));
                }
            }
        }
        for (int n3 = 0; n3 < this.oi; ++n3) {
            for (int n4 = 0; n4 < this.vi; ++n4) {
                array3[n3] = GF2Field.addElem(array3[n3], GF2Field.multElem(this.coeff_gamma[n3][n4], array[n4]));
            }
        }
        for (int n5 = 0; n5 < this.oi; ++n5) {
            for (int vi = this.vi; vi < this.viNext; ++vi) {
                array2[n5][vi - this.vi] = GF2Field.addElem(this.coeff_gamma[n5][vi], array2[n5][vi - this.vi]);
            }
        }
        for (int n6 = 0; n6 < this.oi; ++n6) {
            array3[n6] = GF2Field.addElem(array3[n6], this.coeff_eta[n6]);
        }
        for (int n7 = 0; n7 < this.oi; ++n7) {
            array2[n7][this.oi] = array3[n7];
        }
        return array2;
    }
    
    public int getVi() {
        return this.vi;
    }
    
    public int getViNext() {
        return this.viNext;
    }
    
    public int getOi() {
        return this.oi;
    }
    
    public short[][][] getCoeffAlpha() {
        return this.coeff_alpha;
    }
    
    public short[][][] getCoeffBeta() {
        return this.coeff_beta;
    }
    
    public short[][] getCoeffGamma() {
        return this.coeff_gamma;
    }
    
    public short[] getCoeffEta() {
        return this.coeff_eta;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Layer)) {
            return false;
        }
        final Layer layer = (Layer)o;
        return this.vi == layer.getVi() && this.viNext == layer.getViNext() && this.oi == layer.getOi() && RainbowUtil.equals(this.coeff_alpha, layer.getCoeffAlpha()) && RainbowUtil.equals(this.coeff_beta, layer.getCoeffBeta()) && RainbowUtil.equals(this.coeff_gamma, layer.getCoeffGamma()) && RainbowUtil.equals(this.coeff_eta, layer.getCoeffEta());
    }
    
    @Override
    public int hashCode() {
        return (((((this.vi * 37 + this.viNext) * 37 + this.oi) * 37 + Arrays.hashCode(this.coeff_alpha)) * 37 + Arrays.hashCode(this.coeff_beta)) * 37 + Arrays.hashCode(this.coeff_gamma)) * 37 + Arrays.hashCode(this.coeff_eta);
    }
}
