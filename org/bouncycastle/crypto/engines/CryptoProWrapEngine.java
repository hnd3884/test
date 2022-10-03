package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;

public class CryptoProWrapEngine extends GOST28147WrapEngine
{
    @Override
    public void init(final boolean b, CipherParameters parameters) {
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        final ParametersWithUKM parametersWithUKM = (ParametersWithUKM)parameters;
        byte[] sBox = null;
        KeyParameter keyParameter;
        if (parametersWithUKM.getParameters() instanceof ParametersWithSBox) {
            keyParameter = (KeyParameter)((ParametersWithSBox)parametersWithUKM.getParameters()).getParameters();
            sBox = ((ParametersWithSBox)parametersWithUKM.getParameters()).getSBox();
        }
        else {
            keyParameter = (KeyParameter)parametersWithUKM.getParameters();
        }
        final KeyParameter keyParameter2 = new KeyParameter(cryptoProDiversify(keyParameter.getKey(), parametersWithUKM.getUKM(), sBox));
        if (sBox != null) {
            super.init(b, new ParametersWithUKM(new ParametersWithSBox(keyParameter2, sBox), parametersWithUKM.getUKM()));
        }
        else {
            super.init(b, new ParametersWithUKM(keyParameter2, parametersWithUKM.getUKM()));
        }
    }
    
    private static byte[] cryptoProDiversify(final byte[] array, final byte[] array2, final byte[] array3) {
        for (int i = 0; i != 8; ++i) {
            int n = 0;
            int n2 = 0;
            for (int j = 0; j != 8; ++j) {
                final int littleEndianToInt = Pack.littleEndianToInt(array, j * 4);
                if (bitSet(array2[i], j)) {
                    n += littleEndianToInt;
                }
                else {
                    n2 += littleEndianToInt;
                }
            }
            final byte[] array4 = new byte[8];
            Pack.intToLittleEndian(n, array4, 0);
            Pack.intToLittleEndian(n2, array4, 4);
            final GCFBBlockCipher gcfbBlockCipher = new GCFBBlockCipher(new GOST28147Engine());
            gcfbBlockCipher.init(true, new ParametersWithIV(new ParametersWithSBox(new KeyParameter(array), array3), array4));
            gcfbBlockCipher.processBlock(array, 0, array, 0);
            gcfbBlockCipher.processBlock(array, 8, array, 8);
            gcfbBlockCipher.processBlock(array, 16, array, 16);
            gcfbBlockCipher.processBlock(array, 24, array, 24);
        }
        return array;
    }
    
    private static boolean bitSet(final byte b, final int n) {
        return (b & 1 << n) != 0x0;
    }
}
