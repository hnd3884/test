package com.steadystate.css.parser;

import java.io.IOException;
import java.io.Reader;

public final class CssCharStream implements CharStream
{
    public static final boolean staticFlag = false;
    private static final int BUFFER_SIZE = 2048;
    int bufsize;
    int available;
    int tokenBegin;
    public int bufpos;
    private int[] bufline;
    private int[] bufcolumn;
    private int column;
    private int line;
    private boolean prevCharIsCR;
    private boolean prevCharIsLF;
    private Reader inputStream;
    private char[] buffer;
    private int maxNextCharInd;
    private int inBuf;
    private int tabSize;
    private boolean trackLineColumn;
    
    private void ExpandBuff(final boolean wrapAround) {
        final char[] newbuffer = new char[this.bufsize + 2048];
        final int[] newbufline = new int[this.bufsize + 2048];
        final int[] newbufcolumn = new int[this.bufsize + 2048];
        try {
            if (wrapAround) {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
                this.buffer = newbuffer;
                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufline = newbufline;
                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufcolumn = newbufcolumn;
                final int n = this.bufpos + (this.bufsize - this.tokenBegin);
                this.bufpos = n;
                this.maxNextCharInd = n;
            }
            else {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                this.buffer = newbuffer;
                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                this.bufline = newbufline;
                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = newbufcolumn;
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
    
    private final void FillBuff() throws IOException {
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
            final int i;
            if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
                this.inputStream.close();
                throw new IOException();
            }
            this.maxNextCharInd += i;
        }
        catch (final IOException e) {
            --this.bufpos;
            this.backup(0);
            if (this.tokenBegin == -1) {
                this.tokenBegin = this.bufpos;
            }
            throw e;
        }
    }
    
    public final char BeginToken() throws IOException {
        this.tokenBegin = -1;
        final char c = this.readChar();
        this.tokenBegin = this.bufpos;
        return c;
    }
    
    private final void UpdateLineColumn(final char c) {
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
        }
        this.bufline[this.bufpos] = this.line;
        this.bufcolumn[this.bufpos] = this.column;
    }
    
    public final char readChar() throws IOException {
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
    public final int getColumn() {
        return this.bufcolumn[this.bufpos];
    }
    
    @Deprecated
    public final int getLine() {
        return this.bufline[this.bufpos];
    }
    
    public final int getEndColumn() {
        return this.bufcolumn[this.bufpos];
    }
    
    public final int getEndLine() {
        return this.bufline[this.bufpos];
    }
    
    public final int getBeginColumn() {
        return this.bufcolumn[this.tokenBegin];
    }
    
    public final int getBeginLine() {
        return this.bufline[this.tokenBegin];
    }
    
    public final void backup(final int amount) {
        this.inBuf += amount;
        final int bufpos = this.bufpos - amount;
        this.bufpos = bufpos;
        if (bufpos < 0) {
            this.bufpos += this.bufsize;
        }
    }
    
    public CssCharStream(final Reader dstream, final int startline, final int startcolumn, final int buffersize) {
        this.bufpos = -1;
        this.column = 0;
        this.line = 1;
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.inBuf = 0;
        this.tabSize = 1;
        this.trackLineColumn = true;
        this.inputStream = dstream;
        this.line = startline;
        this.column = startcolumn - 1;
        this.bufsize = buffersize;
        this.available = buffersize;
        this.buffer = new char[buffersize];
        this.bufline = new int[buffersize];
        this.bufcolumn = new int[buffersize];
    }
    
    public CssCharStream(final Reader dstream, final int startline, final int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }
    
    public final String GetImage() {
        if (this.bufpos >= this.tokenBegin) {
            return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
        }
        return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
    }
    
    public final char[] GetSuffix(final int len) {
        final char[] ret = new char[len];
        if (this.bufpos + 1 >= len) {
            System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
        }
        else {
            System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, len - this.bufpos - 1);
            System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
        }
        return ret;
    }
    
    public void Done() {
        this.buffer = null;
        this.bufline = null;
        this.bufcolumn = null;
    }
    
    public void adjustBeginLineColumn(int newLine, final int newCol) {
        int start = this.tokenBegin;
        int len;
        if (this.bufpos >= this.tokenBegin) {
            len = this.bufpos - this.tokenBegin + this.inBuf + 1;
        }
        else {
            len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
        }
        int i = 0;
        int j = 0;
        int k = 0;
        int nextColDiff = 0;
        int columnDiff = 0;
        while (i < len && this.bufline[j = start % this.bufsize] == this.bufline[k = ++start % this.bufsize]) {
            this.bufline[j] = newLine;
            nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
            this.bufcolumn[j] = newCol + columnDiff;
            columnDiff = nextColDiff;
            ++i;
        }
        if (i < len) {
            this.bufline[j] = newLine++;
            this.bufcolumn[j] = newCol + columnDiff;
            while (i++ < len) {
                if (this.bufline[j = start % this.bufsize] != this.bufline[++start % this.bufsize]) {
                    this.bufline[j] = newLine++;
                }
                else {
                    this.bufline[j] = newLine;
                }
            }
        }
        this.line = this.bufline[j];
        this.column = this.bufcolumn[j];
    }
    
    public void setTabSize(final int i) {
        this.tabSize = i;
    }
    
    public int getTabSize() {
        return this.tabSize;
    }
    
    public boolean getTrackLineColumn() {
        return this.trackLineColumn;
    }
    
    public void setTrackLineColumn(final boolean tlc) {
        this.trackLineColumn = tlc;
    }
}
