package com.lowagie.text;

import java.awt.Color;
import com.lowagie.text.error_messages.MessageLocalization;

public class RectangleReadOnly extends Rectangle
{
    public RectangleReadOnly(final float llx, final float lly, final float urx, final float ury) {
        super(llx, lly, urx, ury);
    }
    
    public RectangleReadOnly(final float urx, final float ury) {
        super(0.0f, 0.0f, urx, ury);
    }
    
    public RectangleReadOnly(final Rectangle rect) {
        super(rect.llx, rect.lly, rect.urx, rect.ury);
        super.cloneNonPositionParameters(rect);
    }
    
    private void throwReadOnlyError() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("rectanglereadonly.this.rectangle.is.read.only"));
    }
    
    @Override
    public void setLeft(final float llx) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setRight(final float urx) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setTop(final float ury) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBottom(final float lly) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void normalize() {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBackgroundColor(final Color value) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setGrayFill(final float value) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorder(final int border) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setUseVariableBorders(final boolean useVariableBorders) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void enableBorderSide(final int side) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void disableBorderSide(final int side) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderWidth(final float borderWidth) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderWidthLeft(final float borderWidthLeft) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderWidthRight(final float borderWidthRight) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderWidthTop(final float borderWidthTop) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderWidthBottom(final float borderWidthBottom) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderColor(final Color borderColor) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderColorLeft(final Color borderColorLeft) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderColorRight(final Color borderColorRight) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderColorTop(final Color borderColorTop) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void setBorderColorBottom(final Color borderColorBottom) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void cloneNonPositionParameters(final Rectangle rect) {
        this.throwReadOnlyError();
    }
    
    @Override
    public void softCloneNonPositionParameters(final Rectangle rect) {
        this.throwReadOnlyError();
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("RectangleReadOnly: ");
        buf.append(this.getWidth());
        buf.append('x');
        buf.append(this.getHeight());
        buf.append(" (rot: ");
        buf.append(this.rotation);
        buf.append(" degrees)");
        return buf.toString();
    }
}
