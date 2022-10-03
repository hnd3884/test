package com.lowagie.text.pdf;

public class PdfDeveloperExtension
{
    public static final PdfDeveloperExtension ADOBE_1_7_EXTENSIONLEVEL3;
    protected PdfName prefix;
    protected PdfName baseversion;
    protected int extensionLevel;
    
    public PdfDeveloperExtension(final PdfName prefix, final PdfName baseversion, final int extensionLevel) {
        this.prefix = prefix;
        this.baseversion = baseversion;
        this.extensionLevel = extensionLevel;
    }
    
    public PdfName getPrefix() {
        return this.prefix;
    }
    
    public PdfName getBaseversion() {
        return this.baseversion;
    }
    
    public int getExtensionLevel() {
        return this.extensionLevel;
    }
    
    public PdfDictionary getDeveloperExtensions() {
        final PdfDictionary developerextensions = new PdfDictionary();
        developerextensions.put(PdfName.BASEVERSION, this.baseversion);
        developerextensions.put(PdfName.EXTENSIONLEVEL, new PdfNumber(this.extensionLevel));
        return developerextensions;
    }
    
    static {
        ADOBE_1_7_EXTENSIONLEVEL3 = new PdfDeveloperExtension(PdfName.ADBE, PdfWriter.PDF_VERSION_1_7, 3);
    }
}
