package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.Counter;
import org.w3c.dom.DOMException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import java.util.Iterator;
import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import java.util.List;
import org.w3c.css.sac.Locator;
import com.steadystate.css.userdata.UserDataConstants;
import com.steadystate.css.parser.LexicalUnitImpl;
import org.w3c.css.sac.LexicalUnit;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.CSSPrimitiveValue;

public class CSSValueImpl extends CSSOMObjectImpl implements CSSPrimitiveValue, CSSValueList, CSSFormatable
{
    private static final long serialVersionUID = 406281136418322579L;
    private Object value_;
    
    public Object getValue() {
        return this.value_;
    }
    
    public void setValue(final Object value) {
        this.value_ = value;
    }
    
    public CSSValueImpl(final LexicalUnit value, final boolean forcePrimitive) {
        LexicalUnit parameters = null;
        try {
            parameters = value.getParameters();
        }
        catch (final IllegalStateException ex) {}
        if (!forcePrimitive && value.getNextLexicalUnit() != null) {
            this.value_ = this.getValues(value);
        }
        else if (parameters != null) {
            if (value.getLexicalUnitType() == 38) {
                this.value_ = new RectImpl(value.getParameters());
            }
            else if (value.getLexicalUnitType() == 27) {
                this.value_ = new RGBColorImpl(value.getParameters());
            }
            else if (value.getLexicalUnitType() == 25) {
                this.value_ = new CounterImpl(false, value.getParameters());
            }
            else if (value.getLexicalUnitType() == 26) {
                this.value_ = new CounterImpl(true, value.getParameters());
            }
            else {
                this.value_ = value;
            }
        }
        else {
            this.value_ = value;
        }
        if (value instanceof LexicalUnitImpl) {
            final Locator locator = ((LexicalUnitImpl)value).getLocator();
            if (locator != null) {
                this.setUserData(UserDataConstants.KEY_LOCATOR, locator);
            }
        }
    }
    
    public CSSValueImpl() {
    }
    
    private List<CSSValueImpl> getValues(final LexicalUnit value) {
        final List<CSSValueImpl> values = new ArrayList<CSSValueImpl>();
        for (LexicalUnit lu = value; lu != null; lu = lu.getNextLexicalUnit()) {
            values.add(new CSSValueImpl(lu, true));
        }
        return values;
    }
    
    public CSSValueImpl(final LexicalUnit value) {
        this(value, false);
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        if (this.getCssValueType() == 2) {
            final StringBuilder sb = new StringBuilder();
            final List<?> list = (List<?>)this.value_;
            final Iterator<?> it = list.iterator();
            boolean separate = false;
            while (it.hasNext()) {
                final Object o = it.next();
                final CSSValueImpl cssValue = (CSSValueImpl)o;
                if (separate) {
                    if (cssValue.value_ instanceof LexicalUnit) {
                        final LexicalUnit lu = (LexicalUnit)cssValue.value_;
                        if (lu.getLexicalUnitType() != 0) {
                            sb.append(" ");
                        }
                    }
                    else {
                        sb.append(" ");
                    }
                }
                if (cssValue.value_ instanceof CSSFormatable) {
                    sb.append(((CSSFormatable)o).getCssText(format));
                }
                else {
                    sb.append(o.toString());
                }
                separate = true;
            }
            return sb.toString();
        }
        if (this.value_ instanceof CSSFormatable) {
            return ((CSSFormatable)this.value_).getCssText(format);
        }
        return (this.value_ != null) ? this.value_.toString() : "";
    }
    
