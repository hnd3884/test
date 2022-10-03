package com.zoho.tools.util;

import java.util.logging.Level;
import com.zoho.tools.AES256Util;
import java.util.logging.Logger;

public class CryptoHelper
{
    private static final Logger LOGGER;
    private static String encryptionKey;
    
    public static void initialize(final String encryptionKey) {
        CryptoHelper.encryptionKey = encryptionKey;
    }
    
    public static boolean isEncrypted(final String password) {
        checkInitialized();
        try {
            final String pass = AES256Util.decrypt(password, CryptoHelper.encryptionKey);
            return !pass.equals(password);
        }
        catch (final Exception e) {
            CryptoHelper.LOGGER.log(Level.WARNING, "Exception occurred while checking whether password is encrypted.", e);
            return false;
        }
    }
    
    public static boolean isInitialized() {
        return CryptoHelper.encryptionKey != null && !CryptoHelper.encryptionKey.isEmpty();
    }
    
    private static void checkInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("CryptoUtil is not initialized");
        }
    }
    
    public static String encrypt(final String password) {
        checkInitialized();
        if (password != null && !password.isEmpty()) {
            return AES256Util.encrypt(password, CryptoHelper.encryptionKey);
        }
        return password;
    }
    
    public static String decrypt(final String password) {
        checkInitialized();
        if (password != null && !password.isEmpty()) {
            return AES256Util.decrypt(password, CryptoHelper.encryptionKey);
        }
        return password;
    }
    
    static {
        LOGGER = Logger.getLogger(CryptoHelper.class.getName());
    }
}
