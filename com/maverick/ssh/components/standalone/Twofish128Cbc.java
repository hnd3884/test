package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class Twofish128Cbc extends b
{
    public static final String TWOFISH128_CBC = "twofish128-cbc";
    
    public Twofish128Cbc() {
        super(128, new c(), "twofish128-cbc");
    }
}
