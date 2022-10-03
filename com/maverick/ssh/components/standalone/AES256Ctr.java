package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES256Ctr extends d
{
    public static final String AES256_CTR = "aes256-ctr";
    
    public AES256Ctr() {
        super(256, new e(), "aes256-ctr");
    }
}
