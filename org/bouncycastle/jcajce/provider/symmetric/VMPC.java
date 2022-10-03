package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class VMPC
{
    private VMPC() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new VMPCEngine(), 16);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("VMPC", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mac extends BaseMac
    {
        public Mac() {
            super(new VMPCMac());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.VMPC", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("KeyGenerator.VMPC", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Mac.VMPCMAC", Mappings.PREFIX + "$Mac");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.VMPC", "VMPCMAC");
            configurableProvider.addAlgorithm("Alg.Alias.Mac.VMPC-MAC", "VMPCMAC");
        }
        
        static {
            PREFIX = VMPC.class.getName();
        }
    }
}
