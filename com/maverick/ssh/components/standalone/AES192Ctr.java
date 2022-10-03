package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES192Ctr extends d
{
    public static final String AES192_CTR = "aes192-ctr";
    
    public AES192Ctr() {
        super(192, new e(), "aes192-ctr");
    }
}
