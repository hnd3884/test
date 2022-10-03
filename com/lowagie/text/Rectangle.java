package com.lowagie.text;

import com.lowagie.text.pdf.GrayColor;
import java.util.ArrayList;
import java.awt.Color;

public class Rectangle implements Element
{
    public static final int UNDEFINED = -1;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int NO_BORDER = 0;
    public static final int BOX = 15;
    protected float llx;
    protected float lly;
    protected float urx;
    protected float ury;
    protected int rotation;
    protected Color backgroundColor;
    protected int border;
    protected boolean useVariableBorders;
    protected float borderWidth;
    protected float borderWidthLeft;
    protected float borderWidthRight;
    protected float borderWidthTop;
    protected float borderWidthBottom;
    protected Color borderColor;
    protected Color borderColorLeft;
    protected Color borderColorRight;
    protected Color borderColorTop;
    protected Color borderColorBottom;
    
    public Rectangle(final float llx, final float lly, final float urx, final float ury) {
        this.rotation = 0;
        this.backgroundColor = null;
        this.border = -1;
        this.useVariableBorders = false;
        this.borderWidth = -1.0f;
        this.borderWidthLeft = -1.0f;
        this.borderWidthRight = -1.0f;
        this.borderWidthTop = -1.0f;
        this.borderWidthBottom = -1.0f;
        this.borderColor = null;
        this.borderColorLeft = null;
        this.borderColorRight = null;
        this.borderColorTop = null;
        this.borderColorBottom = null;
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }
    
    public Rectangle(final float urx, final float ury) {
        this(0.0f, 0.0f, urx, ury);
    }
    
    public Rectangle(final float llx, final float lly, final float urx, final float ury, final int rotation) {
        this(llx, lly, urx, ury);
        this.setRotation(rotation);
    }
    
    public Rectangle(final float urx, final float ury, final int rotation) {
        this(0.0f, 0.0f, urx, ury);
        this.setRotation(rotation);
    }
    
    public void setRotation(final int rotation) {
        final int mod = rotation % 360;
        if (mod == 90 || mod == 180 || mod == 270) {
            this.rotation = mod;
        }
        else {
            this.rotation = 0;
        }
    }
    
    public Rectangle(final Rectangle rect) {
        this(rect.llx, rect.lly, rect.urx, rect.ury);
        this.cloneNonPositionParameters(rect);
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 30;
    }
    
    @Override
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    public void setLeft(final float llx) {
        this.llx = llx;
    }
    
    public float getLeft() {
        return this.llx;
    }
    
    public float getLeft(final float margin) {
        return this.llx + margin;
    }
    
    public void setRight(final float urx) {
        this.urx = urx;
    }
    
    public float getRight() {
        return this.urx;
    }
    
    public float getRight(final float margin) {
        return this.urx - margin;
    }
    
    public float getWidth() {
        return this.urx - this.llx;
    }
    
    public void setTop(final float ury) {
        this.ury = ury;
    }
    
    public float getTop() {
        return this.ury;
    }
    
    public float getTop(final float margin) {
        return this.ury - margin;
    }
    
    public void setBottom(final float lly) {
        this.lly = lly;
    }
    
    public float getBottom() {
        return this.lly;
    }
    
    public float getBottom(final float margin) {
        return this.lly + margin;
    }
    
    public float getHeight() {
        return this.ury - this.lly;
    }
    
    public void normalize() {
        if (this.llx > this.urx) {
            final float a = this.llx;
            this.llx = this.urx;
            this.urx = a;
        }
        if (this.lly > this.ury) {
            final float a = this.lly;
            this.lly = this.ury;
            this.ury = a;
        }
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public Rectangle rotate() {
        final Rectangle rect = new Rectangle(this.lly, this.llx, this.ury, this.urx);
        rect.rotation = this.rotation + 90;
        final Rectangle rectangle = rect;
        rectangle.rotation %= 360;
        return rect;
    }
    
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public float getGrayFill() {
        if (this.backgroundColor instanceof GrayColor) {
            return ((GrayColor)this.backgroundColor).getGray();
        }
        return 0.0f;
    }
    
    public void setGrayFill(final float value) {
        this.backgroundColor = new GrayColor(value);
    }
    
    public int getBorder() {
        return this.border;
    }
    
    public boolean hasBorders() {
        switch (this.border) {
            case -1:
            case 0: {
                return false;
            }
            default: {
                return this.borderWidth > 0.0f || this.borderWidthLeft > 0.0f || this.borderWidthRight > 0.0f || this.borderWidthTop > 0.0f || this.borderWidthBottom > 0.0f;
            }
        }
    }
    
    public boolean hasBorder(final int type) {
        return this.border != -1 && (this.border & type) == type;
    }
    
    public void setBorder(final int border) {
        this.border = border;
    }
    
    public boolean isUseVariableBorders() {
        return this.useVariableBorders;
    }
    
    public void setUseVariableBorders(final boolean useVariableBorders) {
        this.useVariableBorders = useVariableBorders;
    }
    
    public void enableBorderSide(final int side) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border |= side;
    }
    
    public void disableBorderSide(final int side) {
        if (this.border == -1) {
            this.border = 0;
        }
        this.border &= ~side;
    }
    
    public float getBorderWidth() {
        return this.borderWidth;
    }
    
    public void setBorderWidth(final float borderWidth) {
        this.borderWidth = borderWidth;
    }
    
    private float getVariableBorderWidth(final float variableWidthValue, final int side) {
        if ((this.border & side) != 0x0) {
            return (variableWidthValue != -1.0f) ? variableWidthValue : this.borderWidth;
        }
        return 0.0f;
    }
    
