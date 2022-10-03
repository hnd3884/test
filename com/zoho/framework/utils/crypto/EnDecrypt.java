package com.zoho.framework.utils.crypto;

public interface EnDecrypt
{
    public static final int AES128 = 1;
    public static final int AES256 = 2;
    public static final int BLOWFISH = 3;
    
    String encrypt(final String p0);
    
    String decrypt(final String p0);
    
    String encrypt(final String p0, final String p1);
    
    String decrypt(final String p0, final String p1);
}
