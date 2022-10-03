package com.sun.xml.internal.txw2.output;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

public class IndentingXMLStreamWriter extends DelegatingXMLStreamWriter
{
    private static final Object SEEN_NOTHING;
    private static final Object SEEN_ELEMENT;
    private static final Object SEEN_DATA;
    private Object state;
    private Stack<Object> stateStack;
    private String indentStep;
    private int depth;
    
    public IndentingXMLStreamWriter(final XMLStreamWriter writer) {
        super(writer);
        this.state = IndentingXMLStreamWriter.SEEN_NOTHING;
        this.stateStack = new Stack<Object>();
        this.indentStep = "  ";
        this.depth = 0;
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
    
    private void onStartElement() throws XMLStreamException {
        this.stateStack.push(IndentingXMLStreamWriter.SEEN_ELEMENT);
        this.state = IndentingXMLStreamWriter.SEEN_NOTHING;
        if (this.depth > 0) {
            super.writeCharacters("\n");
        }
        this.doIndent();
        ++this.depth;
    }
    
    private void onEndElement() throws XMLStreamException {
        --this.depth;
        if (this.state == IndentingXMLStreamWriter.SEEN_ELEMENT) {
            super.writeCharacters("\n");
            this.doIndent();
        }
        this.state = this.stateStack.pop();
    }
    
    private void onEmptyElement() throws XMLStreamException {
        this.state = IndentingXMLStreamWriter.SEEN_ELEMENT;
        if (this.depth > 0) {
            super.writeCharacters("\n");
        }
        this.doIndent();
    }
    
    private void doIndent() throws XMLStreamException {
        if (this.depth > 0) {
            for (int i = 0; i < this.depth; ++i) {
                super.writeCharacters(this.indentStep);
            }
        }
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException {
        super.writeStartDocument();
        super.writeCharacters("\n");
    }
    
    @Override
    public void writeStartDocument(final String version) throws XMLStreamException {
        super.writeStartDocument(version);
        super.writeCharacters("\n");
    }
    
    @Override
    public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        super.writeStartDocument(encoding, version);
        super.writeCharacters("\n");
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.onStartElement();
        super.writeStartElement(localName);
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.onStartElement();
        super.writeStartElement(namespaceURI, localName);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.onStartElement();
        super.writeStartElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(namespaceURI, localName);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.onEmptyElement();
        super.writeEmptyElement(localName);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.onEndElement();
        super.writeEndElement();
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        this.state = IndentingXMLStreamWriter.SEEN_DATA;
        super.writeCharacters(text);
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.state = IndentingXMLStreamWriter.SEEN_DATA;
        super.writeCharacters(text, start, len);
    }
    
    @Override
    public void writeCData(final String data) throws XMLStreamException {
        this.state = IndentingXMLStreamWriter.SEEN_DATA;
        super.writeCData(data);
    }
    
    static {
        SEEN_NOTHING = new Object();
        SEEN_ELEMENT = new Object();
        SEEN_DATA = new Object();
    }
}
