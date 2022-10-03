package org.apache.lucene.queryparser.surround.parser;

import java.io.IOException;
import java.io.Reader;

public final class FastCharStream implements CharStream
{
    char[] buffer;
    int bufferLength;
    int bufferPosition;
    int tokenStart;
    int bufferStart;
    Reader input;
    
    public FastCharStream(final Reader r) {
        this.buffer = null;
        this.bufferLength = 0;
        this.bufferPosition = 0;
        this.tokenStart = 0;
        this.bufferStart = 0;
        this.input = r;
    }
    
    @Override
    public final char readChar() throws IOException {
        if (this.bufferPosition >= this.bufferLength) {
            this.refill();
        }
        return this.buffer[this.bufferPosition++];
    }
    
    private final void refill() throws IOException {
        final int newPosition = this.bufferLength - this.tokenStart;
        if (this.tokenStart == 0) {
            if (this.buffer == null) {
                this.buffer = new char[2048];
            }
            else if (this.bufferLength == this.buffer.length) {
                final char[] newBuffer = new char[this.buffer.length * 2];
                System.arraycopy(this.buffer, 0, newBuffer, 0, this.bufferLength);
                this.buffer = newBuffer;
            }
        }
        else {
            System.arraycopy(this.buffer, this.tokenStart, this.buffer, 0, newPosition);
        }
        this.bufferLength = newPosition;
        this.bufferPosition = newPosition;
        this.bufferStart += this.tokenStart;
        this.tokenStart = 0;
        final int charsRead = this.input.read(this.buffer, newPosition, this.buffer.length - newPosition);
        if (charsRead == -1) {
            throw new IOException("read past eof");
        }
        this.bufferLength += charsRead;
    }
    
    @Override
    public final char BeginToken() throws IOException {
        this.tokenStart = this.bufferPosition;
        return this.readChar();
    }
    
    @Override
    public final void backup(final int amount) {
        this.bufferPosition -= amount;
    }
    
    @Override
    public final String GetImage() {
        return new String(this.buffer, this.tokenStart, this.bufferPosition - this.tokenStart);
    }
    
    @Override
    public final char[] GetSuffix(final int len) {
        final char[] value = new char[len];
        System.arraycopy(this.buffer, this.bufferPosition - len, value, 0, len);
        return value;
    }
    
    @Override
    public final void Done() {
        try {
            this.input.close();
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public final int getColumn() {
        return this.bufferStart + this.bufferPosition;
    }
    
    @Override
    public final int getLine() {
        return 1;
    }
    
    @Override
    public final int getEndColumn() {
        return this.bufferStart + this.bufferPosition;
    }
    
    @Override
    public final int getEndLine() {
        return 1;
    }
    
    @Override
    public final int getBeginColumn() {
        return this.bufferStart + this.tokenStart;
    }
    
    @Override
    public final int getBeginLine() {
        return 1;
    }
}
