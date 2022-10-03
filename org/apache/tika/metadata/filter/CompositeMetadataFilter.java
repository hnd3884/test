package org.apache.tika.metadata.filter;

import org.apache.tika.exception.TikaException;
import java.util.Iterator;
import org.apache.tika.metadata.Metadata;
import java.util.List;

public class CompositeMetadataFilter extends MetadataFilter
{
    private final List<MetadataFilter> filters;
    
    public CompositeMetadataFilter(final List<MetadataFilter> filters) {
        this.filters = filters;
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        for (final MetadataFilter filter : this.filters) {
            filter.filter(metadata);
        }
    }
}
