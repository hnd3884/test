package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaFloatHolder extends XmlObjectBase
{
    private float _value;
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_FLOAT;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return serialize(this._value);
    }
    
    public static String serialize(final float f) {
        if (f == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (f == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        if (f == Float.NaN) {
            return "NaN";
        }
        return Float.toString(f);
    }
    
    @Override
    protected void set_text(final String s) {
        this.set_float(validateLexical(s, JavaFloatHolder._voorVc));
    }
    
    public static float validateLexical(final String v, final ValidationContext context) {
        try {
            return XsTypeConverter.lexFloat(v);
        }
        catch (final NumberFormatException e) {
            context.invalid("float", new Object[] { v });
            return Float.NaN;
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = 0.0f;
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
        return this._value;
    }
    
    @Override
    protected void set_double(final double v) {
        this.set_float((float)v);
    }
    
    @Override
    protected void set_float(final float v) {
        this._value = v;
    }
    
    @Override
    protected void set_long(final long v) {
        this.set_float((float)v);
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        this.set_float(v.floatValue());
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        this.set_float(v.floatValue());
    }
    
    @Override
    protected int compare_to(final XmlObject f) {
        return compare(this._value, ((XmlObjectBase)f).floatValue());
    }
    
    static int compare(final float thisValue, final float thatValue) {
        if (thisValue < thatValue) {
            return -1;
        }
        if (thisValue > thatValue) {
            return 1;
        }
        final int thisBits = Float.floatToIntBits(thisValue);
        final int thatBits = Float.floatToIntBits(thatValue);
        return (thisBits == thatBits) ? 0 : ((thisBits < thatBits) ? -1 : 1);
    }
    
    @Override
    protected boolean equal_to(final XmlObject f) {
        return compare(this._value, ((XmlObjectBase)f).floatValue()) == 0;
    }
    
    @Override
    protected int value_hash_code() {
        return Float.floatToIntBits(this._value);
    }
}
