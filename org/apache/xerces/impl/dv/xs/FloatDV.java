package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.xs.datatypes.XSFloat;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class FloatDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XFloat(s, validationContext.getTypeValidatorHelper() instanceof TypeValidatorHelper.TypeValidatorHelper1_1);
        }
        catch (final NumberFormatException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "float" });
        }
    }
    
    public int compare(final Object o, final Object o2) {
        return ((XFloat)o).compareTo((XFloat)o2);
    }
    
    public boolean isIdentical(final Object o, final Object o2) {
        return o2 instanceof XFloat && ((XFloat)o).isIdentical((XFloat)o2);
    }
    
    private static final class XFloat implements XSFloat
    {
        private final float value;
        private String canonical;
        
        public XFloat(final String s, final boolean b) throws NumberFormatException {
            if (DoubleDV.isPossibleFP(s)) {
                this.value = Float.parseFloat(s);
            }
            else if (s.equals("INF") || (b && s.equals("+INF"))) {
                this.value = Float.POSITIVE_INFINITY;
            }
            else if (s.equals("-INF")) {
                this.value = Float.NEGATIVE_INFINITY;
            }
            else {
                if (!s.equals("NaN")) {
                    throw new NumberFormatException(s);
                }
                this.value = Float.NaN;
            }
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof XFloat)) {
                return false;
            }
            final XFloat xFloat = (XFloat)o;
            return this.value == xFloat.value || (this.value != this.value && xFloat.value != xFloat.value);
        }
        
        public int hashCode() {
            return (this.value == 0.0f) ? 0 : Float.floatToIntBits(this.value);
        }
        
        public boolean isIdentical(final XFloat xFloat) {
            if (xFloat == this) {
                return true;
            }
            if (this.value == xFloat.value) {
                return this.value != 0.0f || Float.floatToIntBits(this.value) == Float.floatToIntBits(xFloat.value);
            }
            return this.value != this.value && xFloat.value != xFloat.value;
        }
        
        private int compareTo(final XFloat xFloat) {
            final float value = xFloat.value;
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
                if (this.value == Float.POSITIVE_INFINITY) {
                    this.canonical = "INF";
                }
                else if (this.value == Float.NEGATIVE_INFINITY) {
                    this.canonical = "-INF";
                }
                else if (this.value != this.value) {
                    this.canonical = "NaN";
                }
                else if (this.value == 0.0f) {
                    this.canonical = "0.0E1";
                }
                else {
                    this.canonical = Float.toString(this.value);
                    if (this.canonical.indexOf(69) == -1) {
                        int length = this.canonical.length();
                        final char[] array = new char[length + 3];
                        this.canonical.getChars(0, length, array, 0);
                        final int n = (array[0] == '-') ? 2 : 1;
                        if (this.value >= 1.0f || this.value <= -1.0f) {
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
        
        public float getValue() {
            return this.value;
        }
    }
}