    private void updateBorderBasedOnWidth(final float width, final int side) {
        this.useVariableBorders = true;
        if (width > 0.0f) {
            this.enableBorderSide(side);
        }
        else {
            this.disableBorderSide(side);
        }
    }
    
    public float getBorderWidthLeft() {
        return this.getVariableBorderWidth(this.borderWidthLeft, 4);
    }
    
    public void setBorderWidthLeft(final float borderWidthLeft) {
        this.updateBorderBasedOnWidth(this.borderWidthLeft = borderWidthLeft, 4);
    }
    
    public float getBorderWidthRight() {
        return this.getVariableBorderWidth(this.borderWidthRight, 8);
    }
    
    public void setBorderWidthRight(final float borderWidthRight) {
        this.updateBorderBasedOnWidth(this.borderWidthRight = borderWidthRight, 8);
    }
    
    public float getBorderWidthTop() {
        return this.getVariableBorderWidth(this.borderWidthTop, 1);
    }
    
    public void setBorderWidthTop(final float borderWidthTop) {
        this.updateBorderBasedOnWidth(this.borderWidthTop = borderWidthTop, 1);
    }
    
    public float getBorderWidthBottom() {
        return this.getVariableBorderWidth(this.borderWidthBottom, 2);
    }
    
    public void setBorderWidthBottom(final float borderWidthBottom) {
        this.updateBorderBasedOnWidth(this.borderWidthBottom = borderWidthBottom, 2);
    }
    
    public Color getBorderColor() {
        return this.borderColor;
    }
    
    public void setBorderColor(final Color borderColor) {
        this.borderColor = borderColor;
    }
    
    public Color getBorderColorLeft() {
        if (this.borderColorLeft == null) {
            return this.borderColor;
        }
        return this.borderColorLeft;
    }
    
    public void setBorderColorLeft(final Color borderColorLeft) {
        this.borderColorLeft = borderColorLeft;
    }
    
    public Color getBorderColorRight() {
        if (this.borderColorRight == null) {
            return this.borderColor;
        }
        return this.borderColorRight;
    }
    
    public void setBorderColorRight(final Color borderColorRight) {
        this.borderColorRight = borderColorRight;
    }
    
    public Color getBorderColorTop() {
        if (this.borderColorTop == null) {
            return this.borderColor;
        }
        return this.borderColorTop;
    }
    
    public void setBorderColorTop(final Color borderColorTop) {
        this.borderColorTop = borderColorTop;
    }
    
    public Color getBorderColorBottom() {
        if (this.borderColorBottom == null) {
            return this.borderColor;
        }
        return this.borderColorBottom;
    }
    
    public void setBorderColorBottom(final Color borderColorBottom) {
        this.borderColorBottom = borderColorBottom;
    }
    
    public Rectangle rectangle(final float top, final float bottom) {
        final Rectangle tmp = new Rectangle(this);
        if (this.getTop() > top) {
            tmp.setTop(top);
            tmp.disableBorderSide(1);
        }
        if (this.getBottom() < bottom) {
            tmp.setBottom(bottom);
            tmp.disableBorderSide(2);
        }
        return tmp;
    }
    
    public void cloneNonPositionParameters(final Rectangle rect) {
        this.rotation = rect.rotation;
        this.backgroundColor = rect.backgroundColor;
        this.border = rect.border;
        this.useVariableBorders = rect.useVariableBorders;
        this.borderWidth = rect.borderWidth;
        this.borderWidthLeft = rect.borderWidthLeft;
        this.borderWidthRight = rect.borderWidthRight;
        this.borderWidthTop = rect.borderWidthTop;
        this.borderWidthBottom = rect.borderWidthBottom;
        this.borderColor = rect.borderColor;
        this.borderColorLeft = rect.borderColorLeft;
        this.borderColorRight = rect.borderColorRight;
        this.borderColorTop = rect.borderColorTop;
        this.borderColorBottom = rect.borderColorBottom;
    }
    
    public void softCloneNonPositionParameters(final Rectangle rect) {
        if (rect.rotation != 0) {
            this.rotation = rect.rotation;
        }
        if (rect.backgroundColor != null) {
            this.backgroundColor = rect.backgroundColor;
        }
        if (rect.border != -1) {
            this.border = rect.border;
        }
        if (this.useVariableBorders) {
            this.useVariableBorders = rect.useVariableBorders;
        }
        if (rect.borderWidth != -1.0f) {
            this.borderWidth = rect.borderWidth;
        }
        if (rect.borderWidthLeft != -1.0f) {
            this.borderWidthLeft = rect.borderWidthLeft;
        }
        if (rect.borderWidthRight != -1.0f) {
            this.borderWidthRight = rect.borderWidthRight;
        }
        if (rect.borderWidthTop != -1.0f) {
            this.borderWidthTop = rect.borderWidthTop;
        }
        if (rect.borderWidthBottom != -1.0f) {
            this.borderWidthBottom = rect.borderWidthBottom;
        }
        if (rect.borderColor != null) {
            this.borderColor = rect.borderColor;
        }
        if (rect.borderColorLeft != null) {
            this.borderColorLeft = rect.borderColorLeft;
        }
        if (rect.borderColorRight != null) {
            this.borderColorRight = rect.borderColorRight;
        }
        if (rect.borderColorTop != null) {
            this.borderColorTop = rect.borderColorTop;
        }
        if (rect.borderColorBottom != null) {
            this.borderColorBottom = rect.borderColorBottom;
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("Rectangle: ");
        buf.append(this.getWidth());
        buf.append('x');
        buf.append(this.getHeight());
        buf.append(" (rot: ");
        buf.append(this.rotation);
        buf.append(" degrees)");
        return buf.toString();
    }
}
