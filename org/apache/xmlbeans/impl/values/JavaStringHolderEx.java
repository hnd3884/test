package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaStringHolderEx extends JavaStringHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaStringHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    protected int get_wscanon_rule() {
        return this.schemaType().getWhiteSpaceRule();
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet()) {
            validateLexical(s, this._schemaType, JavaStringHolderEx._voorVc);
        }
        super.set_text(s);
    }
    
    @Override
    protected boolean is_defaultable_ws(final String v) {
        try {
            validateLexical(v, this._schemaType, JavaStringHolderEx._voorVc);
            return false;
        }
        catch (final XmlValueOutOfRangeException e) {
            return true;
        }
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "string", v, QNameHelper.readable(sType) });
            return;
        }
        final XmlObject len = sType.getFacet(0);
        if (len != null) {
            final int m = ((XmlObjectBase)len).bigIntegerValue().intValue();
            if (v.length() != m) {
                context.invalid("cvc-length-valid.1.1", new Object[] { "string", new Integer(v.length()), new Integer(m), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject min = sType.getFacet(1);
        if (min != null) {
            final int i = ((XmlObjectBase)min).bigIntegerValue().intValue();
            if (v.length() < i) {
                context.invalid("cvc-minLength-valid.1.1", new Object[] { "string", new Integer(v.length()), new Integer(i), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlObject max = sType.getFacet(2);
        if (max != null) {
            final int j = ((XmlObjectBase)max).bigIntegerValue().intValue();
            if (v.length() > j) {
                context.invalid("cvc-maxLength-valid.1.1", new Object[] { "string", new Integer(v.length()), new Integer(j), QNameHelper.readable(sType) });
                return;
            }
        }
        final XmlAnySimpleType[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int k = 0; k < vals.length; ++k) {
                if (v.equals(vals[k].getStringValue())) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "string", v, QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(this.stringValue(), this.schemaType(), ctx);
    }
}
