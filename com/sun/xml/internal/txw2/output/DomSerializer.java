package com.sun.xml.internal.txw2.output;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.xml.internal.txw2.TxwException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;

public class DomSerializer implements XmlSerializer
{
    private final SaxSerializer serializer;
    
    public DomSerializer(final Node node) {
        final Dom2SaxAdapter adapter = new Dom2SaxAdapter(node);
        this.serializer = new SaxSerializer(adapter, adapter, false);
    }
    
    public DomSerializer(final DOMResult domResult) {
        final Node node = domResult.getNode();
        if (node == null) {
            try {
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                final DocumentBuilder db = dbf.newDocumentBuilder();
                final Document doc = db.newDocument();
                domResult.setNode(doc);
                this.serializer = new SaxSerializer(new Dom2SaxAdapter(doc), null, false);
                return;
            }
            catch (final ParserConfigurationException pce) {
                throw new TxwException(pce);
            }
        }
        this.serializer = new SaxSerializer(new Dom2SaxAdapter(node), null, false);
    }
    
    @Override
    public void startDocument() {
        this.serializer.startDocument();
    }
    
    @Override
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        this.serializer.beginStartTag(uri, localName, prefix);
    }
    
    @Override
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        this.serializer.writeAttribute(uri, localName, prefix, value);
    }
    
    @Override
    public void writeXmlns(final String prefix, final String uri) {
        this.serializer.writeXmlns(prefix, uri);
    }
    
    @Override
    public void endStartTag(final String uri, final String localName, final String prefix) {
        this.serializer.endStartTag(uri, localName, prefix);
    }
    
    @Override
    public void endTag() {
        this.serializer.endTag();
    }
    
    @Override
    public void text(final StringBuilder text) {
        this.serializer.text(text);
    }
    
    @Override
    public void cdata(final StringBuilder text) {
        this.serializer.cdata(text);
    }
    
    @Override
    public void comment(final StringBuilder comment) {
        this.serializer.comment(comment);
    }
    
    @Override
    public void endDocument() {
        this.serializer.endDocument();
    }
    
    @Override
    public void flush() {
    }
}
