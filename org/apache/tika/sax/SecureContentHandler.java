package org.apache.tika.sax;

import org.xml.sax.Attributes;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import java.util.LinkedList;
import org.apache.tika.io.TikaInputStream;

public class SecureContentHandler extends ContentHandlerDecorator
{
    private final TikaInputStream stream;
    private final LinkedList<Integer> packageEntryDepths;
    private long characterCount;
    private int currentDepth;
    private long threshold;
    private long ratio;
    private int maxDepth;
    private int maxPackageEntryDepth;
    
    public SecureContentHandler(final ContentHandler handler, final TikaInputStream stream) {
        super(handler);
        this.packageEntryDepths = new LinkedList<Integer>();
        this.characterCount = 0L;
        this.currentDepth = 0;
        this.threshold = 1000000L;
        this.ratio = 100L;
        this.maxDepth = 100;
        this.maxPackageEntryDepth = 10;
        this.stream = stream;
    }
    
    public long getOutputThreshold() {
        return this.threshold;
    }
    
    public void setOutputThreshold(final long threshold) {
        this.threshold = threshold;
    }
    
    public long getMaximumCompressionRatio() {
        return this.ratio;
    }
    
    public void setMaximumCompressionRatio(final long ratio) {
        this.ratio = ratio;
    }
    
    public int getMaximumDepth() {
        return this.maxDepth;
    }
    
    public void setMaximumDepth(final int depth) {
        this.maxDepth = depth;
    }
    
    public int getMaximumPackageEntryDepth() {
        return this.maxPackageEntryDepth;
    }
    
    public void setMaximumPackageEntryDepth(final int depth) {
        this.maxPackageEntryDepth = depth;
    }
    
    public void throwIfCauseOf(final SAXException e) throws TikaException {
        if (e instanceof SecureSAXException && ((SecureSAXException)e).isCausedBy(this)) {
            throw new TikaException("Zip bomb detected!", e);
        }
    }
    
    private long getByteCount() throws SAXException {
        try {
            if (this.stream.hasLength()) {
                return this.stream.getLength();
            }
            return this.stream.getPosition();
        }
        catch (final IOException e) {
            throw new SAXException("Unable to get stream length", e);
        }
    }
    
    protected void advance(final int length) throws SAXException {
        this.characterCount += length;
        final long byteCount = this.getByteCount();
        if (this.characterCount > this.threshold && this.characterCount > byteCount * this.ratio) {
            throw new SecureSAXException("Suspected zip bomb: " + byteCount + " input bytes produced " + this.characterCount + " output characters");
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts) throws SAXException {
        ++this.currentDepth;
        if (this.currentDepth >= this.maxDepth) {
            throw new SecureSAXException("Suspected zip bomb: " + this.currentDepth + " levels of XML element nesting");
        }
        if ("div".equals(name) && "package-entry".equals(atts.getValue("class"))) {
            this.packageEntryDepths.addLast(this.currentDepth);
            if (this.packageEntryDepths.size() >= this.maxPackageEntryDepth) {
                throw new SecureSAXException("Suspected zip bomb: " + this.packageEntryDepths.size() + " levels of package entry nesting");
            }
        }
        super.startElement(uri, localName, name, atts);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        super.endElement(uri, localName, name);
        if (!this.packageEntryDepths.isEmpty() && this.packageEntryDepths.getLast() == this.currentDepth) {
            this.packageEntryDepths.removeLast();
        }
        --this.currentDepth;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.advance(length);
        super.characters(ch, start, length);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.advance(length);
        super.ignorableWhitespace(ch, start, length);
    }
    
    private class SecureSAXException extends SAXException
    {
        private static final long serialVersionUID = 2285245380321771445L;
        
        public SecureSAXException(final String message) throws SAXException {
            super(message);
        }
        
        public boolean isCausedBy(final SecureContentHandler handler) {
            return SecureContentHandler.this == handler;
        }
    }
}
