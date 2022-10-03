package com.maverick.crypto.digests;

public class DigestFactory
{
    static DigestProvider b;
    
    public static void setProvider(final DigestProvider b) {
        DigestFactory.b = b;
    }
    
    public static Digest createDigest(final String s) {
        if (DigestFactory.b != null) {
            return DigestFactory.b.createDigest(s);
        }
        if (s.equals("MD5")) {
            return new MD5Digest();
        }
        return new SHA1Digest();
    }
    
    static {
        DigestFactory.b = null;
    }
}
