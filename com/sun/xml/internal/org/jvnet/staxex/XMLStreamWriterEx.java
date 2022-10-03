package com.sun.xml.internal.org.jvnet.staxex;

import javax.xml.namespace.NamespaceContext;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface XMLStreamWriterEx extends XMLStreamWriter
{
    void writeBinary(final byte[] p0, final int p1, final int p2, final String p3) throws XMLStreamException;
    
    void writeBinary(final DataHandler p0) throws XMLStreamException;
    
    OutputStream writeBinary(final String p0) throws XMLStreamException;
    
    void writePCDATA(final CharSequence p0) throws XMLStreamException;
    
    NamespaceContextEx getNamespaceContext();
}
