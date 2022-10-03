package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaDoubleHolderEx extends JavaDoubleHolder
{
    private SchemaType _schemaType;
    
    public JavaDoubleHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected void set_double(final double v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaDoubleHolderEx._voorVc);
        }
        super.set_double(v);
    }
    
    public static double validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        final double d = JavaDoubleHolder.validateLexical(v, context);
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "double", v, QNameHelper.readable(sType) });
        }
        return d;
    }
    
    public static void validateValue(final double v, final SchemaType sType, final ValidationContext context) {
        XmlObject x;
        double d;
        if ((x = sType.getFacet(3)) != null && JavaDoubleHolder.compare(v, d = ((XmlObjectBase)x).doubleValue()) <= 0) {
            context.invalid("cvc-minExclusive-valid", new Object[] { "double", new Double(v), new Double(d), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(4)) != null && JavaDoubleHolder.compare(v, d = ((XmlObjectBase)x).doubleValue()) < 0) {
            context.invalid("cvc-minInclusive-valid", new Object[] { "double", new Double(v), new Double(d), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(5)) != null && JavaDoubleHolder.compare(v, d = ((XmlObjectBase)x).doubleValue()) > 0) {
            context.invalid("cvc-maxInclusive-valid", new Object[] { "double", new Double(v), new Double(d), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(6)) != null && JavaDoubleHolder.compare(v, d = ((XmlObjectBase)x).doubleValue()) >= 0) {
            context.invalid("cvc-maxExclusive-valid", new Object[] { "double", new Double(v), new Double(d), QNameHelper.readable(sType) });
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i = 0; i < vals.length; ++i) {
                if (JavaDoubleHolder.compare(v, ((XmlObjectBase)vals[i]).doubleValue()) == 0) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "double", new Double(v), QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.doubleValue(), this.schemaType(), ctx);
    }
}
