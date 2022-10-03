package com.me.mdm.server.util.Hash;

public interface PasswordHash
{
    String getDigest(final String p0, final byte[] p1, final int p2);
    
    Boolean verify(final String p0, final String p1);
}
