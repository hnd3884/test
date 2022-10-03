package com.lowagie.text.pdf;

import java.util.Map;
import java.util.ArrayList;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import java.util.Iterator;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.DocListener;
import java.io.OutputStream;
import com.lowagie.text.Document;
import java.util.HashMap;

public class PdfCopy extends PdfWriter
{
    protected HashMap indirects;
    protected HashMap indirectMap;
    protected int currentObjectNum;
    protected PdfReader reader;
    protected PdfIndirectReference acroForm;
    protected int[] namePtr;
    private boolean rotateContents;
    protected PdfArray fieldArray;
    protected HashMap fieldTemplates;
    
    public PdfCopy(final Document document, final OutputStream os) throws DocumentException {
        super(new PdfDocument(), os);
        this.currentObjectNum = 1;
        this.namePtr = new int[] { 0 };
        this.rotateContents = true;
        document.addDocListener(this.pdf);
        this.pdf.addWriter(this);
        this.indirectMap = new HashMap();
    }
    
    public boolean isRotateContents() {
        return this.rotateContents;
    }
    
    public void setRotateContents(final boolean rotateContents) {
        this.rotateContents = rotateContents;
    }
    
    @Override
    public PdfImportedPage getImportedPage(final PdfReader reader, final int pageNumber) {
        if (this.currentPdfReaderInstance != null) {
            if (this.currentPdfReaderInstance.getReader() != reader) {
                try {
                    this.currentPdfReaderInstance.getReader().close();
                    this.currentPdfReaderInstance.getReaderFile().close();
                }
                catch (final IOException ex) {}
                this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
            }
        }
        else {
            this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
        }
        return this.currentPdfReaderInstance.getImportedPage(pageNumber);
    }
    
