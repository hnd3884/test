package com.lowagie.text.pdf;

public class ShadingColor extends ExtendedColor
{
    private static final long serialVersionUID = 4817929454941328671L;
    PdfShadingPattern shadingPattern;
    
    public ShadingColor(final PdfShadingPattern shadingPattern) {
        super(5, 0.5f, 0.5f, 0.5f);
        this.shadingPattern = shadingPattern;
    }
    
    public PdfShadingPattern getPdfShadingPattern() {
        return this.shadingPattern;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    @Override
    public int hashCode() {
        return this.shadingPattern.hashCode();
    }
}