    public void setCssText(final String cssText) throws DOMException {
        try {
            final InputSource is = new InputSource((Reader)new StringReader(cssText));
            final CSSOMParser parser = new CSSOMParser();
            final CSSValueImpl v2 = (CSSValueImpl)parser.parsePropertyValue(is);
            this.value_ = v2.value_;
            this.setUserDataMap(v2.getUserDataMap());
        }
        catch (final Exception e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
    }
    
    public short getCssValueType() {
        if (this.value_ instanceof List) {
            return 2;
        }
        if (this.value_ instanceof LexicalUnit && ((LexicalUnit)this.value_).getLexicalUnitType() == 12) {
            return 0;
        }
        return 1;
    }
    
    public short getPrimitiveType() {
        if (this.value_ instanceof LexicalUnit) {
            final LexicalUnit lu = (LexicalUnit)this.value_;
            switch (lu.getLexicalUnitType()) {
                case 12: {
                    return 21;
                }
                case 13:
                case 14: {
                    return 1;
                }
                case 15: {
                    return 3;
                }
                case 16: {
                    return 4;
                }
                case 17: {
                    return 5;
                }
                case 18: {
                    return 8;
                }
                case 19: {
                    return 6;
                }
                case 20: {
                    return 7;
                }
                case 21: {
                    return 9;
                }
                case 22: {
                    return 10;
                }
                case 23: {
                    return 2;
                }
                case 24: {
                    return 20;
                }
                case 25: {
                    return 23;
                }
                case 28: {
                    return 11;
                }
                case 29: {
                    return 13;
                }
                case 30: {
                    return 12;
                }
                case 31: {
                    return 14;
                }
                case 32: {
                    return 15;
                }
                case 33: {
                    return 16;
                }
                case 34: {
                    return 17;
                }
                case 35: {
                    return 21;
                }
                case 36: {
                    return 19;
                }
                case 37: {
                    return 22;
                }
                case 39:
                case 40:
                case 41: {
                    return 19;
                }
                case 42: {
                    return 18;
                }
                default: {
                    return 0;
                }
            }
        }
        else {
            if (this.value_ instanceof RectImpl) {
                return 24;
            }
            if (this.value_ instanceof RGBColorImpl) {
                return 25;
            }
            if (this.value_ instanceof CounterImpl) {
                return 23;
            }
            return 0;
        }
    }
    
    public void setFloatValue(final short unitType, final float floatValue) throws DOMException {
        this.value_ = LexicalUnitImpl.createNumber(null, floatValue);
    }
    
    public float getFloatValue(final short unitType) throws DOMException {
        if (this.value_ instanceof LexicalUnit) {
            final LexicalUnit lu = (LexicalUnit)this.value_;
            return lu.getFloatValue();
        }
        throw new DOMExceptionImpl((short)15, 10);
    }
    
    public void setStringValue(final short stringType, final String stringValue) throws DOMException {
        switch (stringType) {
            case 19: {
                this.value_ = LexicalUnitImpl.createString(null, stringValue);
                break;
            }
            case 20: {
                this.value_ = LexicalUnitImpl.createURI(null, stringValue);
                break;
            }
            case 21: {
                this.value_ = LexicalUnitImpl.createIdent(null, stringValue);
                break;
            }
            case 22: {
                throw new DOMExceptionImpl((short)9, 19);
            }
            default: {
                throw new DOMExceptionImpl((short)15, 11);
            }
        }
    }
    
    public String getStringValue() throws DOMException {
        if (this.value_ instanceof LexicalUnit) {
            final LexicalUnit lu = (LexicalUnit)this.value_;
            if (lu.getLexicalUnitType() == 35 || lu.getLexicalUnitType() == 36 || lu.getLexicalUnitType() == 24 || lu.getLexicalUnitType() == 12 || lu.getLexicalUnitType() == 37) {
                return lu.getStringValue();
            }
            if (lu.getLexicalUnitType() == 41) {
                return lu.toString();
            }
        }
        else if (this.value_ instanceof List) {
            return null;
        }
        throw new DOMExceptionImpl((short)15, 11);
    }
    
    public Counter getCounterValue() throws DOMException {
        if (this.value_ instanceof Counter) {
            return (Counter)this.value_;
        }
        throw new DOMExceptionImpl((short)15, 12);
    }
    
    public Rect getRectValue() throws DOMException {
        if (this.value_ instanceof Rect) {
            return (Rect)this.value_;
        }
        throw new DOMExceptionImpl((short)15, 13);
    }
    
    public RGBColor getRGBColorValue() throws DOMException {
        if (this.value_ instanceof RGBColor) {
            return (RGBColor)this.value_;
        }
        throw new DOMExceptionImpl((short)15, 14);
    }
    
    public int getLength() {
        if (this.value_ instanceof List) {
            return ((List)this.value_).size();
        }
        return 0;
    }
    
    public CSSValue item(final int index) {
        if (this.value_ instanceof List) {
            final List<CSSValue> list = (List<CSSValue>)this.value_;
            return list.get(index);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSValue)) {
            return false;
        }
        final CSSValue cv = (CSSValue)obj;
        return super.equals(obj) && this.getCssValueType() == cv.getCssValueType() && LangUtils.equals(this.getCssText(), cv.getCssText());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.value_);
        return hash;
    }
}
