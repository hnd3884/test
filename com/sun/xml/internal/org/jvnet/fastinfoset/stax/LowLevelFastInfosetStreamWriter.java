package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public interface LowLevelFastInfosetStreamWriter
{
    void initiateLowLevelWriting() throws XMLStreamException;
    
    int getNextElementIndex();
    
    int getNextAttributeIndex();
    
    int getLocalNameIndex();
    
    int getNextLocalNameIndex();
    
    void writeLowLevelTerminationAndMark() throws IOException;
    
    void writeLowLevelStartElementIndexed(final int p0, final int p1) throws IOException;
    
    boolean writeLowLevelStartElement(final int p0, final String p1, final String p2, final String p3) throws IOException;
    
    void writeLowLevelStartNamespaces() throws IOException;
    
    void writeLowLevelNamespace(final String p0, final String p1) throws IOException;
    
    void writeLowLevelEndNamespaces() throws IOException;
    
    void writeLowLevelStartAttributes() throws IOException;
    
    void writeLowLevelAttributeIndexed(final int p0) throws IOException;
    
    boolean writeLowLevelAttribute(final String p0, final String p1, final String p2) throws IOException;
    
    void writeLowLevelAttributeValue(final String p0) throws IOException;
    
    void writeLowLevelStartNameLiteral(final int p0, final String p1, final byte[] p2, final String p3) throws IOException;
    
    void writeLowLevelStartNameLiteral(final int p0, final String p1, final int p2, final String p3) throws IOException;
    
    void writeLowLevelEndStartElement() throws IOException;
    
    void writeLowLevelEndElement() throws IOException;
    
    void writeLowLevelText(final char[] p0, final int p1) throws IOException;
    
    void writeLowLevelText(final String p0) throws IOException;
    
    void writeLowLevelOctets(final byte[] p0, final int p1) throws IOException;
}
