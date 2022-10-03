package com.github.odiszapc.nginxparser.javacc;

import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

public class SimpleCharStream
{
    public static final boolean staticFlag = false;
    int bufsize;
    int available;
    int tokenBegin;
    public int bufpos;
    protected int[] bufline;
    protected int[] bufcolumn;
    protected int column;
    protected int line;
    protected boolean prevCharIsCR;
    protected boolean prevCharIsLF;
    protected Reader inputStream;
    protected char[] buffer;
    protected int maxNextCharInd;
    protected int inBuf;
    protected int tabSize;
    protected boolean trackLineColumn;
    
    public void setTabSize(final int tabSize) {
        this.tabSize = tabSize;
    }
    
    public int getTabSize() {
        return this.tabSize;
    }
    
    protected void ExpandBuff(final boolean b) {
        final char[] array = new char[this.bufsize + 2048];
        final int[] array2 = new int[this.bufsize + 2048];
        final int[] array3 = new int[this.bufsize + 2048];
        try {
            if (b) {
                System.arraycopy(this.buffer, this.tokenBegin, array, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.buffer, 0, array, this.bufsize - this.tokenBegin, this.bufpos);
                this.buffer = array;
                System.arraycopy(this.bufline, this.tokenBegin, array2, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufline, 0, array2, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufline = array2;
                System.arraycopy(this.bufcolumn, this.tokenBegin, array3, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufcolumn, 0, array3, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufcolumn = array3;
                final int n = this.bufpos + (this.bufsize - this.tokenBegin);
                this.bufpos = n;
                this.maxNextCharInd = n;
            }
            else {
                System.arraycopy(this.buffer, this.tokenBegin, array, 0, this.bufsize - this.tokenBegin);
                this.buffer = array;
                System.arraycopy(this.bufline, this.tokenBegin, array2, 0, this.bufsize - this.tokenBegin);
                this.bufline = array2;
                System.arraycopy(this.bufcolumn, this.tokenBegin, array3, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = array3;
                final int n2 = this.bufpos - this.tokenBegin;
                this.bufpos = n2;
                this.maxNextCharInd = n2;
            }
        }
        catch (final Throwable t) {
            throw new Error(t.getMessage());
        }
        this.bufsize += 2048;
        this.available = this.bufsize;
        this.tokenBegin = 0;
    }
    
    protected void FillBuff() throws IOException {
        if (this.maxNextCharInd == this.available) {
            if (this.available == this.bufsize) {
                if (this.tokenBegin > 2048) {
                    final int n = 0;
                    this.maxNextCharInd = n;
                    this.bufpos = n;
                    this.available = this.tokenBegin;
                }
                else if (this.tokenBegin < 0) {
                    final int n2 = 0;
                    this.maxNextCharInd = n2;
                    this.bufpos = n2;
                }
                else {
                    this.ExpandBuff(false);
                }
            }
            else if (this.available > this.tokenBegin) {
                this.available = this.bufsize;
            }
            else if (this.tokenBegin - this.available < 2048) {
                this.ExpandBuff(true);
            }
            else {
                this.available = this.tokenBegin;
            }
        }
        try {
            final int read;
            if ((read = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
                this.inputStream.close();
                throw new IOException();
            }
            this.maxNextCharInd += read;
        }
        catch (final IOException ex) {
            --this.bufpos;
            this.backup(0);
            if (this.tokenBegin == -1) {
                this.tokenBegin = this.bufpos;
            }
            throw ex;
        }
    }
    
    public char BeginToken() throws IOException {
        this.tokenBegin = -1;
        final char char1 = this.readChar();
        this.tokenBegin = this.bufpos;
        return char1;
    }
    
    protected void UpdateLineColumn(final char c) {
        ++this.column;
        if (this.prevCharIsLF) {
            this.prevCharIsLF = false;
            final int line = this.line;
            final int column = 1;
            this.column = column;
            this.line = line + column;
        }
        else if (this.prevCharIsCR) {
            this.prevCharIsCR = false;
            if (c == '\n') {
                this.prevCharIsLF = true;
            }
            else {
                final int line2 = this.line;
                final int column2 = 1;
                this.column = column2;
                this.line = line2 + column2;
            }
        }
        switch (c) {
            case '\r': {
                this.prevCharIsCR = true;
                break;
            }
            case '\n': {
                this.prevCharIsLF = true;
                break;
            }
            case '\t': {
                --this.column;
                this.column += this.tabSize - this.column % this.tabSize;
                break;
            }
        }
        this.bufline[this.bufpos] = this.line;
        this.bufcolumn[this.bufpos] = this.column;
    }
    
    public char readChar() throws IOException {
        if (this.inBuf > 0) {
            --this.inBuf;
            if (++this.bufpos == this.bufsize) {
                this.bufpos = 0;
            }
            return this.buffer[this.bufpos];
        }
        if (++this.bufpos >= this.maxNextCharInd) {
            this.FillBuff();
        }
        final char c = this.buffer[this.bufpos];
        this.UpdateLineColumn(c);
        return c;
    }
    
    @Deprecated
    public int getColumn() {
        return this.bufcolumn[this.bufpos];
    }
    
    @Deprecated
    public int getLine() {
        return this.bufline[this.bufpos];
    }
    
    public int getEndColumn() {
        return this.bufcolumn[this.bufpos];
    }
    
    public int getEndLine() {
        return this.bufline[this.bufpos];
    }
    
    public int getBeginColumn() {
        return this.bufcolumn[this.tokenBegin];
    }
    
    public int getBeginLine() {
        return this.bufline[this.tokenBegin];
    }
    
    public void backup(final int n) {
        this.inBuf += n;
        final int bufpos = this.bufpos - n;
        this.bufpos = bufpos;
        if (bufpos < 0) {
            this.bufpos += this.bufsize;
        }
    }
    
    public SimpleCharStream(final Reader inputStream, final int line, final int n, final int n2) {
        this.bufpos = -1;
        this.column = 0;
        this.line = 1;
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.inBuf = 0;
        this.tabSize = 8;
        this.trackLineColumn = true;
        this.inputStream = inputStream;
        this.line = line;
        this.column = n - 1;
        this.bufsize = n2;
        this.available = n2;
        this.buffer = new char[n2];
        this.bufline = new int[n2];
        this.bufcolumn = new int[n2];
    }
    
    public SimpleCharStream(final Reader reader, final int n, final int n2) {
        this(reader, n, n2, 4096);
    }
    
    public SimpleCharStream(final Reader reader) {
        this(reader, 1, 1, 4096);
    }
    
    public void ReInit(final Reader inputStream, final int line, final int n, final int n2) {
        this.inputStream = inputStream;
        this.line = line;
        this.column = n - 1;
        if (this.buffer == null || n2 != this.buffer.length) {
            this.bufsize = n2;
            this.available = n2;
            this.buffer = new char[n2];
            this.bufline = new int[n2];
            this.bufcolumn = new int[n2];
        }
        final boolean b = false;
        this.prevCharIsCR = b;
        this.prevCharIsLF = b;
        final int tokenBegin = 0;
        this.maxNextCharInd = tokenBegin;
        this.inBuf = tokenBegin;
        this.tokenBegin = tokenBegin;
        this.bufpos = -1;
    }
    
    public void ReInit(final Reader reader, final int n, final int n2) {
        this.ReInit(reader, n, n2, 4096);
    }
    
    public void ReInit(final Reader reader) {
        this.ReInit(reader, 1, 1, 4096);
    }
    
    public SimpleCharStream(final InputStream inputStream, final String s, final int n, final int n2, final int n3) throws UnsupportedEncodingException {
        this((s == null) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, s), n, n2, n3);
    }
    
    public SimpleCharStream(final InputStream inputStream, final int n, final int n2, final int n3) {
        this(new InputStreamReader(inputStream), n, n2, n3);
    }
    
    public SimpleCharStream(final InputStream inputStream, final String s, final int n, final int n2) throws UnsupportedEncodingException {
        this(inputStream, s, n, n2, 4096);
    }
    
    public SimpleCharStream(final InputStream inputStream, final int n, final int n2) {
        this(inputStream, n, n2, 4096);
    }
    
    public SimpleCharStream(final InputStream inputStream, final String s) throws UnsupportedEncodingException {
        this(inputStream, s, 1, 1, 4096);
    }
    
    public SimpleCharStream(final InputStream inputStream) {
        this(inputStream, 1, 1, 4096);
    }
    
    public void ReInit(final InputStream inputStream, final String s, final int n, final int n2, final int n3) throws UnsupportedEncodingException {
        this.ReInit((s == null) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, s), n, n2, n3);
    }
    
    public void ReInit(final InputStream inputStream, final int n, final int n2, final int n3) {
        this.ReInit(new InputStreamReader(inputStream), n, n2, n3);
    }
    
    public void ReInit(final InputStream inputStream, final String s) throws UnsupportedEncodingException {
        this.ReInit(inputStream, s, 1, 1, 4096);
    }
    
    public void ReInit(final InputStream inputStream) {
        this.ReInit(inputStream, 1, 1, 4096);
    }
    
    public void ReInit(final InputStream inputStream, final String s, final int n, final int n2) throws UnsupportedEncodingException {
        this.ReInit(inputStream, s, n, n2, 4096);
    }
    
    public void ReInit(final InputStream inputStream, final int n, final int n2) {
        this.ReInit(inputStream, n, n2, 4096);
    }
    
    public String GetImage() {
        if (this.bufpos >= this.tokenBegin) {
            return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
        }
        return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
    }
    
    public char[] GetSuffix(final int n) {
        final char[] array = new char[n];
        if (this.bufpos + 1 >= n) {
            System.arraycopy(this.buffer, this.bufpos - n + 1, array, 0, n);
        }
        else {
            System.arraycopy(this.buffer, this.bufsize - (n - this.bufpos - 1), array, 0, n - this.bufpos - 1);
            System.arraycopy(this.buffer, 0, array, n - this.bufpos - 1, this.bufpos + 1);
        }
        return array;
    }
    
    public void Done() {
        this.buffer = null;
        this.bufline = null;
        this.bufcolumn = null;
    }
    
    public void adjustBeginLineColumn(int n, final int n2) {
        int tokenBegin = this.tokenBegin;
        int n3;
        if (this.bufpos >= this.tokenBegin) {
            n3 = this.bufpos - this.tokenBegin + this.inBuf + 1;
        }
        else {
            n3 = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
        }
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7;
        while (n4 < n3 && this.bufline[n5 = tokenBegin % this.bufsize] == this.bufline[n7 = ++tokenBegin % this.bufsize]) {
            this.bufline[n5] = n;
            final int n8 = n6 + this.bufcolumn[n7] - this.bufcolumn[n5];
            this.bufcolumn[n5] = n2 + n6;
            n6 = n8;
            ++n4;
        }
        if (n4 < n3) {
            this.bufline[n5] = n++;
            this.bufcolumn[n5] = n2 + n6;
            while (n4++ < n3) {
                if (this.bufline[n5 = tokenBegin % this.bufsize] != this.bufline[++tokenBegin % this.bufsize]) {
                    this.bufline[n5] = n++;
                }
                else {
                    this.bufline[n5] = n;
                }
            }
        }
        this.line = this.bufline[n5];
        this.column = this.bufcolumn[n5];
    }
    
    boolean getTrackLineColumn() {
        return this.trackLineColumn;
    }
    
    void setTrackLineColumn(final boolean trackLineColumn) {
        this.trackLineColumn = trackLineColumn;
    }
}
