package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16 extends Reader
{
    private InputStream is;
    private char bo;
    
    public ReaderUTF16(final InputStream is, final char bo) {
        switch (bo) {
            case 'l': {
                break;
            }
            case 'b': {
                break;
            }
            default: {
                throw new IllegalArgumentException("");
            }
        }
        this.bo = bo;
        this.is = is;
    }
    
    @Override
    public int read(final char[] array, int n, final int n2) throws IOException {
        int i = 0;
        if (this.bo == 'b') {
            while (i < n2) {
                final int read;
                if ((read = this.is.read()) < 0) {
                    return (i != 0) ? i : -1;
                }
                array[n++] = (char)(read << 8 | (this.is.read() & 0xFF));
                ++i;
            }
        }
        else {
            while (i < n2) {
                final int read2;
                if ((read2 = this.is.read()) < 0) {
                    return (i != 0) ? i : -1;
                }
                array[n++] = (char)(this.is.read() << 8 | (read2 & 0xFF));
                ++i;
            }
        }
        return i;
    }
    
    @Override
    public int read() throws IOException {
        final int read;
        if ((read = this.is.read()) < 0) {
            return -1;
        }
        char c;
        if (this.bo == 'b') {
            c = (char)(read << 8 | (this.is.read() & 0xFF));
        }
        else {
            c = (char)(this.is.read() << 8 | (read & 0xFF));
        }
        return c;
    }
    
    @Override
    public void close() throws IOException {
        this.is.close();
    }
}
