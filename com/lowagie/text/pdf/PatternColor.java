package com.lowagie.text.pdf;

public class PatternColor extends ExtendedColor
{
    private static final long serialVersionUID = -1185448552860615964L;
    PdfPatternPainter painter;
    
    public PatternColor(final PdfPatternPainter painter) {
        super(4, 0.5f, 0.5f, 0.5f);
        this.painter = painter;
    }
    
    public PdfPatternPainter getPainter() {
        return this.painter;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    @Override
    public int hashCode() {
        return this.painter.hashCode();
    }
}
