package com.sun.xml.internal.bind.marshaller;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

public class DataWriter extends XMLWriter
{
    private static final Object SEEN_NOTHING;
    private static final Object SEEN_ELEMENT;
    private static final Object SEEN_DATA;
    private Object state;
    private Stack<Object> stateStack;
    private String indentStep;
    private int depth;
    
    public DataWriter(final Writer writer, final String encoding, final CharacterEscapeHandler _escapeHandler) {
        super(writer, encoding, _escapeHandler);
        this.state = DataWriter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        this.indentStep = "";
        this.depth = 0;
    }
    
    public DataWriter(final Writer writer, final String encoding) {
        this(writer, encoding, DumbEscapeHandler.theInstance);
    }
    
    @Deprecated
    public int getIndentStep() {
        return this.indentStep.length();
    }
    
    @Deprecated
    public void setIndentStep(int indentStep) {
        final StringBuilder buf = new StringBuilder();
        while (indentStep > 0) {
            buf.append(' ');
            --indentStep;
        }
        this.setIndentStep(buf.toString());
    }
    
    public void setIndentStep(final String s) {
        this.indentStep = s;
    }
    
    @Override
    public void reset() {
        this.depth = 0;
        this.state = DataWriter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        super.reset();
    }
    
    @Override
    protected void writeXmlDecl(final String decl) throws IOException {
        super.writeXmlDecl(decl);
        this.write('\n');
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.stateStack.push(DataWriter.SEEN_ELEMENT);
        this.state = DataWriter.SEEN_NOTHING;
        if (this.depth > 0) {
            super.characters("\n");
        }
        this.doIndent();
        super.startElement(uri, localName, qName, atts);
        ++this.depth;
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        --this.depth;
        if (this.state == DataWriter.SEEN_ELEMENT) {
            super.characters("\n");
            this.doIndent();
        }
        super.endElement(uri, localName, qName);
        this.state = this.stateStack.pop();
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.write('\n');
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        super.endDocument();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.state = DataWriter.SEEN_DATA;
        super.characters(ch, start, length);
    }
    
    private void doIndent() throws SAXException {
        if (this.depth > 0) {
            final char[] ch = this.indentStep.toCharArray();
            for (int i = 0; i < this.depth; ++i) {
                this.characters(ch, 0, ch.length);
            }
        }
    }
    
    static {
        SEEN_NOTHING = new Object();
        SEEN_ELEMENT = new Object();
        SEEN_DATA = new Object();
    }
}
