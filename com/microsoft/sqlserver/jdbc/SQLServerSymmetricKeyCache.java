package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ConcurrentHashMap;

final class SQLServerSymmetricKeyCache
{
    static final Object lock;
    private final ConcurrentHashMap<String, SQLServerSymmetricKey> cache;
    private static final SQLServerSymmetricKeyCache instance;
    private static ScheduledExecutorService scheduler;
    private static final Logger aeLogger;
    
    private SQLServerSymmetricKeyCache() {
        this.cache = new ConcurrentHashMap<String, SQLServerSymmetricKey>();
    }
    
    static SQLServerSymmetricKeyCache getInstance() {
        return SQLServerSymmetricKeyCache.instance;
    }
    
    ConcurrentHashMap<String, SQLServerSymmetricKey> getCache() {
        return this.cache;
    }
    
    SQLServerSymmetricKey getKey(final EncryptionKeyInfo keyInfo, final SQLServerConnection connection) throws SQLServerException {
        SQLServerSymmetricKey encryptionKey = null;
        synchronized (SQLServerSymmetricKeyCache.lock) {
            final String serverName = connection.getTrustedServerNameAE();
            assert null != serverName : "serverName should not be null in getKey.";
            final StringBuilder keyLookupValuebuffer = new StringBuilder(serverName);
            keyLookupValuebuffer.append(":");
            keyLookupValuebuffer.append(Base64.getEncoder().encodeToString(new String(keyInfo.encryptedKey, StandardCharsets.UTF_8).getBytes()));
            keyLookupValuebuffer.append(":");
            keyLookupValuebuffer.append(keyInfo.keyStoreName);
            final String keyLookupValue = keyLookupValuebuffer.toString();
            keyLookupValuebuffer.setLength(0);
            if (SQLServerSymmetricKeyCache.aeLogger.isLoggable(Level.FINE)) {
                SQLServerSymmetricKeyCache.aeLogger.fine("Checking trusted master key path...");
            }
            final Boolean[] hasEntry = { null };
            final List<String> trustedKeyPaths = SQLServerConnection.getColumnEncryptionTrustedMasterKeyPaths(serverName, hasEntry);
            if (hasEntry[0] && (null == trustedKeyPaths || 0 == trustedKeyPaths.size() || !trustedKeyPaths.contains(keyInfo.keyPath))) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UntrustedKeyPath"));
                final Object[] msgArgs = { keyInfo.keyPath, serverName };
                throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
            }
            if (SQLServerSymmetricKeyCache.aeLogger.isLoggable(Level.FINE)) {
                SQLServerSymmetricKeyCache.aeLogger.fine("Checking Symmetric key cache...");
            }
            if (!this.cache.containsKey(keyLookupValue)) {
                final byte[] plaintextKey = connection.getColumnEncryptionKeyStoreProvider(keyInfo.keyStoreName).decryptColumnEncryptionKey(keyInfo.keyPath, keyInfo.algorithmName, keyInfo.encryptedKey);
                encryptionKey = new SQLServerSymmetricKey(plaintextKey);
                final long columnEncryptionKeyCacheTtl = SQLServerConnection.getColumnEncryptionKeyCacheTtl();
                if (0L != columnEncryptionKeyCacheTtl) {
                    this.cache.putIfAbsent(keyLookupValue, encryptionKey);
                    if (SQLServerSymmetricKeyCache.aeLogger.isLoggable(Level.FINE)) {
                        SQLServerSymmetricKeyCache.aeLogger.fine("Adding encryption key to cache...");
                    }
                    SQLServerSymmetricKeyCache.scheduler.schedule(new CacheClear(keyLookupValue), columnEncryptionKeyCacheTtl, TimeUnit.SECONDS);
                }
            }
            else {
                encryptionKey = this.cache.get(keyLookupValue);
            }
        }
        return encryptionKey;
    }
    
    static {
        lock = new Object();
        instance = new SQLServerSymmetricKeyCache();
        SQLServerSymmetricKeyCache.scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerSymmetricKeyCache");
    }
}
