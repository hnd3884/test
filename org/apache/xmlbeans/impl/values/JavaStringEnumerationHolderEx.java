package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;

public abstract class JavaStringEnumerationHolderEx extends JavaStringHolderEx
{
    private StringEnumAbstractBase _val;
    
    public JavaStringEnumerationHolderEx(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
    
    @Override
    protected void set_text(final String s) {
        final StringEnumAbstractBase se = this.schemaType().enumForString(s);
        if (se == null) {
            throw new XmlValueOutOfRangeException("cvc-enumeration-valid", new Object[] { "string", s, QNameHelper.readable(this.schemaType()) });
        }
        super.set_text(s);
        this._val = se;
    }
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaStringHolderEx.validateLexical(v, sType, context);
    }
    
    @Override
    protected void set_nil() {
        this._val = null;
        super.set_nil();
    }
    
    @Override
    public StringEnumAbstractBase getEnumValue() {
        this.check_dated();
        return this._val;
    }
    
    @Override
    protected void set_enum(final StringEnumAbstractBase se) {
        final Class ejc = this.schemaType().getEnumJavaClass();
        if (ejc != null && !se.getClass().equals(ejc)) {
            throw new XmlValueOutOfRangeException();
        }
        super.set_text(se.toString());
        this._val = se;
    }
}
