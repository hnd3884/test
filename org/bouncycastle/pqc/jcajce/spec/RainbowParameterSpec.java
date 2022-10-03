package org.bouncycastle.pqc.jcajce.spec;

import org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class RainbowParameterSpec implements AlgorithmParameterSpec
{
    private static final int[] DEFAULT_VI;
    private int[] vi;
    
    public RainbowParameterSpec() {
        this.vi = RainbowParameterSpec.DEFAULT_VI;
    }
    
    public RainbowParameterSpec(final int[] vi) {
        this.vi = vi;
        this.checkParams();
    }
    
    private void checkParams() {
        if (this.vi == null) {
            throw new IllegalArgumentException("no layers defined.");
        }
        if (this.vi.length > 1) {
            for (int i = 0; i < this.vi.length - 1; ++i) {
                if (this.vi[i] >= this.vi[i + 1]) {
                    throw new IllegalArgumentException("v[i] has to be smaller than v[i+1]");
                }
            }
            return;
        }
        throw new IllegalArgumentException("Rainbow needs at least 1 layer, such that v1 < v2.");
    }
    
    public int getNumOfLayers() {
        return this.vi.length - 1;
    }
    
    public int getDocumentLength() {
        return this.vi[this.vi.length - 1] - this.vi[0];
    }
    
    public int[] getVi() {
        return Arrays.clone(this.vi);
    }
    
    static {
        DEFAULT_VI = new int[] { 6, 12, 17, 22, 33 };
    }
}
