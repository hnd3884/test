package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;

public class PdfImportedPage extends PdfTemplate
{
    PdfReaderInstance readerInstance;
    int pageNumber;
    
    PdfImportedPage(final PdfReaderInstance readerInstance, final PdfWriter writer, final int pageNumber) {
        this.readerInstance = readerInstance;
        this.pageNumber = pageNumber;
        this.writer = writer;
        this.bBox = readerInstance.getReader().getPageSize(pageNumber);
        this.setMatrix(1.0f, 0.0f, 0.0f, 1.0f, -this.bBox.getLeft(), -this.bBox.getBottom());
        this.type = 2;
    }
    
    public PdfImportedPage getFromReader() {
        return this;
    }
    
    public int getPageNumber() {
        return this.pageNumber;
    }
    
    @Override
    public void addImage(final Image image, final float a, final float b, final float c, final float d, final float e, final float f) throws DocumentException {
        this.throwError();
    }
    
    @Override
    public void addTemplate(final PdfTemplate template, final float a, final float b, final float c, final float d, final float e, final float f) {
        this.throwError();
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        this.throwError();
        return null;
    }
    
    @Override
    PdfStream getFormXObject(final int compressionLevel) throws IOException {
        return this.readerInstance.getFormXObject(this.pageNumber, compressionLevel);
    }
    
    @Override
    public void setColorFill(final PdfSpotColor sp, final float tint) {
        this.throwError();
    }
    
    @Override
    public void setColorStroke(final PdfSpotColor sp, final float tint) {
        this.throwError();
    }
    
    @Override
    PdfObject getResources() {
        return this.readerInstance.getResources(this.pageNumber);
    }
    
    @Override
    public void setFontAndSize(final BaseFont bf, final float size) {
        this.throwError();
    }
    
    @Override
    public void setGroup(final PdfTransparencyGroup group) {
        this.throwError();
    }
    
    void throwError() {
        throw new RuntimeException(MessageLocalization.getComposedMessage("content.can.not.be.added.to.a.pdfimportedpage"));
    }
    
    PdfReaderInstance getPdfReaderInstance() {
        return this.readerInstance;
    }
}
