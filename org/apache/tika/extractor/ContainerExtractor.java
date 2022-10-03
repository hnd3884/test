package org.apache.tika.extractor;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import java.io.Serializable;

public interface ContainerExtractor extends Serializable
{
    boolean isSupported(final TikaInputStream p0) throws IOException;
    
    void extract(final TikaInputStream p0, final ContainerExtractor p1, final EmbeddedResourceHandler p2) throws IOException, TikaException;
}
