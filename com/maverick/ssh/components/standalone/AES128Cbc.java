package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES128Cbc extends b
{
    public static final String AES128_CBC = "aes128-cbc";
    
    public AES128Cbc() {
        super(128, new e(), "aes128-cbc");
    }
}
