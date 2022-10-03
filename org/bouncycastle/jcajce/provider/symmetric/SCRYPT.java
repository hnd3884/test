package org.bouncycastle.jcajce.provider.symmetric;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.PasswordConverter;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

public class SCRYPT
{
    private SCRYPT() {
    }
    
    public static class BasePBKDF2 extends BaseSecretKeyFactory
    {
        private int scheme;
        
        public BasePBKDF2(final String s, final int scheme) {
            super(s, MiscObjectIdentifiers.id_scrypt);
            this.scheme = scheme;
        }
        
        @Override
        protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
            if (!(keySpec instanceof ScryptKeySpec)) {
                throw new InvalidKeySpecException("Invalid KeySpec");
            }
            final ScryptKeySpec scryptKeySpec = (ScryptKeySpec)keySpec;
            if (scryptKeySpec.getSalt() == null) {
                throw new IllegalArgumentException("Salt S must be provided.");
            }
            if (scryptKeySpec.getCostParameter() <= 1) {
                throw new IllegalArgumentException("Cost parameter N must be > 1.");
            }
            if (scryptKeySpec.getKeyLength() <= 0) {
                throw new InvalidKeySpecException("positive key length required: " + scryptKeySpec.getKeyLength());
            }
            if (scryptKeySpec.getPassword().length == 0) {
                throw new IllegalArgumentException("password empty");
            }
            return new BCPBEKey(this.algName, scryptKeySpec, new KeyParameter(SCrypt.generate(PasswordConverter.UTF8.convert(scryptKeySpec.getPassword()), scryptKeySpec.getSalt(), scryptKeySpec.getCostParameter(), scryptKeySpec.getBlockSize(), scryptKeySpec.getParallelizationParameter(), scryptKeySpec.getKeyLength() / 8)));
        }
    }
    
    public static class Mappings extends AlgorithmProvider
    {
        private static final String PREFIX;
        
        @Override
        public void configure(final ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("SecretKeyFactory.SCRYPT", Mappings.PREFIX + "$ScryptWithUTF8");
            configurableProvider.addAlgorithm("SecretKeyFactory", MiscObjectIdentifiers.id_scrypt, Mappings.PREFIX + "$ScryptWithUTF8");
        }
        
        static {
            PREFIX = SCRYPT.class.getName();
        }
    }
    
    public static class ScryptWithUTF8 extends BasePBKDF2
    {
        public ScryptWithUTF8() {
            super("SCRYPT", 5);
        }
    }
}
