package com.sun.xml.internal.txw2.output;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import java.util.Stack;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class IndentingXMLFilter extends XMLFilterImpl implements LexicalHandler
{
    private LexicalHandler lexical;
    private static final char[] NEWLINE;
    private static final Object SEEN_NOTHING;
    private static final Object SEEN_ELEMENT;
    private static final Object SEEN_DATA;
    private Object state;
    private Stack<Object> stateStack;
    private String indentStep;
    private int depth;
    
    public IndentingXMLFilter() {
        this.state = IndentingXMLFilter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        this.indentStep = "";
        this.depth = 0;
    }
    
    public IndentingXMLFilter(final ContentHandler handler) {
        this.state = IndentingXMLFilter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        this.indentStep = "";
        this.depth = 0;
        this.setContentHandler(handler);
    }
    
    public IndentingXMLFilter(final ContentHandler handler, final LexicalHandler lexical) {
        this.state = IndentingXMLFilter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        this.indentStep = "";
        this.depth = 0;
        this.setContentHandler(handler);
        this.setLexicalHandler(lexical);
    }
    
    public LexicalHandler getLexicalHandler() {
        return this.lexical;
    }
    
    public void setLexicalHandler(final LexicalHandler lexical) {
        this.lexical = lexical;
    }
    
    @Deprecated
    public int getIndentStep() {
        return this.indentStep.length();
    }
    
    @Deprecated
    public void setIndentStep(int indentStep) {
        final StringBuilder s = new StringBuilder();
        while (indentStep > 0) {
            s.append(' ');
            --indentStep;
        }
        this.setIndentStep(s.toString());
    }
    
    public void setIndentStep(final String s) {
        this.indentStep = s;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.stateStack.push(IndentingXMLFilter.SEEN_ELEMENT);
        this.state = IndentingXMLFilter.SEEN_NOTHING;
        if (this.depth > 0) {
            this.writeNewLine();
        }
        this.doIndent();
        super.startElement(uri, localName, qName, atts);
        ++this.depth;
    }
    
    private void writeNewLine() throws SAXException {
        super.characters(IndentingXMLFilter.NEWLINE, 0, IndentingXMLFilter.NEWLINE.length);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        --this.depth;
        if (this.state == IndentingXMLFilter.SEEN_ELEMENT) {
            this.writeNewLine();
            this.doIndent();
        }
        super.endElement(uri, localName, qName);
        this.state = this.stateStack.pop();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.state = IndentingXMLFilter.SEEN_DATA;
        super.characters(ch, start, length);
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        if (this.depth > 0) {
            this.writeNewLine();
        }
        this.doIndent();
        if (this.lexical != null) {
            this.lexical.comment(ch, start, length);
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        if (this.lexical != null) {
            this.lexical.startDTD(name, publicId, systemId);
        }
    }
    
    @Override
    public void endDTD() throws SAXException {
        if (this.lexical != null) {
            this.lexical.endDTD();
        }
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
        if (this.lexical != null) {
            this.lexical.startEntity(name);
        }
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (this.lexical != null) {
            this.lexical.endEntity(name);
        }
    }
    
    @Override
    public void startCDATA() throws SAXException {
        if (this.lexical != null) {
            this.lexical.startCDATA();
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        if (this.lexical != null) {
            this.lexical.endCDATA();
        }
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
        NEWLINE = new char[] { '\n' };
        SEEN_NOTHING = new Object();
        SEEN_ELEMENT = new Object();
        SEEN_DATA = new Object();
    }
}
