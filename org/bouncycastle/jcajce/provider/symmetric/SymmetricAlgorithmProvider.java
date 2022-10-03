package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class SymmetricAlgorithmProvider extends AlgorithmProvider
{
    protected void addCMacAlgorithm(final ConfigurableProvider configurableProvider, final String s, final String s2, final String s3) {
        configurableProvider.addAlgorithm("Mac." + s + "-CMAC", s2);
        configurableProvider.addAlgorithm("Alg.Alias.Mac." + s + "CMAC", s + "-CMAC");
        configurableProvider.addAlgorithm("KeyGenerator." + s + "-CMAC", s3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + s + "CMAC", s + "-CMAC");
    }
    
    protected void addGMacAlgorithm(final ConfigurableProvider configurableProvider, final String s, final String s2, final String s3) {
        configurableProvider.addAlgorithm("Mac." + s + "-GMAC", s2);
        configurableProvider.addAlgorithm("Alg.Alias.Mac." + s + "GMAC", s + "-GMAC");
        configurableProvider.addAlgorithm("KeyGenerator." + s + "-GMAC", s3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + s + "GMAC", s + "-GMAC");
    }
    
    protected void addPoly1305Algorithm(final ConfigurableProvider configurableProvider, final String s, final String s2, final String s3) {
        configurableProvider.addAlgorithm("Mac.POLY1305-" + s, s2);
        configurableProvider.addAlgorithm("Alg.Alias.Mac.POLY1305" + s, "POLY1305-" + s);
        configurableProvider.addAlgorithm("KeyGenerator.POLY1305-" + s, s3);
        configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.POLY1305" + s, "POLY1305-" + s);
    }
}
