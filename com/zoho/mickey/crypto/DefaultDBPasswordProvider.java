package com.zoho.mickey.crypto;

import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.mickey.exception.PasswordException;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultDBPasswordProvider implements DBPasswordProvider
{
    private static final Logger LOGGER;
    
    @Override
    public String getPassword(final Object context) throws PasswordException {
        String password = null;
        if (context == null) {
            return null;
        }
        int algo = 2;
        String cryptTag = null;
        if (context instanceof Properties) {
            final Properties pwProps = (Properties)context;
            password = pwProps.getProperty("password");
            if (pwProps.getProperty("algorithm") != null) {
                algo = Integer.parseInt(pwProps.getProperty("algorithm"));
            }
            if (pwProps.getProperty("CryptTag") != null) {
                cryptTag = pwProps.getProperty("CryptTag");
            }
        }
        else {
            if (!(context instanceof String)) {
                throw new PasswordException("Unknown context for getPassword()!! Only Database Properties (or) String can be given. ");
            }
            password = (String)context;
        }
        try {
            if (cryptTag != null) {
                return CryptoUtil.decrypt(password, algo, cryptTag);
            }
            return CryptoUtil.decrypt(password, algo);
        }
        catch (final Exception e) {
            DefaultDBPasswordProvider.LOGGER.info("Decryption Failed! " + e.getMessage());
            return password;
        }
    }
    
    @Override
    public String getEncryptedPassword(final String plainTextPassword) throws PasswordException {
        if (plainTextPassword != null && !plainTextPassword.trim().isEmpty()) {
            try {
                return CryptoUtil.encrypt(plainTextPassword, 2);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
        return "";
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultDBPasswordProvider.class.getName());
    }
}
