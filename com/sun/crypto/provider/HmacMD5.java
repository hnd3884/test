package com.sun.crypto.provider;

import java.security.NoSuchAlgorithmException;

public final class HmacMD5 extends HmacCore
{
    public HmacMD5() throws NoSuchAlgorithmException {
        super("MD5", 64);
    }
}
