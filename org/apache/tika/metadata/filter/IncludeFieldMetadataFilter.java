package org.apache.tika.metadata.filter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.config.Field;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class IncludeFieldMetadataFilter extends MetadataFilter
{
    private final Set<String> includeSet;
    
    public IncludeFieldMetadataFilter() {
        this(new HashSet<String>());
    }
    
    public IncludeFieldMetadataFilter(final Set<String> fields) {
        this.includeSet = fields;
    }
    
    @Field
    public void setInclude(final List<String> include) {
        this.includeSet.addAll(include);
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        for (final String n : metadata.names()) {
            if (!this.includeSet.contains(n)) {
                metadata.remove(n);
            }
        }
    }
}
