package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaBase64HolderEx extends JavaBase64Holder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaBase64HolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    protected int get_wscanon_rule() {
        return this.schemaType().getWhiteSpaceRule();
    }
    
    @Override
    protected void set_text(final String s) {
        byte[] v;
        if (this._validateOnSet()) {
            v = JavaBase64Holder.validateLexical(s, this.schemaType(), JavaBase64HolderEx._voorVc);
        }
        else {
            v = JavaBase64Holder.lex(s, JavaBase64HolderEx._voorVc);
        }
        if (v != null && this._validateOnSet()) {
            validateValue(v, this.schemaType(), XmlObjectBase._voorVc);
        }
        super.set_ByteArray(v);
    }
    
    @Override
    protected void set_ByteArray(final byte[] v) {
        if (this._validateOnSet()) {
            validateValue(v, this.schemaType(), JavaBase64HolderEx._voorVc);
        }
        super.set_ByteArray(v);
    }
    
    public static void validateValue(final byte[] v, final SchemaType sType, final ValidationContext context) {
        XmlObject o;
        int i = 0;
        if ((o = sType.getFacet(0)) != null && (i = ((XmlObjectBase)o).bigIntegerValue().intValue()) != v.length) {
            context.invalid("cvc-length-valid.1.2", new Object[] { "base64Binary", new Integer(v.length), new Integer(i), QNameHelper.readable(sType) });
        }
        if ((o = sType.getFacet(1)) != null && (i = ((XmlObjectBase)o).bigIntegerValue().intValue()) > v.length) {
            context.invalid("cvc-minLength-valid.1.2", new Object[] { "base64Binary", new Integer(v.length), new Integer(i), QNameHelper.readable(sType) });
        }
        if ((o = sType.getFacet(2)) != null && (i = ((XmlObjectBase)o).bigIntegerValue().intValue()) < v.length) {
            context.invalid("cvc-maxLength-valid.1.2", new Object[] { "base64Binary", new Integer(v.length), new Integer(i), QNameHelper.readable(sType) });
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
        Label_0314:
            for (i = 0; i < vals.length; ++i) {
                final byte[] enumBytes = ((XmlObjectBase)vals[i]).byteArrayValue();
                if (enumBytes.length == v.length) {
                    for (int j = 0; j < enumBytes.length; ++j) {
                        if (enumBytes[j] != v[j]) {
                            continue Label_0314;
                        }
                    }
                    break;
                }
            }
            if (i >= vals.length) {
                context.invalid("cvc-enumeration-valid.b", new Object[] { "base64Binary", QNameHelper.readable(sType) });
            }
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        JavaBase64Holder.validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.byteArrayValue(), this.schemaType(), ctx);
    }
}
