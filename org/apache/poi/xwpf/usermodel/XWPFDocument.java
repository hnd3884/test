package org.apache.poi.xwpf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import java.util.Arrays;
import java.util.Collection;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.util.LinkedList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Collections;
import org.apache.poi.util.Internal;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CommentsDocument;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.DocumentDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.ooxml.POIXMLException;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ooxml.util.PackageHelper;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.ooxml.POIXMLFactory;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.ooxml.util.IdentifierManager;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import java.util.Map;
import java.util.List;
import org.apache.poi.util.POILogger;
import org.apache.poi.ooxml.POIXMLDocument;

public class XWPFDocument extends POIXMLDocument implements Document, IBody
{
    private static final POILogger LOG;
    protected List<XWPFFooter> footers;
    protected List<XWPFHeader> headers;
    protected List<XWPFComment> comments;
    protected List<XWPFHyperlink> hyperlinks;
    protected List<XWPFParagraph> paragraphs;
    protected List<XWPFTable> tables;
    protected List<XWPFSDT> contentControls;
    protected List<IBodyElement> bodyElements;
    protected List<XWPFPictureData> pictures;
    protected Map<Long, List<XWPFPictureData>> packagePictures;
    protected XWPFEndnotes endnotes;
    protected XWPFNumbering numbering;
    protected XWPFStyles styles;
    protected XWPFFootnotes footnotes;
    private CTDocument1 ctDocument;
    private XWPFSettings settings;
    protected final List<XWPFChart> charts;
    private IdentifierManager drawingIdManager;
    private FootnoteEndnoteIdManager footnoteIdManager;
    private XWPFHeaderFooterPolicy headerFooterPolicy;
    
    public XWPFDocument(final OPCPackage pkg) throws IOException {
        super(pkg);
        this.footers = new ArrayList<XWPFFooter>();
        this.headers = new ArrayList<XWPFHeader>();
        this.comments = new ArrayList<XWPFComment>();
        this.hyperlinks = new ArrayList<XWPFHyperlink>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.contentControls = new ArrayList<XWPFSDT>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.packagePictures = new HashMap<Long, List<XWPFPictureData>>();
        this.charts = new ArrayList<XWPFChart>();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        this.footnoteIdManager = new FootnoteEndnoteIdManager(this);
        this.load(XWPFFactory.getInstance());
    }
    
    public XWPFDocument(final InputStream is) throws IOException {
        super(PackageHelper.open(is));
        this.footers = new ArrayList<XWPFFooter>();
        this.headers = new ArrayList<XWPFHeader>();
        this.comments = new ArrayList<XWPFComment>();
        this.hyperlinks = new ArrayList<XWPFHyperlink>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.contentControls = new ArrayList<XWPFSDT>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.packagePictures = new HashMap<Long, List<XWPFPictureData>>();
        this.charts = new ArrayList<XWPFChart>();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        this.footnoteIdManager = new FootnoteEndnoteIdManager(this);
        this.load(XWPFFactory.getInstance());
    }
    
    public XWPFDocument() {
        super(newPackage());
        this.footers = new ArrayList<XWPFFooter>();
        this.headers = new ArrayList<XWPFHeader>();
        this.comments = new ArrayList<XWPFComment>();
        this.hyperlinks = new ArrayList<XWPFHyperlink>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.contentControls = new ArrayList<XWPFSDT>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.packagePictures = new HashMap<Long, List<XWPFPictureData>>();
        this.charts = new ArrayList<XWPFChart>();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        this.footnoteIdManager = new FootnoteEndnoteIdManager(this);
        this.onDocumentCreate();
    }
    
