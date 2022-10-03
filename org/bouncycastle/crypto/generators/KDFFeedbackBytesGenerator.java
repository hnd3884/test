package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.KDFFeedbackParameters;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Mac;
import java.math.BigInteger;
import org.bouncycastle.crypto.MacDerivationFunction;

public class KDFFeedbackBytesGenerator implements MacDerivationFunction
{
    private static final BigInteger INTEGER_MAX;
    private static final BigInteger TWO;
    private final Mac prf;
    private final int h;
    private byte[] fixedInputData;
    private int maxSizeExcl;
    private byte[] ios;
    private byte[] iv;
    private boolean useCounter;
    private int generatedBytes;
    private byte[] k;
    
    public KDFFeedbackBytesGenerator(final Mac prf) {
        this.prf = prf;
        this.h = prf.getMacSize();
        this.k = new byte[this.h];
    }
    
    public void init(final DerivationParameters derivationParameters) {
        if (!(derivationParameters instanceof KDFFeedbackParameters)) {
            throw new IllegalArgumentException("Wrong type of arguments given");
        }
        final KDFFeedbackParameters kdfFeedbackParameters = (KDFFeedbackParameters)derivationParameters;
        this.prf.init(new KeyParameter(kdfFeedbackParameters.getKI()));
        this.fixedInputData = kdfFeedbackParameters.getFixedInputData();
        final int r = kdfFeedbackParameters.getR();
        this.ios = new byte[r / 8];
        if (kdfFeedbackParameters.useCounter()) {
            final BigInteger multiply = KDFFeedbackBytesGenerator.TWO.pow(r).multiply(BigInteger.valueOf(this.h));
            this.maxSizeExcl = ((multiply.compareTo(KDFFeedbackBytesGenerator.INTEGER_MAX) == 1) ? Integer.MAX_VALUE : multiply.intValue());
        }
        else {
            this.maxSizeExcl = Integer.MAX_VALUE;
        }
        this.iv = kdfFeedbackParameters.getIV();
        this.useCounter = kdfFeedbackParameters.useCounter();
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
            this.prf.update(this.iv, 0, this.iv.length);
        }
        else {
            this.prf.update(this.k, 0, this.k.length);
        }
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
