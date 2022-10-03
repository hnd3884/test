package org.apache.tika.metadata.filter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import java.io.IOException;
import org.apache.tika.exception.TikaConfigException;
import org.w3c.dom.Element;
import java.io.Serializable;
import org.apache.tika.config.ConfigBase;

public abstract class MetadataFilter extends ConfigBase implements Serializable
{
    public static MetadataFilter load(final Element root, final boolean allowMissing) throws TikaConfigException, IOException {
        try {
            return ConfigBase.buildComposite("metadataFilters", CompositeMetadataFilter.class, "metadataFilter", MetadataFilter.class, root);
        }
        catch (final TikaConfigException e) {
            if (allowMissing && e.getMessage().contains("could not find metadataFilters")) {
                return new NoOpFilter();
            }
            throw e;
        }
    }
    
    public abstract void filter(final Metadata p0) throws TikaException;
}
