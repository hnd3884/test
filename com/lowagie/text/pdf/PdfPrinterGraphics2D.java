package com.lowagie.text.pdf;

import java.awt.print.PrinterJob;
import java.awt.print.PrinterGraphics;

public class PdfPrinterGraphics2D extends PdfGraphics2D implements PrinterGraphics
{
    private PrinterJob printerJob;
    
    public PdfPrinterGraphics2D(final PdfContentByte cb, final float width, final float height, final FontMapper fontMapper, final boolean onlyShapes, final boolean convertImagesToJPEG, final float quality, final PrinterJob printerJob) {
        super(cb, width, height, fontMapper, onlyShapes, convertImagesToJPEG, quality);
        this.printerJob = printerJob;
    }
    
    @Override
    public PrinterJob getPrinterJob() {
        return this.printerJob;
    }
}
