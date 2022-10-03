package com.lowagie.text.pdf;

import java.security.cert.Certificate;
import com.lowagie.text.DocWriter;
import java.util.List;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfCopyForms implements PdfViewerPreferences, PdfEncryptionSettings
{
    private PdfCopyFormsImp fc;
    
    public PdfCopyForms(final OutputStream os) throws DocumentException {
        this.fc = new PdfCopyFormsImp(os);
    }
    
    public void addDocument(final PdfReader reader) throws DocumentException, IOException {
        this.fc.addDocument(reader);
    }
    
    public void addDocument(final PdfReader reader, final List pagesToKeep) throws DocumentException, IOException {
        this.fc.addDocument(reader, pagesToKeep);
    }
    
    public void addDocument(final PdfReader reader, final String ranges) throws DocumentException, IOException {
        this.fc.addDocument(reader, SequenceList.expand(ranges, reader.getNumberOfPages()));
    }
    
    public void copyDocumentFields(final PdfReader reader) throws DocumentException {
        this.fc.copyDocumentFields(reader);
    }
    
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final boolean strength128Bits) throws DocumentException {
        this.fc.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }
    
    public void setEncryption(final boolean strength, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength);
    }
    
    public void close() {
        this.fc.close();
    }
    
    public void open() {
        this.fc.openDoc();
    }
    
    public void addJavaScript(final String js) {
        this.fc.addJavaScript(js, !PdfEncodings.isPdfDocEncoding(js));
    }
    
    public void setOutlines(final List outlines) {
        this.fc.setOutlines(outlines);
    }
    
    public PdfWriter getWriter() {
        return this.fc;
    }
    
    public boolean isFullCompression() {
        return this.fc.isFullCompression();
    }
    
    public void setFullCompression() {
        this.fc.setFullCompression();
    }
    
    @Override
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final int encryptionType) throws DocumentException {
        this.fc.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
    }
    
    @Override
    public void addViewerPreference(final PdfName key, final PdfObject value) {
        this.fc.addViewerPreference(key, value);
    }
    
    @Override
    public void setViewerPreferences(final int preferences) {
        this.fc.setViewerPreferences(preferences);
    }
    
    @Override
    public void setEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) throws DocumentException {
        this.fc.setEncryption(certs, permissions, encryptionType);
    }
}
