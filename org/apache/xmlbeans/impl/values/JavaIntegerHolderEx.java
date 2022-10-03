package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlPositiveInteger;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;

public class JavaIntegerHolderEx extends JavaIntegerHolder
{
    private SchemaType _schemaType;
    
    public JavaIntegerHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected void set_text(final String s) {
        final BigInteger v = JavaIntegerHolder.lex(s, JavaIntegerHolderEx._voorVc);
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaIntegerHolderEx._voorVc);
        }
        if (this._validateOnSet()) {
            validateLexical(s, this._schemaType, JavaIntegerHolderEx._voorVc);
        }
        super.set_BigInteger(v);
    }
    
    @Override
    protected void set_BigInteger(final BigInteger v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaIntegerHolderEx._voorVc);
        }
        super.set_BigInteger(v);
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaDecimalHolder.validateLexical(v, context);
        if (v.lastIndexOf(46) >= 0) {
            context.invalid("integer", new Object[] { v });
        }
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "integer", v, QNameHelper.readable(sType) });
        }
    }
    
    private static void validateValue(final BigInteger v, final SchemaType sType, final ValidationContext context) {
        final XmlPositiveInteger td = (XmlPositiveInteger)sType.getFacet(7);
        if (td != null) {
            final String temp = v.toString();
            int len = temp.length();
            if (len > 0 && temp.charAt(0) == '-') {
                --len;
            }
            if (len > td.getBigIntegerValue().intValue()) {
                context.invalid("cvc-totalDigits-valid", new Object[] { new Integer(len), temp, new Integer(td.getBigIntegerValue().intValue()), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mine = sType.getFacet(3);
        if (mine != null) {
            final BigInteger m = getBigIntegerValue(mine);
            if (v.compareTo(m) <= 0) {
                context.invalid("cvc-minExclusive-valid", new Object[] { "integer", v, m, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mini = sType.getFacet(4);
        if (mini != null) {
            final BigInteger i = getBigIntegerValue(mini);
            if (v.compareTo(i) < 0) {
                context.invalid("cvc-minInclusive-valid", new Object[] { "integer", v, i, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxi = sType.getFacet(5);
        if (maxi != null) {
            final BigInteger j = getBigIntegerValue(maxi);
            if (v.compareTo(j) > 0) {
                context.invalid("cvc-maxInclusive-valid", new Object[] { "integer", v, j, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxe = sType.getFacet(6);
        if (maxe != null) {
            final BigInteger k = getBigIntegerValue(maxe);
            if (v.compareTo(k) >= 0) {
                context.invalid("cvc-maxExclusive-valid", new Object[] { "integer", v, k, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int l = 0; l < vals.length; ++l) {
                if (v.equals(getBigIntegerValue(vals[l]))) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "integer", v, QNameHelper.readable(sType) });
        }
    }
    
    private static BigInteger getBigIntegerValue(final XmlObject o) {
        final SchemaType s = o.schemaType();
        switch (s.getDecimalSize()) {
            case 1000001: {
                return ((XmlObjectBase)o).bigDecimalValue().toBigInteger();
            }
            case 1000000: {
                return ((XmlObjectBase)o).bigIntegerValue();
            }
            default: {
                throw new IllegalStateException("Bad facet type for Big Int: " + s);
            }
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.getBigIntegerValue(), this.schemaType(), ctx);
    }
}
