package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.math.BigInteger;

public abstract class JavaLongHolder extends XmlObjectBase
{
    private long _value;
    private static final BigInteger _max;
    private static final BigInteger _min;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_LONG;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return Long.toString(this._value);
    }
    
    @Override
    protected void set_text(final String s) {
        try {
            this.set_long(XsTypeConverter.lexLong(s));
        }
        catch (final Exception e) {
            throw new XmlValueOutOfRangeException("long", new Object[] { s });
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = 0L;
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        this.check_dated();
        return BigDecimal.valueOf(this._value);
    }
    
    @Override
    public BigInteger getBigIntegerValue() {
        this.check_dated();
        return BigInteger.valueOf(this._value);
    }
    
    @Override
    public long getLongValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this.set_BigInteger(v.toBigInteger());
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        if (v.compareTo(JavaLongHolder._max) > 0 || v.compareTo(JavaLongHolder._min) < 0) {
            throw new XmlValueOutOfRangeException();
        }
        this._value = v.longValue();
    }
    
    @Override
    protected void set_long(final long l) {
        this._value = l;
    }
    
    @Override
    protected int compare_to(final XmlObject l) {
        if (((SimpleValue)l).instanceType().getDecimalSize() > 64) {
            return -l.compareTo(this);
        }
        return (this._value == ((XmlObjectBase)l).longValue()) ? 0 : ((this._value < ((XmlObjectBase)l).longValue()) ? -1 : 1);
    }
    
    @Override
    protected boolean equal_to(final XmlObject l) {
        if (((SimpleValue)l).instanceType().getDecimalSize() > 64) {
            return l.valueEquals(this);
        }
        return this._value == ((XmlObjectBase)l).longValue();
    }
    
    @Override
    protected int value_hash_code() {
        return (int)((this._value >> 32) * 19L + this._value);
    }
    
    static {
        _max = BigInteger.valueOf(Long.MAX_VALUE);
        _min = BigInteger.valueOf(Long.MIN_VALUE);
    }
}
