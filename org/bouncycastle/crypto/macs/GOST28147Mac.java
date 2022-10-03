package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;

public class GOST28147Mac implements Mac
{
    private int blockSize;
    private int macSize;
    private int bufOff;
    private byte[] buf;
    private byte[] mac;
    private boolean firstStep;
    private int[] workingKey;
    private byte[] macIV;
    private byte[] S;
    
    public GOST28147Mac() {
        this.blockSize = 8;
        this.macSize = 4;
        this.firstStep = true;
        this.workingKey = null;
        this.macIV = null;
        this.S = new byte[] { 9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, 15, 12, 0, 13, 5, 3, 7, 14, 9, 8, 10, 15, 0, 5, 2, 6, 12, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, 12, 15, 5, 10, 0, 7, 1, 9, 14, 7, 10, 12, 13, 1, 3, 9, 0, 2, 11, 4, 15, 8, 5, 6, 11, 5, 1, 9, 8, 13, 15, 0, 14, 4, 2, 3, 12, 7, 10, 6, 3, 10, 13, 12, 1, 2, 0, 11, 7, 5, 9, 4, 8, 15, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, 12, 4, 5, 15, 3, 11, 14, 11, 10, 15, 5, 0, 12, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4 };
        this.mac = new byte[this.blockSize];
        this.buf = new byte[this.blockSize];
        this.bufOff = 0;
    }
    
    private int[] generateWorkingKey(final byte[] array) {
        if (array.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        final int[] array2 = new int[8];
        for (int i = 0; i != 8; ++i) {
            array2[i] = this.bytesToint(array, i * 4);
        }
        return array2;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.reset();
        this.buf = new byte[this.blockSize];
        this.macIV = null;
        if (cipherParameters instanceof ParametersWithSBox) {
            final ParametersWithSBox parametersWithSBox = (ParametersWithSBox)cipherParameters;
            System.arraycopy(parametersWithSBox.getSBox(), 0, this.S, 0, parametersWithSBox.getSBox().length);
            if (parametersWithSBox.getParameters() != null) {
                this.workingKey = this.generateWorkingKey(((KeyParameter)parametersWithSBox.getParameters()).getKey());
            }
        }
        else if (cipherParameters instanceof KeyParameter) {
            this.workingKey = this.generateWorkingKey(((KeyParameter)cipherParameters).getKey());
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + cipherParameters.getClass().getName());
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            this.workingKey = this.generateWorkingKey(((KeyParameter)parametersWithIV.getParameters()).getKey());
            System.arraycopy(parametersWithIV.getIV(), 0, this.mac, 0, this.mac.length);
            this.macIV = parametersWithIV.getIV();
        }
    }
    
    public String getAlgorithmName() {
        return "GOST28147Mac";
    }
    
    public int getMacSize() {
        return this.macSize;
    }
    
    private int gost28147_mainStep(final int n, final int n2) {
        final int n3 = n2 + n;
        final int n4 = (this.S[0 + (n3 >> 0 & 0xF)] << 0) + (this.S[16 + (n3 >> 4 & 0xF)] << 4) + (this.S[32 + (n3 >> 8 & 0xF)] << 8) + (this.S[48 + (n3 >> 12 & 0xF)] << 12) + (this.S[64 + (n3 >> 16 & 0xF)] << 16) + (this.S[80 + (n3 >> 20 & 0xF)] << 20) + (this.S[96 + (n3 >> 24 & 0xF)] << 24) + (this.S[112 + (n3 >> 28 & 0xF)] << 28);
        return n4 << 11 | n4 >>> 21;
    }
    
    private void gost28147MacFunc(final int[] array, final byte[] array2, final int n, final byte[] array3, final int n2) {
        int bytesToint = this.bytesToint(array2, n);
        int bytesToint2 = this.bytesToint(array2, n + 4);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int n3 = bytesToint;
                bytesToint = (bytesToint2 ^ this.gost28147_mainStep(bytesToint, array[j]));
                bytesToint2 = n3;
            }
        }
        this.intTobytes(bytesToint, array3, n2);
        this.intTobytes(bytesToint2, array3, n2 + 4);
    }
    
    private int bytesToint(final byte[] array, final int n) {
        return (array[n + 3] << 24 & 0xFF000000) + (array[n + 2] << 16 & 0xFF0000) + (array[n + 1] << 8 & 0xFF00) + (array[n] & 0xFF);
    }
    
    private void intTobytes(final int n, final byte[] array, final int n2) {
        array[n2 + 3] = (byte)(n >>> 24);
        array[n2 + 2] = (byte)(n >>> 16);
        array[n2 + 1] = (byte)(n >>> 8);
        array[n2] = (byte)n;
    }
    
    private byte[] CM5func(final byte[] array, final int n, final byte[] array2) {
        final byte[] array3 = new byte[array.length - n];
        System.arraycopy(array, n, array3, 0, array2.length);
        for (int i = 0; i != array2.length; ++i) {
            array3[i] ^= array2[i];
        }
        return array3;
    }
    
    public void update(final byte b) throws IllegalStateException {
        if (this.bufOff == this.buf.length) {
            byte[] array = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, array, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    array = this.CM5func(this.buf, 0, this.macIV);
                }
            }
            else {
                array = this.CM5func(this.buf, 0, this.mac);
            }
            this.gost28147MacFunc(this.workingKey, array, 0, this.mac, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = b;
    }
    
    public void update(final byte[] array, int n, int i) throws DataLengthException, IllegalStateException {
        if (i < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        final int n2 = this.blockSize - this.bufOff;
        if (i > n2) {
            System.arraycopy(array, n, this.buf, this.bufOff, n2);
            byte[] array2 = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, array2, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
                if (this.macIV != null) {
                    array2 = this.CM5func(this.buf, 0, this.macIV);
                }
            }
            else {
                array2 = this.CM5func(this.buf, 0, this.mac);
            }
            this.gost28147MacFunc(this.workingKey, array2, 0, this.mac, 0);
            this.bufOff = 0;
            for (i -= n2, n += n2; i > this.blockSize; i -= this.blockSize, n += this.blockSize) {
                this.gost28147MacFunc(this.workingKey, this.CM5func(array, n, this.mac), 0, this.mac, 0);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        while (this.bufOff < this.blockSize) {
            this.buf[this.bufOff] = 0;
            ++this.bufOff;
        }
        byte[] cm5func = new byte[this.buf.length];
        System.arraycopy(this.buf, 0, cm5func, 0, this.mac.length);
        if (this.firstStep) {
            this.firstStep = false;
        }
        else {
            cm5func = this.CM5func(this.buf, 0, this.mac);
        }
        this.gost28147MacFunc(this.workingKey, cm5func, 0, this.mac, 0);
        System.arraycopy(this.mac, this.mac.length / 2 - this.macSize, array, n, this.macSize);
        this.reset();
        return this.macSize;
    }
    
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.firstStep = true;
    }
}
