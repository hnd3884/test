package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;

public class JavaUriHolderEx extends JavaUriHolder
{
    private SchemaType _schemaType;
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    public JavaUriHolderEx(final SchemaType type, final boolean complex) {
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
    
    public static void validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        JavaUriHolder.validateLexical(v, context);
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            int i;
            for (i = 0; i < vals.length; ++i) {
                final String e = ((SimpleValue)vals[i]).getStringValue();
                if (e.equals(v)) {
                    break;
                }
            }
            if (i >= vals.length) {
                context.invalid("cvc-enumeration-valid", new Object[] { "anyURI", v, QNameHelper.readable(sType) });
            }
        }
        if (sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "anyURI", v, QNameHelper.readable(sType) });
        }
        XmlObject x;
        int j;
        if ((x = sType.getFacet(0)) != null && (j = ((SimpleValue)x).getBigIntegerValue().intValue()) != v.length()) {
            context.invalid("cvc-length-valid.1.1", new Object[] { "anyURI", v, new Integer(j), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(1)) != null && (j = ((SimpleValue)x).getBigIntegerValue().intValue()) > v.length()) {
            context.invalid("cvc-minLength-valid.1.1", new Object[] { "anyURI", v, new Integer(j), QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(2)) != null && (j = ((SimpleValue)x).getBigIntegerValue().intValue()) < v.length()) {
            context.invalid("cvc-maxLength-valid.1.1", new Object[] { "anyURI", v, new Integer(j), QNameHelper.readable(sType) });
        }
    }
    
    private static boolean check(final String v, final SchemaType sType) {
        final int length = (v == null) ? 0 : v.length();
        final XmlObject len = sType.getFacet(0);
        if (len != null) {
            final int m = ((SimpleValue)len).getBigIntegerValue().intValue();
            if (length == m) {
                return false;
            }
        }
        final XmlObject min = sType.getFacet(1);
        if (min != null) {
            final int i = ((SimpleValue)min).getBigIntegerValue().intValue();
            if (length < i) {
                return false;
            }
        }
        final XmlObject max = sType.getFacet(2);
        if (max != null) {
            final int j = ((SimpleValue)max).getBigIntegerValue().intValue();
            if (length > j) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(this.stringValue(), this.schemaType(), ctx);
    }
}
