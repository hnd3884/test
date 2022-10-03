package com.lowagie.text.pdf;

class ColorDetails
{
    PdfIndirectReference indirectReference;
    PdfName colorName;
    PdfSpotColor spotcolor;
    
    ColorDetails(final PdfName colorName, final PdfIndirectReference indirectReference, final PdfSpotColor scolor) {
        this.colorName = colorName;
        this.indirectReference = indirectReference;
        this.spotcolor = scolor;
    }
    
    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }
    
    PdfName getColorName() {
        return this.colorName;
    }
    
    PdfObject getSpotColor(final PdfWriter writer) {
        return this.spotcolor.getSpotObject(writer);
    }
}
