package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaFloatHolderEx extends JavaFloatHolder
{
    private SchemaType _schemaType;
    
    public JavaFloatHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected void set_float(final float v) {
        if (this._validateOnSet()) {
            validateValue(v, this._schemaType, JavaFloatHolderEx._voorVc);
        }
        super.set_float(v);
    }
    
    public static float validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        final float f = JavaFloatHolder.validateLexical(v, context);
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "float", v, QNameHelper.readable(sType) });
        }
        return f;
    }
    
    public static void validateValue(final float v, final SchemaType sType, final ValidationContext context) {
        XmlObject x;
        float f;
        if ((x = sType.getFacet(3)) != null && JavaFloatHolder.compare(v, f = ((XmlObjectBase)x).floatValue()) <= 0) {
            context.invalid("cvc-minExclusive-valid", new Object[] { "float", new Float(v), new Float(f), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(4)) != null && JavaFloatHolder.compare(v, f = ((XmlObjectBase)x).floatValue()) < 0) {
            context.invalid("cvc-minInclusive-valid", new Object[] { "float", new Float(v), new Float(f), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(5)) != null && JavaFloatHolder.compare(v, f = ((XmlObjectBase)x).floatValue()) > 0) {
            context.invalid("cvc-maxInclusive-valid", new Object[] { "float", new Float(v), new Float(f), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(6)) != null && JavaFloatHolder.compare(v, f = ((XmlObjectBase)x).floatValue()) >= 0) {
            context.invalid("cvc-maxExclusive-valid", new Object[] { "float", new Float(v), new Float(f), QNameHelper.readable(sType) });
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i = 0; i < vals.length; ++i) {
                if (JavaFloatHolder.compare(v, ((XmlObjectBase)vals[i]).floatValue()) == 0) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "float", new Float(v), QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.floatValue(), this.schemaType(), ctx);
    }
}
