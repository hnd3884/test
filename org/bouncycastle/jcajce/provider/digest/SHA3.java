package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;

public class SHA3
{
    private SHA3() {
    }
    
    public static class Digest224 extends DigestSHA3
    {
        public Digest224() {
            super(224);
        }
    }
    
    public static class Digest256 extends DigestSHA3
    {
        public Digest256() {
            super(256);
        }
    }
    
    public static class DigestSHA3 extends BCMessageDigest implements Cloneable
    {
        public DigestSHA3(final int n) {
            super(new SHA3Digest(n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new SHA3Digest((SHA3Digest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class Digest384 extends DigestSHA3
    {
        public Digest384() {
            super(384);
        }
    }
    
    public static class Digest512 extends DigestSHA3
    {
        public Digest512() {
            super(512);
        }
    }
    
    public static class HashMac224 extends HashMacSHA3
    {
        public HashMac224() {
            super(224);
        }
    }
    
    public static class HashMacSHA3 extends BaseMac
    {
        public HashMacSHA3(final int n) {
            super(new HMac(new SHA3Digest(n)));
        }
    }
    
    public static class HashMac256 extends HashMacSHA3
    {
        public HashMac256() {
            super(256);
        }
    }
    
    public static class HashMac384 extends HashMacSHA3
    {
        public HashMac384() {
            super(384);
        }
    }
    
    public static class HashMac512 extends HashMacSHA3
    {
        public HashMac512() {
            super(512);
        }
    }
    
    public static class KeyGenerator224 extends KeyGeneratorSHA3
    {
        public KeyGenerator224() {
            super(224);
        }
    }
    
    public static class KeyGeneratorSHA3 extends BaseKeyGenerator
    {
        public KeyGeneratorSHA3(final int n) {
            super("HMACSHA3-" + n, n, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator256 extends KeyGeneratorSHA3
    {
        public KeyGenerator256() {
            super(256);
        }
    }
    
    public static class KeyGenerator384 extends KeyGeneratorSHA3
    {
        public KeyGenerator384() {
            super(384);
        }
    }
    
    public static class KeyGenerator512 extends KeyGeneratorSHA3
    {
        public KeyGenerator512() {
            super(512);
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.SHA3-224", Mappings.PREFIX + "$Digest224");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-256", Mappings.PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-384", Mappings.PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-512", Mappings.PREFIX + "$Digest512");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_224, Mappings.PREFIX + "$Digest224");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_256, Mappings.PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_384, Mappings.PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_512, Mappings.PREFIX + "$Digest512");
            this.addHMACAlgorithm(configurableProvider, "SHA3-224", Mappings.PREFIX + "$HashMac224", Mappings.PREFIX + "$KeyGenerator224");
            this.addHMACAlias(configurableProvider, "SHA3-224", NISTObjectIdentifiers.id_hmacWithSHA3_224);
            this.addHMACAlgorithm(configurableProvider, "SHA3-256", Mappings.PREFIX + "$HashMac256", Mappings.PREFIX + "$KeyGenerator256");
            this.addHMACAlias(configurableProvider, "SHA3-256", NISTObjectIdentifiers.id_hmacWithSHA3_256);
            this.addHMACAlgorithm(configurableProvider, "SHA3-384", Mappings.PREFIX + "$HashMac384", Mappings.PREFIX + "$KeyGenerator384");
            this.addHMACAlias(configurableProvider, "SHA3-384", NISTObjectIdentifiers.id_hmacWithSHA3_384);
            this.addHMACAlgorithm(configurableProvider, "SHA3-512", Mappings.PREFIX + "$HashMac512", Mappings.PREFIX + "$KeyGenerator512");
            this.addHMACAlias(configurableProvider, "SHA3-512", NISTObjectIdentifiers.id_hmacWithSHA3_512);
        }
        
        static {
            PREFIX = SHA3.class.getName();
        }
    }
}
