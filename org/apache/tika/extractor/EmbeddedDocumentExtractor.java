package org.apache.tika.extractor;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;

public interface EmbeddedDocumentExtractor
{
    boolean shouldParseEmbedded(final Metadata p0);
    
    void parseEmbedded(final InputStream p0, final ContentHandler p1, final Metadata p2, final boolean p3) throws SAXException, IOException;
}
