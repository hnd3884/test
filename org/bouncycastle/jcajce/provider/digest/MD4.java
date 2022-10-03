package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD4Digest;

public class MD4
{
    private MD4() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new MD4Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new MD4Digest((MD4Digest)this.digest);
            return digest;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new MD4Digest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACMD4", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.MD4", Mappings.PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md4, "MD4");
            this.addHMACAlgorithm(configurableProvider, "MD4", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
        }
        
        static {
            PREFIX = MD4.class.getName();
        }
    }
}
