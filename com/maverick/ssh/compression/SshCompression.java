package com.maverick.ssh.compression;

import java.io.IOException;

public interface SshCompression
{
    public static final int INFLATER = 0;
    public static final int DEFLATER = 1;
    
    void init(final int p0, final int p1);
    
    byte[] compress(final byte[] p0, final int p1, final int p2) throws IOException;
    
    byte[] uncompress(final byte[] p0, final int p1, final int p2) throws IOException;
    
    String getAlgorithm();
}
