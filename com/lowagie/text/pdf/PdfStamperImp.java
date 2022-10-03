package com.lowagie.text.pdf;

import java.util.Collection;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.ExceptionConverter;
import java.util.ArrayList;
import com.lowagie.text.Rectangle;
import java.util.Iterator;
import com.lowagie.text.DocWriter;
import java.util.Map;
import org.xml.sax.SAXException;
import com.lowagie.text.xml.xmp.XmpReader;
import com.lowagie.text.Document;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.exceptions.BadPasswordException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.OutputStream;
import java.util.Calendar;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.util.HashSet;
import java.util.HashMap;

class PdfStamperImp extends PdfWriter
{
    HashMap readers2intrefs;
    HashMap readers2file;
    RandomAccessFileOrArray file;
    PdfReader reader;
    IntHashtable myXref;
    HashMap pagesToContent;
    boolean closed;
    private boolean rotateContents;
    protected AcroFields acroFields;
    protected boolean flat;
    protected boolean flatFreeText;
    protected int[] namePtr;
    protected HashSet partialFlattening;
    protected boolean useVp;
    protected PdfViewerPreferencesImp viewerPreferences;
    protected HashMap fieldTemplates;
    protected boolean fieldsAdded;
    protected int sigFlags;
    protected boolean append;
    protected IntHashtable marked;
    protected int initialXrefSize;
    protected PdfAction openAction;
    private boolean includeFileID;
    private PdfObject overrideFileId;
    private Calendar modificationDate;
    
    PdfStamperImp(final PdfReader reader, final OutputStream os, final char pdfVersion, final boolean append) throws DocumentException, IOException {
        super(new PdfDocument(), os);
        this.readers2intrefs = new HashMap();
        this.readers2file = new HashMap();
        this.myXref = new IntHashtable();
        this.pagesToContent = new HashMap();
        this.closed = false;
        this.rotateContents = true;
        this.flat = false;
        this.flatFreeText = false;
        this.namePtr = new int[] { 0 };
        this.partialFlattening = new HashSet();
        this.useVp = false;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.fieldTemplates = new HashMap();
        this.fieldsAdded = false;
        this.sigFlags = 0;
        this.includeFileID = true;
        this.overrideFileId = null;
        this.modificationDate = null;
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        if (reader.isTampered()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.original.document.was.reused.read.it.again.from.file"));
        }
        reader.setTampered(true);
        this.reader = reader;
        this.file = reader.getSafeFile();
        this.append = append;
        if (append) {
            if (reader.isRebuilt()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.requires.a.document.without.errors.even.if.recovery.was.possible"));
            }
            if (reader.isEncrypted()) {
                this.crypto = new PdfEncryption(reader.getDecrypt());
            }
            this.pdf_version.setAppendmode(true);
            this.file.reOpen();
            final byte[] buf = new byte[8192];
            int n;
            while ((n = this.file.read(buf)) > 0) {
                this.os.write(buf, 0, n);
            }
            this.file.close();
            this.prevxref = reader.getLastXref();
            reader.setAppendable(true);
        }
        else if (pdfVersion == '\0') {
            super.setPdfVersion(reader.getPdfVersion());
        }
        else {
            super.setPdfVersion(pdfVersion);
        }
        super.open();
        this.pdf.addWriter(this);
        if (append) {
            this.body.setRefnum(reader.getXrefSize());
            this.marked = new IntHashtable();
            if (reader.isNewXrefType()) {
                this.fullCompression = true;
            }
            if (reader.isHybridXref()) {
                this.fullCompression = false;
            }
        }
        this.initialXrefSize = reader.getXrefSize();
    }
    
