package org.apache.tika.sax;

import java.util.Iterator;
import java.util.List;
import org.xml.sax.SAXException;
import java.util.Arrays;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;

public class PhoneExtractingContentHandler extends ContentHandlerDecorator
{
    private static final String PHONE_NUMBERS = "phonenumbers";
    private final Metadata metadata;
    private final StringBuilder stringBuilder;
    
    public PhoneExtractingContentHandler(final ContentHandler handler, final Metadata metadata) {
        super(handler);
        this.metadata = metadata;
        this.stringBuilder = new StringBuilder();
    }
    
    protected PhoneExtractingContentHandler() {
        this(new DefaultHandler(), new Metadata());
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
        final List<String> numbers = CleanPhoneText.extractPhoneNumbers(this.stringBuilder.toString());
        for (final String number : numbers) {
            this.metadata.add("phonenumbers", number);
        }
    }
}
