package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.math.BigInteger;

public abstract class JavaIntHolder extends XmlObjectBase
{
    private int _value;
    static final BigInteger _max;
    static final BigInteger _min;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_INT;
    }
    
    public String compute_text(final NamespaceManager nsm) {
        return Long.toString(this._value);
    }
    
    @Override
    protected void set_text(final String s) {
        try {
            this.set_int(XsTypeConverter.lexInt(s));
        }
        catch (final Exception e) {
            throw new XmlValueOutOfRangeException("int", new Object[] { s });
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = 0;
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        this.check_dated();
        return new BigDecimal((double)this._value);
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
    public int getIntValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this.set_BigInteger(v.toBigInteger());
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        if (v.compareTo(JavaIntHolder._max) > 0 || v.compareTo(JavaIntHolder._min) < 0) {
            throw new XmlValueOutOfRangeException();
        }
        this.set_int(v.intValue());
    }
    
    @Override
    protected void set_long(final long l) {
        if (l > 2147483647L || l < -2147483648L) {
            throw new XmlValueOutOfRangeException();
        }
        this.set_int((int)l);
    }
    
    @Override
    protected void set_int(final int i) {
        this._value = i;
    }
    
    @Override
    protected int compare_to(final XmlObject i) {
        if (((SimpleValue)i).instanceType().getDecimalSize() > 32) {
            return -i.compareTo(this);
        }
        return (this._value == ((XmlObjectBase)i).intValue()) ? 0 : ((this._value < ((XmlObjectBase)i).intValue()) ? -1 : 1);
    }
    
    @Override
    protected boolean equal_to(final XmlObject i) {
        if (((SimpleValue)i).instanceType().getDecimalSize() > 32) {
            return i.valueEquals(this);
        }
        return this._value == ((XmlObjectBase)i).intValue();
    }
    
    @Override
    protected int value_hash_code() {
        return this._value;
    }
    
    static {
        _max = BigInteger.valueOf(2147483647L);
        _min = BigInteger.valueOf(-2147483648L);
    }
}
