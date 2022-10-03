package org.apache.tika.sax;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DIFContentHandler extends DefaultHandler
{
    private static final char[] NEWLINE;
    private static final char[] TABSPACE;
    private static final Attributes EMPTY_ATTRIBUTES;
    private final Stack<String> treeStack;
    private final Stack<String> dataStack;
    private final ContentHandler delegate;
    private final Metadata metadata;
    private boolean isLeaf;
    
    public DIFContentHandler(final ContentHandler delegate, final Metadata metadata) {
        this.delegate = delegate;
        this.isLeaf = false;
        this.metadata = metadata;
        this.treeStack = new Stack<String>();
        this.dataStack = new Stack<String>();
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.delegate.setDocumentLocator(locator);
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String value = new String(ch, start, length);
        this.dataStack.push(value);
        if (this.treeStack.peek().equals("Entry_Title")) {
            this.delegate.characters(DIFContentHandler.NEWLINE, 0, DIFContentHandler.NEWLINE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.startElement("", "h3", "h3", DIFContentHandler.EMPTY_ATTRIBUTES);
            String title = "Title: ";
            title += value;
            this.delegate.characters(title.toCharArray(), 0, title.length());
            this.delegate.endElement("", "h3", "h3");
        }
        if (this.treeStack.peek().equals("Southernmost_Latitude") || this.treeStack.peek().equals("Northernmost_Latitude") || this.treeStack.peek().equals("Westernmost_Longitude") || this.treeStack.peek().equals("Easternmost_Longitude")) {
            this.delegate.characters(DIFContentHandler.NEWLINE, 0, DIFContentHandler.NEWLINE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.startElement("", "tr", "tr", DIFContentHandler.EMPTY_ATTRIBUTES);
            this.delegate.startElement("", "td", "td", DIFContentHandler.EMPTY_ATTRIBUTES);
            final String key = this.treeStack.peek() + " : ";
            this.delegate.characters(key.toCharArray(), 0, key.length());
            this.delegate.endElement("", "td", "td");
            this.delegate.startElement("", "td", "td", DIFContentHandler.EMPTY_ATTRIBUTES);
            this.delegate.characters(value.toCharArray(), 0, value.length());
            this.delegate.endElement("", "td", "td");
            this.delegate.endElement("", "tr", "tr");
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.delegate.ignorableWhitespace(ch, start, length);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.isLeaf = true;
        if (localName.equals("Spatial_Coverage")) {
            this.delegate.characters(DIFContentHandler.NEWLINE, 0, DIFContentHandler.NEWLINE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.startElement("", "h3", "h3", DIFContentHandler.EMPTY_ATTRIBUTES);
            final String value = "Geographic Data: ";
            this.delegate.characters(value.toCharArray(), 0, value.length());
            this.delegate.endElement("", "h3", "h3");
            this.delegate.characters(DIFContentHandler.NEWLINE, 0, DIFContentHandler.NEWLINE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.startElement("", "table", "table", DIFContentHandler.EMPTY_ATTRIBUTES);
        }
        this.treeStack.push(localName);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (localName.equals("Spatial_Coverage")) {
            this.delegate.characters(DIFContentHandler.NEWLINE, 0, DIFContentHandler.NEWLINE.length);
            this.delegate.characters(DIFContentHandler.TABSPACE, 0, DIFContentHandler.TABSPACE.length);
            this.delegate.endElement("", "table", "table");
        }
        if (this.isLeaf) {
            final Stack<String> tempStack = (Stack<String>)this.treeStack.clone();
            StringBuilder key = new StringBuilder();
            while (!tempStack.isEmpty()) {
                if (key.length() == 0) {
                    key = new StringBuilder(tempStack.pop());
                }
                else {
                    key.insert(0, tempStack.pop() + "-");
                }
            }
            final String value = this.dataStack.peek();
            this.metadata.add(key.toString(), value);
            this.isLeaf = false;
        }
        this.treeStack.pop();
        this.dataStack.pop();
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.delegate.startDocument();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.delegate.endDocument();
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    static {
        NEWLINE = new char[] { '\n' };
        TABSPACE = new char[] { '\t' };
        EMPTY_ATTRIBUTES = new AttributesImpl();
    }
}
