package com.lowagie.text.pdf;

import java.util.StringTokenizer;

public class PdfDestination extends PdfArray
{
    public static final int XYZ = 0;
    public static final int FIT = 1;
    public static final int FITH = 2;
    public static final int FITV = 3;
    public static final int FITR = 4;
    public static final int FITB = 5;
    public static final int FITBH = 6;
    public static final int FITBV = 7;
    private boolean status;
    
    public PdfDestination(final int type) {
        this.status = false;
        if (type == 5) {
            this.add(PdfName.FITB);
        }
        else {
            this.add(PdfName.FIT);
        }
    }
    
    public PdfDestination(final int type, final float parameter) {
        super(new PdfNumber(parameter));
        this.status = false;
        switch (type) {
            default: {
                this.addFirst(PdfName.FITH);
                break;
            }
            case 3: {
                this.addFirst(PdfName.FITV);
                break;
            }
            case 6: {
                this.addFirst(PdfName.FITBH);
                break;
            }
            case 7: {
                this.addFirst(PdfName.FITBV);
                break;
            }
        }
    }
    
    public PdfDestination(final int type, final float left, final float top, final float zoom) {
        super(PdfName.XYZ);
        this.status = false;
        if (left < 0.0f) {
            this.add(PdfNull.PDFNULL);
        }
        else {
            this.add(new PdfNumber(left));
        }
        if (top < 0.0f) {
            this.add(PdfNull.PDFNULL);
        }
        else {
            this.add(new PdfNumber(top));
        }
        this.add(new PdfNumber(zoom));
    }
    
    public PdfDestination(final int type, final float left, final float bottom, final float right, final float top) {
        super(PdfName.FITR);
        this.status = false;
        this.add(new PdfNumber(left));
        this.add(new PdfNumber(bottom));
        this.add(new PdfNumber(right));
        this.add(new PdfNumber(top));
    }
    
    public PdfDestination(final String dest) {
        this.status = false;
        final StringTokenizer tokens = new StringTokenizer(dest);
        if (tokens.hasMoreTokens()) {
            this.add(new PdfName(tokens.nextToken()));
        }
        while (tokens.hasMoreTokens()) {
            this.add(new PdfNumber(tokens.nextToken()));
        }
    }
    
    public boolean hasPage() {
        return this.status;
    }
    
    public boolean addPage(final PdfIndirectReference page) {
        if (!this.status) {
            this.addFirst(page);
            return this.status = true;
        }
        return false;
    }
}
