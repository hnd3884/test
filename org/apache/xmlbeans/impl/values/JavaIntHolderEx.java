package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaIntHolderEx extends JavaIntHolder
{
    private SchemaType _schemaType;
    
    public JavaIntHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected void set_text(final String s) {
        int v;
        try {
            v = XsTypeConverter.lexInt(s);
        }
        catch (final Exception e) {
            throw new XmlValueOutOfRangeException();
        }
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaIntHolderEx._voorVc);
            validateLexical(s, this._schemaType, JavaIntHolderEx._voorVc);
        }
        super.set_int(v);
    }
    
    @Override
    protected void set_int(final int v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaIntHolderEx._voorVc);
        }
        super.set_int(v);
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaDecimalHolder.validateLexical(v, context);
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "int", v, QNameHelper.readable(sType) });
        }
    }
    
    private static void validateValue(final int v, final SchemaType sType, final ValidationContext context) {
        final XmlObject td = sType.getFacet(7);
        if (td != null) {
            final String temp = Integer.toString(v);
            int len = temp.length();
            if (len > 0 && temp.charAt(0) == '-') {
                --len;
            }
            final int m = getIntValue(td);
            if (len > m) {
                context.invalid("cvc-totalDigits-valid", new Object[] { new Integer(len), temp, new Integer(getIntValue(td)), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mine = sType.getFacet(3);
        if (mine != null) {
            final int i = getIntValue(mine);
            if (v <= i) {
                context.invalid("cvc-minExclusive-valid", new Object[] { "int", new Integer(v), new Integer(i), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mini = sType.getFacet(4);
        if (mini != null) {
            final int m = getIntValue(mini);
            if (v < m) {
                context.invalid("cvc-minInclusive-valid", new Object[] { "int", new Integer(v), new Integer(m), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxi = sType.getFacet(5);
        if (maxi != null) {
            final int j = getIntValue(maxi);
            if (v > j) {
                context.invalid("cvc-maxExclusive-valid", new Object[] { "int", new Integer(v), new Integer(j), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxe = sType.getFacet(6);
        if (maxe != null) {
            final int k = getIntValue(maxe);
            if (v >= k) {
                context.invalid("cvc-maxExclusive-valid", new Object[] { "int", new Integer(v), new Integer(k), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int l = 0; l < vals.length; ++l) {
                if (v == getIntValue(vals[l])) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "int", new Integer(v), QNameHelper.readable(sType) });
        }
    }
    
    private static int getIntValue(final XmlObject o) {
        final SchemaType s = o.schemaType();
        switch (s.getDecimalSize()) {
            case 1000001: {
                return ((XmlObjectBase)o).getBigDecimalValue().intValue();
            }
            case 1000000: {
                return ((XmlObjectBase)o).getBigIntegerValue().intValue();
            }
            case 64: {
                return (int)((XmlObjectBase)o).getLongValue();
            }
            default: {
                return ((XmlObjectBase)o).getIntValue();
            }
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.getIntValue(), this.schemaType(), ctx);
    }
}
