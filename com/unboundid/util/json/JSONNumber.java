package com.unboundid.util.json;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.math.BigDecimal;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONNumber extends JSONValue
{
    private static final long serialVersionUID = -9194944952299318254L;
    private final BigDecimal value;
    private final BigDecimal normalizedValue;
    private final String stringRepresentation;
    
    public JSONNumber(final long value) {
        this.value = new BigDecimal(value);
        this.normalizedValue = this.value;
        this.stringRepresentation = String.valueOf(value);
    }
    
    public JSONNumber(final double value) {
        this.value = new BigDecimal(value);
        this.normalizedValue = this.value;
        this.stringRepresentation = String.valueOf(value);
    }
    
    public JSONNumber(final BigDecimal value) {
        this.value = value;
        this.stringRepresentation = value.toPlainString();
        BigDecimal minimalValue;
        try {
            minimalValue = new BigDecimal(value.toBigIntegerExact());
        }
        catch (final Exception e) {
            minimalValue = value.stripTrailingZeros();
        }
        this.normalizedValue = minimalValue;
    }
    
    public JSONNumber(final String stringRepresentation) throws JSONException {
        this.stringRepresentation = stringRepresentation;
        final char[] chars = stringRepresentation.toCharArray();
        if (chars.length == 0) {
            throw new JSONException(JSONMessages.ERR_NUMBER_EMPTY_STRING.get());
        }
        if (!isDigit(chars[chars.length - 1])) {
            throw new JSONException(JSONMessages.ERR_NUMBER_LAST_CHAR_NOT_DIGIT.get(stringRepresentation));
        }
        int pos = 0;
        if (chars[0] == '-') {
            ++pos;
        }
        if (!isDigit(chars[pos])) {
            throw new JSONException(JSONMessages.ERR_NUMBER_ILLEGAL_CHAR.get(stringRepresentation, pos));
        }
        if (chars[pos++] == '0' && chars.length > pos && isDigit(chars[pos])) {
            throw new JSONException(JSONMessages.ERR_NUMBER_ILLEGAL_LEADING_ZERO.get(stringRepresentation));
        }
        boolean decimalFound = false;
        boolean eFound = false;
        while (pos < chars.length) {
            final char c = chars[pos];
            if (c == '.') {
                if (decimalFound) {
                    throw new JSONException(JSONMessages.ERR_NUMBER_MULTIPLE_DECIMAL_POINTS.get(stringRepresentation));
                }
                decimalFound = true;
                if (eFound) {
                    throw new JSONException(JSONMessages.ERR_NUMBER_DECIMAL_IN_EXPONENT.get(stringRepresentation));
                }
                if (!isDigit(chars[pos + 1])) {
                    throw new JSONException(JSONMessages.ERR_NUMBER_DECIMAL_NOT_FOLLOWED_BY_DIGIT.get(stringRepresentation));
                }
            }
            else if (c == 'e' || c == 'E') {
                if (eFound) {
                    throw new JSONException(JSONMessages.ERR_NUMBER_MULTIPLE_EXPONENTS.get(stringRepresentation));
                }
                eFound = true;
                if (chars[pos + 1] == '-' || chars[pos + 1] == '+') {
                    if (!isDigit(chars[pos + 2])) {
                        throw new JSONException(JSONMessages.ERR_NUMBER_EXPONENT_NOT_FOLLOWED_BY_DIGIT.get(stringRepresentation));
                    }
                    ++pos;
                }
                else if (!isDigit(chars[pos + 1])) {
                    throw new JSONException(JSONMessages.ERR_NUMBER_EXPONENT_NOT_FOLLOWED_BY_DIGIT.get(stringRepresentation));
                }
            }
            else if (!isDigit(chars[pos])) {
                throw new JSONException(JSONMessages.ERR_NUMBER_ILLEGAL_CHAR.get(stringRepresentation, pos));
            }
            ++pos;
        }
        try {
            this.value = new BigDecimal(stringRepresentation);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new JSONException(JSONMessages.ERR_NUMBER_CANNOT_PARSE.get(stringRepresentation, StaticUtils.getExceptionMessage(e)), e);
        }
        BigDecimal minimalValue;
        try {
            minimalValue = new BigDecimal(this.value.toBigIntegerExact());
        }
        catch (final Exception e2) {
            minimalValue = this.value.stripTrailingZeros();
        }
        this.normalizedValue = minimalValue;
    }
    
    private static boolean isDigit(final char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public BigDecimal getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return this.normalizedValue.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONNumber) {
            final JSONNumber n = (JSONNumber)o;
            return this.value.compareTo(n.value) == 0;
        }
        return false;
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONNumber && this.value.compareTo(((JSONNumber)v).value) == 0;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public String toSingleLineString() {
        return this.stringRepresentation;
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public String toNormalizedString() {
        return this.normalizedValue.toPlainString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer) {
        buffer.append(this.normalizedValue.toPlainString());
    }
    
    @Override
    public String toNormalizedString(final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return this.normalizedValue.toPlainString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        buffer.append(this.normalizedValue.toPlainString());
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.appendNumber(this.stringRepresentation);
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.appendNumber(fieldName, this.stringRepresentation);
    }
}
