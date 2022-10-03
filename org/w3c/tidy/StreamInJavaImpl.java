package org.w3c.tidy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;

public class StreamInJavaImpl implements StreamIn
{
    private static final int CHARBUF_SIZE = 16;
    private int[] charbuf;
    private int bufpos;
    private Reader reader;
    private boolean endOfStream;
    private boolean pushed;
    private int curcol;
    private int lastcol;
    private int curline;
    private int tabsize;
    private int tabs;
    
    protected StreamInJavaImpl(final InputStream inputStream, final String s, final int tabsize) throws UnsupportedEncodingException {
        this.charbuf = new int[16];
        this.reader = new InputStreamReader(inputStream, s);
        this.pushed = false;
        this.tabsize = tabsize;
        this.curline = 1;
        this.curcol = 1;
        this.endOfStream = false;
    }
    
    protected StreamInJavaImpl(final Reader reader, final int tabsize) {
        this.charbuf = new int[16];
        this.reader = reader;
        this.pushed = false;
        this.tabsize = tabsize;
        this.curline = 1;
        this.curcol = 1;
        this.endOfStream = false;
    }
    
    public int readCharFromStream() {
        int read;
        try {
            read = this.reader.read();
            if (read < 0) {
                this.endOfStream = true;
            }
        }
        catch (final IOException ex) {
            this.endOfStream = true;
            return -1;
        }
        return read;
    }
    
    public int readChar() {
        if (this.pushed) {
            final int[] charbuf = this.charbuf;
            final int bufpos = this.bufpos - 1;
            this.bufpos = bufpos;
            final int n = charbuf[bufpos];
            if (this.bufpos == 0) {
                this.pushed = false;
            }
            if (n == 10) {
                this.curcol = 1;
                ++this.curline;
                return n;
            }
            ++this.curcol;
            return n;
        }
        else {
            this.lastcol = this.curcol;
            if (this.tabs > 0) {
                ++this.curcol;
                --this.tabs;
                return 32;
            }
            final int charFromStream = this.readCharFromStream();
            if (charFromStream < 0) {
                this.endOfStream = true;
                return -1;
            }
            if (charFromStream == 10) {
                this.curcol = 1;
                ++this.curline;
                return charFromStream;
            }
            if (charFromStream == 13) {
                int charFromStream2 = this.readCharFromStream();
                if (charFromStream2 != 10) {
                    if (charFromStream2 != -1) {
                        this.ungetChar(charFromStream2);
                    }
                    charFromStream2 = 10;
                }
                this.curcol = 1;
                ++this.curline;
                return charFromStream2;
            }
            if (charFromStream == 9) {
                this.tabs = this.tabsize - (this.curcol - 1) % this.tabsize - 1;
                ++this.curcol;
                return 32;
            }
            ++this.curcol;
            return charFromStream;
        }
    }
    
    public void ungetChar(final int n) {
        this.pushed = true;
        if (this.bufpos >= 16) {
            System.arraycopy(this.charbuf, 0, this.charbuf, 1, 15);
            --this.bufpos;
        }
        if ((this.charbuf[this.bufpos++] = n) == 10) {
            --this.curline;
        }
        this.curcol = this.lastcol;
    }
    
    public boolean isEndOfStream() {
        return this.endOfStream;
    }
    
    public int getCurcol() {
        return this.curcol;
    }
    
    public int getCurline() {
        return this.curline;
    }
    
    public void setLexer(final Lexer lexer) {
    }
}
