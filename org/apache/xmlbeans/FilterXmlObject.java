package org.apache.xmlbeans;

import java.util.List;
import javax.xml.namespace.QName;
import java.util.Date;
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.BigInteger;
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

public abstract class FilterXmlObject implements XmlObject, SimpleValue, DelegateXmlObject
{
    @Override
    public SchemaType schemaType() {
        return this.underlyingXmlObject().schemaType();
    }
    
    @Override
    public boolean validate() {
        return this.underlyingXmlObject().validate();
    }
    
    @Override
    public boolean validate(final XmlOptions options) {
        return this.underlyingXmlObject().validate(options);
    }
    
    @Override
    public XmlObject[] selectPath(final String path) {
        return this.underlyingXmlObject().selectPath(path);
    }
    
    @Override
    public XmlObject[] selectPath(final String path, final XmlOptions options) {
        return this.underlyingXmlObject().selectPath(path, options);
    }
    
    @Override
    public XmlObject[] execQuery(final String query) {
        return this.underlyingXmlObject().execQuery(query);
    }
    
    @Override
    public XmlObject[] execQuery(final String query, final XmlOptions options) {
        return this.underlyingXmlObject().execQuery(query, options);
    }
    
    @Override
    public XmlObject changeType(final SchemaType newType) {
        return this.underlyingXmlObject().changeType(newType);
    }
    
    @Override
    public boolean isNil() {
        return this.underlyingXmlObject().isNil();
    }
    
    @Override
    public void setNil() {
        this.underlyingXmlObject().setNil();
    }
    
    @Override
    public boolean isImmutable() {
        return this.underlyingXmlObject().isImmutable();
    }
    
    @Override
    public XmlObject set(final XmlObject srcObj) {
        return this.underlyingXmlObject().set(srcObj);
    }
    
    @Override
    public XmlObject copy() {
        return this.underlyingXmlObject().copy();
    }
    
    @Override
    public XmlObject copy(final XmlOptions options) {
        return this.underlyingXmlObject().copy(options);
    }
    
    @Override
    public boolean valueEquals(final XmlObject obj) {
        return this.underlyingXmlObject().valueEquals(obj);
    }
    
    @Override
    public int valueHashCode() {
        return this.underlyingXmlObject().valueHashCode();
    }
    
    @Override
    public int compareTo(final Object obj) {
        return this.underlyingXmlObject().compareTo(obj);
    }
    
    @Override
    public int compareValue(final XmlObject obj) {
        return this.underlyingXmlObject().compareValue(obj);
    }
    
    @Override
    public Object monitor() {
        return this.underlyingXmlObject().monitor();
    }
    
    @Override
    public XmlDocumentProperties documentProperties() {
        return this.underlyingXmlObject().documentProperties();
    }
    
