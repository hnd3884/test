package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface ExchangePairGenerator
{
    @Deprecated
    ExchangePair GenerateExchange(final AsymmetricKeyParameter p0);
    
    ExchangePair generateExchange(final AsymmetricKeyParameter p0);
}
