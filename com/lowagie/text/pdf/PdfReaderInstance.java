package com.lowagie.text.pdf;

import java.util.Iterator;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.HashMap;

class PdfReaderInstance
{
    static final PdfLiteral IDENTITYMATRIX;
    static final PdfNumber ONE;
    int[] myXref;
    PdfReader reader;
    RandomAccessFileOrArray file;
    HashMap importedPages;
    PdfWriter writer;
    HashMap visited;
    ArrayList nextRound;
    
    PdfReaderInstance(final PdfReader reader, final PdfWriter writer) {
        this.importedPages = new HashMap();
        this.visited = new HashMap();
        this.nextRound = new ArrayList();
        this.reader = reader;
        this.writer = writer;
        this.file = reader.getSafeFile();
        this.myXref = new int[reader.getXrefSize()];
    }
    
    PdfReader getReader() {
        return this.reader;
    }
    
    PdfImportedPage getImportedPage(final int pageNumber) {
        if (!this.reader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        if (pageNumber < 1 || pageNumber > this.reader.getNumberOfPages()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", pageNumber));
        }
        final Integer i = new Integer(pageNumber);
        PdfImportedPage pageT = this.importedPages.get(i);
        if (pageT == null) {
            pageT = new PdfImportedPage(this, this.writer, pageNumber);
            this.importedPages.put(i, pageT);
        }
        return pageT;
    }
    
    int getNewObjectNumber(final int number, final int generation) {
        if (this.myXref[number] == 0) {
            this.myXref[number] = this.writer.getIndirectReferenceNumber();
            this.nextRound.add(new Integer(number));
        }
        return this.myXref[number];
    }
    
    RandomAccessFileOrArray getReaderFile() {
        return this.file;
    }
    
    PdfObject getResources(final int pageNumber) {
        final PdfObject obj = PdfReader.getPdfObjectRelease(this.reader.getPageNRelease(pageNumber).get(PdfName.RESOURCES));
        return obj;
    }
    
    PdfStream getFormXObject(final int pageNumber, final int compressionLevel) throws IOException {
        final PdfDictionary page = this.reader.getPageNRelease(pageNumber);
        final PdfObject contents = PdfReader.getPdfObjectRelease(page.get(PdfName.CONTENTS));
        final PdfDictionary dic = new PdfDictionary();
        byte[] bout = null;
        if (contents != null) {
            if (contents.isStream()) {
                dic.putAll((PdfDictionary)contents);
            }
            else {
                bout = this.reader.getPageContent(pageNumber, this.file);
            }
        }
        else {
            bout = new byte[0];
        }
        dic.put(PdfName.RESOURCES, PdfReader.getPdfObjectRelease(page.get(PdfName.RESOURCES)));
        dic.put(PdfName.TYPE, PdfName.XOBJECT);
        dic.put(PdfName.SUBTYPE, PdfName.FORM);
        final PdfImportedPage impPage = this.importedPages.get(new Integer(pageNumber));
        dic.put(PdfName.BBOX, new PdfRectangle(impPage.getBoundingBox()));
        final PdfArray matrix = impPage.getMatrix();
        if (matrix == null) {
            dic.put(PdfName.MATRIX, PdfReaderInstance.IDENTITYMATRIX);
        }
        else {
            dic.put(PdfName.MATRIX, matrix);
        }
        dic.put(PdfName.FORMTYPE, PdfReaderInstance.ONE);
        PRStream stream;
        if (bout == null) {
            stream = new PRStream((PRStream)contents, dic);
        }
        else {
            stream = new PRStream(this.reader, bout, compressionLevel);
            stream.putAll(dic);
        }
        return stream;
    }
    
    void writeAllVisited() throws IOException {
        while (!this.nextRound.isEmpty()) {
            final ArrayList vec = this.nextRound;
            this.nextRound = new ArrayList();
            for (int k = 0; k < vec.size(); ++k) {
                final Integer i = vec.get(k);
                if (!this.visited.containsKey(i)) {
                    this.visited.put(i, null);
                    final int n = i;
                    this.writer.addToBody(this.reader.getPdfObjectRelease(n), this.myXref[n]);
                }
            }
        }
    }
    
    void writeAllPages() throws IOException {
        try {
            this.file.reOpen();
            for (final PdfImportedPage ip : this.importedPages.values()) {
                this.writer.addToBody(ip.getFormXObject(this.writer.getCompressionLevel()), ip.getIndirectReference());
            }
            this.writeAllVisited();
        }
        finally {
            try {
                this.reader.close();
                this.file.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    static {
        IDENTITYMATRIX = new PdfLiteral("[1 0 0 1 0 0]");
        ONE = new PdfNumber(1);
    }
}
