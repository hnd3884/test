package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.VMPCKSA3Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class VMPCKSA3
{
    private VMPCKSA3() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new VMPCKSA3Engine(), 16);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("VMPC-KSA3", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.VMPC-KSA3", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.VMPC-KSA3", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = VMPCKSA3.class.getName();
        }
    }
}
