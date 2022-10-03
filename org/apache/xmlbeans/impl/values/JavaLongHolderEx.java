package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaLongHolderEx extends JavaLongHolder
{
    private SchemaType _schemaType;
    
    public JavaLongHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected void set_text(final String s) {
        long v;
        try {
            v = XsTypeConverter.lexLong(s);
        }
        catch (final Exception e) {
            throw new XmlValueOutOfRangeException();
        }
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaLongHolderEx._voorVc);
            validateLexical(s, this._schemaType, JavaLongHolderEx._voorVc);
        }
        super.set_long(v);
    }
    
    @Override
    protected void set_long(final long v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaLongHolderEx._voorVc);
        }
        super.set_long(v);
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaDecimalHolder.validateLexical(v, context);
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "long", v, QNameHelper.readable(sType) });
        }
    }
    
    private static void validateValue(final long v, final SchemaType sType, final ValidationContext context) {
        final XmlObject td = sType.getFacet(7);
        if (td != null) {
            final long m = getLongValue(td);
            final String temp = Long.toString(v);
            int len = temp.length();
            if (len > 0 && temp.charAt(0) == '-') {
                --len;
            }
            if (len > m) {
                context.invalid("cvc-totalDigits-valid", new Object[] { new Integer(len), temp, new Long(m), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mine = sType.getFacet(3);
        if (mine != null) {
            final long i = getLongValue(mine);
            if (v <= i) {
                context.invalid("cvc-minExclusive-valid", new Object[] { "long", new Long(v), new Long(i), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mini = sType.getFacet(4);
        if (mini != null) {
            final long j = getLongValue(mini);
            if (v < j) {
                context.invalid("cvc-minInclusive-valid", new Object[] { "long", new Long(v), new Long(j), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxi = sType.getFacet(5);
        if (maxi != null) {
            final long k = getLongValue(maxi);
            if (v > k) {
                context.invalid("cvc-maxInclusive-valid", new Object[] { "long", new Long(v), new Long(k), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxe = sType.getFacet(6);
        if (maxe != null) {
            final long l = getLongValue(maxe);
            if (v >= l) {
                context.invalid("cvc-maxExclusive-valid", new Object[] { "long", new Long(v), new Long(l), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i2 = 0; i2 < vals.length; ++i2) {
                if (v == getLongValue(vals[i2])) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "long", new Long(v), QNameHelper.readable(sType) });
        }
    }
    
    private static long getLongValue(final XmlObject o) {
        final SchemaType s = o.schemaType();
        switch (s.getDecimalSize()) {
            case 1000001: {
                return ((XmlObjectBase)o).getBigDecimalValue().longValue();
            }
            case 1000000: {
                return ((XmlObjectBase)o).getBigIntegerValue().longValue();
            }
            case 64: {
                return ((XmlObjectBase)o).getLongValue();
            }
            default: {
                throw new IllegalStateException("Bad facet type: " + s);
            }
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.getLongValue(), this.schemaType(), ctx);
    }
}
