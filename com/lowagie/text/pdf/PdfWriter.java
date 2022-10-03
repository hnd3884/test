package com.lowagie.text.pdf;

import java.util.TreeSet;
import com.lowagie.text.Table;
import java.util.Arrays;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.ImgWMF;
import java.awt.Color;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.util.Collection;
import java.security.cert.Certificate;
import java.awt.color.ICC_Profile;
import com.lowagie.text.xml.xmp.XmpWriter;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.events.PdfPageEventForwarder;
import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.DocumentException;
import com.lowagie.text.DocListener;
import com.lowagie.text.Document;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import java.util.List;
import java.util.ArrayList;
import com.lowagie.text.pdf.interfaces.PdfAnnotations;
import com.lowagie.text.pdf.interfaces.PdfRunDirection;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import com.lowagie.text.pdf.interfaces.PdfPageActions;
import com.lowagie.text.pdf.interfaces.PdfDocumentActions;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.DocWriter;

public class PdfWriter extends DocWriter implements PdfViewerPreferences, PdfEncryptionSettings, PdfVersion, PdfDocumentActions, PdfPageActions, PdfXConformance, PdfRunDirection, PdfAnnotations
{
    public static final int GENERATION_MAX = 65535;
    protected PdfDocument pdf;
    protected PdfContentByte directContent;
    protected PdfContentByte directContentUnder;
    protected PdfBody body;
    protected PdfDictionary extraCatalog;
    protected PdfPages root;
    protected ArrayList<PdfIndirectReference> pageReferences;
    protected int currentPageNumber;
    protected PdfName tabs;
    private PdfPageEvent pageEvent;
    protected int prevxref;
    protected List newBookmarks;
    public static final char VERSION_1_2 = '2';
    public static final char VERSION_1_3 = '3';
    public static final char VERSION_1_4 = '4';
    public static final char VERSION_1_5 = '5';
    public static final char VERSION_1_6 = '6';
    public static final char VERSION_1_7 = '7';
    public static final PdfName PDF_VERSION_1_2;
    public static final PdfName PDF_VERSION_1_3;
    public static final PdfName PDF_VERSION_1_4;
    public static final PdfName PDF_VERSION_1_5;
    public static final PdfName PDF_VERSION_1_6;
    public static final PdfName PDF_VERSION_1_7;
    protected PdfVersionImp pdf_version;
    public static final int PageLayoutSinglePage = 1;
    public static final int PageLayoutOneColumn = 2;
    public static final int PageLayoutTwoColumnLeft = 4;
    public static final int PageLayoutTwoColumnRight = 8;
    public static final int PageLayoutTwoPageLeft = 16;
    public static final int PageLayoutTwoPageRight = 32;
    public static final int PageModeUseNone = 64;
    public static final int PageModeUseOutlines = 128;
    public static final int PageModeUseThumbs = 256;
    public static final int PageModeFullScreen = 512;
    public static final int PageModeUseOC = 1024;
    public static final int PageModeUseAttachments = 2048;
    public static final int HideToolbar = 4096;
    public static final int HideMenubar = 8192;
    public static final int HideWindowUI = 16384;
    public static final int FitWindow = 32768;
    public static final int CenterWindow = 65536;
    public static final int DisplayDocTitle = 131072;
    public static final int NonFullScreenPageModeUseNone = 262144;
    public static final int NonFullScreenPageModeUseOutlines = 524288;
    public static final int NonFullScreenPageModeUseThumbs = 1048576;
    public static final int NonFullScreenPageModeUseOC = 2097152;
    public static final int DirectionL2R = 4194304;
    public static final int DirectionR2L = 8388608;
    public static final int PrintScalingNone = 16777216;
    public static final PdfName DOCUMENT_CLOSE;
    public static final PdfName WILL_SAVE;
    public static final PdfName DID_SAVE;
    public static final PdfName WILL_PRINT;
    public static final PdfName DID_PRINT;
    public static final int SIGNATURE_EXISTS = 1;
    public static final int SIGNATURE_APPEND_ONLY = 2;
    protected byte[] xmpMetadata;
    public static final int PDFXNONE = 0;
    public static final int PDFX1A2001 = 1;
    public static final int PDFX32002 = 2;
    public static final int PDFA1A = 3;
    public static final int PDFA1B = 4;
    private PdfXConformanceImp pdfxConformance;
    public static final int STANDARD_ENCRYPTION_40 = 0;
    public static final int STANDARD_ENCRYPTION_128 = 1;
    public static final int ENCRYPTION_AES_128 = 2;
    static final int ENCRYPTION_MASK = 7;
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    public static final int EMBEDDED_FILES_ONLY = 24;
    public static final int ALLOW_PRINTING = 2052;
    public static final int ALLOW_MODIFY_CONTENTS = 8;
    public static final int ALLOW_COPY = 16;
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
    public static final int ALLOW_FILL_IN = 256;
    public static final int ALLOW_SCREENREADERS = 512;
    public static final int ALLOW_ASSEMBLY = 1024;
    public static final int ALLOW_DEGRADED_PRINTING = 4;
    @Deprecated
    public static final int AllowPrinting = 2052;
    @Deprecated
    public static final int AllowModifyContents = 8;
    @Deprecated
    public static final int AllowCopy = 16;
    @Deprecated
    public static final int AllowModifyAnnotations = 32;
    @Deprecated
    public static final int AllowFillIn = 256;
    @Deprecated
    public static final int AllowScreenReaders = 512;
    @Deprecated
    public static final int AllowAssembly = 1024;
    @Deprecated
    public static final int AllowDegradedPrinting = 4;
    @Deprecated
    public static final boolean STRENGTH40BITS = false;
    @Deprecated
    public static final boolean STRENGTH128BITS = true;
    protected PdfEncryption crypto;
    protected boolean fullCompression;
    protected int compressionLevel;
    protected LinkedHashMap<BaseFont, FontDetails> documentFonts;
    protected int fontNumber;
    protected LinkedHashMap<PdfIndirectReference, Object[]> formXObjects;
    protected int formXObjectsCounter;
    protected HashMap<PdfReader, PdfReaderInstance> importedPages;
    protected PdfReaderInstance currentPdfReaderInstance;
    protected HashMap<PdfSpotColor, ColorDetails> documentColors;
    protected int colorNumber;
    protected HashMap<PdfPatternPainter, PdfName> documentPatterns;
    protected int patternNumber;
    protected HashMap<PdfShadingPattern, Object> documentShadingPatterns;
    protected HashMap<PdfShading, Object> documentShadings;
    protected HashMap<PdfDictionary, PdfObject[]> documentExtGState;
    protected HashMap<Object, PdfObject[]> documentProperties;
    protected boolean tagged;
    protected PdfStructureTreeRoot structureTreeRoot;
    protected HashSet documentOCG;
    protected ArrayList documentOCGorder;
    protected PdfOCProperties OCProperties;
    protected PdfArray OCGRadioGroup;
    protected PdfArray OCGLocked;
    public static final PdfName PAGE_OPEN;
    public static final PdfName PAGE_CLOSE;
    protected PdfDictionary group;
    public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
    public static final float NO_SPACE_CHAR_RATIO = 1.0E7f;
    private float spaceCharRatio;
    public static final int RUN_DIRECTION_DEFAULT = 0;
    public static final int RUN_DIRECTION_NO_BIDI = 1;
    public static final int RUN_DIRECTION_LTR = 2;
    public static final int RUN_DIRECTION_RTL = 3;
    protected int runDirection;
    protected float userunit;
    protected PdfDictionary defaultColorspace;
    protected HashMap<ColorDetails, ColorDetails> documentSpotPatterns;
    protected ColorDetails patternColorspaceRGB;
    protected ColorDetails patternColorspaceGRAY;
    protected ColorDetails patternColorspaceCMYK;
    protected PdfDictionary imageDictionary;
    private HashMap<Long, PdfName> images;
    protected HashMap<PdfStream, PdfIndirectReference> JBIG2Globals;
    private boolean userProperties;
    private boolean rgbTransparencyBlending;
    
