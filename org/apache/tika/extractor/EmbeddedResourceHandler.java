package org.apache.tika.extractor;

import java.io.InputStream;
import org.apache.tika.mime.MediaType;

public interface EmbeddedResourceHandler
{
    void handle(final String p0, final MediaType p1, final InputStream p2);
}
