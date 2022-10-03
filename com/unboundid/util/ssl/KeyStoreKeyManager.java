package com.unboundid.util.ssl;

import com.unboundid.util.StaticUtils;
import javax.net.ssl.KeyManagerFactory;
import com.unboundid.util.Debug;
import java.io.InputStream;
import java.io.FileInputStream;
import com.unboundid.util.Validator;
import javax.net.ssl.KeyManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.io.File;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class KeyStoreKeyManager extends WrapperKeyManager implements Serializable
{
    private static final long serialVersionUID = -5202641256733094253L;
    private final String keyStoreFile;
    private final String keyStoreFormat;
    
    public KeyStoreKeyManager(final File keyStoreFile, final char[] keyStorePIN) throws KeyStoreException {
        this(keyStoreFile.getAbsolutePath(), keyStorePIN, null, null);
    }
    
    public KeyStoreKeyManager(final String keyStoreFile, final char[] keyStorePIN) throws KeyStoreException {
        this(keyStoreFile, keyStorePIN, null, null);
    }
    
    public KeyStoreKeyManager(final File keyStoreFile, final char[] keyStorePIN, final String keyStoreFormat, final String certificateAlias) throws KeyStoreException {
        this(keyStoreFile.getAbsolutePath(), keyStorePIN, keyStoreFormat, certificateAlias);
    }
    
    public KeyStoreKeyManager(final String keyStoreFile, final char[] keyStorePIN, final String keyStoreFormat, final String certificateAlias) throws KeyStoreException {
        super(getKeyManagers(keyStoreFile, keyStorePIN, keyStoreFormat), certificateAlias);
        this.keyStoreFile = keyStoreFile;
        if (keyStoreFormat == null) {
            this.keyStoreFormat = KeyStore.getDefaultType();
        }
        else {
            this.keyStoreFormat = keyStoreFormat;
        }
    }
    
    private static KeyManager[] getKeyManagers(final String keyStoreFile, final char[] keyStorePIN, final String keyStoreFormat) throws KeyStoreException {
        Validator.ensureNotNull(keyStoreFile);
        String type = keyStoreFormat;
        if (type == null) {
            type = KeyStore.getDefaultType();
        }
        final File f = new File(keyStoreFile);
        if (!f.exists()) {
            throw new KeyStoreException(SSLMessages.ERR_KEYSTORE_NO_SUCH_FILE.get(keyStoreFile));
        }
        final KeyStore ks = KeyStore.getInstance(type);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            ks.load(inputStream, keyStorePIN);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new KeyStoreException(SSLMessages.ERR_KEYSTORE_CANNOT_LOAD.get(keyStoreFile, type, String.valueOf(e)), e);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
        }
        try {
            final KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(ks, keyStorePIN);
            return factory.getKeyManagers();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new KeyStoreException(SSLMessages.ERR_KEYSTORE_CANNOT_GET_KEY_MANAGERS.get(keyStoreFile, keyStoreFormat, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getKeyStoreFile() {
        return this.keyStoreFile;
    }
    
    public String getKeyStoreFormat() {
        return this.keyStoreFormat;
    }
}
