package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.math.BigInteger;

public abstract class JavaIntegerHolder extends XmlObjectBase
{
    private BigInteger _value;
    private static BigInteger _maxlong;
    private static BigInteger _minlong;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_INTEGER;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return this._value.toString();
    }
    
    @Override
    protected void set_text(final String s) {
        this.set_BigInteger(lex(s, JavaIntegerHolder._voorVc));
    }
    
    public static BigInteger lex(String s, final ValidationContext vc) {
        if (s.length() > 0 && s.charAt(0) == '+') {
            s = s.substring(1);
        }
        try {
            return new BigInteger(s);
        }
        catch (final Exception e) {
            vc.invalid("integer", new Object[] { s });
            return null;
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        this.check_dated();
        return (this._value == null) ? null : new BigDecimal(this._value);
    }
    
    @Override
    public BigInteger getBigIntegerValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this._value = v.toBigInteger();
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        this._value = v;
    }
    
    @Override
    protected int compare_to(final XmlObject i) {
        if (((SimpleValue)i).instanceType().getDecimalSize() > 1000000) {
            return -i.compareTo(this);
        }
        return this._value.compareTo(((XmlObjectBase)i).bigIntegerValue());
    }
    
    @Override
    protected boolean equal_to(final XmlObject i) {
        if (((SimpleValue)i).instanceType().getDecimalSize() > 1000000) {
            return i.valueEquals(this);
        }
        return this._value.equals(((XmlObjectBase)i).bigIntegerValue());
    }
    
    @Override
    protected int value_hash_code() {
        if (this._value.compareTo(JavaIntegerHolder._maxlong) > 0 || this._value.compareTo(JavaIntegerHolder._minlong) < 0) {
            return this._value.hashCode();
        }
        final long longval = this._value.longValue();
        return (int)((longval >> 32) * 19L + longval);
    }
    
    static {
        JavaIntegerHolder._maxlong = BigInteger.valueOf(Long.MAX_VALUE);
        JavaIntegerHolder._minlong = BigInteger.valueOf(Long.MIN_VALUE);
    }
}
