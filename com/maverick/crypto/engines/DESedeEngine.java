package com.maverick.crypto.engines;

import java.io.IOException;

public class DESedeEngine extends DESEngine
{
    protected static final int BLOCK_SIZE = 8;
    private int[] ib;
    private int[] gb;
    private int[] fb;
    private boolean hb;
    
    public DESedeEngine() {
        this.ib = null;
        this.gb = null;
        this.fb = null;
    }
    
    public void init(final boolean hb, final byte[] array) {
        final byte[] array2 = new byte[8];
        final byte[] array3 = new byte[8];
        final byte[] array4 = new byte[8];
        this.hb = hb;
        if (array.length == 24) {
            System.arraycopy(array, 0, array2, 0, array2.length);
            System.arraycopy(array, 8, array3, 0, array3.length);
            System.arraycopy(array, 16, array4, 0, array4.length);
            this.ib = this.generateWorkingKey(hb, array2);
            this.gb = this.generateWorkingKey(!hb, array3);
            this.fb = this.generateWorkingKey(hb, array4);
        }
        else {
            System.arraycopy(array, 0, array2, 0, array2.length);
            System.arraycopy(array, 8, array3, 0, array3.length);
            this.ib = this.generateWorkingKey(hb, array2);
            this.gb = this.generateWorkingKey(!hb, array3);
            this.fb = this.ib;
        }
    }
    
    public String getAlgorithmName() {
        return "DESede";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws IOException {
        if (this.ib == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (n + 8 > array.length) {
            throw new IOException("input buffer too short");
        }
        if (n2 + 8 > array2.length) {
            throw new IOException("output buffer too short");
        }
        if (this.hb) {
            this.desFunc(this.ib, array, n, array2, n2);
            this.desFunc(this.gb, array2, n2, array2, n2);
            this.desFunc(this.fb, array2, n2, array2, n2);
        }
        else {
            this.desFunc(this.fb, array, n, array2, n2);
            this.desFunc(this.gb, array2, n2, array2, n2);
            this.desFunc(this.ib, array2, n2, array2, n2);
        }
        return 8;
    }
    
    public void reset() {
    }
}
