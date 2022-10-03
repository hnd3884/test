package com.theorem.radius3.radutil;

import java.security.SecureRandom;

public final class RadRand extends SecureRandom
{
    public static final String Secure = "RADIUS Secure Version";
    
    public final byte nextByte() {
        return (byte)this.next(8);
    }
    
    public final int nextUnsignedByte() {
        return this.next(8) & 0xFF;
    }
    
    public final String getVersion() {
        return "RADIUS Secure Version";
    }
}
