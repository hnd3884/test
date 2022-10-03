package com.lowagie.text.pdf.crypto;

public class ARCFOUREncryption
{
    private byte[] state;
    private int x;
    private int y;
    
    public ARCFOUREncryption() {
        this.state = new byte[256];
    }
    
    public void prepareARCFOURKey(final byte[] key) {
        this.prepareARCFOURKey(key, 0, key.length);
    }
    
    public void prepareARCFOURKey(final byte[] key, final int off, final int len) {
        int index1 = 0;
        int index2 = 0;
        for (int k = 0; k < 256; ++k) {
            this.state[k] = (byte)k;
        }
        this.x = 0;
        this.y = 0;
        for (int i = 0; i < 256; ++i) {
            index2 = (key[index1 + off] + this.state[i] + index2 & 0xFF);
            final byte tmp = this.state[i];
            this.state[i] = this.state[index2];
            this.state[index2] = tmp;
            index1 = (index1 + 1) % len;
        }
    }
    
    public void encryptARCFOUR(final byte[] dataIn, final int off, final int len, final byte[] dataOut, final int offOut) {
        for (int length = len + off, k = off; k < length; ++k) {
            this.x = (this.x + 1 & 0xFF);
            this.y = (this.state[this.x] + this.y & 0xFF);
            final byte tmp = this.state[this.x];
            this.state[this.x] = this.state[this.y];
            this.state[this.y] = tmp;
            dataOut[k - off + offOut] = (byte)(dataIn[k] ^ this.state[this.state[this.x] + this.state[this.y] & 0xFF]);
        }
    }
    
    public void encryptARCFOUR(final byte[] data, final int off, final int len) {
        this.encryptARCFOUR(data, off, len, data, off);
    }
    
    public void encryptARCFOUR(final byte[] dataIn, final byte[] dataOut) {
        this.encryptARCFOUR(dataIn, 0, dataIn.length, dataOut, 0);
    }
    
    public void encryptARCFOUR(final byte[] data) {
        this.encryptARCFOUR(data, 0, data.length, data, 0);
    }
}
