package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;

public class Keccak
{
    private Keccak() {
    }
    
    public static class Digest224 extends DigestKeccak
    {
        public Digest224() {
            super(224);
        }
    }
    
    public static class Digest256 extends DigestKeccak
    {
        public Digest256() {
            super(256);
        }
    }
    
    public static class DigestKeccak extends BCMessageDigest implements Cloneable
    {
        public DigestKeccak(final int n) {
            super(new KeccakDigest(n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new KeccakDigest((KeccakDigest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class Digest288 extends DigestKeccak
    {
        public Digest288() {
            super(288);
        }
    }
    
    public static class Digest384 extends DigestKeccak
    {
        public Digest384() {
            super(384);
        }
    }
    
    public static class Digest512 extends DigestKeccak
    {
        public Digest512() {
            super(512);
        }
    }
    
    public static class HashMac224 extends BaseMac
    {
        public HashMac224() {
            super(new HMac(new KeccakDigest(224)));
        }
    }
    
    public static class HashMac256 extends BaseMac
    {
        public HashMac256() {
            super(new HMac(new KeccakDigest(256)));
        }
    }
    
    public static class HashMac288 extends BaseMac
    {
        public HashMac288() {
            super(new HMac(new KeccakDigest(288)));
        }
    }
    
    public static class HashMac384 extends BaseMac
    {
        public HashMac384() {
            super(new HMac(new KeccakDigest(384)));
        }
    }
    
    public static class HashMac512 extends BaseMac
    {
        public HashMac512() {
            super(new HMac(new KeccakDigest(512)));
        }
    }
    
    public static class KeyGenerator224 extends BaseKeyGenerator
    {
        public KeyGenerator224() {
            super("HMACKECCAK224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator256 extends BaseKeyGenerator
    {
        public KeyGenerator256() {
            super("HMACKECCAK256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator288 extends BaseKeyGenerator
    {
        public KeyGenerator288() {
            super("HMACKECCAK288", 288, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator384 extends BaseKeyGenerator
    {
        public KeyGenerator384() {
            super("HMACKECCAK384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator512 extends BaseKeyGenerator
    {
        public KeyGenerator512() {
            super("HMACKECCAK512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-224", Mappings.PREFIX + "$Digest224");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-288", Mappings.PREFIX + "$Digest288");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-256", Mappings.PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-384", Mappings.PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest.KECCAK-512", Mappings.PREFIX + "$Digest512");
            this.addHMACAlgorithm(configurableProvider, "KECCAK224", Mappings.PREFIX + "$HashMac224", Mappings.PREFIX + "$KeyGenerator224");
            this.addHMACAlgorithm(configurableProvider, "KECCAK256", Mappings.PREFIX + "$HashMac256", Mappings.PREFIX + "$KeyGenerator256");
            this.addHMACAlgorithm(configurableProvider, "KECCAK288", Mappings.PREFIX + "$HashMac288", Mappings.PREFIX + "$KeyGenerator288");
            this.addHMACAlgorithm(configurableProvider, "KECCAK384", Mappings.PREFIX + "$HashMac384", Mappings.PREFIX + "$KeyGenerator384");
            this.addHMACAlgorithm(configurableProvider, "KECCAK512", Mappings.PREFIX + "$HashMac512", Mappings.PREFIX + "$KeyGenerator512");
        }
        
        static {
            PREFIX = Keccak.class.getName();
        }
    }
}
