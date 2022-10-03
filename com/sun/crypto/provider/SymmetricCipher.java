package com.sun.crypto.provider;

import java.security.InvalidKeyException;

abstract class SymmetricCipher
{
    abstract int getBlockSize();
    
    abstract void init(final boolean p0, final String p1, final byte[] p2) throws InvalidKeyException;
    
    abstract void encryptBlock(final byte[] p0, final int p1, final byte[] p2, final int p3);
    
    abstract void decryptBlock(final byte[] p0, final int p1, final byte[] p2, final int p3);
}
