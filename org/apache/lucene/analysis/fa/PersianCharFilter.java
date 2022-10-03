package org.apache.lucene.analysis.fa;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.CharFilter;

public class PersianCharFilter extends CharFilter
{
    public PersianCharFilter(final Reader in) {
        super(in);
    }
    
    public int read(final char[] cbuf, int off, final int len) throws IOException {
        final int charsRead = this.input.read(cbuf, off, len);
        if (charsRead > 0) {
            for (int end = off + charsRead; off < end; ++off) {
                if (cbuf[off] == '\u200c') {
                    cbuf[off] = ' ';
                }
            }
        }
        return charsRead;
    }
    
    public int read() throws IOException {
        final int ch = this.input.read();
        if (ch == 8204) {
            return 32;
        }
        return ch;
    }
    
    protected int correct(final int currentOff) {
        return currentOff;
    }
}
