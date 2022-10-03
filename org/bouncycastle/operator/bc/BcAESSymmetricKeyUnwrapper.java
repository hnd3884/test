package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public class BcAESSymmetricKeyUnwrapper extends BcSymmetricKeyUnwrapper
{
    public BcAESSymmetricKeyUnwrapper(final KeyParameter keyParameter) {
        super(AESUtil.determineKeyEncAlg(keyParameter), (Wrapper)new AESWrapEngine(), keyParameter);
    }
}
