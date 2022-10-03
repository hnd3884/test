package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;

public class CAST128Cbc extends b
{
    public static final String CAST128_CBC = "cast128-cbc";
    
    public CAST128Cbc() {
        super(128, new CAST5Engine(), "cast128-cbc");
    }
}
