package com.lowagie.text.pdf;

import java.util.HashMap;

public class PdfPage extends PdfDictionary
{
    private static final String[] boxStrings;
    private static final PdfName[] boxNames;
    public static final PdfNumber PORTRAIT;
    public static final PdfNumber LANDSCAPE;
    public static final PdfNumber INVERTEDPORTRAIT;
    public static final PdfNumber SEASCAPE;
    PdfRectangle mediaBox;
    
    PdfPage(final PdfRectangle mediaBox, final HashMap boxSize, final PdfDictionary resources, final int rotate) {
        super(PdfPage.PAGE);
        this.mediaBox = mediaBox;
        this.put(PdfName.MEDIABOX, mediaBox);
        this.put(PdfName.RESOURCES, resources);
        if (rotate != 0) {
            this.put(PdfName.ROTATE, new PdfNumber(rotate));
        }
        for (int k = 0; k < PdfPage.boxStrings.length; ++k) {
            final PdfObject rect = boxSize.get(PdfPage.boxStrings[k]);
            if (rect != null) {
                this.put(PdfPage.boxNames[k], rect);
            }
        }
    }
    
    PdfPage(final PdfRectangle mediaBox, final HashMap boxSize, final PdfDictionary resources) {
        this(mediaBox, boxSize, resources, 0);
    }
    
    public boolean isParent() {
        return false;
    }
    
    void add(final PdfIndirectReference contents) {
        this.put(PdfName.CONTENTS, contents);
    }
    
    PdfRectangle rotateMediaBox() {
        this.mediaBox = this.mediaBox.rotate();
        this.put(PdfName.MEDIABOX, this.mediaBox);
        return this.mediaBox;
    }
    
    PdfRectangle getMediaBox() {
        return this.mediaBox;
    }
    
    static {
        boxStrings = new String[] { "crop", "trim", "art", "bleed" };
        boxNames = new PdfName[] { PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX };
        PORTRAIT = new PdfNumber(0);
        LANDSCAPE = new PdfNumber(90);
        INVERTEDPORTRAIT = new PdfNumber(180);
        SEASCAPE = new PdfNumber(270);
    }
}
