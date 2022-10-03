package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES128Ctr extends d
{
    public static final String AES128_CTR = "aes128-ctr";
    
    public AES128Ctr() {
        super(128, new e(), "aes128-ctr");
    }
}
