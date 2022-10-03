package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.awt.Color;

public final class PdfPatternPainter extends PdfTemplate
{
    float xstep;
    float ystep;
    boolean stencil;
    Color defaultColor;
    
    private PdfPatternPainter() {
        this.stencil = false;
        this.type = 3;
    }
    
    PdfPatternPainter(final PdfWriter wr) {
        super(wr);
        this.stencil = false;
        this.type = 3;
    }
    
    PdfPatternPainter(final PdfWriter wr, final Color defaultColor) {
        this(wr);
        this.stencil = true;
        if (defaultColor == null) {
            this.defaultColor = Color.gray;
        }
        else {
            this.defaultColor = defaultColor;
        }
    }
    
    public void setXStep(final float xstep) {
        this.xstep = xstep;
    }
    
    public void setYStep(final float ystep) {
        this.ystep = ystep;
    }
    
    public float getXStep() {
        return this.xstep;
    }
    
    public float getYStep() {
        return this.ystep;
    }
    
    public boolean isStencil() {
        return this.stencil;
    }
    
    public void setPatternMatrix(final float a, final float b, final float c, final float d, final float e, final float f) {
        this.setMatrix(a, b, c, d, e, f);
    }
    
    PdfPattern getPattern() {
        return new PdfPattern(this);
    }
    
    PdfPattern getPattern(final int compressionLevel) {
        return new PdfPattern(this, compressionLevel);
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        final PdfPatternPainter tpl = new PdfPatternPainter();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.xstep = this.xstep;
        tpl.ystep = this.ystep;
        tpl.matrix = this.matrix;
        tpl.stencil = this.stencil;
        tpl.defaultColor = this.defaultColor;
        return tpl;
    }
    
    public Color getDefaultColor() {
        return this.defaultColor;
    }
    
    @Override
    public void setGrayFill(final float gray) {
        this.checkNoColor();
        super.setGrayFill(gray);
    }
    
    @Override
    public void resetGrayFill() {
        this.checkNoColor();
        super.resetGrayFill();
    }
    
    @Override
    public void setGrayStroke(final float gray) {
        this.checkNoColor();
        super.setGrayStroke(gray);
    }
    
    @Override
    public void resetGrayStroke() {
        this.checkNoColor();
        super.resetGrayStroke();
    }
    
    @Override
    public void setRGBColorFillF(final float red, final float green, final float blue) {
        this.checkNoColor();
        super.setRGBColorFillF(red, green, blue);
    }
    
    @Override
    public void resetRGBColorFill() {
        this.checkNoColor();
        super.resetRGBColorFill();
    }
    
    @Override
    public void setRGBColorStrokeF(final float red, final float green, final float blue) {
        this.checkNoColor();
        super.setRGBColorStrokeF(red, green, blue);
    }
    
    @Override
    public void resetRGBColorStroke() {
        this.checkNoColor();
        super.resetRGBColorStroke();
    }
    
    @Override
    public void setCMYKColorFillF(final float cyan, final float magenta, final float yellow, final float black) {
        this.checkNoColor();
        super.setCMYKColorFillF(cyan, magenta, yellow, black);
    }
    
    @Override
    public void resetCMYKColorFill() {
        this.checkNoColor();
        super.resetCMYKColorFill();
    }
    
    @Override
    public void setCMYKColorStrokeF(final float cyan, final float magenta, final float yellow, final float black) {
        this.checkNoColor();
        super.setCMYKColorStrokeF(cyan, magenta, yellow, black);
    }
    
    @Override
    public void resetCMYKColorStroke() {
        this.checkNoColor();
        super.resetCMYKColorStroke();
    }
    
    @Override
    public void addImage(final Image image, final float a, final float b, final float c, final float d, final float e, final float f) throws DocumentException {
        if (this.stencil && !image.isMask()) {
            this.checkNoColor();
        }
        super.addImage(image, a, b, c, d, e, f);
    }
    
    @Override
    public void setCMYKColorFill(final int cyan, final int magenta, final int yellow, final int black) {
        this.checkNoColor();
        super.setCMYKColorFill(cyan, magenta, yellow, black);
    }
    
    @Override
    public void setCMYKColorStroke(final int cyan, final int magenta, final int yellow, final int black) {
        this.checkNoColor();
        super.setCMYKColorStroke(cyan, magenta, yellow, black);
    }
    
    @Override
    public void setRGBColorFill(final int red, final int green, final int blue) {
        this.checkNoColor();
        super.setRGBColorFill(red, green, blue);
    }
    
    @Override
    public void setRGBColorStroke(final int red, final int green, final int blue) {
        this.checkNoColor();
        super.setRGBColorStroke(red, green, blue);
    }
    
    @Override
    public void setColorStroke(final Color color) {
        this.checkNoColor();
        super.setColorStroke(color);
    }
    
    @Override
    public void setColorFill(final Color color) {
        this.checkNoColor();
        super.setColorFill(color);
    }
    
    @Override
    public void setColorFill(final PdfSpotColor sp, final float tint) {
        this.checkNoColor();
        super.setColorFill(sp, tint);
    }
    
    @Override
    public void setColorStroke(final PdfSpotColor sp, final float tint) {
        this.checkNoColor();
        super.setColorStroke(sp, tint);
    }
    
    @Override
    public void setPatternFill(final PdfPatternPainter p) {
        this.checkNoColor();
        super.setPatternFill(p);
    }
    
    @Override
    public void setPatternFill(final PdfPatternPainter p, final Color color, final float tint) {
        this.checkNoColor();
        super.setPatternFill(p, color, tint);
    }
    
    @Override
    public void setPatternStroke(final PdfPatternPainter p, final Color color, final float tint) {
        this.checkNoColor();
        super.setPatternStroke(p, color, tint);
    }
    
    @Override
    public void setPatternStroke(final PdfPatternPainter p) {
        this.checkNoColor();
        super.setPatternStroke(p);
    }
    
    void checkNoColor() {
        if (this.stencil) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("colors.are.not.allowed.in.uncolored.tile.patterns"));
        }
    }
}
