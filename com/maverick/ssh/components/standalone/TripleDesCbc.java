package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;
import com.maverick.crypto.engines.DESedeEngine;

public class TripleDesCbc extends b
{
    public static final String TRIPLEDES_CBC = "3des-cbc";
    
    public TripleDesCbc() {
        super(192, new DESedeEngine(), "3des-cbc");
    }
    
    public String getAlgorithm() {
        return "3des-cbc";
    }
}
