package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.KDFDoublePipelineIterationParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Mac;
import java.math.BigInteger;
import org.bouncycastle.crypto.MacDerivationFunction;

public class KDFDoublePipelineIterationBytesGenerator implements MacDerivationFunction
{
    private static final BigInteger INTEGER_MAX;
    private static final BigInteger TWO;
    private final Mac prf;
    private final int h;
    private byte[] fixedInputData;
    private int maxSizeExcl;
    private byte[] ios;
    private boolean useCounter;
    private int generatedBytes;
    private byte[] a;
    private byte[] k;
    
    public KDFDoublePipelineIterationBytesGenerator(final Mac prf) {
        this.prf = prf;
        this.h = prf.getMacSize();
        this.a = new byte[this.h];
        this.k = new byte[this.h];
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof KDFDoublePipelineIterationParameters)) {
            throw new IllegalArgumentException("Wrong type of arguments given");
        }
        final KDFDoublePipelineIterationParameters kdfDoublePipelineIterationParameters = (KDFDoublePipelineIterationParameters)derivationParameters;
        this.prf.init(new KeyParameter(kdfDoublePipelineIterationParameters.getKI()));
        this.fixedInputData = kdfDoublePipelineIterationParameters.getFixedInputData();
        final int r = kdfDoublePipelineIterationParameters.getR();
        this.ios = new byte[r / 8];
        if (kdfDoublePipelineIterationParameters.useCounter()) {
            final BigInteger multiply = KDFDoublePipelineIterationBytesGenerator.TWO.pow(r).multiply(BigInteger.valueOf(this.h));
            this.maxSizeExcl = ((multiply.compareTo(KDFDoublePipelineIterationBytesGenerator.INTEGER_MAX) == 1) ? Integer.MAX_VALUE : multiply.intValue());
        }
        else {
            this.maxSizeExcl = Integer.MAX_VALUE;
        }
        this.useCounter = kdfDoublePipelineIterationParameters.useCounter();
        this.generatedBytes = 0;
    }
    
    public Mac getMac() {
        return this.prf;
    }
    
    public int generateBytes(final byte[] array, int n, final int n2) throws DataLengthException, IllegalArgumentException {
        final int n3 = this.generatedBytes + n2;
        if (n3 < 0 || n3 >= this.maxSizeExcl) {
            throw new DataLengthException("Current KDFCTR may only be used for " + this.maxSizeExcl + " bytes");
        }
        if (this.generatedBytes % this.h == 0) {
            this.generateNext();
        }
        final int n4 = this.generatedBytes % this.h;
        final int min = Math.min(this.h - this.generatedBytes % this.h, n2);
        System.arraycopy(this.k, n4, array, n, min);
        this.generatedBytes += min;
        int i;
        int min2;
        for (i = n2 - min, n += min; i > 0; i -= min2, n += min2) {
            this.generateNext();
            min2 = Math.min(this.h, i);
            System.arraycopy(this.k, 0, array, n, min2);
            this.generatedBytes += min2;
        }
        return n2;
    }
    
    private void generateNext() {
        if (this.generatedBytes == 0) {
            this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
            this.prf.doFinal(this.a, 0);
        }
        else {
            this.prf.update(this.a, 0, this.a.length);
            this.prf.doFinal(this.a, 0);
        }
        this.prf.update(this.a, 0, this.a.length);
        if (this.useCounter) {
            final int n = this.generatedBytes / this.h + 1;
            switch (this.ios.length) {
                case 4: {
                    this.ios[0] = (byte)(n >>> 24);
                }
                case 3: {
                    this.ios[this.ios.length - 3] = (byte)(n >>> 16);
                }
                case 2: {
                    this.ios[this.ios.length - 2] = (byte)(n >>> 8);
                }
                case 1: {
                    this.ios[this.ios.length - 1] = (byte)n;
                    this.prf.update(this.ios, 0, this.ios.length);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unsupported size of counter i");
                }
            }
        }
        this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
        this.prf.doFinal(this.k, 0);
    }
    
    static {
        INTEGER_MAX = BigInteger.valueOf(2147483647L);
        TWO = BigInteger.valueOf(2L);
    }
}
