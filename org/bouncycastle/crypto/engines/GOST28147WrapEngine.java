package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.Wrapper;

public class GOST28147WrapEngine implements Wrapper
{
    private GOST28147Engine cipher;
    private GOST28147Mac mac;
    
    public GOST28147WrapEngine() {
        this.cipher = new GOST28147Engine();
        this.mac = new GOST28147Mac();
    }
    
    public void init(final boolean b, CipherParameters parameters) {
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        final ParametersWithUKM parametersWithUKM = (ParametersWithUKM)parameters;
        this.cipher.init(b, parametersWithUKM.getParameters());
        KeyParameter keyParameter;
        if (parametersWithUKM.getParameters() instanceof ParametersWithSBox) {
            keyParameter = (KeyParameter)((ParametersWithSBox)parametersWithUKM.getParameters()).getParameters();
        }
        else {
            keyParameter = (KeyParameter)parametersWithUKM.getParameters();
        }
        this.mac.init(new ParametersWithIV(keyParameter, parametersWithUKM.getUKM()));
    }
    
    public String getAlgorithmName() {
        return "GOST28147Wrap";
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        this.mac.update(array, n, n2);
        final byte[] array2 = new byte[n2 + this.mac.getMacSize()];
        this.cipher.processBlock(array, n, array2, 0);
        this.cipher.processBlock(array, n + 8, array2, 8);
        this.cipher.processBlock(array, n + 16, array2, 16);
        this.cipher.processBlock(array, n + 24, array2, 24);
        this.mac.doFinal(array2, n2);
        return array2;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        final byte[] array2 = new byte[n2 - this.mac.getMacSize()];
        this.cipher.processBlock(array, n, array2, 0);
        this.cipher.processBlock(array, n + 8, array2, 8);
        this.cipher.processBlock(array, n + 16, array2, 16);
        this.cipher.processBlock(array, n + 24, array2, 24);
        final byte[] array3 = new byte[this.mac.getMacSize()];
        this.mac.update(array2, 0, array2.length);
        this.mac.doFinal(array3, 0);
        final byte[] array4 = new byte[this.mac.getMacSize()];
        System.arraycopy(array, n + n2 - 4, array4, 0, this.mac.getMacSize());
        if (!Arrays.constantTimeAreEqual(array3, array4)) {
            throw new IllegalStateException("mac mismatch");
        }
        return array2;
    }
}
