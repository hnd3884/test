package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import org.apache.tika.exception.TikaException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;

public class EmptyFetcher implements Fetcher
{
    @Override
    public String getName() {
        return "empty";
    }
    
    @Override
    public InputStream fetch(final String fetchKey, final Metadata metadata) throws TikaException, IOException {
        return null;
    }
}
