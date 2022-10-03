package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;

public class Blake2b
{
    private Blake2b() {
    }
    
    public static class Blake2b160 extends BCMessageDigest implements Cloneable
    {
        public Blake2b160() {
            super(new Blake2bDigest(160));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Blake2b160 blake2b160 = (Blake2b160)super.clone();
            blake2b160.digest = new Blake2bDigest((Blake2bDigest)this.digest);
            return blake2b160;
        }
    }
    
    public static class Blake2b256 extends BCMessageDigest implements Cloneable
    {
        public Blake2b256() {
            super(new Blake2bDigest(256));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Blake2b256 blake2b256 = (Blake2b256)super.clone();
            blake2b256.digest = new Blake2bDigest((Blake2bDigest)this.digest);
            return blake2b256;
        }
    }
    
    public static class Blake2b384 extends BCMessageDigest implements Cloneable
    {
        public Blake2b384() {
            super(new Blake2bDigest(384));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Blake2b384 blake2b384 = (Blake2b384)super.clone();
            blake2b384.digest = new Blake2bDigest((Blake2bDigest)this.digest);
            return blake2b384;
        }
    }
    
    public static class Blake2b512 extends BCMessageDigest implements Cloneable
    {
        public Blake2b512() {
            super(new Blake2bDigest(512));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Blake2b512 blake2b512 = (Blake2b512)super.clone();
            blake2b512.digest = new Blake2bDigest((Blake2bDigest)this.digest);
            return blake2b512;
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2B-512", Mappings.PREFIX + "$Blake2b512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b512, "BLAKE2B-512");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2B-384", Mappings.PREFIX + "$Blake2b384");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b384, "BLAKE2B-384");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2B-256", Mappings.PREFIX + "$Blake2b256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b256, "BLAKE2B-256");
            configurableProvider.addAlgorithm("MessageDigest.BLAKE2B-160", Mappings.PREFIX + "$Blake2b160");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + MiscObjectIdentifiers.id_blake2b160, "BLAKE2B-160");
        }
        
        static {
            PREFIX = Blake2b.class.getName();
        }
    }
}
