package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.crypto.engines.DESEngine;
import com.maverick.ssh.components.SshCipher;

public class Ssh1Des extends SshCipher
{
    byte[] d;
    byte[] c;
    byte[] g;
    int f;
    DESEngine e;
    
    public Ssh1Des() {
        super("DES");
        this.e = new DESEngine();
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public String getAlgorithm() {
        return "des";
    }
    
    public void init(final int f, final byte[] array, final byte[] array2) {
        this.d = new byte[8];
        this.c = new byte[8];
        this.g = new byte[8];
        this.f = f;
        this.e.init(f == 0, array2);
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (this.f == 0) {
            this.encrypt(array, n, array2, n2, n3);
        }
        else {
            this.decrypt(array, n, array2, n2, n3);
        }
    }
    
    public synchronized void encrypt(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        for (int n4 = n + n3, i = n, n5 = n2; i < n4; i += 8, n5 += 8) {
            for (int j = 0; j < 8; ++j) {
                final byte[] d = this.d;
                final int n6 = j;
                d[n6] ^= array[i + j];
            }
            this.e.processBlock(this.d, 0, this.d, 0);
            for (int k = 0; k < 8; ++k) {
                array2[n5 + k] = this.d[k];
            }
        }
    }
    
    public synchronized void decrypt(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        for (int n4 = n + n3, i = n, n5 = n2; i < n4; i += 8, n5 += 8) {
            for (int j = 0; j < 8; ++j) {
                this.c[j] = array[i + j];
            }
            this.e.processBlock(this.c, 0, this.g, 0);
            for (int k = 0; k < 8; ++k) {
                array2[n5 + k] = (byte)((this.d[k] ^ this.g[k]) & 0xFF);
                this.d[k] = this.c[k];
            }
        }
    }
}
