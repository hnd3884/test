package org.bouncycastle.crypto.tls;

import java.io.IOException;

public interface TlsCipher
{
    int getPlaintextLimit(final int p0);
    
    byte[] encodePlaintext(final long p0, final short p1, final byte[] p2, final int p3, final int p4) throws IOException;
    
    byte[] decodeCiphertext(final long p0, final short p1, final byte[] p2, final int p3, final int p4) throws IOException;
}