    @Override
    public XmlCursor newCursor() {
        return this.underlyingXmlObject().newCursor();
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream() {
        return this.underlyingXmlObject().newXMLInputStream();
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader() {
        return this.underlyingXmlObject().newXMLStreamReader();
    }
    
    @Override
    public String xmlText() {
        return this.underlyingXmlObject().xmlText();
    }
    
    @Override
    public InputStream newInputStream() {
        return this.underlyingXmlObject().newInputStream();
    }
    
    @Override
    public Reader newReader() {
        return this.underlyingXmlObject().newReader();
    }
    
    @Override
    public Node newDomNode() {
        return this.underlyingXmlObject().newDomNode();
    }
    
    @Override
    public Node getDomNode() {
        return this.underlyingXmlObject().getDomNode();
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh) throws SAXException {
        this.underlyingXmlObject().save(ch, lh);
    }
    
    @Override
    public void save(final File file) throws IOException {
        this.underlyingXmlObject().save(file);
    }
    
    @Override
    public void save(final OutputStream os) throws IOException {
        this.underlyingXmlObject().save(os);
    }
    
    @Override
    public void save(final Writer w) throws IOException {
        this.underlyingXmlObject().save(w);
    }
    
    @Override
    @Deprecated
    public XMLInputStream newXMLInputStream(final XmlOptions options) {
        return this.underlyingXmlObject().newXMLInputStream(options);
    }
    
    @Override
    public XMLStreamReader newXMLStreamReader(final XmlOptions options) {
        return this.underlyingXmlObject().newXMLStreamReader(options);
    }
    
    @Override
    public String xmlText(final XmlOptions options) {
        return this.underlyingXmlObject().xmlText(options);
    }
    
    @Override
    public InputStream newInputStream(final XmlOptions options) {
        return this.underlyingXmlObject().newInputStream(options);
    }
    
    @Override
    public Reader newReader(final XmlOptions options) {
        return this.underlyingXmlObject().newReader(options);
    }
    
    @Override
    public Node newDomNode(final XmlOptions options) {
        return this.underlyingXmlObject().newDomNode(options);
    }
    
    @Override
    public void save(final ContentHandler ch, final LexicalHandler lh, final XmlOptions options) throws SAXException {
        this.underlyingXmlObject().save(ch, lh, options);
    }
    
    @Override
    public void save(final File file, final XmlOptions options) throws IOException {
        this.underlyingXmlObject().save(file, options);
    }
    
    @Override
    public void save(final OutputStream os, final XmlOptions options) throws IOException {
        this.underlyingXmlObject().save(os, options);
    }
    
    @Override
    public void save(final Writer w, final XmlOptions options) throws IOException {
        this.underlyingXmlObject().save(w, options);
    }
    
    @Override
    public SchemaType instanceType() {
        return ((SimpleValue)this.underlyingXmlObject()).instanceType();
    }
    
    @Override
    @Deprecated
    public String stringValue() {
        return ((SimpleValue)this.underlyingXmlObject()).stringValue();
    }
    
    @Override
    @Deprecated
    public boolean booleanValue() {
        return ((SimpleValue)this.underlyingXmlObject()).booleanValue();
    }
    
    @Override
    @Deprecated
    public byte byteValue() {
        return ((SimpleValue)this.underlyingXmlObject()).byteValue();
    }
    
    @Override
    @Deprecated
    public short shortValue() {
        return ((SimpleValue)this.underlyingXmlObject()).shortValue();
    }
    
    @Override
    @Deprecated
    public int intValue() {
        return ((SimpleValue)this.underlyingXmlObject()).intValue();
    }
    
    @Override
    @Deprecated
    public long longValue() {
        return ((SimpleValue)this.underlyingXmlObject()).longValue();
    }
    
    @Override
    @Deprecated
    public BigInteger bigIntegerValue() {
        return ((SimpleValue)this.underlyingXmlObject()).bigIntegerValue();
    }
    
    @Override
    @Deprecated
    public BigDecimal bigDecimalValue() {
        return ((SimpleValue)this.underlyingXmlObject()).bigDecimalValue();
    }
    
    @Override
    @Deprecated
    public float floatValue() {
        return ((SimpleValue)this.underlyingXmlObject()).floatValue();
    }
    
    @Override
    @Deprecated
    public double doubleValue() {
        return ((SimpleValue)this.underlyingXmlObject()).doubleValue();
    }
    
    @Override
    @Deprecated
    public byte[] byteArrayValue() {
        return ((SimpleValue)this.underlyingXmlObject()).byteArrayValue();
    }
    
    @Override
    @Deprecated
    public StringEnumAbstractBase enumValue() {
        return ((SimpleValue)this.underlyingXmlObject()).enumValue();
    }
    
    @Override
    @Deprecated
    public Calendar calendarValue() {
        return ((SimpleValue)this.underlyingXmlObject()).calendarValue();
    }
    
    @Override
    @Deprecated
    public Date dateValue() {
        return ((SimpleValue)this.underlyingXmlObject()).dateValue();
    }
    
    @Override
    @Deprecated
    public GDate gDateValue() {
        return ((SimpleValue)this.underlyingXmlObject()).gDateValue();
    }
    
    @Override
    @Deprecated
    public GDuration gDurationValue() {
        return ((SimpleValue)this.underlyingXmlObject()).gDurationValue();
    }
    
    @Override
    @Deprecated
    public QName qNameValue() {
        return ((SimpleValue)this.underlyingXmlObject()).qNameValue();
    }
    
    @Override
    @Deprecated
    public List listValue() {
        return ((SimpleValue)this.underlyingXmlObject()).listValue();
    }
    
    @Override
    @Deprecated
    public List xlistValue() {
        return ((SimpleValue)this.underlyingXmlObject()).xlistValue();
    }
    
    @Override
    @Deprecated
    public Object objectValue() {
        return ((SimpleValue)this.underlyingXmlObject()).objectValue();
    }
    
    @Override
    @Deprecated
    public void set(final String obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final boolean v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final byte v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final short v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final int v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final long v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final BigInteger obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final BigDecimal obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final float v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final double v) {
        ((SimpleValue)this.underlyingXmlObject()).set(v);
    }
    
    @Override
    @Deprecated
    public void set(final byte[] obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final StringEnumAbstractBase obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final Calendar obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final Date obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final GDateSpecification obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final GDurationSpecification obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final QName obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    @Deprecated
    public void set(final List obj) {
        ((SimpleValue)this.underlyingXmlObject()).set(obj);
    }
    
    @Override
    public String getStringValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getStringValue();
    }
    
    @Override
    public boolean getBooleanValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getBooleanValue();
    }
    
    @Override
    public byte getByteValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getByteValue();
    }
    
