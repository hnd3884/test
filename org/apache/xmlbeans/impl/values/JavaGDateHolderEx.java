package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.GDateBuilder;
import java.util.Date;
import java.util.Calendar;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.GDateSpecification;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.SchemaType;

public abstract class JavaGDateHolderEx extends XmlObjectBase
{
    private SchemaType _schemaType;
    private GDate _value;
    
    public JavaGDateHolderEx(final SchemaType type, final boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }
    
    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return (this._value == null) ? "" : this._value.toString();
    }
    
    @Override
    protected void set_text(final String s) {
        GDate newVal;
        if (this._validateOnSet()) {
            newVal = validateLexical(s, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        else {
            newVal = lex(s, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        if (this._validateOnSet() && newVal != null) {
            validateValue(newVal, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        this._value = newVal;
    }
    
    public static GDate lex(final String v, final SchemaType sType, final ValidationContext context) {
        GDate date = null;
        try {
            date = new GDate(v);
        }
        catch (final Exception e) {
            context.invalid("date", new Object[] { v });
        }
        if (date != null) {
            if (date.getBuiltinTypeCode() != sType.getPrimitiveType().getBuiltinTypeCode()) {
                context.invalid("date", new Object[] { "wrong type: " + v });
                date = null;
            }
            else if (!date.isValid()) {
                context.invalid("date", new Object[] { v });
                date = null;
            }
        }
        return date;
    }
    
    public static GDate validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        final GDate date = lex(v, sType, context);
        if (date != null && sType.hasPatternFacet() && !sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[] { "date", v, QNameHelper.readable(sType) });
        }
        return date;
    }
    
    public static void validateValue(final GDateSpecification v, final SchemaType sType, final ValidationContext context) {
        if (v.getBuiltinTypeCode() != sType.getPrimitiveType().getBuiltinTypeCode()) {
            context.invalid("date", new Object[] { "Date (" + v + ") does not have the set of fields required for " + QNameHelper.readable(sType) });
        }
        XmlObject x;
        GDate g;
        if ((x = sType.getFacet(3)) != null && v.compareToGDate(g = ((XmlObjectBase)x).gDateValue()) <= 0) {
            context.invalid("cvc-minExclusive-valid", new Object[] { "date", v, g, QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(4)) != null && v.compareToGDate(g = ((XmlObjectBase)x).gDateValue()) < 0) {
            context.invalid("cvc-minInclusive-valid", new Object[] { "date", v, g, QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(6)) != null && v.compareToGDate(g = ((XmlObjectBase)x).gDateValue()) >= 0) {
            context.invalid("cvc-maxExclusive-valid", new Object[] { "date", v, g, QNameHelper.readable(sType) });
        }
        if ((x = sType.getFacet(5)) != null && v.compareToGDate(g = ((XmlObjectBase)x).gDateValue()) > 0) {
            context.invalid("cvc-maxInclusive-valid", new Object[] { "date", v, g, QNameHelper.readable(sType) });
        }
        final XmlObject[] vals = sType.getEnumerationValues();
        if (vals != null) {
            for (int i = 0; i < vals.length; ++i) {
                if (v.compareToGDate(((XmlObjectBase)vals[i]).gDateValue()) == 0) {
                    return;
                }
            }
            context.invalid("cvc-enumeration-valid", new Object[] { "date", v, QNameHelper.readable(sType) });
        }
    }
    
    @Override
    protected void set_nil() {
        this._value = null;
    }
    
    @Override
    public int getIntValue() {
        final int code = this.schemaType().getPrimitiveType().getBuiltinTypeCode();
        if (code != 20 && code != 21 && code != 18) {
            throw new XmlValueOutOfRangeException();
        }
        this.check_dated();
        if (this._value == null) {
            return 0;
        }
        switch (code) {
            case 20: {
                return this._value.getDay();
            }
            case 21: {
                return this._value.getMonth();
            }
            case 18: {
                return this._value.getYear();
            }
            default: {
                assert false;
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public GDate getGDateValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        return this._value;
    }
    
    @Override
    public Calendar getCalendarValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        return this._value.getCalendar();
    }
    
    @Override
    public Date getDateValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        return this._value.getDate();
    }
    
    @Override
    protected void set_int(final int v) {
        final int code = this.schemaType().getPrimitiveType().getBuiltinTypeCode();
        if (code != 20 && code != 21 && code != 18) {
            throw new XmlValueOutOfRangeException();
        }
        final GDateBuilder value = new GDateBuilder();
        switch (code) {
            case 20: {
                value.setDay(v);
                break;
            }
            case 21: {
                value.setMonth(v);
                break;
            }
            case 18: {
                value.setYear(v);
                break;
            }
        }
        if (this._validateOnSet()) {
            validateValue(value, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        this._value = value.toGDate();
    }
    
    @Override
    protected void set_GDate(GDateSpecification v) {
        final int code = this.schemaType().getPrimitiveType().getBuiltinTypeCode();
        GDate candidate;
        if (v.isImmutable() && v instanceof GDate && v.getBuiltinTypeCode() == code) {
            candidate = (GDate)v;
        }
        else {
            if (v.getBuiltinTypeCode() != code) {
                final GDateBuilder gDateBuilder = new GDateBuilder(v);
                gDateBuilder.setBuiltinTypeCode(code);
                v = gDateBuilder;
            }
            candidate = new GDate(v);
        }
        if (this._validateOnSet()) {
            validateValue(candidate, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        this._value = candidate;
    }
    
    @Override
    protected void set_Calendar(final Calendar c) {
        final int code = this.schemaType().getPrimitiveType().getBuiltinTypeCode();
        final GDateBuilder gDateBuilder = new GDateBuilder(c);
        gDateBuilder.setBuiltinTypeCode(code);
        final GDate value = gDateBuilder.toGDate();
        if (this._validateOnSet()) {
            validateValue(value, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        this._value = value;
    }
    
    @Override
    protected void set_Date(final Date v) {
        final int code = this.schemaType().getPrimitiveType().getBuiltinTypeCode();
        if ((code != 16 && code != 14) || v == null) {
            throw new XmlValueOutOfRangeException();
        }
        final GDateBuilder gDateBuilder = new GDateBuilder(v);
        gDateBuilder.setBuiltinTypeCode(code);
        final GDate value = gDateBuilder.toGDate();
        if (this._validateOnSet()) {
            validateValue(value, this._schemaType, JavaGDateHolderEx._voorVc);
        }
        this._value = value;
    }
    
    @Override
    protected int compare_to(final XmlObject obj) {
        return this._value.compareToGDate(((XmlObjectBase)obj).gDateValue());
    }
    
    @Override
    protected boolean equal_to(final XmlObject obj) {
        return this._value.equals(((XmlObjectBase)obj).gDateValue());
    }
    
    @Override
    protected int value_hash_code() {
        return this._value.hashCode();
    }
    
    @Override
    protected void validate_simpleval(final String lexical, final ValidationContext ctx) {
        validateLexical(lexical, this.schemaType(), ctx);
        validateValue(this.gDateValue(), this.schemaType(), ctx);
    }
}
