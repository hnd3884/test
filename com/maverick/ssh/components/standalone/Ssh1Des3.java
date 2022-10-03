package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.ssh.components.SshCipher;

public class Ssh1Des3 extends SshCipher
{
    Ssh1Des j;
    Ssh1Des i;
    Ssh1Des h;
    int k;
    
    public Ssh1Des3() {
        super("3DES");
        this.j = new Ssh1Des();
        this.i = new Ssh1Des();
        this.h = new Ssh1Des();
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public String getAlgorithm() {
        return "3des";
    }
    
    public void init(final int k, final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[8];
        this.k = k;
        System.arraycopy(array2, 0, array3, 0, 8);
        this.j.init((k != 0) ? 1 : 0, array, array3);
        System.arraycopy(array2, 8, array3, 0, 8);
        this.i.init((k == 0) ? 1 : 0, array, array3);
        System.arraycopy(array2, 16, array3, 0, 8);
        this.h.init((k != 0) ? 1 : 0, array, array3);
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (this.k == 0) {
            this.j.transform(array, n, array2, n2, n3);
            this.i.transform(array2, n2, array2, n2, n3);
            this.h.transform(array2, n2, array2, n2, n3);
        }
        else {
            this.h.transform(array, n, array2, n2, n3);
            this.i.transform(array2, n2, array2, n2, n3);
            this.j.transform(array2, n2, array2, n2, n3);
        }
    }
}
