package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaBooleanHolderEx extends JavaBooleanHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public static boolean validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        final boolean b = JavaBooleanHolder.validateLexical(v, context);
        validatePattern(v, sType, context);
        return b;
    }
    
    public static void validatePattern(final String v, final SchemaType sType, final ValidationContext context) {
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "boolean", v, QNameHelper.readable(sType) });
        }
    }
    
    public JavaBooleanHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet()) {
            validatePattern(s, this._schemaType, JavaBooleanHolderEx._voorVc);
        }
        super.set_text(s);
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
    }
}
