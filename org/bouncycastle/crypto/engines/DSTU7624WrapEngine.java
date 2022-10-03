package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.util.ArrayList;
import org.bouncycastle.crypto.Wrapper;

public class DSTU7624WrapEngine implements Wrapper
{
    private static final int BYTES_IN_INTEGER = 4;
    private boolean forWrapping;
    private DSTU7624Engine engine;
    private byte[] B;
    private byte[] intArray;
    private byte[] checkSumArray;
    private byte[] zeroArray;
    private ArrayList<byte[]> Btemp;
    
    public DSTU7624WrapEngine(final int n) {
        this.engine = new DSTU7624Engine(n);
        this.B = new byte[this.engine.getBlockSize() / 2];
        this.checkSumArray = new byte[this.engine.getBlockSize()];
        this.zeroArray = new byte[this.engine.getBlockSize()];
        this.Btemp = new ArrayList<byte[]>();
        this.intArray = new byte[4];
    }
    
    public void init(final boolean forWrapping, CipherParameters parameters) {
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        this.forWrapping = forWrapping;
        if (parameters instanceof KeyParameter) {
            this.engine.init(forWrapping, parameters);
            return;
        }
        throw new IllegalArgumentException("invalid parameters passed to DSTU7624WrapEngine");
    }
    
    public String getAlgorithmName() {
        return "DSTU7624WrapEngine";
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        if (n2 % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("wrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
        }
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        final int n3 = 2 * (1 + n2 / this.engine.getBlockSize());
        final int n4 = (n3 - 1) * 6;
        final byte[] array2 = new byte[n2 + this.engine.getBlockSize()];
        System.arraycopy(array, n, array2, 0, n2);
        System.arraycopy(array2, 0, this.B, 0, this.engine.getBlockSize() / 2);
        this.Btemp.clear();
        for (int i = array2.length - this.engine.getBlockSize() / 2, n5 = this.engine.getBlockSize() / 2; i != 0; i -= this.engine.getBlockSize() / 2, n5 += this.engine.getBlockSize() / 2) {
            final byte[] array3 = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(array2, n5, array3, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(array3);
        }
        for (int j = 0; j < n4; ++j) {
            System.arraycopy(this.B, 0, array2, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(this.Btemp.get(0), 0, array2, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.engine.processBlock(array2, 0, array2, 0);
            this.intToBytes(j + 1, this.intArray, 0);
            for (int k = 0; k < 4; ++k) {
                final byte[] array4 = array2;
                final int n6 = k + this.engine.getBlockSize() / 2;
                array4[n6] ^= this.intArray[k];
            }
            System.arraycopy(array2, this.engine.getBlockSize() / 2, this.B, 0, this.engine.getBlockSize() / 2);
            for (int l = 2; l < n3; ++l) {
                System.arraycopy(this.Btemp.get(l - 1), 0, this.Btemp.get(l - 2), 0, this.engine.getBlockSize() / 2);
            }
            System.arraycopy(array2, 0, this.Btemp.get(n3 - 2), 0, this.engine.getBlockSize() / 2);
        }
        System.arraycopy(this.B, 0, array2, 0, this.engine.getBlockSize() / 2);
        int n7 = this.engine.getBlockSize() / 2;
        for (int n8 = 0; n8 < n3 - 1; ++n8) {
            System.arraycopy(this.Btemp.get(n8), 0, array2, n7, this.engine.getBlockSize() / 2);
            n7 += this.engine.getBlockSize() / 2;
        }
        return array2;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        if (n2 % this.engine.getBlockSize() != 0) {
            throw new DataLengthException("unwrap data must be a multiple of " + this.engine.getBlockSize() + " bytes");
        }
        final int n3 = 2 * n2 / this.engine.getBlockSize();
        final int n4 = (n3 - 1) * 6;
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        final byte[] array3 = new byte[this.engine.getBlockSize() / 2];
        System.arraycopy(array2, 0, array3, 0, this.engine.getBlockSize() / 2);
        this.Btemp.clear();
        for (int i = array2.length - this.engine.getBlockSize() / 2, n5 = this.engine.getBlockSize() / 2; i != 0; i -= this.engine.getBlockSize() / 2, n5 += this.engine.getBlockSize() / 2) {
            final byte[] array4 = new byte[this.engine.getBlockSize() / 2];
            System.arraycopy(array2, n5, array4, 0, this.engine.getBlockSize() / 2);
            this.Btemp.add(array4);
        }
        for (int j = 0; j < n4; ++j) {
            System.arraycopy(this.Btemp.get(n3 - 2), 0, array2, 0, this.engine.getBlockSize() / 2);
            System.arraycopy(array3, 0, array2, this.engine.getBlockSize() / 2, this.engine.getBlockSize() / 2);
            this.intToBytes(n4 - j, this.intArray, 0);
            for (int k = 0; k < 4; ++k) {
                final byte[] array5 = array2;
                final int n6 = k + this.engine.getBlockSize() / 2;
                array5[n6] ^= this.intArray[k];
            }
            this.engine.processBlock(array2, 0, array2, 0);
            System.arraycopy(array2, 0, array3, 0, this.engine.getBlockSize() / 2);
            for (int l = 2; l < n3; ++l) {
                System.arraycopy(this.Btemp.get(n3 - l - 1), 0, this.Btemp.get(n3 - l), 0, this.engine.getBlockSize() / 2);
            }
            System.arraycopy(array2, this.engine.getBlockSize() / 2, this.Btemp.get(0), 0, this.engine.getBlockSize() / 2);
        }
        System.arraycopy(array3, 0, array2, 0, this.engine.getBlockSize() / 2);
        int n7 = this.engine.getBlockSize() / 2;
        for (int n8 = 0; n8 < n3 - 1; ++n8) {
            System.arraycopy(this.Btemp.get(n8), 0, array2, n7, this.engine.getBlockSize() / 2);
            n7 += this.engine.getBlockSize() / 2;
        }
        System.arraycopy(array2, array2.length - this.engine.getBlockSize(), this.checkSumArray, 0, this.engine.getBlockSize());
        final byte[] array6 = new byte[array2.length - this.engine.getBlockSize()];
        if (!Arrays.areEqual(this.checkSumArray, this.zeroArray)) {
            throw new InvalidCipherTextException("checksum failed");
        }
        System.arraycopy(array2, 0, array6, 0, array2.length - this.engine.getBlockSize());
        return array6;
    }
    
    private void intToBytes(final int n, final byte[] array, final int n2) {
        array[n2 + 3] = (byte)(n >> 24);
        array[n2 + 2] = (byte)(n >> 16);
        array[n2 + 1] = (byte)(n >> 8);
        array[n2] = (byte)n;
    }
}
