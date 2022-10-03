package org.apache.tika.embedder;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.parser.ParseContext;
import java.io.Serializable;

public interface Embedder extends Serializable
{
    Set<MediaType> getSupportedEmbedTypes(final ParseContext p0);
    
    void embed(final Metadata p0, final InputStream p1, final OutputStream p2, final ParseContext p3) throws IOException, TikaException;
}
