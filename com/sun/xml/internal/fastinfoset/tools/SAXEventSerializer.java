package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import org.xml.sax.Attributes;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.List;
import java.util.Stack;
import java.io.Writer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer extends DefaultHandler implements LexicalHandler
{
    private Writer _writer;
    private boolean _charactersAreCDATA;
    private StringBuffer _characters;
    private Stack _namespaceStack;
    protected List _namespaceAttributes;
    
    public SAXEventSerializer(final OutputStream s) throws IOException {
        this._namespaceStack = new Stack();
        this._writer = new OutputStreamWriter(s);
        this._charactersAreCDATA = false;
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this._writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
            this._writer.write("<startDocument/>\n");
            this._writer.flush();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this._writer.write("<endDocument/>\n");
            this._writer.write("</sax>");
            this._writer.flush();
            this._writer.close();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this._namespaceAttributes == null) {
            this._namespaceAttributes = new ArrayList();
        }
        final String qName = (prefix.length() == 0) ? "xmlns" : ("xmlns" + prefix);
        final AttributeValueHolder attribute = new AttributeValueHolder(qName, prefix, uri, null, null);
        this._namespaceAttributes.add(attribute);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        try {
            this.outputCharacters();
            if (this._namespaceAttributes != null) {
                AttributeValueHolder[] attrsHolder = new AttributeValueHolder[0];
                attrsHolder = this._namespaceAttributes.toArray(attrsHolder);
                this.quicksort(attrsHolder, 0, attrsHolder.length - 1);
                for (int i = 0; i < attrsHolder.length; ++i) {
                    this._writer.write("<startPrefixMapping prefix=\"" + attrsHolder[i].localName + "\" uri=\"" + attrsHolder[i].uri + "\"/>\n");
                    this._writer.flush();
                }
                this._namespaceStack.push(attrsHolder);
                this._namespaceAttributes = null;
            }
            else {
                this._namespaceStack.push(null);
            }
            AttributeValueHolder[] attrsHolder = new AttributeValueHolder[attributes.getLength()];
            for (int i = 0; i < attributes.getLength(); ++i) {
                attrsHolder[i] = new AttributeValueHolder(attributes.getQName(i), attributes.getLocalName(i), attributes.getURI(i), attributes.getType(i), attributes.getValue(i));
            }
            this.quicksort(attrsHolder, 0, attrsHolder.length - 1);
            int attributeCount = 0;
            for (int j = 0; j < attrsHolder.length; ++j) {
                if (!attrsHolder[j].uri.equals("http://www.w3.org/2000/xmlns/")) {
                    ++attributeCount;
                }
            }
            if (attributeCount == 0) {
                this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
                return;
            }
            this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\">\n");
            for (int j = 0; j < attrsHolder.length; ++j) {
                if (!attrsHolder[j].uri.equals("http://www.w3.org/2000/xmlns/")) {
                    this._writer.write("  <attribute qName=\"" + attrsHolder[j].qName + "\" localName=\"" + attrsHolder[j].localName + "\" uri=\"" + attrsHolder[j].uri + "\" value=\"" + attrsHolder[j].value + "\"/>\n");
                }
            }
            this._writer.write("</startElement>\n");
            this._writer.flush();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<endElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
            this._writer.flush();
            final AttributeValueHolder[] attrsHolder = this._namespaceStack.pop();
            if (attrsHolder != null) {
                for (int i = 0; i < attrsHolder.length; ++i) {
                    this._writer.write("<endPrefixMapping prefix=\"" + attrsHolder[i].localName + "\"/>\n");
                    this._writer.flush();
                }
            }
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (length == 0) {
            return;
        }
        if (this._characters == null) {
            this._characters = new StringBuffer();
        }
        this._characters.append(ch, start, length);
    }
    
    private void outputCharacters() throws SAXException {
        if (this._characters == null) {
            return;
        }
        try {
            this._writer.write("<characters>" + (this._charactersAreCDATA ? "<![CDATA[" : "") + (Object)this._characters + (this._charactersAreCDATA ? "]]>" : "") + "</characters>\n");
            this._writer.flush();
            this._characters = null;
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<processingInstruction target=\"" + target + "\" data=\"" + data + "\"/>\n");
            this._writer.flush();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this._charactersAreCDATA = true;
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this._charactersAreCDATA = false;
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<comment>" + new String(ch, start, length) + "</comment>\n");
            this._writer.flush();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    private void quicksort(final AttributeValueHolder[] attrs, int p, final int r) {
        while (p < r) {
            final int q = this.partition(attrs, p, r);
            this.quicksort(attrs, p, q);
            p = q + 1;
        }
    }
    
    private int partition(final AttributeValueHolder[] attrs, final int p, final int r) {
        final AttributeValueHolder x = attrs[p + r >>> 1];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x.compareTo(attrs[--j]) < 0) {
                continue;
            }
            while (x.compareTo(attrs[++i]) > 0) {}
            if (i >= j) {
                break;
            }
            final AttributeValueHolder t = attrs[i];
            attrs[i] = attrs[j];
            attrs[j] = t;
        }
        return j;
    }
    
    public static class AttributeValueHolder implements Comparable
    {
        public final String qName;
        public final String localName;
        public final String uri;
        public final String type;
        public final String value;
        
        public AttributeValueHolder(final String qName, final String localName, final String uri, final String type, final String value) {
            this.qName = qName;
            this.localName = localName;
            this.uri = uri;
            this.type = type;
            this.value = value;
        }
        
        @Override
        public int compareTo(final Object o) {
            try {
                return this.qName.compareTo(((AttributeValueHolder)o).qName);
            }
            catch (final Exception e) {
                throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            try {
                return o instanceof AttributeValueHolder && this.qName.equals(((AttributeValueHolder)o).qName);
            }
            catch (final Exception e) {
                throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
            }
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + ((this.qName != null) ? this.qName.hashCode() : 0);
            return hash;
        }
    }
}