    protected static OPCPackage newPackage() {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.create(new ByteArrayOutputStream());
            final PackagePartName corePartName = PackagingURIHelper.createPartName(XWPFRelation.DOCUMENT.getDefaultFileName());
            pkg.addRelationship(corePartName, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
            pkg.createPart(corePartName, XWPFRelation.DOCUMENT.getContentType());
            pkg.getPackageProperties().setCreatorProperty("Apache POI");
            return pkg;
        }
        catch (final Exception e) {
            IOUtils.closeQuietly((Closeable)pkg);
            throw new POIXMLException(e);
        }
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        try {
            final DocumentDocument doc = DocumentDocument.Factory.parse(this.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctDocument = doc.getDocument();
            this.initFootnotes();
            final XmlCursor docCursor = this.ctDocument.newCursor();
            docCursor.selectPath("./*");
            while (docCursor.toNextSelection()) {
                final XmlObject o = docCursor.getObject();
                if (o instanceof CTBody) {
                    final XmlCursor bodyCursor = o.newCursor();
                    bodyCursor.selectPath("./*");
                    while (bodyCursor.toNextSelection()) {
                        final XmlObject bodyObj = bodyCursor.getObject();
                        if (bodyObj instanceof CTP) {
                            final XWPFParagraph p = new XWPFParagraph((CTP)bodyObj, this);
                            this.bodyElements.add(p);
                            this.paragraphs.add(p);
                        }
                        else if (bodyObj instanceof CTTbl) {
                            final XWPFTable t = new XWPFTable((CTTbl)bodyObj, this);
                            this.bodyElements.add(t);
                            this.tables.add(t);
                        }
                        else {
                            if (!(bodyObj instanceof CTSdtBlock)) {
                                continue;
                            }
                            final XWPFSDT c = new XWPFSDT((CTSdtBlock)bodyObj, this);
                            this.bodyElements.add(c);
                            this.contentControls.add(c);
                        }
                    }
                    bodyCursor.dispose();
                }
            }
            docCursor.dispose();
            if (doc.getDocument().getBody().getSectPr() != null) {
                this.headerFooterPolicy = new XWPFHeaderFooterPolicy(this);
            }
            for (final RelationPart rp : this.getRelationParts()) {
                final POIXMLDocumentPart p2 = rp.getDocumentPart();
                final String relation = rp.getRelationship().getRelationshipType();
                if (relation.equals(XWPFRelation.STYLES.getRelation())) {
                    (this.styles = (XWPFStyles)p2).onDocumentRead();
                }
                else if (relation.equals(XWPFRelation.NUMBERING.getRelation())) {
                    (this.numbering = (XWPFNumbering)p2).onDocumentRead();
                }
                else if (relation.equals(XWPFRelation.FOOTER.getRelation())) {
                    final XWPFFooter footer = (XWPFFooter)p2;
                    this.footers.add(footer);
                    footer.onDocumentRead();
                }
                else if (relation.equals(XWPFRelation.HEADER.getRelation())) {
                    final XWPFHeader header = (XWPFHeader)p2;
                    this.headers.add(header);
                    header.onDocumentRead();
                }
                else if (relation.equals(XWPFRelation.COMMENT.getRelation())) {
                    final CommentsDocument cmntdoc = CommentsDocument.Factory.parse(p2.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    for (final CTComment ctcomment : cmntdoc.getComments().getCommentArray()) {
                        this.comments.add(new XWPFComment(ctcomment, this));
                    }
                }
                else if (relation.equals(XWPFRelation.SETTINGS.getRelation())) {
                    (this.settings = (XWPFSettings)p2).onDocumentRead();
                }
                else if (relation.equals(XWPFRelation.IMAGES.getRelation())) {
                    final XWPFPictureData picData = (XWPFPictureData)p2;
                    picData.onDocumentRead();
                    this.registerPackagePictureData(picData);
                    this.pictures.add(picData);
                }
                else if (relation.equals(XWPFRelation.CHART.getRelation())) {
                    final XWPFChart chartData = (XWPFChart)p2;
                    this.charts.add(chartData);
                }
                else {
                    if (!relation.equals(XWPFRelation.GLOSSARY_DOCUMENT.getRelation())) {
                        continue;
                    }
                    for (final POIXMLDocumentPart gp : p2.getRelations()) {
                        POIXMLDocumentPart._invokeOnDocumentRead(gp);
                    }
                }
            }
            this.initHyperlinks();
        }
        catch (final XmlException e) {
            throw new POIXMLException((Throwable)e);
        }
    }
    
    private void initHyperlinks() {
        try {
            this.hyperlinks = new ArrayList<XWPFHyperlink>();
            for (final PackageRelationship rel : this.getPackagePart().getRelationshipsByType(XWPFRelation.HYPERLINK.getRelation())) {
                this.hyperlinks.add(new XWPFHyperlink(rel.getId(), rel.getTargetURI().toString()));
            }
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }
    
    private void initFootnotes() throws XmlException, IOException {
        for (final RelationPart rp : this.getRelationParts()) {
            final POIXMLDocumentPart p = rp.getDocumentPart();
            final String relation = rp.getRelationship().getRelationshipType();
            if (relation.equals(XWPFRelation.FOOTNOTE.getRelation())) {
                (this.footnotes = (XWPFFootnotes)p).onDocumentRead();
                this.footnotes.setIdManager(this.footnoteIdManager);
            }
            else {
                if (!relation.equals(XWPFRelation.ENDNOTE.getRelation())) {
                    continue;
                }
                (this.endnotes = (XWPFEndnotes)p).onDocumentRead();
                this.endnotes.setIdManager(this.footnoteIdManager);
            }
        }
    }
    
    @Override
    protected void onDocumentCreate() {
        (this.ctDocument = CTDocument1.Factory.newInstance()).addNewBody();
        this.settings = (XWPFSettings)this.createRelationship(XWPFRelation.SETTINGS, XWPFFactory.getInstance());
        final POIXMLProperties.ExtendedProperties expProps = this.getProperties().getExtendedProperties();
        expProps.getUnderlyingProperties().setApplication("Apache POI");
    }
    
    @Internal
    public CTDocument1 getDocument() {
        return this.ctDocument;
    }
    
    IdentifierManager getDrawingIdManager() {
        return this.drawingIdManager;
    }
    
    @Override
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList((List<? extends IBodyElement>)this.bodyElements);
    }
    
    public Iterator<IBodyElement> getBodyElementsIterator() {
        return this.bodyElements.iterator();
    }
    
    @Override
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList((List<? extends XWPFParagraph>)this.paragraphs);
    }
    
    @Override
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList((List<? extends XWPFTable>)this.tables);
    }
    
