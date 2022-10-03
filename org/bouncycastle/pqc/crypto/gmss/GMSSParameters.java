package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.Arrays;

public class GMSSParameters
{
    private int numOfLayers;
    private int[] heightOfTrees;
    private int[] winternitzParameter;
    private int[] K;
    
    public GMSSParameters(final int n, final int[] array, final int[] array2, final int[] array3) throws IllegalArgumentException {
        this.init(n, array, array2, array3);
    }
    
    private void init(final int numOfLayers, final int[] array, final int[] array2, final int[] array3) throws IllegalArgumentException {
        boolean b = true;
        String s = "";
        this.numOfLayers = numOfLayers;
        if (this.numOfLayers != array2.length || this.numOfLayers != array.length || this.numOfLayers != array3.length) {
            b = false;
            s = "Unexpected parameterset format";
        }
        for (int i = 0; i < this.numOfLayers; ++i) {
            if (array3[i] < 2 || (array[i] - array3[i]) % 2 != 0) {
                b = false;
                s = "Wrong parameter K (K >= 2 and H-K even required)!";
            }
            if (array[i] < 4 || array2[i] < 2) {
                b = false;
                s = "Wrong parameter H or w (H > 3 and w > 1 required)!";
            }
        }
        if (b) {
            this.heightOfTrees = Arrays.clone(array);
            this.winternitzParameter = Arrays.clone(array2);
            this.K = Arrays.clone(array3);
            return;
        }
        throw new IllegalArgumentException(s);
    }
    
    public GMSSParameters(final int n) throws IllegalArgumentException {
        if (n <= 10) {
            final int[] array = { 10 };
            this.init(array.length, array, new int[] { 3 }, new int[] { 2 });
        }
        else if (n <= 20) {
            final int[] array2 = { 10, 10 };
            this.init(array2.length, array2, new int[] { 5, 4 }, new int[] { 2, 2 });
        }
        else {
            final int[] array3 = { 10, 10, 10, 10 };
            this.init(array3.length, array3, new int[] { 9, 9, 9, 3 }, new int[] { 2, 2, 2, 2 });
        }
    }
    
    public int getNumOfLayers() {
        return this.numOfLayers;
    }
    
    public int[] getHeightOfTrees() {
        return Arrays.clone(this.heightOfTrees);
    }
    
    public int[] getWinternitzParameter() {
        return Arrays.clone(this.winternitzParameter);
    }
    
    public int[] getK() {
        return Arrays.clone(this.K);
    }
}
