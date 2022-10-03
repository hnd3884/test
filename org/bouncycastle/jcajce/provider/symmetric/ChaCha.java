package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class ChaCha
{
    private ChaCha() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new ChaChaEngine(), 8);
        }
    }
    
    public static class Base7539 extends BaseStreamCipher
    {
        public Base7539() {
            super(new ChaCha7539Engine(), 12);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("ChaCha", 128, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGen7539 extends BaseKeyGenerator
    {
        public KeyGen7539() {
            super("ChaCha7539", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.CHACHA", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.CHACHA", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Cipher.CHACHA7539", Mappings.PREFIX + "$Base7539");
            configurableProvider.addAlgorithm("KeyGenerator.CHACHA7539", Mappings.PREFIX + "$KeyGen7539");
        }
        
        static {
            PREFIX = ChaCha.class.getName();
        }
    }
}
