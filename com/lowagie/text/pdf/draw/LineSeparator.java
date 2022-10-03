package com.lowagie.text.pdf.draw;

import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;

public class LineSeparator extends VerticalPositionMark
{
    protected float lineWidth;
    protected float percentage;
    protected Color lineColor;
    protected int alignment;
    
    public LineSeparator(final float lineWidth, final float percentage, final Color lineColor, final int align, final float offset) {
        this.lineWidth = 1.0f;
        this.percentage = 100.0f;
        this.alignment = 1;
        this.lineWidth = lineWidth;
        this.percentage = percentage;
        this.lineColor = lineColor;
        this.alignment = align;
        this.offset = offset;
    }
    
    public LineSeparator() {
        this.lineWidth = 1.0f;
        this.percentage = 100.0f;
        this.alignment = 1;
    }
    
    @Override
    public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx, final float ury, final float y) {
        canvas.saveState();
        this.drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }
    
    public void drawLine(final PdfContentByte canvas, final float leftX, final float rightX, final float y) {
        float w;
        if (this.getPercentage() < 0.0f) {
            w = -this.getPercentage();
        }
        else {
            w = (rightX - leftX) * this.getPercentage() / 100.0f;
        }
        float s = 0.0f;
        switch (this.getAlignment()) {
            case 0: {
                s = 0.0f;
                break;
            }
            case 2: {
                s = rightX - leftX - w;
                break;
            }
            default: {
                s = (rightX - leftX - w) / 2.0f;
                break;
            }
        }
        canvas.setLineWidth(this.getLineWidth());
        if (this.getLineColor() != null) {
            canvas.setColorStroke(this.getLineColor());
        }
        canvas.moveTo(s + leftX, y + this.offset);
        canvas.lineTo(s + w + leftX, y + this.offset);
        canvas.stroke();
    }
    
    public float getLineWidth() {
        return this.lineWidth;
    }
    
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    public float getPercentage() {
        return this.percentage;
    }
    
    public void setPercentage(final float percentage) {
        this.percentage = percentage;
    }
    
    public Color getLineColor() {
        return this.lineColor;
    }
    
    public void setLineColor(final Color color) {
        this.lineColor = color;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int align) {
        this.alignment = align;
    }
}
