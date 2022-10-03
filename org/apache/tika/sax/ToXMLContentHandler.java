package org.apache.tika.sax;

import java.util.Collections;
import java.util.Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.io.OutputStream;
import java.util.Map;

public class ToXMLContentHandler extends ToTextContentHandler
{
    protected final Map<String, String> namespaces;
    private final String encoding;
    protected boolean inStartElement;
    private ElementInfo currentElement;
    
    public ToXMLContentHandler(final OutputStream stream, final String encoding) throws UnsupportedEncodingException {
        super(stream, encoding);
        this.namespaces = new HashMap<String, String>();
        this.inStartElement = false;
        this.encoding = encoding;
    }
    
    public ToXMLContentHandler(final String encoding) {
        this.namespaces = new HashMap<String, String>();
        this.inStartElement = false;
        this.encoding = encoding;
    }
    
    public ToXMLContentHandler() {
        this.namespaces = new HashMap<String, String>();
        this.inStartElement = false;
        this.encoding = null;
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (this.encoding != null) {
            this.write("<?xml version=\"1.0\" encoding=\"");
            this.write(this.encoding);
            this.write("\"?>\n");
        }
        this.currentElement = null;
        this.namespaces.clear();
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        try {
            if (this.currentElement != null && prefix.equals(this.currentElement.getPrefix(uri))) {
                return;
            }
        }
        catch (final SAXException ex) {}
        this.namespaces.put(uri, prefix);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.lazyCloseStartElement();
        this.currentElement = new ElementInfo(this.currentElement, this.namespaces);
        this.write('<');
        this.write(this.currentElement.getQName(uri, localName));
        for (int i = 0; i < atts.getLength(); ++i) {
            this.write(' ');
            this.write(this.currentElement.getQName(atts.getURI(i), atts.getLocalName(i)));
            this.write('=');
            this.write('\"');
            final char[] ch = atts.getValue(i).toCharArray();
            this.writeEscaped(ch, 0, ch.length, true);
            this.write('\"');
        }
        for (final Map.Entry<String, String> entry : this.namespaces.entrySet()) {
            this.write(' ');
            this.write("xmlns");
            final String prefix = entry.getValue();
            if (prefix.length() > 0) {
                this.write(':');
                this.write(prefix);
            }
            this.write('=');
            this.write('\"');
            final char[] ch2 = entry.getKey().toCharArray();
            this.writeEscaped(ch2, 0, ch2.length, true);
            this.write('\"');
        }
        this.namespaces.clear();
        this.inStartElement = true;
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (this.inStartElement) {
            this.write(" />");
            this.inStartElement = false;
        }
        else {
            this.write("</");
            this.write(qName);
            this.write('>');
        }
        this.namespaces.clear();
        this.currentElement = this.currentElement.parent;
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.lazyCloseStartElement();
        this.writeEscaped(ch, start, start + length, false);
    }
    
    private void lazyCloseStartElement() throws SAXException {
        if (this.inStartElement) {
            this.write('>');
            this.inStartElement = false;
        }
    }
    
    protected void write(final char ch) throws SAXException {
        super.characters(new char[] { ch }, 0, 1);
    }
    
    protected void write(final String string) throws SAXException {
        super.characters(string.toCharArray(), 0, string.length());
    }
    
    private int writeCharsAndEntity(final char[] ch, final int from, final int to, final String entity) throws SAXException {
        super.characters(ch, from, to - from);
        this.write('&');
        this.write(entity);
        this.write(';');
        return to + 1;
    }
    
    private void writeEscaped(final char[] ch, int from, final int to, final boolean attribute) throws SAXException {
        int pos = from;
        while (pos < to) {
            if (ch[pos] == '<') {
                pos = (from = this.writeCharsAndEntity(ch, from, pos, "lt"));
            }
            else if (ch[pos] == '>') {
                pos = (from = this.writeCharsAndEntity(ch, from, pos, "gt"));
            }
            else if (ch[pos] == '&') {
                pos = (from = this.writeCharsAndEntity(ch, from, pos, "amp"));
            }
            else if (attribute && ch[pos] == '\"') {
                pos = (from = this.writeCharsAndEntity(ch, from, pos, "quot"));
            }
            else {
                ++pos;
            }
        }
        super.characters(ch, from, to - from);
    }
    
    private static class ElementInfo
    {
        private final ElementInfo parent;
        private final Map<String, String> namespaces;
        
        public ElementInfo(final ElementInfo parent, final Map<String, String> namespaces) {
            this.parent = parent;
            if (namespaces.isEmpty()) {
                this.namespaces = Collections.emptyMap();
            }
            else {
                this.namespaces = new HashMap<String, String>(namespaces);
            }
        }
        
        public String getPrefix(final String uri) throws SAXException {
            final String prefix = this.namespaces.get(uri);
            if (prefix != null) {
                return prefix;
            }
            if (this.parent != null) {
                return this.parent.getPrefix(uri);
            }
            if (uri == null || uri.length() == 0) {
                return "";
            }
            throw new SAXException("Namespace " + uri + " not declared");
        }
        
        public String getQName(final String uri, final String localName) throws SAXException {
            final String prefix = this.getPrefix(uri);
            if (prefix.length() > 0) {
                return prefix + ":" + localName;
            }
            return localName;
        }
    }
}
