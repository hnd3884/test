package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import java.io.Serializable;

public interface Parser extends Serializable
{
    Set<MediaType> getSupportedTypes(final ParseContext p0);
    
    void parse(final InputStream p0, final ContentHandler p1, final Metadata p2, final ParseContext p3) throws IOException, SAXException, TikaException;
}
