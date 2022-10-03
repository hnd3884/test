package org.bouncycastle.jcajce.provider.symmetric;

import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class OpenSSLPBKDF
{
    private OpenSSLPBKDF() {
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecretKeyFactory.PBKDF-OPENSSL", Mappings.PREFIX + "$PBKDF");
        }
        
        static {
            PREFIX = OpenSSLPBKDF.class.getName();
        }
    }
    
    public static class PBKDF extends BaseSecretKeyFactory
    {
        public PBKDF() {
            super("PBKDF-OpenSSL", null);
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (!(keySpec instanceof PBEKeySpec)) {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
            final PBEKeySpec pbeKeySpec = (PBEKeySpec)keySpec;
            if (pbeKeySpec.getSalt() == null) {
                throw new InvalidKeySpecException("missing required salt");
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
            final OpenSSLPBEParametersGenerator openSSLPBEParametersGenerator = new OpenSSLPBEParametersGenerator();
            openSSLPBEParametersGenerator.init(Strings.toByteArray(pbeKeySpec.getPassword()), pbeKeySpec.getSalt());
            return new SecretKeySpec(((KeyParameter)openSSLPBEParametersGenerator.generateDerivedParameters(pbeKeySpec.getKeyLength())).getKey(), "OpenSSLPBKDF");
        }
    }
}
