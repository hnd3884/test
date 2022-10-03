package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;

public final class ARC4
{
    private ARC4() {
    }
    
    public static class Base extends BaseStreamCipher
    {
        public Base() {
            super(new RC4Engine(), 0);
        }
    }
    
    public static class KeyGen extends BaseKeyGenerator
    {
        public KeyGen() {
            super("RC4", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.ARC4", Mappings.PREFIX + "$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.rc4, "ARC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.ARCFOUR", "ARC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.RC4", "ARC4");
            configurableProvider.addAlgorithm("KeyGenerator.ARC4", Mappings.PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.RC4", "ARC4");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.1.2.840.113549.3.4", "ARC4");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITRC4", Mappings.PREFIX + "$PBEWithSHAAnd128BitKeyFactory");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND40BITRC4", Mappings.PREFIX + "$PBEWithSHAAnd40BitKeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND40BITRC4", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITRC4", "PKCS12PBE");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAANDRC4", "PKCS12PBE");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND128BITRC4", Mappings.PREFIX + "$PBEWithSHAAnd128Bit");
            configurableProvider.addAlgorithm("Cipher.PBEWITHSHAAND40BITRC4", Mappings.PREFIX + "$PBEWithSHAAnd40Bit");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, "PBEWITHSHAAND128BITRC4");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory", PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, "PBEWITHSHAAND40BITRC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITRC4", "PBEWITHSHAAND128BITRC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND40BITRC4", "PBEWITHSHAAND40BITRC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, "PBEWITHSHAAND128BITRC4");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher", PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, "PBEWITHSHAAND40BITRC4");
        }
        
        static {
            PREFIX = ARC4.class.getName();
        }
    }
    
    public static class PBEWithSHAAnd128Bit extends BaseStreamCipher
    {
        public PBEWithSHAAnd128Bit() {
            super(new RC4Engine(), 0, 128, 1);
        }
    }
    
    public static class PBEWithSHAAnd128BitKeyFactory extends PBESecretKeyFactory
    {
        public PBEWithSHAAnd128BitKeyFactory() {
            super("PBEWithSHAAnd128BitRC4", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, true, 2, 1, 128, 0);
        }
    }
    
    public static class PBEWithSHAAnd40Bit extends BaseStreamCipher
    {
        public PBEWithSHAAnd40Bit() {
            super(new RC4Engine(), 0, 40, 1);
        }
    }
    
    public static class PBEWithSHAAnd40BitKeyFactory extends PBESecretKeyFactory
    {
        public PBEWithSHAAnd40BitKeyFactory() {
            super("PBEWithSHAAnd128BitRC4", PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, true, 2, 1, 40, 0);
        }
    }
}
