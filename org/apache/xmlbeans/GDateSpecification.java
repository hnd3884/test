package org.apache.xmlbeans;

import java.util.Date;
import java.math.BigDecimal;

public interface GDateSpecification
{
    public static final int HAS_TIMEZONE = 1;
    public static final int HAS_YEAR = 2;
    public static final int HAS_MONTH = 4;
    public static final int HAS_DAY = 8;
    public static final int HAS_TIME = 16;
    
    int getFlags();
    
    boolean isImmutable();
    
    boolean isValid();
    
    boolean hasTimeZone();
    
    boolean hasYear();
    
    boolean hasMonth();
    
    boolean hasDay();
    
    boolean hasTime();
    
    boolean hasDate();
    
    int getYear();
    
    int getMonth();
    
    int getDay();
    
    int getHour();
    
    int getMinute();
    
    int getSecond();
    
    int getTimeZoneSign();
    
    int getTimeZoneHour();
    
    int getTimeZoneMinute();
    
    BigDecimal getFraction();
    
    int getMillisecond();
    
    int getJulianDate();
    
    XmlCalendar getCalendar();
    
    Date getDate();
    
    int compareToGDate(final GDateSpecification p0);
    
    int getBuiltinTypeCode();
    
    String canonicalString();
    
    String toString();
}
