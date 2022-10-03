package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlObject;
import java.util.Arrays;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.common.XMLChar;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SimpleValue;
import java.util.List;
import org.apache.xmlbeans.XmlSimpleList;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;

public class XmlListImpl extends XmlObjectBase implements XmlAnySimpleType
{
    private SchemaType _schemaType;
    private XmlSimpleList _value;
    private XmlSimpleList _jvalue;
    private static final String[] EMPTY_STRINGARRAY;
    
    public XmlListImpl(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    private static String nullAsEmpty(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private static String compute_list_text(final List xList) {
        if (xList.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(nullAsEmpty(xList.get(0).getStringValue()));
        for (int i = 1; i < xList.size(); ++i) {
            sb.append(' ');
            sb.append(nullAsEmpty(xList.get(i).getStringValue()));
        }
        return sb.toString();
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return compute_list_text(this._value);
    }
    
    @Override
    protected boolean is_defaultable_ws(final String v) {
        try {
            final XmlSimpleList savedValue = this._value;
            this.set_text(v);
            this._value = savedValue;
            return false;
        }
        catch (final XmlValueOutOfRangeException e) {
            return true;
        }
    }
    
    @Override
    protected void set_text(final String s) {
        if (this._validateOnSet() && !this._schemaType.matchPatternFacet(s)) {
            throw new XmlValueOutOfRangeException("cvc-datatype-valid.1.1", new Object[] { "list", s, QNameHelper.readable(this._schemaType) });
        }
        final SchemaType itemType = this._schemaType.getListItemType();
        final XmlSimpleList newval = lex(s, itemType, XmlListImpl._voorVc, this.has_store() ? this.get_store() : null);
        if (this._validateOnSet()) {
            validateValue(newval, this._schemaType, XmlListImpl._voorVc);
        }
        this._value = newval;
        this._jvalue = null;
    }
    
    public static String[] split_list(final String s) {
        if (s.length() == 0) {
            return XmlListImpl.EMPTY_STRINGARRAY;
        }
        final List result = new ArrayList();
        int i = 0;
        int start = 0;
        while (true) {
            if (i < s.length() && XMLChar.isSpace(s.charAt(i))) {
                ++i;
            }
            else {
                if (i >= s.length()) {
                    break;
                }
                start = i;
                while (i < s.length() && !XMLChar.isSpace(s.charAt(i))) {
                    ++i;
                }
                result.add(s.substring(start, i));
            }
        }
        return result.toArray(XmlListImpl.EMPTY_STRINGARRAY);
    }
    
    public static XmlSimpleList lex(final String s, final SchemaType itemType, final ValidationContext ctx, final PrefixResolver resolver) {
        final String[] parts = split_list(s);
        final XmlAnySimpleType[] newArray = new XmlAnySimpleType[parts.length];
        boolean pushed = false;
        if (resolver != null) {
            NamespaceContext.push(new NamespaceContext(resolver));
            pushed = true;
        }
        int i = 0;
        try {
            for (i = 0; i < parts.length; ++i) {
                try {
                    newArray[i] = itemType.newValue(parts[i]);
                }
                catch (final XmlValueOutOfRangeException e) {
                    ctx.invalid("list", new Object[] { "item '" + parts[i] + "' is not a valid value of " + QNameHelper.readable(itemType) });
                }
            }
        }
        finally {
            if (pushed) {
                NamespaceContext.pop();
            }
        }
        return new XmlSimpleList(Arrays.asList(newArray));
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    @Override
    public List xgetListValue() {
        this.check_dated();
        return this._value;
    }
    
    @Override
    public List getListValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        if (this._jvalue != null) {
            return this._jvalue;
        }
        final List javaResult = new ArrayList();
        for (int i = 0; i < this._value.size(); ++i) {
            javaResult.add(XmlObjectBase.java_value((XmlObject)this._value.get(i)));
        }
        return this._jvalue = new XmlSimpleList(javaResult);
    }
    
    private static boolean permits_inner_space(final XmlObject obj) {
        switch (((SimpleValue)obj).instanceType().getPrimitiveType().getBuiltinTypeCode()) {
            case 1:
            case 2:
            case 6:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean contains_white_space(final String s) {
        return s.indexOf(32) >= 0 || s.indexOf(9) >= 0 || s.indexOf(10) >= 0 || s.indexOf(13) >= 0;
    }
    
    public void set_list(final List list) {
        final SchemaType itemType = this._schemaType.getListItemType();
        boolean pushed = false;
        if (this.has_store()) {
            NamespaceContext.push(new NamespaceContext(this.get_store()));
            pushed = true;
        }
        XmlSimpleList xList;
        try {
            final XmlAnySimpleType[] newval = new XmlAnySimpleType[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                final Object entry = list.get(i);
                if (entry instanceof XmlObject && permits_inner_space(list.get(i))) {
                    final String stringrep = list.get(i).toString();
                    if (contains_white_space(stringrep)) {
                        throw new XmlValueOutOfRangeException();
                    }
                }
                newval[i] = itemType.newValue(entry);
            }
            xList = new XmlSimpleList(Arrays.asList(newval));
        }
        finally {
            if (pushed) {
                NamespaceContext.pop();
            }
        }
        if (this._validateOnSet()) {
            validateValue(xList, this._schemaType, XmlListImpl._voorVc);
        }
        this._value = xList;
        this._jvalue = null;
    }
    
    public static void validateValue(final XmlSimpleList items, final SchemaType sType, final ValidationContext context) {
        final XmlObject[] enumvals = sType.getEnumerationValues();
        Label_0075: {
            if (enumvals != null) {
                for (int i = 0; i < enumvals.length; ++i) {
                    if (equal_xmlLists(items, ((XmlObjectBase)enumvals[i]).xlistValue())) {
                        break Label_0075;
                    }
                }
                context.invalid("cvc-enumeration-valid", new Object[] { "list", items, QNameHelper.readable(sType) });
            }
        }
        XmlObject o;
        int j;
        if ((o = sType.getFacet(0)) != null && (j = ((SimpleValue)o).getIntValue()) != items.size()) {
            context.invalid("cvc-length-valid.2", new Object[] { items, new Integer(items.size()), new Integer(j), QNameHelper.readable(sType) });
        }
        if ((o = sType.getFacet(1)) != null && (j = ((SimpleValue)o).getIntValue()) > items.size()) {
            context.invalid("cvc-minLength-valid.2", new Object[] { items, new Integer(items.size()), new Integer(j), QNameHelper.readable(sType) });
        }
        if ((o = sType.getFacet(2)) != null && (j = ((SimpleValue)o).getIntValue()) < items.size()) {
            context.invalid("cvc-maxLength-valid.2", new Object[] { items, new Integer(items.size()), new Integer(j), QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected boolean equal_to(final XmlObject obj) {
        return equal_xmlLists(this._value, ((XmlObjectBase)obj).xlistValue());
    }
    
    private static boolean equal_xmlLists(final List a, final List b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected int value_hash_code() {
        if (this._value == null) {
            return 0;
        }
        int hash = this._value.size();
        int incr = this._value.size() / 9;
        if (incr < 1) {
            incr = 1;
        }
        int i;
        for (i = 0; i < this._value.size(); i += incr) {
            hash *= 19;
            hash += this._value.get(i).hashCode();
        }
        if (i < this._value.size()) {
            hash *= 19;
            hash += this._value.get(i).hashCode();
        }
        return hash;
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateValue((XmlSimpleList)this.xlistValue(), this.schemaType(), ctx);
    }
    
    static {
        EMPTY_STRINGARRAY = new String[0];
    }
}
