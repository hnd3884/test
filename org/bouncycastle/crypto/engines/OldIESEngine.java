package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.BasicAgreement;

public class OldIESEngine extends IESEngine
{
    public OldIESEngine(final BasicAgreement basicAgreement, final DerivationFunction derivationFunction, final Mac mac) {
        super(basicAgreement, derivationFunction, mac);
    }
    
    public OldIESEngine(final BasicAgreement basicAgreement, final DerivationFunction derivationFunction, final Mac mac, final BufferedBlockCipher bufferedBlockCipher) {
        super(basicAgreement, derivationFunction, mac, bufferedBlockCipher);
    }
    
    @Override
    protected byte[] getLengthTag(final byte[] array) {
        final byte[] array2 = new byte[4];
        if (array != null) {
            Pack.intToBigEndian(array.length * 8, array2, 0);
        }
        return array2;
    }
}
