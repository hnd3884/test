package org.apache.xmlbeans;

import java.util.List;
import javax.xml.namespace.QName;
import java.util.Date;
import java.util.Calendar;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface SimpleValue extends XmlObject
{
    SchemaType instanceType();
    
    String getStringValue();
    
    boolean getBooleanValue();
    
    byte getByteValue();
    
    short getShortValue();
    
    int getIntValue();
    
    long getLongValue();
    
    BigInteger getBigIntegerValue();
    
    BigDecimal getBigDecimalValue();
    
    float getFloatValue();
    
    double getDoubleValue();
    
    byte[] getByteArrayValue();
    
    StringEnumAbstractBase getEnumValue();
    
    Calendar getCalendarValue();
    
    Date getDateValue();
    
    GDate getGDateValue();
    
    GDuration getGDurationValue();
    
    QName getQNameValue();
    
    List getListValue();
    
    List xgetListValue();
    
    Object getObjectValue();
    
    void setStringValue(final String p0);
    
    void setBooleanValue(final boolean p0);
    
    void setByteValue(final byte p0);
    
    void setShortValue(final short p0);
    
    void setIntValue(final int p0);
    
    void setLongValue(final long p0);
    
    void setBigIntegerValue(final BigInteger p0);
    
    void setBigDecimalValue(final BigDecimal p0);
    
    void setFloatValue(final float p0);
    
    void setDoubleValue(final double p0);
    
    void setByteArrayValue(final byte[] p0);
    
    void setEnumValue(final StringEnumAbstractBase p0);
    
    void setCalendarValue(final Calendar p0);
    
    void setDateValue(final Date p0);
    
    void setGDateValue(final GDate p0);
    
    void setGDurationValue(final GDuration p0);
    
    void setQNameValue(final QName p0);
    
    void setListValue(final List p0);
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    String stringValue();
    
    @Deprecated
    boolean booleanValue();
    
    @Deprecated
    byte byteValue();
    
    @Deprecated
    short shortValue();
    
    @Deprecated
    int intValue();
    
    @Deprecated
    long longValue();
    
    @Deprecated
    BigInteger bigIntegerValue();
    
    @Deprecated
    BigDecimal bigDecimalValue();
    
    @Deprecated
    float floatValue();
    
    @Deprecated
    double doubleValue();
    
    @Deprecated
    byte[] byteArrayValue();
    
    @Deprecated
    StringEnumAbstractBase enumValue();
    
    @Deprecated
    Calendar calendarValue();
    
    @Deprecated
    Date dateValue();
    
    @Deprecated
    GDate gDateValue();
    
    @Deprecated
    GDuration gDurationValue();
    
    @Deprecated
    QName qNameValue();
    
    @Deprecated
    List listValue();
    
    @Deprecated
    List xlistValue();
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void set(final String p0);
    
    @Deprecated
    void set(final boolean p0);
    
    @Deprecated
    void set(final byte p0);
    
    @Deprecated
    void set(final short p0);
    
    @Deprecated
    void set(final int p0);
    
    @Deprecated
    void set(final long p0);
    
    @Deprecated
    void set(final BigInteger p0);
    
    @Deprecated
    void set(final BigDecimal p0);
    
    @Deprecated
    void set(final float p0);
    
    @Deprecated
    void set(final double p0);
    
    @Deprecated
    void set(final byte[] p0);
    
    @Deprecated
    void set(final StringEnumAbstractBase p0);
    
    @Deprecated
    void set(final Calendar p0);
    
    @Deprecated
    void set(final Date p0);
    
    @Deprecated
    void set(final GDateSpecification p0);
    
    @Deprecated
    void set(final GDurationSpecification p0);
    
    @Deprecated
    void set(final QName p0);
    
    @Deprecated
    void set(final List p0);
    
    @Deprecated
    void objectSet(final Object p0);
}
