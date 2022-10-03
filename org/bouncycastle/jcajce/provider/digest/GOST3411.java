package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;

public class GOST3411
{
    private GOST3411() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new GOST3411Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new GOST3411Digest((GOST3411Digest)this.digest);
            return digest;
        }
    }
    
    public static class Digest2012_256 extends BCMessageDigest implements Cloneable
    {
        public Digest2012_256() {
            super(new GOST3411_2012_256Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest2012_256 digest2012_256 = (Digest2012_256)super.clone();
            digest2012_256.digest = new GOST3411_2012_256Digest((GOST3411_2012_256Digest)this.digest);
            return digest2012_256;
        }
    }
    
    public static class Digest2012_512 extends BCMessageDigest implements Cloneable
    {
        public Digest2012_512() {
            super(new GOST3411_2012_512Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest2012_512 digest2012_512 = (Digest2012_512)super.clone();
            digest2012_512.digest = new GOST3411_2012_512Digest((GOST3411_2012_512Digest)this.digest);
            return digest2012_512;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new GOST3411Digest()));
        }
    }
    
    public static class HashMac2012_256 extends BaseMac
    {
        public HashMac2012_256() {
            super(new HMac(new GOST3411_2012_256Digest()));
        }
    }
    
    public static class HashMac2012_512 extends BaseMac
    {
        public HashMac2012_512() {
            super(new HMac(new GOST3411_2012_512Digest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACGOST3411", 256, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator2012_256 extends BaseKeyGenerator
    {
        public KeyGenerator2012_256() {
            super("HMACGOST3411", 256, new CipherKeyGenerator());
        }
    }
    
    public static class KeyGenerator2012_512 extends BaseKeyGenerator
    {
        public KeyGenerator2012_512() {
            super("HMACGOST3411", 512, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.GOST3411", Mappings.PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST", "GOST3411");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411", "GOST3411");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + CryptoProObjectIdentifiers.gostR3411, "GOST3411");
            this.addHMACAlgorithm(configurableProvider, "GOST3411", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
            this.addHMACAlias(configurableProvider, "GOST3411", CryptoProObjectIdentifiers.gostR3411);
            configurableProvider.addAlgorithm("MessageDigest.GOST3411-2012-256", Mappings.PREFIX + "$Digest2012_256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-2012-256", "GOST3411-2012-256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411-2012-256", "GOST3411-2012-256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
            this.addHMACAlgorithm(configurableProvider, "GOST3411-2012-256", Mappings.PREFIX + "$HashMac2012_256", Mappings.PREFIX + "$KeyGenerator2012_256");
            this.addHMACAlias(configurableProvider, "GOST3411-2012-256", RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_256);
            configurableProvider.addAlgorithm("MessageDigest.GOST3411-2012-512", Mappings.PREFIX + "$Digest2012_512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-2012-512", "GOST3411-2012-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.GOST-3411-2012-512", "GOST3411-2012-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
            this.addHMACAlgorithm(configurableProvider, "GOST3411-2012-512", Mappings.PREFIX + "$HashMac2012_512", Mappings.PREFIX + "$KeyGenerator2012_512");
            this.addHMACAlias(configurableProvider, "GOST3411-2012-512", RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_512);
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACGOST3411", Mappings.PREFIX + "$PBEWithMacKeyFactory");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + CryptoProObjectIdentifiers.gostR3411, "PBEWITHHMACGOST3411");
        }
        
        static {
            PREFIX = GOST3411.class.getName();
        }
    }
    
    public static class PBEWithMacKeyFactory extends PBESecretKeyFactory
    {
        public PBEWithMacKeyFactory() {
            super("PBEwithHmacGOST3411", null, false, 2, 6, 256, 0);
        }
    }
}
