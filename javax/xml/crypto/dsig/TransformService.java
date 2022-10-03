package javax.xml.crypto.dsig;

import java.security.spec.AlgorithmParameterSpec;
import java.io.OutputStream;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.util.Map;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public abstract class TransformService implements Transform
{
    private String algorithm;
    private String mechanism;
    private Provider provider;
    
    protected TransformService() {
    }
    
    public static TransformService getInstance(final String s, final String s2) throws NoSuchAlgorithmException {
        if (s2 == null || s == null) {
            throw new NullPointerException();
        }
        return findInstance(s, s2, null);
    }
    
    public static TransformService getInstance(final String s, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        if (s2 == null || s == null || provider == null) {
            throw new NullPointerException();
        }
        return findInstance(s, s2, provider);
    }
    
    public static TransformService getInstance(final String s, final String s2, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (s2 == null || s == null || s3 == null) {
            throw new NullPointerException();
        }
        final Provider provider = Security.getProvider(s3);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider named " + s3);
        }
        return findInstance(s, s2, provider);
    }
    
    private static TransformService findInstance(final String algorithm, final String mechanism, final Provider provider) throws NoSuchAlgorithmException {
        final Object[] array = XMLDSigSecurity.getImpl(algorithm, new MechanismMapEntry(algorithm, mechanism), "TransformService", provider);
        final TransformService transformService = (TransformService)array[0];
        transformService.mechanism = mechanism;
        transformService.algorithm = algorithm;
        transformService.provider = (Provider)array[1];
        return transformService;
    }
    
    public final String getMechanismType() {
        return this.mechanism;
    }
    
    public final String getAlgorithm() {
        return this.algorithm;
    }
    
    public final Provider getProvider() {
        return this.provider;
    }
    
    public abstract void init(final TransformParameterSpec p0) throws InvalidAlgorithmParameterException;
    
    public abstract void marshalParams(final XMLStructure p0, final XMLCryptoContext p1) throws MarshalException;
    
    public abstract void init(final XMLStructure p0, final XMLCryptoContext p1) throws InvalidAlgorithmParameterException;
    
    private static class MechanismMapEntry implements Map.Entry
    {
        private final String mechanism;
        private final String key;
        
        MechanismMapEntry(final String s, final String mechanism) {
            this.mechanism = mechanism;
            this.key = "TransformService." + s + " MechanismType";
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            if (this.getKey() == null) {
                if (entry.getKey() != null) {
                    return false;
                }
            }
            else if (!this.getKey().equals(entry.getKey())) {
                return false;
            }
            if ((this.getValue() != null) ? this.getValue().equals(entry.getValue()) : (entry.getValue() == null)) {
                return true;
            }
            return false;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.mechanism;
        }
        
        public Object setValue(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
    }
}
