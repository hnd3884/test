package org.apache.tika.sax;

import java.util.Iterator;
import java.util.List;
import org.xml.sax.SAXException;
import java.util.Arrays;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;

public class StandardsExtractingContentHandler extends ContentHandlerDecorator
{
    public static final String STANDARD_REFERENCES = "standard_references";
    private final Metadata metadata;
    private final StringBuilder stringBuilder;
    private double threshold;
    
    public StandardsExtractingContentHandler(final ContentHandler handler, final Metadata metadata) {
        super(handler);
        this.threshold = 0.0;
        this.metadata = metadata;
        this.stringBuilder = new StringBuilder();
    }
    
    protected StandardsExtractingContentHandler() {
        this(new DefaultHandler(), new Metadata());
    }
    
    public double getThreshold() {
        return this.threshold;
    }
    
    public void setThreshold(final double score) {
        this.threshold = score;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            final String text = new String(Arrays.copyOfRange(ch, start, start + length));
            this.stringBuilder.append(text);
            super.characters(ch, start, length);
        }
        catch (final SAXException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        final List<StandardReference> standards = StandardsText.extractStandardReferences(this.stringBuilder.toString(), this.threshold);
        for (final StandardReference standardReference : standards) {
            this.metadata.add("standard_references", standardReference.toString());
        }
    }
}
