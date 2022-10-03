package com.maverick.ssh.components.standalone;

import com.maverick.crypto.engines.CipherEngine;
import com.maverick.crypto.engines.BlowfishEngine;

public class BlowfishCbc extends b
{
    public BlowfishCbc() {
        super(128, new BlowfishEngine(), "blowfish-cbc");
    }
    
    public String getAlgorithm() {
        return "blowfish-cbc";
    }
}
