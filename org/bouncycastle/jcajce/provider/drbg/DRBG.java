package org.bouncycastle.jcajce.provider.drbg;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.concurrent.atomic.AtomicReference;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.security.Provider;
import java.security.SecureRandomSpi;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;

public class DRBG
{
    private static final String PREFIX;
    private static final String[][] initialEntropySourceNames;
    private static final Object[] initialEntropySourceAndSpi;
    
    private static final Object[] findSource() {
        int i = 0;
        while (i < DRBG.initialEntropySourceNames.length) {
            final String[] array = DRBG.initialEntropySourceNames[i];
            try {
                return new Object[] { Class.forName(array[0]).newInstance(), Class.forName(array[1]).newInstance() };
            }
            catch (final Throwable t) {
                ++i;
                continue;
            }
            break;
        }
        return null;
    }
    
    private static SecureRandom createInitialEntropySource() {
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            public Boolean run() {
                try {
                    return SecureRandom.class.getMethod("getInstanceStrong", (Class<?>[])new Class[0]) != null;
                }
                catch (final Exception ex) {
                    return false;
                }
            }
        })) {
            return AccessController.doPrivileged((PrivilegedAction<SecureRandom>)new PrivilegedAction<SecureRandom>() {
                public SecureRandom run() {
                    try {
                        return (SecureRandom)SecureRandom.class.getMethod("getInstanceStrong", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                    }
                    catch (final Exception ex) {
                        return createCoreSecureRandom();
                    }
                }
            });
        }
        return createCoreSecureRandom();
    }
    
    private static SecureRandom createCoreSecureRandom() {
        if (DRBG.initialEntropySourceAndSpi != null) {
            return new CoreSecureRandom();
        }
        return new SecureRandom();
    }
    
    private static EntropySourceProvider createEntropySource() {
        return AccessController.doPrivileged((PrivilegedAction<EntropySourceProvider>)new PrivilegedAction<EntropySourceProvider>() {
            final /* synthetic */ String val$sourceClass = System.getProperty("org.bouncycastle.drbg.entropysource");
            
            public EntropySourceProvider run() {
                try {
                    return ClassUtil.loadClass(DRBG.class, this.val$sourceClass).newInstance();
                }
                catch (final Exception ex) {
                    throw new IllegalStateException("entropy source " + this.val$sourceClass + " not created: " + ex.getMessage(), ex);
                }
            }
        });
    }
    
    private static SecureRandom createBaseRandom(final boolean b) {
        if (System.getProperty("org.bouncycastle.drbg.entropysource") != null) {
            final EntropySourceProvider entropySource = createEntropySource();
            final EntropySource value = entropySource.get(128);
            return new SP800SecureRandomBuilder(entropySource).setPersonalizationString(b ? generateDefaultPersonalizationString(value.getEntropy()) : generateNonceIVPersonalizationString(value.getEntropy())).buildHash(new SHA512Digest(), Arrays.concatenate(value.getEntropy(), value.getEntropy()), b);
        }
        final HybridSecureRandom hybridSecureRandom = new HybridSecureRandom();
        return new SP800SecureRandomBuilder(hybridSecureRandom, true).setPersonalizationString(b ? generateDefaultPersonalizationString(hybridSecureRandom.generateSeed(16)) : generateNonceIVPersonalizationString(hybridSecureRandom.generateSeed(16))).buildHash(new SHA512Digest(), hybridSecureRandom.generateSeed(32), b);
    }
    
    private static byte[] generateDefaultPersonalizationString(final byte[] array) {
        return Arrays.concatenate(Strings.toByteArray("Default"), array, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
    }
    
    private static byte[] generateNonceIVPersonalizationString(final byte[] array) {
        return Arrays.concatenate(Strings.toByteArray("Nonce"), array, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
    }
    
    static {
        PREFIX = DRBG.class.getName();
        initialEntropySourceNames = new String[][] { { "sun.security.provider.Sun", "sun.security.provider.SecureRandom" }, { "org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl" }, { "com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom" }, { "org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom" } };
        initialEntropySourceAndSpi = findSource();
    }
    
    private static class CoreSecureRandom extends SecureRandom
    {
        CoreSecureRandom() {
            super((SecureRandomSpi)DRBG.initialEntropySourceAndSpi[1], (Provider)DRBG.initialEntropySourceAndSpi[0]);
        }
    }
    
    public static class Default extends SecureRandomSpi
    {
        private static final SecureRandom random;
        
        @Override
        protected void engineSetSeed(final byte[] seed) {
            Default.random.setSeed(seed);
        }
        
        @Override
        protected void engineNextBytes(final byte[] array) {
            Default.random.nextBytes(array);
        }
        
        @Override
        protected byte[] engineGenerateSeed(final int n) {
            return Default.random.generateSeed(n);
        }
        
        static {
            random = createBaseRandom(true);
        }
    }
    
    private static class HybridSecureRandom extends SecureRandom
    {
        private final AtomicBoolean seedAvailable;
        private final AtomicInteger samples;
        private final SecureRandom baseRandom;
        private final SP800SecureRandom drbg;
        
        HybridSecureRandom() {
            super(null, null);
            this.seedAvailable = new AtomicBoolean(false);
            this.samples = new AtomicInteger(0);
            this.baseRandom = createInitialEntropySource();
            this.drbg = new SP800SecureRandomBuilder(new EntropySourceProvider() {
                public EntropySource get(final int n) {
                    return new SignallingEntropySource(n);
                }
            }).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC(new HMac(new SHA512Digest()), this.baseRandom.generateSeed(32), false);
        }
        
        @Override
        public void setSeed(final byte[] seed) {
            if (this.drbg != null) {
                this.drbg.setSeed(seed);
            }
        }
        
        @Override
        public void setSeed(final long seed) {
            if (this.drbg != null) {
                this.drbg.setSeed(seed);
            }
        }
        
        @Override
        public byte[] generateSeed(final int n) {
            final byte[] array = new byte[n];
            if (this.samples.getAndIncrement() > 20 && this.seedAvailable.getAndSet(false)) {
                this.samples.set(0);
                this.drbg.reseed(null);
            }
            this.drbg.nextBytes(array);
            return array;
        }
        
        private class SignallingEntropySource implements EntropySource
        {
            private final int byteLength;
            private final AtomicReference entropy;
            private final AtomicBoolean scheduled;
            
            SignallingEntropySource(final int n) {
                this.entropy = new AtomicReference();
                this.scheduled = new AtomicBoolean(false);
                this.byteLength = (n + 7) / 8;
            }
            
            public boolean isPredictionResistant() {
                return true;
            }
            
            public byte[] getEntropy() {
                byte[] generateSeed = this.entropy.getAndSet(null);
                if (generateSeed == null || generateSeed.length != this.byteLength) {
                    generateSeed = HybridSecureRandom.this.baseRandom.generateSeed(this.byteLength);
                }
                else {
                    this.scheduled.set(false);
                }
                if (!this.scheduled.getAndSet(true)) {
                    new Thread(new EntropyGatherer(this.byteLength)).start();
                }
                return generateSeed;
            }
            
            public int entropySize() {
                return this.byteLength * 8;
            }
            
            private class EntropyGatherer implements Runnable
            {
                private final int numBytes;
                
                EntropyGatherer(final int numBytes) {
                    this.numBytes = numBytes;
                }
                
                public void run() {
                    SignallingEntropySource.this.entropy.set(HybridSecureRandom.this.baseRandom.generateSeed(this.numBytes));
                    HybridSecureRandom.this.seedAvailable.set(true);
                }
            }
        }
    }
    
    public static class Mappings extends AsymmetricAlgorithmProvider
    {
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecureRandom.DEFAULT", DRBG.PREFIX + "$Default");
            configurableProvider.addAlgorithm("SecureRandom.NONCEANDIV", DRBG.PREFIX + "$NonceAndIV");
        }
    }
    
    public static class NonceAndIV extends SecureRandomSpi
    {
        private static final SecureRandom random;
        
        @Override
        protected void engineSetSeed(final byte[] seed) {
            NonceAndIV.random.setSeed(seed);
        }
        
        @Override
        protected void engineNextBytes(final byte[] array) {
            NonceAndIV.random.nextBytes(array);
        }
        
        @Override
        protected byte[] engineGenerateSeed(final int n) {
            return NonceAndIV.random.generateSeed(n);
        }
        
        static {
            random = createBaseRandom(false);
        }
    }
}
