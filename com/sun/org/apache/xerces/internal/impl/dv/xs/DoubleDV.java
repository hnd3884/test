package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.xs.datatypes.XSDouble;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class DoubleDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 2552;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return new XDouble(content);
        }
        catch (final NumberFormatException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "double" });
        }
    }
    
    @Override
    public int compare(final Object value1, final Object value2) {
        return ((XDouble)value1).compareTo((XDouble)value2);
    }
    
    @Override
    public boolean isIdentical(final Object value1, final Object value2) {
        return value2 instanceof XDouble && ((XDouble)value1).isIdentical((XDouble)value2);
    }
    
    static boolean isPossibleFP(final String val) {
        for (int length = val.length(), i = 0; i < length; ++i) {
            final char c = val.charAt(i);
            if ((c < '0' || c > '9') && c != '.' && c != '-' && c != '+' && c != 'E' && c != 'e') {
                return false;
            }
        }
        return true;
    }
    
    private static final class XDouble implements XSDouble
    {
        private final double value;
        private String canonical;
        
        public XDouble(final String s) throws NumberFormatException {
            if (DoubleDV.isPossibleFP(s)) {
                this.value = Double.parseDouble(s);
            }
            else if (s.equals("INF")) {
                this.value = Double.POSITIVE_INFINITY;
            }
            else if (s.equals("-INF")) {
                this.value = Double.NEGATIVE_INFINITY;
            }
            else {
                if (!s.equals("NaN")) {
                    throw new NumberFormatException(s);
                }
                this.value = Double.NaN;
            }
        }
        
        @Override
        public boolean equals(final Object val) {
            if (val == this) {
                return true;
            }
            if (!(val instanceof XDouble)) {
                return false;
            }
            final XDouble oval = (XDouble)val;
            return this.value == oval.value || (this.value != this.value && oval.value != oval.value);
        }
        
        @Override
        public int hashCode() {
            if (this.value == 0.0) {
                return 0;
            }
            final long v = Double.doubleToLongBits(this.value);
            return (int)(v ^ v >>> 32);
        }
        
        public boolean isIdentical(final XDouble val) {
            if (val == this) {
                return true;
            }
            if (this.value == val.value) {
                return this.value != 0.0 || Double.doubleToLongBits(this.value) == Double.doubleToLongBits(val.value);
            }
            return this.value != this.value && val.value != val.value;
        }
        
        private int compareTo(final XDouble val) {
            final double oval = val.value;
            if (this.value < oval) {
                return -1;
            }
            if (this.value > oval) {
                return 1;
            }
            if (this.value == oval) {
                return 0;
            }
            if (this.value == this.value) {
                return 2;
            }
            if (oval != oval) {
                return 0;
            }
            return 2;
        }
        
        @Override
        public synchronized String toString() {
            if (this.canonical == null) {
                if (this.value == Double.POSITIVE_INFINITY) {
                    this.canonical = "INF";
                }
                else if (this.value == Double.NEGATIVE_INFINITY) {
                    this.canonical = "-INF";
                }
                else if (this.value != this.value) {
                    this.canonical = "NaN";
                }
                else if (this.value == 0.0) {
                    this.canonical = "0.0E1";
                }
                else {
                    this.canonical = Double.toString(this.value);
                    if (this.canonical.indexOf(69) == -1) {
                        int len = this.canonical.length();
                        final char[] chars = new char[len + 3];
                        this.canonical.getChars(0, len, chars, 0);
                        final int edp = (chars[0] == '-') ? 2 : 1;
                        if (this.value >= 1.0 || this.value <= -1.0) {
                            int i;
                            int dp;
                            for (dp = (i = this.canonical.indexOf(46)); i > edp; --i) {
                                chars[i] = chars[i - 1];
                            }
                            chars[edp] = '.';
                            while (chars[len - 1] == '0') {
                                --len;
                            }
                            if (chars[len - 1] == '.') {
                                ++len;
                            }
                            chars[len++] = 'E';
                            final int shift = dp - edp;
                            chars[len++] = (char)(shift + 48);
                        }
                        else {
                            int nzp;
                            for (nzp = edp + 1; chars[nzp] == '0'; ++nzp) {}
                            chars[edp - 1] = chars[nzp];
                            chars[edp] = '.';
                            for (int i = nzp + 1, j = edp + 1; i < len; ++i, ++j) {
                                chars[j] = chars[i];
                            }
                            len -= nzp - edp;
                            if (len == edp + 1) {
                                chars[len++] = '0';
                            }
                            chars[len++] = 'E';
                            chars[len++] = '-';
                            final int shift = nzp - edp;
                            chars[len++] = (char)(shift + 48);
                        }
                        this.canonical = new String(chars, 0, len);
                    }
                }
            }
            return this.canonical;
        }
        
        @Override
        public double getValue() {
            return this.value;
        }
    }
}
