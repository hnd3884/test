package com.fasterxml.jackson.dataformat.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamWriter2;
import com.fasterxml.jackson.core.PrettyPrinter;

public interface XmlPrettyPrinter extends PrettyPrinter
{
    void writeStartElement(final XMLStreamWriter2 p0, final String p1, final String p2) throws XMLStreamException;
    
    void writeEndElement(final XMLStreamWriter2 p0, final int p1) throws XMLStreamException;
    
    void writePrologLinefeed(final XMLStreamWriter2 p0) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final String p3, final boolean p4) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final char[] p3, final int p4, final int p5, final boolean p6) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final boolean p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final int p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final long p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final double p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final float p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final BigInteger p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final BigDecimal p3) throws XMLStreamException;
    
    void writeLeafElement(final XMLStreamWriter2 p0, final String p1, final String p2, final byte[] p3, final int p4, final int p5) throws XMLStreamException;
    
    void writeLeafNullElement(final XMLStreamWriter2 p0, final String p1, final String p2) throws XMLStreamException;
}
