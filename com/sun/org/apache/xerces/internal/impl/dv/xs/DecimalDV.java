package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Objects;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class DecimalDV extends TypeValidator
{
    @Override
    public final short getAllowedFacets() {
        return 4088;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return new XDecimal(content);
        }
        catch (final NumberFormatException nfe) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "decimal" });
        }
    }
    
    @Override
    public final int compare(final Object value1, final Object value2) {
        return ((XDecimal)value1).compareTo((XDecimal)value2);
    }
    
    @Override
    public final int getTotalDigits(final Object value) {
        return ((XDecimal)value).totalDigits;
    }
    
    @Override
    public final int getFractionDigits(final Object value) {
        return ((XDecimal)value).fracDigits;
    }
    
    static final class XDecimal implements XSDecimal
    {
        int sign;
        int totalDigits;
        int intDigits;
        int fracDigits;
        String ivalue;
        String fvalue;
        boolean integer;
        private String canonical;
        
        XDecimal(final String content) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.integer = false;
            this.initD(content);
        }
        
        XDecimal(final String content, final boolean integer) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.integer = false;
            if (integer) {
                this.initI(content);
            }
            else {
                this.initD(content);
            }
        }
        
        void initD(final String content) throws NumberFormatException {
            final int len = content.length();
            if (len == 0) {
                throw new NumberFormatException();
            }
            int intStart = 0;
            int intEnd = 0;
            int fracStart = 0;
            int fracEnd = 0;
            if (content.charAt(0) == '+') {
                intStart = 1;
            }
            else if (content.charAt(0) == '-') {
                intStart = 1;
                this.sign = -1;
            }
            int actualIntStart;
            for (actualIntStart = intStart; actualIntStart < len && content.charAt(actualIntStart) == '0'; ++actualIntStart) {}
            for (intEnd = actualIntStart; intEnd < len && TypeValidator.isDigit(content.charAt(intEnd)); ++intEnd) {}
            if (intEnd < len) {
                if (content.charAt(intEnd) != '.') {
                    throw new NumberFormatException();
                }
                fracStart = intEnd + 1;
                fracEnd = len;
            }
            if (intStart == intEnd && fracStart == fracEnd) {
                throw new NumberFormatException();
            }
            while (fracEnd > fracStart && content.charAt(fracEnd - 1) == '0') {
                --fracEnd;
            }
            for (int fracPos = fracStart; fracPos < fracEnd; ++fracPos) {
                if (!TypeValidator.isDigit(content.charAt(fracPos))) {
                    throw new NumberFormatException();
                }
            }
            this.intDigits = intEnd - actualIntStart;
            this.fracDigits = fracEnd - fracStart;
            this.totalDigits = this.intDigits + this.fracDigits;
            if (this.intDigits > 0) {
                this.ivalue = content.substring(actualIntStart, intEnd);
                if (this.fracDigits > 0) {
                    this.fvalue = content.substring(fracStart, fracEnd);
                }
            }
            else if (this.fracDigits > 0) {
                this.fvalue = content.substring(fracStart, fracEnd);
            }
            else {
                this.sign = 0;
            }
        }
        
        void initI(final String content) throws NumberFormatException {
            final int len = content.length();
            if (len == 0) {
                throw new NumberFormatException();
            }
            int intStart = 0;
            int intEnd = 0;
            if (content.charAt(0) == '+') {
                intStart = 1;
            }
            else if (content.charAt(0) == '-') {
                intStart = 1;
                this.sign = -1;
            }
            int actualIntStart;
            for (actualIntStart = intStart; actualIntStart < len && content.charAt(actualIntStart) == '0'; ++actualIntStart) {}
            for (intEnd = actualIntStart; intEnd < len && TypeValidator.isDigit(content.charAt(intEnd)); ++intEnd) {}
            if (intEnd < len) {
                throw new NumberFormatException();
            }
            if (intStart == intEnd) {
                throw new NumberFormatException();
            }
            this.intDigits = intEnd - actualIntStart;
            this.fracDigits = 0;
            this.totalDigits = this.intDigits;
            if (this.intDigits > 0) {
                this.ivalue = content.substring(actualIntStart, intEnd);
            }
            else {
                this.sign = 0;
            }
            this.integer = true;
        }
        
        @Override
        public boolean equals(final Object val) {
            if (val == this) {
                return true;
            }
            if (!(val instanceof XDecimal)) {
                return false;
            }
            final XDecimal oval = (XDecimal)val;
            return this.sign == oval.sign && (this.sign == 0 || (this.intDigits == oval.intDigits && this.fracDigits == oval.fracDigits && this.ivalue.equals(oval.ivalue) && this.fvalue.equals(oval.fvalue)));
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this.sign;
            if (this.sign == 0) {
                return hash;
            }
            hash = 17 * hash + this.intDigits;
            hash = 17 * hash + this.fracDigits;
            hash = 17 * hash + Objects.hashCode(this.ivalue);
            hash = 17 * hash + Objects.hashCode(this.fvalue);
            return hash;
        }
        
        public int compareTo(final XDecimal val) {
            if (this.sign != val.sign) {
                return (this.sign > val.sign) ? 1 : -1;
            }
            if (this.sign == 0) {
                return 0;
            }
            return this.sign * this.intComp(val);
        }
        
        private int intComp(final XDecimal val) {
            if (this.intDigits != val.intDigits) {
                return (this.intDigits > val.intDigits) ? 1 : -1;
            }
            int ret = this.ivalue.compareTo(val.ivalue);
            if (ret != 0) {
                return (ret > 0) ? 1 : -1;
            }
            ret = this.fvalue.compareTo(val.fvalue);
            return (ret == 0) ? 0 : ((ret > 0) ? 1 : -1);
        }
        
        @Override
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
            final StringBuilder buffer = new StringBuilder(this.totalDigits + 3);
            if (this.sign == -1) {
                buffer.append('-');
            }
            if (this.intDigits != 0) {
                buffer.append(this.ivalue);
            }
            else {
                buffer.append('0');
            }
            if (!this.integer) {
                buffer.append('.');
                if (this.fracDigits != 0) {
                    buffer.append(this.fvalue);
                }
                else {
                    buffer.append('0');
                }
            }
            this.canonical = buffer.toString();
        }
        
        @Override
        public BigDecimal getBigDecimal() {
            if (this.sign == 0) {
                return new BigDecimal(BigInteger.ZERO);
            }
            return new BigDecimal(this.toString());
        }
        
        @Override
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
        
        @Override
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
        
        @Override
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
        
        @Override
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
        
        @Override
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