    void close(final HashMap moreInfo) throws IOException {
        if (this.closed) {
            return;
        }
        if (this.useVp) {
            this.reader.setViewerPreferences(this.viewerPreferences);
            this.markUsed(this.reader.getTrailer().get(PdfName.ROOT));
        }
        if (this.flat) {
            this.flatFields();
        }
        if (this.flatFreeText) {
            this.flatFreeTextFields();
        }
        this.addFieldResources();
        final PdfDictionary catalog = this.reader.getCatalog();
        final PdfDictionary pages = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.PAGES));
        pages.put(PdfName.ITXT, new PdfString(Document.getRelease()));
        this.markUsed(pages);
        final PdfObject acroFormObject = PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), this.reader.getCatalog());
        if (acroFormObject instanceof PdfDictionary) {
            final PdfDictionary acroForm = (PdfDictionary)acroFormObject;
            if (this.acroFields != null && this.acroFields.getXfa().isChanged()) {
                this.markUsed(acroForm);
                if (!this.flat) {
                    this.acroFields.getXfa().setXfa(this);
                }
            }
            if (this.sigFlags != 0 && acroForm != null) {
                acroForm.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
                this.markUsed(acroForm);
                this.markUsed(catalog);
            }
        }
        this.closed = true;
        this.addSharedObjectsToBody();
        this.setOutlines();
        this.setJavaScript();
        this.addFileAttachments();
        if (this.openAction != null) {
            catalog.put(PdfName.OPENACTION, this.openAction);
        }
        if (this.pdf.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this));
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            final PdfDictionary ocdict = catalog.getAsDict(PdfName.OCPROPERTIES);
            if (ocdict == null) {
                this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
            }
            else {
                ocdict.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
                PdfDictionary ddict = ocdict.getAsDict(PdfName.D);
                if (ddict == null) {
                    ddict = new PdfDictionary();
                    ocdict.put(PdfName.D, ddict);
                }
                ddict.put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
                ddict.put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
                ddict.put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
                ddict.put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
            }
        }
        int skipInfo = -1;
        final PRIndirectReference iInfo = (PRIndirectReference)this.reader.getTrailer().get(PdfName.INFO);
        final PdfDictionary oldInfo = (PdfDictionary)PdfReader.getPdfObject(iInfo);
        String producer = null;
        if (iInfo != null) {
            skipInfo = iInfo.getNumber();
        }
        if (oldInfo != null && oldInfo.get(PdfName.PRODUCER) != null) {
            producer = oldInfo.getAsString(PdfName.PRODUCER).toString();
        }
        if (producer == null) {
            producer = Document.getVersion();
        }
        else if (producer.indexOf(Document.getProduct()) == -1) {
            final StringBuffer buf = new StringBuffer(producer);
            buf.append("; modified using ");
            buf.append(Document.getVersion());
            producer = buf.toString();
        }
        byte[] altMetadata = null;
        final PdfObject xmpo = PdfReader.getPdfObject(catalog.get(PdfName.METADATA));
        if (xmpo != null && xmpo.isStream()) {
            altMetadata = PdfReader.getStreamBytesRaw((PRStream)xmpo);
            PdfReader.killIndirect(catalog.get(PdfName.METADATA));
        }
        if (this.xmpMetadata != null) {
            altMetadata = this.xmpMetadata;
        }
        PdfDate date = null;
        if (this.modificationDate == null) {
            date = new PdfDate();
        }
        else {
            date = new PdfDate(this.modificationDate);
        }
        if (altMetadata != null) {
            PdfStream xmp;
            try {
                final XmpReader xmpr = new XmpReader(altMetadata);
                if (!xmpr.replace("http://ns.adobe.com/pdf/1.3/", "Producer", producer)) {
                    xmpr.add("rdf:Description", "http://ns.adobe.com/pdf/1.3/", "pdf:Producer", producer);
                }
                if (!xmpr.replace("http://ns.adobe.com/xap/1.0/", "ModifyDate", date.getW3CDate())) {
                    xmpr.add("rdf:Description", "http://ns.adobe.com/xap/1.0/", "xmp:ModifyDate", date.getW3CDate());
                }
                xmpr.replace("http://ns.adobe.com/xap/1.0/", "MetadataDate", date.getW3CDate());
                xmp = new PdfStream(xmpr.serializeDoc());
            }
            catch (final SAXException e) {
                xmp = new PdfStream(altMetadata);
            }
            catch (final IOException e2) {
                xmp = new PdfStream(altMetadata);
            }
            xmp.put(PdfName.TYPE, PdfName.METADATA);
            xmp.put(PdfName.SUBTYPE, PdfName.XML);
            if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                final PdfArray ar = new PdfArray();
                ar.add(PdfName.CRYPT);
                xmp.put(PdfName.FILTER, ar);
            }
            if (this.append && xmpo != null) {
                this.body.add(xmp, xmpo.getIndRef());
            }
            else {
                catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                this.markUsed(catalog);
            }
        }
        try {
            this.file.reOpen();
            this.alterContents();
            final int rootN = ((PRIndirectReference)this.reader.trailer.get(PdfName.ROOT)).getNumber();
            if (this.append) {
                final int[] keys = this.marked.getKeys();
                for (int k = 0; k < keys.length; ++k) {
                    final int j = keys[k];
                    final PdfObject obj = this.reader.getPdfObjectRelease(j);
                    if (obj != null && skipInfo != j && j < this.initialXrefSize) {
                        this.addToBody(obj, j, j != rootN);
                    }
                }
                for (int k = this.initialXrefSize; k < this.reader.getXrefSize(); ++k) {
                    final PdfObject obj2 = this.reader.getPdfObject(k);
                    if (obj2 != null) {
                        this.addToBody(obj2, this.getNewObjectNumber(this.reader, k, 0));
                    }
                }
            }
            else {
                for (int i = 1; i < this.reader.getXrefSize(); ++i) {
                    final PdfObject obj3 = this.reader.getPdfObjectRelease(i);
                    if (obj3 != null && skipInfo != i) {
                        this.addToBody(obj3, this.getNewObjectNumber(this.reader, i, 0), i != rootN);
                    }
                }
            }
        }
        finally {
            try {
                this.file.close();
            }
            catch (final Exception ex) {}
        }
        PdfIndirectReference encryption = null;
        PdfObject fileID = null;
        if (this.crypto != null) {
            if (this.append) {
                encryption = this.reader.getCryptoRef();
            }
            else {
                final PdfIndirectObject encryptionObject = this.addToBody(this.crypto.getEncryptionDictionary(), false);
                encryption = encryptionObject.getIndirectReference();
            }
            if (this.includeFileID) {
                fileID = this.crypto.getFileID();
            }
        }
        else if (this.includeFileID) {
            if (this.overrideFileId != null) {
                fileID = this.overrideFileId;
            }
            else {
                fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
            }
        }
        final PRIndirectReference iRoot = (PRIndirectReference)this.reader.trailer.get(PdfName.ROOT);
        final PdfIndirectReference root = new PdfIndirectReference(0, this.getNewObjectNumber(this.reader, iRoot.getNumber(), 0));
        PdfIndirectReference info = null;
        final PdfDictionary newInfo = new PdfDictionary();
        if (oldInfo != null) {
            for (final PdfName key : oldInfo.getKeys()) {
                final PdfObject value = PdfReader.getPdfObject(oldInfo.get(key));
                newInfo.put(key, value);
            }
        }
        if (moreInfo != null) {
            for (final Map.Entry entry : moreInfo.entrySet()) {
                final String key2 = entry.getKey();
                final PdfName keyName = new PdfName(key2);
                final String value2 = entry.getValue();
                if (value2 == null) {
                    newInfo.remove(keyName);
                }
                else {
                    newInfo.put(keyName, new PdfString(value2, "UnicodeBig"));
                }
            }
        }
        newInfo.put(PdfName.MODDATE, date);
        newInfo.put(PdfName.PRODUCER, new PdfString(producer));
        if (this.append) {
            if (iInfo == null) {
                info = this.addToBody(newInfo, false).getIndirectReference();
            }
            else {
                info = this.addToBody(newInfo, iInfo.getNumber(), false).getIndirectReference();
            }
        }
        else {
            info = this.addToBody(newInfo, false).getIndirectReference();
        }
        this.body.writeCrossReferenceTable(this.os, root, info, encryption, fileID, this.prevxref);
        if (this.fullCompression) {
            this.os.write(DocWriter.getISOBytes("startxref\n"));
            this.os.write(DocWriter.getISOBytes(String.valueOf(this.body.offset())));
            this.os.write(DocWriter.getISOBytes("\n%%EOF\n"));
        }
        else {
            final PdfTrailer trailer = new PdfTrailer(this.body.size(), this.body.offset(), root, info, encryption, fileID, this.prevxref);
            trailer.toPdf(this, this.os);
        }
        this.os.flush();
        if (this.isCloseStream()) {
            this.os.close();
        }
        this.reader.close();
    }
    
    void applyRotation(final PdfDictionary pageN, final ByteBuffer out) {
        if (!this.rotateContents) {
            return;
        }
        final Rectangle page = this.reader.getPageSizeWithRotation(pageN);
        final int rotation = page.getRotation();
        switch (rotation) {
            case 90: {
                out.append(PdfContents.ROTATE90);
                out.append(page.getTop());
                out.append(' ').append('0').append(PdfContents.ROTATEFINAL);
                break;
            }
            case 180: {
                out.append(PdfContents.ROTATE180);
                out.append(page.getRight());
                out.append(' ');
                out.append(page.getTop());
                out.append(PdfContents.ROTATEFINAL);
                break;
            }
            case 270: {
                out.append(PdfContents.ROTATE270);
                out.append('0').append(' ');
                out.append(page.getRight());
                out.append(PdfContents.ROTATEFINAL);
                break;
            }
        }
    }
    
    void alterContents() throws IOException {
        for (final PageStamp ps : this.pagesToContent.values()) {
            final PdfDictionary pageN = ps.pageN;
            this.markUsed(pageN);
            PdfArray ar = null;
            final PdfObject content = PdfReader.getPdfObject(pageN.get(PdfName.CONTENTS), pageN);
            if (content == null) {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            }
            else if (content.isArray()) {
                ar = (PdfArray)content;
                this.markUsed(ar);
            }
            else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(pageN.get(PdfName.CONTENTS));
                pageN.put(PdfName.CONTENTS, ar);
            }
            else {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            }
            final ByteBuffer out = new ByteBuffer();
            if (ps.under != null) {
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(pageN, out);
                out.append(ps.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (ps.over != null) {
                out.append(PdfContents.SAVESTATE);
            }
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(this.compressionLevel);
            ar.addFirst(this.addToBody(stream).getIndirectReference());
            out.reset();
            if (ps.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                final ByteBuffer buf = ps.over.getInternalBuffer();
                out.append(buf.getBuffer(), 0, ps.replacePoint);
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(pageN, out);
                out.append(buf.getBuffer(), ps.replacePoint, buf.size() - ps.replacePoint);
                out.append(PdfContents.RESTORESTATE);
                stream = new PdfStream(out.toByteArray());
                stream.flateCompress(this.compressionLevel);
                ar.add(this.addToBody(stream).getIndirectReference());
            }
            this.alterResources(ps);
        }
    }
    
    void alterResources(final PageStamp ps) {
        ps.pageN.put(PdfName.RESOURCES, ps.pageResources.getResources());
    }
    
    @Override
    protected int getNewObjectNumber(final PdfReader reader, final int number, final int generation) {
        final IntHashtable ref = this.readers2intrefs.get(reader);
        if (ref != null) {
            int n = ref.get(number);
            if (n == 0) {
                n = this.getIndirectReferenceNumber();
                ref.put(number, n);
            }
            return n;
        }
        if (this.currentPdfReaderInstance != null) {
            return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
        }
        if (this.append && number < this.initialXrefSize) {
            return number;
        }
        int n = this.myXref.get(number);
        if (n == 0) {
            n = this.getIndirectReferenceNumber();
            this.myXref.put(number, n);
        }
        return n;
    }
    
    @Override
    RandomAccessFileOrArray getReaderFile(final PdfReader reader) {
        if (this.readers2intrefs.containsKey(reader)) {
            final RandomAccessFileOrArray raf = this.readers2file.get(reader);
            if (raf != null) {
                return raf;
            }
            return reader.getSafeFile();
        }
        else {
            if (this.currentPdfReaderInstance == null) {
                return this.file;
            }
            return this.currentPdfReaderInstance.getReaderFile();
        }
    }
    
    public void registerReader(final PdfReader reader, final boolean openFile) throws IOException {
        if (this.readers2intrefs.containsKey(reader)) {
            return;
        }
        this.readers2intrefs.put(reader, new IntHashtable());
        if (openFile) {
            final RandomAccessFileOrArray raf = reader.getSafeFile();
            this.readers2file.put(reader, raf);
            raf.reOpen();
        }
    }
    
    public void unRegisterReader(final PdfReader reader) {
        if (!this.readers2intrefs.containsKey(reader)) {
            return;
        }
        this.readers2intrefs.remove(reader);
        final RandomAccessFileOrArray raf = this.readers2file.get(reader);
        if (raf == null) {
            return;
        }
        this.readers2file.remove(reader);
        try {
            raf.close();
        }
        catch (final Exception ex) {}
    }
    
    static void findAllObjects(final PdfReader reader, final PdfObject obj, final IntHashtable hits) {
        if (obj == null) {
            return;
        }
        switch (obj.type()) {
            case 10: {
                final PRIndirectReference iref = (PRIndirectReference)obj;
                if (reader != iref.getReader()) {
                    return;
                }
                if (hits.containsKey(iref.getNumber())) {
                    return;
                }
                hits.put(iref.getNumber(), 1);
                findAllObjects(reader, PdfReader.getPdfObject(obj), hits);
                return;
            }
            case 5: {
                final PdfArray a = (PdfArray)obj;
                for (int k = 0; k < a.size(); ++k) {
                    findAllObjects(reader, a.getPdfObject(k), hits);
                }
                return;
            }
            case 6:
            case 7: {
                final PdfDictionary dic = (PdfDictionary)obj;
                for (final PdfName name : dic.getKeys()) {
                    findAllObjects(reader, dic.get(name), hits);
                }
            }
            default: {}
        }
    }
    
    public void addComments(final FdfReader fdf) throws IOException {
        if (this.readers2intrefs.containsKey(fdf)) {
            return;
        }
        PdfDictionary catalog = fdf.getCatalog();
        catalog = catalog.getAsDict(PdfName.FDF);
        if (catalog == null) {
            return;
        }
        final PdfArray annots = catalog.getAsArray(PdfName.ANNOTS);
        if (annots == null || annots.size() == 0) {
            return;
        }
        this.registerReader(fdf, false);
        final IntHashtable hits = new IntHashtable();
        final HashMap irt = new HashMap();
        final ArrayList an = new ArrayList();
        for (int k = 0; k < annots.size(); ++k) {
            final PdfObject obj = annots.getPdfObject(k);
            final PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObject(obj);
            final PdfNumber page = annot.getAsNumber(PdfName.PAGE);
            if (page != null) {
                if (page.intValue() < this.reader.getNumberOfPages()) {
                    findAllObjects(fdf, obj, hits);
                    an.add(obj);
                    if (obj.type() == 10) {
                        final PdfObject nm = PdfReader.getPdfObject(annot.get(PdfName.NM));
                        if (nm != null && nm.type() == 3) {
                            irt.put(nm.toString(), obj);
                        }
                    }
                }
            }
        }
        final int[] arhits = hits.getKeys();
        for (int i = 0; i < arhits.length; ++i) {
            final int n = arhits[i];
            PdfObject obj2 = fdf.getPdfObject(n);
            if (obj2.type() == 6) {
                final PdfObject str = PdfReader.getPdfObject(((PdfDictionary)obj2).get(PdfName.IRT));
                if (str != null && str.type() == 3) {
                    final PdfObject j = irt.get(str.toString());
                    if (j != null) {
                        final PdfDictionary dic2 = new PdfDictionary();
                        dic2.merge((PdfDictionary)obj2);
                        dic2.put(PdfName.IRT, j);
                        obj2 = dic2;
                    }
                }
            }
            this.addToBody(obj2, this.getNewObjectNumber(fdf, n, 0));
        }
        for (int i = 0; i < an.size(); ++i) {
            final PdfObject obj3 = an.get(i);
            final PdfDictionary annot2 = (PdfDictionary)PdfReader.getPdfObject(obj3);
            final PdfNumber page2 = annot2.getAsNumber(PdfName.PAGE);
            final PdfDictionary dic3 = this.reader.getPageN(page2.intValue() + 1);
            PdfArray annotsp = (PdfArray)PdfReader.getPdfObject(dic3.get(PdfName.ANNOTS), dic3);
            if (annotsp == null) {
                annotsp = new PdfArray();
                dic3.put(PdfName.ANNOTS, annotsp);
                this.markUsed(dic3);
            }
            this.markUsed(annotsp);
            annotsp.add(obj3);
        }
    }
    
    PageStamp getPageStamp(final int pageNum) {
        final PdfDictionary pageN = this.reader.getPageN(pageNum);
        PageStamp ps = this.pagesToContent.get(pageN);
        if (ps == null) {
            ps = new PageStamp(this, this.reader, pageN);
            this.pagesToContent.put(pageN, ps);
        }
        return ps;
    }
    
    PdfContentByte getUnderContent(final int pageNum) {
        if (pageNum < 1 || pageNum > this.reader.getNumberOfPages()) {
            return null;
        }
        final PageStamp ps = this.getPageStamp(pageNum);
        if (ps.under == null) {
            ps.under = new StampContent(this, ps);
        }
        return ps.under;
    }
    
    PdfContentByte getOverContent(final int pageNum) {
        if (pageNum < 1 || pageNum > this.reader.getNumberOfPages()) {
            return null;
        }
        final PageStamp ps = this.getPageStamp(pageNum);
        if (ps.over == null) {
            ps.over = new StampContent(this, ps);
        }
        return ps.over;
    }
    
    void correctAcroFieldPages(final int page) {
        if (this.acroFields == null) {
            return;
        }
        if (page > this.reader.getNumberOfPages()) {
            return;
        }
        final HashMap fields = this.acroFields.getFields();
        for (final AcroFields.Item item : fields.values()) {
            for (int k = 0; k < item.size(); ++k) {
                final int p = item.getPage(k);
                if (p >= page) {
                    item.forcePage(k, p + 1);
                }
            }
        }
    }
    
    private static void moveRectangle(final PdfDictionary dic2, final PdfReader r, final int pageImported, final PdfName key, final String name) {
        final Rectangle m = r.getBoxSize(pageImported, name);
        if (m == null) {
            dic2.remove(key);
        }
        else {
            dic2.put(key, new PdfRectangle(m));
        }
    }
    
    void replacePage(final PdfReader r, final int pageImported, final int pageReplaced) {
        final PdfDictionary pageN = this.reader.getPageN(pageReplaced);
        if (this.pagesToContent.containsKey(pageN)) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.page.cannot.be.replaced.new.content.was.already.added"));
        }
        final PdfImportedPage p = this.getImportedPage(r, pageImported);
        final PdfDictionary dic2 = this.reader.getPageNRelease(pageReplaced);
        dic2.remove(PdfName.RESOURCES);
        dic2.remove(PdfName.CONTENTS);
        moveRectangle(dic2, r, pageImported, PdfName.MEDIABOX, "media");
        moveRectangle(dic2, r, pageImported, PdfName.CROPBOX, "crop");
        moveRectangle(dic2, r, pageImported, PdfName.TRIMBOX, "trim");
        moveRectangle(dic2, r, pageImported, PdfName.ARTBOX, "art");
        moveRectangle(dic2, r, pageImported, PdfName.BLEEDBOX, "bleed");
        dic2.put(PdfName.ROTATE, new PdfNumber(r.getPageRotation(pageImported)));
        final PdfContentByte cb = this.getOverContent(pageReplaced);
        cb.addTemplate(p, 0.0f, 0.0f);
        final PageStamp ps = this.pagesToContent.get(pageN);
        ps.replacePoint = ps.over.getInternalBuffer().size();
    }
    
    void insertPage(int pageNumber, final Rectangle mediabox) {
        final Rectangle media = new Rectangle(mediabox);
        final int rotation = media.getRotation() % 360;
        final PdfDictionary page = new PdfDictionary(PdfName.PAGE);
        final PdfDictionary resources = new PdfDictionary();
        final PdfArray procset = new PdfArray();
        procset.add(PdfName.PDF);
        procset.add(PdfName.TEXT);
        procset.add(PdfName.IMAGEB);
        procset.add(PdfName.IMAGEC);
        procset.add(PdfName.IMAGEI);
        resources.put(PdfName.PROCSET, procset);
        page.put(PdfName.RESOURCES, resources);
        page.put(PdfName.ROTATE, new PdfNumber(rotation));
        page.put(PdfName.MEDIABOX, new PdfRectangle(media, rotation));
        final PRIndirectReference pref = this.reader.addPdfObject(page);
        PRIndirectReference parentRef;
        PdfDictionary parent;
        if (pageNumber > this.reader.getNumberOfPages()) {
            final PdfDictionary lastPage = this.reader.getPageNRelease(this.reader.getNumberOfPages());
            parentRef = (PRIndirectReference)lastPage.get(PdfName.PARENT);
            parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
            parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
            final PdfArray kids = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            kids.add(pref);
            this.markUsed(kids);
            this.reader.pageRefs.insertPage(pageNumber, pref);
        }
        else {
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            final PdfDictionary firstPage = this.reader.getPageN(pageNumber);
            final PRIndirectReference firstPageRef = this.reader.getPageOrigRef(pageNumber);
            this.reader.releasePage(pageNumber);
            parentRef = (PRIndirectReference)firstPage.get(PdfName.PARENT);
            parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
            parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
            final PdfArray kids2 = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            final int len = kids2.size();
            final int num = firstPageRef.getNumber();
            for (int k = 0; k < len; ++k) {
                final PRIndirectReference cur = (PRIndirectReference)kids2.getPdfObject(k);
                if (num == cur.getNumber()) {
                    kids2.add(k, pref);
                    break;
                }
            }
            if (len == kids2.size()) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("internal.inconsistence"));
            }
            this.markUsed(kids2);
            this.reader.pageRefs.insertPage(pageNumber, pref);
            this.correctAcroFieldPages(pageNumber);
        }
        page.put(PdfName.PARENT, parentRef);
        while (parent != null) {
            this.markUsed(parent);
            final PdfNumber count = (PdfNumber)PdfReader.getPdfObjectRelease(parent.get(PdfName.COUNT));
            parent.put(PdfName.COUNT, new PdfNumber(count.intValue() + 1));
            parent = parent.getAsDict(PdfName.PARENT);
        }
    }
    
    boolean isRotateContents() {
        return this.rotateContents;
    }
    
    void setRotateContents(final boolean rotateContents) {
        this.rotateContents = rotateContents;
    }
    
    boolean isContentWritten() {
        return this.body.size() > 1;
    }
    
    AcroFields getAcroFields() {
        if (this.acroFields == null) {
            this.acroFields = new AcroFields(this.reader, this);
        }
        return this.acroFields;
    }
    
    void setFormFlattening(final boolean flat) {
        this.flat = flat;
    }
    
    void setFreeTextFlattening(final boolean flat) {
        this.flatFreeText = flat;
    }
    
    boolean partialFormFlattening(final String name) {
        this.getAcroFields();
        if (this.acroFields.getXfa().isXfaPresent()) {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("partial.form.flattening.is.not.supported.with.xfa.forms"));
        }
        if (!this.acroFields.getFields().containsKey(name)) {
            return false;
        }
        this.partialFlattening.add(name);
        return true;
    }
    
    void flatFields() {
        if (this.append) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("field.flattening.is.not.supported.in.append.mode"));
        }
        this.getAcroFields();
        final HashMap fields = this.acroFields.getFields();
        if (this.fieldsAdded && this.partialFlattening.isEmpty()) {
            final Iterator i = fields.keySet().iterator();
            while (i.hasNext()) {
                this.partialFlattening.add(i.next());
            }
        }
        final PdfDictionary acroForm = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        PdfArray acroFds = null;
        if (acroForm != null) {
            acroFds = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
        }
        for (final Map.Entry entry : fields.entrySet()) {
            final String name = entry.getKey();
            if (!this.partialFlattening.isEmpty() && !this.partialFlattening.contains(name)) {
                continue;
            }
            final AcroFields.Item item = entry.getValue();
            for (int k = 0; k < item.size(); ++k) {
                final PdfDictionary merged = item.getMerged(k);
                final PdfNumber ff = merged.getAsNumber(PdfName.F);
                int flags = 0;
                if (ff != null) {
                    flags = ff.intValue();
                }
                final int page = item.getPage(k);
                final PdfDictionary appDic = merged.getAsDict(PdfName.AP);
                if (appDic != null && (flags & 0x4) != 0x0 && (flags & 0x2) == 0x0) {
                    final PdfObject obj = appDic.get(PdfName.N);
                    PdfAppearance app = null;
                    if (obj != null) {
                        PdfObject objReal = PdfReader.getPdfObject(obj);
                        if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
                            app = new PdfAppearance((PdfIndirectReference)obj);
                        }
                        else if (objReal instanceof PdfStream) {
                            ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                            app = new PdfAppearance((PdfIndirectReference)obj);
                        }
                        else if (objReal != null && objReal.isDictionary()) {
                            final PdfName as = merged.getAsName(PdfName.AS);
                            if (as != null) {
                                final PdfIndirectReference iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as);
                                if (iref != null) {
                                    app = new PdfAppearance(iref);
                                    if (iref.isIndirect()) {
                                        objReal = PdfReader.getPdfObject(iref);
                                        ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                                    }
                                }
                            }
                        }
                    }
                    if (app != null) {
                        final Rectangle box = PdfReader.getNormalizedRectangle(merged.getAsArray(PdfName.RECT));
                        final PdfContentByte cb = this.getOverContent(page);
                        cb.setLiteral("Q ");
                        cb.addTemplate(app, box.getLeft(), box.getBottom());
                        cb.setLiteral("q ");
                    }
                }
                if (!this.partialFlattening.isEmpty()) {
                    final PdfDictionary pageDic = this.reader.getPageN(page);
                    final PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                    if (annots != null) {
                        for (int idx = 0; idx < annots.size(); ++idx) {
                            final PdfObject ran = annots.getPdfObject(idx);
                            if (ran.isIndirect()) {
                                final PdfObject ran2 = item.getWidgetRef(k);
                                if (ran2.isIndirect()) {
                                    if (((PRIndirectReference)ran).getNumber() == ((PRIndirectReference)ran2).getNumber()) {
                                        annots.remove(idx--);
                                        PRIndirectReference wdref = (PRIndirectReference)ran2;
                                        while (true) {
                                            final PdfDictionary wd = (PdfDictionary)PdfReader.getPdfObject(wdref);
                                            final PRIndirectReference parentRef = (PRIndirectReference)wd.get(PdfName.PARENT);
                                            PdfReader.killIndirect(wdref);
                                            if (parentRef == null) {
                                                for (int fr = 0; fr < acroFds.size(); ++fr) {
                                                    final PdfObject h = acroFds.getPdfObject(fr);
                                                    if (h.isIndirect() && ((PRIndirectReference)h).getNumber() == wdref.getNumber()) {
                                                        acroFds.remove(fr);
                                                        --fr;
                                                    }
                                                }
                                                break;
                                            }
                                            final PdfDictionary parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
                                            final PdfArray kids = parent.getAsArray(PdfName.KIDS);
                                            for (int fr2 = 0; fr2 < kids.size(); ++fr2) {
                                                final PdfObject h2 = kids.getPdfObject(fr2);
                                                if (h2.isIndirect() && ((PRIndirectReference)h2).getNumber() == wdref.getNumber()) {
                                                    kids.remove(fr2);
                                                    --fr2;
                                                }
                                            }
                                            if (!kids.isEmpty()) {
                                                break;
                                            }
                                            wdref = parentRef;
                                        }
                                    }
                                }
                            }
                        }
                        if (annots.isEmpty()) {
                            PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                            pageDic.remove(PdfName.ANNOTS);
                        }
                    }
                }
            }
        }
        if (!this.fieldsAdded && this.partialFlattening.isEmpty()) {
            for (int page2 = 1; page2 <= this.reader.getNumberOfPages(); ++page2) {
                final PdfDictionary pageDic2 = this.reader.getPageN(page2);
                final PdfArray annots2 = pageDic2.getAsArray(PdfName.ANNOTS);
                if (annots2 != null) {
                    for (int idx2 = 0; idx2 < annots2.size(); ++idx2) {
                        final PdfObject annoto = annots2.getDirectObject(idx2);
                        if (!(annoto instanceof PdfIndirectReference) || annoto.isIndirect()) {
                            if (!annoto.isDictionary() || PdfName.WIDGET.equals(((PdfDictionary)annoto).get(PdfName.SUBTYPE))) {
                                annots2.remove(idx2);
                                --idx2;
                            }
                        }
                    }
                    if (annots2.isEmpty()) {
                        PdfReader.killIndirect(pageDic2.get(PdfName.ANNOTS));
                        pageDic2.remove(PdfName.ANNOTS);
                    }
                }
            }
            this.eliminateAcroformObjects();
        }
    }
    
    void eliminateAcroformObjects() {
        final PdfObject acro = this.reader.getCatalog().get(PdfName.ACROFORM);
        if (acro == null) {
            return;
        }
        final PdfDictionary acrodic = (PdfDictionary)PdfReader.getPdfObject(acro);
        this.reader.killXref(acrodic.get(PdfName.XFA));
        acrodic.remove(PdfName.XFA);
        final PdfObject iFields = acrodic.get(PdfName.FIELDS);
        if (iFields != null) {
            final PdfDictionary kids = new PdfDictionary();
            kids.put(PdfName.KIDS, iFields);
            this.sweepKids(kids);
            PdfReader.killIndirect(iFields);
            acrodic.put(PdfName.FIELDS, new PdfArray());
        }
        acrodic.remove(PdfName.SIGFLAGS);
    }
    
    void sweepKids(final PdfObject obj) {
        final PdfObject oo = PdfReader.killIndirect(obj);
        if (oo == null || !oo.isDictionary()) {
            return;
        }
        final PdfDictionary dic = (PdfDictionary)oo;
        final PdfArray kids = (PdfArray)PdfReader.killIndirect(dic.get(PdfName.KIDS));
        if (kids == null) {
            return;
        }
        for (int k = 0; k < kids.size(); ++k) {
            this.sweepKids(kids.getPdfObject(k));
        }
    }
    
    private void flatFreeTextFields() {
        if (this.append) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("freetext.flattening.is.not.supported.in.append.mode"));
        }
        for (int page = 1; page <= this.reader.getNumberOfPages(); ++page) {
            final PdfDictionary pageDic = this.reader.getPageN(page);
            final PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            if (annots != null) {
                for (int idx = 0; idx < annots.size(); ++idx) {
                    final PdfObject annoto = annots.getDirectObject(idx);
                    if (!(annoto instanceof PdfIndirectReference) || annoto.isIndirect()) {
                        final PdfDictionary annDic = (PdfDictionary)annoto;
                        if (annDic.get(PdfName.SUBTYPE).equals(PdfName.FREETEXT)) {
                            final PdfNumber ff = annDic.getAsNumber(PdfName.F);
                            final int flags = (ff != null) ? ff.intValue() : 0;
                            if ((flags & 0x4) != 0x0 && (flags & 0x2) == 0x0) {
                                final PdfObject obj1 = annDic.get(PdfName.AP);
                                if (obj1 != null) {
                                    final PdfDictionary appDic = (PdfDictionary)((obj1 instanceof PdfIndirectReference) ? PdfReader.getPdfObject(obj1) : ((PdfDictionary)obj1));
                                    final PdfObject obj2 = appDic.get(PdfName.N);
                                    PdfAppearance app = null;
                                    PdfObject objReal = PdfReader.getPdfObject(obj2);
                                    if (obj2 instanceof PdfIndirectReference && !obj2.isIndirect()) {
                                        app = new PdfAppearance((PdfIndirectReference)obj2);
                                    }
                                    else if (objReal instanceof PdfStream) {
                                        ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                                        app = new PdfAppearance((PdfIndirectReference)obj2);
                                    }
                                    else if (objReal.isDictionary()) {
                                        final PdfName as_p = appDic.getAsName(PdfName.AS);
                                        if (as_p != null) {
                                            final PdfIndirectReference iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as_p);
                                            if (iref != null) {
                                                app = new PdfAppearance(iref);
                                                if (iref.isIndirect()) {
                                                    objReal = PdfReader.getPdfObject(iref);
                                                    ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                                                }
                                            }
                                        }
                                    }
                                    if (app != null) {
                                        final Rectangle box = PdfReader.getNormalizedRectangle(annDic.getAsArray(PdfName.RECT));
                                        final PdfContentByte cb = this.getOverContent(page);
                                        cb.setLiteral("Q ");
                                        cb.addTemplate(app, box.getLeft(), box.getBottom());
                                        cb.setLiteral("q ");
                                    }
                                }
                            }
                        }
                    }
                }
                for (int idx = 0; idx < annots.size(); ++idx) {
                    final PdfDictionary annot = annots.getAsDict(idx);
                    if (annot != null && PdfName.FREETEXT.equals(annot.get(PdfName.SUBTYPE))) {
                        annots.remove(idx);
                        --idx;
                    }
                }
                if (annots.isEmpty()) {
                    PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                    pageDic.remove(PdfName.ANNOTS);
                }
            }
        }
    }
    
    @Override
    public PdfIndirectReference getPageReference(final int page) {
        final PdfIndirectReference ref = this.reader.getPageOrigRef(page);
        if (ref == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", page));
        }
        return ref;
    }
    
    @Override
    public void addAnnotation(final PdfAnnotation annot) {
        throw new RuntimeException(MessageLocalization.getComposedMessage("unsupported.in.this.context.use.pdfstamper.addannotation"));
    }
    
    void addDocumentField(final PdfIndirectReference ref) {
        final PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            this.markUsed(catalog);
        }
        PdfArray fields = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
        if (fields == null) {
            fields = new PdfArray();
            acroForm.put(PdfName.FIELDS, fields);
            this.markUsed(acroForm);
        }
        if (!acroForm.contains(PdfName.DA)) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(acroForm);
        }
        fields.add(ref);
        this.markUsed(fields);
    }
    
    void addFieldResources() throws IOException {
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        final PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            this.markUsed(catalog);
        }
        PdfDictionary dr = (PdfDictionary)PdfReader.getPdfObject(acroForm.get(PdfName.DR), acroForm);
        if (dr == null) {
            dr = new PdfDictionary();
            acroForm.put(PdfName.DR, dr);
            this.markUsed(acroForm);
        }
        this.markUsed(dr);
        for (final PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources(), this);
        }
        PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
        if (fonts == null) {
            fonts = new PdfDictionary();
            dr.put(PdfName.FONT, fonts);
        }
        if (!fonts.contains(PdfName.HELV)) {
            final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
            dic.put(PdfName.BASEFONT, PdfName.HELVETICA);
            dic.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
            dic.put(PdfName.NAME, PdfName.HELV);
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.HELV, this.addToBody(dic).getIndirectReference());
        }
        if (!fonts.contains(PdfName.ZADB)) {
            final PdfDictionary dic = new PdfDictionary(PdfName.FONT);
            dic.put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
            dic.put(PdfName.NAME, PdfName.ZADB);
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.ZADB, this.addToBody(dic).getIndirectReference());
        }
        if (acroForm.get(PdfName.DA) == null) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            this.markUsed(acroForm);
        }
    }
    
    void expandFields(final PdfFormField field, final ArrayList allAnnots) {
        allAnnots.add(field);
        final ArrayList kids = field.getKids();
        if (kids != null) {
            for (int k = 0; k < kids.size(); ++k) {
                this.expandFields(kids.get(k), allAnnots);
            }
        }
    }
    
    void addAnnotation(PdfAnnotation annot, PdfDictionary pageN) {
        try {
            final ArrayList allAnnots = new ArrayList();
            if (annot.isForm()) {
                this.fieldsAdded = true;
                this.getAcroFields();
                final PdfFormField field = (PdfFormField)annot;
                if (field.getParent() != null) {
                    return;
                }
                this.expandFields(field, allAnnots);
            }
            else {
                allAnnots.add(annot);
            }
            for (int k = 0; k < allAnnots.size(); ++k) {
                annot = allAnnots.get(k);
                if (annot.getPlaceInPage() > 0) {
                    pageN = this.reader.getPageN(annot.getPlaceInPage());
                }
                if (annot.isForm()) {
                    if (!annot.isUsed()) {
                        final HashMap templates = annot.getTemplates();
                        if (templates != null) {
                            this.fieldTemplates.putAll(templates);
                        }
                    }
                    final PdfFormField field2 = (PdfFormField)annot;
                    if (field2.getParent() == null) {
                        this.addDocumentField(field2.getIndirectReference());
                    }
                }
                if (annot.isAnnotation()) {
                    final PdfObject pdfobj = PdfReader.getPdfObject(pageN.get(PdfName.ANNOTS), pageN);
                    PdfArray annots = null;
                    if (pdfobj == null || !pdfobj.isArray()) {
                        annots = new PdfArray();
                        pageN.put(PdfName.ANNOTS, annots);
                        this.markUsed(pageN);
                    }
                    else {
                        annots = (PdfArray)pdfobj;
                    }
                    annots.add(annot.getIndirectReference());
                    this.markUsed(annots);
                    if (!annot.isUsed()) {
                        final PdfRectangle rect = (PdfRectangle)annot.get(PdfName.RECT);
                        if (rect != null && (rect.left() != 0.0f || rect.right() != 0.0f || rect.top() != 0.0f || rect.bottom() != 0.0f)) {
                            final int rotation = this.reader.getPageRotation(pageN);
                            final Rectangle pageSize = this.reader.getPageSizeWithRotation(pageN);
                            switch (rotation) {
                                case 90: {
                                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.top(), rect.right(), pageSize.getTop() - rect.bottom(), rect.left()));
                                    break;
                                }
                                case 180: {
                                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                    break;
                                }
                                case 270: {
                                    annot.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!annot.isUsed()) {
                    annot.setUsed();
                    this.addToBody(annot, annot.getIndirectReference());
                }
            }
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    @Override
    void addAnnotation(final PdfAnnotation annot, final int page) {
        annot.setPage(page);
        this.addAnnotation(annot, this.reader.getPageN(page));
    }
    
    private void outlineTravel(PRIndirectReference outline) {
        while (outline != null) {
            final PdfDictionary outlineR = (PdfDictionary)PdfReader.getPdfObjectRelease(outline);
            final PRIndirectReference first = (PRIndirectReference)outlineR.get(PdfName.FIRST);
            if (first != null) {
                this.outlineTravel(first);
            }
            PdfReader.killIndirect(outlineR.get(PdfName.DEST));
            PdfReader.killIndirect(outlineR.get(PdfName.A));
            PdfReader.killIndirect(outline);
            outline = (PRIndirectReference)outlineR.get(PdfName.NEXT);
        }
    }
    
    void deleteOutlines() {
        final PdfDictionary catalog = this.reader.getCatalog();
        final PRIndirectReference outlines = (PRIndirectReference)catalog.get(PdfName.OUTLINES);
        if (outlines == null) {
            return;
        }
        this.outlineTravel(outlines);
        PdfReader.killIndirect(outlines);
        catalog.remove(PdfName.OUTLINES);
        this.markUsed(catalog);
    }
    
    void setJavaScript() throws IOException {
        final HashMap djs = this.pdf.getDocumentLevelJS();
        if (djs.isEmpty()) {
            return;
        }
        final PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            this.markUsed(catalog);
        }
        this.markUsed(names);
        final PdfDictionary tree = PdfNameTree.writeTree(djs, this);
        names.put(PdfName.JAVASCRIPT, this.addToBody(tree).getIndirectReference());
    }
    
    void addFileAttachments() throws IOException {
        final HashMap fs = this.pdf.getDocumentFileAttachment();
        if (fs.isEmpty()) {
            return;
        }
        final PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            this.markUsed(catalog);
        }
        this.markUsed(names);
        final HashMap old = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(names.get(PdfName.EMBEDDEDFILES)));
        for (final Map.Entry entry : fs.entrySet()) {
            final String name = entry.getKey();
            int k;
            String nn;
            for (k = 0, nn = name; old.containsKey(nn); nn = nn + " " + k) {
                ++k;
            }
            old.put(nn, entry.getValue());
        }
        final PdfDictionary tree = PdfNameTree.writeTree(old, this);
        final PdfObject oldEmbeddedFiles = names.get(PdfName.EMBEDDEDFILES);
        if (oldEmbeddedFiles != null) {
            PdfReader.killIndirect(oldEmbeddedFiles);
        }
        names.put(PdfName.EMBEDDEDFILES, this.addToBody(tree).getIndirectReference());
    }
    
    void makePackage(final PdfCollection collection) {
        final PdfDictionary catalog = this.reader.getCatalog();
        catalog.put(PdfName.COLLECTION, collection);
    }
    
    void setOutlines() throws IOException {
        if (this.newBookmarks == null) {
            return;
        }
        this.deleteOutlines();
        if (this.newBookmarks.isEmpty()) {
            return;
        }
        final PdfDictionary catalog = this.reader.getCatalog();
        final boolean namedAsNames = catalog.get(PdfName.DESTS) != null;
        this.writeOutlines(catalog, namedAsNames);
        this.markUsed(catalog);
    }
    
    @Override
    public void setViewerPreferences(final int preferences) {
        this.useVp = true;
        this.viewerPreferences.setViewerPreferences(preferences);
    }
    
    @Override
    public void addViewerPreference(final PdfName key, final PdfObject value) {
        this.useVp = true;
        this.viewerPreferences.addViewerPreference(key, value);
    }
    
    @Override
    public void setSigFlags(final int f) {
        this.sigFlags |= f;
    }
    
    @Override
    public void setPageAction(final PdfName actionType, final PdfAction action) throws PdfException {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }
    
    void setPageAction(final PdfName actionType, final PdfAction action, final int page) throws PdfException {
        if (!actionType.equals(PdfStamperImp.PAGE_OPEN) && !actionType.equals(PdfStamperImp.PAGE_CLOSE)) {
            throw new PdfException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", actionType.toString()));
        }
        final PdfDictionary pg = this.reader.getPageN(page);
        PdfDictionary aa = (PdfDictionary)PdfReader.getPdfObject(pg.get(PdfName.AA), pg);
        if (aa == null) {
            aa = new PdfDictionary();
            pg.put(PdfName.AA, aa);
            this.markUsed(pg);
        }
        aa.put(actionType, action);
        this.markUsed(aa);
    }
    
    @Override
    public void setDuration(final int seconds) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }
    
    @Override
    public void setTransition(final PdfTransition transition) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }
    
    void setDuration(final int seconds, final int page) {
        final PdfDictionary pg = this.reader.getPageN(page);
        if (seconds < 0) {
            pg.remove(PdfName.DUR);
        }
        else {
            pg.put(PdfName.DUR, new PdfNumber(seconds));
        }
        this.markUsed(pg);
    }
    
    void setTransition(final PdfTransition transition, final int page) {
        final PdfDictionary pg = this.reader.getPageN(page);
        if (transition == null) {
            pg.remove(PdfName.TRANS);
        }
        else {
            pg.put(PdfName.TRANS, transition.getTransitionDictionary());
        }
        this.markUsed(pg);
    }
    
    protected void markUsed(final PdfObject obj) {
        if (this.append && obj != null) {
            PRIndirectReference ref = null;
            if (obj.type() == 10) {
                ref = (PRIndirectReference)obj;
            }
            else {
                ref = obj.getIndRef();
            }
            if (ref != null) {
                this.marked.put(ref.getNumber(), 1);
            }
        }
    }
    
    protected void markUsed(final int num) {
        if (this.append) {
            this.marked.put(num, 1);
        }
    }
    
    boolean isAppend() {
        return this.append;
    }
    
    @Override
    public void setAdditionalAction(final PdfName actionType, final PdfAction action) throws PdfException {
        if (!actionType.equals(PdfStamperImp.DOCUMENT_CLOSE) && !actionType.equals(PdfStamperImp.WILL_SAVE) && !actionType.equals(PdfStamperImp.DID_SAVE) && !actionType.equals(PdfStamperImp.WILL_PRINT) && !actionType.equals(PdfStamperImp.DID_PRINT)) {
            throw new PdfException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", actionType.toString()));
        }
        PdfDictionary aa = this.reader.getCatalog().getAsDict(PdfName.AA);
        if (aa == null) {
            if (action == null) {
                return;
            }
            aa = new PdfDictionary();
            this.reader.getCatalog().put(PdfName.AA, aa);
        }
        this.markUsed(aa);
        if (action == null) {
            aa.remove(actionType);
        }
        else {
            aa.put(actionType, action);
        }
    }
    
    @Override
    public void setOpenAction(final PdfAction action) {
        this.openAction = action;
    }
    
    @Override
    public void setOpenAction(final String name) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("open.actions.by.name.are.not.supported"));
    }
    
    @Override
    public void setThumbnail(final Image image) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.setthumbnail"));
    }
    
    void setThumbnail(final Image image, final int page) throws DocumentException {
        final PdfIndirectReference thumb = this.getImageReference(this.addDirectImageSimple(image));
        this.reader.resetReleasePage();
        final PdfDictionary dic = this.reader.getPageN(page);
        dic.put(PdfName.THUMB, thumb);
        this.reader.resetReleasePage();
    }
    
    @Override
    public PdfContentByte getDirectContentUnder() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }
    
    @Override
    public PdfContentByte getDirectContent() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }
    
    protected void readOCProperties() {
        if (!this.documentOCG.isEmpty()) {
            return;
        }
        final PdfDictionary dict = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
        if (dict == null) {
            return;
        }
        final PdfArray ocgs = dict.getAsArray(PdfName.OCGS);
        final HashMap ocgmap = new HashMap();
        final Iterator i = ocgs.listIterator();
        while (i.hasNext()) {
            final PdfIndirectReference ref = i.next();
            final PdfLayer layer = new PdfLayer((String)null);
            layer.setRef(ref);
            layer.setOnPanel(false);
            layer.merge((PdfDictionary)PdfReader.getPdfObject(ref));
            ocgmap.put(ref.toString(), layer);
        }
        final PdfDictionary d = dict.getAsDict(PdfName.D);
        final PdfArray off = d.getAsArray(PdfName.OFF);
        if (off != null) {
            final Iterator j = off.listIterator();
            while (j.hasNext()) {
                final PdfIndirectReference ref = j.next();
                final PdfLayer layer = ocgmap.get(ref.toString());
                layer.setOn(false);
            }
        }
        final PdfArray order = d.getAsArray(PdfName.ORDER);
        if (order != null) {
            this.addOrder(null, order, ocgmap);
        }
        this.documentOCG.addAll(ocgmap.values());
        this.OCGRadioGroup = d.getAsArray(PdfName.RBGROUPS);
        this.OCGLocked = d.getAsArray(PdfName.LOCKED);
        if (this.OCGLocked == null) {
            this.OCGLocked = new PdfArray();
        }
    }
    
    private void addOrder(final PdfLayer parent, final PdfArray arr, final Map ocgmap) {
        for (int i = 0; i < arr.size(); ++i) {
            PdfObject obj = arr.getPdfObject(i);
            if (obj.isIndirect()) {
                final PdfLayer layer = ocgmap.get(obj.toString());
                layer.setOnPanel(true);
                this.registerLayer(layer);
                if (parent != null) {
                    parent.addChild(layer);
                }
                if (arr.size() > i + 1 && arr.getPdfObject(i + 1).isArray()) {
                    ++i;
                    this.addOrder(layer, (PdfArray)arr.getPdfObject(i), ocgmap);
                }
            }
            else if (obj.isArray()) {
                final PdfArray sub = (PdfArray)obj;
                if (sub.isEmpty()) {
                    return;
                }
                obj = sub.getPdfObject(0);
                if (obj.isString()) {
                    final PdfLayer layer = new PdfLayer(obj.toString());
                    layer.setOnPanel(true);
                    this.registerLayer(layer);
                    if (parent != null) {
                        parent.addChild(layer);
                    }
                    final PdfArray array = new PdfArray();
                    final Iterator j = sub.listIterator();
                    while (j.hasNext()) {
                        array.add(j.next());
                    }
                    this.addOrder(layer, array, ocgmap);
                }
                else {
                    this.addOrder(parent, (PdfArray)obj, ocgmap);
                }
            }
        }
    }
    
    public Map getPdfLayers() {
        if (this.documentOCG.isEmpty()) {
            this.readOCProperties();
        }
        final HashMap map = new HashMap();
        for (final PdfLayer layer : this.documentOCG) {
            String key;
            if (layer.getTitle() == null) {
                key = layer.getAsString(PdfName.NAME).toString();
            }
            else {
                key = layer.getTitle();
            }
            if (map.containsKey(key)) {
                int seq;
                String tmp;
                for (seq = 2, tmp = key + "(" + seq + ")"; map.containsKey(tmp); tmp = key + "(" + seq + ")") {
                    ++seq;
                }
                key = tmp;
            }
            map.put(key, layer);
        }
        return map;
    }
    
    public boolean isIncludeFileID() {
        return this.includeFileID;
    }
    
    public void setIncludeFileID(final boolean includeFileID) {
        this.includeFileID = includeFileID;
    }
    
    public PdfObject getOverrideFileId() {
        return this.overrideFileId;
    }
    
    public void setOverrideFileId(final PdfObject overrideFileId) {
        this.overrideFileId = overrideFileId;
    }
    
    public Calendar getModificationDate() {
        return this.modificationDate;
    }
    
    public void setModificationDate(final Calendar modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    static class PageStamp
    {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        int replacePoint;
        
        PageStamp(final PdfStamperImp stamper, final PdfReader reader, final PdfDictionary pageN) {
            this.replacePoint = 0;
            this.pageN = pageN;
            this.pageResources = new PageResources();
            final PdfDictionary resources = pageN.getAsDict(PdfName.RESOURCES);
            this.pageResources.setOriginalResources(resources, stamper.namePtr);
        }
    }
}
