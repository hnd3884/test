package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public abstract class SerpentEngineBase implements BlockCipher
{
    protected static final int BLOCK_SIZE = 16;
    static final int ROUNDS = 32;
    static final int PHI = -1640531527;
    protected boolean encrypting;
    protected int[] wKey;
    protected int X0;
    protected int X1;
    protected int X2;
    protected int X3;
    
    SerpentEngineBase() {
    }
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.encrypting = encrypting;
            this.wKey = this.makeWorkingKey(((KeyParameter)cipherParameters).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to " + this.getAlgorithmName() + " init - " + cipherParameters.getClass().getName());
    }
    
    public String getAlgorithmName() {
        return "Serpent";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public final int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.wKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            this.encryptBlock(array, n, array2, n2);
        }
        else {
            this.decryptBlock(array, n, array2, n2);
        }
        return 16;
    }
    
    public void reset() {
    }
    
    protected static int rotateLeft(final int n, final int n2) {
        return n << n2 | n >>> -n2;
    }
    
    protected static int rotateRight(final int n, final int n2) {
        return n >>> n2 | n << -n2;
    }
    
    protected final void sb0(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n ^ n4;
        final int n6 = n3 ^ n5;
        final int n7 = n2 ^ n6;
        this.X3 = ((n & n4) ^ n7);
        final int n8 = n ^ (n2 & n5);
        this.X2 = (n7 ^ (n3 | n8));
        final int n9 = this.X3 & (n6 ^ n8);
        this.X1 = (~n6 ^ n9);
        this.X0 = (n9 ^ ~n8);
    }
    
    protected final void ib0(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n;
        final int n6 = n ^ n2;
        final int n7 = n4 ^ (n5 | n6);
        final int n8 = n3 ^ n7;
        this.X2 = (n6 ^ n8);
        final int n9 = n5 ^ (n4 & n6);
        this.X1 = (n7 ^ (this.X2 & n9));
        this.X3 = ((n & n7) ^ (n8 | this.X1));
        this.X0 = (this.X3 ^ (n8 ^ n9));
    }
    
    protected final void sb1(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 ^ ~n;
        final int n6 = n3 ^ (n | n5);
        this.X2 = (n4 ^ n6);
        final int n7 = n2 ^ (n4 | n5);
        final int n8 = n5 ^ this.X2;
        this.X3 = (n8 ^ (n6 & n7));
        final int n9 = n6 ^ n7;
        this.X1 = (this.X3 ^ n9);
        this.X0 = (n6 ^ (n8 & n9));
    }
    
    protected final void ib1(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 ^ n4;
        final int n6 = n ^ (n2 & n5);
        final int n7 = n5 ^ n6;
        this.X3 = (n3 ^ n7);
        final int n8 = n2 ^ (n5 & n6);
        this.X1 = (n6 ^ (this.X3 | n8));
        final int n9 = ~this.X1;
        final int n10 = this.X3 ^ n8;
        this.X0 = (n9 ^ n10);
        this.X2 = (n7 ^ (n9 | n10));
    }
    
    protected final void sb2(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n;
        final int n6 = n2 ^ n4;
        this.X0 = (n6 ^ (n3 & n5));
        final int n7 = n3 ^ n5;
        final int n8 = n2 & (n3 ^ this.X0);
        this.X3 = (n7 ^ n8);
        this.X2 = (n ^ ((n4 | n8) & (this.X0 | n7)));
        this.X1 = (n6 ^ this.X3 ^ (this.X2 ^ (n4 | n5)));
    }
    
    protected final void ib2(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 ^ n4;
        final int n6 = ~n5;
        final int n7 = n ^ n3;
        final int n8 = n3 ^ n5;
        this.X0 = (n7 ^ (n2 & n8));
        this.X3 = (n5 ^ (n7 | (n4 ^ (n | n6))));
        final int n9 = ~n8;
        final int n10 = this.X0 | this.X3;
        this.X1 = (n9 ^ n10);
        this.X2 = ((n4 & n9) ^ (n7 ^ n10));
    }
    
    protected final void sb3(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n ^ n2;
        final int n6 = n & n3;
        final int n7 = n | n4;
        final int n8 = n3 ^ n4;
        final int n9 = n6 | (n5 & n7);
        this.X2 = (n8 ^ n9);
        final int n10 = n9 ^ (n2 ^ n7);
        this.X0 = (n5 ^ (n8 & n10));
        final int n11 = this.X2 & this.X0;
        this.X1 = (n10 ^ n11);
        this.X3 = ((n2 | n4) ^ (n8 ^ n11));
    }
    
    protected final void ib3(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n | n2;
        final int n6 = n2 ^ n3;
        final int n7 = n ^ (n2 & n6);
        final int n8 = n3 ^ n7;
        final int n9 = n4 | n7;
        this.X0 = (n6 ^ n9);
        final int n10 = n4 ^ (n6 | n9);
        this.X2 = (n8 ^ n10);
        final int n11 = n5 ^ n10;
        this.X3 = (n7 ^ (this.X0 & n11));
        this.X1 = (this.X3 ^ (this.X0 ^ n11));
    }
    
    protected final void sb4(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n ^ n4;
        final int n6 = n3 ^ (n4 & n5);
        final int n7 = n2 | n6;
        this.X3 = (n5 ^ n7);
        final int n8 = ~n2;
        this.X0 = (n6 ^ (n5 | n8));
        final int n9 = n & this.X0;
        final int n10 = n5 ^ n8;
        this.X2 = (n9 ^ (n7 & n10));
        this.X1 = (n ^ n6 ^ (n10 & this.X2));
    }
    
    protected final void ib4(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 ^ (n & (n3 | n4));
        final int n6 = n3 ^ (n & n5);
        this.X1 = (n4 ^ n6);
        final int n7 = ~n;
        this.X3 = (n5 ^ (n6 & this.X1));
        final int n8 = n4 ^ (this.X1 | n7);
        this.X0 = (this.X3 ^ n8);
        this.X2 = ((n5 & n8) ^ (this.X1 ^ n7));
    }
    
    protected final void sb5(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n;
        final int n6 = n ^ n2;
        final int n7 = n ^ n4;
        this.X0 = (n3 ^ n5 ^ (n6 | n7));
        final int n8 = n4 & this.X0;
        this.X1 = (n8 ^ (n6 ^ this.X0));
        final int n9 = n5 | this.X0;
        final int n10 = n6 | n8;
        final int n11 = n7 ^ n9;
        this.X2 = (n10 ^ n11);
        this.X3 = (n2 ^ n8 ^ (this.X1 & n11));
    }
    
    protected final void ib5(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n3;
        final int n6 = n4 ^ (n2 & n5);
        final int n7 = n & n6;
        this.X3 = (n7 ^ (n2 ^ n5));
        final int n8 = n2 | this.X3;
        this.X1 = (n6 ^ (n & n8));
        final int n9 = n | n4;
        this.X0 = (n9 ^ (n5 ^ n8));
        this.X2 = ((n2 & n9) ^ (n7 | (n ^ n3)));
    }
    
    protected final void sb6(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n;
        final int n6 = n ^ n4;
        final int n7 = n2 ^ n6;
        final int n8 = n3 ^ (n5 | n6);
        this.X1 = (n2 ^ n8);
        final int n9 = n4 ^ (n6 | this.X1);
        this.X2 = (n7 ^ (n8 & n9));
        final int n10 = n8 ^ n9;
        this.X0 = (this.X2 ^ n10);
        this.X3 = (~n8 ^ (n7 & n10));
    }
    
    protected final void ib6(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ~n;
        final int n6 = n ^ n2;
        final int n7 = n3 ^ n6;
        final int n8 = n4 ^ (n3 | n5);
        this.X1 = (n7 ^ n8);
        final int n9 = n6 ^ (n7 & n8);
        this.X3 = (n8 ^ (n2 | n9));
        final int n10 = n2 | this.X3;
        this.X0 = (n9 ^ n10);
        this.X2 = ((n4 & n5) ^ (n7 ^ n10));
    }
    
    protected final void sb7(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n2 ^ n3;
        final int n6 = n4 ^ (n3 & n5);
        final int n7 = n ^ n6;
        this.X1 = (n2 ^ (n7 & (n4 | n5)));
        final int n8 = n6 | this.X1;
        this.X3 = (n5 ^ (n & n7));
        final int n9 = n7 ^ n8;
        this.X2 = (n6 ^ (this.X3 & n9));
        this.X0 = (~n9 ^ (this.X3 & this.X2));
    }
    
    protected final void ib7(final int n, final int n2, final int n3, final int n4) {
        final int n5 = n3 | (n & n2);
        final int n6 = n4 & (n | n2);
        this.X3 = (n5 ^ n6);
        final int n7 = ~n4;
        final int n8 = n2 ^ n6;
        this.X1 = (n ^ (n8 | (this.X3 ^ n7)));
        this.X0 = (n3 ^ n8 ^ (n4 | this.X1));
        this.X2 = (n5 ^ this.X1 ^ (this.X0 ^ (n & this.X3)));
    }
    
    protected final void LT() {
        final int rotateLeft = rotateLeft(this.X0, 13);
        final int rotateLeft2 = rotateLeft(this.X2, 3);
        final int n = this.X1 ^ rotateLeft ^ rotateLeft2;
        final int n2 = this.X3 ^ rotateLeft2 ^ rotateLeft << 3;
        this.X1 = rotateLeft(n, 1);
        this.X3 = rotateLeft(n2, 7);
        this.X0 = rotateLeft(rotateLeft ^ this.X1 ^ this.X3, 5);
        this.X2 = rotateLeft(rotateLeft2 ^ this.X3 ^ this.X1 << 7, 22);
    }
    
    protected final void inverseLT() {
        final int n = rotateRight(this.X2, 22) ^ this.X3 ^ this.X1 << 7;
        final int n2 = rotateRight(this.X0, 5) ^ this.X1 ^ this.X3;
        final int rotateRight = rotateRight(this.X3, 7);
        final int rotateRight2 = rotateRight(this.X1, 1);
        this.X3 = (rotateRight ^ n ^ n2 << 3);
        this.X1 = (rotateRight2 ^ n2 ^ n);
        this.X2 = rotateRight(n, 3);
        this.X0 = rotateRight(n2, 13);
    }
    
    protected abstract int[] makeWorkingKey(final byte[] p0);
    
    protected abstract void encryptBlock(final byte[] p0, final int p1, final byte[] p2, final int p3);
    
    protected abstract void decryptBlock(final byte[] p0, final int p1, final byte[] p2, final int p3);
}
