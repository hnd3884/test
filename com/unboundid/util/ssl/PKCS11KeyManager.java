package com.unboundid.util.ssl;

import javax.net.ssl.KeyManagerFactory;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import java.security.KeyStoreException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PKCS11KeyManager extends WrapperKeyManager
{
    private static final String PKCS11_KEY_STORE_TYPE = "PKCS11";
    
    public PKCS11KeyManager(final char[] keyStorePIN, final String certificateAlias) throws KeyStoreException {
        super(getKeyManagers(keyStorePIN), certificateAlias);
    }
    
    private static KeyManager[] getKeyManagers(final char[] keyStorePIN) throws KeyStoreException {
        final KeyStore ks = KeyStore.getInstance("PKCS11");
        try {
            ks.load(null, keyStorePIN);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new KeyStoreException(SSLMessages.ERR_PKCS11_CANNOT_ACCESS.get(StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            final KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(ks, keyStorePIN);
            return factory.getKeyManagers();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new KeyStoreException(SSLMessages.ERR_PKCS11_CANNOT_GET_KEY_MANAGERS.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
}
