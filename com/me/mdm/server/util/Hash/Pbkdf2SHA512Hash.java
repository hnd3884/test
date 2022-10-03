package com.me.mdm.server.util.Hash;

import java.util.logging.Level;
import com.dd.plist.Base64;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.logging.Logger;

public class Pbkdf2SHA512Hash implements PasswordHash
{
    private static final int KEY_LENGTH = 128;
    public static Logger logger;
    
    @Override
    public String getDigest(final String password, final byte[] salt, final int iterations) {
        try {
            final char[] chars = password.toCharArray();
            final PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 1024);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            final byte[] hash = skf.generateSecret(spec).getEncoded();
            return String.valueOf(iterations) + ":" + Base64.encodeBytes(salt) + ":" + Base64.encodeBytes(hash);
        }
        catch (final Exception e) {
            Pbkdf2SHA512Hash.logger.log(Level.SEVERE, "Password Hashing failed!!", e);
            return null;
        }
    }
    
    @Override
    public Boolean verify(final String password, final String hashKey) {
        try {
            final String[] hashComponents = hashKey.split(":");
            return hashComponents[2] == this.getDigest(password, Base64.decode(hashComponents[1]), Integer.parseInt(hashComponents[0]));
        }
        catch (final Exception e) {
            Pbkdf2SHA512Hash.logger.log(Level.SEVERE, "Unablbe to verify password. Wrong hash format!!", e);
            return Boolean.FALSE;
        }
    }
    
    static {
        Pbkdf2SHA512Hash.logger = Logger.getLogger("MDMConfigLogger");
    }
}
