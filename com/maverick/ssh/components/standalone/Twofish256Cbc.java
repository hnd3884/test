package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class Twofish256Cbc extends b
{
    public static final String TWOFISH256_CBC = "twofish256-cbc";
    public static final String TWOFISH_CBC = "twofish-cbc";
    
    public Twofish256Cbc() {
        super(256, new c(), "twofish256-cbc");
    }
}
