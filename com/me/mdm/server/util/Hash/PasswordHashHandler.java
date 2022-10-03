package com.me.mdm.server.util.Hash;

import java.security.SecureRandom;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordHashHandler
{
    private Logger logger;
    public static PasswordHashHandler handler;
    
    public PasswordHashHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static PasswordHashHandler getInstance() {
        if (PasswordHashHandler.handler == null) {
            PasswordHashHandler.handler = new PasswordHashHandler();
        }
        return PasswordHashHandler.handler;
    }
    
    public PasswordHash getHashAlgorithm(final int algorithm) throws APIHTTPException {
        switch (algorithm) {
            case 1: {
                return new Pbkdf2SHA512Hash();
            }
            default: {
                this.logger.log(Level.SEVERE, "Invalid Hashing Algorithm!!");
                throw new APIHTTPException("COM0008", new Object[0]);
            }
        }
    }
    
    private byte[] getSalt(final int saltBytes) {
        final byte[] salt = new byte[saltBytes];
        final SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
    
    public byte[] getSaltForHashAlgorithm(final int hashAlgorithm) throws APIHTTPException {
        switch (hashAlgorithm) {
            case 1: {
                return this.getSalt(32);
            }
            default: {
                this.logger.log(Level.SEVERE, "Invalid Hashing Algorithm!!");
                throw new APIHTTPException("COM0008", new Object[0]);
            }
        }
    }
    
    public Integer getHashIterations(final int hashAlgorithm) throws APIHTTPException {
        switch (hashAlgorithm) {
            case 1: {
                return 38000;
            }
            default: {
                this.logger.log(Level.SEVERE, "Invalid Hashing Algorithm!!");
                throw new APIHTTPException("COM0008", new Object[0]);
            }
        }
    }
    
    static {
        PasswordHashHandler.handler = null;
    }
}
