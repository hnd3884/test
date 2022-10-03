package org.bouncycastle.crypto;

import java.math.BigInteger;

public interface BasicAgreement
{
    void init(final CipherParameters p0);
    
    int getFieldSize();
    
    BigInteger calculateAgreement(final CipherParameters p0);
}
