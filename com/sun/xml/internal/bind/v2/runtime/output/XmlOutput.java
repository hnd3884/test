package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

public interface XmlOutput
{
    void startDocument(final XMLSerializer p0, final boolean p1, final int[] p2, final NamespaceContextImpl p3) throws IOException, SAXException, XMLStreamException;
    
    void endDocument(final boolean p0) throws IOException, SAXException, XMLStreamException;
    
    void beginStartTag(final Name p0) throws IOException, XMLStreamException;
    
    void beginStartTag(final int p0, final String p1) throws IOException, XMLStreamException;
    
    void attribute(final Name p0, final String p1) throws IOException, XMLStreamException;
    
    void attribute(final int p0, final String p1, final String p2) throws IOException, XMLStreamException;
    
    void endStartTag() throws IOException, SAXException;
    
    void endTag(final Name p0) throws IOException, SAXException, XMLStreamException;
    
    void endTag(final int p0, final String p1) throws IOException, SAXException, XMLStreamException;
    
    void text(final String p0, final boolean p1) throws IOException, SAXException, XMLStreamException;
    
    void text(final Pcdata p0, final boolean p1) throws IOException, SAXException, XMLStreamException;
}
