package com.maverick.ssh.components.jce;

import java.io.IOException;
import com.maverick.ssh.components.SshCipher;

public class Ssh1Des3 extends SshCipher
{
    Ssh1Des s;
    Ssh1Des r;
    Ssh1Des q;
    int t;
    
    public Ssh1Des3() throws IOException {
        super("3DES");
        this.s = new Ssh1Des();
        this.r = new Ssh1Des();
        this.q = new Ssh1Des();
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public String getAlgorithm() {
        return "3des";
    }
    
    public void init(final int t, final byte[] array, final byte[] array2) throws IOException {
        final byte[] array3 = new byte[8];
        this.t = t;
        System.arraycopy(array2, 0, array3, 0, 8);
        this.s.init((t != 0) ? 1 : 0, array, array3);
        System.arraycopy(array2, 8, array3, 0, 8);
        this.r.init((t == 0) ? 1 : 0, array, array3);
        System.arraycopy(array2, 16, array3, 0, 8);
        this.q.init((t != 0) ? 1 : 0, array, array3);
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (this.t == 0) {
            this.s.transform(array, n, array2, n2, n3);
            this.r.transform(array2, n2, array2, n2, n3);
            this.q.transform(array2, n2, array2, n2, n3);
        }
        else {
            this.q.transform(array, n, array2, n2, n3);
            this.r.transform(array2, n2, array2, n2, n3);
            this.s.transform(array2, n2, array2, n2, n3);
        }
    }
}
