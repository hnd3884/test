package org.apache.xmlbeans;

import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLInputStream;

public interface XmlTokenSource
{
    Object monitor();
    
    XmlDocumentProperties documentProperties();
    
    XmlCursor newCursor();
    
    @Deprecated
    XMLInputStream newXMLInputStream();
    
    XMLStreamReader newXMLStreamReader();
    
    String xmlText();
    
    InputStream newInputStream();
    
    Reader newReader();
    
    Node newDomNode();
    
    Node getDomNode();
    
    void save(final ContentHandler p0, final LexicalHandler p1) throws SAXException;
    
    void save(final File p0) throws IOException;
    
    void save(final OutputStream p0) throws IOException;
    
    void save(final Writer p0) throws IOException;
    
    @Deprecated
    XMLInputStream newXMLInputStream(final XmlOptions p0);
    
    XMLStreamReader newXMLStreamReader(final XmlOptions p0);
    
    String xmlText(final XmlOptions p0);
    
    InputStream newInputStream(final XmlOptions p0);
    
    Reader newReader(final XmlOptions p0);
    
    Node newDomNode(final XmlOptions p0);
    
    void save(final ContentHandler p0, final LexicalHandler p1, final XmlOptions p2) throws SAXException;
    
    void save(final File p0, final XmlOptions p1) throws IOException;
    
    void save(final OutputStream p0, final XmlOptions p1) throws IOException;
    
    void save(final Writer p0, final XmlOptions p1) throws IOException;
    
    void dump();
}
