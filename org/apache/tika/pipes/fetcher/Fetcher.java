package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import org.apache.tika.exception.TikaException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;

public interface Fetcher
{
    String getName();
    
    InputStream fetch(final String p0, final Metadata p1) throws TikaException, IOException;
}
