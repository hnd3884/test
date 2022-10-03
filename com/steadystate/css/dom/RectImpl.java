package com.steadystate.css.dom;

import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.css.CSSPrimitiveValue;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.Rect;

public class RectImpl implements Rect, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -7031248513917920621L;
    private CSSPrimitiveValue top_;
    private CSSPrimitiveValue right_;
    private CSSPrimitiveValue bottom_;
    private CSSPrimitiveValue left_;
    
    public RectImpl(final LexicalUnit lu) throws DOMException {
        if (lu == null) {
            throw new DOMException((short)12, "Rect misses first parameter.");
        }
        this.top_ = new CSSValueImpl(lu, true);
        LexicalUnit next = lu.getNextLexicalUnit();
        if (next == null) {
            throw new DOMException((short)12, "Rect misses second parameter.");
        }
        boolean isCommaSeparated = false;
        if (next.getLexicalUnitType() == 0) {
            isCommaSeparated = true;
            next = next.getNextLexicalUnit();
            if (next == null) {
                throw new DOMException((short)12, "Rect misses second parameter.");
            }
        }
        this.right_ = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();
        if (next == null) {
            throw new DOMException((short)12, "Rect misses third parameter.");
        }
        if (isCommaSeparated) {
            if (next.getLexicalUnitType() != 0) {
                throw new DOMException((short)12, "All or none rect parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (next == null) {
                throw new DOMException((short)12, "Rect misses third parameter.");
            }
        }
        else if (next.getLexicalUnitType() == 0) {
            throw new DOMException((short)12, "All or none rect parameters must be separated by ','.");
        }
        this.bottom_ = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();
        if (next == null) {
            throw new DOMException((short)12, "Rect misses fourth parameter.");
        }
        if (isCommaSeparated) {
            if (next.getLexicalUnitType() != 0) {
                throw new DOMException((short)12, "All or none rect parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (next == null) {
                throw new DOMException((short)12, "Rect misses fourth parameter.");
            }
        }
        else if (next.getLexicalUnitType() == 0) {
            throw new DOMException((short)12, "All or none rect parameters must be separated by ','.");
        }
        this.left_ = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();
        if (next != null) {
            throw new DOMException((short)12, "Too many parameters for rect function.");
        }
    }
    
    public RectImpl() {
    }
    
    public CSSPrimitiveValue getTop() {
        return this.top_;
    }
    
    public void setTop(final CSSPrimitiveValue top) {
        this.top_ = top;
    }
    
    public CSSPrimitiveValue getRight() {
        return this.right_;
    }
    
    public void setRight(final CSSPrimitiveValue right) {
        this.right_ = right;
    }
    
    public CSSPrimitiveValue getBottom() {
        return this.bottom_;
    }
    
    public void setBottom(final CSSPrimitiveValue bottom) {
        this.bottom_ = bottom;
    }
    
    public CSSPrimitiveValue getLeft() {
        return this.left_;
    }
    
    public void setLeft(final CSSPrimitiveValue left) {
        this.left_ = left;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        return "rect(" + this.top_ + ", " + this.right_ + ", " + this.bottom_ + ", " + this.left_ + ")";
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
