package com.sun.crypto.provider;

import java.security.Security;
import javax.crypto.BadPaddingException;
import java.io.ObjectInputStream;
import java.io.InvalidClassException;
import java.security.AccessController;
import sun.misc.ObjectInputFilter;
import sun.misc.SharedSecrets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.AlgorithmParameters;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import javax.crypto.Cipher;
import java.io.Serializable;
import javax.crypto.SealedObject;

final class SealedObjectForKeyProtector extends SealedObject
{
    static final long serialVersionUID = -3650226485480866989L;
    private static final String KEY_SERIAL_FILTER = "jceks.key.serialFilter";
    
    SealedObjectForKeyProtector(final Serializable s, final Cipher cipher) throws IOException, IllegalBlockSizeException {
        super(s, cipher);
    }
    
    SealedObjectForKeyProtector(final SealedObject sealedObject) {
        super(sealedObject);
    }
    
    AlgorithmParameters getParameters() {
        AlgorithmParameters instance = null;
        if (super.encodedParams != null) {
            try {
                instance = AlgorithmParameters.getInstance("PBE", SunJCE.getInstance());
                instance.init(super.encodedParams);
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new RuntimeException("SunJCE provider is not configured properly");
            }
            catch (final IOException ex2) {
                throw new RuntimeException("Parameter failure: " + ex2.getMessage());
            }
        }
        return instance;
    }
    
    final Key getKey(final Cipher cipher, final int n) throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        try (final ObjectInputStream extObjectInputStream = SharedSecrets.getJavaxCryptoSealedObjectAccess().getExtObjectInputStream((SealedObject)this, cipher)) {
            AccessController.doPrivileged(() -> {
                ObjectInputFilter.Config.setObjectInputFilter(objectInputStream, (ObjectInputFilter)new DeserializationChecker(n2));
                return null;
            });
            try {
                return (Key)extObjectInputStream.readObject();
            }
            catch (final InvalidClassException ex) {
                if (ex.getMessage().contains("REJECTED")) {
                    throw new IOException("Rejected by the jceks.key.serialFilter or jdk.serialFilter property", ex);
                }
                throw ex;
            }
        }
    }
    
    private static class DeserializationChecker implements ObjectInputFilter
    {
        private static final ObjectInputFilter OWN_FILTER;
        private final int maxLength;
        
        private DeserializationChecker(final int maxLength) {
            this.maxLength = maxLength;
        }
        
        public ObjectInputFilter.Status checkInput(final ObjectInputFilter.FilterInfo filterInfo) {
            if (filterInfo.arrayLength() > this.maxLength) {
                return ObjectInputFilter.Status.REJECTED;
            }
            if (filterInfo.serialClass() == Object.class) {
                return ObjectInputFilter.Status.UNDECIDED;
            }
            if (DeserializationChecker.OWN_FILTER != null) {
                final ObjectInputFilter.Status checkInput = DeserializationChecker.OWN_FILTER.checkInput(filterInfo);
                if (checkInput != ObjectInputFilter.Status.UNDECIDED) {
                    return checkInput;
                }
            }
            final ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
            if (serialFilter != null) {
                return serialFilter.checkInput(filterInfo);
            }
            return ObjectInputFilter.Status.UNDECIDED;
        }
        
        static {
            final String s = AccessController.doPrivileged(() -> {
                System.getProperty("jceks.key.serialFilter");
                final String s2;
                if (s2 != null) {
                    return s2;
                }
                else {
                    return Security.getProperty("jceks.key.serialFilter");
                }
            });
            OWN_FILTER = ((s == null) ? null : ObjectInputFilter.Config.createFilter(s));
        }
    }
}
