package org.apache.commons.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

final class ExtendedBufferedReader extends BufferedReader
{
    private int lastChar;
    private long eolCounter;
    private long position;
    private boolean closed;
    
    ExtendedBufferedReader(final Reader reader) {
        super(reader);
        this.lastChar = -2;
    }
    
    @Override
    public int read() throws IOException {
        final int current = super.read();
        if (current == 13 || (current == 10 && this.lastChar != 13)) {
            ++this.eolCounter;
        }
        this.lastChar = current;
        ++this.position;
        return this.lastChar;
    }
    
    int getLastChar() {
        return this.lastChar;
    }
    
    @Override
    public int read(final char[] buf, final int offset, final int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        final int len = super.read(buf, offset, length);
        if (len > 0) {
            for (int i = offset; i < offset + len; ++i) {
                final char ch = buf[i];
                if (ch == '\n') {
                    if (13 != ((i > 0) ? buf[i - 1] : this.lastChar)) {
                        ++this.eolCounter;
                    }
                }
                else if (ch == '\r') {
                    ++this.eolCounter;
                }
            }
            this.lastChar = buf[offset + len - 1];
        }
        else if (len == -1) {
            this.lastChar = -1;
        }
        this.position += len;
        return len;
    }
    
    @Override
    public String readLine() throws IOException {
        final String line = super.readLine();
        if (line != null) {
            this.lastChar = 10;
            ++this.eolCounter;
        }
        else {
            this.lastChar = -1;
        }
        return line;
    }
    
    int lookAhead() throws IOException {
        super.mark(1);
        final int c = super.read();
        super.reset();
        return c;
    }
    
    long getCurrentLineNumber() {
        if (this.lastChar == 13 || this.lastChar == 10 || this.lastChar == -2 || this.lastChar == -1) {
            return this.eolCounter;
        }
        return this.eolCounter + 1L;
    }
    
    long getPosition() {
        return this.position;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        this.lastChar = -1;
        super.close();
    }
}