    @Override
    public short getShortValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getShortValue();
    }
    
    @Override
    public int getIntValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getIntValue();
    }
    
    @Override
    public long getLongValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getLongValue();
    }
    
    @Override
    public BigInteger getBigIntegerValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getBigIntegerValue();
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getBigDecimalValue();
    }
    
    @Override
    public float getFloatValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getFloatValue();
    }
    
    @Override
    public double getDoubleValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getDoubleValue();
    }
    
    @Override
    public byte[] getByteArrayValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getByteArrayValue();
    }
    
    @Override
    public StringEnumAbstractBase getEnumValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getEnumValue();
    }
    
    @Override
    public Calendar getCalendarValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getCalendarValue();
    }
    
    @Override
    public Date getDateValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getDateValue();
    }
    
    @Override
    public GDate getGDateValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getGDateValue();
    }
    
    @Override
    public GDuration getGDurationValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getGDurationValue();
    }
    
    @Override
    public QName getQNameValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getQNameValue();
    }
    
    @Override
    public List getListValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getListValue();
    }
    
    @Override
    public List xgetListValue() {
        return ((SimpleValue)this.underlyingXmlObject()).xgetListValue();
    }
    
    @Override
    public Object getObjectValue() {
        return ((SimpleValue)this.underlyingXmlObject()).getObjectValue();
    }
    
    @Override
    public void setStringValue(final String obj) {
        ((SimpleValue)this.underlyingXmlObject()).setStringValue(obj);
    }
    
    @Override
    public void setBooleanValue(final boolean v) {
        ((SimpleValue)this.underlyingXmlObject()).setBooleanValue(v);
    }
    
    @Override
    public void setByteValue(final byte v) {
        ((SimpleValue)this.underlyingXmlObject()).setByteValue(v);
    }
    
    @Override
    public void setShortValue(final short v) {
        ((SimpleValue)this.underlyingXmlObject()).setShortValue(v);
    }
    
    @Override
    public void setIntValue(final int v) {
        ((SimpleValue)this.underlyingXmlObject()).setIntValue(v);
    }
    
    @Override
    public void setLongValue(final long v) {
        ((SimpleValue)this.underlyingXmlObject()).setLongValue(v);
    }
    
    @Override
    public void setBigIntegerValue(final BigInteger obj) {
        ((SimpleValue)this.underlyingXmlObject()).setBigIntegerValue(obj);
    }
    
    @Override
    public void setBigDecimalValue(final BigDecimal obj) {
        ((SimpleValue)this.underlyingXmlObject()).setBigDecimalValue(obj);
    }
    
    @Override
    public void setFloatValue(final float v) {
        ((SimpleValue)this.underlyingXmlObject()).setFloatValue(v);
    }
    
    @Override
    public void setDoubleValue(final double v) {
        ((SimpleValue)this.underlyingXmlObject()).setDoubleValue(v);
    }
    
    @Override
    public void setByteArrayValue(final byte[] obj) {
        ((SimpleValue)this.underlyingXmlObject()).setByteArrayValue(obj);
    }
    
    @Override
    public void setEnumValue(final StringEnumAbstractBase obj) {
        ((SimpleValue)this.underlyingXmlObject()).setEnumValue(obj);
    }
    
    @Override
    public void setCalendarValue(final Calendar obj) {
        ((SimpleValue)this.underlyingXmlObject()).setCalendarValue(obj);
    }
    
    @Override
    public void setDateValue(final Date obj) {
        ((SimpleValue)this.underlyingXmlObject()).setDateValue(obj);
    }
    
    @Override
    public void setGDateValue(final GDate obj) {
        ((SimpleValue)this.underlyingXmlObject()).setGDateValue(obj);
    }
    
    @Override
    public void setGDurationValue(final GDuration obj) {
        ((SimpleValue)this.underlyingXmlObject()).setGDurationValue(obj);
    }
    
    @Override
    public void setQNameValue(final QName obj) {
        ((SimpleValue)this.underlyingXmlObject()).setQNameValue(obj);
    }
    
    @Override
    public void setListValue(final List obj) {
        ((SimpleValue)this.underlyingXmlObject()).setListValue(obj);
    }
    
    @Override
    public void setObjectValue(final Object obj) {
        ((SimpleValue)this.underlyingXmlObject()).setObjectValue(obj);
    }
    
    @Override
    @Deprecated
    public void objectSet(final Object obj) {
        ((SimpleValue)this.underlyingXmlObject()).objectSet(obj);
    }
    
    @Override
    public XmlObject[] selectChildren(final QName elementName) {
        return this.underlyingXmlObject().selectChildren(elementName);
    }
    
    @Override
    public XmlObject[] selectChildren(final String elementUri, final String elementLocalName) {
        return this.underlyingXmlObject().selectChildren(elementUri, elementLocalName);
    }
    
    @Override
    public XmlObject[] selectChildren(final QNameSet elementNameSet) {
        return this.underlyingXmlObject().selectChildren(elementNameSet);
    }
    
    @Override
    public XmlObject selectAttribute(final QName attributeName) {
        return this.underlyingXmlObject().selectAttribute(attributeName);
    }
    
    @Override
    public XmlObject selectAttribute(final String attributeUri, final String attributeLocalName) {
        return this.underlyingXmlObject().selectAttribute(attributeUri, attributeLocalName);
    }
    
    @Override
    public XmlObject[] selectAttributes(final QNameSet attributeNameSet) {
        return this.underlyingXmlObject().selectAttributes(attributeNameSet);
    }
}
