package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.AlgorithmParameterGeneratorSpi;

public abstract class BaseAlgorithmParameterGenerator extends AlgorithmParameterGeneratorSpi
{
    private final JcaJceHelper helper;
    protected SecureRandom random;
    protected int strength;
    
    public BaseAlgorithmParameterGenerator() {
        this.helper = new BCJcaJceHelper();
        this.strength = 1024;
    }
    
    protected final AlgorithmParameters createParametersInstance(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(s);
    }
    
    @Override
    protected void engineInit(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
}