    protected PdfIndirectReference copyIndirect(final PRIndirectReference in) throws IOException, BadPdfFormatException {
        final RefKey key = new RefKey(in);
        IndirectReferences iRef = this.indirects.get(key);
        PdfIndirectReference theRef;
        if (iRef != null) {
            theRef = iRef.getRef();
            if (iRef.getCopied()) {
                return theRef;
            }
        }
        else {
            theRef = this.body.getPdfIndirectReference();
            iRef = new IndirectReferences(theRef);
            this.indirects.put(key, iRef);
        }
        PdfObject obj = PdfReader.getPdfObjectRelease(in);
        if (obj != null && obj.isDictionary()) {
            final PdfObject type = PdfReader.getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.TYPE));
            if (type != null && PdfName.PAGE.equals(type)) {
                return theRef;
            }
        }
        iRef.setCopied();
        obj = this.copyObject(obj);
        this.addToBody(obj, theRef);
        return theRef;
    }
    
    protected PdfDictionary copyDictionary(final PdfDictionary in) throws IOException, BadPdfFormatException {
        final PdfDictionary out = new PdfDictionary();
        final PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
        for (final PdfName key : in.getKeys()) {
            final PdfObject value = in.get(key);
            if (type != null && PdfName.PAGE.equals(type)) {
                if (key.equals(PdfName.B) || key.equals(PdfName.PARENT)) {
                    continue;
                }
                out.put(key, this.copyObject(value));
            }
            else {
                out.put(key, this.copyObject(value));
            }
        }
        return out;
    }
    
    protected PdfStream copyStream(final PRStream in) throws IOException, BadPdfFormatException {
        final PRStream out = new PRStream(in, null);
        for (final PdfName key : in.getKeys()) {
            final PdfObject value = in.get(key);
            out.put(key, this.copyObject(value));
        }
        return out;
    }
    
    protected PdfArray copyArray(final PdfArray in) throws IOException, BadPdfFormatException {
        final PdfArray out = new PdfArray();
        final Iterator i = in.listIterator();
        while (i.hasNext()) {
            final PdfObject value = i.next();
            out.add(this.copyObject(value));
        }
        return out;
    }
    
    protected PdfObject copyObject(final PdfObject in) throws IOException, BadPdfFormatException {
        if (in == null) {
            return PdfNull.PDFNULL;
        }
        switch (in.type) {
            case 6: {
                return this.copyDictionary((PdfDictionary)in);
            }
            case 10: {
                return this.copyIndirect((PRIndirectReference)in);
            }
            case 5: {
                return this.copyArray((PdfArray)in);
            }
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 8: {
                return in;
            }
            case 7: {
                return this.copyStream((PRStream)in);
            }
            default: {
                if (in.type >= 0) {
                    System.out.println("CANNOT COPY type " + in.type);
                    return null;
                }
                final String lit = in.toString();
                if (lit.equals("true") || lit.equals("false")) {
                    return new PdfBoolean(lit);
                }
                return new PdfLiteral(lit);
            }
        }
    }
    
    protected int setFromIPage(final PdfImportedPage iPage) {
        final int pageNum = iPage.getPageNumber();
        final PdfReaderInstance pdfReaderInstance = iPage.getPdfReaderInstance();
        this.currentPdfReaderInstance = pdfReaderInstance;
        final PdfReaderInstance inst = pdfReaderInstance;
        this.setFromReader(this.reader = inst.getReader());
        return pageNum;
    }
    
    protected void setFromReader(final PdfReader reader) {
        this.reader = reader;
        this.indirects = this.indirectMap.get(reader);
        if (this.indirects == null) {
            this.indirects = new HashMap();
            this.indirectMap.put(reader, this.indirects);
            final PdfDictionary catalog = reader.getCatalog();
            PRIndirectReference ref = null;
            final PdfObject o = catalog.get(PdfName.ACROFORM);
            if (o == null || o.type() != 10) {
                return;
            }
            ref = (PRIndirectReference)o;
            if (this.acroForm == null) {
                this.acroForm = this.body.getPdfIndirectReference();
            }
            this.indirects.put(new RefKey(ref), new IndirectReferences(this.acroForm));
        }
    }
    
    public void addPage(final PdfImportedPage iPage) throws IOException, BadPdfFormatException {
        final int pageNum = this.setFromIPage(iPage);
        final PdfDictionary thePage = this.reader.getPageN(pageNum);
        final PRIndirectReference origRef = this.reader.getPageOrigRef(pageNum);
        this.reader.releasePage(pageNum);
        final RefKey key = new RefKey(origRef);
        IndirectReferences iRef = this.indirects.get(key);
        if (iRef != null && !iRef.getCopied()) {
            this.pageReferences.add(iRef.getRef());
            iRef.setCopied();
        }
        final PdfIndirectReference pageRef = this.getCurrentPage();
        if (iRef == null) {
            iRef = new IndirectReferences(pageRef);
            this.indirects.put(key, iRef);
        }
        iRef.setCopied();
        final PdfDictionary newPage = this.copyDictionary(thePage);
        this.root.addPage(newPage);
        ++this.currentPageNumber;
    }
    
    public void addPage(final Rectangle rect, final int rotation) {
        final PdfRectangle mediabox = new PdfRectangle(rect, rotation);
        final PageResources resources = new PageResources();
        final PdfPage page = new PdfPage(mediabox, new HashMap(), resources.getResources(), 0);
        page.put(PdfName.TABS, this.getTabs());
        this.root.addPage(page);
        ++this.currentPageNumber;
    }
    
    public void copyAcroForm(final PdfReader reader) throws IOException, BadPdfFormatException {
        this.setFromReader(reader);
        final PdfDictionary catalog = reader.getCatalog();
        PRIndirectReference hisRef = null;
        final PdfObject o = catalog.get(PdfName.ACROFORM);
        if (o != null && o.type() == 10) {
            hisRef = (PRIndirectReference)o;
        }
        if (hisRef == null) {
            return;
        }
        final RefKey key = new RefKey(hisRef);
        IndirectReferences iRef = this.indirects.get(key);
        PdfIndirectReference myRef;
        if (iRef != null) {
            myRef = (this.acroForm = iRef.getRef());
        }
        else {
            myRef = (this.acroForm = this.body.getPdfIndirectReference());
            iRef = new IndirectReferences(myRef);
            this.indirects.put(key, iRef);
        }
        if (!iRef.getCopied()) {
            iRef.setCopied();
            final PdfDictionary theForm = this.copyDictionary((PdfDictionary)PdfReader.getPdfObject(hisRef));
            this.addToBody(theForm, myRef);
        }
    }
    
    @Override
    protected PdfDictionary getCatalog(final PdfIndirectReference rootObj) {
        try {
            final PdfDictionary theCat = this.pdf.getCatalog(rootObj);
            if (this.fieldArray == null) {
                if (this.acroForm != null) {
                    theCat.put(PdfName.ACROFORM, this.acroForm);
                }
            }
            else {
                this.addFieldResources(theCat);
            }
            return theCat;
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private void addFieldResources(final PdfDictionary catalog) throws IOException {
        if (this.fieldArray == null) {
            return;
        }
        final PdfDictionary acroForm = new PdfDictionary();
        catalog.put(PdfName.ACROFORM, acroForm);
        acroForm.put(PdfName.FIELDS, this.fieldArray);
        acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        final PdfDictionary dr = new PdfDictionary();
        acroForm.put(PdfName.DR, dr);
        for (final PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources());
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
    }
    
    @Override
    public void close() {
        if (this.open) {
            final PdfReaderInstance ri = this.currentPdfReaderInstance;
            this.pdf.close();
            super.close();
            if (ri != null) {
                try {
                    ri.getReader().close();
                    ri.getReaderFile().close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public PdfIndirectReference add(final PdfOutline outline) {
        return null;
    }
    
    @Override
    public void addAnnotation(final PdfAnnotation annot) {
    }
    
    @Override
    PdfIndirectReference add(final PdfPage page, final PdfContents contents) throws PdfException {
        return null;
    }
    
    @Override
    public void freeReader(final PdfReader reader) throws IOException {
        this.indirectMap.remove(reader);
        if (this.currentPdfReaderInstance != null && this.currentPdfReaderInstance.getReader() == reader) {
            try {
                this.currentPdfReaderInstance.getReader().close();
                this.currentPdfReaderInstance.getReaderFile().close();
            }
            catch (final IOException ex) {}
            this.currentPdfReaderInstance = null;
        }
    }
    
    public PageStamp createPageStamp(final PdfImportedPage iPage) {
        final int pageNum = iPage.getPageNumber();
        final PdfReader reader = iPage.getPdfReaderInstance().getReader();
        final PdfDictionary pageN = reader.getPageN(pageNum);
        return new PageStamp(reader, pageN, this);
    }
    
    static class IndirectReferences
    {
        PdfIndirectReference theRef;
        boolean hasCopied;
        
        IndirectReferences(final PdfIndirectReference ref) {
            this.theRef = ref;
            this.hasCopied = false;
        }
        
        void setCopied() {
            this.hasCopied = true;
        }
        
        boolean getCopied() {
            return this.hasCopied;
        }
        
        PdfIndirectReference getRef() {
            return this.theRef;
        }
    }
    
    protected static class RefKey
    {
        int num;
        int gen;
        
        RefKey(final int num, final int gen) {
            this.num = num;
            this.gen = gen;
        }
        
        RefKey(final PdfIndirectReference ref) {
            this.num = ref.getNumber();
            this.gen = ref.getGeneration();
        }
        
        RefKey(final PRIndirectReference ref) {
            this.num = ref.getNumber();
            this.gen = ref.getGeneration();
        }
        
        @Override
        public int hashCode() {
            return (this.gen << 16) + this.num;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof RefKey)) {
                return false;
            }
            final RefKey other = (RefKey)o;
            return this.gen == other.gen && this.num == other.num;
        }
        
        @Override
        public String toString() {
            return Integer.toString(this.num) + ' ' + this.gen;
        }
    }
    
    public static class PageStamp
    {
        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        PdfReader reader;
        PdfCopy cstp;
        
        PageStamp(final PdfReader reader, final PdfDictionary pageN, final PdfCopy cstp) {
            this.pageN = pageN;
            this.reader = reader;
            this.cstp = cstp;
        }
        
        public PdfContentByte getUnderContent() {
            if (this.under == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    final PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.under = new StampContent(this.cstp, this.pageResources);
            }
            return this.under;
        }
        
        public PdfContentByte getOverContent() {
            if (this.over == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    final PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.over = new StampContent(this.cstp, this.pageResources);
            }
            return this.over;
        }
        
        public void alterContents() throws IOException {
            if (this.over == null && this.under == null) {
                return;
            }
            PdfArray ar = null;
            final PdfObject content = PdfReader.getPdfObject(this.pageN.get(PdfName.CONTENTS), this.pageN);
            if (content == null) {
                ar = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, ar);
            }
            else if (content.isArray()) {
                ar = (PdfArray)content;
            }
            else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(this.pageN.get(PdfName.CONTENTS));
                this.pageN.put(PdfName.CONTENTS, ar);
            }
            else {
                ar = new PdfArray();
                this.pageN.put(PdfName.CONTENTS, ar);
            }
            final ByteBuffer out = new ByteBuffer();
            if (this.under != null) {
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, out);
                out.append(this.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (this.over != null) {
                out.append(PdfContents.SAVESTATE);
            }
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(this.cstp.getCompressionLevel());
            final PdfIndirectReference ref1 = this.cstp.addToBody(stream).getIndirectReference();
            ar.addFirst(ref1);
            out.reset();
            if (this.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                out.append(PdfContents.SAVESTATE);
                this.applyRotation(this.pageN, out);
                out.append(this.over.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
                stream = new PdfStream(out.toByteArray());
                stream.flateCompress(this.cstp.getCompressionLevel());
                ar.add(this.cstp.addToBody(stream).getIndirectReference());
            }
            this.pageN.put(PdfName.RESOURCES, this.pageResources.getResources());
        }
        
        void applyRotation(final PdfDictionary pageN, final ByteBuffer out) {
            if (!this.cstp.rotateContents) {
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
        
        private void addDocumentField(final PdfIndirectReference ref) {
            if (this.cstp.fieldArray == null) {
                this.cstp.fieldArray = new PdfArray();
            }
            this.cstp.fieldArray.add(ref);
        }
        
        private void expandFields(final PdfFormField field, final ArrayList allAnnots) {
            allAnnots.add(field);
            final ArrayList kids = field.getKids();
            if (kids != null) {
                for (int k = 0; k < kids.size(); ++k) {
                    this.expandFields(kids.get(k), allAnnots);
                }
            }
        }
        
        public void addAnnotation(PdfAnnotation annot) {
            try {
                final ArrayList allAnnots = new ArrayList();
                if (annot.isForm()) {
                    final PdfFormField field = (PdfFormField)annot;
                    if (field.getParent() != null) {
                        return;
                    }
                    this.expandFields(field, allAnnots);
                    if (this.cstp.fieldTemplates == null) {
                        this.cstp.fieldTemplates = new HashMap();
                    }
                }
                else {
                    allAnnots.add(annot);
                }
                for (int k = 0; k < allAnnots.size(); ++k) {
                    annot = allAnnots.get(k);
                    if (annot.isForm()) {
                        if (!annot.isUsed()) {
                            final HashMap templates = annot.getTemplates();
                            if (templates != null) {
                                this.cstp.fieldTemplates.putAll(templates);
                            }
                        }
                        final PdfFormField field2 = (PdfFormField)annot;
                        if (field2.getParent() == null) {
                            this.addDocumentField(field2.getIndirectReference());
                        }
                    }
                    if (annot.isAnnotation()) {
                        final PdfObject pdfobj = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
                        PdfArray annots = null;
                        if (pdfobj == null || !pdfobj.isArray()) {
                            annots = new PdfArray();
                            this.pageN.put(PdfName.ANNOTS, annots);
                        }
                        else {
                            annots = (PdfArray)pdfobj;
                        }
                        annots.add(annot.getIndirectReference());
                        if (!annot.isUsed()) {
                            final PdfRectangle rect = (PdfRectangle)annot.get(PdfName.RECT);
                            if (rect != null && (rect.left() != 0.0f || rect.right() != 0.0f || rect.top() != 0.0f || rect.bottom() != 0.0f)) {
                                final int rotation = this.reader.getPageRotation(this.pageN);
                                final Rectangle pageSize = this.reader.getPageSizeWithRotation(this.pageN);
                                switch (rotation) {
                                    case 90: {
                                        annot.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
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
                        this.cstp.addToBody(annot, annot.getIndirectReference());
                    }
                }
            }
            catch (final IOException e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    public static class StampContent extends PdfContentByte
    {
        PageResources pageResources;
        
        StampContent(final PdfWriter writer, final PageResources pageResources) {
            super(writer);
            this.pageResources = pageResources;
        }
        
        @Override
        public PdfContentByte getDuplicate() {
            return new StampContent(this.writer, this.pageResources);
        }
        
        @Override
        PageResources getPageResources() {
            return this.pageResources;
        }
    }
}
