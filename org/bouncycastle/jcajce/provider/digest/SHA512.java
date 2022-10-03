package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.macs.OldHMac;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

public class SHA512
{
    private SHA512() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new SHA512Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new SHA512Digest((SHA512Digest)this.digest);
            return digest;
        }
    }
    
    public static class DigestT extends BCMessageDigest implements Cloneable
    {
        public DigestT(final int n) {
            super(new SHA512tDigest(n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final DigestT digestT = (DigestT)super.clone();
            digestT.digest = new SHA512tDigest((SHA512tDigest)this.digest);
            return digestT;
        }
    }
    
    public static class DigestT224 extends DigestT
    {
        public DigestT224() {
            super(224);
        }
    }
    
    public static class DigestT256 extends DigestT
    {
        public DigestT256() {
            super(256);
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new SHA512Digest()));
        }
    }
    
    public static class HashMacT224 extends BaseMac
    {
        public HashMacT224() {
            super(new HMac(new SHA512tDigest(224)));
        }
    }
    
    public static class HashMacT256 extends BaseMac
    {
        public HashMacT256() {
            super(new HMac(new SHA512tDigest(256)));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACSHA512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGeneratorT224 extends BaseKeyGenerator
    {
        public KeyGeneratorT224() {
            super("HMACSHA512/224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGeneratorT256 extends BaseKeyGenerator
    {
        public KeyGeneratorT256() {
            super("HMACSHA512/256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.SHA-512", Mappings.PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512", "SHA-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512, "SHA-512");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/224", Mappings.PREFIX + "$DigestT224");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512/224", "SHA-512/224");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_224, "SHA-512/224");
            configurableProvider.addAlgorithm("MessageDigest.SHA-512/256", Mappings.PREFIX + "$DigestT256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA512256", "SHA-512/256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + NISTObjectIdentifiers.id_sha512_256, "SHA-512/256");
            configurableProvider.addAlgorithm("Mac.OLDHMACSHA512", Mappings.PREFIX + "$OldSHA512");
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA512", Mappings.PREFIX + "$HashMac");
            this.addHMACAlgorithm(configurableProvider, "SHA512", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
            this.addHMACAlias(configurableProvider, "SHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
            this.addHMACAlgorithm(configurableProvider, "SHA512/224", Mappings.PREFIX + "$HashMacT224", Mappings.PREFIX + "$KeyGeneratorT224");
            this.addHMACAlgorithm(configurableProvider, "SHA512/256", Mappings.PREFIX + "$HashMacT256", Mappings.PREFIX + "$KeyGeneratorT256");
        }
        
        static {
            PREFIX = SHA512.class.getName();
        }
    }
    
    public static class OldSHA512 extends BaseMac
    {
        public OldSHA512() {
            super(new OldHMac(new SHA512Digest()));
        }
    }
}
