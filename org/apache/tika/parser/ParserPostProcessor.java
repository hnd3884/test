package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.tika.utils.RegexUtils;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.InputStream;

public class ParserPostProcessor extends ParserDecorator
{
    public ParserPostProcessor(final Parser parser) {
        super(parser);
    }
    
    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final ContentHandler body = new BodyContentHandler();
        final ContentHandler tee = new TeeContentHandler(new ContentHandler[] { handler, body });
        super.parse(stream, tee, metadata, context);
        final String content = body.toString();
        metadata.set("fulltext", content);
        final int length = Math.min(content.length(), 500);
        metadata.set("summary", content.substring(0, length));
        for (final String link : RegexUtils.extractLinks(content)) {
            metadata.add("outlinks", link);
        }
    }
}
