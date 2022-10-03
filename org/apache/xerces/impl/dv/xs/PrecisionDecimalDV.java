package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

class PrecisionDecimalDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XPrecisionDecimal(s);
        }
        catch (final NumberFormatException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "precisionDecimal" });
        }
    }
    
    public int compare(final Object o, final Object o2) {
        return ((XPrecisionDecimal)o).compareTo((XPrecisionDecimal)o2);
    }
    
    public int getTotalDigits(final Object o) {
        return ((XPrecisionDecimal)o).totalDigits;
    }
    
    public boolean isIdentical(final Object o, final Object o2) {
        return o2 instanceof XPrecisionDecimal && o instanceof XPrecisionDecimal && ((XPrecisionDecimal)o).isIdentical((XPrecisionDecimal)o2);
    }
    
    public int getPrecision(final Object o) {
        return ((XPrecisionDecimal)o).precision;
    }
    
    public boolean hasPrecision(final Object o) {
        final XPrecisionDecimal xPrecisionDecimal = (XPrecisionDecimal)o;
        return xPrecisionDecimal.sign != 0 && xPrecisionDecimal.ivalue != "INF" && xPrecisionDecimal.ivalue != "-INF";
    }
    
    private static class XPrecisionDecimal
    {
        int sign;
        int totalDigits;
        int intDigits;
        int fracDigits;
        int precision;
        String ivalue;
        String fvalue;
        int pvalue;
        private String canonical;
        
        XPrecisionDecimal(final String s) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.precision = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.pvalue = 0;
            if (s.equals("NaN")) {
                this.ivalue = "NaN";
                this.sign = 0;
                return;
            }
            if (s.equals("+INF") || s.equals("INF")) {
                this.ivalue = "INF";
                return;
            }
            if (s.equals("-INF")) {
                this.ivalue = "-INF";
                return;
            }
            this.initD(s);
        }
        
        void initD(final String s) throws NumberFormatException {
            final int length = s.length();
            if (length == 0) {
                throw new NumberFormatException();
            }
            boolean b = false;
            int n = 0;
            int n2 = 0;
            if (s.charAt(0) == '+') {
                b = true;
            }
            else if (s.charAt(0) == '-') {
                b = true;
                this.sign = -1;
            }
            int n3;
            for (n3 = (b ? 1 : 0); n3 < length && s.charAt(n3) == '0'; ++n3) {}
            int n4;
            for (n4 = n3; n4 < length && TypeValidator.isDigit(s.charAt(n4)); ++n4) {}
            if (n4 < length) {
                if (s.charAt(n4) != '.' && s.charAt(n4) != 'E' && s.charAt(n4) != 'e') {
                    throw new NumberFormatException();
                }
                if (s.charAt(n4) == '.') {
                    for (n = (n2 = n4 + 1); n2 < length && TypeValidator.isDigit(s.charAt(n2)); ++n2) {}
                    this.fracDigits = n2 - n;
                    if (this.fracDigits > 0) {
                        this.fvalue = s.substring(n, n2);
                    }
                    if (n2 < length) {
                        if (s.charAt(n2) != 'E' && s.charAt(n2) != 'e') {
                            throw new NumberFormatException();
                        }
                        if (s.charAt(n2 + 1) == '+') {
                            ++n2;
                        }
                        this.pvalue = Integer.parseInt(s.substring(n2 + 1, length));
                    }
                }
                else {
                    this.pvalue = Integer.parseInt(s.substring(n4 + 1 + ((s.charAt(n4 + 1) == '+') ? 1 : 0), length));
                }
            }
            if ((b ? 1 : 0) == n4 && n == n2) {
                throw new NumberFormatException();
            }
            this.intDigits = n4 - n3;
            if (this.intDigits > 0) {
                this.ivalue = s.substring(n3, n4);
                this.totalDigits = this.intDigits + this.fracDigits;
            }
            else {
                this.totalDigits = this.fracDigits;
                for (int n5 = 0; n5 < this.fracDigits && this.fvalue.charAt(n5) == '0'; ++n5, --this.totalDigits) {}
                if (this.totalDigits == 0) {
                    this.totalDigits = 1;
                }
            }
            this.precision = this.fracDigits - this.pvalue;
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof XPrecisionDecimal)) {
                return false;
            }
            final XPrecisionDecimal xPrecisionDecimal = (XPrecisionDecimal)o;
            return (this.sign == 0 && xPrecisionDecimal.sign == 0) || this.compareTo(xPrecisionDecimal) == 0;
        }
        
        private int compareFractionalPart(final XPrecisionDecimal xPrecisionDecimal) {
            if (this.fvalue.equals(xPrecisionDecimal.fvalue)) {
                return 0;
            }
            final StringBuffer sb = new StringBuffer(this.fvalue);
            final StringBuffer sb2 = new StringBuffer(xPrecisionDecimal.fvalue);
            this.truncateTrailingZeros(sb, sb2);
            return sb.toString().compareTo(sb2.toString());
        }
        
        private void truncateTrailingZeros(final StringBuffer sb, final StringBuffer sb2) {
            for (int n = sb.length() - 1; n >= 0 && sb.charAt(n) == '0'; --n) {
                sb.deleteCharAt(n);
            }
            for (int n2 = sb2.length() - 1; n2 >= 0 && sb2.charAt(n2) == '0'; --n2) {
                sb2.deleteCharAt(n2);
            }
        }
        
        public int compareTo(final XPrecisionDecimal xPrecisionDecimal) {
            if (this.sign == 0 || xPrecisionDecimal.sign == 0) {
                return 2;
            }
            if (this.ivalue.equals("INF") || xPrecisionDecimal.ivalue.equals("INF")) {
                if (this.ivalue.equals(xPrecisionDecimal.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("INF")) {
                    return 1;
                }
                return -1;
            }
            else if (this.ivalue.equals("-INF") || xPrecisionDecimal.ivalue.equals("-INF")) {
                if (this.ivalue.equals(xPrecisionDecimal.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("-INF")) {
                    return -1;
                }
                return 1;
            }
            else {
                if (this.sign == xPrecisionDecimal.sign) {
                    return this.sign * this.compare(xPrecisionDecimal);
                }
                if (this.isZero() && xPrecisionDecimal.isZero()) {
                    return 0;
                }
                return (this.sign > xPrecisionDecimal.sign) ? 1 : -1;
            }
        }
        
        private boolean isZero() {
            return this.totalDigits == 1 && this.intDigits == 0 && (this.fracDigits == 0 || this.fvalue.charAt(this.fracDigits - 1) == '0');
        }
        
        private int compare(final XPrecisionDecimal xPrecisionDecimal) {
            if (this.pvalue == xPrecisionDecimal.pvalue) {
                return this.intComp(xPrecisionDecimal);
            }
            if (this.pvalue > xPrecisionDecimal.pvalue) {
                final int n = this.pvalue - xPrecisionDecimal.pvalue;
                final StringBuffer sb = new StringBuffer(this.ivalue);
                final StringBuffer sb2 = new StringBuffer(this.fvalue);
                for (int i = 0; i < n; ++i) {
                    if (i < this.fracDigits) {
                        sb.append(this.fvalue.charAt(i));
                        sb2.deleteCharAt(0);
                    }
                    else {
                        sb.append('0');
                    }
                }
                while (sb.length() > 0 && sb.charAt(0) == '0') {
                    sb.deleteCharAt(0);
                }
                return this.compareDecimal(sb.toString(), sb2.toString(), xPrecisionDecimal.ivalue, xPrecisionDecimal.fvalue);
            }
            final int n2 = xPrecisionDecimal.pvalue - this.pvalue;
            final StringBuffer sb3 = new StringBuffer(xPrecisionDecimal.ivalue);
            final StringBuffer sb4 = new StringBuffer(xPrecisionDecimal.fvalue);
            for (int j = 0; j < n2; ++j) {
                if (j < xPrecisionDecimal.fracDigits) {
                    sb3.append(xPrecisionDecimal.fvalue.charAt(j));
                    sb4.deleteCharAt(0);
                }
                else {
                    sb3.append('0');
                }
            }
            while (sb3.length() > 0 && sb3.charAt(0) == '0') {
                sb3.deleteCharAt(0);
            }
            return this.compareDecimal(this.ivalue, this.fvalue, sb3.toString(), sb4.toString());
        }
        
        private int intComp(final XPrecisionDecimal xPrecisionDecimal) {
            if (this.intDigits != xPrecisionDecimal.intDigits) {
                return (this.intDigits > xPrecisionDecimal.intDigits) ? 1 : -1;
            }
            return this.compareDecimal(this.ivalue, this.fvalue, xPrecisionDecimal.ivalue, xPrecisionDecimal.fvalue);
        }
        
        private int compareDecimal(final String s, final String s2, final String s3, final String s4) {
            if (s.length() != s3.length()) {
                return (s.length() > s3.length()) ? 1 : -1;
            }
            final int compareTo = s.compareTo(s3);
            if (compareTo != 0) {
                return (compareTo > 0) ? 1 : -1;
            }
            if (s2.equals(s4)) {
                return 0;
            }
            final StringBuffer sb = new StringBuffer(s2);
            final StringBuffer sb2 = new StringBuffer(s4);
            this.truncateTrailingZeros(sb, sb2);
            final int compareTo2 = sb.toString().compareTo(sb2.toString());
            return (compareTo2 == 0) ? 0 : ((compareTo2 > 0) ? 1 : -1);
        }
        
        public synchronized String toString() {
            if (this.canonical == null) {
                this.makeCanonical();
            }
            return this.canonical;
        }
        
        private void makeCanonical() {
            if (this.ivalue.equals("INF") || this.ivalue.equals("-INF") || this.ivalue.equals("NaN")) {
                this.canonical = this.ivalue;
            }
            else {
                final StringBuffer sb = new StringBuffer();
                if (this.intDigits > 0) {
                    sb.append(this.ivalue);
                }
                if (this.fracDigits > 0) {
                    sb.append('.');
                    sb.append(this.fvalue);
                }
                if (this.pvalue != 0) {
                    sb.append('E');
                    sb.append(this.pvalue);
                }
                this.canonical = sb.toString();
            }
        }
        
        public boolean isIdentical(final XPrecisionDecimal xPrecisionDecimal) {
            return (this.ivalue.equals(xPrecisionDecimal.ivalue) && (this.ivalue.equals("INF") || this.ivalue.equals("-INF") || this.ivalue.equals("NaN"))) || (this.sign == xPrecisionDecimal.sign && this.intDigits == xPrecisionDecimal.intDigits && this.fracDigits == xPrecisionDecimal.fracDigits && this.pvalue == xPrecisionDecimal.pvalue && this.ivalue.equals(xPrecisionDecimal.ivalue) && this.fvalue.equals(xPrecisionDecimal.fvalue));
        }
    }
}
