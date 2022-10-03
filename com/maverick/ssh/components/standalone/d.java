package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.crypto.engines.CipherEngine;
import com.maverick.ssh.components.SshCipher;

class d extends SshCipher
{
    private CipherEngine p;
    private byte[] n;
    private byte[] o;
    private byte[] l;
    private byte[] m;
    
    public d(final int n, final CipherEngine p3, final String s) {
        super(s);
        this.n = null;
        this.o = null;
        this.l = null;
        this.m = null;
        this.o = new byte[n / 8];
        this.p = p3;
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (n3 % this.getBlockSize() != 0) {
            throw new IOException("Input data length MUST be a multiple of the cipher block size!");
        }
        for (int i = 0; i < n3; i += this.getBlockSize()) {
            this.b(array, n + i, array2, i + n2);
        }
    }
    
    public void init(final int n, final byte[] array, final byte[] array2) throws IOException {
        System.arraycopy(array2, 0, this.o, 0, this.o.length);
        this.p.init(true, this.o);
        System.arraycopy(array, 0, this.n = new byte[this.getBlockSize()], 0, this.n.length);
        this.l = new byte[this.getBlockSize()];
        this.m = new byte[this.getBlockSize()];
        System.arraycopy(array, 0, this.l, 0, this.l.length);
    }
    
    public int getBlockSize() {
        return this.p.getBlockSize();
    }
    
    public int b(final byte[] array, final int n, final byte[] array2, final int n2) throws IOException {
        this.p.processBlock(this.l, 0, this.m, 0);
        for (int i = 0; i < this.m.length; ++i) {
            array2[n2 + i] = (byte)(this.m[i] ^ array[n + i]);
        }
        int n3 = 1;
        for (int j = this.l.length - 1; j >= 0; --j) {
            final int n4 = (this.l[j] & 0xFF) + n3;
            if (n4 > 255) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            this.l[j] = (byte)n4;
        }
        return this.l.length;
    }
}
