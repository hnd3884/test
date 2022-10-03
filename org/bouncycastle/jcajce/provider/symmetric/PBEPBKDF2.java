package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.crypto.PasswordConverter;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.util.HashMap;
import java.util.Map;

public class PBEPBKDF2
{
    private static final Map prfCodes;
    
    private PBEPBKDF2() {
    }
    
    static {
        (prfCodes = new HashMap()).put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(6));
        PBEPBKDF2.prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(1));
        PBEPBKDF2.prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(4));
        PBEPBKDF2.prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(7));
        PBEPBKDF2.prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(8));
        PBEPBKDF2.prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(9));
        PBEPBKDF2.prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(11));
        PBEPBKDF2.prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(10));
        PBEPBKDF2.prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(12));
        PBEPBKDF2.prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(13));
    }
    
    public static class AlgParams extends BaseAlgorithmParameters
    {
        PBKDF2Params params;
        
        @Override
        protected byte[] engineGetEncoded() {
            try {
                return this.params.getEncoded("DER");
            }
            catch (final IOException ex) {
                throw new RuntimeException("Oooops! " + ex.toString());
            }
        }
        
        @Override
        protected byte[] engineGetEncoded(final String s) {
            if (this.isASN1FormatString(s)) {
                return this.engineGetEncoded();
            }
            return null;
        }
        
        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
            if (clazz == PBEParameterSpec.class) {
                return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF2 PBE parameters object.");
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
            if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF2 PBE parameters algorithm parameters object");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            this.params = new PBKDF2Params(pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
        }
        
        @Override
        protected void engineInit(final byte[] array) throws IOException {
            this.params = PBKDF2Params.getInstance(ASN1Primitive.fromByteArray(array));
        }
        
        @Override
        protected void engineInit(final byte[] array, final String s) throws IOException {
            if (this.isASN1FormatString(s)) {
                this.engineInit(array);
                return;
            }
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
        }
        
        @Override
        protected String engineToString() {
            return "PBKDF2 Parameters";
        }
    }
    
    public static class BasePBKDF2 extends BaseSecretKeyFactory
    {
        private int scheme;
        private int defaultDigest;
        
        public BasePBKDF2(final String s, final int n) {
            this(s, n, 1);
        }
        
        public BasePBKDF2(final String s, final int scheme, final int defaultDigest) {
            super(s, PKCSObjectIdentifiers.id_PBKDF2);
            this.scheme = scheme;
            this.defaultDigest = defaultDigest;
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (!(keySpec instanceof PBEKeySpec)) {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
            final PBEKeySpec pbeKeySpec = (PBEKeySpec)keySpec;
            if (pbeKeySpec.getSalt() == null) {
                return new PBKDF2Key(((PBEKeySpec)keySpec).getPassword(), (this.scheme == 1) ? PasswordConverter.ASCII : PasswordConverter.UTF8);
            }
            if (pbeKeySpec.getIterationCount() <= 0) {
                throw new InvalidKeySpecException("positive iteration count required: " + pbeKeySpec.getIterationCount());
            }
            if (pbeKeySpec.getKeyLength() <= 0) {
                throw new InvalidKeySpecException("positive key length required: " + pbeKeySpec.getKeyLength());
            }
            if (pbeKeySpec.getPassword().length == 0) {
                throw new IllegalArgumentException("password empty");
            }
            if (pbeKeySpec instanceof PBKDF2KeySpec) {
                final int digestCode = this.getDigestCode(((PBKDF2KeySpec)pbeKeySpec).getPrf().getAlgorithm());
                final int keyLength = pbeKeySpec.getKeyLength();
                return new BCPBEKey(this.algName, this.algOid, this.scheme, digestCode, keyLength, -1, pbeKeySpec, PBE.Util.makePBEMacParameters(pbeKeySpec, this.scheme, digestCode, keyLength));
            }
            final int defaultDigest = this.defaultDigest;
            final int keyLength2 = pbeKeySpec.getKeyLength();
            return new BCPBEKey(this.algName, this.algOid, this.scheme, defaultDigest, keyLength2, -1, pbeKeySpec, PBE.Util.makePBEMacParameters(pbeKeySpec, this.scheme, defaultDigest, keyLength2));
        }
        
        private int getDigestCode(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws InvalidKeySpecException {
            final Integer n = PBEPBKDF2.prfCodes.get(asn1ObjectIdentifier);
            if (n != null) {
                return n;
            }
            throw new InvalidKeySpecException("Invalid KeySpec: unknown PRF algorithm " + asn1ObjectIdentifier);
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("AlgorithmParameters.PBKDF2", Mappings.PREFIX + "$AlgParams");
            configurableProvider.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2", Mappings.PREFIX + "$PBKDF2withUTF8");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1", "PBKDF2");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1ANDUTF8", "PBKDF2");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHASCII", Mappings.PREFIX + "$PBKDF2with8BIT");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITH8BIT", "PBKDF2WITHASCII");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1AND8BIT", "PBKDF2WITHASCII");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA224", Mappings.PREFIX + "$PBKDF2withSHA224");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA256", Mappings.PREFIX + "$PBKDF2withSHA256");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA384", Mappings.PREFIX + "$PBKDF2withSHA384");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512", Mappings.PREFIX + "$PBKDF2withSHA512");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-224", Mappings.PREFIX + "$PBKDF2withSHA3_224");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-256", Mappings.PREFIX + "$PBKDF2withSHA3_256");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-384", Mappings.PREFIX + "$PBKDF2withSHA3_384");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-512", Mappings.PREFIX + "$PBKDF2withSHA3_512");
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACGOST3411", Mappings.PREFIX + "$PBKDF2withGOST3411");
        }
        
        static {
            PREFIX = PBEPBKDF2.class.getName();
        }
    }
    
    public static class PBKDF2with8BIT extends BasePBKDF2
    {
        public PBKDF2with8BIT() {
            super("PBKDF2", 1);
        }
    }
    
    public static class PBKDF2withGOST3411 extends BasePBKDF2
    {
        public PBKDF2withGOST3411() {
            super("PBKDF2", 5, 6);
        }
    }
    
    public static class PBKDF2withSHA224 extends BasePBKDF2
    {
        public PBKDF2withSHA224() {
            super("PBKDF2", 5, 7);
        }
    }
    
    public static class PBKDF2withSHA256 extends BasePBKDF2
    {
        public PBKDF2withSHA256() {
            super("PBKDF2", 5, 4);
        }
    }
    
    public static class PBKDF2withSHA384 extends BasePBKDF2
    {
        public PBKDF2withSHA384() {
            super("PBKDF2", 5, 8);
        }
    }
    
    public static class PBKDF2withSHA3_224 extends BasePBKDF2
    {
        public PBKDF2withSHA3_224() {
            super("PBKDF2", 5, 10);
        }
    }
    
    public static class PBKDF2withSHA3_256 extends BasePBKDF2
    {
        public PBKDF2withSHA3_256() {
            super("PBKDF2", 5, 11);
        }
    }
    
    public static class PBKDF2withSHA3_384 extends BasePBKDF2
    {
        public PBKDF2withSHA3_384() {
            super("PBKDF2", 5, 12);
        }
    }
    
    public static class PBKDF2withSHA3_512 extends BasePBKDF2
    {
        public PBKDF2withSHA3_512() {
            super("PBKDF2", 5, 13);
        }
    }
    
    public static class PBKDF2withSHA512 extends BasePBKDF2
    {
        public PBKDF2withSHA512() {
            super("PBKDF2", 5, 9);
        }
    }
    
    public static class PBKDF2withUTF8 extends BasePBKDF2
    {
        public PBKDF2withUTF8() {
            super("PBKDF2", 5);
        }
    }
}
