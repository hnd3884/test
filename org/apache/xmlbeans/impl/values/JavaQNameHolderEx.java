package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.XmlAnySimpleType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaQNameHolderEx extends JavaQNameHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaQNameHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    protected int get_wscanon_rule() {
        return this.schemaType().getWhiteSpaceRule();
    }
    
    @Override
    protected void set_text(final String s) {
        PrefixResolver resolver = NamespaceContext.getCurrent();
        if (resolver == null && this.has_store()) {
            resolver = this.get_store();
        }
        QName v;
        if (this._validateOnSet()) {
            v = validateLexical(s, this._schemaType, JavaQNameHolderEx._voorVc, resolver);
            if (v != null) {
                validateValue(v, this._schemaType, JavaQNameHolderEx._voorVc);
            }
        }
        else {
            v = JavaQNameHolder.validateLexical(s, JavaQNameHolderEx._voorVc, resolver);
        }
        super.set_QName(v);
    }
    
    @Override
    protected void set_QName(final QName name) {
        if (this._validateOnSet()) {
            validateValue(name, this._schemaType, JavaQNameHolderEx._voorVc);
        }
        super.set_QName(name);
    }
    
    @Override
    protected void set_xmlanysimple(final XmlAnySimpleType value) {
        QName v;
        if (this._validateOnSet()) {
            v = validateLexical(value.getStringValue(), this._schemaType, JavaQNameHolderEx._voorVc, NamespaceContext.getCurrent());
            if (v != null) {
                validateValue(v, this._schemaType, JavaQNameHolderEx._voorVc);
            }
        }
        else {
            v = JavaQNameHolder.validateLexical(value.getStringValue(), JavaQNameHolderEx._voorVc, NamespaceContext.getCurrent());
        }
        super.set_QName(v);
    }
    
    public static QName validateLexical(final String v, final SchemaType sType, final ValidationContext context, final PrefixResolver resolver) {
        final QName name = JavaQNameHolder.validateLexical(v, context, resolver);
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "QName", v, QNameHelper.readable(sType) });
        }
        return name;
    }
    
    public static void validateValue(final QName v, final SchemaType sType, final ValidationContext context) {
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i = 0; i < vals.length; ++i) {
                if (v.equals(((XmlObjectBase)vals[i]).getQNameValue())) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "QName", v, QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateValue(this.getQNameValue(), this.schemaType(), ctx);
    }
}
