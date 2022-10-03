package org.apache.xerces.jaxp.datatype;

import java.util.GregorianCalendar;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeFactory;

public class DatatypeFactoryImpl extends DatatypeFactory
{
    public Duration newDuration(final String s) {
        return new DurationImpl(s);
    }
    
    public Duration newDuration(final long n) {
        return new DurationImpl(n);
    }
    
    public Duration newDuration(final boolean b, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigDecimal bigDecimal) {
        return new DurationImpl(b, bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigDecimal);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendar() {
        return new XMLGregorianCalendarImpl();
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendar(final String s) {
        return new XMLGregorianCalendarImpl(s);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendar(final GregorianCalendar gregorianCalendar) {
        return new XMLGregorianCalendarImpl(gregorianCalendar);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendar(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        return XMLGregorianCalendarImpl.createDateTime(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendar(final BigInteger bigInteger, final int n, final int n2, final int n3, final int n4, final int n5, final BigDecimal bigDecimal, final int n6) {
        return new XMLGregorianCalendarImpl(bigInteger, n, n2, n3, n4, n5, bigDecimal, n6);
    }
}
