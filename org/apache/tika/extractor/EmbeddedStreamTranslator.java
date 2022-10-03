package org.apache.tika.extractor;

import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;

public interface EmbeddedStreamTranslator
{
    boolean shouldTranslate(final InputStream p0, final Metadata p1) throws IOException;
    
    InputStream translate(final InputStream p0, final Metadata p1) throws IOException;
}
