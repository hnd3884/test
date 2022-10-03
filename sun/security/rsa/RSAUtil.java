package sun.security.rsa;

import java.security.InvalidKeyException;
import sun.security.util.ObjectIdentifier;
import java.security.spec.InvalidParameterSpecException;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import sun.security.x509.AlgorithmId;
import java.security.spec.PSSParameterSpec;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class RSAUtil
{
    public static void checkParamsAgainstType(final KeyType keyType, final AlgorithmParameterSpec algorithmParameterSpec) throws ProviderException {
        switch (keyType) {
            case RSA: {
                if (algorithmParameterSpec != null) {
                    throw new ProviderException("null params expected for " + keyType.keyAlgo());
                }
                break;
            }
            case PSS: {
                if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof PSSParameterSpec)) {
                    throw new ProviderException("PSSParmeterSpec expected for " + keyType.keyAlgo());
                }
                break;
            }
            default: {
                throw new ProviderException("Unsupported RSA algorithm " + keyType);
            }
        }
    }
    
    public static AlgorithmId createAlgorithmId(final KeyType keyType, final AlgorithmParameterSpec algorithmParameterSpec) throws ProviderException {
        checkParamsAgainstType(keyType, algorithmParameterSpec);
        AlgorithmParameters instance = null;
        try {
            ObjectIdentifier objectIdentifier = null;
            switch (keyType) {
                case RSA: {
                    objectIdentifier = AlgorithmId.RSAEncryption_oid;
                    break;
                }
                case PSS: {
                    if (algorithmParameterSpec != null) {
                        instance = AlgorithmParameters.getInstance(keyType.keyAlgo());
                        instance.init(algorithmParameterSpec);
                    }
                    objectIdentifier = AlgorithmId.RSASSA_PSS_oid;
                    break;
                }
                default: {
                    throw new ProviderException("Unsupported RSA algorithm " + keyType);
                }
            }
            AlgorithmId algorithmId;
            if (instance == null) {
                algorithmId = new AlgorithmId(objectIdentifier);
            }
            else {
                algorithmId = new AlgorithmId(objectIdentifier, instance);
            }
            return algorithmId;
        }
        catch (final NoSuchAlgorithmException | InvalidParameterSpecException ex) {
            throw new ProviderException((Throwable)ex);
        }
    }
    
    public static AlgorithmParameterSpec getParamSpec(final AlgorithmId algorithmId) throws ProviderException {
        if (algorithmId == null) {
            throw new ProviderException("AlgorithmId should not be null");
        }
        return getParamSpec(algorithmId.getParameters());
    }
    
    public static AlgorithmParameterSpec getParamSpec(final AlgorithmParameters algorithmParameters) throws ProviderException {
        if (algorithmParameters == null) {
            return null;
        }
        try {
            final String algorithm = algorithmParameters.getAlgorithm();
            final KeyType lookup = KeyType.lookup(algorithm);
            switch (lookup) {
                case RSA: {
                    throw new ProviderException("No params accepted for " + lookup.keyAlgo());
                }
                case PSS: {
                    return algorithmParameters.getParameterSpec(PSSParameterSpec.class);
                }
                default: {
                    throw new ProviderException("Unsupported RSA algorithm: " + algorithm);
                }
            }
        }
        catch (final ProviderException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new ProviderException(ex2);
        }
    }
    
    public enum KeyType
    {
        RSA("RSA"), 
        PSS("RSASSA-PSS");
        
        private final String algo;
        
        private KeyType(final String algo) {
            this.algo = algo;
        }
        
        public String keyAlgo() {
            return this.algo;
        }
        
        public static KeyType lookup(final String s) throws InvalidKeyException, ProviderException {
            if (s == null) {
                throw new InvalidKeyException("Null key algorithm");
            }
            for (final KeyType keyType : values()) {
                if (keyType.keyAlgo().equalsIgnoreCase(s)) {
                    return keyType;
                }
            }
            throw new ProviderException("Unsupported algorithm " + s);
        }
    }
}
