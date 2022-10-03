package org.apache.tika.sax;

import org.xml.sax.SAXException;
import org.apache.tika.exception.WriteLimitReachedException;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.Writer;
import org.xml.sax.ContentHandler;

public class WriteOutContentHandler extends ContentHandlerDecorator
{
    private final int writeLimit;
    private int writeCount;
    
    public WriteOutContentHandler(final ContentHandler handler, final int writeLimit) {
        super(handler);
        this.writeCount = 0;
        this.writeLimit = writeLimit;
    }
    
    public WriteOutContentHandler(final Writer writer, final int writeLimit) {
        this(new ToTextContentHandler(writer), writeLimit);
    }
    
    public WriteOutContentHandler(final Writer writer) {
        this(writer, -1);
    }
    
    @Deprecated
    public WriteOutContentHandler(final OutputStream stream) {
        this(new OutputStreamWriter(stream, Charset.defaultCharset()));
    }
    
    public WriteOutContentHandler(final int writeLimit) {
        this(new StringWriter(), writeLimit);
    }
    
    public WriteOutContentHandler() {
        this(100000);
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.characters(ch, start, length);
            this.writeCount += length;
            return;
        }
        super.characters(ch, start, this.writeLimit - this.writeCount);
        this.writeCount = this.writeLimit;
        throw new WriteLimitReachedException(this.writeLimit);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.writeLimit == -1 || this.writeCount + length <= this.writeLimit) {
            super.ignorableWhitespace(ch, start, length);
            this.writeCount += length;
            return;
        }
        super.ignorableWhitespace(ch, start, this.writeLimit - this.writeCount);
        this.writeCount = this.writeLimit;
        throw new WriteLimitReachedException(this.writeLimit);
    }
}
