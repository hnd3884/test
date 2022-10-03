package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.io.InputStream;

class TarArchiveSparseZeroInputStream extends InputStream
{
    @Override
    public int read() throws IOException {
        return 0;
    }
    
    @Override
    public long skip(final long n) {
        return n;
    }
}
