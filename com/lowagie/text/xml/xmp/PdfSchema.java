package com.lowagie.text.xml.xmp;

import com.lowagie.text.Document;

public class PdfSchema extends XmpSchema
{
    private static final long serialVersionUID = -1541148669123992185L;
    public static final String DEFAULT_XPATH_ID = "pdf";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
    public static final String KEYWORDS = "pdf:keywords";
    public static final String VERSION = "pdf:PDFVersion";
    public static final String PRODUCER = "pdf:Producer";
    
    public PdfSchema() {
        super("xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\"");
        this.addProducer(Document.getVersion());
    }
    
    public void addKeywords(final String keywords) {
        this.setProperty("pdf:keywords", keywords);
    }
    
    public void addProducer(final String producer) {
        this.setProperty("pdf:Producer", producer);
    }
    
    public void addVersion(final String version) {
        this.setProperty("pdf:PDFVersion", version);
    }
}
