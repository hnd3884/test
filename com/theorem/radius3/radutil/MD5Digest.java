package com.theorem.radius3.radutil;

import java.security.Provider;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class MD5Digest
{
    public static final int DIGEST_LENGTH = 16;
    
    public static MessageDigest get() {
        try {
            return MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            System.err.println("Unable to find the message digest algorithm MD5 " + ex.getMessage());
            return null;
        }
    }
    
    public static MessageDigest get(final Provider provider) {
        try {
            return MessageDigest.getInstance("MD5", provider);
        }
        catch (final NoSuchAlgorithmException ex) {
            return null;
        }
    }
}
