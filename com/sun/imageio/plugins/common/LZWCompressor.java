package com.sun.imageio.plugins.common;

import java.io.PrintStream;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class LZWCompressor
{
    int codeSize;
    int clearCode;
    int endOfInfo;
    int numBits;
    int limit;
    short prefix;
    BitFile bf;
    LZWStringTable lzss;
    boolean tiffFudge;
    
    public LZWCompressor(final ImageOutputStream imageOutputStream, final int codeSize, final boolean tiffFudge) throws IOException {
        this.bf = new BitFile(imageOutputStream, !tiffFudge);
        this.codeSize = codeSize;
        this.tiffFudge = tiffFudge;
        this.clearCode = 1 << codeSize;
        this.endOfInfo = this.clearCode + 1;
        this.numBits = codeSize + 1;
        this.limit = (1 << this.numBits) - 1;
        if (this.tiffFudge) {
            --this.limit;
        }
        this.prefix = -1;
        (this.lzss = new LZWStringTable()).clearTable(codeSize);
        this.bf.writeBits(this.clearCode, this.numBits);
    }
    
    public void compress(final byte[] array, final int n, final int n2) throws IOException {
        for (int n3 = n + n2, i = n; i < n3; ++i) {
            final byte b = array[i];
            final short charString;
            if ((charString = this.lzss.findCharString(this.prefix, b)) != -1) {
                this.prefix = charString;
            }
            else {
                this.bf.writeBits(this.prefix, this.numBits);
                if (this.lzss.addCharString(this.prefix, b) > this.limit) {
                    if (this.numBits == 12) {
                        this.bf.writeBits(this.clearCode, this.numBits);
                        this.lzss.clearTable(this.codeSize);
                        this.numBits = this.codeSize + 1;
                    }
                    else {
                        ++this.numBits;
                    }
                    this.limit = (1 << this.numBits) - 1;
                    if (this.tiffFudge) {
                        --this.limit;
                    }
                }
                this.prefix = (short)(b & 0xFF);
            }
        }
    }
    
    public void flush() throws IOException {
        if (this.prefix != -1) {
            this.bf.writeBits(this.prefix, this.numBits);
        }
        this.bf.writeBits(this.endOfInfo, this.numBits);
        this.bf.flush();
    }
    
    public void dump(final PrintStream printStream) {
        this.lzss.dump(printStream);
    }
}
