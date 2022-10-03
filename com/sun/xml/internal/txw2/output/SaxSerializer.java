package com.sun.xml.internal.txw2.output;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.xml.internal.txw2.TxwException;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Stack;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public class SaxSerializer implements XmlSerializer
{
    private final ContentHandler writer;
    private final LexicalHandler lexical;
    private final Stack<String> prefixBindings;
    private final Stack<String> elementBindings;
    private final AttributesImpl attrs;
    
    public SaxSerializer(final ContentHandler handler) {
        this(handler, null, true);
    }
    
    public SaxSerializer(final ContentHandler handler, final LexicalHandler lex) {
        this(handler, lex, true);
    }
    
    public SaxSerializer(final ContentHandler handler, final LexicalHandler lex, final boolean indenting) {
        this.prefixBindings = new Stack<String>();
        this.elementBindings = new Stack<String>();
        this.attrs = new AttributesImpl();
        if (!indenting) {
            this.writer = handler;
            this.lexical = lex;
        }
        else {
            final IndentingXMLFilter indenter = new IndentingXMLFilter(handler, lex);
            this.writer = indenter;
            this.lexical = indenter;
        }
    }
    
    public SaxSerializer(final SAXResult result) {
        this(result.getHandler(), result.getLexicalHandler());
    }
    
    @Override
    public void startDocument() {
        try {
            this.writer.startDocument();
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void writeXmlns(String prefix, final String uri) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals("xml")) {
            return;
        }
        this.prefixBindings.add(uri);
        this.prefixBindings.add(prefix);
    }
    
    @Override
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        this.elementBindings.add(getQName(prefix, localName));
        this.elementBindings.add(localName);
        this.elementBindings.add(uri);
    }
    
    @Override
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        this.attrs.addAttribute(uri, localName, getQName(prefix, localName), "CDATA", value.toString());
    }
    
    @Override
    public void endStartTag(final String uri, final String localName, final String prefix) {
        try {
            while (this.prefixBindings.size() != 0) {
                this.writer.startPrefixMapping(this.prefixBindings.pop(), this.prefixBindings.pop());
            }
            this.writer.startElement(uri, localName, getQName(prefix, localName), this.attrs);
            this.attrs.clear();
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void endTag() {
        try {
            this.writer.endElement(this.elementBindings.pop(), this.elementBindings.pop(), this.elementBindings.pop());
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void text(final StringBuilder text) {
        try {
            this.writer.characters(text.toString().toCharArray(), 0, text.length());
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void cdata(final StringBuilder text) {
        if (this.lexical == null) {
            throw new UnsupportedOperationException("LexicalHandler is needed to write PCDATA");
        }
        try {
            this.lexical.startCDATA();
            this.text(text);
            this.lexical.endCDATA();
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void comment(final StringBuilder comment) {
        try {
            if (this.lexical == null) {
                throw new UnsupportedOperationException("LexicalHandler is needed to write comments");
            }
            this.lexical.comment(comment.toString().toCharArray(), 0, comment.length());
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void endDocument() {
        try {
            this.writer.endDocument();
        }
        catch (final SAXException e) {
            throw new TxwException(e);
        }
    }
    
    @Override
    public void flush() {
    }
    
    private static String getQName(final String prefix, final String localName) {
        String qName;
        if (prefix == null || prefix.length() == 0) {
            qName = localName;
        }
        else {
            qName = prefix + ':' + localName;
        }
        return qName;
    }
}
