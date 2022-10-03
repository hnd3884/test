package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;

public class RIPEMD320
{
    private RIPEMD320() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new RIPEMD320Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new RIPEMD320Digest((RIPEMD320Digest)this.digest);
            return digest;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new RIPEMD320Digest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACRIPEMD320", 320, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.RIPEMD320", Mappings.PREFIX + "$Digest");
            this.addHMACAlgorithm(configurableProvider, "RIPEMD320", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
        }
        
        static {
            PREFIX = RIPEMD320.class.getName();
        }
    }
}
