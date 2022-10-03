package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class XSalsa20
{
    private XSalsa20() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new XSalsa20Engine(), 24);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("XSalsa20", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.XSALSA20", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.XSALSA20", Mappings.PREFIX + "$KeyGen");
        }
        
        static {
            PREFIX = XSalsa20.class.getName();
        }
    }
}
