package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import java.math.BigDecimal;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaDecimalHolderEx extends JavaDecimalHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaDecimalHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet()) {
            validateLexical(s, this._schemaType, JavaDecimalHolderEx._voorVc);
        }
        BigDecimal v = null;
        try {
            v = new BigDecimal(s);
        }
        catch (final NumberFormatException e) {
            JavaDecimalHolderEx._voorVc.invalid("decimal", new Object[] { s });
        }
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaDecimalHolderEx._voorVc);
        }
        super.set_BigDecimal(v);
    }
    
    @Override
    protected void set_BigDecimal(final BigDecimal v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaDecimalHolderEx._voorVc);
        }
        super.set_BigDecimal(v);
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaDecimalHolder.validateLexical(v, context);
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "decimal", v, QNameHelper.readable(sType) });
        }
    }
    
    public static void validateValue(final BigDecimal v, final SchemaType sType, final ValidationContext context) {
        final XmlObject fd = sType.getFacet(8);
        if (fd != null) {
            final int scale = ((XmlObjectBase)fd).getBigIntegerValue().intValue();
            try {
                v.setScale(scale);
            }
            catch (final ArithmeticException e) {
                context.invalid("cvc-fractionDigits-valid", new Object[] { new Integer(v.scale()), v.toString(), new Integer(scale), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject td = sType.getFacet(7);
        if (td != null) {
            final String temp = v.unscaledValue().toString();
            final int tdf = ((XmlObjectBase)td).getBigIntegerValue().intValue();
            final int origLen = temp.length();
            int len;
            if ((len = origLen) > 0) {
                if (temp.charAt(0) == '-') {
                    --len;
                }
                int insignificantTrailingZeros = 0;
                for (int vScale = v.scale(), j = origLen - 1; temp.charAt(j) == '0' && j > 0 && insignificantTrailingZeros < vScale; ++insignificantTrailingZeros, --j) {}
                len -= insignificantTrailingZeros;
            }
            if (len > tdf) {
                context.invalid("cvc-totalDigits-valid", new Object[] { new Integer(len), v.toString(), new Integer(tdf), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mine = sType.getFacet(3);
        if (mine != null) {
            final BigDecimal m = ((XmlObjectBase)mine).getBigDecimalValue();
            if (v.compareTo(m) <= 0) {
                context.invalid("cvc-minExclusive-valid", new Object[] { "decimal", v, m, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject mini = sType.getFacet(4);
        if (mini != null) {
            final BigDecimal i = ((XmlObjectBase)mini).getBigDecimalValue();
            if (v.compareTo(i) < 0) {
                context.invalid("cvc-minInclusive-valid", new Object[] { "decimal", v, i, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxi = sType.getFacet(5);
        if (maxi != null) {
            final BigDecimal k = ((XmlObjectBase)maxi).getBigDecimalValue();
            if (v.compareTo(k) > 0) {
                context.invalid("cvc-maxInclusive-valid", new Object[] { "decimal", v, k, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject maxe = sType.getFacet(6);
        if (maxe != null) {
            final BigDecimal l = ((XmlObjectBase)maxe).getBigDecimalValue();
            if (v.compareTo(l) >= 0) {
                context.invalid("cvc-maxExclusive-valid", new Object[] { "decimal", v, l, QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i2 = 0; i2 < vals.length; ++i2) {
                if (v.equals(((XmlObjectBase)vals[i2]).getBigDecimalValue())) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "decimal", v, QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.getBigDecimalValue(), this.schemaType(), ctx);
    }
}
