package javax.xml.datatype;

import java.util.GregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class DatatypeFactory
{
    public static final String DATATYPEFACTORY_PROPERTY = "javax.xml.datatype.DatatypeFactory";
    public static final String DATATYPEFACTORY_IMPLEMENTATION_CLASS;
    
    protected DatatypeFactory() {
    }
    
    public static DatatypeFactory newInstance() throws DatatypeConfigurationException {
        try {
            return (DatatypeFactory)FactoryFinder.find("javax.xml.datatype.DatatypeFactory", DatatypeFactory.DATATYPEFACTORY_IMPLEMENTATION_CLASS);
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new DatatypeConfigurationException(configurationError.getMessage(), configurationError.getException());
        }
    }
    
    public static DatatypeFactory newInstance(final String s, ClassLoader contextClassLoader) throws DatatypeConfigurationException {
        if (s == null) {
            throw new DatatypeConfigurationException("factoryClassName cannot be null.");
        }
        if (contextClassLoader == null) {
            contextClassLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (DatatypeFactory)FactoryFinder.newInstance(s, contextClassLoader);
        }
        catch (final FactoryFinder.ConfigurationError configurationError) {
            throw new DatatypeConfigurationException(configurationError.getMessage(), configurationError.getException());
        }
    }
    
    public abstract Duration newDuration(final String p0);
    
    public abstract Duration newDuration(final long p0);
    
    public abstract Duration newDuration(final boolean p0, final BigInteger p1, final BigInteger p2, final BigInteger p3, final BigInteger p4, final BigInteger p5, final BigDecimal p6);
    
    public Duration newDuration(final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return this.newDuration(b, (n != Integer.MIN_VALUE) ? BigInteger.valueOf(n) : null, (n2 != Integer.MIN_VALUE) ? BigInteger.valueOf(n2) : null, (n3 != Integer.MIN_VALUE) ? BigInteger.valueOf(n3) : null, (n4 != Integer.MIN_VALUE) ? BigInteger.valueOf(n4) : null, (n5 != Integer.MIN_VALUE) ? BigInteger.valueOf(n5) : null, (n6 != Integer.MIN_VALUE) ? BigDecimal.valueOf(n6) : null);
    }
    
    public Duration newDurationDayTime(final String s) {
        if (s == null) {
            throw new NullPointerException("The lexical representation cannot be null.");
        }
        final int index = s.indexOf(84);
        for (int n = (index >= 0) ? index : s.length(), i = 0; i < n; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == 'Y' || char1 == 'M') {
                throw new IllegalArgumentException("Invalid dayTimeDuration value: " + s);
            }
        }
        return this.newDuration(s);
    }
    
    public Duration newDurationDayTime(final long n) {
        long n2 = n;
        if (n2 == 0L) {
            return this.newDuration(true, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0);
        }
        boolean b = false;
        boolean b2;
        if (n2 < 0L) {
            b2 = false;
            if (n2 == Long.MIN_VALUE) {
                ++n2;
                b = true;
            }
            n2 *= -1L;
        }
        else {
            b2 = true;
        }
        final long n3 = n2;
        int n4 = (int)(n3 % 60000L);
        if (b) {
            ++n4;
        }
        if (n4 % 1000 != 0) {
            final BigDecimal value = BigDecimal.valueOf(n4, 3);
            final long n5 = n3 / 60000L;
            final BigInteger value2 = BigInteger.valueOf(n5 % 60L);
            final long n6 = n5 / 60L;
            return this.newDuration(b2, null, null, BigInteger.valueOf(n6 / 24L), BigInteger.valueOf(n6 % 24L), value2, value);
        }
        final int n7 = n4 / 1000;
        final long n8 = n3 / 60000L;
        final int n9 = (int)(n8 % 60L);
        final long n10 = n8 / 60L;
        final int n11 = (int)(n10 % 24L);
        final long n12 = n10 / 24L;
        if (n12 <= 2147483647L) {
            return this.newDuration(b2, Integer.MIN_VALUE, Integer.MIN_VALUE, (int)n12, n11, n9, n7);
        }
        return this.newDuration(b2, null, null, BigInteger.valueOf(n12), BigInteger.valueOf(n11), BigInteger.valueOf(n9), BigDecimal.valueOf(n4, 3));
    }
    
    public Duration newDurationDayTime(final boolean b, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return this.newDuration(b, null, null, bigInteger, bigInteger2, bigInteger3, (bigInteger4 != null) ? new BigDecimal(bigInteger4) : null);
    }
    
    public Duration newDurationDayTime(final boolean b, final int n, final int n2, final int n3, final int n4) {
        return this.newDuration(b, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, n4);
    }
    
    public Duration newDurationYearMonth(final String s) {
        if (s == null) {
            throw new NullPointerException("The lexical representation cannot be null.");
        }
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == 'D' || char1 == 'T') {
                throw new IllegalArgumentException("Invalid yearMonthDuration value: " + s);
            }
        }
        return this.newDuration(s);
    }
    
    public Duration newDurationYearMonth(final long n) {
        return this.newDuration(n);
    }
    
    public Duration newDurationYearMonth(final boolean b, final BigInteger bigInteger, final BigInteger bigInteger2) {
        return this.newDuration(b, bigInteger, bigInteger2, null, null, null, null);
    }
    
    public Duration newDurationYearMonth(final boolean b, final int n, final int n2) {
        return this.newDuration(b, n, n2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    public abstract XMLGregorianCalendar newXMLGregorianCalendar();
    
    public abstract XMLGregorianCalendar newXMLGregorianCalendar(final String p0);
    
    public abstract XMLGregorianCalendar newXMLGregorianCalendar(final GregorianCalendar p0);
    
    public abstract XMLGregorianCalendar newXMLGregorianCalendar(final BigInteger p0, final int p1, final int p2, final int p3, final int p4, final int p5, final BigDecimal p6, final int p7);
    
    public XMLGregorianCalendar newXMLGregorianCalendar(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        final BigInteger bigInteger = (n != Integer.MIN_VALUE) ? BigInteger.valueOf(n) : null;
        BigDecimal value = null;
        if (n7 != Integer.MIN_VALUE) {
            if (n7 < 0 || n7 > 1000) {
                throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond, int timezone)with invalid millisecond: " + n7);
            }
            value = BigDecimal.valueOf(n7, 3);
        }
        return this.newXMLGregorianCalendar(bigInteger, n2, n3, n4, n5, n6, value, n8);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendarDate(final int n, final int n2, final int n3, final int n4) {
        return this.newXMLGregorianCalendar(n, n2, n3, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n4);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendarTime(final int n, final int n2, final int n3, final int n4) {
        return this.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, Integer.MIN_VALUE, n4);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendarTime(final int n, final int n2, final int n3, final BigDecimal bigDecimal, final int n4) {
        return this.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, bigDecimal, n4);
    }
    
    public XMLGregorianCalendar newXMLGregorianCalendarTime(final int n, final int n2, final int n3, final int n4, final int n5) {
        BigDecimal value = null;
        if (n4 != Integer.MIN_VALUE) {
            if (n4 < 0 || n4 > 1000) {
                throw new IllegalArgumentException("javax.xml.datatype.DatatypeFactory#newXMLGregorianCalendarTime(int hours, int minutes, int seconds, int milliseconds, int timezone)with invalid milliseconds: " + n4);
            }
            value = BigDecimal.valueOf(n4, 3);
        }
        return this.newXMLGregorianCalendarTime(n, n2, n3, value, n5);
    }
    
    static {
        DATATYPEFACTORY_IMPLEMENTATION_CLASS = new String("org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl");
    }
}
