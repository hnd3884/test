package org.tukaani.xz;

import java.io.InputStream;

interface FilterDecoder extends FilterCoder
{
    int getMemoryUsage();
    
    InputStream getInputStream(final InputStream p0, final ArrayCache p1);
}
