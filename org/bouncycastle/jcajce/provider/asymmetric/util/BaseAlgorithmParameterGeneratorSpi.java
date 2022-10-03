package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.AlgorithmParameterGeneratorSpi;

public abstract class BaseAlgorithmParameterGeneratorSpi extends AlgorithmParameterGeneratorSpi
{
    private final JcaJceHelper helper;
    
    public BaseAlgorithmParameterGeneratorSpi() {
        this.helper = new BCJcaJceHelper();
    }
    
    protected final AlgorithmParameters createParametersInstance(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(s);
    }
}
