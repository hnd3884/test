package com.lowagie.text.pdf;

import java.awt.Color;

class PdfColor extends PdfArray
{
    PdfColor(final int red, final int green, final int blue) {
        super(new PdfNumber((red & 0xFF) / 255.0));
        this.add(new PdfNumber((green & 0xFF) / 255.0));
        this.add(new PdfNumber((blue & 0xFF) / 255.0));
    }
    
    PdfColor(final Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
    }
}