    protected PdfWriter() {
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList<PdfIndirectReference>();
        this.currentPageNumber = 1;
        this.tabs = null;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new LinkedHashMap<BaseFont, FontDetails>();
        this.fontNumber = 1;
        this.formXObjects = new LinkedHashMap<PdfIndirectReference, Object[]>();
        this.formXObjectsCounter = 1;
        this.importedPages = new HashMap<PdfReader, PdfReaderInstance>();
        this.documentColors = new HashMap<PdfSpotColor, ColorDetails>();
        this.colorNumber = 1;
        this.documentPatterns = new HashMap<PdfPatternPainter, PdfName>();
        this.patternNumber = 1;
        this.documentShadingPatterns = new HashMap<PdfShadingPattern, Object>();
        this.documentShadings = new HashMap<PdfShading, Object>();
        this.documentExtGState = new HashMap<PdfDictionary, PdfObject[]>();
        this.documentProperties = new HashMap<Object, PdfObject[]>();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = 2.5f;
        this.runDirection = 1;
        this.userunit = 0.0f;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new HashMap<ColorDetails, ColorDetails>();
        this.imageDictionary = new PdfDictionary();
        this.images = new HashMap<Long, PdfName>();
        this.JBIG2Globals = new HashMap<PdfStream, PdfIndirectReference>();
    }
    
    protected PdfWriter(final PdfDocument document, final OutputStream os) {
        super(document, os);
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList<PdfIndirectReference>();
        this.currentPageNumber = 1;
        this.tabs = null;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new LinkedHashMap<BaseFont, FontDetails>();
        this.fontNumber = 1;
        this.formXObjects = new LinkedHashMap<PdfIndirectReference, Object[]>();
        this.formXObjectsCounter = 1;
        this.importedPages = new HashMap<PdfReader, PdfReaderInstance>();
        this.documentColors = new HashMap<PdfSpotColor, ColorDetails>();
        this.colorNumber = 1;
        this.documentPatterns = new HashMap<PdfPatternPainter, PdfName>();
        this.patternNumber = 1;
        this.documentShadingPatterns = new HashMap<PdfShadingPattern, Object>();
        this.documentShadings = new HashMap<PdfShading, Object>();
        this.documentExtGState = new HashMap<PdfDictionary, PdfObject[]>();
        this.documentProperties = new HashMap<Object, PdfObject[]>();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = 2.5f;
        this.runDirection = 1;
        this.userunit = 0.0f;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new HashMap<ColorDetails, ColorDetails>();
        this.imageDictionary = new PdfDictionary();
        this.images = new HashMap<Long, PdfName>();
        this.JBIG2Globals = new HashMap<PdfStream, PdfIndirectReference>();
        this.pdf = document;
        this.directContent = new PdfContentByte(this);
        this.directContentUnder = new PdfContentByte(this);
    }
    
    public static PdfWriter getInstance(final Document document, final OutputStream os) throws DocumentException {
        final PdfDocument pdf = new PdfDocument();
        document.addDocListener(pdf);
        final PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }
    
    public static PdfWriter getInstance(final Document document, final OutputStream os, final DocListener listener) throws DocumentException {
        final PdfDocument pdf = new PdfDocument();
        pdf.addDocListener(listener);
        document.addDocListener(pdf);
        final PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }
    
    PdfDocument getPdfDocument() {
        return this.pdf;
    }
    
    public PdfDictionary getInfo() {
        return this.pdf.getInfo();
    }
    
    public float getVerticalPosition(final boolean ensureNewLine) {
        return this.pdf.getVerticalPosition(ensureNewLine);
    }
    
    public void setInitialLeading(final float leading) throws DocumentException {
        if (this.open) {
            throw new DocumentException(MessageLocalization.getComposedMessage("you.can.t.set.the.initial.leading.if.the.document.is.already.open"));
        }
        this.pdf.setLeading(leading);
    }
    
    public PdfContentByte getDirectContent() {
        if (!this.open) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        return this.directContent;
    }
    
    public PdfContentByte getDirectContentUnder() {
        if (!this.open) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        return this.directContentUnder;
    }
    
    void resetContent() {
        this.directContent.reset();
        this.directContentUnder.reset();
    }
    
    void addLocalDestinations(final TreeMap dest) throws IOException {
        for (final Map.Entry entry : dest.entrySet()) {
            final String name = entry.getKey();
            final Object[] obj = entry.getValue();
            final PdfDestination destination = (PdfDestination)obj[2];
            if (obj[1] == null) {
                obj[1] = this.getPdfIndirectReference();
            }
            if (destination == null) {
                this.addToBody(new PdfString("invalid_" + name), (PdfIndirectReference)obj[1]);
            }
            else {
                this.addToBody(destination, (PdfIndirectReference)obj[1]);
            }
        }
    }
    
