package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;

public class Skein
{
    private Skein() {
    }
    
    public static class DigestSkein1024 extends BCMessageDigest implements Cloneable
    {
        public DigestSkein1024(final int n) {
            super(new SkeinDigest(1024, n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new SkeinDigest((SkeinDigest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class DigestSkein256 extends BCMessageDigest implements Cloneable
    {
        public DigestSkein256(final int n) {
            super(new SkeinDigest(256, n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new SkeinDigest((SkeinDigest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class DigestSkein512 extends BCMessageDigest implements Cloneable
    {
        public DigestSkein512(final int n) {
            super(new SkeinDigest(512, n));
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final BCMessageDigest bcMessageDigest = (BCMessageDigest)super.clone();
            bcMessageDigest.digest = new SkeinDigest((SkeinDigest)this.digest);
            return bcMessageDigest;
        }
    }
    
    public static class Digest_1024_1024 extends DigestSkein1024
    {
        public Digest_1024_1024() {
            super(1024);
        }
    }
    
    public static class Digest_1024_384 extends DigestSkein1024
    {
        public Digest_1024_384() {
            super(384);
        }
    }
    
    public static class Digest_1024_512 extends DigestSkein1024
    {
        public Digest_1024_512() {
            super(512);
        }
    }
    
    public static class Digest_256_128 extends DigestSkein256
    {
        public Digest_256_128() {
            super(128);
        }
    }
    
    public static class Digest_256_160 extends DigestSkein256
    {
        public Digest_256_160() {
            super(160);
        }
    }
    
    public static class Digest_256_224 extends DigestSkein256
    {
        public Digest_256_224() {
            super(224);
        }
    }
    
    public static class Digest_256_256 extends DigestSkein256
    {
        public Digest_256_256() {
            super(256);
        }
    }
    
    public static class Digest_512_128 extends DigestSkein512
    {
        public Digest_512_128() {
            super(128);
        }
    }
    
    public static class Digest_512_160 extends DigestSkein512
    {
        public Digest_512_160() {
            super(160);
        }
    }
    
    public static class Digest_512_224 extends DigestSkein512
    {
        public Digest_512_224() {
            super(224);
        }
    }
    
    public static class Digest_512_256 extends DigestSkein512
    {
        public Digest_512_256() {
            super(256);
        }
    }
    
    public static class Digest_512_384 extends DigestSkein512
    {
        public Digest_512_384() {
            super(384);
        }
    }
    
    public static class Digest_512_512 extends DigestSkein512
    {
        public Digest_512_512() {
            super(512);
        }
    }
    
    public static class HMacKeyGenerator_1024_1024 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_1024_1024() {
            super("HMACSkein-1024-1024", 1024, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_1024_384 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_1024_384() {
            super("HMACSkein-1024-384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_1024_512 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_1024_512() {
            super("HMACSkein-1024-512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_256_128 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_256_128() {
            super("HMACSkein-256-128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_256_160 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_256_160() {
            super("HMACSkein-256-160", 160, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_256_224 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_256_224() {
            super("HMACSkein-256-224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_256_256 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_256_256() {
            super("HMACSkein-256-256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_128 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_128() {
            super("HMACSkein-512-128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_160 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_160() {
            super("HMACSkein-512-160", 160, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_224 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_224() {
            super("HMACSkein-512-224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_256 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_256() {
            super("HMACSkein-512-256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_384 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_384() {
            super("HMACSkein-512-384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class HMacKeyGenerator_512_512 extends BaseKeyGenerator
    {
        public HMacKeyGenerator_512_512() {
            super("HMACSkein-512-512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class HashMac_1024_1024 extends BaseMac
    {
        public HashMac_1024_1024() {
            super(new HMac(new SkeinDigest(1024, 1024)));
        }
    }
    
    public static class HashMac_1024_384 extends BaseMac
    {
        public HashMac_1024_384() {
            super(new HMac(new SkeinDigest(1024, 384)));
        }
    }
    
    public static class HashMac_1024_512 extends BaseMac
    {
        public HashMac_1024_512() {
            super(new HMac(new SkeinDigest(1024, 512)));
        }
    }
    
    public static class HashMac_256_128 extends BaseMac
    {
        public HashMac_256_128() {
            super(new HMac(new SkeinDigest(256, 128)));
        }
    }
    
    public static class HashMac_256_160 extends BaseMac
    {
        public HashMac_256_160() {
            super(new HMac(new SkeinDigest(256, 160)));
        }
    }
    
    public static class HashMac_256_224 extends BaseMac
    {
        public HashMac_256_224() {
            super(new HMac(new SkeinDigest(256, 224)));
        }
    }
    
    public static class HashMac_256_256 extends BaseMac
    {
        public HashMac_256_256() {
            super(new HMac(new SkeinDigest(256, 256)));
        }
    }
    
    public static class HashMac_512_128 extends BaseMac
    {
        public HashMac_512_128() {
            super(new HMac(new SkeinDigest(512, 128)));
        }
    }
    
    public static class HashMac_512_160 extends BaseMac
    {
        public HashMac_512_160() {
            super(new HMac(new SkeinDigest(512, 160)));
        }
    }
    
    public static class HashMac_512_224 extends BaseMac
    {
        public HashMac_512_224() {
            super(new HMac(new SkeinDigest(512, 224)));
        }
    }
    
    public static class HashMac_512_256 extends BaseMac
    {
        public HashMac_512_256() {
            super(new HMac(new SkeinDigest(512, 256)));
        }
    }
    
    public static class HashMac_512_384 extends BaseMac
    {
        public HashMac_512_384() {
            super(new HMac(new SkeinDigest(512, 384)));
        }
    }
    
    public static class HashMac_512_512 extends BaseMac
    {
        public HashMac_512_512() {
            super(new HMac(new SkeinDigest(512, 512)));
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.Skein-256-128", Mappings.PREFIX + "$Digest_256_128");
            configurableProvider.addAlgorithm("MessageDigest.Skein-256-160", Mappings.PREFIX + "$Digest_256_160");
            configurableProvider.addAlgorithm("MessageDigest.Skein-256-224", Mappings.PREFIX + "$Digest_256_224");
            configurableProvider.addAlgorithm("MessageDigest.Skein-256-256", Mappings.PREFIX + "$Digest_256_256");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-128", Mappings.PREFIX + "$Digest_512_128");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-160", Mappings.PREFIX + "$Digest_512_160");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-224", Mappings.PREFIX + "$Digest_512_224");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-256", Mappings.PREFIX + "$Digest_512_256");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-384", Mappings.PREFIX + "$Digest_512_384");
            configurableProvider.addAlgorithm("MessageDigest.Skein-512-512", Mappings.PREFIX + "$Digest_512_512");
            configurableProvider.addAlgorithm("MessageDigest.Skein-1024-384", Mappings.PREFIX + "$Digest_1024_384");
            configurableProvider.addAlgorithm("MessageDigest.Skein-1024-512", Mappings.PREFIX + "$Digest_1024_512");
            configurableProvider.addAlgorithm("MessageDigest.Skein-1024-1024", Mappings.PREFIX + "$Digest_1024_1024");
            this.addHMACAlgorithm(configurableProvider, "Skein-256-128", Mappings.PREFIX + "$HashMac_256_128", Mappings.PREFIX + "$HMacKeyGenerator_256_128");
            this.addHMACAlgorithm(configurableProvider, "Skein-256-160", Mappings.PREFIX + "$HashMac_256_160", Mappings.PREFIX + "$HMacKeyGenerator_256_160");
            this.addHMACAlgorithm(configurableProvider, "Skein-256-224", Mappings.PREFIX + "$HashMac_256_224", Mappings.PREFIX + "$HMacKeyGenerator_256_224");
            this.addHMACAlgorithm(configurableProvider, "Skein-256-256", Mappings.PREFIX + "$HashMac_256_256", Mappings.PREFIX + "$HMacKeyGenerator_256_256");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-128", Mappings.PREFIX + "$HashMac_512_128", Mappings.PREFIX + "$HMacKeyGenerator_512_128");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-160", Mappings.PREFIX + "$HashMac_512_160", Mappings.PREFIX + "$HMacKeyGenerator_512_160");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-224", Mappings.PREFIX + "$HashMac_512_224", Mappings.PREFIX + "$HMacKeyGenerator_512_224");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-256", Mappings.PREFIX + "$HashMac_512_256", Mappings.PREFIX + "$HMacKeyGenerator_512_256");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-384", Mappings.PREFIX + "$HashMac_512_384", Mappings.PREFIX + "$HMacKeyGenerator_512_384");
            this.addHMACAlgorithm(configurableProvider, "Skein-512-512", Mappings.PREFIX + "$HashMac_512_512", Mappings.PREFIX + "$HMacKeyGenerator_512_512");
            this.addHMACAlgorithm(configurableProvider, "Skein-1024-384", Mappings.PREFIX + "$HashMac_1024_384", Mappings.PREFIX + "$HMacKeyGenerator_1024_384");
            this.addHMACAlgorithm(configurableProvider, "Skein-1024-512", Mappings.PREFIX + "$HashMac_1024_512", Mappings.PREFIX + "$HMacKeyGenerator_1024_512");
            this.addHMACAlgorithm(configurableProvider, "Skein-1024-1024", Mappings.PREFIX + "$HashMac_1024_1024", Mappings.PREFIX + "$HMacKeyGenerator_1024_1024");
            this.addSkeinMacAlgorithm(configurableProvider, 256, 128);
            this.addSkeinMacAlgorithm(configurableProvider, 256, 160);
            this.addSkeinMacAlgorithm(configurableProvider, 256, 224);
            this.addSkeinMacAlgorithm(configurableProvider, 256, 256);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 128);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 160);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 224);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 256);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 384);
            this.addSkeinMacAlgorithm(configurableProvider, 512, 512);
            this.addSkeinMacAlgorithm(configurableProvider, 1024, 384);
            this.addSkeinMacAlgorithm(configurableProvider, 1024, 512);
            this.addSkeinMacAlgorithm(configurableProvider, 1024, 1024);
        }
        
        private void addSkeinMacAlgorithm(final ConfigurableProvider configurableProvider, final int n, final int n2) {
            final String string = "Skein-MAC-" + n + "-" + n2;
            final String string2 = Mappings.PREFIX + "$SkeinMac_" + n + "_" + n2;
            final String string3 = Mappings.PREFIX + "$SkeinMacKeyGenerator_" + n + "_" + n2;
            configurableProvider.addAlgorithm("Mac." + string, string2);
            configurableProvider.addAlgorithm("Alg.Alias.Mac.Skein-MAC" + n + "/" + n2, string);
            configurableProvider.addAlgorithm("KeyGenerator." + string, string3);
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator.Skein-MAC" + n + "/" + n2, string);
        }
        
        static {
            PREFIX = Skein.class.getName();
        }
    }
    
    public static class SkeinMacKeyGenerator_1024_1024 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_1024_1024() {
            super("Skein-MAC-1024-1024", 1024, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_1024_384 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_1024_384() {
            super("Skein-MAC-1024-384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_1024_512 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_1024_512() {
            super("Skein-MAC-1024-512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_256_128 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_256_128() {
            super("Skein-MAC-256-128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_256_160 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_256_160() {
            super("Skein-MAC-256-160", 160, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_256_224 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_256_224() {
            super("Skein-MAC-256-224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_256_256 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_256_256() {
            super("Skein-MAC-256-256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_128 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_128() {
            super("Skein-MAC-512-128", 128, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_160 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_160() {
            super("Skein-MAC-512-160", 160, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_224 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_224() {
            super("Skein-MAC-512-224", 224, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_256 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_256() {
            super("Skein-MAC-512-256", 256, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_384 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_384() {
            super("Skein-MAC-512-384", 384, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMacKeyGenerator_512_512 extends BaseKeyGenerator
    {
        public SkeinMacKeyGenerator_512_512() {
            super("Skein-MAC-512-512", 512, new CipherKeyGenerator());
        }
    }
    
    public static class SkeinMac_1024_1024 extends BaseMac
    {
        public SkeinMac_1024_1024() {
            super(new SkeinMac(1024, 1024));
        }
    }
    
    public static class SkeinMac_1024_384 extends BaseMac
    {
        public SkeinMac_1024_384() {
            super(new SkeinMac(1024, 384));
        }
    }
    
    public static class SkeinMac_1024_512 extends BaseMac
    {
        public SkeinMac_1024_512() {
            super(new SkeinMac(1024, 512));
        }
    }
    
    public static class SkeinMac_256_128 extends BaseMac
    {
        public SkeinMac_256_128() {
            super(new SkeinMac(256, 128));
        }
    }
    
    public static class SkeinMac_256_160 extends BaseMac
    {
        public SkeinMac_256_160() {
            super(new SkeinMac(256, 160));
        }
    }
    
    public static class SkeinMac_256_224 extends BaseMac
    {
        public SkeinMac_256_224() {
            super(new SkeinMac(256, 224));
        }
    }
    
    public static class SkeinMac_256_256 extends BaseMac
    {
        public SkeinMac_256_256() {
            super(new SkeinMac(256, 256));
        }
    }
    
    public static class SkeinMac_512_128 extends BaseMac
    {
        public SkeinMac_512_128() {
            super(new SkeinMac(512, 128));
        }
    }
    
    public static class SkeinMac_512_160 extends BaseMac
    {
        public SkeinMac_512_160() {
            super(new SkeinMac(512, 160));
        }
    }
    
    public static class SkeinMac_512_224 extends BaseMac
    {
        public SkeinMac_512_224() {
            super(new SkeinMac(512, 224));
        }
    }
    
    public static class SkeinMac_512_256 extends BaseMac
    {
        public SkeinMac_512_256() {
            super(new SkeinMac(512, 256));
        }
    }
    
    public static class SkeinMac_512_384 extends BaseMac
    {
        public SkeinMac_512_384() {
            super(new SkeinMac(512, 384));
        }
    }
    
    public static class SkeinMac_512_512 extends BaseMac
    {
        public SkeinMac_512_512() {
            super(new SkeinMac(512, 512));
        }
    }
}
