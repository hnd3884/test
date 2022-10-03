package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class AES192Cbc extends b
{
    public static final String AES192_CBC = "aes192-cbc";
    
    public AES192Cbc() {
        super(192, new e(), "aes192-cbc");
    }
}