    public List<XWPFChart> getCharts() {
        return Collections.unmodifiableList((List<? extends XWPFChart>)this.charts);
    }
    
    @Override
    public XWPFTable getTableArray(final int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }
    
    public List<XWPFFooter> getFooterList() {
        return Collections.unmodifiableList((List<? extends XWPFFooter>)this.footers);
    }
    
    public XWPFFooter getFooterArray(final int pos) {
        if (pos >= 0 && pos < this.footers.size()) {
            return this.footers.get(pos);
        }
        return null;
    }
    
    public List<XWPFHeader> getHeaderList() {
        return Collections.unmodifiableList((List<? extends XWPFHeader>)this.headers);
    }
    
    public XWPFHeader getHeaderArray(final int pos) {
        if (pos >= 0 && pos < this.headers.size()) {
            return this.headers.get(pos);
        }
        return null;
    }
    
    public String getTblStyle(final XWPFTable table) {
        return table.getStyleID();
    }
    
    public XWPFHyperlink getHyperlinkByID(final String id) {
        for (final XWPFHyperlink link : this.hyperlinks) {
            if (link.getId().equals(id)) {
                return link;
            }
        }
        this.initHyperlinks();
        for (final XWPFHyperlink link : this.hyperlinks) {
            if (link.getId().equals(id)) {
                return link;
            }
        }
        return null;
    }
    
    public XWPFFootnote getFootnoteByID(final int id) {
        if (this.footnotes == null) {
            return null;
        }
        return (XWPFFootnote)this.footnotes.getFootnoteById(id);
    }
    
    public XWPFEndnote getEndnoteByID(final int id) {
        if (this.endnotes == null) {
            return null;
        }
        return this.endnotes.getFootnoteById(id);
    }
    
    public List<XWPFFootnote> getFootnotes() {
        if (this.footnotes == null) {
            return Collections.emptyList();
        }
        return this.footnotes.getFootnotesList();
    }
    
    public XWPFHyperlink[] getHyperlinks() {
        return this.hyperlinks.toArray(new XWPFHyperlink[0]);
    }
    
    public XWPFComment getCommentByID(final String id) {
        for (final XWPFComment comment : this.comments) {
            if (comment.getId().equals(id)) {
                return comment;
            }
        }
        return null;
    }
    
    public XWPFComment[] getComments() {
        return this.comments.toArray(new XWPFComment[0]);
    }
    
