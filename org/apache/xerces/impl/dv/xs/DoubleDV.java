package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.datatypes.XSDouble;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DoubleDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XDouble(s, validationContext.getTypeValidatorHelper() instanceof TypeValidatorHelper.TypeValidatorHelper1_1);
        }
        catch (final NumberFormatException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "double" });
        }
    }
    
    public int compare(final Object o, final Object o2) {
        return ((XDouble)o).compareTo((XDouble)o2);
    }
    
    public boolean isIdentical(final Object o, final Object o2) {
        return o2 instanceof XDouble && ((XDouble)o).isIdentical((XDouble)o2);
    }
    
    static boolean isPossibleFP(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if ((char1 < '0' || char1 > '9') && char1 != '.' && char1 != '-' && char1 != '+' && char1 != 'E' && char1 != 'e') {
                return false;
            }
        }
        return true;
    }
    
    private static final class XDouble implements XSDouble
    {
        private final double value;
        private String canonical;
        
        public XDouble(final String s, final boolean b) throws NumberFormatException {
            if (DoubleDV.isPossibleFP(s)) {
                this.value = Double.parseDouble(s);
            }
            else if (s.equals("INF") || (b && s.equals("+INF"))) {
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
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof XDouble)) {
                return false;
            }
            final XDouble xDouble = (XDouble)o;
            return this.value == xDouble.value || (this.value != this.value && xDouble.value != xDouble.value);
        }
        
        public int hashCode() {
            if (this.value == 0.0) {
                return 0;
            }
            final long doubleToLongBits = Double.doubleToLongBits(this.value);
            return (int)(doubleToLongBits ^ doubleToLongBits >>> 32);
        }
        
        public boolean isIdentical(final XDouble xDouble) {
            if (xDouble == this) {
                return true;
            }
            if (this.value == xDouble.value) {
                return this.value != 0.0 || Double.doubleToLongBits(this.value) == Double.doubleToLongBits(xDouble.value);
            }
            return this.value != this.value && xDouble.value != xDouble.value;
        }
        
        private int compareTo(final XDouble xDouble) {
            final double value = xDouble.value;
            if (this.value < value) {
                return -1;
            }
            if (this.value > value) {
                return 1;
            }
            if (this.value == value) {
                return 0;
            }
            if (this.value == this.value) {
                return 2;
            }
            if (value != value) {
                return 0;
            }
            return 2;
        }
        
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
                        int length = this.canonical.length();
                        final char[] array = new char[length + 3];
                        this.canonical.getChars(0, length, array, 0);
                        final int n = (array[0] == '-') ? 2 : 1;
                        if (this.value >= 1.0 || this.value <= -1.0) {
                            int i;
                            int n2;
                            for (n2 = (i = this.canonical.indexOf(46)); i > n; --i) {
                                array[i] = array[i - 1];
                            }
                            array[n] = '.';
                            while (array[length - 1] == '0') {
                                --length;
                            }
                            if (array[length - 1] == '.') {
                                ++length;
                            }
                            array[length++] = 'E';
                            array[length++] = (char)(n2 - n + 48);
                        }
                        else {
                            int n3;
                            for (n3 = n + 1; array[n3] == '0'; ++n3) {}
                            array[n - 1] = array[n3];
                            array[n] = '.';
                            for (int j = n3 + 1, n4 = n + 1; j < length; ++j, ++n4) {
                                array[n4] = array[j];
                            }
                            length -= n3 - n;
                            if (length == n + 1) {
                                array[length++] = '0';
                            }
                            array[length++] = 'E';
                            array[length++] = '-';
                            array[length++] = (char)(n3 - n + 48);
                        }
                        this.canonical = new String(array, 0, length);
                    }
                }
            }
            return this.canonical;
        }
        
        public double getValue() {
            return this.value;
        }
    }
}
