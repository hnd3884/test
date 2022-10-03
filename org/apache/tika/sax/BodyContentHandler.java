package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Writer;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.XPathParser;

public class BodyContentHandler extends ContentHandlerDecorator
{
    private static final XPathParser PARSER;
    private static final Matcher MATCHER;
    
    public BodyContentHandler(final ContentHandler handler) {
        super(new MatchingContentHandler(handler, BodyContentHandler.MATCHER));
    }
    
    public BodyContentHandler(final Writer writer) {
        this(new WriteOutContentHandler(writer));
    }
    
    public BodyContentHandler(final OutputStream stream) {
        this(new WriteOutContentHandler(stream));
    }
    
    public BodyContentHandler(final int writeLimit) {
        this(new WriteOutContentHandler(writeLimit));
    }
    
    public BodyContentHandler() {
        this(new WriteOutContentHandler());
    }
    
    static {
        PARSER = new XPathParser("xhtml", "http://www.w3.org/1999/xhtml");
        MATCHER = BodyContentHandler.PARSER.parse("/xhtml:html/xhtml:body/descendant::node()");
    }
}
