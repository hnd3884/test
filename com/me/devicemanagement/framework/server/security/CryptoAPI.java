package com.me.devicemanagement.framework.server.security;

public interface CryptoAPI
{
    String encrypt(final String p0);
    
    String decrypt(final String p0);
    
    String encrypt(final String p0, final Integer p1);
    
    String decrypt(final String p0, final Integer p1);
    
    String encrypt(final String p0, final String p1, final String p2);
    
    String decrypt(final String p0, final String p1, final String p2);
}
