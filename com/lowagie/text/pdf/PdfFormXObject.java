package com.lowagie.text.pdf;

public class PdfFormXObject extends PdfStream
{
    public static final PdfNumber ZERO;
    public static final PdfNumber ONE;
    public static final PdfLiteral MATRIX;
    
    PdfFormXObject(final PdfTemplate template, final int compressionLevel) {
        this.put(PdfName.TYPE, PdfName.XOBJECT);
        this.put(PdfName.SUBTYPE, PdfName.FORM);
        this.put(PdfName.RESOURCES, template.getResources());
        this.put(PdfName.BBOX, new PdfRectangle(template.getBoundingBox()));
        this.put(PdfName.FORMTYPE, PdfFormXObject.ONE);
        if (template.getLayer() != null) {
            this.put(PdfName.OC, template.getLayer().getRef());
        }
        if (template.getGroup() != null) {
            this.put(PdfName.GROUP, template.getGroup());
        }
        final PdfArray matrix = template.getMatrix();
        if (matrix == null) {
            this.put(PdfName.MATRIX, PdfFormXObject.MATRIX);
        }
        else {
            this.put(PdfName.MATRIX, matrix);
        }
        this.bytes = template.toPdf(null);
        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
        this.flateCompress(compressionLevel);
    }
    
    static {
        ZERO = new PdfNumber(0);
        ONE = new PdfNumber(1);
        MATRIX = new PdfLiteral("[1 0 0 1 0 0]");
    }
}
