package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.math.BigInteger;
import java.math.BigDecimal;

public class JavaDecimalHolder extends XmlObjectBase
{
    private BigDecimal _value;
    private static BigInteger _maxlong;
    private static BigInteger _minlong;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_DECIMAL;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return XsTypeConverter.printDecimal(this._value);
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet()) {
            validateLexical(s, JavaDecimalHolder._voorVc);
        }
        try {
            this.set_BigDecimal(new BigDecimal(s));
        }
        catch (final NumberFormatException e) {
            JavaDecimalHolder._voorVc.invalid("decimal", new Object[] { s });
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    public static void validateLexical(final String v, final ValidationContext context) {
        final int l = v.length();
        int i = 0;
        if (i < l) {
            final int ch = v.charAt(i);
            if (ch == 43 || ch == 45) {
                ++i;
            }
        }
        boolean sawDot = false;
        boolean sawDigit = false;
        while (i < l) {
            final int ch2 = v.charAt(i);
            if (ch2 == 46) {
                if (sawDot) {
                    context.invalid("decimal", new Object[] { "saw '.' more than once: " + v });
                    return;
                }
                sawDot = true;
            }
            else {
                if (ch2 < 48 || ch2 > 57) {
                    context.invalid("decimal", new Object[] { "unexpected char '" + ch2 + "'" });
                    return;
                }
                sawDigit = true;
            }
            ++i;
        }
        if (!sawDigit) {
            context.invalid("decimal", new Object[] { "expected at least one digit" });
        }
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this._value = v;
    }
    
    @Override
    protected int compare_to(final XmlObject decimal) {
        return this._value.compareTo(((XmlObjectBase)decimal).bigDecimalValue());
    }
    
    @Override
    protected boolean equal_to(final XmlObject decimal) {
        return this._value.compareTo(((XmlObjectBase)decimal).bigDecimalValue()) == 0;
    }
    
    @Override
    protected int value_hash_code() {
        if (this._value.scale() > 0 && this._value.setScale(0, 1).compareTo(this._value) != 0) {
            return this.decimalHashCode();
        }
        final BigInteger intval = this._value.toBigInteger();
        if (intval.compareTo(JavaDecimalHolder._maxlong) > 0 || intval.compareTo(JavaDecimalHolder._minlong) < 0) {
            return intval.hashCode();
        }
        final long longval = intval.longValue();
        return (int)((longval >> 32) * 19L + longval);
    }
    
    protected int decimalHashCode() {
        assert this._value.scale() > 0;
        String strValue;
        int i;
        for (strValue = this._value.toString(), i = strValue.length() - 1; i >= 0 && strValue.charAt(i) == '0'; --i) {}
        assert strValue.indexOf(46) < i;
        return strValue.substring(0, i + 1).hashCode();
    }
    
    static {
        JavaDecimalHolder._maxlong = BigInteger.valueOf(Long.MAX_VALUE);
        JavaDecimalHolder._minlong = BigInteger.valueOf(Long.MIN_VALUE);
    }
}
