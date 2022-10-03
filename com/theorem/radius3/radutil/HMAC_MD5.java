package com.theorem.radius3.radutil;

import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

public class HMAC_MD5
{
    public static final int DIGEST_LENGTH = 16;
    
    public static Mac get() {
        try {
            return Mac.getInstance("HmacMD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            System.err.println("Unable to find the HMAC-MD5 " + ex.getMessage());
            return null;
        }
    }
    
    public static Mac get(final Provider provider) {
        try {
            return Mac.getInstance("HmacMD5", provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            return null;
        }
    }
}
