package org.apache.tika.metadata.filter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

public class NoOpFilter extends MetadataFilter
{
    public static NoOpFilter NOOP_FILTER;
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
    }
    
    static {
        NoOpFilter.NOOP_FILTER = new NoOpFilter();
    }
}
