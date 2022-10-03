package org.apache.tika.metadata.filter;

import org.apache.tika.config.Field;
import java.util.Collection;
import java.util.List;
import org.apache.tika.exception.TikaException;
import java.util.Iterator;
import org.apache.tika.metadata.Metadata;
import java.util.HashSet;
import java.util.Set;

public class ExcludeFieldMetadataFilter extends MetadataFilter
{
    private final Set<String> excludeSet;
    
    public ExcludeFieldMetadataFilter() {
        this(new HashSet<String>());
    }
    
    public ExcludeFieldMetadataFilter(final Set<String> exclude) {
        this.excludeSet = exclude;
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        for (final String field : this.excludeSet) {
            metadata.remove(field);
        }
    }
    
    @Field
    public void setExclude(final List<String> exclude) {
        this.excludeSet.addAll(exclude);
    }
}
