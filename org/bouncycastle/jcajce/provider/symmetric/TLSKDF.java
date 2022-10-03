package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.spec.TLSKeyMaterialSpec;

public class TLSKDF
{
    private static byte[] PRF_legacy(final TLSKeyMaterialSpec tlsKeyMaterialSpec) {
        final HMac hMac = new HMac(DigestFactory.createMD5());
        final HMac hMac2 = new HMac(DigestFactory.createSHA1());
        final byte[] concatenate = Arrays.concatenate(Strings.toByteArray(tlsKeyMaterialSpec.getLabel()), tlsKeyMaterialSpec.getSeed());
        final byte[] secret = tlsKeyMaterialSpec.getSecret();
        final int n = (secret.length + 1) / 2;
        final byte[] array = new byte[n];
        final byte[] array2 = new byte[n];
        System.arraycopy(secret, 0, array, 0, n);
        System.arraycopy(secret, secret.length - n, array2, 0, n);
        final int length = tlsKeyMaterialSpec.getLength();
        final byte[] array3 = new byte[length];
        final byte[] array4 = new byte[length];
        hmac_hash(hMac, array, concatenate, array3);
        hmac_hash(hMac2, array2, concatenate, array4);
        for (int i = 0; i < length; ++i) {
            final byte[] array5 = array3;
            final int n2 = i;
            array5[n2] ^= array4[i];
        }
        return array3;
    }
    
    private static void hmac_hash(final Mac mac, final byte[] array, final byte[] array2, final byte[] array3) {
        mac.init(new KeyParameter(array));
        byte[] array4 = array2;
        final int macSize = mac.getMacSize();
        final int n = (array3.length + macSize - 1) / macSize;
        final byte[] array5 = new byte[mac.getMacSize()];
        final byte[] array6 = new byte[mac.getMacSize()];
        for (int i = 0; i < n; ++i) {
            mac.update(array4, 0, array4.length);
            mac.doFinal(array5, 0);
            array4 = array5;
            mac.update(array4, 0, array4.length);
            mac.update(array2, 0, array2.length);
            mac.doFinal(array6, 0);
            System.arraycopy(array6, 0, array3, macSize * i, Math.min(macSize, array3.length - macSize * i));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS10KDF", Mappings.PREFIX + "$TLS10");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS11KDF", Mappings.PREFIX + "$TLS11");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA256KDF", Mappings.PREFIX + "$TLS12withSHA256");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA384KDF", Mappings.PREFIX + "$TLS12withSHA384");
            configurableProvider.addAlgorithm("SecretKeyFactory.TLS12WITHSHA512KDF", Mappings.PREFIX + "$TLS12withSHA512");
        }
        
        static {
            PREFIX = TLSKDF.class.getName();
        }
    }
    
    public static final class TLS10 extends TLSKeyMaterialFactory
    {
        public TLS10() {
            super("TLS10KDF");
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(PRF_legacy((TLSKeyMaterialSpec)keySpec), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
    }
    
    public static class TLSKeyMaterialFactory extends BaseSecretKeyFactory
    {
        protected TLSKeyMaterialFactory(final String s) {
            super(s, null);
        }
    }
    
    public static final class TLS11 extends TLSKeyMaterialFactory
    {
        public TLS11() {
            super("TLS11KDF");
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(PRF_legacy((TLSKeyMaterialSpec)keySpec), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
    }
    
    public static class TLS12 extends TLSKeyMaterialFactory
    {
        private final Mac prf;
        
        protected TLS12(final String s, final Mac prf) {
            super(s);
            this.prf = prf;
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (keySpec instanceof TLSKeyMaterialSpec) {
                return new SecretKeySpec(this.PRF((TLSKeyMaterialSpec)keySpec, this.prf), this.algName);
            }
            throw new InvalidKeySpecException("Invalid KeySpec");
        }
        
        private byte[] PRF(final TLSKeyMaterialSpec tlsKeyMaterialSpec, final Mac mac) {
            final byte[] concatenate = Arrays.concatenate(Strings.toByteArray(tlsKeyMaterialSpec.getLabel()), tlsKeyMaterialSpec.getSeed());
            final byte[] secret = tlsKeyMaterialSpec.getSecret();
            final byte[] array = new byte[tlsKeyMaterialSpec.getLength()];
            hmac_hash(mac, secret, concatenate, array);
            return array;
        }
    }
    
    public static final class TLS12withSHA256 extends TLS12
    {
        public TLS12withSHA256() {
            super("TLS12withSHA256KDF", new HMac(new SHA256Digest()));
        }
    }
    
    public static final class TLS12withSHA384 extends TLS12
    {
        public TLS12withSHA384() {
            super("TLS12withSHA384KDF", new HMac(new SHA384Digest()));
        }
    }
    
    public static final class TLS12withSHA512 extends TLS12
    {
        public TLS12withSHA512() {
            super("TLS12withSHA512KDF", new HMac(new SHA512Digest()));
        }
    }
}
