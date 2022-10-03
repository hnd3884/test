package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.util.ArrayUtil;
import java.io.Reader;

public final class RollingCharBuffer
{
    private Reader reader;
    private char[] buffer;
    private int nextWrite;
    private int nextPos;
    private int count;
    private boolean end;
    
    public RollingCharBuffer() {
        this.buffer = new char[512];
    }
    
    public void reset(final Reader reader) {
        this.reader = reader;
        this.nextPos = 0;
        this.nextWrite = 0;
        this.count = 0;
        this.end = false;
    }
    
    public int get(final int pos) throws IOException {
        if (pos == this.nextPos) {
            if (this.end) {
                return -1;
            }
            if (this.count == this.buffer.length) {
                final char[] newBuffer = new char[ArrayUtil.oversize(1 + this.count, 2)];
                System.arraycopy(this.buffer, this.nextWrite, newBuffer, 0, this.buffer.length - this.nextWrite);
                System.arraycopy(this.buffer, 0, newBuffer, this.buffer.length - this.nextWrite, this.nextWrite);
                this.nextWrite = this.buffer.length;
                this.buffer = newBuffer;
            }
            if (this.nextWrite == this.buffer.length) {
                this.nextWrite = 0;
            }
            final int toRead = this.buffer.length - Math.max(this.count, this.nextWrite);
            final int readCount = this.reader.read(this.buffer, this.nextWrite, toRead);
            if (readCount == -1) {
                this.end = true;
                return -1;
            }
            final int ch = this.buffer[this.nextWrite];
            this.nextWrite += readCount;
            this.count += readCount;
            this.nextPos += readCount;
            return ch;
        }
        else {
            assert pos < this.nextPos;
            assert this.nextPos - pos <= this.count : "nextPos=" + this.nextPos + " pos=" + pos + " count=" + this.count;
            return this.buffer[this.getIndex(pos)];
        }
    }
    
    private boolean inBounds(final int pos) {
        return pos >= 0 && pos < this.nextPos && pos >= this.nextPos - this.count;
    }
    
    private int getIndex(final int pos) {
        int index = this.nextWrite - (this.nextPos - pos);
        if (index < 0) {
            index += this.buffer.length;
            assert index >= 0;
        }
        return index;
    }
    
    public char[] get(final int posStart, final int length) {
        assert length > 0;
        assert this.inBounds(posStart) : "posStart=" + posStart + " length=" + length;
        final int startIndex = this.getIndex(posStart);
        final int endIndex = this.getIndex(posStart + length);
        final char[] result = new char[length];
        if (endIndex >= startIndex && length < this.buffer.length) {
            System.arraycopy(this.buffer, startIndex, result, 0, endIndex - startIndex);
        }
        else {
            final int part1 = this.buffer.length - startIndex;
            System.arraycopy(this.buffer, startIndex, result, 0, part1);
            System.arraycopy(this.buffer, 0, result, this.buffer.length - startIndex, length - part1);
        }
        return result;
    }
    
    public void freeBefore(final int pos) {
        assert pos >= 0;
        assert pos <= this.nextPos;
        final int newCount = this.nextPos - pos;
        assert newCount <= this.count : "newCount=" + newCount + " count=" + this.count;
        assert newCount <= this.buffer.length : "newCount=" + newCount + " buf.length=" + this.buffer.length;
        this.count = newCount;
    }
}
