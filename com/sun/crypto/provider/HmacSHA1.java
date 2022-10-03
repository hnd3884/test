package com.sun.crypto.provider;

import java.security.NoSuchAlgorithmException;

public final class HmacSHA1 extends HmacCore
{
    public HmacSHA1() throws NoSuchAlgorithmException {
        super("SHA1", 64);
    }
}
