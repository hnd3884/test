package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;

public class PdfShadingPattern extends PdfDictionary
{
    protected PdfShading shading;
    protected PdfWriter writer;
    protected float[] matrix;
    protected PdfName patternName;
    protected PdfIndirectReference patternReference;
    
    public PdfShadingPattern(final PdfShading shading) {
        this.matrix = new float[] { 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };
        this.writer = shading.getWriter();
        this.put(PdfName.PATTERNTYPE, new PdfNumber(2));
        this.shading = shading;
    }
    
    PdfName getPatternName() {
        return this.patternName;
    }
    
    PdfName getShadingName() {
        return this.shading.getShadingName();
    }
    
    PdfIndirectReference getPatternReference() {
        if (this.patternReference == null) {
            this.patternReference = this.writer.getPdfIndirectReference();
        }
        return this.patternReference;
    }
    
    PdfIndirectReference getShadingReference() {
        return this.shading.getShadingReference();
    }
    
    void setName(final int number) {
        this.patternName = new PdfName("P" + number);
    }
    
    void addToBody() throws IOException {
        this.put(PdfName.SHADING, this.getShadingReference());
        this.put(PdfName.MATRIX, new PdfArray(this.matrix));
        this.writer.addToBody(this, this.getPatternReference());
    }
    
    public void setMatrix(final float[] matrix) {
        if (matrix.length != 6) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.matrix.size.must.be.6"));
        }
        this.matrix = matrix;
    }
    
    public float[] getMatrix() {
        return this.matrix;
    }
    
    public PdfShading getShading() {
        return this.shading;
    }
    
    ColorDetails getColorDetails() {
        return this.shading.getColorDetails();
    }
}
