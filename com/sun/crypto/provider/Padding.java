package com.sun.crypto.provider;

import javax.crypto.ShortBufferException;

interface Padding
{
    void padWithLen(final byte[] p0, final int p1, final int p2) throws ShortBufferException;
    
    int unpad(final byte[] p0, final int p1, final int p2);
    
    int padLength(final int p0);
}
