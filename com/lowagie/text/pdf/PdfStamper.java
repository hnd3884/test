package com.lowagie.text.pdf;

import java.util.Calendar;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.File;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.Image;
import java.util.List;
import java.security.cert.Certificate;
import com.lowagie.text.DocWriter;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.InputStream;
import java.security.SignatureException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import java.util.HashMap;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfStamper implements PdfViewerPreferences, PdfEncryptionSettings
{
    protected PdfStamperImp stamper;
    private HashMap moreInfo;
    private boolean hasSignature;
    private PdfSignatureAppearance sigApp;
    
    public PdfStamper(final PdfReader reader, final OutputStream os) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(reader, os, '\0', false);
    }
    
    public PdfStamper(final PdfReader reader, final OutputStream os, final char pdfVersion) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(reader, os, pdfVersion, false);
    }
    
    public PdfStamper(final PdfReader reader, final OutputStream os, final char pdfVersion, final boolean append) throws DocumentException, IOException {
        this.stamper = new PdfStamperImp(reader, os, pdfVersion, append);
    }
    
    public HashMap getMoreInfo() {
        return this.moreInfo;
    }
    
    public void setMoreInfo(final HashMap moreInfo) {
        this.moreInfo = moreInfo;
    }
    
    public void replacePage(final PdfReader r, final int pageImported, final int pageReplaced) {
        this.stamper.replacePage(r, pageImported, pageReplaced);
    }
    
    public void insertPage(final int pageNumber, final Rectangle mediabox) {
        this.stamper.insertPage(pageNumber, mediabox);
    }
    
    public PdfSignatureAppearance getSignatureAppearance() {
        return this.sigApp;
    }
    
    public void close() throws DocumentException, IOException {
        if (!this.hasSignature) {
            this.stamper.close(this.moreInfo);
            return;
        }
        this.sigApp.preClose();
        final PdfSigGenericPKCS sig = this.sigApp.getSigStandard();
        final PdfLiteral lit = (PdfLiteral)sig.get(PdfName.CONTENTS);
        final int totalBuf = (lit.getPosLength() - 2) / 2;
        byte[] buf = new byte[8192];
        final InputStream inp = this.sigApp.getRangeStream();
        try {
            int n;
            while ((n = inp.read(buf)) > 0) {
                sig.getSigner().update(buf, 0, n);
            }
        }
        catch (final SignatureException se) {
            throw new ExceptionConverter(se);
        }
        buf = new byte[totalBuf];
        final byte[] bsig = sig.getSignerContents();
        System.arraycopy(bsig, 0, buf, 0, bsig.length);
        final PdfString str = new PdfString(buf);
        str.setHexWriting(true);
        final PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.CONTENTS, str);
        this.sigApp.close(dic);
        this.stamper.reader.close();
    }
    
    public PdfContentByte getUnderContent(final int pageNum) {
        return this.stamper.getUnderContent(pageNum);
    }
    
    public PdfContentByte getOverContent(final int pageNum) {
        return this.stamper.getOverContent(pageNum);
    }
    
    public boolean isRotateContents() {
        return this.stamper.isRotateContents();
    }
    
    public void setRotateContents(final boolean rotateContents) {
        this.stamper.setRotateContents(rotateContents);
    }
    
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final boolean strength128Bits) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status"));
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output"));
        }
        this.stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }
    
    @Override
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final int encryptionType) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status"));
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output"));
        }
        this.stamper.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
    }
    
    public void setEncryption(final boolean strength, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength);
    }
    
    public void setEncryption(final int encryptionType, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, encryptionType);
    }
    
    @Override
    public void setEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) throws DocumentException {
        if (this.stamper.isAppend()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status"));
        }
        if (this.stamper.isContentWritten()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output"));
        }
        this.stamper.setEncryption(certs, permissions, encryptionType);
    }
    
    public PdfImportedPage getImportedPage(final PdfReader reader, final int pageNumber) {
        return this.stamper.getImportedPage(reader, pageNumber);
    }
    
    public PdfWriter getWriter() {
        return this.stamper;
    }
    
    public PdfReader getReader() {
        return this.stamper.reader;
    }
    
    public AcroFields getAcroFields() {
        return this.stamper.getAcroFields();
    }
    
    public void setFormFlattening(final boolean flat) {
        this.stamper.setFormFlattening(flat);
    }
    
    public void setFreeTextFlattening(final boolean flat) {
        this.stamper.setFreeTextFlattening(flat);
    }
    
    public void addAnnotation(final PdfAnnotation annot, final int page) {
        this.stamper.addAnnotation(annot, page);
    }
    
    public PdfFormField addSignature(final String name, final int page, final float llx, final float lly, final float urx, final float ury) {
        final PdfAcroForm acroForm = this.stamper.getAcroForm();
        final PdfFormField signature = PdfFormField.createSignature(this.stamper);
        acroForm.setSignatureParams(signature, name, llx, lly, urx, ury);
        acroForm.drawSignatureAppearences(signature, llx, lly, urx, ury);
        this.addAnnotation(signature, page);
        return signature;
    }
    
    public void addComments(final FdfReader fdf) throws IOException {
        this.stamper.addComments(fdf);
    }
    
    public void setOutlines(final List outlines) {
        this.stamper.setOutlines(outlines);
    }
    
    public void setThumbnail(final Image image, final int page) throws DocumentException {
        this.stamper.setThumbnail(image, page);
    }
    
    public boolean partialFormFlattening(final String name) {
        return this.stamper.partialFormFlattening(name);
    }
    
    public void addJavaScript(final String js) {
        this.stamper.addJavaScript(js, !PdfEncodings.isPdfDocEncoding(js));
    }
    
    public void addFileAttachment(final String description, final byte[] fileStore, final String file, final String fileDisplay) throws IOException {
        this.addFileAttachment(description, PdfFileSpecification.fileEmbedded(this.stamper, file, fileDisplay, fileStore));
    }
    
    public void addFileAttachment(final String description, final PdfFileSpecification fs) throws IOException {
        this.stamper.addFileAttachment(description, fs);
    }
    
    public void makePackage(final PdfName initialView) {
        final PdfCollection collection = new PdfCollection(0);
        collection.put(PdfName.VIEW, initialView);
        this.stamper.makePackage(collection);
    }
    
    public void makePackage(final PdfCollection collection) {
        this.stamper.makePackage(collection);
    }
    
    @Override
    public void setViewerPreferences(final int preferences) {
        this.stamper.setViewerPreferences(preferences);
    }
    
    @Override
    public void addViewerPreference(final PdfName key, final PdfObject value) {
        this.stamper.addViewerPreference(key, value);
    }
    
    public void setXmpMetadata(final byte[] xmp) {
        this.stamper.setXmpMetadata(xmp);
    }
    
    public boolean isFullCompression() {
        return this.stamper.isFullCompression();
    }
    
    public void setFullCompression() {
        if (this.stamper.isAppend()) {
            return;
        }
        this.stamper.setFullCompression();
    }
    
    public void setPageAction(final PdfName actionType, final PdfAction action, final int page) throws PdfException {
        this.stamper.setPageAction(actionType, action, page);
    }
    
    public void setDuration(final int seconds, final int page) {
        this.stamper.setDuration(seconds, page);
    }
    
    public void setTransition(final PdfTransition transition, final int page) {
        this.stamper.setTransition(transition, page);
    }
    
    public static PdfStamper createSignature(final PdfReader reader, final OutputStream os, final char pdfVersion, File tempFile, final boolean append) throws DocumentException, IOException {
        PdfStamper stp;
        if (tempFile == null) {
            final ByteBuffer bout = new ByteBuffer();
            stp = new PdfStamper(reader, bout, pdfVersion, append);
            (stp.sigApp = new PdfSignatureAppearance(stp.stamper)).setSigout(bout);
        }
        else {
            if (tempFile.isDirectory()) {
                tempFile = File.createTempFile("pdf", null, tempFile);
            }
            final FileOutputStream fout = new FileOutputStream(tempFile);
            stp = new PdfStamper(reader, fout, pdfVersion, append);
            (stp.sigApp = new PdfSignatureAppearance(stp.stamper)).setTempFile(tempFile);
        }
        stp.sigApp.setOriginalout(os);
        stp.sigApp.setStamper(stp);
        stp.hasSignature = true;
        final PdfDictionary catalog = reader.getCatalog();
        final PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm != null) {
            acroForm.remove(PdfName.NEEDAPPEARANCES);
            stp.stamper.markUsed(acroForm);
        }
        return stp;
    }
    
    public static PdfStamper createSignature(final PdfReader reader, final OutputStream os, final char pdfVersion) throws DocumentException, IOException {
        return createSignature(reader, os, pdfVersion, null, false);
    }
    
    public static PdfStamper createSignature(final PdfReader reader, final OutputStream os, final char pdfVersion, final File tempFile) throws DocumentException, IOException {
        return createSignature(reader, os, pdfVersion, tempFile, false);
    }
    
    public Map getPdfLayers() {
        return this.stamper.getPdfLayers();
    }
    
    public void setIncludeFileID(final boolean includeFileID) {
        this.stamper.setIncludeFileID(includeFileID);
    }
    
    public boolean isIncludeFileID() {
        return this.stamper.isIncludeFileID();
    }
    
    public void setOverrideFileId(final PdfObject overrideFileId) {
        this.stamper.setOverrideFileId(overrideFileId);
    }
    
    public PdfObject getOverrideFileId() {
        return this.stamper.getOverrideFileId();
    }
    
    public Calendar getEnforcedModificationDate() {
        return this.stamper.getModificationDate();
    }
    
    public void setEnforcedModificationDate(final Calendar modificationDate) {
        this.stamper.setModificationDate(modificationDate);
    }
}
