package org.apache.xmlbeans.impl.richParser;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDate;
import java.util.Date;
import org.apache.xmlbeans.XmlCalendar;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface XMLStreamReaderExt extends XMLStreamReader
{
    public static final int WS_PRESERVE = 1;
    public static final int WS_REPLACE = 2;
    public static final int WS_COLLAPSE = 3;
    
    String getStringValue() throws XMLStreamException;
    
    String getStringValue(final int p0) throws XMLStreamException;
    
    boolean getBooleanValue() throws XMLStreamException;
    
    byte getByteValue() throws XMLStreamException;
    
    short getShortValue() throws XMLStreamException;
    
    int getIntValue() throws XMLStreamException;
    
    long getLongValue() throws XMLStreamException;
    
    BigInteger getBigIntegerValue() throws XMLStreamException;
    
    BigDecimal getBigDecimalValue() throws XMLStreamException;
    
    float getFloatValue() throws XMLStreamException;
    
    double getDoubleValue() throws XMLStreamException;
    
    InputStream getHexBinaryValue() throws XMLStreamException;
    
    InputStream getBase64Value() throws XMLStreamException;
    
    XmlCalendar getCalendarValue() throws XMLStreamException;
    
    Date getDateValue() throws XMLStreamException;
    
    GDate getGDateValue() throws XMLStreamException;
    
    GDuration getGDurationValue() throws XMLStreamException;
    
    QName getQNameValue() throws XMLStreamException;
    
    String getAttributeStringValue(final int p0) throws XMLStreamException;
    
    String getAttributeStringValue(final int p0, final int p1) throws XMLStreamException;
    
    boolean getAttributeBooleanValue(final int p0) throws XMLStreamException;
    
    byte getAttributeByteValue(final int p0) throws XMLStreamException;
    
    short getAttributeShortValue(final int p0) throws XMLStreamException;
    
    int getAttributeIntValue(final int p0) throws XMLStreamException;
    
    long getAttributeLongValue(final int p0) throws XMLStreamException;
    
    BigInteger getAttributeBigIntegerValue(final int p0) throws XMLStreamException;
    
    BigDecimal getAttributeBigDecimalValue(final int p0) throws XMLStreamException;
    
    float getAttributeFloatValue(final int p0) throws XMLStreamException;
    
    double getAttributeDoubleValue(final int p0) throws XMLStreamException;
    
    InputStream getAttributeHexBinaryValue(final int p0) throws XMLStreamException;
    
    InputStream getAttributeBase64Value(final int p0) throws XMLStreamException;
    
    XmlCalendar getAttributeCalendarValue(final int p0) throws XMLStreamException;
    
    Date getAttributeDateValue(final int p0) throws XMLStreamException;
    
    GDate getAttributeGDateValue(final int p0) throws XMLStreamException;
    
    GDuration getAttributeGDurationValue(final int p0) throws XMLStreamException;
    
    QName getAttributeQNameValue(final int p0) throws XMLStreamException;
    
    String getAttributeStringValue(final String p0, final String p1) throws XMLStreamException;
    
    String getAttributeStringValue(final String p0, final String p1, final int p2) throws XMLStreamException;
    
    boolean getAttributeBooleanValue(final String p0, final String p1) throws XMLStreamException;
    
    byte getAttributeByteValue(final String p0, final String p1) throws XMLStreamException;
    
    short getAttributeShortValue(final String p0, final String p1) throws XMLStreamException;
    
    int getAttributeIntValue(final String p0, final String p1) throws XMLStreamException;
    
    long getAttributeLongValue(final String p0, final String p1) throws XMLStreamException;
    
    BigInteger getAttributeBigIntegerValue(final String p0, final String p1) throws XMLStreamException;
    
    BigDecimal getAttributeBigDecimalValue(final String p0, final String p1) throws XMLStreamException;
    
    float getAttributeFloatValue(final String p0, final String p1) throws XMLStreamException;
    
    double getAttributeDoubleValue(final String p0, final String p1) throws XMLStreamException;
    
    InputStream getAttributeHexBinaryValue(final String p0, final String p1) throws XMLStreamException;
    
    InputStream getAttributeBase64Value(final String p0, final String p1) throws XMLStreamException;
    
    XmlCalendar getAttributeCalendarValue(final String p0, final String p1) throws XMLStreamException;
    
    Date getAttributeDateValue(final String p0, final String p1) throws XMLStreamException;
    
    GDate getAttributeGDateValue(final String p0, final String p1) throws XMLStreamException;
    
    GDuration getAttributeGDurationValue(final String p0, final String p1) throws XMLStreamException;
    
    QName getAttributeQNameValue(final String p0, final String p1) throws XMLStreamException;
    
    void setDefaultValue(final String p0) throws XMLStreamException;
}
