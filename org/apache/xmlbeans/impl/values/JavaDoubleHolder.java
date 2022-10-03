package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaDoubleHolder extends XmlObjectBase
{
    double _value;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_DOUBLE;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return serialize(this._value);
    }
    
    public static String serialize(final double d) {
        if (d == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (d == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        if (d == Double.NaN) {
            return "NaN";
        }
        return Double.toString(d);
    }
    
    @Override
    protected void set_text(final String s) {
        this.set_double(validateLexical(s, JavaDoubleHolder._voorVc));
    }
    
    public static double validateLexical(final String v, final ValidationContext context) {
        try {
            return XsTypeConverter.lexDouble(v);
        }
        catch (final NumberFormatException e) {
            context.invalid("double", new Object[] { v });
            return Double.NaN;
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = 0.0;
    }
    
    @Override
    public BigDecimal getBigDecimalValue() {
        this.check_dated();
        return new BigDecimal(this._value);
    }
    
    @Override
    public double getDoubleValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    public float getFloatValue() {
        this.check_dated();
        return (float)this._value;
    }
    
    @Override
    protected void set_double(final double v) {
        this._value = v;
    }
    
    @Override
    protected void set_float(final float v) {
        this.set_double(v);
    }
    
    @Override
    protected void set_long(final long v) {
        this.set_double((double)v);
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this.set_double(v.doubleValue());
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        this.set_double(v.doubleValue());
    }
    
    @Override
    protected int compare_to(final XmlObject d) {
        return compare(this._value, ((XmlObjectBase)d).doubleValue());
    }
    
    static int compare(final double thisValue, final double thatValue) {
        if (thisValue < thatValue) {
            return -1;
        }
        if (thisValue > thatValue) {
            return 1;
        }
        final long thisBits = Double.doubleToLongBits(thisValue);
        final long thatBits = Double.doubleToLongBits(thatValue);
        return (thisBits == thatBits) ? 0 : ((thisBits < thatBits) ? -1 : 1);
    }
    
    @Override
    protected boolean equal_to(final XmlObject d) {
        return compare(this._value, ((XmlObjectBase)d).doubleValue()) == 0;
    }
    
    @Override
    protected int value_hash_code() {
        final long v = Double.doubleToLongBits(this._value);
        return (int)((v >> 32) * 19L + v);
    }
}
