package com.lowagie.text.pdf;

import com.lowagie.text.PageSize;
import java.util.Arrays;
import java.util.Stack;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import com.lowagie.text.ExceptionConverter;
import java.security.MessageDigest;
import com.lowagie.bouncycastle.BouncyCastleHelper;
import com.lowagie.text.exceptions.UnsupportedPdfException;
import com.lowagie.text.DocWriter;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.exceptions.InvalidPdfException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Iterator;
import java.util.HashMap;
import com.lowagie.text.Rectangle;
import java.util.Collection;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.io.Closeable;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfReader implements PdfViewerPreferences, Closeable
{
    static final PdfName[] pageInhCandidates;
    private static final byte[] endstream;
    private static final byte[] endobj;
    protected PRTokeniser tokens;
    protected int[] xref;
    protected Map<Integer, IntHashtable> objStmMark;
    protected IntHashtable objStmToOffset;
    protected boolean newXrefType;
    private List<PdfObject> xrefObj;
    PdfDictionary rootPages;
    protected PdfDictionary trailer;
    protected PdfDictionary catalog;
    protected PageRefs pageRefs;
    protected PRAcroForm acroForm;
    protected boolean acroFormParsed;
    protected boolean encrypted;
    protected boolean rebuilt;
    protected int freeXref;
    protected boolean tampered;
    protected int lastXref;
    protected int eofPos;
    protected char pdfVersion;
    protected PdfEncryption decrypt;
    protected byte[] password;
    protected Key certificateKey;
    protected Certificate certificate;
    protected String certificateKeyProvider;
    private boolean ownerPasswordUsed;
    private boolean modificationAllowedWithoutOwnerPassword;
    protected List<PdfObject> strings;
    protected boolean sharedStreams;
    protected boolean consolidateNamedDestinations;
    protected boolean remoteToLocalNamedDestinations;
    protected int rValue;
    protected int pValue;
    private int objNum;
    private int objGen;
    private int fileLength;
    private boolean hybridXref;
    private int lastXrefPartial;
    private boolean partial;
    private PRIndirectReference cryptoRef;
    private final PdfViewerPreferencesImp viewerPreferences;
    private boolean encryptionError;
    private boolean appendable;
    private int readDepth;
    
    protected PdfReader() {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
    }
    
    public PdfReader(final String filename) throws IOException {
        this(filename, null);
    }
    
    public PdfReader(final String filename, final byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(filename);
        this.readPdf();
    }
    
    public PdfReader(final byte[] pdfIn) throws IOException {
        this(pdfIn, null);
    }
    
    public PdfReader(final byte[] pdfIn, final byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(pdfIn);
        this.readPdf();
    }
    
    public PdfReader(final String filename, final Certificate certificate, final Key certificateKey, final String certificateKeyProvider) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.tokens = new PRTokeniser(filename);
        this.readPdf();
    }
    
    public PdfReader(final URL url) throws IOException {
        this(url, null);
    }
    
    public PdfReader(final URL url, final byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(url));
        this.readPdf();
    }
    
    public PdfReader(final InputStream is, final byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.tokens = new PRTokeniser(new RandomAccessFileOrArray(is));
        this.readPdf();
    }
    
    public PdfReader(final InputStream is) throws IOException {
        this(is, null);
    }
    
    public PdfReader(final RandomAccessFileOrArray raf, final byte[] ownerPassword) throws IOException {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.password = ownerPassword;
        this.partial = true;
        this.tokens = new PRTokeniser(raf);
        this.readPdfPartial();
    }
    
    public PdfReader(final PdfReader reader) {
        this.acroForm = null;
        this.acroFormParsed = false;
        this.encrypted = false;
        this.rebuilt = false;
        this.tampered = false;
        this.password = null;
        this.certificateKey = null;
        this.certificate = null;
        this.certificateKeyProvider = null;
        this.modificationAllowedWithoutOwnerPassword = true;
        this.strings = new ArrayList<PdfObject>();
        this.sharedStreams = true;
        this.consolidateNamedDestinations = false;
        this.remoteToLocalNamedDestinations = false;
        this.lastXrefPartial = -1;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.readDepth = 0;
        this.appendable = reader.appendable;
        this.consolidateNamedDestinations = reader.consolidateNamedDestinations;
        this.encrypted = reader.encrypted;
        this.rebuilt = reader.rebuilt;
        this.sharedStreams = reader.sharedStreams;
        this.tampered = reader.tampered;
        this.password = reader.password;
        this.pdfVersion = reader.pdfVersion;
        this.eofPos = reader.eofPos;
        this.freeXref = reader.freeXref;
        this.lastXref = reader.lastXref;
        this.tokens = new PRTokeniser(reader.tokens.getSafeFile());
        if (reader.decrypt != null) {
            this.decrypt = new PdfEncryption(reader.decrypt);
        }
        this.pValue = reader.pValue;
        this.rValue = reader.rValue;
        this.xrefObj = new ArrayList<PdfObject>(reader.xrefObj);
        for (int k = 0; k < reader.xrefObj.size(); ++k) {
            this.xrefObj.set(k, duplicatePdfObject(reader.xrefObj.get(k), this));
        }
        this.pageRefs = new PageRefs(reader.pageRefs, this);
        this.trailer = (PdfDictionary)duplicatePdfObject(reader.trailer, this);
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.fileLength = reader.fileLength;
        this.partial = reader.partial;
        this.hybridXref = reader.hybridXref;
        this.objStmToOffset = reader.objStmToOffset;
        this.xref = reader.xref;
        this.cryptoRef = (PRIndirectReference)duplicatePdfObject(reader.cryptoRef, this);
        this.ownerPasswordUsed = reader.ownerPasswordUsed;
    }
    
    public RandomAccessFileOrArray getSafeFile() {
        return this.tokens.getSafeFile();
    }
    
    protected PdfReaderInstance getPdfReaderInstance(final PdfWriter writer) {
        return new PdfReaderInstance(this, writer);
    }
    
    public int getNumberOfPages() {
        return this.pageRefs.size();
    }
    
    public PdfDictionary getCatalog() {
        return this.catalog;
    }
    
    public PRAcroForm getAcroForm() {
        if (!this.acroFormParsed) {
            this.acroFormParsed = true;
            final PdfObject form = this.catalog.get(PdfName.ACROFORM);
            if (form != null) {
                try {
                    (this.acroForm = new PRAcroForm(this)).readAcroForm((PdfDictionary)getPdfObject(form));
                }
                catch (final Exception e) {
                    this.acroForm = null;
                }
            }
        }
        return this.acroForm;
    }
    
    public int getPageRotation(final int index) {
        return this.getPageRotation(this.pageRefs.getPageNRelease(index));
    }
    
    int getPageRotation(final PdfDictionary page) {
        final PdfNumber rotate = page.getAsNumber(PdfName.ROTATE);
        if (rotate == null) {
            return 0;
        }
        int n = rotate.intValue();
        n %= 360;
        return (n < 0) ? (n + 360) : n;
    }
    
    public Rectangle getPageSizeWithRotation(final int index) {
        return this.getPageSizeWithRotation(this.pageRefs.getPageNRelease(index));
    }
    
    public Rectangle getPageSizeWithRotation(final PdfDictionary page) {
        Rectangle rect = this.getPageSize(page);
        for (int rotation = this.getPageRotation(page); rotation > 0; rotation -= 90) {
            rect = rect.rotate();
        }
        return rect;
    }
    
    public Rectangle getPageSize(final int index) {
        return this.getPageSize(this.pageRefs.getPageNRelease(index));
    }
    
    public Rectangle getPageSize(final PdfDictionary page) {
        final PdfArray mediaBox = page.getAsArray(PdfName.MEDIABOX);
        return getNormalizedRectangle(mediaBox);
    }
    
    public Rectangle getCropBox(final int index) {
        final PdfDictionary page = this.pageRefs.getPageNRelease(index);
        final PdfArray cropBox = (PdfArray)getPdfObjectRelease(page.get(PdfName.CROPBOX));
        if (cropBox == null) {
            return this.getPageSize(page);
        }
        return getNormalizedRectangle(cropBox);
    }
    
    public Rectangle getBoxSize(final int index, final String boxName) {
        final PdfDictionary page = this.pageRefs.getPageNRelease(index);
        PdfArray box = null;
        if (boxName.equals("trim")) {
            box = (PdfArray)getPdfObjectRelease(page.get(PdfName.TRIMBOX));
        }
        else if (boxName.equals("art")) {
            box = (PdfArray)getPdfObjectRelease(page.get(PdfName.ARTBOX));
        }
        else if (boxName.equals("bleed")) {
            box = (PdfArray)getPdfObjectRelease(page.get(PdfName.BLEEDBOX));
        }
        else if (boxName.equals("crop")) {
            box = (PdfArray)getPdfObjectRelease(page.get(PdfName.CROPBOX));
        }
        else if (boxName.equals("media")) {
            box = (PdfArray)getPdfObjectRelease(page.get(PdfName.MEDIABOX));
        }
        if (box == null) {
            return null;
        }
        return getNormalizedRectangle(box);
    }
    
    public Map<String, String> getInfo() {
        final Map<String, String> map = new HashMap<String, String>();
        final PdfDictionary info = this.trailer.getAsDict(PdfName.INFO);
        if (info == null) {
            return map;
        }
        for (final Object o : info.getKeys()) {
            final PdfName key = (PdfName)o;
            final PdfObject obj = getPdfObject(info.get(key));
            if (obj == null) {
                continue;
            }
            String value = obj.toString();
            switch (obj.type()) {
                case 3: {
                    value = ((PdfString)obj).toUnicodeString();
                    break;
                }
                case 4: {
                    value = PdfName.decodeName(value);
                    break;
                }
            }
            map.put(PdfName.decodeName(key.toString()), value);
        }
        return map;
    }
    
    public static Rectangle getNormalizedRectangle(final PdfArray box) {
        final float llx = ((PdfNumber)getPdfObjectRelease(box.getPdfObject(0))).floatValue();
        final float lly = ((PdfNumber)getPdfObjectRelease(box.getPdfObject(1))).floatValue();
        final float urx = ((PdfNumber)getPdfObjectRelease(box.getPdfObject(2))).floatValue();
        final float ury = ((PdfNumber)getPdfObjectRelease(box.getPdfObject(3))).floatValue();
        return new Rectangle(Math.min(llx, urx), Math.min(lly, ury), Math.max(llx, urx), Math.max(lly, ury));
    }
    
    protected void readPdf() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (final Exception e) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (final Exception ne) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("rebuild.failed.1.original.message.2", ne.getMessage(), e.getMessage()));
                }
            }
            try {
                this.readDocObj();
            }
            catch (final Exception e) {
                if (e instanceof BadPasswordException) {
                    throw new BadPasswordException(e.getMessage());
                }
                if (this.rebuilt || this.encryptionError) {
                    throw new InvalidPdfException(e.getMessage());
                }
                this.rebuilt = true;
                this.encrypted = false;
                this.rebuildXref();
                this.lastXref = -1;
                this.readDocObj();
            }
            this.strings.clear();
            this.readPages();
            this.eliminateSharedStreams();
            this.removeUnusedObjects();
        }
        finally {
            try {
                this.tokens.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected void readPdfPartial() throws IOException {
        try {
            this.fileLength = this.tokens.getFile().length();
            this.pdfVersion = this.tokens.checkPdfHeader();
            try {
                this.readXref();
            }
            catch (final Exception e) {
                try {
                    this.rebuilt = true;
                    this.rebuildXref();
                    this.lastXref = -1;
                }
                catch (final Exception ne) {
                    throw new InvalidPdfException(MessageLocalization.getComposedMessage("rebuild.failed.1.original.message.2", ne.getMessage(), e.getMessage()));
                }
            }
            this.readDocObjPartial();
            this.readPages();
        }
        catch (final IOException e2) {
            try {
                this.tokens.close();
            }
            catch (final Exception ex) {}
            throw e2;
        }
    }
    
    private boolean equalsArray(final byte[] ar1, final byte[] ar2, final int size) {
        for (int k = 0; k < size; ++k) {
            if (ar1[k] != ar2[k]) {
                return false;
            }
        }
        return true;
    }
    
    private void readDecryptedDocObj() throws IOException {
        if (this.encrypted) {
            return;
        }
        if (this.trailer == null) {
            return;
        }
        final PdfObject encDic = this.trailer.get(PdfName.ENCRYPT);
        if (encDic == null || encDic.toString().equals("null")) {
            return;
        }
        this.encryptionError = true;
        byte[] encryptionKey = null;
        this.encrypted = true;
        final PdfDictionary enc = (PdfDictionary)getPdfObject(encDic);
        final PdfArray documentIDs = this.trailer.getAsArray(PdfName.ID);
        byte[] documentID = null;
        if (documentIDs != null) {
            final PdfObject o = documentIDs.getPdfObject(0);
            this.strings.remove(o);
            final String s = o.toString();
            documentID = DocWriter.getISOBytes(s);
            if (documentIDs.size() > 1) {
                this.strings.remove(documentIDs.getPdfObject(1));
            }
        }
        if (documentID == null) {
            documentID = new byte[0];
        }
        byte[] uValue = null;
        byte[] oValue = null;
        int cryptoMode = 0;
        int lengthValue = 0;
        final PdfObject filter = getPdfObjectRelease(enc.get(PdfName.FILTER));
        if (filter.equals(PdfName.STANDARD)) {
            String s = enc.get(PdfName.U).toString();
            this.strings.remove(enc.get(PdfName.U));
            uValue = DocWriter.getISOBytes(s);
            s = enc.get(PdfName.O).toString();
            this.strings.remove(enc.get(PdfName.O));
            oValue = DocWriter.getISOBytes(s);
            PdfObject o = enc.get(PdfName.P);
            if (!o.isNumber()) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.p.value"));
            }
            this.pValue = ((PdfNumber)o).intValue();
            o = enc.get(PdfName.R);
            if (!o.isNumber()) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.r.value"));
            }
            switch (this.rValue = ((PdfNumber)o).intValue()) {
                case 2: {
                    cryptoMode = 0;
                    break;
                }
                case 3: {
                    o = enc.get(PdfName.LENGTH);
                    if (!o.isNumber()) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                    }
                    lengthValue = ((PdfNumber)o).intValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                    }
                    cryptoMode = 1;
                    break;
                }
                case 4: {
                    PdfDictionary dic = (PdfDictionary)enc.get(PdfName.CF);
                    if (dic == null) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("cf.not.found.encryption"));
                    }
                    dic = (PdfDictionary)dic.get(PdfName.STDCF);
                    if (dic == null) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("stdcf.not.found.encryption"));
                    }
                    if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                        cryptoMode = 1;
                    }
                    else {
                        if (!PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                            throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("no.compatible.encryption.found"));
                        }
                        cryptoMode = 2;
                    }
                    final PdfObject em = enc.get(PdfName.ENCRYPTMETADATA);
                    if (em != null && em.toString().equals("false")) {
                        cryptoMode |= 0x8;
                        break;
                    }
                    break;
                }
                default: {
                    throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("unknown.encryption.type.r.eq.1", this.rValue));
                }
            }
        }
        else if (filter.equals(PdfName.PUBSEC)) {
            PdfObject o = enc.get(PdfName.V);
            if (!o.isNumber()) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.v.value"));
            }
            final int vValue = ((PdfNumber)o).intValue();
            PdfArray recipients = null;
            switch (vValue) {
                case 1: {
                    cryptoMode = 0;
                    lengthValue = 40;
                    recipients = (PdfArray)enc.get(PdfName.RECIPIENTS);
                    break;
                }
                case 2: {
                    o = enc.get(PdfName.LENGTH);
                    if (!o.isNumber()) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                    }
                    lengthValue = ((PdfNumber)o).intValue();
                    if (lengthValue > 128 || lengthValue < 40 || lengthValue % 8 != 0) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("illegal.length.value"));
                    }
                    cryptoMode = 1;
                    recipients = (PdfArray)enc.get(PdfName.RECIPIENTS);
                    break;
                }
                case 4: {
                    PdfDictionary dic2 = (PdfDictionary)enc.get(PdfName.CF);
                    if (dic2 == null) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("cf.not.found.encryption"));
                    }
                    dic2 = (PdfDictionary)dic2.get(PdfName.DEFAULTCRYPTFILTER);
                    if (dic2 == null) {
                        throw new InvalidPdfException(MessageLocalization.getComposedMessage("defaultcryptfilter.not.found.encryption"));
                    }
                    if (PdfName.V2.equals(dic2.get(PdfName.CFM))) {
                        cryptoMode = 1;
                        lengthValue = 128;
                    }
                    else {
                        if (!PdfName.AESV2.equals(dic2.get(PdfName.CFM))) {
                            throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("no.compatible.encryption.found"));
                        }
                        cryptoMode = 2;
                        lengthValue = 128;
                    }
                    final PdfObject em2 = dic2.get(PdfName.ENCRYPTMETADATA);
                    if (em2 != null && em2.toString().equals("false")) {
                        cryptoMode |= 0x8;
                    }
                    recipients = (PdfArray)dic2.get(PdfName.RECIPIENTS);
                    break;
                }
                default: {
                    throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("unknown.encryption.type.v.eq.1", this.rValue));
                }
            }
            BouncyCastleHelper.checkCertificateEncodingOrThrowException(this.certificate);
            final byte[] envelopedData = BouncyCastleHelper.getEnvelopedData(recipients, this.strings, this.certificate, this.certificateKey, this.certificateKeyProvider);
            if (envelopedData == null) {
                throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("bad.certificate.and.key"));
            }
            try {
                final MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(envelopedData, 0, 20);
                for (int i = 0; i < recipients.size(); ++i) {
                    final byte[] encodedRecipient = recipients.getPdfObject(i).getBytes();
                    md.update(encodedRecipient);
                }
                if ((cryptoMode & 0x8) != 0x0) {
                    md.update(new byte[] { -1, -1, -1, -1 });
                }
                encryptionKey = md.digest();
            }
            catch (final Exception f) {
                throw new ExceptionConverter(f);
            }
        }
        (this.decrypt = new PdfEncryption()).setCryptoMode(cryptoMode, lengthValue);
        if (filter.equals(PdfName.STANDARD)) {
            this.decrypt.setupByOwnerPassword(documentID, this.password, uValue, oValue, this.pValue);
            if (!this.equalsArray(uValue, this.decrypt.userKey, (this.rValue == 3 || this.rValue == 4) ? 16 : 32)) {
                this.decrypt.setupByUserPassword(documentID, this.password, oValue, this.pValue);
                if (!this.equalsArray(uValue, this.decrypt.userKey, (this.rValue == 3 || this.rValue == 4) ? 16 : 32)) {
                    throw new BadPasswordException(MessageLocalization.getComposedMessage("bad.user.password"));
                }
            }
            else {
                this.ownerPasswordUsed = true;
            }
        }
        else if (filter.equals(PdfName.PUBSEC)) {
            this.decrypt.setupByEncryptionKey(encryptionKey, lengthValue);
            this.ownerPasswordUsed = true;
        }
        for (final Object string : this.strings) {
            final PdfString str = (PdfString)string;
            str.decrypt(this);
        }
        if (encDic.isIndirect()) {
            this.cryptoRef = (PRIndirectReference)encDic;
            this.xrefObj.set(this.cryptoRef.getNumber(), null);
        }
        this.encryptionError = false;
    }
    
    public static PdfObject getPdfObjectRelease(final PdfObject obj) {
        final PdfObject obj2 = getPdfObject(obj);
        releaseLastXrefPartial(obj);
        return obj2;
    }
    
    public static PdfObject getPdfObject(PdfObject obj) {
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            return obj;
        }
        try {
            final PRIndirectReference ref = (PRIndirectReference)obj;
            final int idx = ref.getNumber();
            final boolean appendable = ref.getReader().appendable;
            obj = ref.getReader().getPdfObject(idx);
            if (obj == null) {
                return null;
            }
            if (appendable) {
                switch (obj.type()) {
                    case 8: {
                        obj = new PdfNull();
                        break;
                    }
                    case 1: {
                        obj = new PdfBoolean(((PdfBoolean)obj).booleanValue());
                        break;
                    }
                    case 4: {
                        obj = new PdfName(obj.getBytes());
                        break;
                    }
                }
                obj.setIndRef(ref);
            }
            return obj;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static PdfObject getPdfObjectRelease(final PdfObject obj, final PdfObject parent) {
        final PdfObject obj2 = getPdfObject(obj, parent);
        releaseLastXrefPartial(obj);
        return obj2;
    }
    
    public static PdfObject getPdfObject(PdfObject obj, final PdfObject parent) {
        if (obj == null) {
            return null;
        }
        if (!obj.isIndirect()) {
            final PRIndirectReference ref;
            if (parent != null && (ref = parent.getIndRef()) != null && ref.getReader().isAppendable()) {
                switch (obj.type()) {
                    case 8: {
                        obj = new PdfNull();
                        break;
                    }
                    case 1: {
                        obj = new PdfBoolean(((PdfBoolean)obj).booleanValue());
                        break;
                    }
                    case 4: {
                        obj = new PdfName(obj.getBytes());
                        break;
                    }
                }
                obj.setIndRef(ref);
            }
            return obj;
        }
        return getPdfObject(obj);
    }
    
    public PdfObject getPdfObjectRelease(final int idx) {
        final PdfObject obj = this.getPdfObject(idx);
        this.releaseLastXrefPartial();
        return obj;
    }
    
    public PdfObject getPdfObject(final int idx) {
        try {
            this.lastXrefPartial = -1;
            if (idx < 0 || idx >= this.xrefObj.size()) {
                return null;
            }
            PdfObject obj = this.xrefObj.get(idx);
            if (!this.partial || obj != null) {
                return obj;
            }
            if (idx * 2 >= this.xref.length) {
                return null;
            }
            obj = this.readSingleObject(idx);
            this.lastXrefPartial = -1;
            if (obj != null) {
                this.lastXrefPartial = idx;
            }
            return obj;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public void resetLastXrefPartial() {
        this.lastXrefPartial = -1;
    }
    
    public void releaseLastXrefPartial() {
        if (this.partial && this.lastXrefPartial != -1) {
            this.xrefObj.set(this.lastXrefPartial, null);
            this.lastXrefPartial = -1;
        }
    }
    
    public static void releaseLastXrefPartial(final PdfObject obj) {
        if (obj == null) {
            return;
        }
        if (!obj.isIndirect()) {
            return;
        }
        if (!(obj instanceof PRIndirectReference)) {
            return;
        }
        final PRIndirectReference ref = (PRIndirectReference)obj;
        final PdfReader reader = ref.getReader();
        if (reader.partial && reader.lastXrefPartial != -1 && reader.lastXrefPartial == ref.getNumber()) {
            reader.xrefObj.set(reader.lastXrefPartial, null);
        }
        reader.lastXrefPartial = -1;
    }
    
    private void setXrefPartialObject(final int idx, final PdfObject obj) {
        if (!this.partial || idx < 0) {
            return;
        }
        this.xrefObj.set(idx, obj);
    }
    
    public PRIndirectReference addPdfObject(final PdfObject obj) {
        this.xrefObj.add(obj);
        return new PRIndirectReference(this, this.xrefObj.size() - 1);
    }
    
    protected void readPages() throws IOException {
        this.catalog = this.trailer.getAsDict(PdfName.ROOT);
        this.rootPages = this.catalog.getAsDict(PdfName.PAGES);
        this.pageRefs = new PageRefs(this);
    }
    
    protected void readDocObjPartial() throws IOException {
        (this.xrefObj = new ArrayList<PdfObject>(this.xref.length / 2)).addAll((Collection<? extends PdfObject>)Collections.nCopies(this.xref.length / 2, (Object)null));
        this.readDecryptedDocObj();
        if (this.objStmToOffset != null) {
            final int[] keys2;
            final int[] keys = keys2 = this.objStmToOffset.getKeys();
            for (final int n : keys2) {
                this.objStmToOffset.put(n, this.xref[n * 2]);
                this.xref[n * 2] = -1;
            }
        }
    }
    
    protected PdfObject readSingleObject(final int k) throws IOException {
        this.strings.clear();
        final int k2 = k * 2;
        int pos = this.xref[k2];
        if (pos < 0) {
            return null;
        }
        if (this.xref[k2 + 1] > 0) {
            pos = this.objStmToOffset.get(this.xref[k2 + 1]);
        }
        if (pos == 0) {
            return null;
        }
        this.tokens.seek(pos);
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.object.number"));
        }
        this.objNum = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (this.tokens.getTokenType() != 1) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.generation.number"));
        }
        this.objGen = this.tokens.intValue();
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("obj")) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("token.obj.expected"));
        }
        PdfObject obj;
        try {
            obj = this.readPRObject();
            for (final PdfObject string : this.strings) {
                final PdfString str = (PdfString)string;
                str.decrypt(this);
            }
            if (obj.isStream()) {
                this.checkPRStreamLength((PRStream)obj);
            }
        }
        catch (final Exception e) {
            obj = null;
        }
        if (this.xref[k2 + 1] > 0) {
            obj = this.readOneObjStm((PRStream)obj, this.xref[k2]);
        }
        this.xrefObj.set(k, obj);
        return obj;
    }
    
    protected PdfObject readOneObjStm(final PRStream stream, int idx) throws IOException {
        final int first = stream.getAsNumber(PdfName.FIRST).intValue();
        final byte[] b = getStreamBytes(stream, this.tokens.getFile());
        final PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            int address = 0;
            boolean ok = true;
            ++idx;
            for (int k = 0; k < idx; ++k) {
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("error.reading.objstm"));
            }
            this.tokens.seek(address);
            return this.readPRObject();
        }
        finally {
            this.tokens = saveTokens;
        }
    }
    
    public double dumpPerc() {
        int total = 0;
        for (final PdfObject aXrefObj : this.xrefObj) {
            if (aXrefObj != null) {
                ++total;
            }
        }
        return total * 100.0 / this.xrefObj.size();
    }
    
    protected void readDocObj() throws IOException {
        final List<PdfObject> streams = new ArrayList<PdfObject>();
        (this.xrefObj = new ArrayList<PdfObject>(this.xref.length / 2)).addAll((Collection<? extends PdfObject>)Collections.nCopies(this.xref.length / 2, (Object)null));
        for (int k = 2; k < this.xref.length; k += 2) {
            final int pos = this.xref[k];
            if (pos > 0) {
                if (this.xref[k + 1] <= 0) {
                    this.tokens.seek(pos);
                    this.tokens.nextValidToken();
                    if (this.tokens.getTokenType() != 1) {
                        this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.object.number"));
                    }
                    this.objNum = this.tokens.intValue();
                    this.tokens.nextValidToken();
                    if (this.tokens.getTokenType() != 1) {
                        this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.generation.number"));
                    }
                    this.objGen = this.tokens.intValue();
                    this.tokens.nextValidToken();
                    if (!this.tokens.getStringValue().equals("obj")) {
                        this.tokens.throwError(MessageLocalization.getComposedMessage("token.obj.expected"));
                    }
                    PdfObject obj;
                    try {
                        obj = this.readPRObject();
                        if (obj.isStream()) {
                            streams.add(obj);
                        }
                    }
                    catch (final Exception e) {
                        obj = null;
                    }
                    this.xrefObj.set(k / 2, obj);
                }
            }
        }
        for (final PdfObject stream : streams) {
            this.checkPRStreamLength((PRStream)stream);
        }
        this.readDecryptedDocObj();
        if (this.objStmMark != null) {
            for (final Object o : this.objStmMark.entrySet()) {
                final Map.Entry entry = (Map.Entry)o;
                final int n = entry.getKey();
                final IntHashtable h = entry.getValue();
                this.readObjStm(this.xrefObj.get(n), h);
                this.xrefObj.set(n, null);
            }
            this.objStmMark = null;
        }
        this.xref = null;
    }
    
    private void checkPRStreamLength(final PRStream stream) throws IOException {
        final int fileLength = this.tokens.length();
        final int start = stream.getOffset();
        boolean calc = false;
        int streamLength = 0;
        final PdfObject obj = getPdfObjectRelease(stream.get(PdfName.LENGTH));
        if (obj != null && obj.type() == 2) {
            streamLength = ((PdfNumber)obj).intValue();
            if (streamLength + start > fileLength - 20) {
                calc = true;
            }
            else {
                this.tokens.seek(start + streamLength);
                final String line = this.tokens.readString(20);
                if (!line.startsWith("\nendstream") && !line.startsWith("\r\nendstream") && !line.startsWith("\rendstream") && !line.startsWith("endstream")) {
                    calc = true;
                }
            }
        }
        else {
            calc = true;
        }
        if (calc) {
            final byte[] tline = new byte[16];
            this.tokens.seek(start);
            while (true) {
                int pos = this.tokens.getFilePointer();
                if (!this.tokens.readLineSegment(tline)) {
                    break;
                }
                if (equalsn(tline, PdfReader.endstream)) {
                    streamLength = pos - start;
                    break;
                }
                if (equalsn(tline, PdfReader.endobj)) {
                    this.tokens.seek(pos - 16);
                    final String s = this.tokens.readString(16);
                    final int index = s.indexOf("endstream");
                    if (index >= 0) {
                        pos = pos - 16 + index;
                    }
                    streamLength = pos - start;
                    break;
                }
            }
        }
        stream.setLength(streamLength);
    }
    
    protected void readObjStm(final PRStream stream, final IntHashtable map) throws IOException {
        final int first = stream.getAsNumber(PdfName.FIRST).intValue();
        final int n = stream.getAsNumber(PdfName.N).intValue();
        final byte[] b = getStreamBytes(stream, this.tokens.getFile());
        final PRTokeniser saveTokens = this.tokens;
        this.tokens = new PRTokeniser(b);
        try {
            final int[] address = new int[n];
            final int[] objNumber = new int[n];
            boolean ok = true;
            for (int k = 0; k < n; ++k) {
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                objNumber[k] = this.tokens.intValue();
                ok = this.tokens.nextToken();
                if (!ok) {
                    break;
                }
                if (this.tokens.getTokenType() != 1) {
                    ok = false;
                    break;
                }
                address[k] = this.tokens.intValue() + first;
            }
            if (!ok) {
                throw new InvalidPdfException(MessageLocalization.getComposedMessage("error.reading.objstm"));
            }
            for (int k = 0; k < n; ++k) {
                if (map.containsKey(k)) {
                    this.tokens.seek(address[k]);
                    final PdfObject obj = this.readPRObject();
                    this.xrefObj.set(objNumber[k], obj);
                }
            }
        }
        finally {
            this.tokens = saveTokens;
        }
    }
    
    public static PdfObject killIndirect(final PdfObject obj) {
        if (obj == null || obj.isNull()) {
            return null;
        }
        final PdfObject ret = getPdfObjectRelease(obj);
        if (obj.isIndirect()) {
            final PRIndirectReference ref = (PRIndirectReference)obj;
            final PdfReader reader = ref.getReader();
            final int n = ref.getNumber();
            reader.xrefObj.set(n, null);
            if (reader.partial) {
                reader.xref[n * 2] = -1;
            }
        }
        return ret;
    }
    
    private void ensureXrefSize(final int size) {
        if (size == 0) {
            return;
        }
        if (this.xref == null) {
            this.xref = new int[size];
        }
        else if (this.xref.length < size) {
            final int[] xref2 = new int[size];
            System.arraycopy(this.xref, 0, xref2, 0, this.xref.length);
            this.xref = xref2;
        }
    }
    
    protected void readXref() throws IOException {
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(this.tokens.getStartxref());
        this.tokens.nextToken();
        if (!this.tokens.getStringValue().equals("startxref")) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("startxref.not.found"));
        }
        this.tokens.nextToken();
        if (this.tokens.getTokenType() != 1) {
            throw new InvalidPdfException(MessageLocalization.getComposedMessage("startxref.is.not.followed.by.a.number"));
        }
        final int startxref = this.tokens.intValue();
        this.lastXref = startxref;
        this.eofPos = this.tokens.getFilePointer();
        try {
            if (this.readXRefStream(startxref)) {
                this.newXrefType = true;
                return;
            }
        }
        catch (final Exception ex) {}
        this.xref = null;
        this.tokens.seek(startxref);
        this.trailer = this.readXrefSection();
        PdfDictionary trailer2 = this.trailer;
        while (true) {
            final PdfNumber prev = (PdfNumber)trailer2.get(PdfName.PREV);
            if (prev == null) {
                break;
            }
            this.tokens.seek(prev.intValue());
            trailer2 = this.readXrefSection();
        }
    }
    
    protected PdfDictionary readXrefSection() throws IOException {
        this.tokens.nextValidToken();
        if (!this.tokens.getStringValue().equals("xref")) {
            this.tokens.throwError(MessageLocalization.getComposedMessage("xref.subsection.not.found"));
        }
        while (true) {
            this.tokens.nextValidToken();
            if (this.tokens.getStringValue().equals("trailer")) {
                break;
            }
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("object.number.of.the.first.object.in.this.xref.subsection.not.found"));
            }
            int start = this.tokens.intValue();
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() != 1) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("number.of.entries.in.this.xref.subsection.not.found"));
            }
            int end = this.tokens.intValue() + start;
            if (start == 1) {
                final int back = this.tokens.getFilePointer();
                this.tokens.nextValidToken();
                final int pos = this.tokens.intValue();
                this.tokens.nextValidToken();
                final int gen = this.tokens.intValue();
                if (pos == 0 && gen == 65535) {
                    --start;
                    --end;
                }
                this.tokens.seek(back);
            }
            this.ensureXrefSize(end * 2);
            for (int k = start; k < end; ++k) {
                this.tokens.nextValidToken();
                final int pos = this.tokens.intValue();
                this.tokens.nextValidToken();
                this.tokens.nextValidToken();
                final int p = k * 2;
                if (this.tokens.getStringValue().equals("n")) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = pos;
                    }
                }
                else if (this.tokens.getStringValue().equals("f")) {
                    if (this.xref[p] == 0 && this.xref[p + 1] == 0) {
                        this.xref[p] = -1;
                    }
                }
                else {
                    this.tokens.throwError(MessageLocalization.getComposedMessage("invalid.cross.reference.entry.in.this.xref.subsection"));
                }
            }
        }
        final PdfDictionary trailer = (PdfDictionary)this.readPRObject();
        final PdfNumber xrefSize = (PdfNumber)trailer.get(PdfName.SIZE);
        this.ensureXrefSize(xrefSize.intValue() * 2);
        final PdfObject xrs = trailer.get(PdfName.XREFSTM);
        if (xrs != null && xrs.isNumber()) {
            final int loc = ((PdfNumber)xrs).intValue();
            try {
                this.readXRefStream(loc);
                this.newXrefType = true;
                this.hybridXref = true;
            }
            catch (final IOException e) {
                this.xref = null;
                throw e;
            }
        }
        return trailer;
    }
    
    protected boolean readXRefStream(final int ptr) throws IOException {
        this.tokens.seek(ptr);
        if (!this.tokens.nextToken()) {
            return false;
        }
        if (this.tokens.getTokenType() != 1) {
            return false;
        }
        int thisStream = this.tokens.intValue();
        if (!this.tokens.nextToken() || this.tokens.getTokenType() != 1) {
            return false;
        }
        if (!this.tokens.nextToken() || !this.tokens.getStringValue().equals("obj")) {
            return false;
        }
        final PdfObject object = this.readPRObject();
        if (!object.isStream()) {
            return false;
        }
        final PRStream stm = (PRStream)object;
        if (!PdfName.XREF.equals(stm.get(PdfName.TYPE))) {
            return false;
        }
        if (this.trailer == null) {
            (this.trailer = new PdfDictionary()).putAll(stm);
        }
        stm.setLength(((PdfNumber)stm.get(PdfName.LENGTH)).intValue());
        final int size = ((PdfNumber)stm.get(PdfName.SIZE)).intValue();
        PdfObject obj = stm.get(PdfName.INDEX);
        PdfArray index;
        if (obj == null) {
            index = new PdfArray();
            index.add(new int[] { 0, size });
        }
        else {
            index = (PdfArray)obj;
        }
        final PdfArray w = (PdfArray)stm.get(PdfName.W);
        int prev = -1;
        obj = stm.get(PdfName.PREV);
        if (obj != null) {
            prev = ((PdfNumber)obj).intValue();
        }
        this.ensureXrefSize(size * 2);
        if (this.objStmMark == null && !this.partial) {
            this.objStmMark = new HashMap<Integer, IntHashtable>();
        }
        if (this.objStmToOffset == null && this.partial) {
            this.objStmToOffset = new IntHashtable();
        }
        final byte[] b = getStreamBytes(stm, this.tokens.getFile());
        int bptr = 0;
        final int[] wc = new int[3];
        for (int k = 0; k < 3; ++k) {
            wc[k] = w.getAsNumber(k).intValue();
        }
        for (int idx = 0; idx < index.size(); idx += 2) {
            int start = index.getAsNumber(idx).intValue();
            int length = index.getAsNumber(idx + 1).intValue();
            this.ensureXrefSize((start + length) * 2);
            while (length-- > 0) {
                int type = 1;
                if (wc[0] > 0) {
                    type = 0;
                    for (int i = 0; i < wc[0]; ++i) {
                        type = (type << 8) + (b[bptr++] & 0xFF);
                    }
                }
                int field2 = 0;
                for (int j = 0; j < wc[1]; ++j) {
                    field2 = (field2 << 8) + (b[bptr++] & 0xFF);
                }
                int field3 = 0;
                for (int l = 0; l < wc[2]; ++l) {
                    field3 = (field3 << 8) + (b[bptr++] & 0xFF);
                }
                final int base = start * 2;
                if (this.xref[base] == 0 && this.xref[base + 1] == 0) {
                    switch (type) {
                        case 0: {
                            this.xref[base] = -1;
                            break;
                        }
                        case 1: {
                            this.xref[base] = field2;
                            break;
                        }
                        case 2: {
                            this.xref[base] = field3;
                            this.xref[base + 1] = field2;
                            if (this.partial) {
                                this.objStmToOffset.put(field2, 0);
                                break;
                            }
                            final Integer on = field2;
                            IntHashtable seq = this.objStmMark.get(on);
                            if (seq == null) {
                                seq = new IntHashtable();
                                seq.put(field3, 1);
                                this.objStmMark.put(on, seq);
                                break;
                            }
                            seq.put(field3, 1);
                            break;
                        }
                    }
                }
                ++start;
            }
        }
        thisStream *= 2;
        if (thisStream < this.xref.length) {
            this.xref[thisStream] = -1;
        }
        return prev == -1 || this.readXRefStream(prev);
    }
    
    protected void rebuildXref() throws IOException {
        this.hybridXref = false;
        this.newXrefType = false;
        this.tokens.seek(0);
        int[][] xr = new int[1024][];
        int top = 0;
        this.trailer = null;
        final byte[] line = new byte[64];
        while (true) {
            int pos = this.tokens.getFilePointer();
            if (!this.tokens.readLineSegment(line)) {
                break;
            }
            if (line[0] == 116) {
                if (!PdfEncodings.convertToString(line, null).startsWith("trailer")) {
                    continue;
                }
                this.tokens.seek(pos);
                this.tokens.nextToken();
                pos = this.tokens.getFilePointer();
                try {
                    final PdfDictionary dic = (PdfDictionary)this.readPRObject();
                    if (dic.get(PdfName.ROOT) != null) {
                        this.trailer = dic;
                    }
                    else {
                        this.tokens.seek(pos);
                    }
                }
                catch (final Exception e) {
                    this.tokens.seek(pos);
                }
            }
            else {
                if (line[0] < 48 || line[0] > 57) {
                    continue;
                }
                final int[] obj = PRTokeniser.checkObjectStart(line);
                if (obj == null) {
                    continue;
                }
                final int num = obj[0];
                final int gen = obj[1];
                if (num >= xr.length) {
                    final int newLength = num * 2;
                    final int[][] xr2 = new int[newLength][];
                    System.arraycopy(xr, 0, xr2, 0, top);
                    xr = xr2;
                }
                if (num >= top) {
                    top = num + 1;
                }
                if (xr[num] != null && gen < xr[num][1]) {
                    continue;
                }
                obj[0] = pos;
                xr[num] = obj;
            }
        }
        this.xref = new int[top * 2];
        for (int k = 0; k < top; ++k) {
            final int[] obj = xr[k];
            if (obj != null) {
                this.xref[k * 2] = obj[0];
            }
        }
    }
    
    protected PdfDictionary readDictionary() throws IOException {
        final PdfDictionary dic = new PdfDictionary();
        while (true) {
            this.tokens.nextValidToken();
            if (this.tokens.getTokenType() == 8) {
                break;
            }
            if (this.tokens.getTokenType() != 3) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("dictionary.key.is.not.a.name"));
            }
            final PdfName name = new PdfName(this.tokens.getStringValue(), false);
            final PdfObject obj = this.readPRObject();
            final int type = obj.type();
            if (-type == 8) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            if (-type == 6) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.close.bracket"));
            }
            dic.put(name, obj);
        }
        return dic;
    }
    
    protected PdfArray readArray() throws IOException {
        final PdfArray array = new PdfArray();
        while (true) {
            final PdfObject obj = this.readPRObject();
            final int type = obj.type();
            if (-type == 6) {
                break;
            }
            if (-type == 8) {
                this.tokens.throwError(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            array.add(obj);
        }
        return array;
    }
    
    protected PdfObject readPRObject() throws IOException {
        this.tokens.nextValidToken();
        final int type = this.tokens.getTokenType();
        switch (type) {
            case 7: {
                ++this.readDepth;
                final PdfDictionary dic = this.readDictionary();
                --this.readDepth;
                final int pos = this.tokens.getFilePointer();
                boolean hasNext;
                do {
                    hasNext = this.tokens.nextToken();
                } while (hasNext && this.tokens.getTokenType() == 4);
                if (hasNext && this.tokens.getStringValue().equals("stream")) {
                    int ch;
                    do {
                        ch = this.tokens.read();
                    } while (ch == 32 || ch == 9 || ch == 0 || ch == 12);
                    if (ch != 10) {
                        ch = this.tokens.read();
                    }
                    if (ch != 10) {
                        this.tokens.backOnePosition(ch);
                    }
                    final PRStream stream = new PRStream(this, this.tokens.getFilePointer());
                    stream.putAll(dic);
                    stream.setObjNum(this.objNum, this.objGen);
                    return stream;
                }
                this.tokens.seek(pos);
                return dic;
            }
            case 5: {
                ++this.readDepth;
                final PdfArray arr = this.readArray();
                --this.readDepth;
                return arr;
            }
            case 1: {
                return new PdfNumber(this.tokens.getStringValue());
            }
            case 2: {
                final PdfString str = new PdfString(this.tokens.getStringValue(), null).setHexWriting(this.tokens.isHexString());
                str.setObjNum(this.objNum, this.objGen);
                if (this.strings != null) {
                    this.strings.add(str);
                }
                return str;
            }
            case 3: {
                final PdfName cachedName = PdfName.staticNames.get(this.tokens.getStringValue());
                if (this.readDepth > 0 && cachedName != null) {
                    return cachedName;
                }
                return new PdfName(this.tokens.getStringValue(), false);
            }
            case 9: {
                final int num = this.tokens.getReference();
                final PRIndirectReference ref = new PRIndirectReference(this, num, this.tokens.getGeneration());
                return ref;
            }
            case 11: {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.end.of.file"));
            }
            default: {
                final String sv = this.tokens.getStringValue();
                if ("null".equals(sv)) {
                    if (this.readDepth == 0) {
                        return new PdfNull();
                    }
                    return PdfNull.PDFNULL;
                }
                else if ("true".equals(sv)) {
                    if (this.readDepth == 0) {
                        return new PdfBoolean(true);
                    }
                    return PdfBoolean.PDFTRUE;
                }
                else {
                    if (!"false".equals(sv)) {
                        return new PdfLiteral(-type, this.tokens.getStringValue());
                    }
                    if (this.readDepth == 0) {
                        return new PdfBoolean(false);
                    }
                    return PdfBoolean.PDFFALSE;
                }
                break;
            }
        }
    }
    
    public static byte[] FlateDecode(final byte[] in) {
        final byte[] b = FlateDecode(in, true);
        if (b == null) {
            return FlateDecode(in, false);
        }
        return b;
    }
    
    public static byte[] decodePredictor(final byte[] in, final PdfObject dicPar) {
        if (dicPar == null || !dicPar.isDictionary()) {
            return in;
        }
        final PdfDictionary dic = (PdfDictionary)dicPar;
        PdfObject obj = getPdfObject(dic.get(PdfName.PREDICTOR));
        if (obj == null || !obj.isNumber()) {
            return in;
        }
        final int predictor = ((PdfNumber)obj).intValue();
        if (predictor < 10) {
            return in;
        }
        int width = 1;
        obj = getPdfObject(dic.get(PdfName.COLUMNS));
        if (obj != null && obj.isNumber()) {
            width = ((PdfNumber)obj).intValue();
        }
        int colors = 1;
        obj = getPdfObject(dic.get(PdfName.COLORS));
        if (obj != null && obj.isNumber()) {
            colors = ((PdfNumber)obj).intValue();
        }
        int bpc = 8;
        obj = getPdfObject(dic.get(PdfName.BITSPERCOMPONENT));
        if (obj != null && obj.isNumber()) {
            bpc = ((PdfNumber)obj).intValue();
        }
        final DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(in));
        final ByteArrayOutputStream fout = new ByteArrayOutputStream(in.length);
        final int bytesPerPixel = colors * bpc / 8;
        final int bytesPerRow = (colors * width * bpc + 7) / 8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        while (true) {
            int filter;
            try {
                filter = dataStream.read();
                if (filter < 0) {
                    return fout.toByteArray();
                }
                dataStream.readFully(curr, 0, bytesPerRow);
            }
            catch (final Exception e) {
                return fout.toByteArray();
            }
            switch (filter) {
                case 0: {
                    break;
                }
                case 1: {
                    for (int i = bytesPerPixel; i < bytesPerRow; ++i) {
                        final byte[] array = curr;
                        final int n = i;
                        array[n] += curr[i - bytesPerPixel];
                    }
                    break;
                }
                case 2: {
                    for (int i = 0; i < bytesPerRow; ++i) {
                        final byte[] array2 = curr;
                        final int n2 = i;
                        array2[n2] += prior[i];
                    }
                    break;
                }
                case 3: {
                    for (int i = 0; i < bytesPerPixel; ++i) {
                        final byte[] array3 = curr;
                        final int n3 = i;
                        array3[n3] += (byte)(prior[i] / 2);
                    }
                    for (int i = bytesPerPixel; i < bytesPerRow; ++i) {
                        final byte[] array4 = curr;
                        final int n4 = i;
                        array4[n4] += (byte)(((curr[i - bytesPerPixel] & 0xFF) + (prior[i] & 0xFF)) / 2);
                    }
                    break;
                }
                case 4: {
                    for (int i = 0; i < bytesPerPixel; ++i) {
                        final byte[] array5 = curr;
                        final int n5 = i;
                        array5[n5] += prior[i];
                    }
                    for (int i = bytesPerPixel; i < bytesPerRow; ++i) {
                        final int a = curr[i - bytesPerPixel] & 0xFF;
                        final int b = prior[i] & 0xFF;
                        final int c = prior[i - bytesPerPixel] & 0xFF;
                        final int p = a + b - c;
                        final int pa = Math.abs(p - a);
                        final int pb = Math.abs(p - b);
                        final int pc = Math.abs(p - c);
                        int ret;
                        if (pa <= pb && pa <= pc) {
                            ret = a;
                        }
                        else if (pb <= pc) {
                            ret = b;
                        }
                        else {
                            ret = c;
                        }
                        final byte[] array6 = curr;
                        final int n6 = i;
                        array6[n6] += (byte)ret;
                    }
                    break;
                }
                default: {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("png.filter.unknown"));
                }
            }
            try {
                fout.write(curr);
            }
            catch (final IOException ex) {}
            final byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }
    
    public static byte[] FlateDecode(final byte[] in, final boolean strict) {
        final ByteArrayInputStream stream = new ByteArrayInputStream(in);
        final InflaterInputStream zip = new InflaterInputStream(stream);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] b = new byte[strict ? 4092 : 1];
        try {
            int n;
            while ((n = zip.read(b)) >= 0) {
                out.write(b, 0, n);
            }
            zip.close();
            out.close();
            return out.toByteArray();
        }
        catch (final Exception e) {
            if (strict) {
                return null;
            }
            return out.toByteArray();
        }
    }
    
    public static byte[] ASCIIHexDecode(final byte[] in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean first = true;
        int n1 = 0;
        for (int k = 0; k < in.length; ++k) {
            final int ch = in[k] & 0xFF;
            if (ch == 62) {
                break;
            }
            if (!PRTokeniser.isWhitespace(ch)) {
                final int n2 = PRTokeniser.getHex(ch);
                if (n2 == -1) {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("illegal.character.in.asciihexdecode"));
                }
                if (first) {
                    n1 = n2;
                }
                else {
                    out.write((byte)((n1 << 4) + n2));
                }
                first = !first;
            }
        }
        if (!first) {
            out.write((byte)(n1 << 4));
        }
        return out.toByteArray();
    }
    
    public static byte[] ASCII85Decode(final byte[] in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int state = 0;
        final int[] chn = new int[5];
        for (int k = 0; k < in.length; ++k) {
            final int ch = in[k] & 0xFF;
            if (ch == 126) {
                break;
            }
            if (!PRTokeniser.isWhitespace(ch)) {
                if (ch == 122 && state == 0) {
                    out.write(0);
                    out.write(0);
                    out.write(0);
                    out.write(0);
                }
                else {
                    if (ch < 33 || ch > 117) {
                        throw new RuntimeException(MessageLocalization.getComposedMessage("illegal.character.in.ascii85decode"));
                    }
                    chn[state] = ch - 33;
                    if (++state == 5) {
                        state = 0;
                        int r = 0;
                        for (int j = 0; j < 5; ++j) {
                            r = r * 85 + chn[j];
                        }
                        out.write((byte)(r >> 24));
                        out.write((byte)(r >> 16));
                        out.write((byte)(r >> 8));
                        out.write((byte)r);
                    }
                }
            }
        }
        if (state == 2) {
            final int r2 = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + 614125 + 7225 + 85;
            out.write((byte)(r2 >> 24));
        }
        else if (state == 3) {
            final int r2 = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + 7225 + 85;
            out.write((byte)(r2 >> 24));
            out.write((byte)(r2 >> 16));
        }
        else if (state == 4) {
            final int r2 = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + chn[2] * 85 * 85 + chn[3] * 85 + 85;
            out.write((byte)(r2 >> 24));
            out.write((byte)(r2 >> 16));
            out.write((byte)(r2 >> 8));
        }
        return out.toByteArray();
    }
    
    public static byte[] LZWDecode(final byte[] in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final ZCompressorInputStream is = new ZCompressorInputStream((InputStream)new ByteArrayInputStream(in));
            return IOUtils.toByteArray((InputStream)is);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public boolean isRebuilt() {
        return this.rebuilt;
    }
    
    public PdfDictionary getPageN(final int pageNum) {
        final PdfDictionary dic = this.pageRefs.getPageN(pageNum);
        if (dic == null) {
            return null;
        }
        if (this.appendable) {
            dic.setIndRef(this.pageRefs.getPageOrigRef(pageNum));
        }
        return dic;
    }
    
    public PdfDictionary getPageNRelease(final int pageNum) {
        final PdfDictionary dic = this.getPageN(pageNum);
        this.pageRefs.releasePage(pageNum);
        return dic;
    }
    
    public void releasePage(final int pageNum) {
        this.pageRefs.releasePage(pageNum);
    }
    
    public void resetReleasePage() {
        this.pageRefs.resetReleasePage();
    }
    
    public PRIndirectReference getPageOrigRef(final int pageNum) {
        return this.pageRefs.getPageOrigRef(pageNum);
    }
    
    public byte[] getPageContent(final int pageNum, final RandomAccessFileOrArray file) throws IOException {
        final PdfDictionary page = this.getPageNRelease(pageNum);
        if (page == null) {
            return null;
        }
        final PdfObject contents = getPdfObjectRelease(page.get(PdfName.CONTENTS));
        if (contents == null) {
            return new byte[0];
        }
        if (contents.isStream()) {
            return getStreamBytes((PRStream)contents, file);
        }
        if (contents.isArray()) {
            final PdfArray array = (PdfArray)contents;
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            for (int k = 0; k < array.size(); ++k) {
                final PdfObject item = getPdfObjectRelease(array.getPdfObject(k));
                if (item != null) {
                    if (item.isStream()) {
                        final byte[] b = getStreamBytes((PRStream)item, file);
                        bout.write(b);
                        if (k != array.size() - 1) {
                            bout.write(10);
                        }
                    }
                }
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }
    
    public byte[] getPageContent(final int pageNum) throws IOException {
        final RandomAccessFileOrArray rf = this.getSafeFile();
        try {
            rf.reOpen();
            return this.getPageContent(pageNum, rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected void killXref(PdfObject obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
            return;
        }
        switch (obj.type()) {
            case 10: {
                final int xr = ((PRIndirectReference)obj).getNumber();
                obj = this.xrefObj.get(xr);
                this.xrefObj.set(xr, null);
                this.freeXref = xr;
                this.killXref(obj);
                break;
            }
            case 5: {
                final PdfArray t = (PdfArray)obj;
                for (int i = 0; i < t.size(); ++i) {
                    this.killXref(t.getPdfObject(i));
                }
                break;
            }
            case 6:
            case 7: {
                final PdfDictionary dic = (PdfDictionary)obj;
                for (final Object o : dic.getKeys()) {
                    this.killXref(dic.get((PdfName)o));
                }
                break;
            }
        }
    }
    
    public void setPageContent(final int pageNum, final byte[] content) {
        this.setPageContent(pageNum, content, -1);
    }
    
    public void setPageContent(final int pageNum, final byte[] content, final int compressionLevel) {
        final PdfDictionary page = this.getPageN(pageNum);
        if (page == null) {
            return;
        }
        final PdfObject contents = page.get(PdfName.CONTENTS);
        this.freeXref = -1;
        this.killXref(contents);
        if (this.freeXref == -1) {
            this.xrefObj.add(null);
            this.freeXref = this.xrefObj.size() - 1;
        }
        page.put(PdfName.CONTENTS, new PRIndirectReference(this, this.freeXref));
        this.xrefObj.set(this.freeXref, new PRStream(this, content, compressionLevel));
    }
    
    public static byte[] getStreamBytes(final PRStream stream, final RandomAccessFileOrArray file) throws IOException {
        final PdfObject filter = getPdfObjectRelease(stream.get(PdfName.FILTER));
        byte[] b = getStreamBytesRaw(stream, file);
        List<PdfObject> filters = new ArrayList<PdfObject>();
        filters = addFilters(filters, filter);
        List<PdfObject> dp = new ArrayList<PdfObject>();
        PdfObject dpo = getPdfObjectRelease(stream.get(PdfName.DECODEPARMS));
        if (dpo == null || (!dpo.isDictionary() && !dpo.isArray())) {
            dpo = getPdfObjectRelease(stream.get(PdfName.DP));
        }
        if (dpo != null) {
            if (dpo.isDictionary()) {
                dp.add(dpo);
            }
            else if (dpo.isArray()) {
                dp = ((PdfArray)dpo).getElements();
            }
        }
        for (int j = 0; j < filters.size(); ++j) {
            final String name = getPdfObjectRelease(filters.get(j)).toString();
            if (name.equals("/FlateDecode") || name.equals("/Fl")) {
                b = FlateDecode(b);
                if (j < dp.size()) {
                    final PdfObject dicParam = dp.get(j);
                    b = decodePredictor(b, dicParam);
                }
            }
            else if (name.equals("/ASCIIHexDecode") || name.equals("/AHx")) {
                b = ASCIIHexDecode(b);
            }
            else if (name.equals("/ASCII85Decode") || name.equals("/A85")) {
                b = ASCII85Decode(b);
            }
            else if (name.equals("/LZWDecode")) {
                b = LZWDecode(b);
                if (j < dp.size()) {
                    final PdfObject dicParam = dp.get(j);
                    b = decodePredictor(b, dicParam);
                }
            }
            else if (!name.equals("/Crypt")) {
                throw new UnsupportedPdfException(MessageLocalization.getComposedMessage("the.filter.1.is.not.supported", name));
            }
        }
        return b;
    }
    
    public static byte[] getStreamBytes(final PRStream stream) throws IOException {
        final RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            return getStreamBytes(stream, rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static byte[] getStreamBytesRaw(final PRStream stream, final RandomAccessFileOrArray file) throws IOException {
        final PdfReader reader = stream.getReader();
        byte[] b;
        if (stream.getOffset() < 0) {
            b = stream.getBytes();
        }
        else {
            b = new byte[stream.getLength()];
            file.seek(stream.getOffset());
            file.readFully(b);
            final PdfEncryption decrypt = reader.getDecrypt();
            if (decrypt != null) {
                final PdfObject filter = getPdfObjectRelease(stream.get(PdfName.FILTER));
                List<PdfObject> filters = new ArrayList<PdfObject>();
                filters = addFilters(filters, filter);
                boolean skip = false;
                for (final PdfObject filter2 : filters) {
                    final PdfObject obj = getPdfObjectRelease(filter2);
                    if (obj != null && obj.toString().equals("/Crypt")) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    decrypt.setHashKey(stream.getObjNum(), stream.getObjGen());
                    b = decrypt.decryptByteArray(b);
                }
            }
        }
        return b;
    }
    
    private static List<PdfObject> addFilters(List<PdfObject> filters, final PdfObject filter) {
        if (filter != null) {
            if (filter.isName()) {
                filters.add(filter);
            }
            else if (filter.isArray()) {
                filters = ((PdfArray)filter).getElements();
            }
        }
        return filters;
    }
    
    public static byte[] getStreamBytesRaw(final PRStream stream) throws IOException {
        final RandomAccessFileOrArray rf = stream.getReader().getSafeFile();
        try {
            rf.reOpen();
            return getStreamBytesRaw(stream, rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public void eliminateSharedStreams() {
        if (!this.sharedStreams) {
            return;
        }
        this.sharedStreams = false;
        if (this.pageRefs.size() == 1) {
            return;
        }
        final List<PdfObject> newRefs = new ArrayList<PdfObject>();
        final List<PdfObject> newStreams = new ArrayList<PdfObject>();
        final IntHashtable visited = new IntHashtable();
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            final PdfDictionary page = this.pageRefs.getPageN(k);
            if (page != null) {
                final PdfObject contents = getPdfObject(page.get(PdfName.CONTENTS));
                if (contents != null) {
                    if (contents.isStream()) {
                        final PRIndirectReference ref = (PRIndirectReference)page.get(PdfName.CONTENTS);
                        if (visited.containsKey(ref.getNumber())) {
                            newRefs.add(ref);
                            newStreams.add(new PRStream((PRStream)contents, null));
                        }
                        else {
                            visited.put(ref.getNumber(), 1);
                        }
                    }
                    else if (contents.isArray()) {
                        final PdfArray array = (PdfArray)contents;
                        for (int j = 0; j < array.size(); ++j) {
                            final PRIndirectReference ref2 = (PRIndirectReference)array.getPdfObject(j);
                            if (visited.containsKey(ref2.getNumber())) {
                                newRefs.add(ref2);
                                newStreams.add(new PRStream((PRStream)getPdfObject(ref2), null));
                            }
                            else {
                                visited.put(ref2.getNumber(), 1);
                            }
                        }
                    }
                }
            }
        }
        if (newStreams.isEmpty()) {
            return;
        }
        for (int k = 0; k < newStreams.size(); ++k) {
            this.xrefObj.add(newStreams.get(k));
            final PRIndirectReference ref3 = newRefs.get(k);
            ref3.setNumber(this.xrefObj.size() - 1, 0);
        }
    }
    
    public boolean isTampered() {
        return this.tampered;
    }
    
    public void setTampered(final boolean tampered) {
        this.tampered = tampered;
        this.pageRefs.keepPages();
    }
    
    public byte[] getMetadata() throws IOException {
        final PdfObject obj = getPdfObject(this.catalog.get(PdfName.METADATA));
        if (!(obj instanceof PRStream)) {
            return null;
        }
        final RandomAccessFileOrArray rf = this.getSafeFile();
        byte[] b;
        try {
            rf.reOpen();
            b = getStreamBytes((PRStream)obj, rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
        return b;
    }
    
    public int getLastXref() {
        return this.lastXref;
    }
    
    public int getXrefSize() {
        return this.xrefObj.size();
    }
    
    public int getEofPos() {
        return this.eofPos;
    }
    
    public char getPdfVersion() {
        return this.pdfVersion;
    }
    
    public boolean isEncrypted() {
        return this.encrypted;
    }
    
    public int getPermissions() {
        return this.pValue;
    }
    
    public boolean is128Key() {
        return this.rValue == 3;
    }
    
    public PdfDictionary getTrailer() {
        return this.trailer;
    }
    
    PdfEncryption getDecrypt() {
        return this.decrypt;
    }
    
    private static boolean equalsn(final byte[] a1, final byte[] a2) {
        for (int length = a2.length, k = 0; k < length; ++k) {
            if (a1[k] != a2[k]) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean existsName(final PdfDictionary dic, final PdfName key, final PdfName value) {
        final PdfObject type = getPdfObjectRelease(dic.get(key));
        if (type == null || !type.isName()) {
            return false;
        }
        final PdfName name = (PdfName)type;
        return name.equals(value);
    }
    
    static String getFontNameFromDescriptor(final PdfDictionary dic) {
        return getFontName(dic, PdfName.FONTNAME);
    }
    
    private static String getFontName(final PdfDictionary dic) {
        return getFontName(dic, PdfName.BASEFONT);
    }
    
    private static String getFontName(final PdfDictionary dic, final PdfName property) {
        if (dic == null) {
            return null;
        }
        final PdfObject type = getPdfObjectRelease(dic.get(property));
        if (type == null || !type.isName()) {
            return null;
        }
        return PdfName.decodeName(type.toString());
    }
    
    static boolean isFontSubset(final String fontName) {
        return fontName != null && fontName.length() >= 8 && fontName.charAt(6) == '+';
    }
    
    private static String getSubsetPrefix(final PdfDictionary dic) {
        if (dic == null) {
            return null;
        }
        final String s = getFontName(dic);
        if (s == null) {
            return null;
        }
        if (s.length() < 8 || s.charAt(6) != '+') {
            return null;
        }
        for (int k = 0; k < 6; ++k) {
            final char c = s.charAt(k);
            if (c < 'A' || c > 'Z') {
                return null;
            }
        }
        return s;
    }
    
    public int shuffleSubsetNames() {
        int total = 0;
        for (int k = 1; k < this.xrefObj.size(); ++k) {
            final PdfObject obj = this.getPdfObjectRelease(k);
            if (obj != null) {
                if (obj.isDictionary()) {
                    final PdfDictionary dic = (PdfDictionary)obj;
                    if (existsName(dic, PdfName.TYPE, PdfName.FONT)) {
                        if (existsName(dic, PdfName.SUBTYPE, PdfName.TYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.MMTYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.TRUETYPE)) {
                            final String s = getSubsetPrefix(dic);
                            if (s != null) {
                                final String ns = BaseFont.createSubsetPrefix() + s.substring(7);
                                final PdfName newName = new PdfName(ns);
                                dic.put(PdfName.BASEFONT, newName);
                                this.setXrefPartialObject(k, dic);
                                ++total;
                                final PdfDictionary fd = dic.getAsDict(PdfName.FONTDESCRIPTOR);
                                if (fd != null) {
                                    fd.put(PdfName.FONTNAME, newName);
                                }
                            }
                        }
                        else if (existsName(dic, PdfName.SUBTYPE, PdfName.TYPE0)) {
                            final String s = getSubsetPrefix(dic);
                            final PdfArray arr = dic.getAsArray(PdfName.DESCENDANTFONTS);
                            if (arr != null) {
                                if (!arr.isEmpty()) {
                                    final PdfDictionary desc = arr.getAsDict(0);
                                    final String sde = getSubsetPrefix(desc);
                                    if (sde != null) {
                                        final String ns2 = BaseFont.createSubsetPrefix();
                                        if (s != null) {
                                            dic.put(PdfName.BASEFONT, new PdfName(ns2 + s.substring(7)));
                                        }
                                        this.setXrefPartialObject(k, dic);
                                        final PdfName newName2 = new PdfName(ns2 + sde.substring(7));
                                        desc.put(PdfName.BASEFONT, newName2);
                                        ++total;
                                        final PdfDictionary fd2 = desc.getAsDict(PdfName.FONTDESCRIPTOR);
                                        if (fd2 != null) {
                                            fd2.put(PdfName.FONTNAME, newName2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }
    
    public int createFakeFontSubsets() {
        int total = 0;
        for (int k = 1; k < this.xrefObj.size(); ++k) {
            final PdfObject obj = this.getPdfObjectRelease(k);
            if (obj != null) {
                if (obj.isDictionary()) {
                    final PdfDictionary dic = (PdfDictionary)obj;
                    if (existsName(dic, PdfName.TYPE, PdfName.FONT)) {
                        if (existsName(dic, PdfName.SUBTYPE, PdfName.TYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.MMTYPE1) || existsName(dic, PdfName.SUBTYPE, PdfName.TRUETYPE)) {
                            String s = getSubsetPrefix(dic);
                            if (s == null) {
                                s = getFontName(dic);
                                if (s != null) {
                                    final String ns = BaseFont.createSubsetPrefix() + s;
                                    PdfDictionary fd = (PdfDictionary)getPdfObjectRelease(dic.get(PdfName.FONTDESCRIPTOR));
                                    if (fd != null) {
                                        if (fd.get(PdfName.FONTFILE) != null || fd.get(PdfName.FONTFILE2) != null || fd.get(PdfName.FONTFILE3) != null) {
                                            fd = dic.getAsDict(PdfName.FONTDESCRIPTOR);
                                            final PdfName newName = new PdfName(ns);
                                            dic.put(PdfName.BASEFONT, newName);
                                            fd.put(PdfName.FONTNAME, newName);
                                            this.setXrefPartialObject(k, dic);
                                            ++total;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }
    
    private static PdfArray getNameArray(PdfObject obj) {
        if (obj == null) {
            return null;
        }
        obj = getPdfObjectRelease(obj);
        if (obj == null) {
            return null;
        }
        if (obj.isArray()) {
            return (PdfArray)obj;
        }
        if (obj.isDictionary()) {
            final PdfObject arr2 = getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.D));
            if (arr2 != null && arr2.isArray()) {
                return (PdfArray)arr2;
            }
        }
        return null;
    }
    
    public HashMap getNamedDestination() {
        return this.getNamedDestination(false);
    }
    
    public HashMap getNamedDestination(final boolean keepNames) {
        final HashMap names = this.getNamedDestinationFromNames(keepNames);
        names.putAll(this.getNamedDestinationFromStrings());
        return names;
    }
    
    public HashMap getNamedDestinationFromNames() {
        return this.getNamedDestinationFromNames(false);
    }
    
    public HashMap getNamedDestinationFromNames(final boolean keepNames) {
        final HashMap names = new HashMap();
        if (this.catalog.get(PdfName.DESTS) != null) {
            final PdfDictionary dic = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.DESTS));
            if (dic == null) {
                return names;
            }
            final Set keys = dic.getKeys();
            for (final PdfName key : keys) {
                final PdfArray arr = getNameArray(dic.get(key));
                if (arr == null) {
                    continue;
                }
                if (keepNames) {
                    names.put(key, arr);
                }
                else {
                    final String name = PdfName.decodeName(key.toString());
                    names.put(name, arr);
                }
            }
        }
        return names;
    }
    
    public HashMap getNamedDestinationFromStrings() {
        if (this.catalog.get(PdfName.NAMES) != null) {
            PdfDictionary dic = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
            if (dic != null) {
                dic = (PdfDictionary)getPdfObjectRelease(dic.get(PdfName.DESTS));
                if (dic != null) {
                    final HashMap names = PdfNameTree.readTree(dic);
                    final Iterator it = names.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry entry = it.next();
                        final PdfArray arr = getNameArray(entry.getValue());
                        if (arr != null) {
                            entry.setValue(arr);
                        }
                        else {
                            it.remove();
                        }
                    }
                    return names;
                }
            }
        }
        return new HashMap();
    }
    
    public void removeFields() {
        this.pageRefs.resetReleasePage();
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            final PdfDictionary page = this.pageRefs.getPageN(k);
            final PdfArray annots = page.getAsArray(PdfName.ANNOTS);
            if (annots == null) {
                this.pageRefs.releasePage(k);
            }
            else {
                for (int j = 0; j < annots.size(); ++j) {
                    final PdfObject obj = getPdfObjectRelease(annots.getPdfObject(j));
                    if (obj != null) {
                        if (obj.isDictionary()) {
                            final PdfDictionary annot = (PdfDictionary)obj;
                            if (PdfName.WIDGET.equals(annot.get(PdfName.SUBTYPE))) {
                                annots.remove(j--);
                            }
                        }
                    }
                }
                if (annots.isEmpty()) {
                    page.remove(PdfName.ANNOTS);
                }
                else {
                    this.pageRefs.releasePage(k);
                }
            }
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }
    
    public void removeAnnotations() {
        this.pageRefs.resetReleasePage();
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            final PdfDictionary page = this.pageRefs.getPageN(k);
            if (page.get(PdfName.ANNOTS) == null) {
                this.pageRefs.releasePage(k);
            }
            else {
                page.remove(PdfName.ANNOTS);
            }
        }
        this.catalog.remove(PdfName.ACROFORM);
        this.pageRefs.resetReleasePage();
    }
    
    public ArrayList getLinks(final int page) {
        this.pageRefs.resetReleasePage();
        final ArrayList result = new ArrayList();
        final PdfDictionary pageDic = this.pageRefs.getPageN(page);
        if (pageDic.get(PdfName.ANNOTS) != null) {
            final PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            for (int j = 0; j < annots.size(); ++j) {
                final PdfDictionary annot = (PdfDictionary)getPdfObjectRelease(annots.getPdfObject(j));
                if (PdfName.LINK.equals(annot.get(PdfName.SUBTYPE))) {
                    result.add(new PdfAnnotation.PdfImportedLink(annot));
                }
            }
        }
        this.pageRefs.releasePage(page);
        this.pageRefs.resetReleasePage();
        return result;
    }
    
    private void iterateBookmarks(PdfObject outlineRef, final HashMap names) {
        while (outlineRef != null) {
            this.replaceNamedDestination(outlineRef, names);
            final PdfDictionary outline = (PdfDictionary)getPdfObjectRelease(outlineRef);
            final PdfObject first = outline.get(PdfName.FIRST);
            if (first != null) {
                this.iterateBookmarks(first, names);
            }
            outlineRef = outline.get(PdfName.NEXT);
        }
    }
    
    public void makeRemoteNamedDestinationsLocal() {
        if (this.remoteToLocalNamedDestinations) {
            return;
        }
        this.remoteToLocalNamedDestinations = true;
        final HashMap names = this.getNamedDestination(true);
        if (names.isEmpty()) {
            return;
        }
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            final PdfDictionary page = this.pageRefs.getPageN(k);
            final PdfObject annotsRef;
            final PdfArray annots = (PdfArray)getPdfObject(annotsRef = page.get(PdfName.ANNOTS));
            final int annotIdx = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            if (annots == null) {
                this.pageRefs.releasePage(k);
            }
            else {
                boolean commitAnnots = false;
                for (int an = 0; an < annots.size(); ++an) {
                    final PdfObject objRef = annots.getPdfObject(an);
                    if (this.convertNamedDestination(objRef, names) && !objRef.isIndirect()) {
                        commitAnnots = true;
                    }
                }
                if (commitAnnots) {
                    this.setXrefPartialObject(annotIdx, annots);
                }
                if (!commitAnnots || annotsRef.isIndirect()) {
                    this.pageRefs.releasePage(k);
                }
            }
        }
    }
    
    private boolean convertNamedDestination(PdfObject obj, final HashMap names) {
        obj = getPdfObject(obj);
        final int objIdx = this.lastXrefPartial;
        this.releaseLastXrefPartial();
        if (obj != null && obj.isDictionary()) {
            final PdfObject ob2 = getPdfObject(((PdfDictionary)obj).get(PdfName.A));
            if (ob2 != null) {
                final int obj2Idx = this.lastXrefPartial;
                this.releaseLastXrefPartial();
                final PdfDictionary dic = (PdfDictionary)ob2;
                final PdfName type = (PdfName)getPdfObjectRelease(dic.get(PdfName.S));
                if (PdfName.GOTOR.equals(type)) {
                    final PdfObject ob3 = getPdfObjectRelease(dic.get(PdfName.D));
                    Object name = null;
                    if (ob3 != null) {
                        if (ob3.isName()) {
                            name = ob3;
                        }
                        else if (ob3.isString()) {
                            name = ob3.toString();
                        }
                        final PdfArray dest = names.get(name);
                        if (dest != null) {
                            dic.remove(PdfName.F);
                            dic.remove(PdfName.NEWWINDOW);
                            dic.put(PdfName.S, PdfName.GOTO);
                            this.setXrefPartialObject(obj2Idx, ob2);
                            this.setXrefPartialObject(objIdx, obj);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void consolidateNamedDestinations() {
        if (this.consolidateNamedDestinations) {
            return;
        }
        this.consolidateNamedDestinations = true;
        final HashMap names = this.getNamedDestination(true);
        if (names.isEmpty()) {
            return;
        }
        for (int k = 1; k <= this.pageRefs.size(); ++k) {
            final PdfDictionary page = this.pageRefs.getPageN(k);
            final PdfObject annotsRef;
            final PdfArray annots = (PdfArray)getPdfObject(annotsRef = page.get(PdfName.ANNOTS));
            final int annotIdx = this.lastXrefPartial;
            this.releaseLastXrefPartial();
            if (annots == null) {
                this.pageRefs.releasePage(k);
            }
            else {
                boolean commitAnnots = false;
                for (int an = 0; an < annots.size(); ++an) {
                    final PdfObject objRef = annots.getPdfObject(an);
                    if (this.replaceNamedDestination(objRef, names) && !objRef.isIndirect()) {
                        commitAnnots = true;
                    }
                }
                if (commitAnnots) {
                    this.setXrefPartialObject(annotIdx, annots);
                }
                if (!commitAnnots || annotsRef.isIndirect()) {
                    this.pageRefs.releasePage(k);
                }
            }
        }
        final PdfDictionary outlines = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.OUTLINES));
        if (outlines == null) {
            return;
        }
        this.iterateBookmarks(outlines.get(PdfName.FIRST), names);
    }
    
    private boolean replaceNamedDestination(PdfObject obj, final HashMap names) {
        obj = getPdfObject(obj);
        final int objIdx = this.lastXrefPartial;
        this.releaseLastXrefPartial();
        if (obj != null && obj.isDictionary()) {
            PdfObject ob2 = getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.DEST));
            Object name = null;
            if (ob2 != null) {
                if (ob2.isName()) {
                    name = ob2;
                }
                else if (ob2.isString()) {
                    name = ob2.toString();
                }
                final PdfArray dest = names.get(name);
                if (dest != null) {
                    ((PdfDictionary)obj).put(PdfName.DEST, dest);
                    this.setXrefPartialObject(objIdx, obj);
                    return true;
                }
            }
            else if ((ob2 = getPdfObject(((PdfDictionary)obj).get(PdfName.A))) != null) {
                final int obj2Idx = this.lastXrefPartial;
                this.releaseLastXrefPartial();
                final PdfDictionary dic = (PdfDictionary)ob2;
                final PdfName type = (PdfName)getPdfObjectRelease(dic.get(PdfName.S));
                if (PdfName.GOTO.equals(type)) {
                    final PdfObject ob3 = getPdfObjectRelease(dic.get(PdfName.D));
                    if (ob3 != null) {
                        if (ob3.isName()) {
                            name = ob3;
                        }
                        else if (ob3.isString()) {
                            name = ob3.toString();
                        }
                    }
                    final PdfArray dest2 = names.get(name);
                    if (dest2 != null) {
                        dic.put(PdfName.D, dest2);
                        this.setXrefPartialObject(obj2Idx, ob2);
                        this.setXrefPartialObject(objIdx, obj);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected static PdfDictionary duplicatePdfDictionary(final PdfDictionary original, PdfDictionary copy, final PdfReader newReader) {
        if (copy == null) {
            copy = new PdfDictionary();
        }
        for (final Object o : original.getKeys()) {
            final PdfName key = (PdfName)o;
            copy.put(key, duplicatePdfObject(original.get(key), newReader));
        }
        return copy;
    }
    
    protected static PdfObject duplicatePdfObject(final PdfObject original, final PdfReader newReader) {
        if (original == null) {
            return null;
        }
        switch (original.type()) {
            case 6: {
                return duplicatePdfDictionary((PdfDictionary)original, null, newReader);
            }
            case 7: {
                final PRStream org = (PRStream)original;
                final PRStream stream = new PRStream(org, null, newReader);
                duplicatePdfDictionary(org, stream, newReader);
                return stream;
            }
            case 5: {
                final PdfArray arr = new PdfArray();
                final Iterator it = ((PdfArray)original).listIterator();
                while (it.hasNext()) {
                    arr.add(duplicatePdfObject(it.next(), newReader));
                }
                return arr;
            }
            case 10: {
                final PRIndirectReference org2 = (PRIndirectReference)original;
                return new PRIndirectReference(newReader, org2.getNumber(), org2.getGeneration());
            }
            default: {
                return original;
            }
        }
    }
    
    @Override
    public void close() {
        if (!this.partial) {
            return;
        }
        try {
            this.tokens.close();
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    protected void removeUnusedNode(PdfObject obj, final boolean[] hits) {
        final Stack state = new Stack();
        state.push(obj);
        while (!state.empty()) {
            final Object current = state.pop();
            if (current == null) {
                continue;
            }
            List<PdfObject> ar = null;
            PdfDictionary dic = null;
            PdfName[] keys = null;
            Object[] objs = null;
            int idx = 0;
            if (current instanceof PdfObject) {
                obj = (PdfObject)current;
                switch (obj.type()) {
                    case 6:
                    case 7: {
                        dic = (PdfDictionary)obj;
                        keys = new PdfName[dic.size()];
                        dic.getKeys().toArray(keys);
                        break;
                    }
                    case 5: {
                        ar = ((PdfArray)obj).getElements();
                        break;
                    }
                    case 10: {
                        final PRIndirectReference ref = (PRIndirectReference)obj;
                        final int num = ref.getNumber();
                        if (!hits[num]) {
                            hits[num] = true;
                            state.push(getPdfObjectRelease(ref));
                            continue;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            else {
                objs = (Object[])current;
                if (objs[0] instanceof ArrayList) {
                    ar = (ArrayList)objs[0];
                    idx = (int)objs[1];
                }
                else {
                    keys = (PdfName[])objs[0];
                    dic = (PdfDictionary)objs[1];
                    idx = (int)objs[2];
                }
            }
            if (ar != null) {
                int k = idx;
                while (k < ar.size()) {
                    final PdfObject v = ar.get(k);
                    if (v.isIndirect()) {
                        final int num2 = ((PRIndirectReference)v).getNumber();
                        if (num2 >= this.xrefObj.size() || (!this.partial && this.xrefObj.get(num2) == null)) {
                            ar.set(k, PdfNull.PDFNULL);
                            ++k;
                            continue;
                        }
                    }
                    if (objs == null) {
                        state.push(new Object[] { ar, k + 1 });
                    }
                    else {
                        objs[1] = k + 1;
                        state.push(objs);
                    }
                    state.push(v);
                    break;
                }
            }
            else {
                int k = idx;
                while (k < keys.length) {
                    final PdfName key = keys[k];
                    final PdfObject v2 = dic.get(key);
                    if (v2.isIndirect()) {
                        final int num3 = ((PRIndirectReference)v2).getNumber();
                        if (num3 >= this.xrefObj.size() || (!this.partial && this.xrefObj.get(num3) == null)) {
                            dic.put(key, PdfNull.PDFNULL);
                            ++k;
                            continue;
                        }
                    }
                    if (objs == null) {
                        state.push(new Object[] { keys, dic, k + 1 });
                    }
                    else {
                        objs[2] = k + 1;
                        state.push(objs);
                    }
                    state.push(v2);
                    break;
                }
            }
        }
    }
    
    public int removeUnusedObjects() {
        final boolean[] hits = new boolean[this.xrefObj.size()];
        this.removeUnusedNode(this.trailer, hits);
        int total = 0;
        if (this.partial) {
            for (int k = 1; k < hits.length; ++k) {
                if (!hits[k]) {
                    this.xref[k * 2] = -1;
                    this.xref[k * 2 + 1] = 0;
                    this.xrefObj.set(k, null);
                    ++total;
                }
            }
        }
        else {
            for (int k = 1; k < hits.length; ++k) {
                if (!hits[k]) {
                    this.xrefObj.set(k, null);
                    ++total;
                }
            }
        }
        return total;
    }
    
    public AcroFields getAcroFields() {
        return new AcroFields(this, null);
    }
    
    public String getJavaScript(final RandomAccessFileOrArray file) throws IOException {
        final PdfDictionary names = (PdfDictionary)getPdfObjectRelease(this.catalog.get(PdfName.NAMES));
        if (names == null) {
            return null;
        }
        final PdfDictionary js = (PdfDictionary)getPdfObjectRelease(names.get(PdfName.JAVASCRIPT));
        if (js == null) {
            return null;
        }
        final HashMap jscript = PdfNameTree.readTree(js);
        String[] sortedNames = new String[jscript.size()];
        sortedNames = (String[])jscript.keySet().toArray(sortedNames);
        Arrays.sort(sortedNames);
        final StringBuilder buf = new StringBuilder();
        for (final String sortedName : sortedNames) {
            final PdfDictionary j = (PdfDictionary)getPdfObjectRelease(jscript.get(sortedName));
            if (j != null) {
                final PdfObject obj = getPdfObjectRelease(j.get(PdfName.JS));
                if (obj != null) {
                    if (obj.isString()) {
                        buf.append(((PdfString)obj).toUnicodeString()).append('\n');
                    }
                    else if (obj.isStream()) {
                        final byte[] bytes = getStreamBytes((PRStream)obj, file);
                        if (bytes.length >= 2 && bytes[0] == -2 && bytes[1] == -1) {
                            buf.append(PdfEncodings.convertToString(bytes, "UnicodeBig"));
                        }
                        else {
                            buf.append(PdfEncodings.convertToString(bytes, "PDF"));
                        }
                        buf.append('\n');
                    }
                }
            }
        }
        return buf.toString();
    }
    
    public String getJavaScript() throws IOException {
        final RandomAccessFileOrArray rf = this.getSafeFile();
        try {
            rf.reOpen();
            return this.getJavaScript(rf);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public void selectPages(final String ranges) {
        this.selectPages(SequenceList.expand(ranges, this.getNumberOfPages()));
    }
    
    public void selectPages(final List pagesToKeep) {
        this.pageRefs.selectPages(pagesToKeep);
        this.removeUnusedObjects();
    }
    
    @Override
    public void setViewerPreferences(final int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
        this.setViewerPreferences(this.viewerPreferences);
    }
    
    @Override
    public void addViewerPreference(final PdfName key, final PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
        this.setViewerPreferences(this.viewerPreferences);
    }
    
    void setViewerPreferences(final PdfViewerPreferencesImp vp) {
        vp.addToCatalog(this.catalog);
    }
    
    public int getSimpleViewerPreferences() {
        return PdfViewerPreferencesImp.getViewerPreferences(this.catalog).getPageLayoutAndMode();
    }
    
    public boolean isAppendable() {
        return this.appendable;
    }
    
    public void setAppendable(final boolean appendable) {
        this.appendable = appendable;
        if (appendable) {
            getPdfObject(this.trailer.get(PdfName.ROOT));
        }
    }
    
    public boolean isNewXrefType() {
        return this.newXrefType;
    }
    
    public int getFileLength() {
        return this.fileLength;
    }
    
    public boolean isHybridXref() {
        return this.hybridXref;
    }
    
    PdfIndirectReference getCryptoRef() {
        if (this.cryptoRef == null) {
            return null;
        }
        return new PdfIndirectReference(0, this.cryptoRef.getNumber(), this.cryptoRef.getGeneration());
    }
    
    public void removeUsageRights() {
        final PdfDictionary perms = this.catalog.getAsDict(PdfName.PERMS);
        if (perms == null) {
            return;
        }
        perms.remove(PdfName.UR);
        perms.remove(PdfName.UR3);
        if (perms.size() == 0) {
            this.catalog.remove(PdfName.PERMS);
        }
    }
    
    public int getCertificationLevel() {
        PdfDictionary dic = this.catalog.getAsDict(PdfName.PERMS);
        if (dic == null) {
            return 0;
        }
        dic = dic.getAsDict(PdfName.DOCMDP);
        if (dic == null) {
            return 0;
        }
        final PdfArray arr = dic.getAsArray(PdfName.REFERENCE);
        if (arr == null || arr.size() == 0) {
            return 0;
        }
        dic = arr.getAsDict(0);
        if (dic == null) {
            return 0;
        }
        dic = dic.getAsDict(PdfName.TRANSFORMPARAMS);
        if (dic == null) {
            return 0;
        }
        final PdfNumber p = dic.getAsNumber(PdfName.P);
        if (p == null) {
            return 0;
        }
        return p.intValue();
    }
    
    public boolean isModificationlowedWithoutOwnerPassword() {
        return this.modificationAllowedWithoutOwnerPassword;
    }
    
    public void setModificationAllowedWithoutOwnerPassword(final boolean modificationAllowedWithoutOwnerPassword) {
        this.modificationAllowedWithoutOwnerPassword = modificationAllowedWithoutOwnerPassword;
    }
    
    public final boolean isOpenedWithFullPermissions() {
        return !this.encrypted || this.ownerPasswordUsed || this.modificationAllowedWithoutOwnerPassword;
    }
    
    public int getCryptoMode() {
        if (this.decrypt == null) {
            return -1;
        }
        return this.decrypt.getCryptoMode();
    }
    
    public boolean isMetadataEncrypted() {
        return this.decrypt != null && this.decrypt.isMetadataEncrypted();
    }
    
    public byte[] computeUserPassword() {
        if (!this.encrypted || !this.ownerPasswordUsed) {
            return null;
        }
        return this.decrypt.computeUserPassword(this.password);
    }
    
    static {
        pageInhCandidates = new PdfName[] { PdfName.MEDIABOX, PdfName.ROTATE, PdfName.RESOURCES, PdfName.CROPBOX };
        endstream = PdfEncodings.convertToBytes("endstream", null);
        endobj = PdfEncodings.convertToBytes("endobj", null);
    }
    
    static class PageRefs
    {
        private final PdfReader reader;
        private ArrayList refsn;
        private int sizep;
        private IntHashtable refsp;
        private int lastPageRead;
        private ArrayList pageInh;
        private boolean keepPages;
        
        private PageRefs(final PdfReader reader) {
            this.lastPageRead = -1;
            this.reader = reader;
            if (reader.partial) {
                this.refsp = new IntHashtable();
                final PdfNumber npages = (PdfNumber)PdfReader.getPdfObjectRelease(reader.rootPages.get(PdfName.COUNT));
                this.sizep = npages.intValue();
            }
            else {
                this.readPages();
            }
        }
        
        PageRefs(final PageRefs other, final PdfReader reader) {
            this.lastPageRead = -1;
            this.reader = reader;
            this.sizep = other.sizep;
            if (other.refsn != null) {
                this.refsn = new ArrayList(other.refsn);
                for (int k = 0; k < this.refsn.size(); ++k) {
                    this.refsn.set(k, PdfReader.duplicatePdfObject(this.refsn.get(k), reader));
                }
            }
            else {
                this.refsp = (IntHashtable)other.refsp.clone();
            }
        }
        
        int size() {
            if (this.refsn != null) {
                return this.refsn.size();
            }
            return this.sizep;
        }
        
        void readPages() {
            if (this.refsn != null) {
                return;
            }
            this.refsp = null;
            this.refsn = new ArrayList();
            this.pageInh = new ArrayList();
            this.iteratePages((PRIndirectReference)this.reader.catalog.get(PdfName.PAGES));
            this.pageInh = null;
            this.reader.rootPages.put(PdfName.COUNT, new PdfNumber(this.refsn.size()));
        }
        
        void reReadPages() {
            this.refsn = null;
            this.readPages();
        }
        
        public PdfDictionary getPageN(final int pageNum) {
            final PRIndirectReference ref = this.getPageOrigRef(pageNum);
            return (PdfDictionary)PdfReader.getPdfObject(ref);
        }
        
        public PdfDictionary getPageNRelease(final int pageNum) {
            final PdfDictionary page = this.getPageN(pageNum);
            this.releasePage(pageNum);
            return page;
        }
        
        public PRIndirectReference getPageOrigRefRelease(final int pageNum) {
            final PRIndirectReference ref = this.getPageOrigRef(pageNum);
            this.releasePage(pageNum);
            return ref;
        }
        
        public PRIndirectReference getPageOrigRef(int pageNum) {
            try {
                if (--pageNum < 0 || pageNum >= this.size()) {
                    return null;
                }
                if (this.refsn != null) {
                    return this.refsn.get(pageNum);
                }
                final int n = this.refsp.get(pageNum);
                if (n == 0) {
                    final PRIndirectReference ref = this.getSinglePage(pageNum);
                    if (this.reader.lastXrefPartial == -1) {
                        this.lastPageRead = -1;
                    }
                    else {
                        this.lastPageRead = pageNum;
                    }
                    this.reader.lastXrefPartial = -1;
                    this.refsp.put(pageNum, ref.getNumber());
                    if (this.keepPages) {
                        this.lastPageRead = -1;
                    }
                    return ref;
                }
                if (this.lastPageRead != pageNum) {
                    this.lastPageRead = -1;
                }
                if (this.keepPages) {
                    this.lastPageRead = -1;
                }
                return new PRIndirectReference(this.reader, n);
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        void keepPages() {
            if (this.refsp == null || this.keepPages) {
                return;
            }
            this.keepPages = true;
            this.refsp.clear();
        }
        
        public void releasePage(int pageNum) {
            if (this.refsp == null) {
                return;
            }
            if (--pageNum < 0 || pageNum >= this.size()) {
                return;
            }
            if (pageNum != this.lastPageRead) {
                return;
            }
            this.lastPageRead = -1;
            this.reader.lastXrefPartial = this.refsp.get(pageNum);
            this.reader.releaseLastXrefPartial();
            this.refsp.remove(pageNum);
        }
        
        public void resetReleasePage() {
            if (this.refsp == null) {
                return;
            }
            this.lastPageRead = -1;
        }
        
        void insertPage(int pageNum, final PRIndirectReference ref) {
            --pageNum;
            if (this.refsn != null) {
                if (pageNum >= this.refsn.size()) {
                    this.refsn.add(ref);
                }
                else {
                    this.refsn.add(pageNum, ref);
                }
            }
            else {
                ++this.sizep;
                this.lastPageRead = -1;
                if (pageNum >= this.size()) {
                    this.refsp.put(this.size(), ref.getNumber());
                }
                else {
                    final IntHashtable refs2 = new IntHashtable((this.refsp.size() + 1) * 2);
                    final Iterator it = this.refsp.getEntryIterator();
                    while (it.hasNext()) {
                        final IntHashtable.Entry entry = it.next();
                        final int p = entry.getKey();
                        refs2.put((p >= pageNum) ? (p + 1) : p, entry.getValue());
                    }
                    refs2.put(pageNum, ref.getNumber());
                    this.refsp = refs2;
                }
            }
        }
        
        private void pushPageAttributes(final PdfDictionary nodePages) {
            final PdfDictionary dic = new PdfDictionary();
            if (!this.pageInh.isEmpty()) {
                dic.putAll(this.pageInh.get(this.pageInh.size() - 1));
            }
            for (final PdfName pageInhCandidate : PdfReader.pageInhCandidates) {
                final PdfObject obj = nodePages.get(pageInhCandidate);
                if (obj != null) {
                    dic.put(pageInhCandidate, obj);
                }
            }
            this.pageInh.add(dic);
        }
        
        private void popPageAttributes() {
            this.pageInh.remove(this.pageInh.size() - 1);
        }
        
        private void iteratePages(final PRIndirectReference rpage) {
            final PdfDictionary page = (PdfDictionary)PdfReader.getPdfObject(rpage);
            final PdfArray kidsPR = page.getAsArray(PdfName.KIDS);
            if (kidsPR == null) {
                page.put(PdfName.TYPE, PdfName.PAGE);
                final PdfDictionary dic = this.pageInh.get(this.pageInh.size() - 1);
                for (final Object o : dic.getKeys()) {
                    final PdfName key = (PdfName)o;
                    if (page.get(key) == null) {
                        page.put(key, dic.get(key));
                    }
                }
                if (page.get(PdfName.MEDIABOX) == null) {
                    final PdfArray arr = new PdfArray(new float[] { 0.0f, 0.0f, PageSize.LETTER.getRight(), PageSize.LETTER.getTop() });
                    page.put(PdfName.MEDIABOX, arr);
                }
                this.refsn.add(rpage);
            }
            else {
                page.put(PdfName.TYPE, PdfName.PAGES);
                this.pushPageAttributes(page);
                for (int k = 0; k < kidsPR.size(); ++k) {
                    final PdfObject obj = kidsPR.getPdfObject(k);
                    if (!obj.isIndirect()) {
                        while (k < kidsPR.size()) {
                            kidsPR.remove(k);
                        }
                        break;
                    }
                    this.iteratePages((PRIndirectReference)obj);
                }
                this.popPageAttributes();
            }
        }
        
        protected PRIndirectReference getSinglePage(final int n) {
            final PdfDictionary acc = new PdfDictionary();
            PdfDictionary top = this.reader.rootPages;
            int base = 0;
            PRIndirectReference ref = null;
            PdfDictionary dic = null;
        Block_7:
            while (true) {
                for (final PdfName pageInhCandidate : PdfReader.pageInhCandidates) {
                    final PdfObject obj = top.get(pageInhCandidate);
                    if (obj != null) {
                        acc.put(pageInhCandidate, obj);
                    }
                }
                final PdfArray kids = (PdfArray)PdfReader.getPdfObjectRelease(top.get(PdfName.KIDS));
                final Iterator it = kids.listIterator();
                while (it.hasNext()) {
                    ref = it.next();
                    dic = (PdfDictionary)PdfReader.getPdfObject(ref);
                    final int last = this.reader.lastXrefPartial;
                    final PdfObject count = PdfReader.getPdfObjectRelease(dic.get(PdfName.COUNT));
                    this.reader.lastXrefPartial = last;
                    int acn = 1;
                    if (count != null && count.type() == 2) {
                        acn = ((PdfNumber)count).intValue();
                    }
                    if (n < base + acn) {
                        if (count == null) {
                            break Block_7;
                        }
                        this.reader.releaseLastXrefPartial();
                        top = dic;
                        break;
                    }
                    else {
                        this.reader.releaseLastXrefPartial();
                        base += acn;
                    }
                }
            }
            dic.mergeDifferent(acc);
            return ref;
        }
        
        private void selectPages(final List pagesToKeep) {
            final IntHashtable pg = new IntHashtable();
            final ArrayList finalPages = new ArrayList();
            final int psize = this.size();
            for (final Object aPagesToKeep : pagesToKeep) {
                final Integer pi = (Integer)aPagesToKeep;
                final int p = pi;
                if (p >= 1 && p <= psize && pg.put(p, 1) == 0) {
                    finalPages.add(pi);
                }
            }
            if (this.reader.partial) {
                for (int k = 1; k <= psize; ++k) {
                    this.getPageOrigRef(k);
                    this.resetReleasePage();
                }
            }
            final PRIndirectReference parent = (PRIndirectReference)this.reader.catalog.get(PdfName.PAGES);
            final PdfDictionary topPages = (PdfDictionary)PdfReader.getPdfObject(parent);
            final ArrayList newPageRefs = new ArrayList(finalPages.size());
            final PdfArray kids = new PdfArray();
            for (final Object finalPage : finalPages) {
                final int p2 = (int)finalPage;
                final PRIndirectReference pref = this.getPageOrigRef(p2);
                this.resetReleasePage();
                kids.add(pref);
                newPageRefs.add(pref);
                this.getPageN(p2).put(PdfName.PARENT, parent);
            }
            final AcroFields af = this.reader.getAcroFields();
            final boolean removeFields = af.getFields().size() > 0;
            for (int i = 1; i <= psize; ++i) {
                if (!pg.containsKey(i)) {
                    if (removeFields) {
                        af.removeFieldsFromPage(i);
                    }
                    final PRIndirectReference pref = this.getPageOrigRef(i);
                    final int nref = pref.getNumber();
                    this.reader.xrefObj.set(nref, null);
                    if (this.reader.partial) {
                        this.reader.xref[nref * 2] = -1;
                        this.reader.xref[nref * 2 + 1] = 0;
                    }
                }
            }
            topPages.put(PdfName.COUNT, new PdfNumber(finalPages.size()));
            topPages.put(PdfName.KIDS, kids);
            this.refsp = null;
            this.refsn = newPageRefs;
        }
    }
}
