package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.crypto.engines.CipherEngine;
import com.maverick.ssh.components.SshCipher;

class b extends SshCipher
{
    private CipherEngine ab;
    private int eb;
    private byte[] y;
    private byte[] db;
    private byte[] bb;
    private byte[] z;
    private int cb;
    
    public b(final int n, final CipherEngine ab, final String s) {
        super(s);
        this.y = null;
        this.db = null;
        this.bb = null;
        this.z = null;
        this.z = new byte[n / 8];
        this.ab = ab;
        this.cb = ab.getBlockSize();
    }
    
    public void init(final int eb, final byte[] array, final byte[] array2) {
        this.eb = eb;
        System.arraycopy(array2, 0, this.z, 0, this.z.length);
        this.ab.init(eb == 0, this.z);
        System.arraycopy(array, 0, this.y = new byte[this.cb], 0, this.y.length);
        this.db = new byte[this.cb];
        System.arraycopy(this.y, 0, this.db, 0, this.db.length);
        this.bb = new byte[this.cb];
    }
    
    public int getBlockSize() {
        return this.cb;
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (this.db == null) {
            throw new IOException("Cipher not initialized!");
        }
        if (n3 % this.cb != 0) {
            throw new IOException("Input data length MUST be a multiple of the cipher block size!");
        }
        for (int i = 0; i < n3; i += this.cb) {
            switch (this.eb) {
                case 0: {
                    for (int j = 0; j < this.cb; ++j) {
                        this.bb[j] = (byte)(array[n + i + j] ^ this.db[j]);
                    }
                    this.ab.processBlock(this.bb, 0, this.db, 0);
                    System.arraycopy(this.db, 0, array2, n2 + i, this.cb);
                    break;
                }
                case 1: {
                    final byte[] array3 = new byte[this.cb];
                    System.arraycopy(array, n + i, array3, 0, this.cb);
                    this.ab.processBlock(array, n2 + i, this.bb, 0);
                    for (int k = 0; k < this.cb; ++k) {
                        array2[n2 + k + i] = (byte)(this.bb[k] ^ this.db[k]);
                    }
                    System.arraycopy(array3, 0, this.db, 0, this.cb);
                    break;
                }
                default: {
                    throw new IOException("Invalid cipher mode!");
                }
            }
        }
    }
}
