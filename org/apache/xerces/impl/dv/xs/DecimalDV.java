package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.xerces.xs.datatypes.XSDecimal;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DecimalDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XDecimal(s);
        }
        catch (final NumberFormatException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "decimal" });
        }
    }
    
    public final int compare(final Object o, final Object o2) {
        return ((XDecimal)o).compareTo((XDecimal)o2);
    }
    
    public final int getTotalDigits(final Object o) {
        return ((XDecimal)o).totalDigits;
    }
    
    public final int getFractionDigits(final Object o) {
        return ((XDecimal)o).fracDigits;
    }
    
    static class XDecimal implements XSDecimal
    {
        int sign;
        int totalDigits;
        int intDigits;
        int fracDigits;
        String ivalue;
        String fvalue;
        boolean integer;
        private String canonical;
        
        XDecimal(final String s) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.integer = false;
            this.initD(s);
        }
        
        XDecimal(final String s, final boolean b) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.integer = false;
            if (b) {
                this.initI(s);
            }
            else {
                this.initD(s);
            }
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
                if (s.charAt(n4) != '.') {
                    throw new NumberFormatException();
                }
                n = n4 + 1;
                n2 = length;
            }
            if ((b ? 1 : 0) == n4 && n == n2) {
                throw new NumberFormatException();
            }
            while (n2 > n && s.charAt(n2 - 1) == '0') {
                --n2;
            }
            for (int i = n; i < n2; ++i) {
                if (!TypeValidator.isDigit(s.charAt(i))) {
                    throw new NumberFormatException();
                }
            }
            this.intDigits = n4 - n3;
            this.fracDigits = n2 - n;
            this.totalDigits = this.intDigits + this.fracDigits;
            if (this.intDigits > 0) {
                this.ivalue = s.substring(n3, n4);
                if (this.fracDigits > 0) {
                    this.fvalue = s.substring(n, n2);
                }
            }
            else if (this.fracDigits > 0) {
                this.fvalue = s.substring(n, n2);
            }
            else {
                this.sign = 0;
            }
        }
        
        void initI(final String s) throws NumberFormatException {
            final int length = s.length();
            if (length == 0) {
                throw new NumberFormatException();
            }
            boolean b = false;
            if (s.charAt(0) == '+') {
                b = true;
            }
            else if (s.charAt(0) == '-') {
                b = true;
                this.sign = -1;
            }
            int n;
            for (n = (b ? 1 : 0); n < length && s.charAt(n) == '0'; ++n) {}
            int n2;
            for (n2 = n; n2 < length && TypeValidator.isDigit(s.charAt(n2)); ++n2) {}
            if (n2 < length) {
                throw new NumberFormatException();
            }
            if ((b ? 1 : 0) == n2) {
                throw new NumberFormatException();
            }
            this.intDigits = n2 - n;
            this.fracDigits = 0;
            this.totalDigits = this.intDigits;
            if (this.intDigits > 0) {
                this.ivalue = s.substring(n, n2);
            }
            else {
                this.sign = 0;
            }
            this.integer = true;
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof XDecimal)) {
                return false;
            }
            final XDecimal xDecimal = (XDecimal)o;
            return this.sign == xDecimal.sign && (this.sign == 0 || (this.intDigits == xDecimal.intDigits && this.fracDigits == xDecimal.fracDigits && this.ivalue.equals(xDecimal.ivalue) && this.fvalue.equals(xDecimal.fvalue)));
        }
        
        public int compareTo(final XDecimal xDecimal) {
            if (this.sign != xDecimal.sign) {
                return (this.sign > xDecimal.sign) ? 1 : -1;
            }
            if (this.sign == 0) {
                return 0;
            }
            return this.sign * this.intComp(xDecimal);
        }
        
        private int intComp(final XDecimal xDecimal) {
            if (this.intDigits != xDecimal.intDigits) {
                return (this.intDigits > xDecimal.intDigits) ? 1 : -1;
            }
            final int compareTo = this.ivalue.compareTo(xDecimal.ivalue);
            if (compareTo != 0) {
                return (compareTo > 0) ? 1 : -1;
            }
            final int compareTo2 = this.fvalue.compareTo(xDecimal.fvalue);
            return (compareTo2 == 0) ? 0 : ((compareTo2 > 0) ? 1 : -1);
        }
        
        public synchronized String toString() {
            if (this.canonical == null) {
                this.makeCanonical();
            }
            return this.canonical;
        }
        
        private void makeCanonical() {
            if (this.sign == 0) {
                if (this.integer) {
                    this.canonical = "0";
                }
                else {
                    this.canonical = "0.0";
                }
                return;
            }
            if (this.integer && this.sign > 0) {
                this.canonical = this.ivalue;
                return;
            }
            final StringBuffer sb = new StringBuffer(this.totalDigits + 3);
            if (this.sign == -1) {
                sb.append('-');
            }
            if (this.intDigits != 0) {
                sb.append(this.ivalue);
            }
            else {
                sb.append('0');
            }
            if (!this.integer) {
                sb.append('.');
                if (this.fracDigits != 0) {
                    sb.append(this.fvalue);
                }
                else {
                    sb.append('0');
                }
            }
            this.canonical = sb.toString();
        }
        
        public BigDecimal getBigDecimal() {
            if (this.sign == 0) {
                return new BigDecimal(BigInteger.ZERO);
            }
            return new BigDecimal(this.toString());
        }
        
        public BigInteger getBigInteger() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return BigInteger.ZERO;
            }
            if (this.sign == 1) {
                return new BigInteger(this.ivalue);
            }
            return new BigInteger("-" + this.ivalue);
        }
        
        public long getLong() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0L;
            }
            if (this.sign == 1) {
                return Long.parseLong(this.ivalue);
            }
            return Long.parseLong("-" + this.ivalue);
        }
        
        public int getInt() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Integer.parseInt(this.ivalue);
            }
            return Integer.parseInt("-" + this.ivalue);
        }
        
        public short getShort() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Short.parseShort(this.ivalue);
            }
            return Short.parseShort("-" + this.ivalue);
        }
        
        public byte getByte() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Byte.parseByte(this.ivalue);
            }
            return Byte.parseByte("-" + this.ivalue);
        }
    }
}