    public PdfIndirectObject addToBody(final PdfObject object) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object);
        return iobj;
    }
    
    public PdfIndirectObject addToBody(final PdfObject object, final boolean inObjStm) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object, inObjStm);
        return iobj;
    }
    
    public PdfIndirectObject addToBody(final PdfObject object, final PdfIndirectReference ref) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object, ref);
        return iobj;
    }
    
    public PdfIndirectObject addToBody(final PdfObject object, final PdfIndirectReference ref, final boolean inObjStm) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object, ref, inObjStm);
        return iobj;
    }
    
    public PdfIndirectObject addToBody(final PdfObject object, final int refNumber) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object, refNumber);
        return iobj;
    }
    
    public PdfIndirectObject addToBody(final PdfObject object, final int refNumber, final boolean inObjStm) throws IOException {
        final PdfIndirectObject iobj = this.body.add(object, refNumber, inObjStm);
        return iobj;
    }
    
    public PdfIndirectReference getPdfIndirectReference() {
        return this.body.getPdfIndirectReference();
    }
    
    int getIndirectReferenceNumber() {
        return this.body.getIndirectReferenceNumber();
    }
    
    OutputStreamCounter getOs() {
        return this.os;
    }
    
    protected PdfDictionary getCatalog(final PdfIndirectReference rootObj) {
        final PdfDictionary catalog = this.pdf.getCatalog(rootObj);
        if (this.tagged) {
            try {
                this.getStructureTreeRoot().buildTree();
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
            catalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
            final PdfDictionary mi = new PdfDictionary();
            mi.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
            if (this.userProperties) {
                mi.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE);
            }
            catalog.put(PdfName.MARKINFO, mi);
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            catalog.put(PdfName.OCPROPERTIES, this.OCProperties);
        }
        return catalog;
    }
    
    public PdfDictionary getExtraCatalog() {
        if (this.extraCatalog == null) {
            this.extraCatalog = new PdfDictionary();
        }
        return this.extraCatalog;
    }
    
    public void setLinearPageMode() {
        this.root.setLinearMode(null);
    }
    
    public int reorderPages(final int[] order) throws DocumentException {
        return this.root.reorderPages(order);
    }
    
    public PdfIndirectReference getPageReference(int page) {
        if (--page < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.page.number.must.be.gt.eq.1"));
        }
        PdfIndirectReference ref;
        if (page < this.pageReferences.size()) {
            ref = this.pageReferences.get(page);
            if (ref == null) {
                ref = this.body.getPdfIndirectReference();
                this.pageReferences.set(page, ref);
            }
        }
        else {
            for (int empty = page - this.pageReferences.size(), k = 0; k < empty; ++k) {
                this.pageReferences.add(null);
            }
            ref = this.body.getPdfIndirectReference();
            this.pageReferences.add(ref);
        }
        return ref;
    }
    
    public int getPageNumber() {
        return this.pdf.getPageNumber();
    }
    
    PdfIndirectReference getCurrentPage() {
        return this.getPageReference(this.currentPageNumber);
    }
    
    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }
    
    public void setTabs(final PdfName tabs) {
        this.tabs = tabs;
    }
    
    public PdfName getTabs() {
        return this.tabs;
    }
    
    PdfIndirectReference add(final PdfPage page, final PdfContents contents) throws PdfException {
        if (!this.open) {
            throw new PdfException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        PdfIndirectObject object;
        try {
            object = this.addToBody(contents);
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        page.add(object.getIndirectReference());
        if (this.group != null) {
            page.put(PdfName.GROUP, this.group);
            this.group = null;
        }
        else if (this.rgbTransparencyBlending) {
            final PdfDictionary pp = new PdfDictionary();
            pp.put(PdfName.TYPE, PdfName.GROUP);
            pp.put(PdfName.S, PdfName.TRANSPARENCY);
            pp.put(PdfName.CS, PdfName.DEVICERGB);
            page.put(PdfName.GROUP, pp);
        }
        this.root.addPage(page);
        ++this.currentPageNumber;
        return null;
    }
    
    public void setPageEvent(final PdfPageEvent event) {
        if (event == null) {
            this.pageEvent = null;
        }
        else if (this.pageEvent == null) {
            this.pageEvent = event;
        }
        else if (this.pageEvent instanceof PdfPageEventForwarder) {
            ((PdfPageEventForwarder)this.pageEvent).addPageEvent(event);
        }
        else {
            final PdfPageEventForwarder forward = new PdfPageEventForwarder();
            forward.addPageEvent(this.pageEvent);
            forward.addPageEvent(event);
            this.pageEvent = forward;
        }
    }
    
    public PdfPageEvent getPageEvent() {
        return this.pageEvent;
    }
    
    @Override
    public void open() {
        super.open();
        try {
            this.pdf_version.writeHeader(this.os);
            this.body = new PdfBody(this);
            if (this.pdfxConformance.isPdfX32002()) {
                final PdfDictionary sec = new PdfDictionary();
                sec.put(PdfName.GAMMA, new PdfArray(new float[] { 2.2f, 2.2f, 2.2f }));
                sec.put(PdfName.MATRIX, new PdfArray(new float[] { 0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f }));
                sec.put(PdfName.WHITEPOINT, new PdfArray(new float[] { 0.9505f, 1.0f, 1.089f }));
                final PdfArray arr = new PdfArray(PdfName.CALRGB);
                arr.add(sec);
                this.setDefaultColorspace(PdfName.DEFAULTRGB, this.addToBody(arr).getIndirectReference());
            }
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    @Override
    public void close() {
        if (this.open) {
            if (this.currentPageNumber - 1 != this.pageReferences.size()) {
                throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages.");
            }
            this.pdf.close();
            try {
                this.addSharedObjectsToBody();
                final PdfIndirectReference rootRef = this.root.writePageTree();
                final PdfDictionary catalog = this.getCatalog(rootRef);
                if (this.xmpMetadata != null) {
                    final PdfStream xmp = new PdfStream(this.xmpMetadata);
                    xmp.put(PdfName.TYPE, PdfName.METADATA);
                    xmp.put(PdfName.SUBTYPE, PdfName.XML);
                    if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                        final PdfArray ar = new PdfArray();
                        ar.add(PdfName.CRYPT);
                        xmp.put(PdfName.FILTER, ar);
                    }
                    catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                }
                if (this.isPdfX()) {
                    this.pdfxConformance.completeInfoDictionary(this.getInfo());
                    this.pdfxConformance.completeExtraCatalog(this.getExtraCatalog());
                }
                if (this.extraCatalog != null) {
                    catalog.mergeDifferent(this.extraCatalog);
                }
                this.writeOutlines(catalog, false);
                final PdfIndirectObject indirectCatalog = this.addToBody(catalog, false);
                final PdfIndirectObject infoObj = this.addToBody(this.getInfo(), false);
                PdfIndirectReference encryption = null;
                PdfObject fileID = null;
                this.body.flushObjStm();
                if (this.crypto != null) {
                    final PdfIndirectObject encryptionObject = this.addToBody(this.crypto.getEncryptionDictionary(), false);
                    encryption = encryptionObject.getIndirectReference();
                    fileID = this.crypto.getFileID();
                }
                else {
                    fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
                }
                this.body.writeCrossReferenceTable(this.os, indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
                if (this.fullCompression) {
                    this.os.write(DocWriter.getISOBytes("startxref\n"));
                    this.os.write(DocWriter.getISOBytes(String.valueOf(this.body.offset())));
                    this.os.write(DocWriter.getISOBytes("\n%%EOF\n"));
                }
                else {
                    final PdfTrailer trailer = new PdfTrailer(this.body.size(), this.body.offset(), indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
                    trailer.toPdf(this, this.os);
                }
                super.close();
            }
            catch (final IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
    }
    
    protected void addSharedObjectsToBody() throws IOException {
        for (final FontDetails details : this.documentFonts.values()) {
            details.writeFont(this);
        }
        for (final Object[] objs : this.formXObjects.values()) {
            final PdfTemplate template = (PdfTemplate)objs[1];
            if (template != null && template.getIndirectReference() instanceof PRIndirectReference) {
                continue;
            }
            if (template == null || template.getType() != 1) {
                continue;
            }
            this.addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference());
        }
        final Iterator<PdfReaderInstance> it3 = this.importedPages.values().iterator();
        while (it3.hasNext()) {
            (this.currentPdfReaderInstance = it3.next()).writeAllPages();
        }
        this.currentPdfReaderInstance = null;
        for (final ColorDetails color : this.documentColors.values()) {
            this.addToBody(color.getSpotColor(this), color.getIndirectReference());
        }
        for (final PdfPatternPainter pat : this.documentPatterns.keySet()) {
            this.addToBody(pat.getPattern(this.compressionLevel), pat.getIndirectReference());
        }
        for (final PdfShadingPattern shadingPattern : this.documentShadingPatterns.keySet()) {
            shadingPattern.addToBody();
        }
        for (final PdfShading shading : this.documentShadings.keySet()) {
            shading.addToBody();
        }
        for (final Map.Entry<PdfDictionary, PdfObject[]> entry : this.documentExtGState.entrySet()) {
            final PdfDictionary gstate = entry.getKey();
            final PdfObject[] obj = entry.getValue();
            this.addToBody(gstate, (PdfIndirectReference)obj[1]);
        }
        for (final Map.Entry<Object, PdfObject[]> entry2 : this.documentProperties.entrySet()) {
            final Object prop = entry2.getKey();
            final PdfObject[] obj = entry2.getValue();
            if (prop instanceof PdfLayerMembership) {
                final PdfLayerMembership layer = (PdfLayerMembership)prop;
                this.addToBody(layer.getPdfObject(), layer.getRef());
            }
            else {
                if (!(prop instanceof PdfDictionary) || prop instanceof PdfLayer) {
                    continue;
                }
                this.addToBody((PdfObject)prop, (PdfIndirectReference)obj[1]);
            }
        }
        for (final PdfOCG layer2 : this.documentOCG) {
            this.addToBody(layer2.getPdfObject(), layer2.getRef());
        }
    }
    
    public PdfOutline getRootOutline() {
        return this.directContent.getRootOutline();
    }
    
    public void setOutlines(final List outlines) {
        this.newBookmarks = outlines;
    }
    
    protected void writeOutlines(final PdfDictionary catalog, final boolean namedAsNames) throws IOException {
        if (this.newBookmarks == null || this.newBookmarks.isEmpty()) {
            return;
        }
        final PdfDictionary top = new PdfDictionary();
        final PdfIndirectReference topRef = this.getPdfIndirectReference();
        final Object[] kids = SimpleBookmark.iterateOutlines(this, topRef, this.newBookmarks, namedAsNames);
        top.put(PdfName.FIRST, (PdfObject)kids[0]);
        top.put(PdfName.LAST, (PdfObject)kids[1]);
        top.put(PdfName.COUNT, new PdfNumber((int)kids[2]));
        this.addToBody(top, topRef);
        catalog.put(PdfName.OUTLINES, topRef);
    }
    
    @Override
    public void setPdfVersion(final char version) {
        this.pdf_version.setPdfVersion(version);
    }
    
    @Override
    public void setAtLeastPdfVersion(final char version) {
        this.pdf_version.setAtLeastPdfVersion(version);
    }
    
    @Override
    public void setPdfVersion(final PdfName version) {
        this.pdf_version.setPdfVersion(version);
    }
    
    @Override
    public void addDeveloperExtension(final PdfDeveloperExtension de) {
        this.pdf_version.addDeveloperExtension(de);
    }
    
    PdfVersionImp getPdfVersion() {
        return this.pdf_version;
    }
    
    @Override
    public void setViewerPreferences(final int preferences) {
        this.pdf.setViewerPreferences(preferences);
    }
    
    @Override
    public void addViewerPreference(final PdfName key, final PdfObject value) {
        this.pdf.addViewerPreference(key, value);
    }
    
    public void setPageLabels(final PdfPageLabels pageLabels) {
        this.pdf.setPageLabels(pageLabels);
    }
    
    public void addNamedDestinations(final Map<String, String> map, final int page_offset) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            final String dest = entry.getValue();
            final int page = Integer.parseInt(dest.substring(0, dest.indexOf(" ")));
            final PdfDestination destination = new PdfDestination(dest.substring(dest.indexOf(" ") + 1));
            this.addNamedDestination(entry.getKey(), page + page_offset, destination);
        }
    }
    
    public void addNamedDestination(final String name, final int page, final PdfDestination dest) {
        dest.addPage(this.getPageReference(page));
        this.pdf.localDestination(name, dest);
    }
    
    public void addJavaScript(final PdfAction js) {
        this.pdf.addJavaScript(js);
    }
    
    public void addJavaScript(final String code, final boolean unicode) {
        this.addJavaScript(PdfAction.javaScript(code, this, unicode));
    }
    
    public void addJavaScript(final String code) {
        this.addJavaScript(code, false);
    }
    
    public void addJavaScript(final String name, final PdfAction js) {
        this.pdf.addJavaScript(name, js);
    }
    
    public void addJavaScript(final String name, final String code, final boolean unicode) {
        this.addJavaScript(name, PdfAction.javaScript(code, this, unicode));
    }
    
    public void addJavaScript(final String name, final String code) {
        this.addJavaScript(name, code, false);
    }
    
    public void addFileAttachment(final String description, final byte[] fileStore, final String file, final String fileDisplay) throws IOException {
        this.addFileAttachment(description, PdfFileSpecification.fileEmbedded(this, file, fileDisplay, fileStore));
    }
    
    public void addFileAttachment(final String description, final PdfFileSpecification fs) throws IOException {
        this.pdf.addFileAttachment(description, fs);
    }
    
    public void addFileAttachment(final PdfFileSpecification fs) throws IOException {
        this.addFileAttachment(null, fs);
    }
    
    @Override
    public void setOpenAction(final String name) {
        this.pdf.setOpenAction(name);
    }
    
    @Override
    public void setOpenAction(final PdfAction action) {
        this.pdf.setOpenAction(action);
    }
    
    @Override
    public void setAdditionalAction(final PdfName actionType, final PdfAction action) throws DocumentException {
        if (!actionType.equals(PdfWriter.DOCUMENT_CLOSE) && !actionType.equals(PdfWriter.WILL_SAVE) && !actionType.equals(PdfWriter.DID_SAVE) && !actionType.equals(PdfWriter.WILL_PRINT) && !actionType.equals(PdfWriter.DID_PRINT)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", actionType.toString()));
        }
        this.pdf.addAdditionalAction(actionType, action);
    }
    
    public void setCollection(final PdfCollection collection) {
        this.setAtLeastPdfVersion('7');
        this.pdf.setCollection(collection);
    }
    
    @Override
    public PdfAcroForm getAcroForm() {
        return this.pdf.getAcroForm();
    }
    
    @Override
    public void addAnnotation(final PdfAnnotation annot) {
        this.pdf.addAnnotation(annot);
    }
    
    void addAnnotation(final PdfAnnotation annot, final int page) {
        this.addAnnotation(annot);
    }
    
    @Override
    public void addCalculationOrder(final PdfFormField annot) {
        this.pdf.addCalculationOrder(annot);
    }
    
    @Override
    public void setSigFlags(final int f) {
        this.pdf.setSigFlags(f);
    }
    
    public void setXmpMetadata(final byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }
    
    public void setPageXmpMetadata(final byte[] xmpMetadata) {
        this.pdf.setXmpMetadata(xmpMetadata);
    }
    
    public void createXmpMetadata() {
        this.setXmpMetadata(this.createXmpMetadataBytes());
    }
    
    private byte[] createXmpMetadataBytes() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final XmpWriter xmp = new XmpWriter(baos, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
            xmp.close();
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    @Override
    public void setPDFXConformance(final int pdfx) {
        if (this.pdfxConformance.getPDFXConformance() == pdfx) {
            return;
        }
        if (this.pdf.isOpen()) {
            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("pdfx.conformance.can.only.be.set.before.opening.the.document"));
        }
        if (this.crypto != null) {
            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("a.pdfx.conforming.document.cannot.be.encrypted"));
        }
        if (pdfx == 3 || pdfx == 4) {
            this.setPdfVersion('4');
        }
        else if (pdfx != 0) {
            this.setPdfVersion('3');
        }
        this.pdfxConformance.setPDFXConformance(pdfx);
    }
    
    @Override
    public int getPDFXConformance() {
        return this.pdfxConformance.getPDFXConformance();
    }
    
    @Override
    public boolean isPdfX() {
        return this.pdfxConformance.isPdfX();
    }
    
    public void setOutputIntents(final String outputConditionIdentifier, final String outputCondition, final String registryName, final String info, final ICC_Profile colorProfile) throws IOException {
        this.getExtraCatalog();
        final PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
        if (outputCondition != null) {
            out.put(PdfName.OUTPUTCONDITION, new PdfString(outputCondition, "UnicodeBig"));
        }
        if (outputConditionIdentifier != null) {
            out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(outputConditionIdentifier, "UnicodeBig"));
        }
        if (registryName != null) {
            out.put(PdfName.REGISTRYNAME, new PdfString(registryName, "UnicodeBig"));
        }
        if (info != null) {
            out.put(PdfName.INFO, new PdfString(info, "UnicodeBig"));
        }
        if (colorProfile != null) {
            final PdfStream stream = new PdfICCBased(colorProfile, this.compressionLevel);
            out.put(PdfName.DESTOUTPUTPROFILE, this.addToBody(stream).getIndirectReference());
        }
        PdfName intentSubtype;
        if (this.pdfxConformance.isPdfA1() || "PDFA/1".equals(outputCondition)) {
            intentSubtype = PdfName.GTS_PDFA1;
        }
        else {
            intentSubtype = PdfName.GTS_PDFX;
        }
        out.put(PdfName.S, intentSubtype);
        this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
    }
    
    public void setOutputIntents(final String outputConditionIdentifier, final String outputCondition, final String registryName, final String info, final byte[] destOutputProfile) throws IOException {
        final ICC_Profile colorProfile = (destOutputProfile == null) ? null : ICC_Profile.getInstance(destOutputProfile);
        this.setOutputIntents(outputConditionIdentifier, outputCondition, registryName, info, colorProfile);
    }
    
    public boolean setOutputIntents(final PdfReader reader, final boolean checkExistence) throws IOException {
        final PdfDictionary catalog = reader.getCatalog();
        final PdfArray outs = catalog.getAsArray(PdfName.OUTPUTINTENTS);
        if (outs == null) {
            return false;
        }
        if (outs.isEmpty()) {
            return false;
        }
        final PdfDictionary out = outs.getAsDict(0);
        final PdfObject obj = PdfReader.getPdfObject(out.get(PdfName.S));
        if (obj == null || !PdfName.GTS_PDFX.equals(obj)) {
            return false;
        }
        if (checkExistence) {
            return true;
        }
        final PRStream stream = (PRStream)PdfReader.getPdfObject(out.get(PdfName.DESTOUTPUTPROFILE));
        byte[] destProfile = null;
        if (stream != null) {
            destProfile = PdfReader.getStreamBytes(stream);
        }
        this.setOutputIntents(getNameString(out, PdfName.OUTPUTCONDITIONIDENTIFIER), getNameString(out, PdfName.OUTPUTCONDITION), getNameString(out, PdfName.REGISTRYNAME), getNameString(out, PdfName.INFO), destProfile);
        return true;
    }
    
    private static String getNameString(final PdfDictionary dic, final PdfName key) {
        final PdfObject obj = PdfReader.getPdfObject(dic.get(key));
        if (obj == null || !obj.isString()) {
            return null;
        }
        return ((PdfString)obj).toUnicodeString();
    }
    
    PdfEncryption getEncryption() {
        return this.crypto;
    }
    
    @Override
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document"));
        }
        (this.crypto = new PdfEncryption()).setCryptoMode(encryptionType, 0);
        this.crypto.setupAllKeys(userPassword, ownerPassword, permissions);
    }
    
    @Override
    public void setEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document"));
        }
        this.crypto = new PdfEncryption();
        if (certs != null) {
            for (int i = 0; i < certs.length; ++i) {
                this.crypto.addRecipient(certs[i], permissions[i]);
            }
        }
        this.crypto.setCryptoMode(encryptionType, 0);
        this.crypto.getEncryptionDictionary();
    }
    
    @Deprecated
    public void setEncryption(final byte[] userPassword, final byte[] ownerPassword, final int permissions, final boolean strength128Bits) throws DocumentException {
        this.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
    }
    
    @Deprecated
    public void setEncryption(final boolean strength, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength ? 1 : 0);
    }
    
    @Deprecated
    public void setEncryption(final int encryptionType, final String userPassword, final String ownerPassword, final int permissions) throws DocumentException {
        this.setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, encryptionType);
    }
    
    public boolean isFullCompression() {
        return this.fullCompression;
    }
    
    public void setFullCompression() {
        this.fullCompression = true;
        this.setAtLeastPdfVersion('5');
    }
    
    public int getCompressionLevel() {
        return this.compressionLevel;
    }
    
    public void setCompressionLevel(final int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        }
        else {
            this.compressionLevel = compressionLevel;
        }
    }
    
    FontDetails addSimple(final BaseFont bf) {
        if (bf.getFontType() == 4) {
            return new FontDetails(new PdfName("F" + this.fontNumber++), ((DocumentFont)bf).getIndirectReference(), bf);
        }
        FontDetails ret = this.documentFonts.get(bf);
        if (ret == null) {
            PdfXConformanceImp.checkPDFXConformance(this, 4, bf);
            ret = new FontDetails(new PdfName("F" + this.fontNumber++), this.body.getPdfIndirectReference(), bf);
            this.documentFonts.put(bf, ret);
        }
        return ret;
    }
    
    void eliminateFontSubset(final PdfDictionary fonts) {
        for (final FontDetails ft : this.documentFonts.values()) {
            if (fonts.get(ft.getFontName()) != null) {
                ft.setSubset(false);
            }
        }
    }
    
    PdfName addDirectTemplateSimple(PdfTemplate template, final PdfName forcedName) {
        final PdfIndirectReference ref = template.getIndirectReference();
        final Object[] obj = this.formXObjects.get(ref);
        PdfName name = null;
        try {
            if (obj == null) {
                if (forcedName == null) {
                    name = new PdfName("Xf" + this.formXObjectsCounter);
                    ++this.formXObjectsCounter;
                }
                else {
                    name = forcedName;
                }
                if (template.getType() == 2) {
                    final PdfImportedPage ip = (PdfImportedPage)template;
                    final PdfReader r = ip.getPdfReaderInstance().getReader();
                    if (!this.importedPages.containsKey(r)) {
                        this.importedPages.put(r, ip.getPdfReaderInstance());
                    }
                    template = null;
                }
                this.formXObjects.put(ref, new Object[] { name, template });
            }
            else {
                name = (PdfName)obj[0];
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }
    
    public void releaseTemplate(final PdfTemplate tp) throws IOException {
        final PdfIndirectReference ref = tp.getIndirectReference();
        final Object[] objs = this.formXObjects.get(ref);
        if (objs == null || objs[1] == null) {
            return;
        }
        final PdfTemplate template = (PdfTemplate)objs[1];
        if (template.getIndirectReference() instanceof PRIndirectReference) {
            return;
        }
        if (template.getType() == 1) {
            this.addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference());
            objs[1] = null;
        }
    }
    
    public PdfImportedPage getImportedPage(final PdfReader reader, final int pageNumber) {
        PdfReaderInstance inst = this.importedPages.get(reader);
        if (inst == null) {
            inst = reader.getPdfReaderInstance(this);
            this.importedPages.put(reader, inst);
        }
        return inst.getImportedPage(pageNumber);
    }
    
    public void freeReader(final PdfReader reader) throws IOException {
        this.currentPdfReaderInstance = this.importedPages.get(reader);
        if (this.currentPdfReaderInstance == null) {
            return;
        }
        this.currentPdfReaderInstance.writeAllPages();
        this.currentPdfReaderInstance = null;
        this.importedPages.remove(reader);
    }
    
    public int getCurrentDocumentSize() {
        return this.body.offset() + this.body.size() * 20 + 72;
    }
    
    protected int getNewObjectNumber(final PdfReader reader, final int number, final int generation) {
        return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
    }
    
    RandomAccessFileOrArray getReaderFile(final PdfReader reader) {
        return this.currentPdfReaderInstance.getReaderFile();
    }
    
    PdfName getColorspaceName() {
        return new PdfName("CS" + this.colorNumber++);
    }
    
    ColorDetails addSimple(final PdfSpotColor spc) {
        ColorDetails ret = this.documentColors.get(spc);
        if (ret == null) {
            ret = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), spc);
            this.documentColors.put(spc, ret);
        }
        return ret;
    }
    
    PdfName addSimplePattern(final PdfPatternPainter painter) {
        PdfName name = this.documentPatterns.get(painter);
        try {
            if (name == null) {
                name = new PdfName("P" + this.patternNumber);
                ++this.patternNumber;
                this.documentPatterns.put(painter, name);
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }
    
    void addSimpleShadingPattern(final PdfShadingPattern shading) {
        if (!this.documentShadingPatterns.containsKey(shading)) {
            shading.setName(this.patternNumber);
            ++this.patternNumber;
            this.documentShadingPatterns.put(shading, null);
            this.addSimpleShading(shading.getShading());
        }
    }
    
    void addSimpleShading(final PdfShading shading) {
        if (!this.documentShadings.containsKey(shading)) {
            this.documentShadings.put(shading, null);
            shading.setName(this.documentShadings.size());
        }
    }
    
    PdfObject[] addSimpleExtGState(final PdfDictionary gstate) {
        if (!this.documentExtGState.containsKey(gstate)) {
            PdfXConformanceImp.checkPDFXConformance(this, 6, gstate);
            this.documentExtGState.put(gstate, new PdfObject[] { new PdfName("GS" + (this.documentExtGState.size() + 1)), this.getPdfIndirectReference() });
        }
        return this.documentExtGState.get(gstate);
    }
    
    PdfObject[] addSimpleProperty(final Object prop, final PdfIndirectReference refi) {
        if (!this.documentProperties.containsKey(prop)) {
            if (prop instanceof PdfOCG) {
                PdfXConformanceImp.checkPDFXConformance(this, 7, null);
            }
            this.documentProperties.put(prop, new PdfObject[] { new PdfName("Pr" + (this.documentProperties.size() + 1)), refi });
        }
        return this.documentProperties.get(prop);
    }
    
    boolean propertyExists(final Object prop) {
        return this.documentProperties.containsKey(prop);
    }
    
    public void setTagged() {
        if (this.open) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("tagging.must.be.set.before.opening.the.document"));
        }
        this.tagged = true;
    }
    
    public boolean isTagged() {
        return this.tagged;
    }
    
    public PdfStructureTreeRoot getStructureTreeRoot() {
        if (this.tagged && this.structureTreeRoot == null) {
            this.structureTreeRoot = new PdfStructureTreeRoot(this);
        }
        return this.structureTreeRoot;
    }
    
    public PdfOCProperties getOCProperties() {
        this.fillOCProperties(true);
        return this.OCProperties;
    }
    
    public void addOCGRadioGroup(final ArrayList<PdfLayer> group) {
        final PdfArray ar = new PdfArray();
        for (int k = 0; k < group.size(); ++k) {
            final PdfLayer layer = group.get(k);
            if (layer.getTitle() == null) {
                ar.add(layer.getRef());
            }
        }
        if (ar.size() == 0) {
            return;
        }
        this.OCGRadioGroup.add(ar);
    }
    
    public void lockLayer(final PdfLayer layer) {
        this.OCGLocked.add(layer.getRef());
    }
    
    private static void getOCGOrder(final PdfArray order, final PdfLayer layer) {
        if (!layer.isOnPanel()) {
            return;
        }
        if (layer.getTitle() == null) {
            order.add(layer.getRef());
        }
        final ArrayList<PdfLayer> children = layer.getChildren();
        if (children == null) {
            return;
        }
        final PdfArray kids = new PdfArray();
        if (layer.getTitle() != null) {
            kids.add(new PdfString(layer.getTitle(), "UnicodeBig"));
        }
        for (int k = 0; k < children.size(); ++k) {
            getOCGOrder(kids, children.get(k));
        }
        if (kids.size() > 0) {
            order.add(kids);
        }
    }
    
    private void addASEvent(final PdfName event, final PdfName category) {
        final PdfArray arr = new PdfArray();
        for (final PdfLayer layer : this.documentOCG) {
            final PdfDictionary usage = (PdfDictionary)layer.get(PdfName.USAGE);
            if (usage != null && usage.get(category) != null) {
                arr.add(layer.getRef());
            }
        }
        if (arr.size() == 0) {
            return;
        }
        final PdfDictionary d = (PdfDictionary)this.OCProperties.get(PdfName.D);
        PdfArray arras = (PdfArray)d.get(PdfName.AS);
        if (arras == null) {
            arras = new PdfArray();
            d.put(PdfName.AS, arras);
        }
        final PdfDictionary as = new PdfDictionary();
        as.put(PdfName.EVENT, event);
        as.put(PdfName.CATEGORY, new PdfArray(category));
        as.put(PdfName.OCGS, arr);
        arras.add(as);
    }
    
    protected void fillOCProperties(final boolean erase) {
        if (this.OCProperties == null) {
            this.OCProperties = new PdfOCProperties();
        }
        if (erase) {
            this.OCProperties.remove(PdfName.OCGS);
            this.OCProperties.remove(PdfName.D);
        }
        if (this.OCProperties.get(PdfName.OCGS) == null) {
            final PdfArray gr = new PdfArray();
            for (final PdfLayer layer : this.documentOCG) {
                gr.add(layer.getRef());
            }
            this.OCProperties.put(PdfName.OCGS, gr);
        }
        if (this.OCProperties.get(PdfName.D) != null) {
            return;
        }
        final ArrayList docOrder = new ArrayList(this.documentOCGorder);
        final Iterator it = docOrder.iterator();
        while (it.hasNext()) {
            final PdfLayer layer = it.next();
            if (layer.getParent() != null) {
                it.remove();
            }
        }
        final PdfArray order = new PdfArray();
        for (final PdfLayer layer2 : docOrder) {
            getOCGOrder(order, layer2);
        }
        final PdfDictionary d = new PdfDictionary();
        this.OCProperties.put(PdfName.D, d);
        d.put(PdfName.ORDER, order);
        final PdfArray gr2 = new PdfArray();
        for (final PdfLayer layer3 : this.documentOCG) {
            if (!layer3.isOn()) {
                gr2.add(layer3.getRef());
            }
        }
        if (gr2.size() > 0) {
            d.put(PdfName.OFF, gr2);
        }
        if (this.OCGRadioGroup.size() > 0) {
            d.put(PdfName.RBGROUPS, this.OCGRadioGroup);
        }
        if (this.OCGLocked.size() > 0) {
            d.put(PdfName.LOCKED, this.OCGLocked);
        }
        this.addASEvent(PdfName.VIEW, PdfName.ZOOM);
        this.addASEvent(PdfName.VIEW, PdfName.VIEW);
        this.addASEvent(PdfName.PRINT, PdfName.PRINT);
        this.addASEvent(PdfName.EXPORT, PdfName.EXPORT);
        d.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
    }
    
    void registerLayer(final PdfOCG layer) {
        PdfXConformanceImp.checkPDFXConformance(this, 7, null);
        if (layer instanceof PdfLayer) {
            final PdfLayer la = (PdfLayer)layer;
            if (la.getTitle() == null) {
                if (!this.documentOCG.contains(layer)) {
                    this.documentOCG.add(layer);
                    this.documentOCGorder.add(layer);
                }
            }
            else {
                this.documentOCGorder.add(layer);
            }
            return;
        }
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("only.pdflayer.is.accepted"));
    }
    
    public Rectangle getPageSize() {
        return this.pdf.getPageSize();
    }
    
    public void setCropBoxSize(final Rectangle crop) {
        this.pdf.setCropBoxSize(crop);
    }
    
    public void setBoxSize(final String boxName, final Rectangle size) {
        this.pdf.setBoxSize(boxName, size);
    }
    
    public Rectangle getBoxSize(final String boxName) {
        return this.pdf.getBoxSize(boxName);
    }
    
    public void setPageEmpty(final boolean pageEmpty) {
        if (pageEmpty) {
            return;
        }
        this.pdf.setPageEmpty(pageEmpty);
    }
    
    public boolean isPageEmpty() {
        return this.pdf.isPageEmpty();
    }
    
    @Override
    public void setPageAction(final PdfName actionType, final PdfAction action) throws DocumentException {
        if (!actionType.equals(PdfWriter.PAGE_OPEN) && !actionType.equals(PdfWriter.PAGE_CLOSE)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", actionType.toString()));
        }
        this.pdf.setPageAction(actionType, action);
    }
    
    @Override
    public void setDuration(final int seconds) {
        this.pdf.setDuration(seconds);
    }
    
    @Override
    public void setTransition(final PdfTransition transition) {
        this.pdf.setTransition(transition);
    }
    
    public void setThumbnail(final Image image) throws DocumentException {
        this.pdf.setThumbnail(image);
    }
    
    public PdfDictionary getGroup() {
        return this.group;
    }
    
    public void setGroup(final PdfDictionary group) {
        this.group = group;
    }
    
    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }
    
    public void setSpaceCharRatio(final float spaceCharRatio) {
        if (spaceCharRatio < 0.001f) {
            this.spaceCharRatio = 0.001f;
        }
        else {
            this.spaceCharRatio = spaceCharRatio;
        }
    }
    
    @Override
    public void setRunDirection(final int runDirection) {
        if (runDirection < 1 || runDirection > 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
        }
        this.runDirection = runDirection;
    }
    
    @Override
    public int getRunDirection() {
        return this.runDirection;
    }
    
    public float getUserunit() {
        return this.userunit;
    }
    
    public void setUserunit(final float userunit) throws DocumentException {
        if (userunit < 1.0f || userunit > 75000.0f) {
            throw new DocumentException(MessageLocalization.getComposedMessage("userunit.should.be.a.value.between.1.and.75000"));
        }
        this.userunit = userunit;
        this.setAtLeastPdfVersion('6');
    }
    
    public PdfDictionary getDefaultColorspace() {
        return this.defaultColorspace;
    }
    
    public void setDefaultColorspace(final PdfName key, final PdfObject cs) {
        if (cs == null || cs.isNull()) {
            this.defaultColorspace.remove(key);
        }
        this.defaultColorspace.put(key, cs);
    }
    
    ColorDetails addSimplePatternColorspace(final Color color) {
        final int type = ExtendedColor.getType(color);
        if (type == 4 || type == 5) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.tile.pattern.can.not.have.another.pattern.or.shading.as.color"));
        }
        try {
            switch (type) {
                case 0: {
                    if (this.patternColorspaceRGB == null) {
                        this.patternColorspaceRGB = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        final PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICERGB);
                        this.addToBody(array, this.patternColorspaceRGB.getIndirectReference());
                    }
                    return this.patternColorspaceRGB;
                }
                case 2: {
                    if (this.patternColorspaceCMYK == null) {
                        this.patternColorspaceCMYK = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        final PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICECMYK);
                        this.addToBody(array, this.patternColorspaceCMYK.getIndirectReference());
                    }
                    return this.patternColorspaceCMYK;
                }
                case 1: {
                    if (this.patternColorspaceGRAY == null) {
                        this.patternColorspaceGRAY = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        final PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICEGRAY);
                        this.addToBody(array, this.patternColorspaceGRAY.getIndirectReference());
                    }
                    return this.patternColorspaceGRAY;
                }
                case 3: {
                    final ColorDetails details = this.addSimple(((SpotColor)color).getPdfSpotColor());
                    ColorDetails patternDetails = this.documentSpotPatterns.get(details);
                    if (patternDetails == null) {
                        patternDetails = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        final PdfArray array2 = new PdfArray(PdfName.PATTERN);
                        array2.add(details.getIndirectReference());
                        this.addToBody(array2, patternDetails.getIndirectReference());
                        this.documentSpotPatterns.put(details, patternDetails);
                    }
                    return patternDetails;
                }
                default: {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public boolean isStrictImageSequence() {
        return this.pdf.isStrictImageSequence();
    }
    
    public void setStrictImageSequence(final boolean strictImageSequence) {
        this.pdf.setStrictImageSequence(strictImageSequence);
    }
    
    public void clearTextWrap() throws DocumentException {
        this.pdf.clearTextWrap();
    }
    
    public PdfName addDirectImageSimple(final Image image) throws DocumentException {
        return this.addDirectImageSimple(image, null);
    }
    
    public PdfName addDirectImageSimple(final Image image, final PdfIndirectReference fixedRef) throws DocumentException {
        PdfName name = null;
        if (this.images.containsKey(image.getMySerialId())) {
            name = this.images.get(image.getMySerialId());
        }
        else {
            Label_0463: {
                if (image.isImgTemplate()) {
                    name = new PdfName("img" + this.images.size());
                    if (!(image instanceof ImgWMF)) {
                        break Label_0463;
                    }
                    try {
                        final ImgWMF wmf = (ImgWMF)image;
                        wmf.readWMF(PdfTemplate.createTemplate(this, 0.0f, 0.0f));
                        break Label_0463;
                    }
                    catch (final Exception e) {
                        throw new DocumentException(e);
                    }
                }
                final PdfIndirectReference dref = image.getDirectReference();
                if (dref != null) {
                    final PdfName rname = new PdfName("img" + this.images.size());
                    this.images.put(image.getMySerialId(), rname);
                    this.imageDictionary.put(rname, dref);
                    return rname;
                }
                final Image maskImage = image.getImageMask();
                PdfIndirectReference maskRef = null;
                if (maskImage != null) {
                    final PdfName mname = this.images.get(maskImage.getMySerialId());
                    maskRef = this.getImageReference(mname);
                }
                final PdfImage i = new PdfImage(image, "img" + this.images.size(), maskRef);
                if (image instanceof ImgJBIG2) {
                    final byte[] globals = ((ImgJBIG2)image).getGlobalBytes();
                    if (globals != null) {
                        final PdfDictionary decodeparms = new PdfDictionary();
                        decodeparms.put(PdfName.JBIG2GLOBALS, this.getReferenceJBIG2Globals(globals));
                        i.put(PdfName.DECODEPARMS, decodeparms);
                    }
                }
                if (image.hasICCProfile()) {
                    final PdfICCBased icc = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
                    final PdfIndirectReference iccRef = this.add(icc);
                    final PdfArray iccArray = new PdfArray();
                    iccArray.add(PdfName.ICCBASED);
                    iccArray.add(iccRef);
                    final PdfArray colorspace = i.getAsArray(PdfName.COLORSPACE);
                    if (colorspace != null) {
                        if (colorspace.size() > 1 && PdfName.INDEXED.equals(colorspace.getPdfObject(0))) {
                            colorspace.set(1, iccArray);
                        }
                        else {
                            i.put(PdfName.COLORSPACE, iccArray);
                        }
                    }
                    else {
                        i.put(PdfName.COLORSPACE, iccArray);
                    }
                }
                this.add(i, fixedRef);
                name = i.name();
            }
            this.images.put(image.getMySerialId(), name);
        }
        return name;
    }
    
    PdfIndirectReference add(final PdfImage pdfImage, PdfIndirectReference fixedRef) throws PdfException {
        if (!this.imageDictionary.contains(pdfImage.name())) {
            PdfXConformanceImp.checkPDFXConformance(this, 5, pdfImage);
            if (fixedRef instanceof PRIndirectReference) {
                final PRIndirectReference r2 = (PRIndirectReference)fixedRef;
                fixedRef = new PdfIndirectReference(0, this.getNewObjectNumber(r2.getReader(), r2.getNumber(), r2.getGeneration()));
            }
            try {
                if (fixedRef == null) {
                    fixedRef = this.addToBody(pdfImage).getIndirectReference();
                }
                else {
                    this.addToBody(pdfImage, fixedRef);
                }
            }
            catch (final IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            this.imageDictionary.put(pdfImage.name(), fixedRef);
            return fixedRef;
        }
        return (PdfIndirectReference)this.imageDictionary.get(pdfImage.name());
    }
    
    PdfIndirectReference getImageReference(final PdfName name) {
        return (PdfIndirectReference)this.imageDictionary.get(name);
    }
    
    protected PdfIndirectReference add(final PdfICCBased icc) {
        PdfIndirectObject object;
        try {
            object = this.addToBody(icc);
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return object.getIndirectReference();
    }
    
    protected PdfIndirectReference getReferenceJBIG2Globals(final byte[] content) {
        if (content == null) {
            return null;
        }
        for (final PdfStream stream : this.JBIG2Globals.keySet()) {
            if (Arrays.equals(content, stream.getBytes())) {
                return this.JBIG2Globals.get(stream);
            }
        }
        PdfStream stream = new PdfStream(content);
        PdfIndirectObject ref;
        try {
            ref = this.addToBody(stream);
        }
        catch (final IOException e) {
            return null;
        }
        this.JBIG2Globals.put(stream, ref.getIndirectReference());
        return ref.getIndirectReference();
    }
    
    public boolean fitsPage(final Table table, final float margin) {
        return this.pdf.bottom(table) > this.pdf.indentBottom() + margin;
    }
    
    public boolean fitsPage(final Table table) {
        return this.fitsPage(table, 0.0f);
    }
    
    public boolean isUserProperties() {
        return this.userProperties;
    }
    
    public void setUserProperties(final boolean userProperties) {
        this.userProperties = userProperties;
    }
    
    public boolean isRgbTransparencyBlending() {
        return this.rgbTransparencyBlending;
    }
    
    public void setRgbTransparencyBlending(final boolean rgbTransparencyBlending) {
        this.rgbTransparencyBlending = rgbTransparencyBlending;
    }
    
    static {
        PDF_VERSION_1_2 = new PdfName("1.2");
        PDF_VERSION_1_3 = new PdfName("1.3");
        PDF_VERSION_1_4 = new PdfName("1.4");
        PDF_VERSION_1_5 = new PdfName("1.5");
        PDF_VERSION_1_6 = new PdfName("1.6");
        PDF_VERSION_1_7 = new PdfName("1.7");
        DOCUMENT_CLOSE = PdfName.WC;
        WILL_SAVE = PdfName.WS;
        DID_SAVE = PdfName.DS;
        WILL_PRINT = PdfName.WP;
        DID_PRINT = PdfName.DP;
        PAGE_OPEN = PdfName.O;
        PAGE_CLOSE = PdfName.C;
    }
    
    public static class PdfBody
    {
        private static final int OBJSINSTREAM = 200;
        private TreeSet<PdfCrossReference> xrefs;
        private int refnum;
        private int position;
        private PdfWriter writer;
        private ByteBuffer index;
        private ByteBuffer streamObjects;
        private int currentObjNum;
        private int numObj;
        
        PdfBody(final PdfWriter writer) {
            this.numObj = 0;
            (this.xrefs = new TreeSet<PdfCrossReference>()).add(new PdfCrossReference(0, 0, 65535));
            this.position = writer.getOs().getCounter();
            this.refnum = 1;
            this.writer = writer;
        }
        
        void setRefnum(final int refnum) {
            this.refnum = refnum;
        }
        
        private PdfCrossReference addToObjStm(final PdfObject obj, final int nObj) throws IOException {
            if (this.numObj >= 200) {
                this.flushObjStm();
            }
            if (this.index == null) {
                this.index = new ByteBuffer();
                this.streamObjects = new ByteBuffer();
                this.currentObjNum = this.getIndirectReferenceNumber();
                this.numObj = 0;
            }
            final int p = this.streamObjects.size();
            final int idx = this.numObj++;
            final PdfEncryption enc = this.writer.crypto;
            this.writer.crypto = null;
            obj.toPdf(this.writer, this.streamObjects);
            this.writer.crypto = enc;
            this.streamObjects.append(' ');
            this.index.append(nObj).append(' ').append(p).append(' ');
            return new PdfCrossReference(2, nObj, this.currentObjNum, idx);
        }
        
        private void flushObjStm() throws IOException {
            if (this.numObj == 0) {
                return;
            }
            final int first = this.index.size();
            this.index.append(this.streamObjects);
            final PdfStream stream = new PdfStream(this.index.toByteArray());
            stream.flateCompress(this.writer.getCompressionLevel());
            stream.put(PdfName.TYPE, PdfName.OBJSTM);
            stream.put(PdfName.N, new PdfNumber(this.numObj));
            stream.put(PdfName.FIRST, new PdfNumber(first));
            this.add(stream, this.currentObjNum);
            this.index = null;
            this.streamObjects = null;
            this.numObj = 0;
        }
        
        PdfIndirectObject add(final PdfObject object) throws IOException {
            return this.add(object, this.getIndirectReferenceNumber());
        }
        
        PdfIndirectObject add(final PdfObject object, final boolean inObjStm) throws IOException {
            return this.add(object, this.getIndirectReferenceNumber(), inObjStm);
        }
        
        PdfIndirectReference getPdfIndirectReference() {
            return new PdfIndirectReference(0, this.getIndirectReferenceNumber());
        }
        
        int getIndirectReferenceNumber() {
            final int n = this.refnum++;
            this.xrefs.add(new PdfCrossReference(n, 0, 65535));
            return n;
        }
        
        PdfIndirectObject add(final PdfObject object, final PdfIndirectReference ref) throws IOException {
            return this.add(object, ref.getNumber());
        }
        
        PdfIndirectObject add(final PdfObject object, final PdfIndirectReference ref, final boolean inObjStm) throws IOException {
            return this.add(object, ref.getNumber(), inObjStm);
        }
        
        PdfIndirectObject add(final PdfObject object, final int refNumber) throws IOException {
            return this.add(object, refNumber, true);
        }
        
        PdfIndirectObject add(final PdfObject object, final int refNumber, final boolean inObjStm) throws IOException {
            if (inObjStm && object.canBeInObjStm() && this.writer.isFullCompression()) {
                final PdfCrossReference pxref = this.addToObjStm(object, refNumber);
                final PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, this.writer);
                if (!this.xrefs.add(pxref)) {
                    this.xrefs.remove(pxref);
                    this.xrefs.add(pxref);
                }
                return indirect;
            }
            final PdfIndirectObject indirect2 = new PdfIndirectObject(refNumber, object, this.writer);
            final PdfCrossReference pxref2 = new PdfCrossReference(refNumber, this.position);
            if (!this.xrefs.add(pxref2)) {
                this.xrefs.remove(pxref2);
                this.xrefs.add(pxref2);
            }
            indirect2.writeTo(this.writer.getOs());
            this.position = this.writer.getOs().getCounter();
            return indirect2;
        }
        
        int offset() {
            return this.position;
        }
        
        int size() {
            return Math.max(this.xrefs.last().getRefnum() + 1, this.refnum);
        }
        
        void writeCrossReferenceTable(final OutputStream os, final PdfIndirectReference root, final PdfIndirectReference info, final PdfIndirectReference encryption, final PdfObject fileID, final int prevxref) throws IOException {
            int refNumber = 0;
            if (this.writer.isFullCompression()) {
                this.flushObjStm();
                refNumber = this.getIndirectReferenceNumber();
                this.xrefs.add(new PdfCrossReference(refNumber, this.position));
            }
            PdfCrossReference entry = this.xrefs.first();
            int first = entry.getRefnum();
            int len = 0;
            final ArrayList<Integer> sections = new ArrayList<Integer>();
            Iterator<PdfCrossReference> i = this.xrefs.iterator();
            while (i.hasNext()) {
                entry = i.next();
                if (first + len == entry.getRefnum()) {
                    ++len;
                }
                else {
                    sections.add(new Integer(first));
                    sections.add(new Integer(len));
                    first = entry.getRefnum();
                    len = 1;
                }
            }
            sections.add(new Integer(first));
            sections.add(new Integer(len));
            if (this.writer.isFullCompression()) {
                int mid = 4;
                for (int mask = -16777216; mid > 1 && (mask & this.position) == 0x0; mask >>>= 8, --mid) {}
                ByteBuffer buf = new ByteBuffer();
                final Iterator<PdfCrossReference> j = this.xrefs.iterator();
                while (j.hasNext()) {
                    entry = j.next();
                    entry.toPdf(mid, buf);
                }
                final PdfStream xr = new PdfStream(buf.toByteArray());
                buf = null;
                xr.flateCompress(this.writer.getCompressionLevel());
                xr.put(PdfName.SIZE, new PdfNumber(this.size()));
                xr.put(PdfName.ROOT, root);
                if (info != null) {
                    xr.put(PdfName.INFO, info);
                }
                if (encryption != null) {
                    xr.put(PdfName.ENCRYPT, encryption);
                }
                if (fileID != null) {
                    xr.put(PdfName.ID, fileID);
                }
                xr.put(PdfName.W, new PdfArray(new int[] { 1, mid, 2 }));
                xr.put(PdfName.TYPE, PdfName.XREF);
                final PdfArray idx = new PdfArray();
                for (int k = 0; k < sections.size(); ++k) {
                    idx.add(new PdfNumber(sections.get(k)));
                }
                xr.put(PdfName.INDEX, idx);
                if (prevxref > 0) {
                    xr.put(PdfName.PREV, new PdfNumber(prevxref));
                }
                final PdfEncryption enc = this.writer.crypto;
                this.writer.crypto = null;
                final PdfIndirectObject indirect = new PdfIndirectObject(refNumber, xr, this.writer);
                indirect.writeTo(this.writer.getOs());
                this.writer.crypto = enc;
            }
            else {
                os.write(DocWriter.getISOBytes("xref\n"));
                i = this.xrefs.iterator();
                for (int l = 0; l < sections.size(); l += 2) {
                    first = sections.get(l);
                    len = sections.get(l + 1);
                    os.write(DocWriter.getISOBytes(String.valueOf(first)));
                    os.write(DocWriter.getISOBytes(" "));
                    os.write(DocWriter.getISOBytes(String.valueOf(len)));
                    os.write(10);
                    while (len-- > 0) {
                        entry = i.next();
                        entry.toPdf(os);
                    }
                }
            }
        }
        
        static class PdfCrossReference implements Comparable
        {
            private int type;
            private int offset;
            private int refnum;
            private int generation;
            
            PdfCrossReference(final int refnum, final int offset, final int generation) {
                this.type = 0;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }
            
            PdfCrossReference(final int refnum, final int offset) {
                this.type = 1;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = 0;
            }
            
            PdfCrossReference(final int type, final int refnum, final int offset, final int generation) {
                this.type = type;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }
            
            int getRefnum() {
                return this.refnum;
            }
            
            public void toPdf(final OutputStream os) throws IOException {
                final StringBuffer off = new StringBuffer("0000000000").append(this.offset);
                off.delete(0, off.length() - 10);
                final StringBuffer gen = new StringBuffer("00000").append(this.generation);
                gen.delete(0, gen.length() - 5);
                off.append(' ').append(gen).append((this.generation == 65535) ? " f \n" : " n \n");
                os.write(DocWriter.getISOBytes(off.toString()));
            }
            
            public void toPdf(int midSize, final OutputStream os) throws IOException {
                os.write((byte)this.type);
                while (--midSize >= 0) {
                    os.write((byte)(this.offset >>> 8 * midSize & 0xFF));
                }
                os.write((byte)(this.generation >>> 8 & 0xFF));
                os.write((byte)(this.generation & 0xFF));
            }
            
            @Override
            public int compareTo(final Object o) {
                final PdfCrossReference other = (PdfCrossReference)o;
                return (this.refnum < other.refnum) ? -1 : ((this.refnum == other.refnum) ? 0 : 1);
            }
            
            @Override
            public boolean equals(final Object obj) {
                if (obj instanceof PdfCrossReference) {
                    final PdfCrossReference other = (PdfCrossReference)obj;
                    return this.refnum == other.refnum;
                }
                return false;
            }
            
            @Override
            public int hashCode() {
                return this.refnum;
            }
        }
    }
    
    static class PdfTrailer extends PdfDictionary
    {
        int offset;
        
        PdfTrailer(final int size, final int offset, final PdfIndirectReference root, final PdfIndirectReference info, final PdfIndirectReference encryption, final PdfObject fileID, final int prevxref) {
            this.offset = offset;
            this.put(PdfName.SIZE, new PdfNumber(size));
            this.put(PdfName.ROOT, root);
            if (info != null) {
                this.put(PdfName.INFO, info);
            }
            if (encryption != null) {
                this.put(PdfName.ENCRYPT, encryption);
            }
            if (fileID != null) {
                this.put(PdfName.ID, fileID);
            }
            if (prevxref > 0) {
                this.put(PdfName.PREV, new PdfNumber(prevxref));
            }
        }
        
        @Override
        public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
            os.write(DocWriter.getISOBytes("trailer\n"));
            super.toPdf(null, os);
            os.write(DocWriter.getISOBytes("\nstartxref\n"));
            os.write(DocWriter.getISOBytes(String.valueOf(this.offset)));
            os.write(DocWriter.getISOBytes("\n%%EOF\n"));
        }
    }
}
