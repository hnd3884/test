package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD2Digest;

public class MD2
{
    private MD2() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new MD2Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new MD2Digest((MD2Digest)this.digest);
            return digest;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new MD2Digest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACMD2", 128, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.MD2", Mappings.PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + PKCSObjectIdentifiers.md2, "MD2");
            this.addHMACAlgorithm(configurableProvider, "MD2", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
        }
        
        static {
            PREFIX = MD2.class.getName();
        }
    }
}
