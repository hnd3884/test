package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

class PrecisionDecimalDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 4088;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return new XPrecisionDecimal(content);
        }
        catch (final NumberFormatException nfe) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "precisionDecimal" });
        }
    }
    
    @Override
    public int compare(final Object value1, final Object value2) {
        return ((XPrecisionDecimal)value1).compareTo((XPrecisionDecimal)value2);
    }
    
    @Override
    public int getFractionDigits(final Object value) {
        return ((XPrecisionDecimal)value).fracDigits;
    }
    
    @Override
    public int getTotalDigits(final Object value) {
        return ((XPrecisionDecimal)value).totalDigits;
    }
    
    @Override
    public boolean isIdentical(final Object value1, final Object value2) {
        return value2 instanceof XPrecisionDecimal && value1 instanceof XPrecisionDecimal && ((XPrecisionDecimal)value1).isIdentical((XPrecisionDecimal)value2);
    }
    
    static final class XPrecisionDecimal
    {
        int sign;
        int totalDigits;
        int intDigits;
        int fracDigits;
        String ivalue;
        String fvalue;
        int pvalue;
        private String canonical;
        
        XPrecisionDecimal(final String content) throws NumberFormatException {
            this.sign = 1;
            this.totalDigits = 0;
            this.intDigits = 0;
            this.fracDigits = 0;
            this.ivalue = "";
            this.fvalue = "";
            this.pvalue = 0;
            if (content.equals("NaN")) {
                this.ivalue = content;
                this.sign = 0;
            }
            if (content.equals("+INF") || content.equals("INF") || content.equals("-INF")) {
                this.ivalue = ((content.charAt(0) == '+') ? content.substring(1) : content);
                return;
            }
            this.initD(content);
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
                if (content.charAt(intEnd) != '.' && content.charAt(intEnd) != 'E' && content.charAt(intEnd) != 'e') {
                    throw new NumberFormatException();
                }
                if (content.charAt(intEnd) == '.') {
                    for (fracStart = (fracEnd = intEnd + 1); fracEnd < len && TypeValidator.isDigit(content.charAt(fracEnd)); ++fracEnd) {}
                }
                else {
                    this.pvalue = Integer.parseInt(content.substring(intEnd + 1, len));
                }
            }
            if (intStart == intEnd && fracStart == fracEnd) {
                throw new NumberFormatException();
            }
            for (int fracPos = fracStart; fracPos < fracEnd; ++fracPos) {
                if (!TypeValidator.isDigit(content.charAt(fracPos))) {
                    throw new NumberFormatException();
                }
            }
            this.intDigits = intEnd - actualIntStart;
            this.fracDigits = fracEnd - fracStart;
            if (this.intDigits > 0) {
                this.ivalue = content.substring(actualIntStart, intEnd);
            }
            if (this.fracDigits > 0) {
                this.fvalue = content.substring(fracStart, fracEnd);
                if (fracEnd < len) {
                    this.pvalue = Integer.parseInt(content.substring(fracEnd + 1, len));
                }
            }
            this.totalDigits = this.intDigits + this.fracDigits;
        }
        
        private static String canonicalToStringForHashCode(final String ivalue, final String fvalue, final int sign, final int pvalue) {
            if ("NaN".equals(ivalue)) {
                return "NaN";
            }
            if ("INF".equals(ivalue)) {
                return (sign < 0) ? "-INF" : "INF";
            }
            final StringBuilder builder = new StringBuilder();
            final int ilen = ivalue.length();
            int lastNonZero;
            for (int flen0 = lastNonZero = fvalue.length(); lastNonZero > 0 && fvalue.charAt(lastNonZero - 1) == '0'; --lastNonZero) {}
            final int flen2 = lastNonZero;
            int exponent = pvalue;
            int iStart;
            for (iStart = 0; iStart < ilen && ivalue.charAt(iStart) == '0'; ++iStart) {}
            int fStart = 0;
            if (iStart < ivalue.length()) {
                builder.append((sign == -1) ? "-" : "");
                builder.append(ivalue.charAt(iStart));
                ++iStart;
            }
            else {
                if (flen2 <= 0) {
                    return "0";
                }
                for (fStart = 0; fStart < flen2 && fvalue.charAt(fStart) == '0'; ++fStart) {}
                if (fStart >= flen2) {
                    return "0";
                }
                builder.append((sign == -1) ? "-" : "");
                builder.append(fvalue.charAt(fStart));
                exponent -= ++fStart;
            }
            if (iStart < ilen || fStart < flen2) {
                builder.append('.');
            }
            while (iStart < ilen) {
                builder.append(ivalue.charAt(iStart++));
                ++exponent;
            }
            while (fStart < flen2) {
                builder.append(fvalue.charAt(fStart++));
            }
            if (exponent != 0) {
                builder.append("E").append(exponent);
            }
            return builder.toString();
        }
        
        @Override
        public boolean equals(final Object val) {
            if (val == this) {
                return true;
            }
            if (!(val instanceof XPrecisionDecimal)) {
                return false;
            }
            final XPrecisionDecimal oval = (XPrecisionDecimal)val;
            return this.compareTo(oval) == 0;
        }
        
        @Override
        public int hashCode() {
            return canonicalToStringForHashCode(this.ivalue, this.fvalue, this.sign, this.pvalue).hashCode();
        }
        
        private int compareFractionalPart(final XPrecisionDecimal oval) {
            if (this.fvalue.equals(oval.fvalue)) {
                return 0;
            }
            final StringBuffer temp1 = new StringBuffer(this.fvalue);
            final StringBuffer temp2 = new StringBuffer(oval.fvalue);
            this.truncateTrailingZeros(temp1, temp2);
            return temp1.toString().compareTo(temp2.toString());
        }
        
        private void truncateTrailingZeros(final StringBuffer fValue, final StringBuffer otherFValue) {
            for (int i = fValue.length() - 1; i >= 0 && fValue.charAt(i) == '0'; --i) {
                fValue.deleteCharAt(i);
            }
            for (int i = otherFValue.length() - 1; i >= 0 && otherFValue.charAt(i) == '0'; --i) {
                otherFValue.deleteCharAt(i);
            }
        }
        
        public int compareTo(final XPrecisionDecimal val) {
            if (this.sign == 0) {
                return 2;
            }
            if (this.ivalue.equals("INF") || val.ivalue.equals("INF")) {
                if (this.ivalue.equals(val.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("INF")) {
                    return 1;
                }
                return -1;
            }
            else if (this.ivalue.equals("-INF") || val.ivalue.equals("-INF")) {
                if (this.ivalue.equals(val.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("-INF")) {
                    return -1;
                }
                return 1;
            }
            else {
                if (this.sign != val.sign) {
                    return (this.sign > val.sign) ? 1 : -1;
                }
                return this.sign * this.compare(val);
            }
        }
        
        private int compare(final XPrecisionDecimal val) {
            if (this.pvalue == 0 && val.pvalue == 0) {
                return this.intComp(val);
            }
            if (this.pvalue == val.pvalue) {
                return this.intComp(val);
            }
            if (this.intDigits + this.pvalue != val.intDigits + val.pvalue) {
                return (this.intDigits + this.pvalue > val.intDigits + val.pvalue) ? 1 : -1;
            }
            if (this.pvalue > val.pvalue) {
                final int expDiff = this.pvalue - val.pvalue;
                final StringBuffer buffer = new StringBuffer(this.ivalue);
                final StringBuffer fbuffer = new StringBuffer(this.fvalue);
                for (int i = 0; i < expDiff; ++i) {
                    if (i < this.fracDigits) {
                        buffer.append(this.fvalue.charAt(i));
                        fbuffer.deleteCharAt(i);
                    }
                    else {
                        buffer.append('0');
                    }
                }
                return this.compareDecimal(buffer.toString(), val.ivalue, fbuffer.toString(), val.fvalue);
            }
            final int expDiff = val.pvalue - this.pvalue;
            final StringBuffer buffer = new StringBuffer(val.ivalue);
            final StringBuffer fbuffer = new StringBuffer(val.fvalue);
            for (int i = 0; i < expDiff; ++i) {
                if (i < val.fracDigits) {
                    buffer.append(val.fvalue.charAt(i));
                    fbuffer.deleteCharAt(i);
                }
                else {
                    buffer.append('0');
                }
            }
            return this.compareDecimal(this.ivalue, buffer.toString(), this.fvalue, fbuffer.toString());
        }
        
        private int intComp(final XPrecisionDecimal val) {
            if (this.intDigits != val.intDigits) {
                return (this.intDigits > val.intDigits) ? 1 : -1;
            }
            return this.compareDecimal(this.ivalue, val.ivalue, this.fvalue, val.fvalue);
        }
        
        private int compareDecimal(final String iValue, final String fValue, final String otherIValue, final String otherFValue) {
            int ret = iValue.compareTo(otherIValue);
            if (ret != 0) {
                return (ret > 0) ? 1 : -1;
            }
            if (fValue.equals(otherFValue)) {
                return 0;
            }
            final StringBuffer temp1 = new StringBuffer(fValue);
            final StringBuffer temp2 = new StringBuffer(otherFValue);
            this.truncateTrailingZeros(temp1, temp2);
            ret = temp1.toString().compareTo(temp2.toString());
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
            this.canonical = "TBD by Working Group";
        }
        
        public boolean isIdentical(final XPrecisionDecimal decimal) {
            return (this.ivalue.equals(decimal.ivalue) && (this.ivalue.equals("INF") || this.ivalue.equals("-INF") || this.ivalue.equals("NaN"))) || (this.sign == decimal.sign && this.intDigits == decimal.intDigits && this.fracDigits == decimal.fracDigits && this.pvalue == decimal.pvalue && this.ivalue.equals(decimal.ivalue) && this.fvalue.equals(decimal.fvalue));
        }
    }
}
