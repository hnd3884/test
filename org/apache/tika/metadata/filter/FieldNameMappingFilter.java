package org.apache.tika.metadata.filter;

import java.util.Iterator;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldNameMappingFilter extends MetadataFilter
{
    Map<String, String> mappings;
    boolean excludeUnmapped;
    
    public FieldNameMappingFilter() {
        this.mappings = new LinkedHashMap<String, String>();
        this.excludeUnmapped = true;
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        if (this.excludeUnmapped) {
            for (final String n : metadata.names()) {
                if (this.mappings.containsKey(n)) {
                    final String[] vals = metadata.getValues(n);
                    metadata.remove(n);
                    for (final String val : vals) {
                        metadata.add(this.mappings.get(n), val);
                    }
                }
                else {
                    metadata.remove(n);
                }
            }
        }
        else {
            for (final String n : metadata.names()) {
                if (this.mappings.containsKey(n)) {
                    final String[] vals = metadata.getValues(n);
                    metadata.remove(n);
                    for (final String val : vals) {
                        metadata.add(this.mappings.get(n), val);
                    }
                }
            }
        }
    }
    
    @Field
    public void setExcludeUnmapped(final boolean excludeUnmapped) {
        this.excludeUnmapped = excludeUnmapped;
    }
    
    @Field
    public void setMappings(final Map<String, String> mappings) {
        for (final Map.Entry<String, String> e : mappings.entrySet()) {
            this.mappings.put(e.getKey(), e.getValue());
        }
    }
}
