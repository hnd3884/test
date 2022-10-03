package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.DSTU7564Digest;

public class DSTU7564
{
    private DSTU7564() {
    }
    
    public static class Digest256 extends DigestDSTU7564
    {
        public Digest256() {
            super(256);
        }
    }
    
    public static class Digest384 extends DigestDSTU7564
    {
        public Digest384() {
            super(384);
        }
    }
    
    public static class DigestDSTU7564 extends BCMessageDigest implements Cloneable
    {
        public DigestDSTU7564(final int n) {
            super(new DSTU7564Digest(n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new DSTU7564Digest((DSTU7564Digest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class Digest512 extends DigestDSTU7564
    {
        public Digest512() {
            super(512);
        }
    }
    
    public static class HashMac256 extends BaseMac
    {
        public HashMac256() {
            super(new DSTU7564Mac(256));
        }
    }
    
    public static class HashMac384 extends BaseMac
    {
        public HashMac384() {
            super(new DSTU7564Mac(384));
        }
    }
    
    public static class HashMac512 extends BaseMac
    {
        public HashMac512() {
            super(new DSTU7564Mac(512));
        }
    }
    
    public static class KeyGenerator256 extends BaseKeyGenerator
    {
        public KeyGenerator256() {
            super("HMACDSTU7564-256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator384 extends BaseKeyGenerator
    {
        public KeyGenerator384() {
            super("HMACDSTU7564-384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator512 extends BaseKeyGenerator
    {
        public KeyGenerator512() {
            super("HMACDSTU7564-512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-256", Mappings.PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-384", Mappings.PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest.DSTU7564-512", Mappings.PREFIX + "$Digest512");
            configurableProvider.addAlgorithm("MessageDigest", UAObjectIdentifiers.dstu7564digest_256, Mappings.PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest", UAObjectIdentifiers.dstu7564digest_384, Mappings.PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest", UAObjectIdentifiers.dstu7564digest_512, Mappings.PREFIX + "$Digest512");
            this.addHMACAlgorithm(configurableProvider, "DSTU7564-256", Mappings.PREFIX + "$HashMac256", Mappings.PREFIX + "$KeyGenerator256");
            this.addHMACAlgorithm(configurableProvider, "DSTU7564-384", Mappings.PREFIX + "$HashMac384", Mappings.PREFIX + "$KeyGenerator384");
            this.addHMACAlgorithm(configurableProvider, "DSTU7564-512", Mappings.PREFIX + "$HashMac512", Mappings.PREFIX + "$KeyGenerator512");
            this.addHMACAlias(configurableProvider, "DSTU7564-256", UAObjectIdentifiers.dstu7564mac_256);
            this.addHMACAlias(configurableProvider, "DSTU7564-384", UAObjectIdentifiers.dstu7564mac_384);
            this.addHMACAlias(configurableProvider, "DSTU7564-512", UAObjectIdentifiers.dstu7564mac_512);
        }
        
        static {
            PREFIX = DSTU7564.class.getName();
        }
    }
}
