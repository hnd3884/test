package com.sun.mail.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class CRLFOutputStream extends FilterOutputStream
{
    protected int lastb;
    protected boolean atBOL;
    private static final byte[] newline;
    
    public CRLFOutputStream(final OutputStream os) {
        super(os);
        this.lastb = -1;
        this.atBOL = true;
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (b == 13) {
            this.writeln();
        }
        else if (b == 10) {
            if (this.lastb != 13) {
                this.writeln();
            }
        }
        else {
            this.out.write(b);
            this.atBOL = false;
        }
        this.lastb = b;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, int len) throws IOException {
        int start = off;
        len += off;
        for (int i = start; i < len; ++i) {
            if (b[i] == 13) {
                this.out.write(b, start, i - start);
                this.writeln();
                start = i + 1;
            }
            else if (b[i] == 10) {
                if (this.lastb != 13) {
                    this.out.write(b, start, i - start);
                    this.writeln();
                }
                start = i + 1;
            }
            this.lastb = b[i];
        }
        if (len - start > 0) {
            this.out.write(b, start, len - start);
            this.atBOL = false;
        }
    }
    
    public void writeln() throws IOException {
        this.out.write(CRLFOutputStream.newline);
        this.atBOL = true;
    }
    
    static {
        newline = new byte[] { 13, 10 };
    }
}
