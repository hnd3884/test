package com.lowagie.text.pdf;

import java.util.Set;
import java.util.HashSet;
import com.lowagie.text.Font;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import com.lowagie.text.HeaderFooter;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.pdf.draw.DrawInterface;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Table;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.ListItem;
import com.lowagie.text.List;
import com.lowagie.text.Section;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Annotation;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Meta;
import com.lowagie.text.Element;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.collection.PdfCollection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TreeMap;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.util.ArrayList;
import com.lowagie.text.Document;

public class PdfDocument extends Document
{
    protected PdfWriter writer;
    protected PdfContentByte text;
    protected PdfContentByte graphics;
    protected float leading;
    protected int alignment;
    protected float currentHeight;
    protected boolean isSectionTitle;
    protected int leadingCount;
    protected PdfAction anchorAction;
    protected int textEmptySize;
    protected byte[] xmpMetadata;
    protected float nextMarginLeft;
    protected float nextMarginRight;
    protected float nextMarginTop;
    protected float nextMarginBottom;
    protected boolean firstPageEvent;
    protected PdfLine line;
    protected ArrayList<PdfLine> lines;
    protected int lastElementType;
    static final String hangingPunctuation = ".,;:'";
    protected Indentation indentation;
    protected PdfInfo info;
    protected PdfOutline rootOutline;
    protected PdfOutline currentOutline;
    protected PdfViewerPreferencesImp viewerPreferences;
    protected PdfPageLabels pageLabels;
    protected TreeMap<String, Object[]> localDestinations;
    int jsCounter;
    protected HashMap<String, PdfIndirectReference> documentLevelJS;
    protected static final DecimalFormat SIXTEEN_DIGITS;
    protected HashMap<String, PdfIndirectReference> documentFileAttachment;
    protected String openActionName;
    protected PdfAction openActionAction;
    protected PdfDictionary additionalActions;
    protected PdfCollection collection;
    PdfAnnotationsImp annotationsImp;
    protected int markPoint;
    protected Rectangle nextPageSize;
    protected HashMap<String, PdfRectangle> thisBoxSize;
    protected HashMap<String, PdfRectangle> boxSize;
    private boolean pageEmpty;
    protected int duration;
    protected PdfTransition transition;
    protected PdfDictionary pageAA;
    protected PdfIndirectReference thumb;
    protected PageResources pageResources;
    protected boolean strictImageSequence;
    protected float imageEnd;
    protected Image imageWait;
    
    public PdfDocument() {
        this.leading = 0.0f;
        this.alignment = 0;
        this.currentHeight = 0.0f;
        this.isSectionTitle = false;
        this.leadingCount = 0;
        this.anchorAction = null;
        this.xmpMetadata = null;
        this.firstPageEvent = true;
        this.line = null;
        this.lines = new ArrayList<PdfLine>();
        this.lastElementType = -1;
        this.indentation = new Indentation();
        this.info = new PdfInfo();
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.localDestinations = new TreeMap<String, Object[]>();
        this.documentLevelJS = new HashMap<String, PdfIndirectReference>();
        this.documentFileAttachment = new HashMap<String, PdfIndirectReference>();
        this.nextPageSize = null;
        this.thisBoxSize = new HashMap<String, PdfRectangle>();
        this.boxSize = new HashMap<String, PdfRectangle>();
        this.pageEmpty = true;
        this.duration = -1;
        this.transition = null;
        this.pageAA = null;
        this.strictImageSequence = false;
        this.imageEnd = -1.0f;
        this.imageWait = null;
        this.addProducer();
        this.addCreationDate();
    }
    
    public void addWriter(final PdfWriter writer) throws DocumentException {
        if (this.writer == null) {
            this.writer = writer;
            this.annotationsImp = new PdfAnnotationsImp(writer);
            return;
        }
        throw new DocumentException(MessageLocalization.getComposedMessage("you.can.only.add.a.writer.to.a.pdfdocument.once"));
    }
    
    public float getLeading() {
        return this.leading;
    }
    
    void setLeading(final float leading) {
        this.leading = leading;
    }
    
