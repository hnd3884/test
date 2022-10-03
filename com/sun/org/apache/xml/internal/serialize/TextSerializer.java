package com.sun.org.apache.xml.internal.serialize;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;

public class TextSerializer extends BaseMarkupSerializer
{
    public TextSerializer() {
        super(new OutputFormat("text", null, false));
    }
    
    @Override
    public void setOutputFormat(final OutputFormat format) {
        super.setOutputFormat((format != null) ? format : new OutputFormat("text", null, false));
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String rawName, final Attributes attrs) throws SAXException {
        this.startElement((rawName == null) ? localName : rawName, null);
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String rawName) throws SAXException {
        this.endElement((rawName == null) ? localName : rawName);
    }
    
    @Override
    public void startElement(final String tagName, final AttributeList attrs) throws SAXException {
        try {
            ElementState state = this.getElementState();
            if (this.isDocumentState() && !this._started) {
                this.startDocument(tagName);
            }
            final boolean preserveSpace = state.preserveSpace;
            state = this.enterElementState(null, null, tagName, preserveSpace);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    public void endElement(final String tagName) throws SAXException {
        try {
            this.endElementIO(tagName);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    public void endElementIO(final String tagName) throws IOException {
        ElementState state = this.getElementState();
        state = this.leaveElementState();
        state.afterElement = true;
        state.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }
    
    @Override
    public void processingInstructionIO(final String target, final String code) throws IOException {
    }
    
    @Override
    public void comment(final String text) {
    }
    
    @Override
    public void comment(final char[] chars, final int start, final int length) {
    }
    
    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        try {
            final ElementState content = this.content();
            final boolean b = false;
            content.inCData = b;
            content.doCData = b;
            this.printText(chars, start, length, true, true);
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    protected void characters(final String text, final boolean unescaped) throws IOException {
        final ElementState content = this.content();
        final boolean b = false;
        content.inCData = b;
        content.doCData = b;
        this.printText(text, true, true);
    }
    
    protected void startDocument(final String rootTagName) throws IOException {
        this._printer.leaveDTD();
        this._started = true;
        this.serializePreRoot();
    }
    
    @Override
    protected void serializeElement(final Element elem) throws IOException {
        final String tagName = elem.getTagName();
        ElementState state = this.getElementState();
        if (this.isDocumentState() && !this._started) {
            this.startDocument(tagName);
        }
        final boolean preserveSpace = state.preserveSpace;
        if (elem.hasChildNodes()) {
            state = this.enterElementState(null, null, tagName, preserveSpace);
            for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                this.serializeNode(child);
            }
            this.endElementIO(tagName);
        }
        else if (!this.isDocumentState()) {
            state.afterElement = true;
            state.empty = false;
        }
    }
    
    @Override
    protected void serializeNode(final Node node) throws IOException {
        switch (node.getNodeType()) {
            case 3: {
                final String text = node.getNodeValue();
                if (text != null) {
                    this.characters(node.getNodeValue(), true);
                    break;
                }
                break;
            }
            case 4: {
                final String text = node.getNodeValue();
                if (text != null) {
                    this.characters(node.getNodeValue(), true);
                    break;
                }
                break;
            }
            case 8: {}
            case 5: {}
            case 1: {
                this.serializeElement((Element)node);
                break;
            }
            case 9:
            case 11: {
                for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    this.serializeNode(child);
                }
                break;
            }
        }
    }
    
    @Override
    protected ElementState content() {
        final ElementState state = this.getElementState();
        if (!this.isDocumentState()) {
            if (state.empty) {
                state.empty = false;
            }
            state.afterElement = false;
        }
        return state;
    }
    
    @Override
    protected String getEntityRef(final int ch) {
        return null;
    }
}
