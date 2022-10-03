package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;

public class Whirlpool
{
    private Whirlpool() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new WhirlpoolDigest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new WhirlpoolDigest((WhirlpoolDigest)this.digest);
            return digest;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new WhirlpoolDigest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACWHIRLPOOL", 512, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.WHIRLPOOL", Mappings.PREFIX + "$Digest");
            this.addHMACAlgorithm(configurableProvider, "WHIRLPOOL", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
        }
        
        static {
            PREFIX = Whirlpool.class.getName();
        }
    }
}
