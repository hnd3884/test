package org.apache.xerces.impl.dv.xs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public abstract class TypeValidator
{
    private static final boolean USE_CODE_POINT_COUNT_FOR_STRING_LENGTH;
    public static final short LESS_THAN = -1;
    public static final short EQUAL = 0;
    public static final short GREATER_THAN = 1;
    public static final short INDETERMINATE = 2;
    
    public abstract Object getActualValue(final String p0, final ValidationContext p1) throws InvalidDatatypeValueException;
    
    public void checkExtraRules(final Object o, final ValidationContext validationContext) throws InvalidDatatypeValueException {
    }
    
    public boolean isIdentical(final Object o, final Object o2) {
        return o.equals(o2);
    }
    
    public int compare(final Object o, final Object o2) {
        return -1;
    }
    
    public int getDataLength(final Object o) {
        if (!(o instanceof String)) {
            return -1;
        }
        final String s = (String)o;
        if (!TypeValidator.USE_CODE_POINT_COUNT_FOR_STRING_LENGTH) {
            return s.length();
        }
        return this.getCodePointLength(s);
    }
    
    public int getTotalDigits(final Object o) {
        return -1;
    }
    
    public int getFractionDigits(final Object o) {
        return -1;
    }
    
    private int getCodePointLength(final String s) {
        final int length = s.length();
        int n = 0;
        for (int i = 0; i < length - 1; ++i) {
            if (XMLChar.isHighSurrogate(s.charAt(i))) {
                if (XMLChar.isLowSurrogate(s.charAt(++i))) {
                    ++n;
                }
                else {
                    --i;
                }
            }
        }
        return length - n;
    }
    
    public static final boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    public static final int getDigit(final char c) {
        return isDigit(c) ? (c - '0') : -1;
    }
    
    public int getPrecision(final Object o) {
        return 0;
    }
    
    public boolean hasPrecision(final Object o) {
        return false;
    }
    
    public boolean hasTimeZone(final Object o) {
        return false;
    }
    
    static {
        USE_CODE_POINT_COUNT_FOR_STRING_LENGTH = (AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                try {
                    return Boolean.getBoolean("org.apache.xerces.impl.dv.xs.useCodePointCountForStringLength") ? Boolean.TRUE : Boolean.FALSE;
                }
                catch (final SecurityException ex) {
                    return Boolean.FALSE;
                }
            }
        }) == Boolean.TRUE);
    }
}
