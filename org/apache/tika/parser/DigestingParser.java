package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;

public class DigestingParser extends ParserDecorator
{
    private final Digester digester;
    
    public DigestingParser(final Parser parser, final Digester digester) {
        super(parser);
        this.digester = digester;
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final TemporaryResources tmp = new TemporaryResources();
        final TikaInputStream tis = TikaInputStream.get(stream, tmp);
        try {
            if (this.digester != null) {
                this.digester.digest((InputStream)tis, metadata, context);
            }
            super.parse((InputStream)tis, handler, metadata, context);
        }
        finally {
            tmp.dispose();
        }
    }
    
    public interface Encoder
    {
        String encode(final byte[] p0);
    }
    
    public interface Digester
    {
        void digest(final InputStream p0, final Metadata p1, final ParseContext p2) throws IOException;
    }
}
