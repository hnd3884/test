package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;

public interface XmlSaxHandler
{
    ContentHandler getContentHandler();
    
    LexicalHandler getLexicalHandler();
    
    void bookmarkLastEvent(final XmlCursor.XmlBookmark p0);
    
    void bookmarkLastAttr(final QName p0, final XmlCursor.XmlBookmark p1);
    
    XmlObject getObject() throws XmlException;
}
