package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class BitFile
{
    ImageOutputStream output;
    byte[] buffer;
    int index;
    int bitsLeft;
    boolean blocks;
    
    public BitFile(final ImageOutputStream output, final boolean blocks) {
        this.blocks = false;
        this.output = output;
        this.blocks = blocks;
        this.buffer = new byte[256];
        this.index = 0;
        this.bitsLeft = 8;
    }
    
    public void flush() throws IOException {
        final int n = this.index + ((this.bitsLeft != 8) ? 1 : 0);
        if (n > 0) {
            if (this.blocks) {
                this.output.write(n);
            }
            this.output.write(this.buffer, 0, n);
            this.buffer[0] = 0;
            this.index = 0;
            this.bitsLeft = 8;
        }
    }
    
    public void writeBits(int n, int i) throws IOException {
        int n2 = 0;
        final int n3 = 255;
        do {
            if ((this.index == 254 && this.bitsLeft == 0) || this.index > 254) {
                if (this.blocks) {
                    this.output.write(n3);
                }
                this.output.write(this.buffer, 0, n3);
                this.buffer[0] = 0;
                this.index = 0;
                this.bitsLeft = 8;
            }
            if (i <= this.bitsLeft) {
                if (this.blocks) {
                    final byte[] buffer = this.buffer;
                    final int index = this.index;
                    buffer[index] |= (byte)((n & (1 << i) - 1) << 8 - this.bitsLeft);
                    n2 += i;
                    this.bitsLeft -= i;
                    i = 0;
                }
                else {
                    final byte[] buffer2 = this.buffer;
                    final int index2 = this.index;
                    buffer2[index2] |= (byte)((n & (1 << i) - 1) << this.bitsLeft - i);
                    n2 += i;
                    this.bitsLeft -= i;
                    i = 0;
                }
            }
            else if (this.blocks) {
                final byte[] buffer3 = this.buffer;
                final int index3 = this.index;
                buffer3[index3] |= (byte)((n & (1 << this.bitsLeft) - 1) << 8 - this.bitsLeft);
                n2 += this.bitsLeft;
                n >>= this.bitsLeft;
                i -= this.bitsLeft;
                this.buffer[++this.index] = 0;
                this.bitsLeft = 8;
            }
            else {
                final int n4 = n >>> i - this.bitsLeft & (1 << this.bitsLeft) - 1;
                final byte[] buffer4 = this.buffer;
                final int index4 = this.index;
                buffer4[index4] |= (byte)n4;
                i -= this.bitsLeft;
                n2 += this.bitsLeft;
                this.buffer[++this.index] = 0;
                this.bitsLeft = 8;
            }
        } while (i != 0);
    }
}
