package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.impl.common.ValidationContext;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaNotationHolderEx extends JavaNotationHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaNotationHolderEx(final SchemaType type, final boolean complex) {
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
            if (!check(s, this._schemaType)) {
                throw new XmlValueOutOfRangeException();
            }
            if (!this._schemaType.matchPatternFacet(s)) {
                throw new XmlValueOutOfRangeException();
            }
        }
        super.set_text(s);
    }
    
    @Override
    protected void set_notation(final String v) {
        this.set_text(v);
    }
    
    @Override
    protected void set_xmlanysimple(final XmlAnySimpleType value) {
        QName v;
        if (this._validateOnSet()) {
            v = validateLexical(value.getStringValue(), this._schemaType, JavaNotationHolderEx._voorVc, NamespaceContext.getCurrent());
            if (v != null) {
                validateValue(v, this._schemaType, JavaNotationHolderEx._voorVc);
            }
        }
        else {
            v = JavaQNameHolder.validateLexical(value.getStringValue(), JavaNotationHolderEx._voorVc, NamespaceContext.getCurrent());
        }
        super.set_QName(v);
    }
    
    public static QName validateLexical(final String v, final SchemaType sType, final ValidationContext context, final PrefixResolver resolver) {
        final QName name = JavaQNameHolder.validateLexical(v, context, resolver);
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "NOTATION", v, QNameHelper.readable(sType) });
        }
        check(v, sType);
        return name;
    }
    
    private static boolean check(final String v, final SchemaType sType) {
        final XmlObject len = sType.getFacet(0);
        if (len != null) {
            final int m = ((XmlObjectBase)len).getBigIntegerValue().intValue();
            if (v.length() == m) {
                return false;
            }
        }
        final XmlObject min = sType.getFacet(1);
        if (min != null) {
            final int i = ((XmlObjectBase)min).getBigIntegerValue().intValue();
            if (v.length() < i) {
                return false;
            }
        }
        final XmlObject max = sType.getFacet(2);
        if (max != null) {
            final int j = ((XmlObjectBase)max).getBigIntegerValue().intValue();
            if (v.length() > j) {
                return false;
            }
        }
        return true;
    }
    
    public static void validateValue(final QName v, final SchemaType sType, final ValidationContext context) {
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i = 0; i < vals.length; ++i) {
                if (v.equals(((XmlObjectBase)vals[i]).getQNameValue())) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "NOTATION", v, QNameHelper.readable(sType) });
        }
    }
}