    public PackagePart getPartById(final String id) {
        try {
            final PackagePart corePart = this.getCorePart();
            return corePart.getRelatedPart(corePart.getRelationship(id));
        }
        catch (final InvalidFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public XWPFHeaderFooterPolicy getHeaderFooterPolicy() {
        return this.headerFooterPolicy;
    }
    
    public XWPFHeaderFooterPolicy createHeaderFooterPolicy() {
        if (this.headerFooterPolicy == null) {
            this.headerFooterPolicy = new XWPFHeaderFooterPolicy(this);
        }
        return this.headerFooterPolicy;
    }
    
    public XWPFHeader createHeader(final HeaderFooterType type) {
        final XWPFHeaderFooterPolicy hfPolicy = this.createHeaderFooterPolicy();
        if (type == HeaderFooterType.FIRST) {
            final CTSectPr ctSectPr = this.getSection();
            if (!ctSectPr.isSetTitlePg()) {
                final CTOnOff titlePg = ctSectPr.addNewTitlePg();
                titlePg.setVal(STOnOff.ON);
            }
        }
        return hfPolicy.createHeader(STHdrFtr.Enum.forInt(type.toInt()));
    }
    
    public XWPFFooter createFooter(final HeaderFooterType type) {
        final XWPFHeaderFooterPolicy hfPolicy = this.createHeaderFooterPolicy();
        if (type == HeaderFooterType.FIRST) {
            final CTSectPr ctSectPr = this.getSection();
            if (!ctSectPr.isSetTitlePg()) {
                final CTOnOff titlePg = ctSectPr.addNewTitlePg();
                titlePg.setVal(STOnOff.ON);
            }
        }
        return hfPolicy.createFooter(STHdrFtr.Enum.forInt(type.toInt()));
    }
    
    private CTSectPr getSection() {
        final CTBody ctBody = this.getDocument().getBody();
        return ctBody.isSetSectPr() ? ctBody.getSectPr() : ctBody.addNewSectPr();
    }
    
    @Internal
    public CTStyles getStyle() throws XmlException, IOException {
        PackagePart[] parts;
        try {
            parts = this.getRelatedByType(XWPFRelation.STYLES.getRelation());
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
        if (parts.length != 1) {
            throw new IllegalStateException("Expecting one Styles document part, but found " + parts.length);
        }
        final StylesDocument sd = StylesDocument.Factory.parse(parts[0].getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        return sd.getStyles();
    }
    
    @Override
    public List<PackagePart> getAllEmbeddedParts() throws OpenXML4JException {
        final List<PackagePart> embedds = new LinkedList<PackagePart>();
        final PackagePart part = this.getPackagePart();
        for (final PackageRelationship rel : this.getPackagePart().getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject")) {
            embedds.add(part.getRelatedPart(rel));
        }
        for (final PackageRelationship rel : this.getPackagePart().getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package")) {
            embedds.add(part.getRelatedPart(rel));
        }
        return embedds;
    }
    
    private int getBodyElementSpecificPos(final int pos, final List<? extends IBodyElement> list) {
        if (list.size() == 0) {
            return -1;
        }
        if (pos >= 0 && pos < this.bodyElements.size()) {
            final IBodyElement needle = this.bodyElements.get(pos);
            if (needle.getElementType() != ((IBodyElement)list.get(0)).getElementType()) {
                return -1;
            }
            int i;
            for (int startPos = i = Math.min(pos, list.size() - 1); i >= 0; --i) {
                if (list.get(i) == needle) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getParagraphPos(final int pos) {
        return this.getBodyElementSpecificPos(pos, this.paragraphs);
    }
    
    public int getTablePos(final int pos) {
        return this.getBodyElementSpecificPos(pos, this.tables);
    }
    
    @Override
    public XWPFParagraph insertNewParagraph(final XmlCursor cursor) {
        if (this.isCursorInBody(cursor)) {
            final String uri = CTP.type.getName().getNamespaceURI();
            final String localPart = "p";
            cursor.beginElement(localPart, uri);
            cursor.toParent();
            final CTP p = (CTP)cursor.getObject();
            final XWPFParagraph newP = new XWPFParagraph(p, this);
            XmlObject o;
            for (o = null; !(o instanceof CTP) && cursor.toPrevSibling(); o = cursor.getObject()) {}
            if (!(o instanceof CTP) || o == p) {
                this.paragraphs.add(0, newP);
            }
            else {
                final int pos = this.paragraphs.indexOf(this.getParagraph((CTP)o)) + 1;
                this.paragraphs.add(pos, newP);
            }
            final XmlCursor newParaPos = p.newCursor();
            try {
                int i = 0;
                cursor.toCursor(newParaPos);
                while (cursor.toPrevSibling()) {
                    o = cursor.getObject();
                    if (o instanceof CTP || o instanceof CTTbl) {
                        ++i;
                    }
                }
                this.bodyElements.add(i, newP);
                cursor.toCursor(newParaPos);
                cursor.toEndToken();
                return newP;
            }
            finally {
                newParaPos.dispose();
            }
        }
        return null;
    }
    
    @Override
    public XWPFTable insertNewTbl(final XmlCursor cursor) {
        if (this.isCursorInBody(cursor)) {
            final String uri = CTTbl.type.getName().getNamespaceURI();
            final String localPart = "tbl";
            cursor.beginElement(localPart, uri);
            cursor.toParent();
            final CTTbl t = (CTTbl)cursor.getObject();
            final XWPFTable newT = new XWPFTable(t, this);
            XmlObject o;
            for (o = null; !(o instanceof CTTbl) && cursor.toPrevSibling(); o = cursor.getObject()) {}
            if (!(o instanceof CTTbl)) {
                this.tables.add(0, newT);
            }
            else {
                final int pos = this.tables.indexOf(this.getTable((CTTbl)o)) + 1;
                this.tables.add(pos, newT);
            }
            int i = 0;
            final XmlCursor tableCursor = t.newCursor();
            try {
                cursor.toCursor(tableCursor);
                while (cursor.toPrevSibling()) {
                    o = cursor.getObject();
                    if (o instanceof CTP || o instanceof CTTbl) {
                        ++i;
                    }
                }
                this.bodyElements.add(i, newT);
                cursor.toCursor(tableCursor);
                cursor.toEndToken();
                return newT;
            }
            finally {
                tableCursor.dispose();
            }
        }
        return null;
    }
    
    private boolean isCursorInBody(final XmlCursor cursor) {
        final XmlCursor verify = cursor.newCursor();
        verify.toParent();
        final boolean result = verify.getObject() == this.ctDocument.getBody();
        verify.dispose();
        return result;
    }
    
    private int getPosOfBodyElement(final IBodyElement needle) {
        final BodyElementType type = needle.getElementType();
        for (int i = 0; i < this.bodyElements.size(); ++i) {
            final IBodyElement current = this.bodyElements.get(i);
            if (current.getElementType() == type && current.equals(needle)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPosOfParagraph(final XWPFParagraph p) {
        return this.getPosOfBodyElement(p);
    }
    
    public int getPosOfTable(final XWPFTable t) {
        return this.getPosOfBodyElement(t);
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTDocument1.type.getName().getNamespaceURI(), "document"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctDocument.save(out, xmlOptions);
        out.close();
    }
    
    private int getRelationIndex(final XWPFRelation relation) {
        int i = 1;
        for (final RelationPart rp : this.getRelationParts()) {
            if (rp.getRelationship().getRelationshipType().equals(relation.getRelation())) {
                ++i;
            }
        }
        return i;
    }
    
    public XWPFParagraph createParagraph() {
        final XWPFParagraph p = new XWPFParagraph(this.ctDocument.getBody().addNewP(), this);
        this.bodyElements.add(p);
        this.paragraphs.add(p);
        return p;
    }
    
    public XWPFNumbering createNumbering() {
        if (this.numbering == null) {
            final NumberingDocument numberingDoc = NumberingDocument.Factory.newInstance();
            final XWPFRelation relation = XWPFRelation.NUMBERING;
            final int i = this.getRelationIndex(relation);
            final XWPFNumbering wrapper = (XWPFNumbering)this.createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setNumbering(numberingDoc.addNewNumbering());
            this.numbering = wrapper;
        }
        return this.numbering;
    }
    
    public XWPFStyles createStyles() {
        if (this.styles == null) {
            final StylesDocument stylesDoc = StylesDocument.Factory.newInstance();
            final XWPFRelation relation = XWPFRelation.STYLES;
            final int i = this.getRelationIndex(relation);
            final XWPFStyles wrapper = (XWPFStyles)this.createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setStyles(stylesDoc.addNewStyles());
            this.styles = wrapper;
        }
        return this.styles;
    }
    
    public XWPFFootnotes createFootnotes() {
        if (this.footnotes == null) {
            final FootnotesDocument footnotesDoc = FootnotesDocument.Factory.newInstance();
            final XWPFRelation relation = XWPFRelation.FOOTNOTE;
            final int i = this.getRelationIndex(relation);
            final XWPFFootnotes wrapper = (XWPFFootnotes)this.createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setFootnotes(footnotesDoc.addNewFootnotes());
            wrapper.setIdManager(this.footnoteIdManager);
            this.footnotes = wrapper;
        }
        return this.footnotes;
    }
    
    @Internal
    public XWPFFootnote addFootnote(final CTFtnEdn note) {
        return this.footnotes.addFootnote(note);
    }
    
    @Internal
    public XWPFEndnote addEndnote(final CTFtnEdn note) {
        final XWPFEndnote endnote = new XWPFEndnote(this, note);
        this.endnotes.addEndnote(note);
        return endnote;
    }
    
    public boolean removeBodyElement(final int pos) {
        if (pos >= 0 && pos < this.bodyElements.size()) {
            final BodyElementType type = this.bodyElements.get(pos).getElementType();
            if (type == BodyElementType.TABLE) {
                final int tablePos = this.getTablePos(pos);
                this.tables.remove(tablePos);
                this.ctDocument.getBody().removeTbl(tablePos);
            }
            if (type == BodyElementType.PARAGRAPH) {
                final int paraPos = this.getParagraphPos(pos);
                this.paragraphs.remove(paraPos);
                this.ctDocument.getBody().removeP(paraPos);
            }
            this.bodyElements.remove(pos);
            return true;
        }
        return false;
    }
    
    public void setParagraph(final XWPFParagraph paragraph, final int pos) {
        this.paragraphs.set(pos, paragraph);
        this.ctDocument.getBody().setPArray(pos, paragraph.getCTP());
    }
    
    public XWPFParagraph getLastParagraph() {
        final int lastPos = this.paragraphs.toArray().length - 1;
        return this.paragraphs.get(lastPos);
    }
    
    public XWPFTable createTable() {
        final XWPFTable table = new XWPFTable(this.ctDocument.getBody().addNewTbl(), this);
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }
    
    public XWPFTable createTable(final int rows, final int cols) {
        final XWPFTable table = new XWPFTable(this.ctDocument.getBody().addNewTbl(), this, rows, cols);
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }
    
    public void createTOC() {
        final CTSdtBlock block = this.getDocument().getBody().addNewSdt();
        final TOC toc = new TOC(block);
        for (final XWPFParagraph par : this.paragraphs) {
            final String parStyle = par.getStyle();
            if (parStyle != null && parStyle.startsWith("Heading")) {
                try {
                    final int level = Integer.parseInt(parStyle.substring("Heading".length()));
                    toc.addRow(level, par.getText(), 1, "112723803");
                }
                catch (final NumberFormatException e) {
                    XWPFDocument.LOG.log(7, new Object[] { "can't format number in TOC heading", e });
                }
            }
        }
    }
    
    public void setTable(final int pos, final XWPFTable table) {
        this.tables.set(pos, table);
        this.ctDocument.getBody().setTblArray(pos, table.getCTTbl());
    }
    
    public boolean isEnforcedProtection() {
        return this.settings.isEnforcedWith();
    }
    
    public boolean isEnforcedReadonlyProtection() {
        return this.settings.isEnforcedWith(STDocProtect.READ_ONLY);
    }
    
    public boolean isEnforcedFillingFormsProtection() {
        return this.settings.isEnforcedWith(STDocProtect.FORMS);
    }
    
    public boolean isEnforcedCommentsProtection() {
        return this.settings.isEnforcedWith(STDocProtect.COMMENTS);
    }
    
    public boolean isEnforcedTrackedChangesProtection() {
        return this.settings.isEnforcedWith(STDocProtect.TRACKED_CHANGES);
    }
    
    public boolean isEnforcedUpdateFields() {
        return this.settings.isUpdateFields();
    }
    
    public void enforceReadonlyProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.READ_ONLY);
    }
    
    public void enforceReadonlyProtection(final String password, final HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.READ_ONLY, password, hashAlgo);
    }
    
    public void enforceFillingFormsProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.FORMS);
    }
    
    public void enforceFillingFormsProtection(final String password, final HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.FORMS, password, hashAlgo);
    }
    
    public void enforceCommentsProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.COMMENTS);
    }
    
    public void enforceCommentsProtection(final String password, final HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.COMMENTS, password, hashAlgo);
    }
    
    public void enforceTrackedChangesProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.TRACKED_CHANGES);
    }
    
    public void enforceTrackedChangesProtection(final String password, final HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.TRACKED_CHANGES, password, hashAlgo);
    }
    
    public boolean validateProtectionPassword(final String password) {
        return this.settings.validateProtectionPassword(password);
    }
    
    public void removeProtectionEnforcement() {
        this.settings.removeEnforcement();
    }
    
    public void enforceUpdateFields() {
        this.settings.setUpdateFields();
    }
    
    public boolean isTrackRevisions() {
        return this.settings.isTrackRevisions();
    }
    
    public void setTrackRevisions(final boolean enable) {
        this.settings.setTrackRevisions(enable);
    }
    
    public long getZoomPercent() {
        return this.settings.getZoomPercent();
    }
    
    public void setZoomPercent(final long zoomPercent) {
        this.settings.setZoomPercent(zoomPercent);
    }
    
    public boolean getEvenAndOddHeadings() {
        return this.settings.getEvenAndOddHeadings();
    }
    
    public void setEvenAndOddHeadings(final boolean enable) {
        this.settings.setEvenAndOddHeadings(enable);
    }
    
    public boolean getMirrorMargins() {
        return this.settings.getMirrorMargins();
    }
    
    public void setMirrorMargins(final boolean enable) {
        this.settings.setMirrorMargins(enable);
    }
    
    @Override
    public void insertTable(final int pos, final XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (final CTTbl tbl : this.ctDocument.getBody().getTblArray()) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            ++i;
        }
        this.tables.add(i, table);
    }
    
    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList((List<? extends XWPFPictureData>)this.pictures);
    }
    
    public List<XWPFPictureData> getAllPackagePictures() {
        final List<XWPFPictureData> result = new ArrayList<XWPFPictureData>();
        final Collection<List<XWPFPictureData>> values = this.packagePictures.values();
        for (final List<XWPFPictureData> list : values) {
            result.addAll(list);
        }
        return Collections.unmodifiableList((List<? extends XWPFPictureData>)result);
    }
    
    void registerPackagePictureData(final XWPFPictureData picData) {
        final List<XWPFPictureData> list = this.packagePictures.computeIfAbsent(picData.getChecksum(), k -> new ArrayList(1));
        if (!list.contains(picData)) {
            list.add(picData);
        }
    }
    
    XWPFPictureData findPackagePictureData(final byte[] pictureData, final int format) {
        final long checksum = IOUtils.calculateChecksum(pictureData);
        XWPFPictureData xwpfPicData = null;
        final List<XWPFPictureData> xwpfPicDataList = this.packagePictures.get(checksum);
        if (xwpfPicDataList != null) {
            XWPFPictureData curElem;
            for (Iterator<XWPFPictureData> iter = xwpfPicDataList.iterator(); iter.hasNext() && xwpfPicData == null; xwpfPicData = curElem) {
                curElem = iter.next();
                if (Arrays.equals(pictureData, curElem.getData())) {}
            }
        }
        return xwpfPicData;
    }
    
    public String addPictureData(final byte[] pictureData, final int format) throws InvalidFormatException {
        XWPFPictureData xwpfPicData = this.findPackagePictureData(pictureData, format);
        final POIXMLRelation relDesc = XWPFPictureData.RELATIONS[format];
        if (xwpfPicData == null) {
            final int idx = this.getNextPicNameNumber(format);
            xwpfPicData = (XWPFPictureData)this.createRelationship(relDesc, XWPFFactory.getInstance(), idx);
            final PackagePart picDataPart = xwpfPicData.getPackagePart();
            try (final OutputStream out = picDataPart.getOutputStream()) {
                out.write(pictureData);
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
            this.registerPackagePictureData(xwpfPicData);
            this.pictures.add(xwpfPicData);
            return this.getRelationId(xwpfPicData);
        }
        if (!this.getRelations().contains(xwpfPicData)) {
            final RelationPart rp = this.addRelation(null, XWPFRelation.IMAGES, xwpfPicData);
            return rp.getRelationship().getId();
        }
        return this.getRelationId(xwpfPicData);
    }
    
    public String addPictureData(final InputStream is, final int format) throws InvalidFormatException {
        try {
            final byte[] data = IOUtils.toByteArray(is);
            return this.addPictureData(data, format);
        }
        catch (final IOException e) {
            throw new POIXMLException(e);
        }
    }
    
    public int getNextPicNameNumber(final int format) throws InvalidFormatException {
        int img = this.getAllPackagePictures().size() + 1;
        String proposal = XWPFPictureData.RELATIONS[format].getFileName(img);
        for (PackagePartName createPartName = PackagingURIHelper.createPartName(proposal); this.getPackage().getPart(createPartName) != null; createPartName = PackagingURIHelper.createPartName(proposal)) {
            ++img;
            proposal = XWPFPictureData.RELATIONS[format].getFileName(img);
        }
        return img;
    }
    
    public XWPFPictureData getPictureDataByID(final String blipID) {
        final POIXMLDocumentPart relatedPart = this.getRelationById(blipID);
        if (relatedPart instanceof XWPFPictureData) {
            return (XWPFPictureData)relatedPart;
        }
        return null;
    }
    
    public XWPFNumbering getNumbering() {
        return this.numbering;
    }
    
    public XWPFStyles getStyles() {
        return this.styles;
    }
    
    @Override
    public XWPFParagraph getParagraph(final CTP p) {
        for (int i = 0; i < this.getParagraphs().size(); ++i) {
            if (this.getParagraphs().get(i).getCTP() == p) {
                return this.getParagraphs().get(i);
            }
        }
        return null;
    }
    
    @Override
    public XWPFTable getTable(final CTTbl ctTbl) {
        for (int i = 0; i < this.tables.size(); ++i) {
            if (this.getTables().get(i).getCTTbl() == ctTbl) {
                return this.getTables().get(i);
            }
        }
        return null;
    }
    
    public Iterator<XWPFTable> getTablesIterator() {
        return this.tables.iterator();
    }
    
    public Iterator<XWPFParagraph> getParagraphsIterator() {
        return this.paragraphs.iterator();
    }
    
    @Override
    public XWPFParagraph getParagraphArray(final int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        return this;
    }
    
    @Override
    public BodyType getPartType() {
        return BodyType.DOCUMENT;
    }
    
    @Override
    public XWPFTableCell getTableCell(final CTTc cell) {
        final XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            return null;
        }
        final CTRow row = (CTRow)o;
        cursor.toParent();
        o = cursor.getObject();
        cursor.dispose();
        if (!(o instanceof CTTbl)) {
            return null;
        }
        final CTTbl tbl = (CTTbl)o;
        final XWPFTable table = this.getTable(tbl);
        if (table == null) {
            return null;
        }
        final XWPFTableRow tableRow = table.getRow(row);
        if (tableRow == null) {
            return null;
        }
        return tableRow.getTableCell(cell);
    }
    
    @Override
    public XWPFDocument getXWPFDocument() {
        return this;
    }
    
    public XWPFChart createChart() throws InvalidFormatException, IOException {
        return this.createChart(500000, 500000);
    }
    
    public XWPFChart createChart(final int width, final int height) throws InvalidFormatException, IOException {
        return this.createChart(this.createParagraph().createRun(), width, height);
    }
    
    public XWPFChart createChart(final XWPFRun run, final int width, final int height) throws InvalidFormatException, IOException {
        final int chartNumber = this.getNextPartNumber(XWPFRelation.CHART, this.charts.size() + 1);
        final RelationPart rp = this.createRelationship(XWPFRelation.CHART, XWPFFactory.getInstance(), chartNumber, false);
        final XWPFChart xwpfChart = rp.getDocumentPart();
        xwpfChart.setChartIndex(chartNumber);
        xwpfChart.attach(rp.getRelationship().getId(), run);
        xwpfChart.setChartBoundingBox(width, height);
        this.charts.add(xwpfChart);
        return xwpfChart;
    }
    
    public XWPFFootnote createFootnote() {
        final XWPFFootnotes footnotes = this.createFootnotes();
        final XWPFFootnote footnote = footnotes.createFootnote();
        return footnote;
    }
    
    public boolean removeFootnote(final int pos) {
        return null != this.footnotes && this.footnotes.removeFootnote(pos);
    }
    
    public XWPFEndnote createEndnote() {
        final XWPFEndnotes endnotes = this.createEndnotes();
        final XWPFEndnote endnote = endnotes.createEndnote();
        return endnote;
    }
    
    public XWPFEndnotes createEndnotes() {
        if (this.endnotes == null) {
            final EndnotesDocument endnotesDoc = EndnotesDocument.Factory.newInstance();
            final XWPFRelation relation = XWPFRelation.ENDNOTE;
            final int i = this.getRelationIndex(relation);
            final XWPFEndnotes wrapper = (XWPFEndnotes)this.createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setEndnotes(endnotesDoc.addNewEndnotes());
            wrapper.setIdManager(this.footnoteIdManager);
            this.endnotes = wrapper;
        }
        return this.endnotes;
    }
    
    public List<XWPFEndnote> getEndnotes() {
        if (this.endnotes == null) {
            return Collections.emptyList();
        }
        return this.endnotes.getEndnotesList();
    }
    
    public boolean removeEndnote(final int pos) {
        return null != this.endnotes && this.endnotes.removeEndnote(pos);
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XWPFDocument.class);
    }
}
