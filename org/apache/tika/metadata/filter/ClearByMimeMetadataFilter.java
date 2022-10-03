package org.apache.tika.metadata.filter;

import org.apache.tika.config.Field;
import java.util.Collection;
import java.util.List;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.metadata.Metadata;
import java.util.HashSet;
import java.util.Set;

public class ClearByMimeMetadataFilter extends MetadataFilter
{
    private final Set<String> mimes;
    
    public ClearByMimeMetadataFilter() {
        this(new HashSet<String>());
    }
    
    public ClearByMimeMetadataFilter(final Set<String> mimes) {
        this.mimes = mimes;
    }
    
    @Override
    public void filter(final Metadata metadata) throws TikaException {
        String mimeString = metadata.get("Content-Type");
        if (mimeString == null) {
            return;
        }
        final MediaType mt = MediaType.parse(mimeString);
        if (mt != null) {
            mimeString = mt.getBaseType().toString();
        }
        if (this.mimes.contains(mimeString)) {
            for (final String n : metadata.names()) {
                metadata.remove(n);
            }
        }
    }
    
    @Field
    public void setMimes(final List<String> mimes) {
        this.mimes.addAll(mimes);
    }
}