    @Override
    public boolean add(final Element element) throws DocumentException {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        try {
            switch (element.type()) {
                case 0: {
                    this.info.addkey(((Meta)element).getName(), ((Meta)element).getContent());
                    break;
                }
                case 1: {
                    this.info.addTitle(((Meta)element).getContent());
                    break;
                }
                case 2: {
                    this.info.addSubject(((Meta)element).getContent());
                    break;
                }
                case 3: {
                    this.info.addKeywords(((Meta)element).getContent());
                    break;
                }
                case 4: {
                    this.info.addAuthor(((Meta)element).getContent());
                    break;
                }
                case 7: {
                    this.info.addCreator(((Meta)element).getContent());
                    break;
                }
                case 5: {
                    this.info.addProducer(((Meta)element).getContent());
                    break;
                }
                case 6: {
                    this.info.addCreationDate();
                    break;
                }
                case 10: {
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    PdfChunk chunk = new PdfChunk((Chunk)element, this.anchorAction);
                    PdfChunk overflow;
                    while ((overflow = this.line.add(chunk)) != null) {
                        this.carriageReturn();
                        chunk = overflow;
                        chunk.trimFirstSpace();
                    }
                    this.pageEmpty = false;
                    if (chunk.isAttribute("NEWPAGE")) {
                        this.newPage();
                        break;
                    }
                    break;
                }
                case 17: {
                    ++this.leadingCount;
                    final Anchor anchor = (Anchor)element;
                    final String url = anchor.getReference();
                    this.leading = anchor.getLeading();
                    if (url != null) {
                        this.anchorAction = new PdfAction(url);
                    }
                    element.process(this);
                    this.anchorAction = null;
                    --this.leadingCount;
                    break;
                }
                case 29: {
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    final Annotation annot = (Annotation)element;
                    Rectangle rect = new Rectangle(0.0f, 0.0f);
                    if (this.line != null) {
                        rect = new Rectangle(annot.llx(this.indentRight() - this.line.widthLeft()), annot.ury(this.indentTop() - this.currentHeight - 20.0f), annot.urx(this.indentRight() - this.line.widthLeft() + 20.0f), annot.lly(this.indentTop() - this.currentHeight));
                    }
                    final PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, rect);
                    this.annotationsImp.addPlainAnnotation(an);
                    this.pageEmpty = false;
                    break;
                }
                case 11: {
                    ++this.leadingCount;
                    this.leading = ((Phrase)element).getLeading();
                    element.process(this);
                    --this.leadingCount;
                    break;
                }
                case 12: {
                    ++this.leadingCount;
                    final Paragraph paragraph = (Paragraph)element;
                    this.addSpacing(paragraph.getSpacingBefore(), this.leading, paragraph.getFont());
                    this.alignment = paragraph.getAlignment();
                    this.leading = paragraph.getTotalLeading();
                    this.carriageReturn();
                    if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
                        this.newPage();
                    }
                    final Indentation indentation = this.indentation;
                    indentation.indentLeft += paragraph.getIndentationLeft();
                    final Indentation indentation2 = this.indentation;
                    indentation2.indentRight += paragraph.getIndentationRight();
                    this.carriageReturn();
                    final PdfPageEvent pageEvent = this.writer.getPageEvent();
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraph(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    if (paragraph.getKeepTogether()) {
                        this.carriageReturn();
                        final PdfPTable table = new PdfPTable(1);
                        table.setWidthPercentage(100.0f);
                        final PdfPCell cell = new PdfPCell();
                        cell.addElement(paragraph);
                        cell.setBorder(0);
                        cell.setPadding(0.0f);
                        table.addCell(cell);
                        final Indentation indentation3 = this.indentation;
                        indentation3.indentLeft -= paragraph.getIndentationLeft();
                        final Indentation indentation4 = this.indentation;
                        indentation4.indentRight -= paragraph.getIndentationRight();
                        this.add(table);
                        final Indentation indentation5 = this.indentation;
                        indentation5.indentLeft += paragraph.getIndentationLeft();
                        final Indentation indentation6 = this.indentation;
                        indentation6.indentRight += paragraph.getIndentationRight();
                    }
                    else {
                        this.line.setExtraIndent(paragraph.getFirstLineIndent());
                        element.process(this);
                        this.carriageReturn();
                        this.addSpacing(paragraph.getSpacingAfter(), paragraph.getTotalLeading(), paragraph.getFont());
                    }
                    if (pageEvent != null && !this.isSectionTitle) {
                        pageEvent.onParagraphEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    this.alignment = 0;
                    final Indentation indentation7 = this.indentation;
                    indentation7.indentLeft -= paragraph.getIndentationLeft();
                    final Indentation indentation8 = this.indentation;
                    indentation8.indentRight -= paragraph.getIndentationRight();
                    this.carriageReturn();
                    --this.leadingCount;
                    break;
                }
                case 13:
                case 16: {
                    final Section section = (Section)element;
                    final PdfPageEvent pageEvent = this.writer.getPageEvent();
                    final boolean hasTitle = section.isNotAddedYet() && section.getTitle() != null;
                    if (section.isTriggerNewPage()) {
                        this.newPage();
                    }
                    if (hasTitle) {
                        float fith = this.indentTop() - this.currentHeight;
                        final int rotation = this.pageSize.getRotation();
                        if (rotation == 90 || rotation == 180) {
                            fith = this.pageSize.getHeight() - fith;
                        }
                        final PdfDestination destination = new PdfDestination(2, fith);
                        while (this.currentOutline.level() >= section.getDepth()) {
                            this.currentOutline = this.currentOutline.parent();
                        }
                        final PdfOutline outline = new PdfOutline(this.currentOutline, destination, section.getBookmarkTitle(), section.isBookmarkOpen());
                        this.currentOutline = outline;
                    }
                    this.carriageReturn();
                    final Indentation indentation9 = this.indentation;
                    indentation9.sectionIndentLeft += section.getIndentationLeft();
                    final Indentation indentation10 = this.indentation;
                    indentation10.sectionIndentRight += section.getIndentationRight();
                    if (section.isNotAddedYet() && pageEvent != null) {
                        if (element.type() == 16) {
                            pageEvent.onChapter(this.writer, this, this.indentTop() - this.currentHeight, section.getTitle());
                        }
                        else {
                            pageEvent.onSection(this.writer, this, this.indentTop() - this.currentHeight, section.getDepth(), section.getTitle());
                        }
                    }
                    if (hasTitle) {
                        this.isSectionTitle = true;
                        this.add(section.getTitle());
                        this.isSectionTitle = false;
                    }
                    final Indentation indentation11 = this.indentation;
                    indentation11.sectionIndentLeft += section.getIndentation();
                    element.process(this);
                    this.flushLines();
                    final Indentation indentation12 = this.indentation;
                    indentation12.sectionIndentLeft -= section.getIndentationLeft() + section.getIndentation();
                    final Indentation indentation13 = this.indentation;
                    indentation13.sectionIndentRight -= section.getIndentationRight();
                    if (!section.isComplete() || pageEvent == null) {
                        break;
                    }
                    if (element.type() == 16) {
                        pageEvent.onChapterEnd(this.writer, this, this.indentTop() - this.currentHeight);
                        break;
                    }
                    pageEvent.onSectionEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    break;
                }
                case 14: {
                    final List list = (List)element;
                    if (list.isAlignindent()) {
                        list.normalizeIndentation();
                    }
                    final Indentation indentation14 = this.indentation;
                    indentation14.listIndentLeft += list.getIndentationLeft();
                    final Indentation indentation15 = this.indentation;
                    indentation15.indentRight += list.getIndentationRight();
                    element.process(this);
                    final Indentation indentation16 = this.indentation;
                    indentation16.listIndentLeft -= list.getIndentationLeft();
                    final Indentation indentation17 = this.indentation;
                    indentation17.indentRight -= list.getIndentationRight();
                    this.carriageReturn();
                    break;
                }
                case 15: {
                    ++this.leadingCount;
                    final ListItem listItem = (ListItem)element;
                    this.addSpacing(listItem.getSpacingBefore(), this.leading, listItem.getFont());
                    this.alignment = listItem.getAlignment();
                    final Indentation indentation18 = this.indentation;
                    indentation18.listIndentLeft += listItem.getIndentationLeft();
                    final Indentation indentation19 = this.indentation;
                    indentation19.indentRight += listItem.getIndentationRight();
                    this.leading = listItem.getTotalLeading();
                    this.carriageReturn();
                    this.line.setListItem(listItem);
                    element.process(this);
                    this.addSpacing(listItem.getSpacingAfter(), listItem.getTotalLeading(), listItem.getFont());
                    if (this.line.hasToBeJustified()) {
                        this.line.resetAlignment();
                    }
                    this.carriageReturn();
                    final Indentation indentation20 = this.indentation;
                    indentation20.listIndentLeft -= listItem.getIndentationLeft();
                    final Indentation indentation21 = this.indentation;
                    indentation21.indentRight -= listItem.getIndentationRight();
                    --this.leadingCount;
                    break;
                }
                case 30: {
                    final Rectangle rectangle = (Rectangle)element;
                    this.graphics.rectangle(rectangle);
                    this.pageEmpty = false;
                    break;
                }
                case 23: {
                    final PdfPTable ptable = (PdfPTable)element;
                    if (ptable.size() <= ptable.getHeaderRows()) {
                        break;
                    }
                    this.ensureNewLine();
                    this.flushLines();
                    this.addPTable(ptable);
                    this.pageEmpty = false;
                    this.newLine();
                    break;
                }
                case 40: {
                    this.ensureNewLine();
                    this.flushLines();
                    final MultiColumnText multiText = (MultiColumnText)element;
                    final float height = multiText.write(this.writer.getDirectContent(), this, this.indentTop() - this.currentHeight);
                    this.currentHeight += height;
                    this.text.moveText(0.0f, -1.0f * height);
                    this.pageEmpty = false;
                    break;
                }
                case 22: {
                    if (element instanceof SimpleTable) {
                        final PdfPTable ptable = ((SimpleTable)element).createPdfPTable();
                        if (ptable.size() <= ptable.getHeaderRows()) {
                            break;
                        }
                        this.ensureNewLine();
                        this.flushLines();
                        this.addPTable(ptable);
                        this.pageEmpty = false;
                        break;
                    }
                    else {
                        if (element instanceof Table) {
                            try {
                                final PdfPTable ptable = ((Table)element).createPdfPTable();
                                if (ptable.size() > ptable.getHeaderRows()) {
                                    this.ensureNewLine();
                                    this.flushLines();
                                    this.addPTable(ptable);
                                    this.pageEmpty = false;
                                }
                            }
                            catch (final BadElementException bee) {
                                float offset = ((Table)element).getOffset();
                                if (Float.isNaN(offset)) {
                                    offset = this.leading;
                                }
                                this.carriageReturn();
                                this.lines.add(new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, offset));
                                this.currentHeight += offset;
                                this.addPdfTable((Table)element);
                            }
                            break;
                        }
                        return false;
                    }
                    break;
                }
                case 32:
                case 33:
                case 34:
                case 35:
                case 36: {
                    this.add((Image)element);
                    break;
                }
                case 55: {
                    final DrawInterface zh = (DrawInterface)element;
                    zh.draw(this.graphics, this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop(), this.indentTop() - this.currentHeight - ((this.leadingCount > 0) ? this.leading : 0.0f));
                    this.pageEmpty = false;
                    break;
                }
                case 50: {
                    if (element instanceof MarkedSection) {
                        final MarkedObject mo = ((MarkedSection)element).getTitle();
                        if (mo != null) {
                            mo.process(this);
                        }
                    }
                    final MarkedObject mo = (MarkedObject)element;
                    mo.process(this);
                    break;
                }
                default: {
                    return false;
                }
            }
            this.lastElementType = element.type();
            return true;
        }
        catch (final Exception e) {
            throw new DocumentException(e);
        }
    }
    
    @Override
    public void open() {
        if (!this.open) {
            super.open();
            this.writer.open();
            this.rootOutline = new PdfOutline(this.writer);
            this.currentOutline = this.rootOutline;
        }
        try {
            this.initPage();
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    @Override
    public void close() {
        if (this.close) {
            return;
        }
        try {
            final boolean wasImage = this.imageWait != null;
            this.newPage();
            if (this.imageWait != null || wasImage) {
                this.newPage();
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("not.all.annotations.could.be.added.to.the.document.the.document.doesn.t.have.enough.pages"));
            }
            final PdfPageEvent pageEvent = this.writer.getPageEvent();
            if (pageEvent != null) {
                pageEvent.onCloseDocument(this.writer, this);
            }
            super.close();
            this.writer.addLocalDestinations(this.localDestinations);
            this.calculateOutlineCount();
            this.writeOutlines();
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        this.writer.close();
    }
    
    public void setXmpMetadata(final byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }
    
    @Override
    public boolean newPage() {
        this.lastElementType = -1;
        if (this.isPageEmpty()) {
            this.setNewPageSizeAndMargins();
            return false;
        }
        if (!this.open || this.close) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        final PdfPageEvent pageEvent = this.writer.getPageEvent();
        if (pageEvent != null) {
            pageEvent.onEndPage(this.writer, this);
        }
        super.newPage();
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        try {
            this.flushLines();
            final int rotation = this.pageSize.getRotation();
            if (this.writer.isPdfX()) {
                if (this.thisBoxSize.containsKey("art") && this.thisBoxSize.containsKey("trim")) {
                    throw new PdfXConformanceException(MessageLocalization.getComposedMessage("only.one.of.artbox.or.trimbox.can.exist.in.the.page"));
                }
                if (!this.thisBoxSize.containsKey("art") && !this.thisBoxSize.containsKey("trim")) {
                    if (this.thisBoxSize.containsKey("crop")) {
                        this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
                    }
                    else {
                        this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
                    }
                }
            }
            this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
            if (this.writer.isRgbTransparencyBlending()) {
                final PdfDictionary dcs = new PdfDictionary();
                dcs.put(PdfName.CS, PdfName.DEVICERGB);
                this.pageResources.addDefaultColorDiff(dcs);
            }
            final PdfDictionary resources = this.pageResources.getResources();
            final PdfPage page = new PdfPage(new PdfRectangle(this.pageSize, rotation), this.thisBoxSize, resources, rotation);
            page.put(PdfName.TABS, this.writer.getTabs());
            if (this.xmpMetadata != null) {
                final PdfStream xmp = new PdfStream(this.xmpMetadata);
                xmp.put(PdfName.TYPE, PdfName.METADATA);
                xmp.put(PdfName.SUBTYPE, PdfName.XML);
                final PdfEncryption crypto = this.writer.getEncryption();
                if (crypto != null && !crypto.isMetadataEncrypted()) {
                    final PdfArray ar = new PdfArray();
                    ar.add(PdfName.CRYPT);
                    xmp.put(PdfName.FILTER, ar);
                }
                page.put(PdfName.METADATA, this.writer.addToBody(xmp).getIndirectReference());
            }
            if (this.transition != null) {
                page.put(PdfName.TRANS, this.transition.getTransitionDictionary());
                this.transition = null;
            }
            if (this.duration > 0) {
                page.put(PdfName.DUR, new PdfNumber(this.duration));
                this.duration = 0;
            }
            if (this.pageAA != null) {
                page.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
                this.pageAA = null;
            }
            if (this.thumb != null) {
                page.put(PdfName.THUMB, this.thumb);
                this.thumb = null;
            }
            if (this.writer.getUserunit() > 0.0f) {
                page.put(PdfName.USERUNIT, new PdfNumber(this.writer.getUserunit()));
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                final PdfArray array = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize);
                if (array.size() != 0) {
                    page.put(PdfName.ANNOTS, array);
                }
            }
            if (this.writer.isTagged()) {
                page.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1));
            }
            if (this.text.size() > this.textEmptySize) {
                this.text.endText();
            }
            else {
                this.text = null;
            }
            this.writer.add(page, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
            this.initPage();
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return true;
    }
    
    @Override
    public boolean setPageSize(final Rectangle pageSize) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextPageSize = new Rectangle(pageSize);
        return true;
    }
    
    @Override
    public boolean setMargins(final float marginLeft, final float marginRight, final float marginTop, final float marginBottom) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextMarginLeft = marginLeft;
        this.nextMarginRight = marginRight;
        this.nextMarginTop = marginTop;
        this.nextMarginBottom = marginBottom;
        return true;
    }
    
    @Override
    public boolean setMarginMirroring(final boolean MarginMirroring) {
        return (this.writer == null || !this.writer.isPaused()) && super.setMarginMirroring(MarginMirroring);
    }
    
    @Override
    public boolean setMarginMirroringTopBottom(final boolean MarginMirroringTopBottom) {
        return (this.writer == null || !this.writer.isPaused()) && super.setMarginMirroringTopBottom(MarginMirroringTopBottom);
    }
    
    @Override
    public void setPageCount(final int pageN) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setPageCount(pageN);
    }
    
    @Override
    public void resetPageCount() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetPageCount();
    }
    
    @Override
    public void setHeader(final HeaderFooter header) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setHeader(header);
    }
    
    @Override
    public void resetHeader() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetHeader();
    }
    
    @Override
    public void setFooter(final HeaderFooter footer) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setFooter(footer);
    }
    
    @Override
    public void resetFooter() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetFooter();
    }
    
    protected void initPage() throws DocumentException {
        ++this.pageN;
        this.annotationsImp.resetAnnotations();
        this.pageResources = new PageResources();
        this.writer.resetContent();
        this.graphics = new PdfContentByte(this.writer);
        (this.text = new PdfContentByte(this.writer)).reset();
        this.text.beginText();
        this.textEmptySize = this.text.size();
        this.markPoint = 0;
        this.setNewPageSizeAndMargins();
        this.imageEnd = -1.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.indentBottom = 0.0f;
        this.indentation.indentTop = 0.0f;
        this.currentHeight = 0.0f;
        this.thisBoxSize = new HashMap<String, PdfRectangle>(this.boxSize);
        if (this.pageSize.getBackgroundColor() != null || this.pageSize.hasBorders() || this.pageSize.getBorderColor() != null) {
            this.add(this.pageSize);
        }
        final float oldleading = this.leading;
        final int oldAlignment = this.alignment;
        this.doFooter();
        this.text.moveText(this.left(), this.top());
        this.doHeader();
        this.pageEmpty = true;
        try {
            if (this.imageWait != null) {
                this.add(this.imageWait);
                this.imageWait = null;
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        this.leading = oldleading;
        this.alignment = oldAlignment;
        this.carriageReturn();
        final PdfPageEvent pageEvent = this.writer.getPageEvent();
        if (pageEvent != null) {
            if (this.firstPageEvent) {
                pageEvent.onOpenDocument(this.writer, this);
            }
            pageEvent.onStartPage(this.writer, this);
        }
        this.firstPageEvent = false;
    }
    
    protected void newLine() throws DocumentException {
        this.lastElementType = -1;
        this.carriageReturn();
        if (this.lines != null && !this.lines.isEmpty()) {
            this.lines.add(this.line);
            this.currentHeight += this.line.height();
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }
    
    protected void carriageReturn() {
        if (this.lines == null) {
            this.lines = new ArrayList<PdfLine>();
        }
        if (this.line != null) {
            if (this.currentHeight + this.line.height() + this.leading < this.indentTop() - this.indentBottom()) {
                if (this.line.size() > 0) {
                    this.currentHeight += this.line.height();
                    this.lines.add(this.line);
                    this.pageEmpty = false;
                }
            }
            else {
                this.newPage();
            }
        }
        if (this.imageEnd > -1.0f && this.currentHeight > this.imageEnd) {
            this.imageEnd = -1.0f;
            this.indentation.imageIndentRight = 0.0f;
            this.indentation.imageIndentLeft = 0.0f;
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }
    
    public float getVerticalPosition(final boolean ensureNewLine) {
        if (ensureNewLine) {
            this.ensureNewLine();
        }
        return this.top() - this.currentHeight - this.indentation.indentTop;
    }
    
    protected void ensureNewLine() {
        try {
            if (this.lastElementType == 11 || this.lastElementType == 10) {
                this.newLine();
                this.flushLines();
            }
        }
        catch (final DocumentException ex) {
            throw new ExceptionConverter(ex);
        }
    }
    
    protected float flushLines() throws DocumentException {
        if (this.lines == null) {
            return 0.0f;
        }
        if (this.line != null && this.line.size() > 0) {
            this.lines.add(this.line);
            this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
        }
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        final Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        float displacement = 0.0f;
        final Float lastBaseFactor = new Float(0.0f);
        currentValues[1] = lastBaseFactor;
        for (final PdfLine l : this.lines) {
            final float moveTextX = l.indentLeft() - this.indentLeft() + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
            this.text.moveText(moveTextX, -l.height());
            if (l.listSymbol() != null) {
                ColumnText.showTextAligned(this.graphics, 0, new Phrase(l.listSymbol()), this.text.getXTLM() - l.listIndent(), this.text.getYTLM(), 0.0f);
            }
            currentValues[0] = currentFont;
            this.writeLineToContent(l, this.text, this.graphics, currentValues, this.writer.getSpaceCharRatio());
            currentFont = (PdfFont)currentValues[0];
            displacement += l.height();
            this.text.moveText(-moveTextX, 0.0f);
        }
        this.lines = new ArrayList<PdfLine>();
        return displacement;
    }
    
    void writeLineToContent(final PdfLine line, final PdfContentByte text, final PdfContentByte graphics, final Object[] currentValues, final float ratio) throws DocumentException {
        PdfFont currentFont = (PdfFont)currentValues[0];
        float lastBaseFactor = (float)currentValues[1];
        float hangingCorrection = 0.0f;
        float hScale = 1.0f;
        float lastHScale = Float.NaN;
        float baseWordSpacing = 0.0f;
        float baseCharacterSpacing = 0.0f;
        float glueWidth = 0.0f;
        final int numberOfSpaces = line.numberOfSpaces();
        final int lineLen = line.GetLineLengthUtf32();
        final boolean isJustified = line.hasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1);
        final int separatorCount = line.getSeparatorCount();
        if (separatorCount > 0) {
            glueWidth = line.widthLeft() / separatorCount;
        }
        else if (isJustified) {
            if (line.isNewlineSplit() && line.widthLeft() >= lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1.0f)) {
                if (line.isRTL()) {
                    text.moveText(line.widthLeft() - lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1.0f), 0.0f);
                }
                baseWordSpacing = ratio * lastBaseFactor;
                baseCharacterSpacing = lastBaseFactor;
            }
            else {
                float width = line.widthLeft();
                final PdfChunk last = line.getChunk(line.size() - 1);
                if (last != null) {
                    final String s = last.toString();
                    final char c;
                    if (s.length() > 0 && ".,;:'".indexOf(c = s.charAt(s.length() - 1)) >= 0) {
                        final float oldWidth = width;
                        width += last.font().width(c) * 0.4f;
                        hangingCorrection = width - oldWidth;
                    }
                }
                final float baseFactor = (numberOfSpaces == 0 && ratio == 1.0E7f) ? 0.0f : (width / (ratio * numberOfSpaces + lineLen - 1.0f));
                baseWordSpacing = ratio * baseFactor;
                baseCharacterSpacing = baseFactor;
                lastBaseFactor = baseFactor;
            }
        }
        final int lastChunkStroke = line.getLastStrokeChunk();
        int chunkStrokeIdx = 0;
        final float baseXMarker;
        float xMarker = baseXMarker = text.getXTLM();
        final float yMarker = text.getYTLM();
        boolean adjustMatrix = false;
        float tabPosition = 0.0f;
        for (final PdfChunk chunk : line) {
            final Color color = chunk.color();
            hScale = 1.0f;
            if (chunkStrokeIdx <= lastChunkStroke) {
                float width2;
                if (isJustified) {
                    width2 = chunk.getWidthCorrected(baseCharacterSpacing, baseWordSpacing);
                }
                else {
                    width2 = chunk.width();
                }
                if (chunk.isStroked()) {
                    final PdfChunk nextChunk = line.getChunk(chunkStrokeIdx + 1);
                    if (chunk.isSeparator()) {
                        width2 = glueWidth;
                        final Object[] sep = (Object[])chunk.getAttribute("SEPARATOR");
                        final DrawInterface di = (DrawInterface)sep[0];
                        final Boolean vertical = (Boolean)sep[1];
                        final float fontSize = chunk.font().size();
                        final float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
                        final float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
                        if (vertical) {
                            di.draw(graphics, baseXMarker, yMarker + descender, baseXMarker + line.getOriginalWidth(), ascender - descender, yMarker);
                        }
                        else {
                            di.draw(graphics, xMarker, yMarker + descender, xMarker + width2, ascender - descender, yMarker);
                        }
                    }
                    if (chunk.isTab()) {
                        final Object[] tab = (Object[])chunk.getAttribute("TAB");
                        final DrawInterface di = (DrawInterface)tab[0];
                        tabPosition = (float)tab[1] + (float)tab[3];
                        final float fontSize2 = chunk.font().size();
                        final float ascender2 = chunk.font().getFont().getFontDescriptor(1, fontSize2);
                        final float descender2 = chunk.font().getFont().getFontDescriptor(3, fontSize2);
                        if (tabPosition > xMarker) {
                            di.draw(graphics, xMarker, yMarker + descender2, tabPosition, ascender2 - descender2, yMarker);
                        }
                        final float tmp = xMarker;
                        xMarker = tabPosition;
                        tabPosition = tmp;
                    }
                    if (chunk.isAttribute("BACKGROUND")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("BACKGROUND")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        final float fontSize3 = chunk.font().size();
                        final float ascender3 = chunk.font().getFont().getFontDescriptor(1, fontSize3);
                        final float descender3 = chunk.font().getFont().getFontDescriptor(3, fontSize3);
                        final Object[] bgr = (Object[])chunk.getAttribute("BACKGROUND");
                        graphics.setColorFill((Color)bgr[0]);
                        final float[] extra = (float[])bgr[1];
                        graphics.rectangle(xMarker - extra[0], yMarker + descender3 - extra[1] + chunk.getTextRise(), width2 - subtract + extra[0] + extra[2], ascender3 - descender3 + extra[1] + extra[3]);
                        graphics.fill();
                        graphics.setGrayFill(0.0f);
                    }
                    if (chunk.isAttribute("UNDERLINE")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("UNDERLINE")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        final Object[][] unders = (Object[][])chunk.getAttribute("UNDERLINE");
                        Color scolor = null;
                        for (int k = 0; k < unders.length; ++k) {
                            final Object[] obj = unders[k];
                            scolor = (Color)obj[0];
                            final float[] ps = (float[])obj[1];
                            if (scolor == null) {
                                scolor = color;
                            }
                            if (scolor != null) {
                                graphics.setColorStroke(scolor);
                            }
                            final float fsize = chunk.font().size();
                            graphics.setLineWidth(ps[0] + fsize * ps[1]);
                            final float shift = ps[2] + fsize * ps[3];
                            final int cap2 = (int)ps[4];
                            if (cap2 != 0) {
                                graphics.setLineCap(cap2);
                            }
                            graphics.moveTo(xMarker, yMarker + shift);
                            graphics.lineTo(xMarker + width2 - subtract, yMarker + shift);
                            graphics.stroke();
                            if (scolor != null) {
                                graphics.resetGrayStroke();
                            }
                            if (cap2 != 0) {
                                graphics.setLineCap(0);
                            }
                        }
                        graphics.setLineWidth(1.0f);
                    }
                    if (chunk.isAttribute("ACTION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("ACTION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        text.addAnnotation(new PdfAnnotation(this.writer, xMarker, yMarker, xMarker + width2 - subtract, yMarker + chunk.font().size(), (PdfAction)chunk.getAttribute("ACTION")));
                    }
                    if (chunk.isAttribute("REMOTEGOTO")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("REMOTEGOTO")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        final Object[] obj2 = (Object[])chunk.getAttribute("REMOTEGOTO");
                        final String filename = (String)obj2[0];
                        if (obj2[1] instanceof String) {
                            this.remoteGoto(filename, (String)obj2[1], xMarker, yMarker, xMarker + width2 - subtract, yMarker + chunk.font().size());
                        }
                        else {
                            this.remoteGoto(filename, (int)obj2[1], xMarker, yMarker, xMarker + width2 - subtract, yMarker + chunk.font().size());
                        }
                    }
                    if (chunk.isAttribute("LOCALGOTO")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("LOCALGOTO")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        this.localGoto((String)chunk.getAttribute("LOCALGOTO"), xMarker, yMarker, xMarker + width2 - subtract, yMarker + chunk.font().size());
                    }
                    if (chunk.isAttribute("LOCALDESTINATION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("LOCALDESTINATION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        this.localDestination((String)chunk.getAttribute("LOCALDESTINATION"), new PdfDestination(0, xMarker, yMarker + chunk.font().size(), 0.0f));
                    }
                    if (chunk.isAttribute("GENERICTAG")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("GENERICTAG")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        final Rectangle rect = new Rectangle(xMarker, yMarker, xMarker + width2 - subtract, yMarker + chunk.font().size());
                        final PdfPageEvent pev = this.writer.getPageEvent();
                        if (pev != null) {
                            pev.onGenericTag(this.writer, this, rect, (String)chunk.getAttribute("GENERICTAG"));
                        }
                    }
                    if (chunk.isAttribute("PDFANNOTATION")) {
                        float subtract = lastBaseFactor;
                        if (nextChunk != null && nextChunk.isAttribute("PDFANNOTATION")) {
                            subtract = 0.0f;
                        }
                        if (nextChunk == null) {
                            subtract += hangingCorrection;
                        }
                        final float fontSize3 = chunk.font().size();
                        final float ascender3 = chunk.font().getFont().getFontDescriptor(1, fontSize3);
                        final float descender3 = chunk.font().getFont().getFontDescriptor(3, fontSize3);
                        final PdfAnnotation annot = PdfFormField.shallowDuplicate((PdfAnnotation)chunk.getAttribute("PDFANNOTATION"));
                        annot.put(PdfName.RECT, new PdfRectangle(xMarker, yMarker + descender3, xMarker + width2 - subtract, yMarker + ascender3));
                        text.addAnnotation(annot);
                    }
                    final float[] params = (float[])chunk.getAttribute("SKEW");
                    final Float hs = (Float)chunk.getAttribute("HSCALE");
                    if (params != null || hs != null) {
                        float b = 0.0f;
                        float c2 = 0.0f;
                        if (params != null) {
                            b = params[0];
                            c2 = params[1];
                        }
                        if (hs != null) {
                            hScale = hs;
                        }
                        text.setTextMatrix(hScale, b, c2, 1.0f, xMarker, yMarker);
                    }
                    if (chunk.isAttribute("CHAR_SPACING")) {
                        final Float cs = (Float)chunk.getAttribute("CHAR_SPACING");
                        text.setCharacterSpacing(cs);
                    }
                    if (chunk.isImage()) {
                        final Image image = chunk.getImage();
                        final float[] matrix = image.matrix();
                        matrix[4] = xMarker + chunk.getImageOffsetX() - matrix[4];
                        matrix[5] = yMarker + chunk.getImageOffsetY() - matrix[5];
                        graphics.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
                        text.moveText(xMarker + lastBaseFactor + image.getScaledWidth() - text.getXTLM(), 0.0f);
                    }
                }
                xMarker += width2;
                ++chunkStrokeIdx;
            }
            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            float rise = 0.0f;
            final Object[] textRender = (Object[])chunk.getAttribute("TEXTRENDERMODE");
            int tr = 0;
            float strokeWidth = 1.0f;
            Color strokeColor = null;
            final Float fr = (Float)chunk.getAttribute("SUBSUPSCRIPT");
            if (textRender != null) {
                tr = ((int)textRender[0] & 0x3);
                if (tr != 0) {
                    text.setTextRenderingMode(tr);
                }
                if (tr == 1 || tr == 2) {
                    strokeWidth = (float)textRender[1];
                    if (strokeWidth != 1.0f) {
                        text.setLineWidth(strokeWidth);
                    }
                    strokeColor = (Color)textRender[2];
                    if (strokeColor == null) {
                        strokeColor = color;
                    }
                    if (strokeColor != null) {
                        text.setColorStroke(strokeColor);
                    }
                }
            }
            if (fr != null) {
                rise = fr;
            }
            if (color != null) {
                text.setColorFill(color);
            }
            if (rise != 0.0f) {
                text.setTextRise(rise);
            }
            if (chunk.isImage()) {
                adjustMatrix = true;
            }
            else if (chunk.isHorizontalSeparator()) {
                final PdfTextArray array = new PdfTextArray();
                array.add(-glueWidth * 1000.0f / chunk.font.size() / hScale);
                text.showText(array);
            }
            else if (chunk.isTab()) {
                final PdfTextArray array = new PdfTextArray();
                array.add((tabPosition - xMarker) * 1000.0f / chunk.font.size() / hScale);
                text.showText(array);
            }
            else if (isJustified && numberOfSpaces > 0 && chunk.isSpecialEncoding()) {
                if (hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                final String s2 = chunk.toString();
                int idx = s2.indexOf(32);
                if (idx < 0) {
                    text.showText(s2);
                }
                else {
                    final float spaceCorrection = -baseWordSpacing * 1000.0f / chunk.font.size() / hScale;
                    final PdfTextArray textArray = new PdfTextArray(s2.substring(0, idx));
                    int lastIdx;
                    for (lastIdx = idx; (idx = s2.indexOf(32, lastIdx + 1)) >= 0; lastIdx = idx) {
                        textArray.add(spaceCorrection);
                        textArray.add(s2.substring(lastIdx, idx));
                    }
                    textArray.add(spaceCorrection);
                    textArray.add(s2.substring(lastIdx));
                    text.showText(textArray);
                }
            }
            else {
                if (isJustified && hScale != lastHScale) {
                    lastHScale = hScale;
                    text.setWordSpacing(baseWordSpacing / hScale);
                    text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
                }
                text.showText(chunk.toString());
            }
            if (rise != 0.0f) {
                text.setTextRise(0.0f);
            }
            if (color != null) {
                text.resetRGBColorFill();
            }
            if (tr != 0) {
                text.setTextRenderingMode(0);
            }
            if (strokeColor != null) {
                text.resetRGBColorStroke();
            }
            if (strokeWidth != 1.0f) {
                text.setLineWidth(1.0f);
            }
            if (chunk.isAttribute("SKEW") || chunk.isAttribute("HSCALE")) {
                adjustMatrix = true;
                text.setTextMatrix(xMarker, yMarker);
            }
            if (chunk.isAttribute("CHAR_SPACING")) {
                text.setCharacterSpacing(baseCharacterSpacing);
            }
        }
        if (isJustified) {
            text.setWordSpacing(0.0f);
            text.setCharacterSpacing(0.0f);
            if (line.isNewlineSplit()) {
                lastBaseFactor = 0.0f;
            }
        }
        if (adjustMatrix) {
            text.moveText(baseXMarker - text.getXTLM(), 0.0f);
        }
        currentValues[0] = currentFont;
        currentValues[1] = new Float(lastBaseFactor);
    }
    
    protected float indentLeft() {
        return this.left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
    }
    
    protected float indentRight() {
        return this.right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
    }
    
    protected float indentTop() {
        return this.top(this.indentation.indentTop);
    }
    
    float indentBottom() {
        return this.bottom(this.indentation.indentBottom);
    }
    
    protected void addSpacing(final float extraspace, final float oldleading, Font f) {
        if (extraspace == 0.0f) {
            return;
        }
        if (this.pageEmpty) {
            return;
        }
        if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
            return;
        }
        this.leading = extraspace;
        this.carriageReturn();
        if (f.isUnderlined() || f.isStrikethru()) {
            f = new Font(f);
            int style = f.getStyle();
            style &= 0xFFFFFFFB;
            style &= 0xFFFFFFF7;
            f.setStyle(style);
        }
        final Chunk space = new Chunk(" ", f);
        space.process(this);
        this.carriageReturn();
        this.leading = oldleading;
    }
    
    protected PdfInfo getInfo() {
        return this.info;
    }
    
    PdfCatalog getCatalog(final PdfIndirectReference pages) {
        final PdfCatalog catalog = new PdfCatalog(pages, this.writer);
        if (this.rootOutline.getKids().size() > 0) {
            catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
            catalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
        }
        this.writer.getPdfVersion().addToCatalog(catalog);
        this.viewerPreferences.addToCatalog(catalog);
        if (this.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer));
        }
        catalog.addNames(this.localDestinations, this.getDocumentLevelJS(), this.documentFileAttachment, this.writer);
        if (this.openActionName != null) {
            final PdfAction action = this.getLocalGotoAction(this.openActionName);
            catalog.setOpenAction(action);
        }
        else if (this.openActionAction != null) {
            catalog.setOpenAction(this.openActionAction);
        }
        if (this.additionalActions != null) {
            catalog.setAdditionalActions(this.additionalActions);
        }
        if (this.collection != null) {
            catalog.put(PdfName.COLLECTION, this.collection);
        }
        if (this.annotationsImp.hasValidAcroForm()) {
            try {
                catalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
            }
            catch (final IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        return catalog;
    }
    
    void addOutline(final PdfOutline outline, final String name) {
        this.localDestination(name, outline.getPdfDestination());
    }
    
    public PdfOutline getRootOutline() {
        return this.rootOutline;
    }
    
    void calculateOutlineCount() {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.traverseOutlineCount(this.rootOutline);
    }
    
    void traverseOutlineCount(final PdfOutline outline) {
        final java.util.List<PdfOutline> kids = outline.getKids();
        final PdfOutline parent = outline.parent();
        if (kids.isEmpty()) {
            if (parent != null) {
                parent.setCount(parent.getCount() + 1);
            }
        }
        else {
            for (int k = 0; k < kids.size(); ++k) {
                this.traverseOutlineCount(kids.get(k));
            }
            if (parent != null) {
                if (outline.isOpen()) {
                    parent.setCount(outline.getCount() + parent.getCount() + 1);
                }
                else {
                    parent.setCount(parent.getCount() + 1);
                    outline.setCount(-outline.getCount());
                }
            }
        }
    }
    
    void writeOutlines() throws IOException {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.outlineTree(this.rootOutline);
        this.writer.addToBody(this.rootOutline, this.rootOutline.indirectReference());
    }
    
    void outlineTree(final PdfOutline outline) throws IOException {
        outline.setIndirectReference(this.writer.getPdfIndirectReference());
        if (outline.parent() != null) {
            outline.put(PdfName.PARENT, outline.parent().indirectReference());
        }
        final java.util.List<PdfOutline> kids = outline.getKids();
        final int size = kids.size();
        for (int k = 0; k < size; ++k) {
            this.outlineTree(kids.get(k));
        }
        for (int k = 0; k < size; ++k) {
            if (k > 0) {
                kids.get(k).put(PdfName.PREV, kids.get(k - 1).indirectReference());
            }
            if (k < size - 1) {
                kids.get(k).put(PdfName.NEXT, kids.get(k + 1).indirectReference());
            }
        }
        if (size > 0) {
            outline.put(PdfName.FIRST, kids.get(0).indirectReference());
            outline.put(PdfName.LAST, kids.get(size - 1).indirectReference());
        }
        for (int k = 0; k < size; ++k) {
            final PdfOutline kid = kids.get(k);
            this.writer.addToBody(kid, kid.indirectReference());
        }
    }
    
    void setViewerPreferences(final int preferences) {
        this.viewerPreferences.setViewerPreferences(preferences);
    }
    
    void addViewerPreference(final PdfName key, final PdfObject value) {
        this.viewerPreferences.addViewerPreference(key, value);
    }
    
    void setPageLabels(final PdfPageLabels pageLabels) {
        this.pageLabels = pageLabels;
    }
    
    void localGoto(final String name, final float llx, final float lly, final float urx, final float ury) {
        final PdfAction action = this.getLocalGotoAction(name);
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }
    
    void remoteGoto(final String filename, final String name, final float llx, final float lly, final float urx, final float ury) {
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, name)));
    }
    
    void remoteGoto(final String filename, final int page, final float llx, final float lly, final float urx, final float ury) {
        this.addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, page)));
    }
    
    void setAction(final PdfAction action, final float llx, final float lly, final float urx, final float ury) {
        this.addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
    }
    
    PdfAction getLocalGotoAction(final String name) {
        Object[] obj = this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        PdfAction action;
        if (obj[0] == null) {
            if (obj[1] == null) {
                obj[1] = this.writer.getPdfIndirectReference();
            }
            action = new PdfAction((PdfIndirectReference)obj[1]);
            obj[0] = action;
            this.localDestinations.put(name, obj);
        }
        else {
            action = (PdfAction)obj[0];
        }
        return action;
    }
    
    boolean localDestination(final String name, final PdfDestination destination) {
        Object[] obj = this.localDestinations.get(name);
        if (obj == null) {
            obj = new Object[3];
        }
        if (obj[2] != null) {
            return false;
        }
        obj[2] = destination;
        this.localDestinations.put(name, obj);
        if (!destination.hasPage()) {
            destination.addPage(this.writer.getCurrentPage());
        }
        return true;
    }
    
    void addJavaScript(final PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed"));
        }
        try {
            this.documentLevelJS.put(PdfDocument.SIXTEEN_DIGITS.format(this.jsCounter++), this.writer.addToBody(js).getIndirectReference());
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    void addJavaScript(final String name, final PdfAction js) {
        if (js.get(PdfName.JS) == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed"));
        }
        try {
            this.documentLevelJS.put(name, this.writer.addToBody(js).getIndirectReference());
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
    }
    
    HashMap<String, PdfIndirectReference> getDocumentLevelJS() {
        return this.documentLevelJS;
    }
    
    void addFileAttachment(String description, final PdfFileSpecification fs) throws IOException {
        if (description == null) {
            final PdfString desc = (PdfString)fs.get(PdfName.DESC);
            if (desc == null) {
                description = "";
            }
            else {
                description = PdfEncodings.convertToString(desc.getBytes(), null);
            }
        }
        fs.addDescription(description, true);
        if (description.length() == 0) {
            description = "Unnamed";
        }
        String fn = PdfEncodings.convertToString(new PdfString(description, "UnicodeBig").getBytes(), null);
        for (int k = 0; this.documentFileAttachment.containsKey(fn); fn = PdfEncodings.convertToString(new PdfString(description + " " + k, "UnicodeBig").getBytes(), null)) {
            ++k;
        }
        this.documentFileAttachment.put(fn, fs.getReference());
    }
    
    HashMap<String, PdfIndirectReference> getDocumentFileAttachment() {
        return this.documentFileAttachment;
    }
    
    void setOpenAction(final String name) {
        this.openActionName = name;
        this.openActionAction = null;
    }
    
    void setOpenAction(final PdfAction action) {
        this.openActionAction = action;
        this.openActionName = null;
    }
    
    void addAdditionalAction(final PdfName actionType, final PdfAction action) {
        if (this.additionalActions == null) {
            this.additionalActions = new PdfDictionary();
        }
        if (action == null) {
            this.additionalActions.remove(actionType);
        }
        else {
            this.additionalActions.put(actionType, action);
        }
        if (this.additionalActions.size() == 0) {
            this.additionalActions = null;
        }
    }
    
    public void setCollection(final PdfCollection collection) {
        this.collection = collection;
    }
    
    PdfAcroForm getAcroForm() {
        return this.annotationsImp.getAcroForm();
    }
    
    void setSigFlags(final int f) {
        this.annotationsImp.setSigFlags(f);
    }
    
    void addCalculationOrder(final PdfFormField formField) {
        this.annotationsImp.addCalculationOrder(formField);
    }
    
    void addAnnotation(final PdfAnnotation annot) {
        this.pageEmpty = false;
        this.annotationsImp.addAnnotation(annot);
    }
    
    int getMarkPoint() {
        return this.markPoint;
    }
    
    void incMarkPoint() {
        ++this.markPoint;
    }
    
    void setCropBoxSize(final Rectangle crop) {
        this.setBoxSize("crop", crop);
    }
    
    void setBoxSize(final String boxName, final Rectangle size) {
        if (size == null) {
            this.boxSize.remove(boxName);
        }
        else {
            this.boxSize.put(boxName, new PdfRectangle(size));
        }
    }
    
    protected void setNewPageSizeAndMargins() {
        this.pageSize = this.nextPageSize;
        if (this.marginMirroring && (this.getPageNumber() & 0x1) == 0x0) {
            this.marginRight = this.nextMarginLeft;
            this.marginLeft = this.nextMarginRight;
        }
        else {
            this.marginLeft = this.nextMarginLeft;
            this.marginRight = this.nextMarginRight;
        }
        if (this.marginMirroringTopBottom && (this.getPageNumber() & 0x1) == 0x0) {
            this.marginTop = this.nextMarginBottom;
            this.marginBottom = this.nextMarginTop;
        }
        else {
            this.marginTop = this.nextMarginTop;
            this.marginBottom = this.nextMarginBottom;
        }
    }
    
    Rectangle getBoxSize(final String boxName) {
        final PdfRectangle r = this.thisBoxSize.get(boxName);
        if (r != null) {
            return r.getRectangle();
        }
        return null;
    }
    
    void setPageEmpty(final boolean pageEmpty) {
        this.pageEmpty = pageEmpty;
    }
    
    boolean isPageEmpty() {
        return this.writer == null || (this.writer.getDirectContent().size() == 0 && this.writer.getDirectContentUnder().size() == 0 && (this.pageEmpty || this.writer.isPaused()));
    }
    
    void setDuration(final int seconds) {
        if (seconds > 0) {
            this.duration = seconds;
        }
        else {
            this.duration = -1;
        }
    }
    
    void setTransition(final PdfTransition transition) {
        this.transition = transition;
    }
    
    void setPageAction(final PdfName actionType, final PdfAction action) {
        if (this.pageAA == null) {
            this.pageAA = new PdfDictionary();
        }
        this.pageAA.put(actionType, action);
    }
    
    void setThumbnail(final Image image) throws DocumentException {
        this.thumb = this.writer.getImageReference(this.writer.addDirectImageSimple(image));
    }
    
    PageResources getPageResources() {
        return this.pageResources;
    }
    
    boolean isStrictImageSequence() {
        return this.strictImageSequence;
    }
    
    void setStrictImageSequence(final boolean strictImageSequence) {
        this.strictImageSequence = strictImageSequence;
    }
    
    public void clearTextWrap() {
        float tmpHeight = this.imageEnd - this.currentHeight;
        if (this.line != null) {
            tmpHeight += this.line.height();
        }
        if (this.imageEnd > -1.0f && tmpHeight > 0.0f) {
            this.carriageReturn();
            this.currentHeight += tmpHeight;
        }
    }
    
    protected void add(final Image image) throws DocumentException {
        if (image.hasAbsoluteY()) {
            this.graphics.addImage(image);
            this.pageEmpty = false;
            return;
        }
        if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
            if (!this.strictImageSequence && this.imageWait == null) {
                this.imageWait = image;
                return;
            }
            this.newPage();
            if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
                this.imageWait = image;
                return;
            }
        }
        this.pageEmpty = false;
        if (image == this.imageWait) {
            this.imageWait = null;
        }
        final boolean textwrap = (image.getAlignment() & 0x4) == 0x4 && (image.getAlignment() & 0x1) != 0x1;
        final boolean underlying = (image.getAlignment() & 0x8) == 0x8;
        float diff = this.leading / 2.0f;
        if (textwrap) {
            diff += this.leading;
        }
        final float lowerleft = this.indentTop() - this.currentHeight - image.getScaledHeight() - diff;
        final float[] mt = image.matrix();
        float startPosition = this.indentLeft() - mt[4];
        if ((image.getAlignment() & 0x2) == 0x2) {
            startPosition = this.indentRight() - image.getScaledWidth() - mt[4];
        }
        if ((image.getAlignment() & 0x1) == 0x1) {
            startPosition = this.indentLeft() + (this.indentRight() - this.indentLeft() - image.getScaledWidth()) / 2.0f - mt[4];
        }
        if (image.hasAbsoluteX()) {
            startPosition = image.getAbsoluteX();
        }
        if (textwrap) {
            if (this.imageEnd < 0.0f || this.imageEnd < this.currentHeight + image.getScaledHeight() + diff) {
                this.imageEnd = this.currentHeight + image.getScaledHeight() + diff;
            }
            if ((image.getAlignment() & 0x2) == 0x2) {
                final Indentation indentation = this.indentation;
                indentation.imageIndentRight += image.getScaledWidth() + image.getIndentationLeft();
            }
            else {
                final Indentation indentation2 = this.indentation;
                indentation2.imageIndentLeft += image.getScaledWidth() + image.getIndentationRight();
            }
        }
        else if ((image.getAlignment() & 0x2) == 0x2) {
            startPosition -= image.getIndentationRight();
        }
        else if ((image.getAlignment() & 0x1) == 0x1) {
            startPosition += image.getIndentationLeft() - image.getIndentationRight();
        }
        else {
            startPosition += image.getIndentationLeft();
        }
        this.graphics.addImage(image, mt[0], mt[1], mt[2], mt[3], startPosition, lowerleft - mt[5]);
        if (!textwrap && !underlying) {
            this.currentHeight += image.getScaledHeight() + diff;
            this.flushLines();
            this.text.moveText(0.0f, -(image.getScaledHeight() + diff));
            this.newLine();
        }
    }
    
    void addPTable(final PdfPTable ptable) throws DocumentException {
        final ColumnText ct = new ColumnText(this.writer.getDirectContent());
        if (ptable.getKeepTogether() && !this.fitsPage(ptable, 0.0f) && this.currentHeight > 0.0f) {
            this.newPage();
        }
        if (this.currentHeight > 0.0f) {
            final Paragraph p = new Paragraph();
            p.setLeading(0.0f);
            ct.addElement(p);
        }
        ct.addElement(ptable);
        final boolean he = ptable.isHeadersInEvent();
        ptable.setHeadersInEvent(true);
        int loop = 0;
        while (true) {
            ct.setSimpleColumn(this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop() - this.currentHeight);
            final int status = ct.go();
            if ((status & 0x1) != 0x0) {
                this.text.moveText(0.0f, ct.getYLine() - this.indentTop() + this.currentHeight);
                this.currentHeight = this.indentTop() - ct.getYLine();
                break;
            }
            if (this.indentTop() - this.currentHeight == ct.getYLine()) {
                ++loop;
            }
            else {
                loop = 0;
            }
            if (loop == 3) {
                this.add(new Paragraph("ERROR: Infinite table loop"));
                break;
            }
            this.newPage();
        }
        ptable.setHeadersInEvent(he);
    }
    
    boolean fitsPage(final PdfPTable table, final float margin) {
        if (!table.isLockedWidth()) {
            final float totalWidth = (this.indentRight() - this.indentLeft()) * table.getWidthPercentage() / 100.0f;
            table.setTotalWidth(totalWidth);
        }
        this.ensureNewLine();
        return table.getTotalHeight() + ((this.currentHeight > 0.0f) ? table.spacingBefore() : 0.0f) <= this.indentTop() - this.currentHeight - this.indentBottom() - margin;
    }
    
    private void addPdfTable(final Table t) throws DocumentException {
        this.flushLines();
        final PdfTable table = new PdfTable(t, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        final RenderingContext ctx = new RenderingContext();
        ctx.pagetop = this.indentTop();
        ctx.oldHeight = this.currentHeight;
        ctx.cellGraphics = new PdfContentByte(this.writer);
        ctx.rowspanMap = new HashMap<PdfCell, Integer>();
        ctx.table = table;
        final ArrayList headercells = table.getHeaderCells();
        final ArrayList cells = table.getCells();
        final ArrayList rows = this.extractRows(cells, ctx);
        boolean isContinue = false;
        while (!cells.isEmpty()) {
            ctx.lostTableBottom = 0.0f;
            boolean cellsShown = false;
            Iterator iterator = rows.iterator();
            boolean atLeastOneFits = false;
            while (iterator.hasNext()) {
                final ArrayList row = iterator.next();
                this.analyzeRow(rows, ctx);
                this.renderCells(ctx, row, table.hasToFitPageCells() & atLeastOneFits);
                if (!this.mayBeRemoved(row)) {
                    break;
                }
                this.consumeRowspan(row, ctx);
                iterator.remove();
                atLeastOneFits = true;
            }
            cells.clear();
            final Set<PdfCell> opt = new HashSet<PdfCell>();
            iterator = rows.iterator();
            while (iterator.hasNext()) {
                final ArrayList row2 = iterator.next();
                for (final PdfCell cell : row2) {
                    if (!opt.contains(cell)) {
                        cells.add(cell);
                        opt.add(cell);
                    }
                }
            }
            Rectangle tablerec = new Rectangle(table);
            tablerec.setBorder(table.getBorder());
            tablerec.setBorderWidth(table.getBorderWidth());
            tablerec.setBorderColor(table.getBorderColor());
            tablerec.setBackgroundColor(table.getBackgroundColor());
            final PdfContentByte under = this.writer.getDirectContentUnder();
            under.rectangle(tablerec.rectangle(this.top(), this.indentBottom()));
            under.add(ctx.cellGraphics);
            tablerec.setBackgroundColor(null);
            tablerec = tablerec.rectangle(this.top(), this.indentBottom());
            tablerec.setBorder(table.getBorder());
            under.rectangle(tablerec);
            ctx.cellGraphics = new PdfContentByte(null);
            if (!rows.isEmpty()) {
                isContinue = true;
                this.graphics.setLineWidth(table.getBorderWidth());
                if (cellsShown && (table.getBorder() & 0x2) == 0x2) {
                    final Color tColor = table.getBorderColor();
                    if (tColor != null) {
                        this.graphics.setColorStroke(tColor);
                    }
                    this.graphics.moveTo(table.getLeft(), Math.max(table.getBottom(), this.indentBottom()));
                    this.graphics.lineTo(table.getRight(), Math.max(table.getBottom(), this.indentBottom()));
                    this.graphics.stroke();
                    if (tColor != null) {
                        this.graphics.resetRGBColorStroke();
                    }
                }
                this.pageEmpty = false;
                float difference = ctx.lostTableBottom;
                this.newPage();
                float heightCorrection = 0.0f;
                boolean somethingAdded = false;
                if (this.currentHeight > 0.0f) {
                    heightCorrection = 6.0f;
                    this.currentHeight += heightCorrection;
                    somethingAdded = true;
                    this.newLine();
                    this.flushLines();
                    this.indentation.indentTop = this.currentHeight - this.leading;
                    this.currentHeight = 0.0f;
                }
                else {
                    this.flushLines();
                }
                int size = headercells.size();
                if (size > 0) {
                    PdfCell cell = headercells.get(0);
                    final float oldTop = cell.getTop(0.0f);
                    for (int i = 0; i < size; ++i) {
                        cell = headercells.get(i);
                        cell.setTop(this.indentTop() - oldTop + cell.getTop(0.0f));
                        cell.setBottom(this.indentTop() - oldTop + cell.getBottom(0.0f));
                        ctx.pagetop = cell.getBottom();
                        ctx.cellGraphics.rectangle(cell.rectangle(this.indentTop(), this.indentBottom()));
                        final ArrayList images = cell.getImages(this.indentTop(), this.indentBottom());
                        final Iterator im = images.iterator();
                        while (im.hasNext()) {
                            cellsShown = true;
                            final Image image = im.next();
                            this.graphics.addImage(image);
                        }
                        this.lines = cell.getLines(this.indentTop(), this.indentBottom());
                        final float cellTop = cell.getTop(this.indentTop());
                        this.text.moveText(0.0f, cellTop - heightCorrection);
                        final float cellDisplacement = this.flushLines() - cellTop + heightCorrection;
                        this.text.moveText(0.0f, cellDisplacement);
                    }
                    this.currentHeight = this.indentTop() - ctx.pagetop + table.cellspacing();
                    this.text.moveText(0.0f, ctx.pagetop - this.indentTop() - this.currentHeight);
                }
                else if (somethingAdded) {
                    ctx.pagetop = this.indentTop();
                    this.text.moveText(0.0f, -table.cellspacing());
                }
                ctx.oldHeight = this.currentHeight - heightCorrection;
                size = Math.min(cells.size(), table.columns());
                for (int j = 0; j < size; ++j) {
                    final PdfCell cell = cells.get(j);
                    if (cell.getTop(-table.cellspacing()) > ctx.lostTableBottom) {
                        final float newBottom = ctx.pagetop - difference + cell.getBottom();
                        final float neededHeight = cell.remainingHeight();
                        if (newBottom > ctx.pagetop - neededHeight) {
                            difference += newBottom - (ctx.pagetop - neededHeight);
                        }
                    }
                }
                size = cells.size();
                table.setTop(this.indentTop());
                table.setBottom(ctx.pagetop - difference + table.getBottom(table.cellspacing()));
                for (int j = 0; j < size; ++j) {
                    final PdfCell cell = cells.get(j);
                    final float newBottom = ctx.pagetop - difference + cell.getBottom();
                    float newTop = ctx.pagetop - difference + cell.getTop(-table.cellspacing());
                    if (newTop > this.indentTop() - this.currentHeight) {
                        newTop = this.indentTop() - this.currentHeight;
                    }
                    cell.setTop(newTop);
                    cell.setBottom(newBottom);
                }
            }
        }
        final float tableHeight = table.getTop() - table.getBottom();
        if (isContinue) {
            this.currentHeight = tableHeight;
            this.text.moveText(0.0f, -(tableHeight - ctx.oldHeight * 2.0f));
        }
        else {
            this.currentHeight = ctx.oldHeight + tableHeight;
            this.text.moveText(0.0f, -tableHeight);
        }
        this.pageEmpty = false;
    }
    
    protected void analyzeRow(final ArrayList rows, final RenderingContext ctx) {
        ctx.maxCellBottom = this.indentBottom();
        int rowIndex = 0;
        ArrayList row = rows.get(rowIndex);
        int maxRowspan = 1;
        for (final PdfCell cell : row) {
            maxRowspan = Math.max(ctx.currentRowspan(cell), maxRowspan);
        }
        rowIndex += maxRowspan;
        boolean useTop = true;
        if (rowIndex == rows.size()) {
            rowIndex = rows.size() - 1;
            useTop = false;
        }
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return;
        }
        row = rows.get(rowIndex);
        for (final PdfCell cell2 : row) {
            final Rectangle cellRect = cell2.rectangle(ctx.pagetop, this.indentBottom());
            if (useTop) {
                ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getTop());
            }
            else {
                if (ctx.currentRowspan(cell2) != 1) {
                    continue;
                }
                ctx.maxCellBottom = Math.max(ctx.maxCellBottom, cellRect.getBottom());
            }
        }
    }
    
    protected boolean mayBeRemoved(final ArrayList row) {
        final Iterator iterator = row.iterator();
        boolean mayBeRemoved = true;
        while (iterator.hasNext()) {
            final PdfCell cell = iterator.next();
            mayBeRemoved &= cell.mayBeRemoved();
        }
        return mayBeRemoved;
    }
    
    protected void consumeRowspan(final ArrayList row, final RenderingContext ctx) {
        for (final PdfCell c : row) {
            ctx.consumeRowspan(c);
        }
    }
    
    protected ArrayList extractRows(final ArrayList cells, final RenderingContext ctx) {
        PdfCell previousCell = null;
        final ArrayList rows = new ArrayList();
        java.util.List<PdfCell> rowCells = new ArrayList<PdfCell>();
        final Iterator iterator = cells.iterator();
        while (iterator.hasNext()) {
            final PdfCell cell = iterator.next();
            boolean isAdded = false;
            boolean isEndOfRow = !iterator.hasNext();
            boolean isCurrentCellPartOfRow = !iterator.hasNext();
            if (previousCell != null && cell.getLeft() <= previousCell.getLeft()) {
                isEndOfRow = true;
                isCurrentCellPartOfRow = false;
            }
            if (isCurrentCellPartOfRow) {
                rowCells.add(cell);
                isAdded = true;
            }
            if (isEndOfRow) {
                if (!rowCells.isEmpty()) {
                    rows.add(rowCells);
                }
                rowCells = new ArrayList<PdfCell>();
            }
            if (!isAdded) {
                rowCells.add(cell);
            }
            previousCell = cell;
        }
        if (!rowCells.isEmpty()) {
            rows.add(rowCells);
        }
        for (int i = rows.size() - 1; i >= 0; --i) {
            final ArrayList row = rows.get(i);
            for (int j = 0; j < row.size(); ++j) {
                final PdfCell c = row.get(j);
                for (int rowspan = c.rowspan(), k = 1; k < rowspan && rows.size() < i + k; ++k) {
                    final ArrayList spannedRow = rows.get(i + k);
                    if (spannedRow.size() > j) {
                        spannedRow.add(j, c);
                    }
                }
            }
        }
        return rows;
    }
    
    protected void renderCells(final RenderingContext ctx, final java.util.List cells, final boolean hasToFit) throws DocumentException {
        if (hasToFit) {
            for (final PdfCell cell : cells) {
                if (!cell.isHeader() && cell.getBottom() < this.indentBottom()) {
                    return;
                }
            }
        }
        for (final PdfCell cell : cells) {
            if (!ctx.isCellRenderedOnPage(cell, this.getPageNumber())) {
                float correction = 0.0f;
                if (ctx.numCellRendered(cell) >= 1) {
                    correction = 1.0f;
                }
                this.lines = cell.getLines(ctx.pagetop, this.indentBottom() - correction);
                if (this.lines != null && !this.lines.isEmpty()) {
                    final float cellTop = cell.getTop(ctx.pagetop - ctx.oldHeight);
                    this.text.moveText(0.0f, cellTop);
                    final float cellDisplacement = this.flushLines() - cellTop;
                    this.text.moveText(0.0f, cellDisplacement);
                    if (ctx.oldHeight + cellDisplacement > this.currentHeight) {
                        this.currentHeight = ctx.oldHeight + cellDisplacement;
                    }
                    ctx.cellRendered(cell, this.getPageNumber());
                }
                float indentBottom = Math.max(cell.getBottom(), this.indentBottom());
                final Rectangle tableRect = ctx.table.rectangle(ctx.pagetop, this.indentBottom());
                indentBottom = Math.max(tableRect.getBottom(), indentBottom);
                final Rectangle cellRect = cell.rectangle(tableRect.getTop(), indentBottom);
                if (cellRect.getHeight() > 0.0f) {
                    ctx.lostTableBottom = indentBottom;
                    ctx.cellGraphics.rectangle(cellRect);
                }
                final ArrayList images = cell.getImages(ctx.pagetop, this.indentBottom());
                for (final Image image : images) {
                    this.graphics.addImage(image);
                }
            }
        }
    }
    
    float bottom(final Table table) {
        final PdfTable tmp = new PdfTable(table, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        return tmp.getBottom();
    }
    
    protected void doFooter() throws DocumentException {
        if (this.footer == null) {
            return;
        }
        final float tmpIndentLeft = this.indentation.indentLeft;
        final float tmpIndentRight = this.indentation.indentRight;
        final float tmpListIndentLeft = this.indentation.listIndentLeft;
        final float tmpImageIndentLeft = this.indentation.imageIndentLeft;
        final float tmpImageIndentRight = this.indentation.imageIndentRight;
        final Indentation indentation = this.indentation;
        final Indentation indentation2 = this.indentation;
        final float n = 0.0f;
        indentation2.indentRight = n;
        indentation.indentLeft = n;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.footer.setPageNumber(this.pageN);
        this.leading = this.footer.paragraph().getTotalLeading();
        this.add(this.footer.paragraph());
        this.indentation.indentBottom = this.currentHeight;
        this.text.moveText(this.left(), this.indentBottom());
        this.flushLines();
        this.text.moveText(-this.left(), -this.bottom());
        this.footer.setTop(this.bottom(this.currentHeight));
        this.footer.setBottom(this.bottom() - 0.75f * this.leading);
        this.footer.setLeft(this.left());
        this.footer.setRight(this.right());
        this.graphics.rectangle(this.footer);
        this.indentation.indentBottom = this.currentHeight + this.leading * 2.0f;
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = tmpIndentLeft;
        this.indentation.indentRight = tmpIndentRight;
        this.indentation.listIndentLeft = tmpListIndentLeft;
        this.indentation.imageIndentLeft = tmpImageIndentLeft;
        this.indentation.imageIndentRight = tmpImageIndentRight;
    }
    
    protected void doHeader() throws DocumentException {
        if (this.header == null) {
            return;
        }
        final float tmpIndentLeft = this.indentation.indentLeft;
        final float tmpIndentRight = this.indentation.indentRight;
        final float tmpListIndentLeft = this.indentation.listIndentLeft;
        final float tmpImageIndentLeft = this.indentation.imageIndentLeft;
        final float tmpImageIndentRight = this.indentation.imageIndentRight;
        final Indentation indentation = this.indentation;
        final Indentation indentation2 = this.indentation;
        final float n = 0.0f;
        indentation2.indentRight = n;
        indentation.indentLeft = n;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.header.setPageNumber(this.pageN);
        this.leading = this.header.paragraph().getTotalLeading();
        this.text.moveText(0.0f, this.leading);
        this.add(this.header.paragraph());
        this.newLine();
        this.indentation.indentTop = this.currentHeight - this.leading;
        this.header.setTop(this.top() + this.leading);
        this.header.setBottom(this.indentTop() + this.leading * 2.0f / 3.0f);
        this.header.setLeft(this.left());
        this.header.setRight(this.right());
        this.graphics.rectangle(this.header);
        this.flushLines();
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = tmpIndentLeft;
        this.indentation.indentRight = tmpIndentRight;
        this.indentation.listIndentLeft = tmpListIndentLeft;
        this.indentation.imageIndentLeft = tmpImageIndentLeft;
        this.indentation.imageIndentRight = tmpImageIndentRight;
    }
    
    static {
        SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
    }
    
    public static class PdfInfo extends PdfDictionary
    {
        PdfInfo() {
            this.addProducer();
            this.addCreationDate();
        }
        
        PdfInfo(final String author, final String title, final String subject) {
            this();
            this.addTitle(title);
            this.addSubject(subject);
            this.addAuthor(author);
        }
        
        void addTitle(final String title) {
            this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
        }
        
        void addSubject(final String subject) {
            this.put(PdfName.SUBJECT, new PdfString(subject, "UnicodeBig"));
        }
        
        void addKeywords(final String keywords) {
            this.put(PdfName.KEYWORDS, new PdfString(keywords, "UnicodeBig"));
        }
        
        void addAuthor(final String author) {
            this.put(PdfName.AUTHOR, new PdfString(author, "UnicodeBig"));
        }
        
        void addCreator(final String creator) {
            this.put(PdfName.CREATOR, new PdfString(creator, "UnicodeBig"));
        }
        
        void addProducer() {
            this.addProducer(Document.getVersion());
        }
        
        void addProducer(final String producer) {
            this.put(PdfName.PRODUCER, new PdfString(producer));
        }
        
        void addCreationDate() {
            final PdfString date = new PdfDate();
            this.put(PdfName.CREATIONDATE, date);
            this.put(PdfName.MODDATE, date);
        }
        
        void addkey(final String key, final String value) {
            if (key.equals("Producer") || key.equals("CreationDate")) {
                return;
            }
            this.put(new PdfName(key), new PdfString(value, "UnicodeBig"));
        }
    }
    
    static class PdfCatalog extends PdfDictionary
    {
        PdfWriter writer;
        
        PdfCatalog(final PdfIndirectReference pages, final PdfWriter writer) {
            super(PdfCatalog.CATALOG);
            this.writer = writer;
            this.put(PdfName.PAGES, pages);
        }
        
        void addNames(final TreeMap<String, Object[]> localDestinations, final HashMap<String, PdfIndirectReference> documentLevelJS, final HashMap<String, PdfIndirectReference> documentFileAttachment, final PdfWriter writer) {
            if (localDestinations.isEmpty() && documentLevelJS.isEmpty() && documentFileAttachment.isEmpty()) {
                return;
            }
            try {
                final PdfDictionary names = new PdfDictionary();
                if (!localDestinations.isEmpty()) {
                    final PdfArray ar = new PdfArray();
                    for (final Map.Entry<String, Object[]> entry : localDestinations.entrySet()) {
                        final String name = entry.getKey();
                        final Object[] obj = entry.getValue();
                        if (obj[2] == null) {
                            continue;
                        }
                        final PdfIndirectReference ref = (PdfIndirectReference)obj[1];
                        ar.add(new PdfString(name, null));
                        ar.add(ref);
                    }
                    if (ar.size() > 0) {
                        final PdfDictionary dests = new PdfDictionary();
                        dests.put(PdfName.NAMES, ar);
                        names.put(PdfName.DESTS, writer.addToBody(dests).getIndirectReference());
                    }
                }
                if (!documentLevelJS.isEmpty()) {
                    final PdfDictionary tree = PdfNameTree.writeTree(documentLevelJS, writer);
                    names.put(PdfName.JAVASCRIPT, writer.addToBody(tree).getIndirectReference());
                }
                if (!documentFileAttachment.isEmpty()) {
                    names.put(PdfName.EMBEDDEDFILES, writer.addToBody(PdfNameTree.writeTree(documentFileAttachment, writer)).getIndirectReference());
                }
                if (names.size() > 0) {
                    this.put(PdfName.NAMES, writer.addToBody(names).getIndirectReference());
                }
            }
            catch (final IOException e) {
                throw new ExceptionConverter(e);
            }
        }
        
        void setOpenAction(final PdfAction action) {
            this.put(PdfName.OPENACTION, action);
        }
        
        void setAdditionalActions(final PdfDictionary actions) {
            try {
                this.put(PdfName.AA, this.writer.addToBody(actions).getIndirectReference());
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    public static class Indentation
    {
        float indentLeft;
        float sectionIndentLeft;
        float listIndentLeft;
        float imageIndentLeft;
        float indentRight;
        float sectionIndentRight;
        float imageIndentRight;
        float indentTop;
        float indentBottom;
        
        public Indentation() {
            this.indentLeft = 0.0f;
            this.sectionIndentLeft = 0.0f;
            this.listIndentLeft = 0.0f;
            this.imageIndentLeft = 0.0f;
            this.indentRight = 0.0f;
            this.sectionIndentRight = 0.0f;
            this.imageIndentRight = 0.0f;
            this.indentTop = 0.0f;
            this.indentBottom = 0.0f;
        }
    }
    
    protected static class RenderingContext
    {
        float pagetop;
        float oldHeight;
        PdfContentByte cellGraphics;
        float lostTableBottom;
        float maxCellBottom;
        float maxCellHeight;
        Map<PdfCell, Integer> rowspanMap;
        Map<Object, Object> pageMap;
        public PdfTable table;
        
        protected RenderingContext() {
            this.pagetop = -1.0f;
            this.oldHeight = -1.0f;
            this.cellGraphics = null;
            this.rowspanMap = new HashMap<PdfCell, Integer>();
            this.pageMap = new HashMap<Object, Object>();
        }
        
        public int consumeRowspan(final PdfCell c) {
            if (c.rowspan() == 1) {
                return 1;
            }
            Integer i = this.rowspanMap.get(c);
            if (i == null) {
                i = new Integer(c.rowspan());
            }
            i = new Integer(i - 1);
            this.rowspanMap.put(c, i);
            if (i < 1) {
                return 1;
            }
            return i;
        }
        
        public int currentRowspan(final PdfCell c) {
            final Integer i = this.rowspanMap.get(c);
            if (i == null) {
                return c.rowspan();
            }
            return i;
        }
        
        public int cellRendered(final PdfCell cell, final int pageNumber) {
            Integer i = this.pageMap.get(cell);
            if (i == null) {
                i = 1;
            }
            else {
                ++i;
            }
            this.pageMap.put(cell, i);
            final Integer pageInteger = pageNumber;
            Set set = this.pageMap.get(pageInteger);
            if (set == null) {
                set = new HashSet();
                this.pageMap.put(pageInteger, set);
            }
            set.add(cell);
            return i;
        }
        
        public int numCellRendered(final PdfCell cell) {
            Integer i = this.pageMap.get(cell);
            if (i == null) {
                i = 0;
            }
            return i;
        }
        
        public boolean isCellRenderedOnPage(final PdfCell cell, final int pageNumber) {
            final Integer pageInteger = pageNumber;
            final Set set = this.pageMap.get(pageInteger);
            return set != null && set.contains(cell);
        }
    }
}
