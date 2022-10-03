package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class SHA1
{
    private SHA1() {
    }
    
    public static class Digest extends BCMessageDigest implements Cloneable
    {
        public Digest() {
            super(new SHA1Digest());
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            final Digest digest = (Digest)super.clone();
            digest.digest = new SHA1Digest((SHA1Digest)this.digest);
            return digest;
        }
    }
    
    public static class HashMac extends BaseMac
    {
        public HashMac() {
            super(new HMac(new SHA1Digest()));
        }
    }
    
    public static class KeyGenerator extends BaseKeyGenerator
    {
        public KeyGenerator() {
            super("HMACSHA1", 160, new CipherKeyGenerator());
        }
    }
    
    public static class Mappings extends DigestAlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.SHA-1", Mappings.PREFIX + "$Digest");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA1", "SHA-1");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHA", "SHA-1");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest." + OIWObjectIdentifiers.idSHA1, "SHA-1");
            this.addHMACAlgorithm(configurableProvider, "SHA1", Mappings.PREFIX + "$HashMac", Mappings.PREFIX + "$KeyGenerator");
            this.addHMACAlias(configurableProvider, "SHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
            this.addHMACAlias(configurableProvider, "SHA1", IANAObjectIdentifiers.hmacSHA1);
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA", Mappings.PREFIX + "$SHA1Mac");
            configurableProvider.addAlgorithm("Mac.PBEWITHHMACSHA1", Mappings.PREFIX + "$SHA1Mac");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHHMACSHA", "PBEWITHHMACSHA1");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA1");
            configurableProvider.addAlgorithm("Alg.Alias.Mac." + OIWObjectIdentifiers.idSHA1, "PBEWITHHMACSHA");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBEWITHHMACSHA1", Mappings.PREFIX + "$PBEWithMacKeyFactory");
        }
        
        static {
            PREFIX = SHA1.class.getName();
        }
    }
    
    public static class PBEWithMacKeyFactory extends PBESecretKeyFactory
    {
        public PBEWithMacKeyFactory() {
            super("PBEwithHmacSHA", null, false, 2, 1, 160, 0);
        }
    }
    
    public static class SHA1Mac extends BaseMac
    {
        public SHA1Mac() {
            super(new HMac(new SHA1Digest()));
        }
    }
}
