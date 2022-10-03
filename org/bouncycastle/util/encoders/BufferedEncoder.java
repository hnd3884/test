package org.bouncycastle.util.encoders;

public class BufferedEncoder
{
    protected byte[] buf;
    protected int bufOff;
    protected Translator translator;
    
    public BufferedEncoder(final Translator translator, final int n) {
        this.translator = translator;
        if (n % translator.getEncodedBlockSize() != 0) {
            throw new IllegalArgumentException("buffer size not multiple of input block size");
        }
        this.buf = new byte[n];
        this.bufOff = 0;
    }
    
    public int processByte(final byte b, final byte[] array, final int n) {
        int encode = 0;
        this.buf[this.bufOff++] = b;
        if (this.bufOff == this.buf.length) {
            encode = this.translator.encode(this.buf, 0, this.buf.length, array, n);
            this.bufOff = 0;
        }
        return encode;
    }
    
    public int processBytes(final byte[] array, int n, int n2, final byte[] array2, int n3) {
        if (n2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int n4 = 0;
        final int n5 = this.buf.length - this.bufOff;
        if (n2 > n5) {
            System.arraycopy(array, n, this.buf, this.bufOff, n5);
            final int n6 = n4 + this.translator.encode(this.buf, 0, this.buf.length, array2, n3);
            this.bufOff = 0;
            n2 -= n5;
            n += n5;
            n3 += n6;
            final int n7 = n2 - n2 % this.buf.length;
            n4 = n6 + this.translator.encode(array, n, n7, array2, n3);
            n2 -= n7;
            n += n7;
        }
        if (n2 != 0) {
            System.arraycopy(array, n, this.buf, this.bufOff, n2);
            this.bufOff += n2;
        }
        return n4;
    }
}
