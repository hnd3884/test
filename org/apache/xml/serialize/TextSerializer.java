package org.apache.xml.serialize;

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
    
    public void setOutputFormat(final OutputFormat outputFormat) {
        super.setOutputFormat((outputFormat != null) ? outputFormat : new OutputFormat("text", null, false));
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        this.startElement((s3 == null) ? s2 : s3, null);
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        this.endElement((s3 == null) ? s2 : s3);
    }
    
    public void startElement(final String s, final AttributeList list) throws SAXException {
        try {
            final ElementState elementState = this.getElementState();
            if (this.isDocumentState() && !this._started) {
                this.startDocument(s);
            }
            this.enterElementState(null, null, s, elementState.preserveSpace);
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void endElement(final String s) throws SAXException {
        try {
            this.endElementIO(s);
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void endElementIO(final String s) throws IOException {
        this.getElementState();
        final ElementState leaveElementState = this.leaveElementState();
        leaveElementState.afterElement = true;
        leaveElementState.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }
    
    public void processingInstructionIO(final String s, final String s2) throws IOException {
    }
    
    public void comment(final String s) {
    }
    
    public void comment(final char[] array, final int n, final int n2) {
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        try {
            final ElementState content = this.content();
            final boolean b = false;
            content.inCData = b;
            content.doCData = b;
            this.printText(array, n, n2, true, true);
        }
        catch (final IOException ex) {
            throw new SAXException(ex);
        }
    }
    
    protected void characters(final String s, final boolean b) throws IOException {
        final ElementState content = this.content();
        final boolean b2 = false;
        content.inCData = b2;
        content.doCData = b2;
        this.printText(s, true, true);
    }
    
    protected void startDocument(final String s) throws IOException {
        this._printer.leaveDTD();
        this._started = true;
        this.serializePreRoot();
    }
    
    protected void serializeElement(final Element element) throws IOException {
        final String tagName = element.getTagName();
        final ElementState elementState = this.getElementState();
        if (this.isDocumentState() && !this._started) {
            this.startDocument(tagName);
        }
        final boolean preserveSpace = elementState.preserveSpace;
        if (element.hasChildNodes()) {
            this.enterElementState(null, null, tagName, preserveSpace);
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                this.serializeNode(node);
            }
            this.endElementIO(tagName);
        }
        else if (!this.isDocumentState()) {
            elementState.afterElement = true;
            elementState.empty = false;
        }
    }
    
    protected void serializeNode(final Node node) throws IOException {
        switch (node.getNodeType()) {
            case 3: {
                if (node.getNodeValue() != null) {
                    this.characters(node.getNodeValue(), true);
                    break;
                }
                break;
            }
            case 4: {
                if (node.getNodeValue() != null) {
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
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    this.serializeNode(node2);
                }
                break;
            }
        }
    }
    
    protected ElementState content() {
        final ElementState elementState = this.getElementState();
        if (!this.isDocumentState()) {
            if (elementState.empty) {
                elementState.empty = false;
            }
            elementState.afterElement = false;
        }
        return elementState;
    }
    
    protected String getEntityRef(final int n) {
        return null;
    }
}
