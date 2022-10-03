package org.bouncycastle.mail.smime.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class CRLFOutputStream extends FilterOutputStream
{
    protected int lastb;
    protected static byte[] newline;
    
    public CRLFOutputStream(final OutputStream outputStream) {
        super(outputStream);
        this.lastb = -1;
    }
    
    @Override
    public void write(final int lastb) throws IOException {
        if (lastb == 13) {
            this.out.write(CRLFOutputStream.newline);
        }
        else if (lastb == 10) {
            if (this.lastb != 13) {
                this.out.write(CRLFOutputStream.newline);
            }
        }
        else {
            this.out.write(lastb);
        }
        this.lastb = lastb;
    }
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        for (int i = n; i != n + n2; ++i) {
            this.write(array[i]);
        }
    }
    
    public void writeln() throws IOException {
        super.out.write(CRLFOutputStream.newline);
    }
    
    static {
        (CRLFOutputStream.newline = new byte[2])[0] = 13;
        CRLFOutputStream.newline[1] = 10;
    }
}
