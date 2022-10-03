package com.steadystate.css.dom;

import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSPrimitiveValue;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.RGBColor;

public class RGBColorImpl implements RGBColor, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 8152675334081993160L;
    private CSSPrimitiveValue red_;
    private CSSPrimitiveValue green_;
    private CSSPrimitiveValue blue_;
    
    public RGBColorImpl(final LexicalUnit lu) throws DOMException {
        LexicalUnit next = lu;
        this.red_ = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();
        if (next != null) {
            if (next.getLexicalUnitType() != 0) {
                throw new DOMException((short)12, "rgb parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (next != null) {
                this.green_ = new CSSValueImpl(next, true);
                next = next.getNextLexicalUnit();
                if (next != null) {
                    if (next.getLexicalUnitType() != 0) {
                        throw new DOMException((short)12, "rgb parameters must be separated by ','.");
                    }
                    next = next.getNextLexicalUnit();
                    this.blue_ = new CSSValueImpl(next, true);
                    next = next.getNextLexicalUnit();
                    if (next != null) {
                        throw new DOMException((short)12, "Too many parameters for rgb function.");
                    }
                }
            }
        }
    }
    
    public RGBColorImpl() {
    }
    
    public CSSPrimitiveValue getRed() {
        return this.red_;
    }
    
    public void setRed(final CSSPrimitiveValue red) {
        this.red_ = red;
    }
    
    public CSSPrimitiveValue getGreen() {
        return this.green_;
    }
    
    public void setGreen(final CSSPrimitiveValue green) {
        this.green_ = green;
    }
    
    public CSSPrimitiveValue getBlue() {
        return this.blue_;
    }
    
    public void setBlue(final CSSPrimitiveValue blue) {
        this.blue_ = blue;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (null != format && format.isRgbAsHex()) {
            sb.append("#").append(this.getColorAsHex(this.red_)).append(this.getColorAsHex(this.green_)).append(this.getColorAsHex(this.blue_));
            return sb.toString();
        }
        sb.append("rgb(").append(this.red_).append(", ").append(this.green_).append(", ").append(this.blue_).append(")");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
    
    private String getColorAsHex(final CSSPrimitiveValue color) {
        return String.format("%02x", Math.round(color.getFloatValue((short)1)));
    }
}
