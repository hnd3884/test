package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;

public class SipHash implements Mac
{
    protected final int c;
    protected final int d;
    protected long k0;
    protected long k1;
    protected long v0;
    protected long v1;
    protected long v2;
    protected long v3;
    protected long m;
    protected int wordPos;
    protected int wordCount;
    
    public SipHash() {
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
        this.c = 2;
        this.d = 4;
    }
    
    public SipHash(final int c, final int d) {
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
        this.c = c;
        this.d = d;
    }
    
    public String getAlgorithmName() {
        return "SipHash-" + this.c + "-" + this.d;
    }
    
    public int getMacSize() {
        return 8;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("'params' must be an instance of KeyParameter");
        }
        final byte[] key = ((KeyParameter)cipherParameters).getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("'params' must be a 128-bit key");
        }
        this.k0 = Pack.littleEndianToLong(key, 0);
        this.k1 = Pack.littleEndianToLong(key, 8);
        this.reset();
    }
    
    public void update(final byte b) throws IllegalStateException {
        this.m >>>= 8;
        this.m |= ((long)b & 0xFFL) << 56;
        if (++this.wordPos == 8) {
            this.processMessageWord();
            this.wordPos = 0;
        }
    }
    
    public void update(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalStateException {
        int i = 0;
        final int n3 = n2 & 0xFFFFFFF8;
        if (this.wordPos == 0) {
            while (i < n3) {
                this.m = Pack.littleEndianToLong(array, n + i);
                this.processMessageWord();
                i += 8;
            }
            while (i < n2) {
                this.m >>>= 8;
                this.m |= ((long)array[n + i] & 0xFFL) << 56;
                ++i;
            }
            this.wordPos = n2 - n3;
        }
        else {
            final int n4 = this.wordPos << 3;
            while (i < n3) {
                final long littleEndianToLong = Pack.littleEndianToLong(array, n + i);
                this.m = (littleEndianToLong << n4 | this.m >>> -n4);
                this.processMessageWord();
                this.m = littleEndianToLong;
                i += 8;
            }
            while (i < n2) {
                this.m >>>= 8;
                this.m |= ((long)array[n + i] & 0xFFL) << 56;
                if (++this.wordPos == 8) {
                    this.processMessageWord();
                    this.wordPos = 0;
                }
                ++i;
            }
        }
    }
    
    public long doFinal() throws DataLengthException, IllegalStateException {
        this.m >>>= 7 - this.wordPos << 3;
        this.m >>>= 8;
        this.m |= ((long)((this.wordCount << 3) + this.wordPos) & 0xFFL) << 56;
        this.processMessageWord();
        this.v2 ^= 0xFFL;
        this.applySipRounds(this.d);
        final long n = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
        this.reset();
        return n;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        Pack.longToLittleEndian(this.doFinal(), array, n);
        return 8;
    }
    
    public void reset() {
        this.v0 = (this.k0 ^ 0x736F6D6570736575L);
        this.v1 = (this.k1 ^ 0x646F72616E646F6DL);
        this.v2 = (this.k0 ^ 0x6C7967656E657261L);
        this.v3 = (this.k1 ^ 0x7465646279746573L);
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
    }
    
    protected void processMessageWord() {
        ++this.wordCount;
        this.v3 ^= this.m;
        this.applySipRounds(this.c);
        this.v0 ^= this.m;
    }
    
    protected void applySipRounds(final int n) {
        long v0 = this.v0;
        long v2 = this.v1;
        long v3 = this.v2;
        long v4 = this.v3;
        for (int i = 0; i < n; ++i) {
            final long n2 = v0 + v2;
            final long n3 = v3 + v4;
            final long rotateLeft = rotateLeft(v2, 13);
            final long rotateLeft2 = rotateLeft(v4, 16);
            final long n4 = rotateLeft ^ n2;
            final long n5 = rotateLeft2 ^ n3;
            final long rotateLeft3 = rotateLeft(n2, 32);
            final long n6 = n3 + n4;
            v0 = rotateLeft3 + n5;
            final long rotateLeft4 = rotateLeft(n4, 17);
            final long rotateLeft5 = rotateLeft(n5, 21);
            v2 = (rotateLeft4 ^ n6);
            v4 = (rotateLeft5 ^ v0);
            v3 = rotateLeft(n6, 32);
        }
        this.v0 = v0;
        this.v1 = v2;
        this.v2 = v3;
        this.v3 = v4;
    }
    
    protected static long rotateLeft(final long n, final int n2) {
        return n << n2 | n >>> -n2;
    }
}
