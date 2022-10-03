package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES256Cbc extends b
{
    public static final String AES256_CBC = "aes256-cbc";
    
    public AES256Cbc() {
        super(256, new e(), "aes256-cbc");
    }
}
